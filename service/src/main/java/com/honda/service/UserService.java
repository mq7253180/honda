package com.honda.service;

import com.honda.entity.UserEntity;
import com.quincy.sdk.Client;
import com.quincy.sdk.o.User;

public interface UserService {
	public UserEntity update(User vo);
	public UserEntity updateLogin(User vo, Client client);
	public User find(String loginName, Client client);
	public UserEntity create(User vo, Long roleId);
}