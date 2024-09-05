package com.honda;

import com.honda.entity.UserEntity;
import com.quincy.sdk.Client;
import com.quincy.sdk.o.User;

public class ControllerUtils {
	public static User toUser(UserEntity entity, Client client) {
		User user = new User();
		user.setId(entity.getId());
		user.setName(entity.getName());
		user.setGender(entity.getGender());
		user.setEmail(entity.getEmail());
		user.setMobilePhone(entity.getMobilePhone());
		user.setUsername(entity.getUsername());
		user.setPassword(entity.getPassword());
//		user.setLastLogined(entity.getLastLogined());
		user.setCreationTime(entity.getCreationTime());
		String jsessionid = null;
		if(client.isPc()) {
			jsessionid = entity.getJsessionidPcBrowser();
		} else if(client.isMobile()) {
			jsessionid = entity.getJsessionidMobileBrowser();
		} else if(client.isApp()) {
			jsessionid = entity.getJsessionidApp();
		}
		user.setJsessionid(jsessionid);
		return user;
	}

	public static void loadEntity(User vo, UserEntity po) {
		if(vo.getName()!=null)
			po.setName(vo.getName());;
		if(vo.getGender()!=null)
			po.setGender(vo.getGender());;
		if(vo.getEmail()!=null)
			po.setEmail(vo.getEmail());;
		if(vo.getMobilePhone()!=null)
			po.setMobilePhone(vo.getMobilePhone());;
		if(vo.getUsername()!=null)
			po.setUsername(vo.getUsername());;
		if(vo.getPassword()!=null)
			po.setPassword(vo.getPassword());;
	}
}