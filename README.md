# hzkeeper
Hazelcast cluster discovery strategy based on Apache Zookeeper

It allows to use Apache Zookeeper for Hazelcast cluster discovery.

```java
@Test
public void testIntegration() {
  //my Zookeeper URL
  String zookeeperURL = "localhost:2181";

  Config config = new Config();
  config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
  config.setProperty(GroupProperty.DISCOVERY_SPI_ENABLED, "true");

  DiscoveryStrategyConfig dicoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory());
  dicoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), zookeeperURL);
  config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryProviderConfig(dicoveryStrategyConfig);

  HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
  HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);

  int instance1Size = instance1.getCluster().getMembers().size();
  assertEquals(2, instance1Size);
  int instance2Size = instance2.getCluster().getMembers().size();
  assertEquals(2, instance2Size);
}
```
