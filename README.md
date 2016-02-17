# Hazelcast Discovery Plugin for Apache ZooKeeper


##Configuration

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
###Client XML Config

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
### Server Programatic Config

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
  config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(dicoveryStrategyConfig);

  Hazelcast.newHazelcastInstance(config);
}
```

### Client Programatic Config

```java
    ClientConfig config = new ClientConfig();
    config.getNetworkConfig().getAwsConfig().setEnabled(false);
    config.setProperty(GroupProperty.DISCOVERY_SPI_ENABLED, "true");

    DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory());
    discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), "{ip-address-of-zookeeper}:{port-of-zookeeper}");
    discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_PATH.key(), "{path-on-zookeeper}");
    discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.GROUP.key(), "{clusterId}");
    config.getNetworkConfig().getDiscoveryConfig().addDiscoveryStrategyConfig(dicoveryStrategyConfig);

    HazelcastClient.newHazelcastClient(config);

```
###Configuration via Maven
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
            <version>1.0</version>
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
    
    <repositories>
        <repository>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
            <id>central</id>
            <name>bintray</name>
            <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>
...
</project>
```

##Important Notice
In order to work hazelcast-zookeeper plugin for client you should have hazelcast client version greater than 3.6.




