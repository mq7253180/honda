package com.honda.dao;

import com.quincy.sdk.annotation.ExecuteUpdate;
import com.quincy.sdk.annotation.JDBCDao;

@JDBCDao
public interface UserShardingDao {
	@ExecuteUpdate(sql = "UPDATE b_user_ext SET updation_status=0,last_updation_time=NOW() WHERE id=?")
	public int updateUpdationStatusToSyncing(Long id);
	@ExecuteUpdate(sql = "UPDATE b_user_ext SET updation_status=1,updation_version=updation_version+1 WHERE updation_status=0 AND id=?")
	public int updateUpdationStatusToSynced(Long id);
	@ExecuteUpdate(sql = "UPDATE b_user_ext SET updation_status=1,updation_version=updation_version+1 WHERE updation_status=0 AND id=? AND updation_version=?")
	public int updateUpdationStatusToSynced(Long id, int version);
}