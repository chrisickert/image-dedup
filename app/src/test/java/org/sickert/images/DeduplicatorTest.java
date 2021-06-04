package org.sickert.images;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeduplicatorTest {
    @Test void appHasCall() throws Exception {
        Deduplicator classUnderTest = new Deduplicator();
        assertNotNull(classUnderTest.getClass().getMethod("call"), "app should have a 'call' method");
    }
}
