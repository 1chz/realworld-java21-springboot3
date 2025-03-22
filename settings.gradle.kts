rootProject.name = "realworld"

// Include all modules
include(":module:core", ":module:persistence")

// Include the server module
include(":realworld")
project(":realworld").projectDir = file("server/api")
