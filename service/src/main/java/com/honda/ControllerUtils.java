package com.honda;

import com.honda.entity.UserEntity;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;

public class ControllerUtils {
	public static User toUser(UserEntity entity, Client client) {
		User user = new User();
		user.setId(entity.getId());
		user.setCreationTime(entity.getCreationTime());
		user.setName(entity.getName());
		user.setUsername(entity.getUsername());
		user.setMobilePhone(entity.getMobilePhone());
		user.setEmail(entity.getEmail());
		user.setPassword(entity.getPassword());
		if(client.isPc())
			user.setJsessionid(entity.getJsessionidPcBrowser());
		if(client.isMobile())
			user.setJsessionid(entity.getJsessionidMobileBrowser());
		if(client.isApp())
			user.setJsessionid(entity.getJsessionidApp());
		user.setLastLogined(entity.getLastLogined());
		return user;
	}
}