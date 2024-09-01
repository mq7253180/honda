package com.honda.o;

import java.util.Date;

import com.quincy.sdk.annotation.Column;
import com.quincy.sdk.annotation.DTO;

import lombok.Data;

@Data
@DTO
public class UserExtDto {
	@Column("id")
	private Long id;
	@Column("updation_status")
	private Integer updationStatus;
	@Column("updation_version")
	private Integer updationVersion;
	@Column("last_updation_time")
	private Date lastUpdationTime;
}