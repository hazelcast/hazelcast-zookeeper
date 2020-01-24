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

import com.hazelcast.logging.ILogger;
import com.hazelcast.cluster.Address;
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


/**
 * Implementation for Zookeeper Discovery Strategy
 */
public class ZookeeperDiscoveryStrategy
        extends AbstractDiscoveryStrategy {

    private static final String DEFAULT_PATH = "/discovery/hazelcast";
    private static final String DEFAULT_GROUP = "hazelcast";
    private static final int CURATOR_BASE_SLEEP_TIME_MS = 1000;

    private final DiscoveryNode thisNode;
    private final ILogger logger;

    private String group;
    private CuratorFramework client;
    private ServiceDiscovery<Void> serviceDiscovery;
    private ServiceInstance<Void> serviceInstance;

    public ZookeeperDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.thisNode = discoveryNode;
        this.logger = logger;
    }

    private boolean isMember() {
        return thisNode != null;
    }

    @Override
    public void start() {
        startCuratorClient();

        group = getOrDefault(ZookeeperDiscoveryProperties.GROUP, DEFAULT_GROUP);
        try {
            String path = getOrDefault(ZookeeperDiscoveryProperties.ZOOKEEPER_PATH, DEFAULT_PATH);
            ServiceDiscoveryBuilder<Void> discoveryBuilder = ServiceDiscoveryBuilder.builder(Void.class)
                    .basePath(path)
                    .client(client);

            if (isMember()) {
                //register members only into zookeeper
                //there no need to register clients
                prepareServiceInstance();
                discoveryBuilder.thisInstance(serviceInstance);
            }
            serviceDiscovery = discoveryBuilder.build();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new IllegalStateException("Error while talking to ZooKeeper. ", e);
        }
    }

    private void prepareServiceInstance() throws Exception {
        Address privateAddress = thisNode.getPrivateAddress();
        serviceInstance = ServiceInstance.<Void>builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address(privateAddress.getHost())
                .port(privateAddress.getPort())
                .name(group)
                .build();
    }

    private void startCuratorClient() {
        String zookeeperUrl = getOrNull(ZookeeperDiscoveryProperties.ZOOKEEPER_URL);
        if (zookeeperUrl == null) {
            throw new IllegalStateException("Zookeeper URL cannot be null.");
        }
        if (logger.isFinestEnabled()) {
            logger.finest("Using " + zookeeperUrl + " as Zookeeper URL");
        }
        client = CuratorFrameworkFactory.newClient(zookeeperUrl, new ExponentialBackoffRetry(CURATOR_BASE_SLEEP_TIME_MS, 3));
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
            if (isMember() && serviceDiscovery != null) {
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
