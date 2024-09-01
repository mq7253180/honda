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
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;
import com.quincy.sdk.SnowFlake;
import com.quincy.sdk.annotation.sharding.ShardingKey;

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
		userShardingDao.updateUpdationStatus(0, vo.getId());
		return this.updateLogin(vo, client);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public UserEntity update(@ShardingKey Long shardingKey, User vo) {
		userShardingDao.updateUpdationStatus(0, vo.getId());
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
	public int syncData(@ShardingKey Long shardingKey, Long id) {
		UserEntity po = userRepository.findById(id).get();
		userAllShardsDao.update(shardingKey, po.getUsername(), po.getMobilePhone(), po.getEmail(), po.getPassword(), po.getName(), po.getGender(), id);
		return userShardingDao.updateUpdationStatus(1, id);
	}
}