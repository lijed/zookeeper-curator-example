/*
 * Copyright 2021 tu.cn All right reserved. This software is the
 * confidential and proprietary information of tu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tu.cn
 */
package com.example.zookeepercuratorexample.app.lock;

import com.example.zookeepercuratorexample.ZookeeperServerConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.Locker;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/8/19
 **/
public class LockExamplePeer1 {


    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectionTimeoutMs(20000)
                .sessionTimeoutMs(20000)
                .connectString(ZookeeperServerConfig.ZK_SERVERS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework, "/ship-order-1");
        Locker locker = null;
        try {
            System.out.println("===================" + LocalTime.now().toString() + " 准备获得锁");
            locker = new Locker(interProcessMutex);
            System.out.println("===================" + LocalTime.now().toString() + " 获得锁");
            System.out.println("Process the request!!!!");
            TimeUnit.SECONDS.sleep(20);
            System.out.println("释放锁");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (locker != null) {
                System.out.println("===================" + LocalTime.now().toString() + " 释放锁");
                locker.close();
            }

            curatorFramework.close();
        }
    }
}
