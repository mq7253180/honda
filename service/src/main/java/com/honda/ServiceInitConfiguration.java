package com.honda;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.quincy.sdk.AuthActions;
import com.quincy.sdk.RootControllerHandler;
import com.quincy.sdk.TempPwdLoginEmailInfo;

import jakarta.servlet.http.HttpServletRequest;

@PropertySource({"classpath:application-service.properties", "classpath:application-sensitiveness.properties"})
@Configuration
public class ServiceInitConfiguration {
	@Bean
	public AuthActions authActions() {
		return new AuthActions() {
			@Override
			public void loadAttributes(Long userId, Map<String, Object> attributes) {
				
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