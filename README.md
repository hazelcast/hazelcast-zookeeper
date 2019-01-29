# Table of Contents temp

* [Hazelcast Discovery Plugin for Apache ZooKeeper](#hazelcast-discovery-plugin-for-apache-zooKeeper)
  * [Configuration](#configuration)
    * [Server XML Config](#server-xml-config)
    * [Client XML Config](#client-xml-config)
    * [Server Programmatic Config](#server-programmatic-config)
    * [Client Programmatic Config](#client-programmatic-config)
    * [Configuration via Maven](#configuration-via-maven)
  * [Compatibilities](#compatibilities)


# Hazelcast Discovery Plugin for Apache ZooKeeper

This plugin provides a service based discovery strategy by using Apache Curator to communicate with your Zookeeper server. You can use this plugin with Discovery SPI enabled Hazelcast 3.6.1 and higher  applications.

## Configuration

### Server XML Config  

```xml
<hazelcast xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.hazelcast.com/schema/config
                               http://www.hazelcast.com/schema/config/hazelcast-config-3.6.xsd"
           xmlns="http://www.hazelcast.com/schema/config">

    <properties>
        <property name="hazelcast.discovery.enabled">true</property>
    </properties>

    <network>
        <join>
            <multicast enabled="false"/>
            <tcp-ip enabled="false" />
            <aws enabled="false"/>
            <discovery-strategies>
                <discovery-strategy enabled="true" class="com.hazelcast.zookeeper.ZookeeperDiscoveryStrategy">
                    <properties>
                        <!--
                          Connection string to your ZooKeeper server.
                          Default: There is no default, this is a required property.
                          Example: 127.0.0.1:2181
                        -->
                        <property name="zookeeper_url">{ip-address-of-zookeeper}:{port-of-zookeeper}</property>
                        <!--Path in ZooKeeper Hazelcast will useDefault: /discovery/hazelcast -->
                        <property name="zookeeper_path">{path-on-zookeeper}</property>
                        <!--Name of this Hazelcast cluster. You can have multiple distinct clusters to use the same ZooKeeper installation.-->
                        <property name="group">{clusterId}</property>
                    </properties>
                </discovery-strategy>
            </discovery-strategies>
        </join>
    </network>
</hazelcast>
```

### Client XML Config

```xml
<hazelcast-client xsi:schemaLocation="http://www.hazelcast.com/schema/client-config hazelcast-client-config-3.6.xsd"
                  xmlns="http://www.hazelcast.com/schema/client-config"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <properties>
        <property name="hazelcast.discovery.enabled">true</property>
    </properties>

    <network>
        <aws enabled="false"/>
        <smart-routing>true</smart-routing>
        <redo-operation>true</redo-operation>
        <discovery-strategies>
            <discovery-strategy enabled="true" class="com.hazelcast.zookeeper.ZookeeperDiscoveryStrategy">
                <properties>
                    <!--
                          Connection string to your ZooKeeper server.
                          Default: There is no default, this is a required property.
                          Example: 127.0.0.1:2181
                    -->
                    <property name="zookeeper_url">{ip-address-of-zookeeper}:{port-of-zookeeper}</property>
                    <!--Path in ZooKeeper Hazelcast will useDefault: /discovery/hazelcast -->
                    <property name="zookeeper_path">{path-on-zookeeper}</property>
                    <!--Name of this Hazelcast cluster. You can have multiple distinct clusters to use the same ZooKeeper installation.-->
                    <property name="group">{clusterId}</property>
                </properties>
            </discovery-strategy>
        </discovery-strategies>
    </network>

</hazelcast-client>
```
### Server Programmatic Config

```java

public static void main(String[] args) {

  String zookeeperURL = "{ip-address-of-zookeeper}:{port-of-zookeeper}";

  Config config = new Config();
  config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
  config.setProperty(GroupProperty.DISCOVERY_SPI_ENABLED, "true");

  DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory());
  discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), "{ip-address-of-zookeeper}:{port-of-zookeeper}");
  discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_PATH.key(), "{path-on-zookeeper}");
  discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.GROUP.key(), "{clusterId}");
  config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

  Hazelcast.newHazelcastInstance(config);
}
```

### Client Programmatic Config

```java
    ClientConfig config = new ClientConfig();
    config.getNetworkConfig().getAwsConfig().setEnabled(false);
    config.setProperty(GroupProperty.DISCOVERY_SPI_ENABLED, "true");

    DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory());
    discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), "{ip-address-of-zookeeper}:{port-of-zookeeper}");
    discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_PATH.key(), "{path-on-zookeeper}");
    discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.GROUP.key(), "{clusterId}");
    config.getNetworkConfig().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

    HazelcastClient.newHazelcastClient(config);

```
### Configuration via Maven

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
      
    ...  
    <dependencies>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-test</artifactId>
            <version>${curator.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-x-discovery</artifactId>
            <version>${curator.version}</version>
        </dependency>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast-zookeeper</artifactId>
            <version>${hazelcast.version}</version>
        </dependency>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast</artifactId>
            <version>${hazelcast.version}</version>
        </dependency>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast-client</artifactId>
            <version>${hazelcast.version}</version>
      <dependency>
    </dependencies>
...
</project>
```

## Compatibilities

In order for the hazelcast-zookeeper plugin to work for the Hazelcast Java client, you need to use Hazelcast Client 3.6.1 or higher.

hazelcast-zookeeper-3.6.3 has been tested with Curator 2.9.0 and Zookeeper 3.4.6

Current Snapshot version of hazelcast-zookeeper has been tested with Curator 4.0.1 and Zookeeper 3.5.3-beta

### Known Issues
There is an issue between zookeeper and curator client in some versions. You may get `Received packet at server of unknown type 15` error.
