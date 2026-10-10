pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "ponder"

val platforms = mutableListOf("common", "neoforge")
// Fabric subprojects fail configuration without flywheel-fabric-26.1; enable with -PincludeFabric.
if (providers.gradleProperty("includeFabric").isPresent) {
    platforms += "fabric"
}

for (platform in platforms) {
    include(platform)

    include(":catnip:$platform")
    include(":testmod:$platform")
}

includeBuild("build-logic")
