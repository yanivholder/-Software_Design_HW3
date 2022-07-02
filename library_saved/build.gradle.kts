val externalLibraryVersion: String? by extra
val guiceVersion: String? by extra
val kotlinGuiceVersion: String? by extra

dependencies {
    implementation("il.ac.technion.cs.softwaredesign", "primitive-storage-layer", externalLibraryVersion)
    implementation("com.github.Dor-B:sd-loan-service:1.0.3")
    implementation ("com.google.code.gson:gson:2.8.6")

    implementation("com.google.inject", "guice", guiceVersion)
    implementation("dev.misfitlabs.kotlinguice4", "kotlin-guice", kotlinGuiceVersion)
}