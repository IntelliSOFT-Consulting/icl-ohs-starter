rootProject.name = "ohs-monorepo-template" // TODO: rename per project

include(
    ":apps:mobile-kotlin",
    ":apps:core-platform:service-fhir-java",
    ":packages:shared-libraries:generated-kotlin"
)

project(":apps:mobile-kotlin").projectDir = file("apps/mobile-kotlin")
project(":apps:core-platform:service-fhir-java").projectDir = file("apps/core-platform/service-fhir-java")
project(":packages:shared-libraries:generated-kotlin").projectDir = file("packages/shared-libraries/generated-kotlin")
