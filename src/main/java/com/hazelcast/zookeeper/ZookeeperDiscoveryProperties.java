/*
* Copyright 2020 Hazelcast Inc.
*
* Licensed under the Hazelcast Community License (the "License"); you may not use
* this file except in compliance with the License. You may obtain a copy of the
* License at
*
* http://hazelcast.com/hazelcast-community-license
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OF ANY KIND, either express or implied. See the License for the
* specific language governing permissions and limitations under the License.
*/

package com.hazelcast.zookeeper;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.config.properties.ValueValidator;

import static com.hazelcast.config.properties.PropertyTypeConverter.STRING;

/**
 * The type Zookeeper discovery properties.
 */
public final class ZookeeperDiscoveryProperties {

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

    private ZookeeperDiscoveryProperties() {
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter) {
        return property(key, typeConverter, null);
    }

    private static PropertyDefinition property(String key, PropertyTypeConverter typeConverter,
                                               ValueValidator valueValidator) {
        return new SimplePropertyDefinition(key, true, typeConverter, valueValidator);
    }
}
