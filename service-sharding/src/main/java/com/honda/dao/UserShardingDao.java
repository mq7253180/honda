package com.honda.dao;

import com.quincy.sdk.annotation.ExecuteUpdate;
import com.quincy.sdk.annotation.JDBCDao;

@JDBCDao
public interface UserShardingDao {
	@ExecuteUpdate(sql = "UPDATE b_user_ext SET updation_status=? WHERE id=?")
	public int updateUpdationStatus(int status, Long id);
}