plugins {
    `java-library`
    id("io.github.vpavic.bearerauth.conventions")
}

dependencies {
    implementation(platform("org.junit:junit-bom:5.5.2"))

    api(project(":bearerauth-core"))
    api("jakarta.servlet:jakarta.servlet-api:4.0.3")

    testImplementation("org.assertj:assertj-core:3.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
