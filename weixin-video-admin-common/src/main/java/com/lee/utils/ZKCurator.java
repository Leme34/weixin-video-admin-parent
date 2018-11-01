package com.lee.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {

	// zk客户端
	private CuratorFramework client = null;
	final static Logger log = LoggerFactory.getLogger(ZKCurator.class);

	public ZKCurator(CuratorFramework client) {
		this.client = client;
	}

	public void init() {
		//指定此项目使用的命名空间(区分同一个zk管理的项目)
		client = client.usingNamespace("admin");

		try {
			// 判断在admin命名空间下是否有bgm节点  /admin/bmg
			if (client.checkExists().forPath("/bgm") == null) {
				/**
				 * 对于zk来讲，有两种类型的节点:
				 * 持久节点: 当你创建一个节点的时候，这个节点就永远存在，除非你手动删除
				 * 临时节点: 你创建一个节点之后，会话断开，会自动删除，当然也可以手动删除
				 */
				client.create().creatingParentsIfNeeded()   //递归创建节点
						.withMode(CreateMode.PERSISTENT)		// 节点类型：持久节点
						.withACL(Ids.OPEN_ACL_UNSAFE)			// acl：匿名权限(匿名用户都可访问)
						.forPath("/bgm");						//所要操作的节点
				log.info("zookeeper初始化成功...");

				log.info("zookeeper服务器状态：{}", client.isStarted());
			}
		} catch (Exception e) {
			log.error("zookeeper客户端连接、初始化错误...");
			e.printStackTrace();
		}
	}

	/**
	 * 增加或者删除bgm，向zk-server创建子节点，供小程序后端监听
	 * operObj:此节点存放的数据
	 */
	public void sendBgmOperator(String bgmId, String operObj) {
		try {
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT)		// 节点类型：持久节点
					.withACL(Ids.OPEN_ACL_UNSAFE)			// acl：匿名权限
					.forPath("/bgm/" + bgmId, operObj.getBytes());  //bgmId作为每个子节点名称
			System.out.println("创建子节点成功!bgmId="+bgmId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
