plugins {
    base
    id("com.github.ben-manes.versions")
}

group = "io.github.vpavic.bearerauth"

subprojects {
    apply<JavaPlugin>()

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
