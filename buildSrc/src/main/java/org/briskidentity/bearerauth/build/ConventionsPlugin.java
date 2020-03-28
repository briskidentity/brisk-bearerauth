package org.briskidentity.bearerauth.build;

import com.github.benmanes.gradle.versions.VersionsPlugin;
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(@Nonnull Project project) {
        applyJavaConventions(project);
        applyMavenPublishConventions(project);
    }

    private void applyJavaConventions(Project project) {
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
            javaPluginExtension.setSourceCompatibility(JavaVersion.VERSION_1_8);
            project.apply(action -> action.from(new File(project.getRootDir(), "gradle/dependency-versions.gradle")));
            project.getPluginManager().apply(VersionsPlugin.class);
            TaskContainer tasks = project.getTasks();
            tasks.withType(DependencyUpdatesTask.class, dependencyUpdatesTask -> {
                dependencyUpdatesTask.setGradleReleaseChannel("current");
                dependencyUpdatesTask.rejectVersionIf(candidate -> isNonStable(candidate.getCandidate().getVersion())
                        && !isNonStable(candidate.getCurrentVersion()));
            });
            tasks.withType(Test.class, test -> {
                test.useJUnitPlatform();
                test.setMaxHeapSize("1g");
            });
        });
    }

    private static boolean isNonStable(String version) {
        boolean containsStableKeyword = Stream.of("RELEASE", "FINAL", "GA")
                .anyMatch(s -> version.toUpperCase().contains(s));
        boolean isStableVersion = Pattern.compile("^[0-9,.v-]+(-r)?$").matcher(version).matches();
        return !containsStableKeyword && !isStableVersion;
    }

    @SuppressWarnings("UnstableApiUsage")
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
