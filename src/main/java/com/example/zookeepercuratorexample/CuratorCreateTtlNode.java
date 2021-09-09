/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.zookeepercuratorexample;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.nodes.PersistentTtlNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/8/18
 **/
public class CuratorCreateTtlNode {
    CuratorFramework curatorFramework;
    public CuratorCreateTtlNode() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectionTimeoutMs(15000)
                .sessionTimeoutMs(15000)
                .connectString(ZookeeperServerConfig.ZK_SERVERS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
    }

    public void ayncMode() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        curatorFramework.create()
                .withTtl(10000)
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_WITH_TTL)
                .inBackground((CuratorFramework client, CuratorEvent event) -> {
                    System.out.println(event.toString());

                    countDownLatch.countDown();
                }).forPath("/ttl-node", "ttl".getBytes());


        countDownLatch.await();

        curatorFramework.close();
    }

    public void crateTLLMethod2() throws InterruptedException {
        PersistentTtlNode persistentTtlNode = new PersistentTtlNode(curatorFramework, "/stores/abc.com", 10000, "".getBytes());
        persistentTtlNode.start();
        boolean flag = persistentTtlNode.waitForInitialCreate(1000, TimeUnit.MICROSECONDS);
        System.out.println(flag);
        persistentTtlNode.close();
    }

    public static void main(String[] args) throws Exception {
        CuratorCreateTtlNode curatorCreateTtlNode = new CuratorCreateTtlNode();
        curatorCreateTtlNode.ayncMode();
    }
}
