package com.honda;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.quincy.auth.entity.Permission;
import com.quincy.auth.entity.Role;
import com.quincy.sdk.AuthServerActions;
import com.quincy.sdk.RootControllerHandler;
import com.quincy.sdk.TempPwdLoginEmailInfo;
import com.quincy.sdk.o.Menu;
import com.quincy.sdk.o.User;

import jakarta.servlet.http.HttpServletRequest;

@PropertySource({"classpath:application-service.properties", "classpath:application-sensitiveness.properties"})
@Configuration
public class ServiceInitConfiguration {
	@Bean
	public AuthServerActions authActions() {
		return new AuthServerActions() {
			@Override
			public Object userExt(User user) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void sms(String mobilePhone, String vcode, int expireMinuts) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<Role> findRoles(Long userId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Permission> findPermissions(Long userId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Menu> findMenus(Long userId) {
				// TODO Auto-generated method stub
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