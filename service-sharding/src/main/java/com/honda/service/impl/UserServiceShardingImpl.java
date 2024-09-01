package com.honda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.honda.dao.UserAllShardsDao;
import com.honda.dao.UserRepository;
import com.honda.dao.UserShardingDao;
import com.honda.entity.UserEntity;
import com.honda.o.UserDto;
import com.honda.service.UserService;
import com.honda.service.UserServiceShardingProxy;
import com.quincy.auth.o.Enterprise;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;
import com.quincy.sdk.SnowFlake;

@Primary
@Service
public class UserServiceShardingImpl implements UserService {
	private final static Log log = LogFactory.getLog(UserServiceShardingImpl.class);
	@Autowired
	private UserServiceShardingProxy userServiceShardingProxy;
	@Autowired
	private UserAllShardsDao userAllShardsDao;
	@Autowired
	private UserShardingDao userShardingDao;
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserEntity updateLogin(User vo, Client client) {
		UserEntity po = userServiceShardingProxy.updateLogin(vo.getShardingKey(), vo, client);
		sync(vo.getId());
		return po;
	}

	@Override
	public UserEntity update(User vo) {
		UserEntity po = userServiceShardingProxy.update(vo.getShardingKey(), vo);
		sync(vo.getId());
		return po;
	}

	private static Map<Long, Long> userIdsToSync = new ConcurrentHashMap<Long, Long>();
	private static final Object LOCK = new Object();
	private static boolean stoped = false;

	private static void sync(Long userId) {
		userIdsToSync.put(userId, userId);
		synchronized(LOCK) {
			LOCK.notifyAll();
		}
	}

	@PostConstruct
	public void init() {
		while(!stoped) {
			synchronized(LOCK) {
				try {
					LOCK.wait(30000);
				} catch (InterruptedException e) {
					log.error("WAITING_ERROR=========", e);
				}
				userIdsToSync.keySet().forEach(id->{
					syncData(id);
					userIdsToSync.remove(id);
				});
			}
		}
	}

	@PreDestroy
	public void destroy() {
		stoped = true;
	}

	private void syncData(Long id) {
		UserEntity po = userRepository.findById(id).get();
		
		userShardingDao.updateUpdationStatus(1, id);
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
		if(user.getEnterprises().size()==1) {
			Enterprise currentEnterprise = user.getEnterprises().get(0);
			user.setCurrentEnterprise(currentEnterprise);
			user.setShardingKey(SnowFlake.extractShardingKey(currentEnterprise.getId()));
		}
		return user;
	}

	@Override
	public UserEntity create(User vo, Long roleId) {
		return userServiceShardingProxy.create(vo.getShardingKey(), vo, roleId);
	}

	public static void main(String[] args) {
		for(int i=0;i<10;i++) {
			long l = Long.parseLong(String.valueOf(i));
			userIdsToSync.put(l, l);
		}
		userIdsToSync.keySet().forEach(l->{
			System.out.println(l);
			userIdsToSync.remove(l);
		});
		System.out.println("=============");
		userIdsToSync.keySet().forEach(l->{
			System.out.println(l);
		});
	}
}