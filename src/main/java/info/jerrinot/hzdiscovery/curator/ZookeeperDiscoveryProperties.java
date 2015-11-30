package info.jerrinot.hzdiscovery.curator;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;

import static com.hazelcast.config.properties.PropertyTypeConverter.STRING;

public class ZookeeperDiscoveryProperties {

    /**
     * Connection string to your ZooKeeper server.
     * Default: There is no default, this is a required property.
     * Example: 127.0.0.1:2181
     *
     */
    public static final PropertyDefinition ZOOKEEPER_URL = property("zookeeper_url", STRING);

    /**
     * Path in ZooKeeper Hazelcast will use
     * Default: /discovery/hazelcast
     *
     */
    public static final PropertyDefinition ZOOKEEPER_PATH = property("zookeeper_path", STRING);

    /**
     * Name of this Hazelcast cluster. You can have multiple distinct clusters to use the
     * same ZooKeeper installation.
     *
     */
    public static final PropertyDefinition GROUP = property("group", STRING);

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter) {
        return property(key, typeConverter, null);
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter,
                                               ValueValidator valueValidator) {
        return new SimplePropertyDefinition(key, true, typeConverter, valueValidator);
    }
}
