package com.honda.service;

import com.honda.entity.UserEntity;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;

public interface UserService {
	public UserEntity update(User vo);
	public UserEntity updateLogin(User vo, Client client);
	public User find(String loginName, Client client);
	public void create(User vo, Long roleId);
	public int updatePassword(User vo);
}