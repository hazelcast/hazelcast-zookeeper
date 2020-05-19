package com.hazelcast.zookeeper;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class IOUtilsTest {
    @Test
    public void testCloseSafely() {
        final AtomicBoolean called = new AtomicBoolean(false);

        IOUtils.closeSafely(() -> called.set(true));

        assertTrue(called.get());
    }

    @Test
    public void testCloseSafely_nullSafety() {
        IOUtils.closeSafely(null);
    }

    @Test
    public void testCloseSafely_swallowsIOException() {
        IOUtils.closeSafely(() -> {
            throw new IOException("Test exception");
        });
    }
}
