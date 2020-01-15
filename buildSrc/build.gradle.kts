plugins {
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("conventionsPlugin") {
            id = "io.github.vpavic.bearerauth.conventions"
            implementationClass = "io.github.vpavic.bearerauth.build.ConventionsPlugin"
        }
    }
}
