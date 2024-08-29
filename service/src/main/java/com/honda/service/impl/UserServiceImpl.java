package com.honda.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.quincy.auth.o.Enterprise;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;
import com.quincy.sdk.annotation.ReadOnly;
import com.quincy.sdk.annotation.sharding.Sharding;
import com.quincy.sdk.annotation.sharding.ShardingKey;
import com.quincy.sdk.helper.CommonHelper;

import com.honda.dao.UserAllShardsDao;
import com.honda.dao.UserDao;
import com.honda.dao.UserRepository;
import com.honda.entity.UserEntity;
import com.honda.o.UserDto;
import com.honda.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserAllShardsDao userAllShardsDao;
	@Autowired
	private UserDao userDao;
	

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public UserEntity update(@ShardingKey(snowFlake = true)Long userId, UserEntity vo) {
		UserEntity po = userRepository.findById(userId).get();
		String username = CommonHelper.trim(vo.getUsername());
		if(username!=null)
			po.setUsername(username);
		String password = CommonHelper.trim(vo.getPassword());
		if(password!=null)
			po.setPassword(password);
		String email = CommonHelper.trim(vo.getEmail());
		if(email!=null)
			po.setEmail(email);
		String mobilePhone = CommonHelper.trim(vo.getMobilePhone());
		if(mobilePhone!=null)
			po.setMobilePhone(mobilePhone);
		String name = CommonHelper.trim(vo.getName());
		if(name!=null)
			po.setName(name);
		String jsessionidPc = CommonHelper.trim(vo.getJsessionidPcBrowser());
		if(jsessionidPc!=null)
			po.setJsessionidPcBrowser(jsessionidPc);
		String jsessionidMobile = CommonHelper.trim(vo.getJsessionidMobileBrowser());
		if(jsessionidMobile!=null)
			po.setJsessionidMobileBrowser(jsessionidMobile);
		String jsessionidApp = CommonHelper.trim(vo.getJsessionidApp());
		if(jsessionidApp!=null)
			po.setJsessionidApp(jsessionidApp);
		Date lastLogined = vo.getLastLogined();
		if(lastLogined!=null)
			po.setLastLogined(lastLogined);
		userRepository.save(po);
		return po;
	}

	@Override
	@ReadOnly
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
		user.setEnterprises(new ArrayList<>(enterprises.size()));
		for(Enterprise e:enterprises.values())
			user.getEnterprises().add(e);
		if(user.getEnterprises().size()==1)
			user.setCurrentEnterprise(user.getEnterprises().get(0));
		return user;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public void create(@ShardingKey(snowFlake = true)Long userId, UserEntity vo, Long roleId) {
		vo.setId(userId);
		UserEntity po = userRepository.save(vo);
		userDao.addRoleUserRel(roleId, po.getId());
	}

	@Sharding
	@Override
	public int updatePassword(@ShardingKey(snowFlake = true)Long userId, String password) {
		return userDao.updatePassword(password, userId);
	}
}