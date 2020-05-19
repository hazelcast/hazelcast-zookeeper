package com.hazelcast.zookeeper;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(HazelcastSerialClassRunner.class)
public class ClientSmokeTest extends HazelcastTestSupport {

    //use an unusual port so clients won't guess it without ZooKeeper
    private static final int HAZELCAST_BASE_PORT = 9999;
    private static final int CLUSTER_SIZE = 2;

    private TestingServer zkTestServer;
    private final HazelcastInstance[] instances = new HazelcastInstance[CLUSTER_SIZE];

    @Before
    public void setUp() throws Exception {
        zkTestServer = new TestingServer();
    }

    @After
    public void tearDown() throws IOException {
        try {
            HazelcastClient.shutdownAll();
            Hazelcast.shutdownAll();
        } finally {
            zkTestServer.close();
        }
    }

    @Test
    public void testClientCanConnectionToCluster() {
        String zookeeperURL = zkTestServer.getConnectString();
        startCluster(zookeeperURL);

        ClientConfig clientConfig = createClientConfig(zookeeperURL);

        //throws an exception when it cannot connect to a cluster
        HazelcastClient.newHazelcastClient(clientConfig);
    }

    private ClientConfig createClientConfig(String zookeeperURL) {
        DiscoveryStrategyConfig discoveryStrategyConfig = createDiscoveryConfig(zookeeperURL);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty("hazelcast.discovery.enabled", "true");
        clientConfig.getNetworkConfig().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);
        return clientConfig;
    }

    private void startCluster(String zookeeperURL) {
        Config config = createMemberConfig(zookeeperURL);
        for (int i = 0; i < CLUSTER_SIZE; i++) {
            instances[i] = Hazelcast.newHazelcastInstance(config);
        }
    }

    private Config createMemberConfig(String zookeeperURL) {
        Config config = new Config();
        config.getNetworkConfig().setPort(HAZELCAST_BASE_PORT);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.setProperty("hazelcast.discovery.enabled", "true");

        DiscoveryStrategyConfig discoveryStrategyConfig = createDiscoveryConfig(zookeeperURL);
        config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);
        return config;
    }

    private DiscoveryStrategyConfig createDiscoveryConfig(String zookeeperURL) {
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory());
        discoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), zookeeperURL);
        return discoveryStrategyConfig;
    }
}
