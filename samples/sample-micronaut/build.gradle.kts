plugins {
    application
}

dependencies {
    arrayOf("annotationProcessor", "implementation", "testAnnotationProcessor").forEach {
        add(it, platform("io.micronaut:micronaut-bom:1.2.9"))
    }
    annotationProcessor("io.micronaut:micronaut-inject-java")
    implementation(project(":bearerauth-core"))
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-inject")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClassName = "sample.SampleMicronautApplication"
}
