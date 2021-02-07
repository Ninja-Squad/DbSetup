rootProject.name = "DbSetupParent"

include("DbSetup-core", "DbSetup-kotlin")

project(":DbSetup-core").name = "DbSetup"
