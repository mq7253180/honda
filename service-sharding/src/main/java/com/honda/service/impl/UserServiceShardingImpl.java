package com.honda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.honda.dao.UserAllShardsDao;
import com.honda.entity.UserEntity;
import com.honda.o.UserDto;
import com.honda.o.UserExtDto;
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
	private UserAllShardsDao userAllShardsDao;
	@Autowired
	private UserServiceShardingProxy userServiceShardingProxy;

	@Override
	public UserEntity updateLogin(User vo, Client client) {
		UserEntity po = userServiceShardingProxy.updateLogin(vo.getShardingKey(), vo, client);
		sync(vo.getId(), vo.getShardingKey());
		return po;
	}

	@Override
	public UserEntity update(User vo) {
		UserEntity po = userServiceShardingProxy.update(vo.getShardingKey(), vo);
		sync(vo.getId(), vo.getShardingKey());
		return po;
	}

	private static Map<Long, Long> userIdsToSync = new ConcurrentHashMap<Long, Long>();
	private static final Object LOCK = new Object();
	private static boolean stoped = false;

	private static void sync(Long userId, Long shardingKey) {
		userIdsToSync.put(userId, shardingKey);
		synchronized(LOCK) {
			LOCK.notifyAll();
		}
	}

	private final static long TIMER_DELAY_PERIOD = 30000;

	@PostConstruct
	public void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(!stoped) {
					synchronized(LOCK) {
						try {
							LOCK.wait(10000);
						} catch (InterruptedException e) {
							log.error("WAITING_ERROR=========", e);
						}
						if(userIdsToSync!=null&&userIdsToSync.size()>0) {
							userIdsToSync.entrySet().forEach(entity->{
								Long id = entity.getKey();
								try {
									userServiceShardingProxy.syncData(entity.getValue(), id);
									userIdsToSync.remove(id);
								} catch(Exception ex) {
									log.error("USER_DATA_SYNC_ERROR===============\r\n", ex);
								}
							});
						}
					}
				}
			}
		}).start();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				long currentTimeMillis = System.currentTimeMillis();
				List<UserExtDto>[] userExtListArray = userAllShardsDao.findUserExt();
				for(int i=0;i<userExtListArray.length;i++) {
					List<UserExtDto> userExtList = userExtListArray[i];
					long shardingKey = i;
					userExtList.forEach(o->{
						if(currentTimeMillis-o.getLastUpdationTime().getTime()>TIMER_DELAY_PERIOD)//超过一定时间才同步，防止与正常同步冲突
							userServiceShardingProxy.syncData(shardingKey, o.getId(), o.getUpdationVersion());
					});
				}
			}
		}, TIMER_DELAY_PERIOD, TIMER_DELAY_PERIOD);
	}

	@PreDestroy
	public void destroy() {
		stoped = true;
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