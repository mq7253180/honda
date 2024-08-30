package com.honda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.honda.dao.UserAllShardsDao;
import com.honda.entity.UserEntity;
import com.honda.o.UserDto;
import com.honda.service.UserService;
import com.honda.service.UserServiceShardingProxy;
import com.quincy.auth.o.Enterprise;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;

@Primary
@Service
public class UserServiceShardingImpl implements UserService {
	@Autowired
	private UserServiceShardingProxy userServiceShardingProxy;
	@Autowired
	private UserAllShardsDao userAllShardsDao;

	@Override
	public UserEntity updateLogin(User vo, Client client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserEntity update(User vo) {
		return null;
	}

	@Override
	public User find(String loginName, Client client) {
		List<UserDto>[] lists = userAllShardsDao.findUsers(loginName, loginName, loginName);
		User user = null;
		Map<Long, Enterprise> enterprises = new HashMap<Long, Enterprise>();;
		for(List<UserDto> list:lists) {
			for(UserDto o:list) {
				if(user==null) {
					user = new User();
					user.setId(o.getId());
					user.setUsername(o.getUsername());
					user.setMobilePhone(o.getMobilePhone());
					user.setEmail(o.getEmail());
					user.setPassword(o.getPassword());
					user.setName(o.getName());
					user.setGender(o.getGender());
					if(client.isPc())
						user.setJsessionid(o.getJsessionidPc());
					if(client.isMobile())
						user.setJsessionid(o.getJsessionidMobile());
					if(client.isApp())
						user.setJsessionid(o.getJsessionidApp());
				}
				Enterprise e = enterprises.get(o.getEnterpriseId());
				if(e==null) {
					e = new Enterprise();
					e.setId(o.getEnterpriseId());
					e.setName(o.getEnterpriseName());
					e.setShardingKey(o.getShardingKey());
					enterprises.put(o.getEnterpriseId(), e);
				}
			}
		}
		List<Enterprise> enterpriseList = new ArrayList<Enterprise>(enterprises.size());
		enterpriseList.addAll(enterprises.values());
		user.setEnterprises(enterpriseList);
		if(user.getEnterprises().size()==1)
			user.setCurrentEnterprise(user.getEnterprises().get(0));
		return user;
	}

	@Override
	public void create(UserEntity vo, Long roleId) {
		
	}

	@Override
	public int updatePassword(User vo) {
		return 0;
	}
}