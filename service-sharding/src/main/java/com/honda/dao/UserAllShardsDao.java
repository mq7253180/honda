package com.honda.dao;

import java.util.List;

import com.honda.o.UserExtDto;
import com.quincy.sdk.annotation.sharding.AllShardsJDBCDao;
import com.quincy.sdk.annotation.sharding.ExecuteQuery;
import com.quincy.sdk.annotation.sharding.ExecuteUpdate;
import com.quincy.sdk.annotation.sharding.ShardingKeyToSkip;

@AllShardsJDBCDao
public interface UserAllShardsDao {
	@ExecuteUpdate(sql = "UPDATE b_user SET username=?,mobile_phone=?,email=?,password=?,name=?,gender=? WHERE id=?")
	public int[] update(@ShardingKeyToSkip Long shardingKeyToSkip, String username, String mobilePhone, String email, String password, String name, int gender, Long id);
	@ExecuteQuery(sql = "SELECT * FROM b_user_ext WHERE updation_status=0;", returnItemType = UserExtDto.class)
	public List<UserExtDto>[] findUserExt();
}