package com.honda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.honda.service.UserService;
import com.quincy.sdk.o.User;

@Controller
@RequestMapping("/ttt")
public class TestController {
	@Autowired
	private UserService userService;

	@RequestMapping("/user/update")
	@ResponseBody
	public void updateUser(@RequestParam(required = true, value = "name")String name) {
		User vo = new User();
		vo.setId(4l);
		vo.setShardingKey(17l);
		vo.setName(name);
		userService.update(vo);
	}
}