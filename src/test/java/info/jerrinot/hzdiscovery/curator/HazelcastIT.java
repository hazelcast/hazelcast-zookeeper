package info.jerrinot.hzdiscovery.curator;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.GroupProperty;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class HazelcastIT {
    private TestingServer zkTestServer;

    @Before
    public void setUp() throws Exception {
        zkTestServer = new TestingServer();
    }

    @After
    public void tearDown() throws IOException {
        zkTestServer.close();
    }


    @Test
    public void testIntegration() {
        Config config = new Config();
        config.setProperty(GroupProperty.DISCOVERY_SPI_ENABLED, "true");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        DiscoveryStrategyConfig dicoveryStrategyConfig = new DiscoveryStrategyConfig(new ZookeeperDiscoveryStrategyFactory());
        dicoveryStrategyConfig.addProperty(ZookeeperDiscoveryProperties.ZOOKEEPER_URL.key(), zkTestServer.getConnectString());
        DiscoveryConfig discoveryConfig = config.getNetworkConfig().getJoin().getDiscoveryConfig();
        discoveryConfig.addDiscoveryProviderConfig(dicoveryStrategyConfig);

        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);

        int instance1Size = instance1.getCluster().getMembers().size();
        assertEquals(2, instance1Size);
        int instance2Size = instance2.getCluster().getMembers().size();
        assertEquals(2, instance2Size);

    }
}
