/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.zookeepercuratorexample.app.queue;

import com.example.zookeepercuratorexample.ZookeeperServerConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.SimpleDistributedQueue;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/9/9
 **/
public class QueueDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectionTimeoutMs(20000)
                .sessionTimeoutMs(20000)
                .connectString(ZookeeperServerConfig.ZK_SERVERS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();

        SimpleDistributedQueue queue = new SimpleDistributedQueue(curatorFramework, "/zkshare/queue-");
        for (int i = 0; i < 20 ; i++) {
            queue.offer(("request"+ i).getBytes());
        }

        for (int i = 0; i <10 ; i++ ) {
            queue.take();
        }

    }
}
