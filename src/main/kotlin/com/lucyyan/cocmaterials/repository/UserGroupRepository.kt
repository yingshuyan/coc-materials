package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.UserGroup
import org.springframework.data.jpa.repository.JpaRepository

interface UserGroupRepository : JpaRepository<UserGroup, Long> {}