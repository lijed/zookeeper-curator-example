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
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @Author: Administrator
 * Created: 2021/8/18
 **/
public class CuratorGetDataWithAcl {
    public static void main(String[] args) {
        String id0 = "jed";
        String id1 = "jed";

        /**
         * * 建立连接（session）
         * * CRUD的操作命令
         * * 基于特性提供解决方案层面的封装
         */
        ACLProvider aclProvider = new ACLProvider() {
            private List<ACL> acl;
            @Override
            public List<ACL> getDefaultAcl() {
                if (acl == null) {
                    ArrayList<ACL> acl = ZooDefs.Ids.CREATOR_ALL_ACL; //初始化
                    acl.clear();
                    acl.add(new ACL(ZooDefs.Perms.ALL, new Id("auth", id0 + ":" + id1)));//添加
                    this.acl = acl;
                }
                return acl;
            }

            @Override
            public List<ACL> getAclForPath(String s) {
                return acl;
            }
        };
        CuratorFramework curatorFramework= null;
        try {
            curatorFramework= CuratorFrameworkFactory
                    .builder()
                    .connectionTimeoutMs(20000)
                    .aclProvider(aclProvider)
                    .connectString(ZookeeperServerConfig.ZK_SERVERS) //读写分离(zookeeper-server)
                    .authorization("digest", (id0 + ":" + id1).getBytes())

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
            //启动
            curatorFramework.start();
        } catch (Exception e) {
            e.printStackTrace();
            if (curatorFramework != null) {
                curatorFramework.close();
            }
        }


        try {
           byte[] data =  curatorFramework.getData().forPath("/node");
            System.out.println("data = " + new String(data));

            //创建节点
            curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/a/child","test1".getBytes());
            //获取数据
            System.out.println("data:" + new String(curatorFramework.getData().forPath("/a/child")));

            //设置数据
            curatorFramework.setData().forPath("/a", "test2".getBytes());
            System.out.println("data:" + new String(curatorFramework.getData().forPath("/a")));
            //获取子节点
            List<String> list = curatorFramework.getChildren().forPath("/");
            for(String s : list)
            {
                System.out.println("list:"+s);
            }
            //判断是否存在，stat==null即不存在
            Stat stat = curatorFramework.checkExists().forPath("/a");
            System.out.println("stat:"+ stat);
            //删除节点
            curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath("/a");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (curatorFramework != null) {
                curatorFramework.close();
            }
        }

    }
}
