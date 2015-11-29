package info.jerrinot.hzdiscovery.curator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);

    public static final void closeSafely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.debug("Error while closing " + closeable, e);
            }
        }
    }
}
