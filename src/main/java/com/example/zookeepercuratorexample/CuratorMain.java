package com.example.zookeepercuratorexample;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;

public class CuratorMain {

    public static void main(String[] args) {
        /**
         * * 建立连接（session）
         * * CRUD的操作命令
         * * 基于特性提供解决方案层面的封装
         */
        CuratorFramework curatorFramework=CuratorFrameworkFactory
                .builder()
                .connectionTimeoutMs(20000)
                .connectString(ZookeeperServerConfig.ZK_SERVERS) //读写分离(zookeeper-server)

                /**
                 * RetryNTimes 指定最大重试次数
                 * RetryOneTimes
                 * RetryUntilElapsed 一直重试，直到达到规定时间
                 * baseSleepTimeMs*Math.max(1,random.nextInt(1<<(maxRetries+1)) 递减重试，也就是说随着重试次数增加
                 *   sleep的时间也等增
                 */
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .sessionTimeoutMs(15000)
                .build();
                curatorFramework.start(); //启动
        try {

            Id id=new Id("digest", DigestAuthenticationProvider.generateDigest("jed:jed"));
            List<ACL> acls=new ArrayList<>();
            acls.add(new ACL(ZooDefs.Perms.ALL,id));

            byte[] data=curatorFramework.getData().forPath("/node");
            System.out.println("new String(data) = " + new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
