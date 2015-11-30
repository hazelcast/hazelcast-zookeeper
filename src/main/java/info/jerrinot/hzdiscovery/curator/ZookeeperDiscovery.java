package info.jerrinot.hzdiscovery.curator;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ZookeeperDiscovery extends AbstractDiscoveryStrategy {
    private static final String DEFAULT_PATH = "/discovery/hazelcast";
    private static final String DEFAULT_GROUP = "hazelcast";

    private final DiscoveryNode thisNode;
    private final ILogger logger;

    private String group;
    private CuratorFramework client;
    private ServiceDiscovery<Void> serviceDiscovery;
    private ServiceInstance<Void> serviceInstance;

    public ZookeeperDiscovery(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.thisNode = discoveryNode;
        this.logger = logger;
    }

    @Override
    public void start() {
        startCuratorClient();

        Address privateAddress = thisNode.getPrivateAddress();
        group = getOrDefault(ZookeeperDiscoveryProperties.GROUP, DEFAULT_GROUP);
        try {
            serviceInstance = ServiceInstance.<Void>builder()
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .address(privateAddress.getHost())
                    .port(privateAddress.getPort())
                    .name(group)
                    .build();

            String path = getOrDefault(ZookeeperDiscoveryProperties.ZOOKEEPER_PATH, DEFAULT_PATH);
            serviceDiscovery = ServiceDiscoveryBuilder.builder(Void.class)
                    .basePath(path)
                    .client(client)
                    .thisInstance(serviceInstance)
                    .build();

            serviceDiscovery.start();
        } catch (Exception e) {
            throw new IllegalStateException("Error while talking to ZooKeeper. ", e);
        }
    }

    private void startCuratorClient() {
        String zookeeperUrl = getOrNull(ZookeeperDiscoveryProperties.ZOOKEEPER_URL);
        if (zookeeperUrl == null) {
            throw new IllegalStateException("Zookeeper URL cannot be null.");
        }
        if (logger.isFinestEnabled()) {
            logger.finest("Using " + zookeeperUrl + " as Zookeeper URL");
        }
        client = CuratorFrameworkFactory.newClient(zookeeperUrl, new ExponentialBackoffRetry(1000, 3));
        client.start();
    }

    public Iterable<DiscoveryNode> discoverNodes() {
        try {
            Collection<ServiceInstance<Void>> members = serviceDiscovery.queryForInstances(group);
            List<DiscoveryNode> nodes = new ArrayList<DiscoveryNode>(members.size());
            for (ServiceInstance<Void> serviceInstance : members) {
                String host = serviceInstance.getAddress();
                Integer port = serviceInstance.getPort();

                Address address = new Address(host, port);

                SimpleDiscoveryNode node = new SimpleDiscoveryNode(address);
                nodes.add(node);
            }
            return nodes;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error while talking to ZooKeeper", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error while talking to ZooKeeper", e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (serviceDiscovery != null) {
                serviceDiscovery.unregisterService(serviceInstance);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error while talking to ZooKeeper", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error while talking to ZooKeeper", e);
        } finally {
            IOUtils.closeSafely(serviceDiscovery);
            IOUtils.closeSafely(client);
        }
    }
}
