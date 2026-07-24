#!/usr/bin/env node
/**
 * Universal compilation engine: JSON Schema -> TypeScript + Kotlin.
 *
 * Reads every *.json FHIR/data schema in ./schemas, and writes:
 *   - generated-ts/<Name>.ts   (TypeScript interfaces)
 *   - generated-kotlin/.../<Name>.kt (Kotlin data classes)
 *
 * This is a minimal starter implementation covering flat schemas with
 * primitive property types. Swap in `json-schema-to-typescript` and a
 * Kotlin equivalent (or a codegen tool like `quicktype`) for production use
 * with nested/composed FHIR profiles.
 */
const fs = require("fs");
const path = require("path");

const SCHEMAS_DIR = path.join(__dirname, "schemas");
const TS_OUT_DIR = path.join(__dirname, "generated-ts", "src");
const KOTLIN_OUT_DIR = path.join(__dirname, "generated-kotlin", "src", "main", "kotlin", "generated");

const JSON_TO_TS = { string: "string", integer: "number", number: "number", boolean: "boolean" };
const JSON_TO_KOTLIN = { string: "String", integer: "Int", number: "Double", boolean: "Boolean" };

function toPascalCase(name) {
  return name.replace(/(^\w|[-_]\w)/g, (m) => m.replace(/[-_]/, "").toUpperCase());
}

function generateTs(schema, typeName) {
  const props = schema.properties || {};
  const required = new Set(schema.required || []);
  const fields = Object.entries(props)
    .map(([key, def]) => {
      const optional = required.has(key) ? "" : "?";
      const type = JSON_TO_TS[def.type] || "unknown";
      return `  ${key}${optional}: ${type};`;
    })
    .join("\n");
  return `// AUTO-GENERATED from schemas/${typeName}.json — do not edit by hand.\nexport interface ${toPascalCase(typeName)} {\n${fields}\n}\n`;
}

function generateKotlin(schema, typeName) {
  const props = schema.properties || {};
  const required = new Set(schema.required || []);
  const fields = Object.entries(props)
    .map(([key, def]) => {
      const nullable = required.has(key) ? "" : "?";
      const type = JSON_TO_KOTLIN[def.type] || "Any";
      return `    val ${key}: ${type}${nullable}${nullable ? " = null" : ""}`;
    })
    .join(",\n");
  return `// AUTO-GENERATED from schemas/${typeName}.json — do not edit by hand.\npackage generated\n\ndata class ${toPascalCase(typeName)}(\n${fields}\n)\n`;
}

function main() {
  if (!fs.existsSync(SCHEMAS_DIR)) {
    console.error(`Schemas directory not found: ${SCHEMAS_DIR}`);
    process.exit(1);
  }

  const schemaFiles = fs.readdirSync(SCHEMAS_DIR).filter((f) => f.endsWith(".json"));

  if (schemaFiles.length === 0) {
    console.log("No schemas found in ./schemas — nothing to generate yet.");
    return;
  }

  fs.mkdirSync(TS_OUT_DIR, { recursive: true });
  fs.mkdirSync(KOTLIN_OUT_DIR, { recursive: true });

  for (const file of schemaFiles) {
    const typeName = path.basename(file, ".json");
    const schema = JSON.parse(fs.readFileSync(path.join(SCHEMAS_DIR, file), "utf-8"));

    const tsOutPath = path.join(TS_OUT_DIR, `${toPascalCase(typeName)}.ts`);
    fs.writeFileSync(tsOutPath, generateTs(schema, typeName));
    console.log(`generated ${tsOutPath}`);

    const ktOutPath = path.join(KOTLIN_OUT_DIR, `${toPascalCase(typeName)}.kt`);
    fs.writeFileSync(ktOutPath, generateKotlin(schema, typeName));
    console.log(`generated ${ktOutPath}`);
  }
}

main();
