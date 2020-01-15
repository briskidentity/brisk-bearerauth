pluginManagement {
    plugins {
        id("com.github.ben-manes.versions") version "0.27.0"
        id("org.springframework.boot") version "2.2.2.RELEASE"
    }
}

rootProject.name = "bearerauth"

include("bearerauth-core")
include("bearerauth-servlet")
include("bearerauth-spring-webflux")

file("$rootDir/bearerauth-tests").listFiles { f -> f.isDirectory and f.name.startsWith("bearerauth-test-") }?.forEach {
    include("bearerauth-tests:${it.name}")
}
