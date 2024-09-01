package com.honda;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.ModelAndView;

import com.honda.service.UserService;
import com.quincy.auth.TempPwdLoginEmailInfo;
import com.quincy.auth.controller.AuthActions;
import com.quincy.auth.controller.RootControllerHandler;
import com.quincy.auth.o.User;
import com.quincy.sdk.Client;

import jakarta.servlet.http.HttpServletRequest;

@PropertySource(value = {"classpath:application-service.properties", "classpath:application-sensitiveness.properties"})
@Configuration
public class ServiceInitConfiguration {
	@Autowired
	private UserService userService;

	@Bean
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

	@Bean
	public RootControllerHandler rootControllerHandler() {
		return new RootControllerHandler() {
			@Override
			public boolean loginRequired() {
				return true;
			}

			@Override
			public Map<String, ?> viewObjects(HttpServletRequest request) throws Exception {
				return null;
			}
		};
	}

	@Bean
	public TempPwdLoginEmailInfo tempPwdLoginEmailInfo() {
		return new TempPwdLoginEmailInfo() {
			@Override
			public String getSubject() {
				return "临时密码";
			}

			@Override
			public String getContent() {
				return "临时密码为: {0}, {1}分钟内有效";
			}
		};
	}
}