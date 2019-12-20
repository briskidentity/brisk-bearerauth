pluginManagement {
    plugins {
        id("com.github.ben-manes.versions") version "0.27.0"
        id("org.springframework.boot") version "2.2.2.RELEASE"
    }

    resolutionStrategy {
    }

    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "bearerauth"

include("bearerauth-core")
include("bearerauth-servlet")
include("bearerauth-spring-webflux")
include("samples:sample-micronaut")
include("samples:sample-spring-servlet")
include("samples:sample-spring-webflux")
