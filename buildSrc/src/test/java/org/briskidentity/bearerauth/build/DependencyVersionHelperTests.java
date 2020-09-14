package org.briskidentity.bearerauth.build;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyVersionHelperTests {

    @Test
    void isStable() {
        assertTrue(DependencyVersionHelper.isStable("1.0.0"));
    }

}
