package com.honda.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.honda.dao.UserAllShardsDao;
import com.honda.dao.UserExtRepositoy;
import com.honda.dao.UserRepository;
import com.honda.dao.UserShardingDao;
import com.honda.entity.UserEntity;
import com.honda.entity.UserExtEntity;
import com.honda.service.UserServiceShardingProxy;
import com.quincy.sdk.Client;
import com.quincy.sdk.SnowFlake;
import com.quincy.sdk.annotation.sharding.ShardingKey;
import com.quincy.sdk.o.User;

@Service
public class UserServiceShardingProxyImpl extends UserServiceImpl implements UserServiceShardingProxy {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserExtRepositoy userExtRepositoy;
	@Autowired
	private UserShardingDao userShardingDao;
	@Autowired
	private UserAllShardsDao userAllShardsDao;

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public UserEntity updateLogin(@ShardingKey Long shardingKey, User vo, Client client) {
		userShardingDao.updateUpdationStatusToSyncing(vo.getId());
		return this.updateLogin(vo, client);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public UserEntity update(@ShardingKey Long shardingKey, User vo) {
		userShardingDao.updateUpdationStatusToSyncing(vo.getId());
		return this.update(vo);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public UserEntity create(@ShardingKey Long shardingKey, User vo, Long roleId) {
		if(vo.getId()==null)
			vo.setId(SnowFlake.nextId(shardingKey));
		UserEntity po = this.create(vo, roleId);
		UserExtEntity userExt = new UserExtEntity();
		userExt.setId(po.getId());
		userExt.setUpdationStatus(1);
		userExtRepositoy.save(userExt);
		return po;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public void syncData(@ShardingKey Long shardingKey, Long id) {
		if(userShardingDao.updateUpdationStatusToSynced(id)>0)//修改完用户信息后立即同步，当前进程行为无需乐观锁
			this.doSyncData(shardingKey, id);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public void syncData(@ShardingKey Long shardingKey, Long id, int version) {
		if(userShardingDao.updateUpdationStatusToSynced(id, version)>0)//定时任务扫描，事务补偿，防止同时同步同一条数据
			this.doSyncData(shardingKey, id);
	}

	private void doSyncData(Long shardingKey, Long id) {
		UserEntity po = userRepository.findById(id).get();//查询当前分片待同步数据
		/*
		 * 同步至其他分片
		 */
		userAllShardsDao.update(shardingKey, po.getUsername(), po.getMobilePhone(), po.getEmail(), po.getPassword(), po.getName(), po.getGender(), id);
	}
}