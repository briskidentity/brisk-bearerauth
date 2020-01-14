plugins {
    base
    id("com.github.ben-manes.versions")
}

allprojects {
    group = "io.github.vpavic.bearerauth"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply<JavaPlugin>()

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
