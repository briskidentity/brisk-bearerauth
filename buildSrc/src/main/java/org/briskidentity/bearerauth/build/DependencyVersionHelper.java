package org.briskidentity.bearerauth.build;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DependencyVersionHelper {

    public static boolean isStable(String version) {
        boolean isStableVersion = Pattern.compile("^[0-9,.v-]+(-r)?$").matcher(version).matches();
        boolean containsStableKeyword = Stream.of("RELEASE", "FINAL", "GA")
                .anyMatch(s -> version.toUpperCase().contains(s));
        return isStableVersion || containsStableKeyword;
    }

}
