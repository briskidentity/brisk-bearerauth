plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("conventionsPlugin") {
            id = "org.briskidentity.bearerauth.conventions"
            implementationClass = "org.briskidentity.bearerauth.build.ConventionsPlugin"
        }
    }
}
