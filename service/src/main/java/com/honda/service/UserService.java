package com.honda.service;

import com.honda.entity.UserEntity;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;

public interface UserService {
	public UserEntity update(Long userId, UserEntity vo);
	public User find(String loginName, Client client);
	public void create(Long userId, UserEntity vo, Long roleId);
	public int updatePassword(Long userId, String password);
}