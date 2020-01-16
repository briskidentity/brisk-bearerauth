package org.briskidentity.bearerauth.build;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.testing.Test;

public class ConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        applyJavaConventions(project);
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

}
