package com.honda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.honda.entity.UserExtEntity;

@Repository
public interface UserExtRepositoy extends JpaRepository<UserExtEntity, Long>, JpaSpecificationExecutor<UserExtEntity> {
}