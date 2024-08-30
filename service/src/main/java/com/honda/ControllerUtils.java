package com.honda;

import com.honda.entity.UserEntity;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;

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
		user.setLastLogined(entity.getLastLogined());
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
}