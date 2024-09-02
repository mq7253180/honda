package com.honda.service;

import com.honda.entity.UserEntity;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;
import com.quincy.sdk.annotation.sharding.ShardingKey;

public interface UserServiceShardingProxy {
	public UserEntity updateLogin(Long shardingKey, User vo, Client client);
	public UserEntity update(Long shardingKey, User vo);
	public UserEntity create(Long shardingKey, User vo, Long roleId);
	public void syncData(@ShardingKey Long shardingKey, Long id, int version);
	public void syncData(@ShardingKey Long shardingKey, Long id);
}