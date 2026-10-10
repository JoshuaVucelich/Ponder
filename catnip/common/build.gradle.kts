plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.configure.platform)
}

dependencies {
    minecraft(libs.minecraft)
    compileOnly(libs.bundles.mixin)
    // PartialModel + TransformStack used by CachedBuffers / SuperByteBuffer (NeoForge API; Fabric gated off)
    compileOnly(libs.flywheel.neoforge.api)
}

loom {
    accessWidenerPath = file("catnip_common_source.accesswidener")
}
