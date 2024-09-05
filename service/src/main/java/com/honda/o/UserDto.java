package com.honda.o;

import java.io.Serializable;

import com.quincy.sdk.annotation.jdbc.Column;
import com.quincy.sdk.annotation.jdbc.DTO;

import lombok.Data;

@Data
@DTO
public class UserDto implements Serializable {
	private static final long serialVersionUID = 2622155359969919915L;
	@Column("id")
	private Long id;
	@Column("username")
	private String username;
	@Column("email")
	private String email;
	@Column("mobile_phone")
	private String mobilePhone;
	@Column("password")
	private String password;
	@Column("name")
	private String name;
	@Column("gender")
	private Integer gender;
	@Column("jsessionid_pc_browser")
	private String jsessionidPc;
	@Column("jsessionid_mobile_browser")
	private String jsessionidMobile;
	@Column("jsessionid_app")
	private String jsessionidApp;
	@Column("e_id")
	private Long enterpriseId;
	@Column("e_name")
	private String enterpriseName;
	@Column("sharding_key")
	private Integer shardingKey;
}