package com.honda.service;

import com.honda.entity.UserEntity;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;
import com.quincy.sdk.annotation.sharding.ShardingKey;

public interface UserService {
	public UserEntity update(Integer shardingKey, UserEntity vo);
	public User find(String loginName, Client client);
	public void create(@ShardingKey Integer shardingKey, UserEntity vo, Long roleId);
}