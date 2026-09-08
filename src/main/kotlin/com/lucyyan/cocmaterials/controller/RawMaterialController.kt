package com.lucyyan.cocmaterials.controller

import com.lucyyan.cocmaterials.dto.CreateRawMaterialRequest
import com.lucyyan.cocmaterials.dto.PageResponse
import com.lucyyan.cocmaterials.dto.RawMaterialFilter
import com.lucyyan.cocmaterials.dto.RawMaterialResponse
import com.lucyyan.cocmaterials.dto.UpdateRawMaterialRequest
import com.lucyyan.cocmaterials.service.RawMaterialService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.JsonNode

@RestController
@RequestMapping("/api/raw-materials")
class RawMaterialController (
    private val rawMaterialService: RawMaterialService
) {

    @GetMapping
    fun getAllMaterials(
        filter: RawMaterialFilter,
        pageable: Pageable): PageResponse<RawMaterialResponse> {
            return rawMaterialService.getAllRawMaterials(
                pageable,
                filter
            )
    }

    @GetMapping("/{id}")
    fun getRawMaterialById(@PathVariable id: Long): RawMaterialResponse {
        return rawMaterialService.getRawMaterialById(id)
    }

    @PostMapping
    fun createRawMaterial(
        @Valid @RequestBody request: CreateRawMaterialRequest
    ): ResponseEntity<RawMaterialResponse> {
        val created =  rawMaterialService.createRawMaterial(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateRawMaterial(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateRawMaterialRequest
    ): RawMaterialResponse {
        return rawMaterialService.updateRawMaterial(id,request)
    }

    @PatchMapping("/{id}")
    fun patchRawMaterial(
        @PathVariable id: Long,
        @RequestBody patch: JsonNode
    ): RawMaterialResponse {
        return rawMaterialService.patchRawMaterial(id, patch)
    }

    @DeleteMapping("/{id}")
    fun deleteRawMaterial(@PathVariable id: Long): ResponseEntity<Void> {
        rawMaterialService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}