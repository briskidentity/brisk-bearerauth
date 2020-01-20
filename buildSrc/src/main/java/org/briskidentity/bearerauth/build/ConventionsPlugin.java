package org.briskidentity.bearerauth.build;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.testing.Test;

public class ConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        applyJavaConventions(project);
        applyMavenPublishConventions(project);
    }

    private void applyJavaConventions(Project project) {
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
            javaPluginExtension.setSourceCompatibility(JavaVersion.VERSION_1_8);
            project.getTasks().withType(Test.class, test -> {
                test.useJUnitPlatform();
                test.setMaxHeapSize("1g");
            });
        });
    }

    private void applyMavenPublishConventions(Project project) {
        project.getPlugins().withType(MavenPublishPlugin.class, mavenPublishPlugin -> {
            PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);
            MavenPublication mavenPublication = publishingExtension.getPublications().create("maven",
                    MavenPublication.class);
            MavenPom pom = mavenPublication.getPom();
            // TODO customize POM
            project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
                project.getComponents().matching((component) -> component.getName().equals("java"))
                        .all(mavenPublication::from);
                JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
                javaPluginExtension.withJavadocJar();
                javaPluginExtension.withSourcesJar();
            });
        });

    }

}
