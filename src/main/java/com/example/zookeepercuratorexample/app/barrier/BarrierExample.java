/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.zookeepercuratorexample.app.barrier;

import com.example.zookeepercuratorexample.ZookeeperServerConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.server.ZooKeeperServerConf;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/8/20
 **/
public class BarrierExample {

    public static void main(String[] args) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZookeeperServerConfig.ZK_SERVERS)
                .connectionTimeoutMs(20000)
                .sessionTimeoutMs(20000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
        DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(curatorFramework, "/barrier", 3);

        new Thread(new Workder(barrier)).start();
    }

    static class Workder implements  Runnable {

        DistributedDoubleBarrier barrier;

        public Workder(DistributedDoubleBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                barrier.enter();
                System.out.println(Thread.currentThread().getName() + " 开始处理");
                TimeUnit.SECONDS.sleep(5);

                System.out.println(Thread.currentThread().getName() +  " 处理结束");

                barrier.leave();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
