package com.honda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.ModelAndView;

import com.honda.service.UserService;
import com.quincy.auth.controller.AuthActions;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;

import jakarta.servlet.http.HttpServletRequest;

@PropertySource(value = {"classpath:application-service.properties", "classpath:application-sensitiveness.properties"})
@Configuration
public class ShardingServiceInitConfiguration {
	@Autowired
	private UserService userService;

	@Primary
	@Bean("primaryAuthActions")
	public AuthActions authActions() {
		return new AuthActions() {
			@Override
			public User findUser(String username, Client client) {
				return userService.find(username, client);
			}

			@Override
			public void updateLastLogin(Long userId, String jsessionid, Client client) {
				User vo = new User();
				vo.setId(userId);
				vo.setJsessionid(jsessionid);
				userService.updateLogin(vo, client);
			}

			@Override
			public void updatePassword(Long userId, String password) {
				User vo = new User();
				vo.setId(userId);
				vo.setPassword(password);
				userService.update(vo);
			}

			@Override
			public ModelAndView signinView(HttpServletRequest request) {
				return null;
			}
		};
	}
}