/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.sickert.images.dedup;

import org.junit.jupiter.api.Test;
import org.sickert.images.dedup.Cli;

import static org.junit.jupiter.api.Assertions.*;

class CliTest {
    @Test void appHasAGreeting() {
        Cli classUnderTest = new Cli();
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
    }
}