package com.honda.dao;

import java.util.List;

import com.honda.o.UserDto;
import com.quincy.sdk.annotation.sharding.AllShardsJDBCDao;
import com.quincy.sdk.annotation.sharding.ExecuteQuery;

@AllShardsJDBCDao
public interface UserAllShardsDao {
	@ExecuteQuery(sql = "SELECT u.id,u.username,u.mobile_phone,u.email,u.password,u.name,u.gender,u.jsessionid_pc_browser,u.jsessionid_mobile_browser,u.jsessionid_app,e.id AS e_id,e.name AS e_name,e.sharding_key "
			+ "FROM b_user u "
			+ "INNER JOIN s_role_user_rel r ON u.id=r.user_id AND r.status=1 "
			+ "INNER JOIN s_role role ON r.role_id=role.id "
			+ "INNER JOIN s_enterprise e ON role.enterprise_id=e.id "
			+ "WHERE u.mobile_phone=? OR u.email=? OR u.username=?", returnItemType = UserDto.class)
	public List<UserDto>[] findUsers(String mobilePhone, String email, String username);
}