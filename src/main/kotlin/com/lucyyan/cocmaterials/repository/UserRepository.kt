package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>