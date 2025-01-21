package com.example.jobstat.core.base.mongo

import com.example.jobstat.core.state.DocumentStatus
import com.example.jobstat.core.state.EntityType
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Field

@CompoundIndexes(
    CompoundIndex(
        name = "reference_lookup_idx",
        def = "{'reference_id': 1, 'entity_type': 1}",
        unique = true,
    ),
    CompoundIndex(
        name = "status_lookup_idx",
        def = "{'entity_type': 1, 'status': 1}",
    ),
)
abstract class BaseReferenceDocument(
    id: String? = null,
    @Field("reference_id")
    val referenceId: Long,
    @Field("entity_type")
    val entityType: EntityType,
    @Field("status")
    val status: DocumentStatus = DocumentStatus.ACTIVE,
) : BaseDocument(id)
