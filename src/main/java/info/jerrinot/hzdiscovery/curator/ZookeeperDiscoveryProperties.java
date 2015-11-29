package info.jerrinot.hzdiscovery.curator;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;

import static com.hazelcast.config.properties.PropertyTypeConverter.STRING;

public class ZookeeperDiscoveryProperties {
    public static final PropertyDefinition GROUP = property("group", STRING);

    public static final PropertyDefinition ZOOKEEPER_URL = property("zookeeper_url", STRING);
    public static final PropertyDefinition ZOOKEEPER_PATH = property("zookeeper_path", STRING);

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter) {
        return property(key, typeConverter, null);
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter,
                                               ValueValidator valueValidator) {
        return new SimplePropertyDefinition(key, true, typeConverter, valueValidator);
    }
}
