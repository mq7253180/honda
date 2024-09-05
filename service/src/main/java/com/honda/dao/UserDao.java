package com.honda.dao;

import com.quincy.sdk.annotation.jdbc.ExecuteUpdate;
import com.quincy.sdk.annotation.jdbc.JDBCDao;

@JDBCDao
public interface UserDao {
	@ExecuteUpdate(sql = "INSERT INTO s_role_user_rel(role_id, user_id) VALUES(?, ?)")
	public int addRoleUserRel(Long roleId, Long userId);
}