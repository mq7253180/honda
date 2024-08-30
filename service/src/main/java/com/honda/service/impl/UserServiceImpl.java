package com.honda.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.quincy.auth.o.User;
import com.quincy.sdk.Client;
import com.quincy.sdk.annotation.ReadOnly;
import com.quincy.sdk.helper.CommonHelper;
import com.honda.ControllerUtils;
import com.honda.dao.UserDao;
import com.honda.dao.UserRepository;
import com.honda.entity.UserEntity;
import com.honda.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserDao userDao;

	@Override
	public UserEntity updateLogin(User vo, Client client) {
		String jsessionid = vo.getJsessionid();
		UserEntity po = userRepository.findById(vo.getId()).get();
		po.setLastLogined(new Date());
		if(client.isPc())
			po.setJsessionidPcBrowser(jsessionid);
		else if(client.isMobile())
			po.setJsessionidMobileBrowser(jsessionid);
		else if(client.isApp())
			po.setJsessionidApp(jsessionid);
		return userRepository.save(po);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public UserEntity update(User vo) {
		UserEntity po = userRepository.findById(vo.getId()).get();
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
		Integer gender = vo.getGender();
		if(gender!=null)
			po.setGender(gender);
		return userRepository.save(po);
	}

	@Override
	@ReadOnly
	public User find(String loginName, Client client) {
		UserEntity userPo = userRepository.findByUsernameOrEmailOrMobilePhone(loginName, loginName, loginName);
		return ControllerUtils.toUser(userPo, client);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public void create(User vo, Long roleId) {
		UserEntity po = new UserEntity();
		ControllerUtils.loadEntity(vo, po);
		Date now = new Date();
		po.setCreationTime(now);
		po.setLastLogined(now);
		if(vo.getId()!=null)
			po.setId(vo.getId());
		po = userRepository.save(po);
		userDao.addRoleUserRel(roleId, po.getId());
	}

	@Override
	public int updatePassword(User vo) {
		return userDao.updatePassword(vo.getPassword(), vo.getId()).length;
	}
}