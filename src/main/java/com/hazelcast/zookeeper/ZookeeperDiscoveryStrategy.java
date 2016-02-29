/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.zookeeper;

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

import java.net.UnknownHostException;
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
    private static final int DEFAULT_HZ_PORT = 5701;
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

    @Override
    public void start() {
        startCuratorClient();
        /* Added this for clients, because Discovery SPI does not set
        discoveryNode for clients */
        Address privateAddress = null;
        try {
            privateAddress = new Address("127.0.0.1", DEFAULT_HZ_PORT);
        } catch (UnknownHostException e) {
            logger.warning("Cannot bind local host");
        }

        if (thisNode != null) {
            privateAddress = thisNode.getPrivateAddress();
        }

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
