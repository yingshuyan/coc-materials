package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.StorageArea
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StorageAreaRepository : JpaRepository<StorageArea, Long>