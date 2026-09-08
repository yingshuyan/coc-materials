package com.lucyyan.cocmaterials

import com.lucyyan.cocmaterials.dto.CreateRawMaterialRequest
import com.lucyyan.cocmaterials.entity.StorageArea
import com.lucyyan.cocmaterials.entity.StorageLocation
import com.lucyyan.cocmaterials.entity.Supplier
import com.lucyyan.cocmaterials.entity.User
import com.lucyyan.cocmaterials.entity.UserGroup
import com.lucyyan.cocmaterials.repository.StorageAreaRepository
import com.lucyyan.cocmaterials.repository.StorageLocationRepository
import com.lucyyan.cocmaterials.repository.SupplierRepository
import com.lucyyan.cocmaterials.repository.UserGroupRepository
import com.lucyyan.cocmaterials.repository.UserRepository
import com.lucyyan.cocmaterials.service.RawMaterialService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.Instant

@Testcontainers
@SpringBootTest
@Transactional
class RawMaterialIntegrationTest {

    companion object {

        @Container
        val postgres = PostgreSQLContainer("postgres:18")
            .withDatabaseName("coc_materials_test")
            .withUsername("test")
            .withPassword("test")

        @JvmStatic
        @DynamicPropertySource
        fun configureDatabase(
            registry: DynamicPropertyRegistry
        ) {
            registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
            )

            registry.add(
                "spring.datasource.username",
                postgres::getUsername
            )

            registry.add(
                "spring.datasource.password",
                postgres::getPassword
            )
        }
    }

    @Autowired
    lateinit var rawMaterialService: RawMaterialService

    @Autowired
    lateinit var userGroupRepository: UserGroupRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var supplierRepository: SupplierRepository

    @Autowired
    lateinit var storageAreaRepository: StorageAreaRepository

    @Autowired
    lateinit var storageLocationRepository: StorageLocationRepository

    @Test
    fun contextLoads() {
    }

    @Test
    fun `creates raw material`() {
        val group = userGroupRepository.save(
            UserGroup(
                name = "Admin"
            )
        )

        val owner = userRepository.save(
            User(
                name = "Test User",
                email = "test@example.com",
                group = group
            )
        )

        val supplier = supplierRepository.save(
            Supplier(
                name = "Test Supplier"
            )
        )

        val storageArea = storageAreaRepository.save(
            StorageArea(
                name = "Warehouse A"
            )
        )

        val storageLocation = storageLocationRepository.save(
            StorageLocation(
                name = "Shelf A1",
                storageArea = storageArea
            )
        )

        val request = CreateRawMaterialRequest(
            productCode = "RM-TEST-001",
            corningPartNumber = "CP-TEST-001",
            lotNumber = "LOT-TEST-001",
            materialType = "Glass",
            materialClass = "A",
            form = "Sheet",
            category = "Production",
            quantity = BigDecimal("12.500"),
            uom = "KG",
            notes = "Integration test",
            supplierId = requireNotNull(supplier.id),
            ownerId = requireNotNull(owner.id),
            storageLocationId = requireNotNull(storageLocation.id),
            dateIn = Instant.parse("2026-08-23T16:00:00Z"),
            dateOut = null
        )

        val result = rawMaterialService.createRawMaterial(request)

        assertNotNull(result.id)
        assertEquals("RM-TEST-001", result.productCode)
        assertEquals("LOT-TEST-001", result.lotNumber)
        assertEquals(BigDecimal("12.500"), result.quantity)
        assertEquals("Test Supplier", result.supplierName)
        assertEquals("Test User", result.ownerName)
        assertEquals("Shelf A1", result.storageLocationName)
        assertEquals(0L, result.version)
    }
}