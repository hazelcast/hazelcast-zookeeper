  <a href="https://github.com/hazelcast/hazelcast-zookeeper/actions?query=workflow%3Abuild"><img alt="GitHub Actions status" src="https://github.com/hazelcast/hazelcast-zookeeper/workflows/build/badge.svg"></a>
  <a href="https://hazelcast.github.io/hazelcast-zookeeper/pitest/"><img alt="GitHub Actions status" src="https://github.com/hazelcast/hazelcast-zookeeper/workflows/pitest/badge.svg"></a>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hazelcast/hazelcast-zookeeper/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.hazelcast/hazelcast-zookeeper) 

# Table of Contents

* [Hazelcast Discovery Plugin for Apache ZooKeeper](#hazelcast-discovery-plugin-for-apache-zooKeeper)
  * [Configuration](#configuration)
    * [Server XML Config](#server-xml-config)
    * [Client XML Config](#client-xml-config)
    * [Server Programmatic Config](#server-programmatic-config)
    * [Client Programmatic Config](#client-programmatic-config)
    * [Configuration via Maven](#configuration-via-maven)
  * [Compatibilities](#compatibilities)


# Hazelcast Apache ZooKeeper Discovery Plugin 

This plugin provides a service-based discovery by using Apache Curator to communicate with your Zookeeper server. 

You can use this plugin with Discovery SPI enabled Hazelcast 3.6.1 and higher applications.

## Configuration

### Server XML Config  

```xml
<hazelcast xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.hazelcast.com/schema/config
                               http://www.hazelcast.com/schema/config/hazelcast-config-4.0.xsd"
           xmlns="http://www.hazelcast.com/schema/config">

    <properties>
        <property name="hazelcast.discovery.enabled">true</property>
    </properties>

    <network>
        <join>
            <multicast enabled="false"/>
            <discovery-strategies>
                <discovery-strategy enabled="true" class="com.hazelcast.zookeeper.ZookeeperDiscoveryStrategy">
                    <properties>
                        <property name="zookeeper_url">{ip-address-of-zookeeper}:{port-of-zookeeper}</property> 
                        <!--defaults to /discovery/hazelcast -->
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
<hazelcast-client xsi:schemaLocation="http://www.hazelcast.com/schema/client-config hazelcast-client-config-4.0.xsd"
                  xmlns="http://www.hazelcast.com/schema/client-config"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <properties>
        <property name="hazelcast.discovery.enabled">true</property>
    </properties>

    <network>
        <aws enabled="false"/>
        <discovery-strategies>
            <discovery-strategy enabled="true" class="com.hazelcast.zookeeper.ZookeeperDiscoveryStrategy">
                <properties>
                    <property name="zookeeper_url">{ip-address-of-zookeeper}:{port-of-zookeeper}</property>
                    <!--Default: /discovery/hazelcast -->
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
Config config = new Config();
config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
config.setProperty("hazelcast.discovery.enabled", "true");

DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory())
  .addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), "{ip-address-of-zookeeper}:{port-of-zookeeper}")
  .addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_PATH.key(), "{path-on-zookeeper}")
  .addProperty(ZookeeperDiscoveryProperties.GROUP.key(), "{clusterId}");
config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

Hazelcast.newHazelcastInstance(config);
```

### Client Programmatic Config

```java
ClientConfig config = new ClientConfig();

config.setProperty("hazelcast.discovery.enabled", "true");

DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory())
  .addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), "{ip-address-of-zookeeper}:{port-of-zookeeper}")
  .addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_PATH.key(), "{path-on-zookeeper}")
  .addProperty(ZookeeperDiscoveryProperties.GROUP.key(), "{clusterId}");
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
            <artifactId>curator-x-discovery</artifactId>
            <version>${curator.version}</version>
        </dependency>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast-zookeeper</artifactId>
            <version>${hazelcast-zookeeper.version}</version>
        </dependency>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast</artifactId>
            <version>${hazelcast.version}</version>
        </dependency>
    </dependencies>
...
</project>
```

## Compatibilities

- `3.6.3` has been tested with Curator 2.9.0 and Zookeeper 3.4.6
- `4.0.1` has been tested with Curator 4.0.1 and Zookeeper 3.5.7

### Known Issues
There is an issue between Zookeeper and curator client in some versions. You may get `Received packet at server of unknown type 15` error.
