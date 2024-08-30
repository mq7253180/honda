package com.honda.service.impl;

import org.springframework.stereotype.Service;

import com.honda.entity.UserEntity;
import com.honda.service.UserServiceShardingProxy;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;
import com.quincy.sdk.SnowFlake;
import com.quincy.sdk.annotation.sharding.ShardingKey;

@Service
public class UserServiceShardingProxyImpl extends UserServiceImpl implements UserServiceShardingProxy {
	@Override
	public UserEntity updateLogin(@ShardingKey Long shardingKey, User vo, Client client) {
		return this.updateLogin(vo, client);
	}

	@Override
	public UserEntity update(@ShardingKey Long shardingKey, User vo) {
		return this.update(vo);
	}

	@Override
	public void create(@ShardingKey Long shardingKey, User vo, Long roleId) {
		if(vo.getId()==null)
			vo.setId(SnowFlake.nextId(shardingKey));
		this.create(vo, roleId);
	}
}