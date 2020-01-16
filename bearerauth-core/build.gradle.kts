plugins {
    `java-library`
    id("org.briskidentity.bearerauth.conventions")
}

dependencies {
    implementation(platform("org.junit:junit-bom:5.5.2"))

    testImplementation("org.assertj:assertj-core:3.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
