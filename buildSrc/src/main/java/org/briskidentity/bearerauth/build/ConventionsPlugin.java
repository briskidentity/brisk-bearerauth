package org.briskidentity.bearerauth.build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;

@Deprecated
public class ConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(@Nonnull Project project) {
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> project.apply(
                action -> action.from(new File(project.getRootDir(), "gradle/dependency-versions.gradle"))));
    }

}
