package com.hazelcast.zookeeper;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class IOUtilsTest {
    @Test
    public void testCloseSafely() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        IOUtils.closeSafely(new Closeable() {
            @Override
            public void close() throws IOException {
                called.set(true);
            }
        });

        assertTrue(called.get());
    }

    @Test
    public void testCloseSafely_nullSafety() throws Exception {
        IOUtils.closeSafely(null);
    }

    @Test
    public void testCloseSafely_swallowsIOException() throws Exception {
        IOUtils.closeSafely(new Closeable() {
            @Override
            public void close() throws IOException {
                throw new IOException("Test exception");
            }
        });
    }
}
