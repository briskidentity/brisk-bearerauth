import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(enforcedPlatform(SpringBootPlugin.BOM_COORDINATES))

    implementation(project(":bearerauth-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
