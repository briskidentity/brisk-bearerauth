plugins {
    `java-library`
}

dependencies {
    implementation(platform("org.junit:junit-bom:5.5.2"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
