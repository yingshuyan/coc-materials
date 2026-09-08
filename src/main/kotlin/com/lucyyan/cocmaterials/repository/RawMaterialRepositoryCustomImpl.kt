package com.lucyyan.cocmaterials.repository

import com.lucyyan.cocmaterials.entity.RawMaterial
import com.lucyyan.cocmaterials.entity.StorageLocation
import com.lucyyan.cocmaterials.entity.Supplier
import com.lucyyan.cocmaterials.entity.User
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant

@Repository
class RawMaterialRepositoryCustomImpl(

    @PersistenceContext
    private val entityManager: EntityManager

) : RawMaterialRepositoryCustom {

    companion object {
        private val ALLOWED_SORT_PROPERTIES = setOf(
            "id",
            "productCode",
            "lotNumber",
            "quantity",
            "dateIn",
            "dateOut",
            "createdAt",
            "updatedAt"
        )
    }

    override fun findAllWithRelations(
        specification: Specification<RawMaterial>,
        pageable: Pageable
    ): Page<RawMaterial> {

        val criteriaBuilder = entityManager.criteriaBuilder

        // Main query
        val query = criteriaBuilder.createQuery(RawMaterial::class.java)
        val root = query.from(RawMaterial::class.java)

        root.fetch<Any, Any>("supplier", JoinType.LEFT)
        root.fetch<Any, Any>("owner", JoinType.LEFT)
        root.fetch<Any, Any>("storageLocation", JoinType.LEFT)

        query.distinct(true)

        specification.toPredicate(
            root,
            query,
            criteriaBuilder
        )?.let {
            query.where(it)
        }

        if (pageable.sort.isSorted) {
            val orders = pageable.sort.map { sortOrder ->

                val path = resolveSortPath(
                    root,
                    sortOrder.property
                )

                if (sortOrder.isAscending) {
                    criteriaBuilder.asc(path)
                } else {
                    criteriaBuilder.desc(path)
                }
            }.toMutableList()

            val idAlreadySorted = pageable.sort.any {
                it.property == "id"
            }

            if (!idAlreadySorted) {
                orders.add(
                    criteriaBuilder.desc(root.get<Long>("id"))
                )
            }

            query.orderBy(orders)
        }

        val typedQuery = entityManager.createQuery(query)

        typedQuery.firstResult = pageable.offset.toInt()
        typedQuery.maxResults = pageable.pageSize

        val content = typedQuery.resultList

        // Separate count query
        val countQuery = criteriaBuilder.createQuery(Long::class.java)
        val countRoot = countQuery.from(RawMaterial::class.java)

        countQuery.select(
            criteriaBuilder.countDistinct(countRoot)
        )

        specification.toPredicate(
            countRoot,
            countQuery,
            criteriaBuilder
        )?.let {
            countQuery.where(it)
        }

        val total = entityManager
            .createQuery(countQuery)
            .singleResult

        return PageImpl(
            content,
            pageable,
            total
        )
    }

    private fun resolveSortPath(
        root: Root<RawMaterial>,
        property: String
    ): Expression<*> {
        return when (property) {
            "id" -> root.get<Long>("id")
            "productCode" -> root.get<String>("productCode")
            "lotNumber" -> root.get<String>("lotNumber")
            "quantity" -> root.get<BigDecimal>("quantity")
            "dateIn" -> root.get<Instant>("dateIn")
            "dateOut" -> root.get<Instant>("dateOut")
            "createdAt" -> root.get<Instant>("createdAt")
            "updatedAt" -> root.get<Instant>("updatedAt")

            "supplierName" ->
                root.get<Supplier>("supplier")
                    .get<String>("name")

            "ownerName" ->
                root.get<User>("owner")
                    .get<String>("name")

            "storageLocationName" ->
                root.get<StorageLocation>("storageLocation")
                    .get<String>("name")

            else -> throw IllegalArgumentException(
                "Unsupported sort property: $property"
            )
        }
    }
}