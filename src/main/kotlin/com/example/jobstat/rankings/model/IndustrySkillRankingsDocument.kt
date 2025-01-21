package com.example.jobstat.rankings.model

import com.example.jobstat.core.base.mongo.SnapshotPeriod
import com.example.jobstat.core.base.mongo.ranking.RankingMetrics
import com.example.jobstat.core.base.mongo.ranking.RelationshipRankingDocument
import com.example.jobstat.core.base.mongo.ranking.VolatilityMetrics
import com.example.jobstat.core.state.EntityType
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "industry_skill_rankings")
class IndustrySkillRankingsDocument(
    id: String? = null,
    @Field("base_date")
    override val baseDate: String,
    @Field("period")
    override val period: SnapshotPeriod,
    @Field("metrics")
    override val metrics: IndustrySkillMetrics,
    @Field("primary_entity_type")
    override val primaryEntityType: EntityType = EntityType.INDUSTRY,
    @Field("related_entity_type")
    override val relatedEntityType: EntityType = EntityType.SKILL,
    @Field("rankings")
    override val rankings: List<IndustrySkillRankingEntry>,
) : RelationshipRankingDocument<IndustrySkillRankingsDocument.IndustrySkillRankingEntry>(
        id,
        baseDate,
        period,
        metrics,
        primaryEntityType,
        relatedEntityType,
        rankings,
    ) {
    data class IndustrySkillMetrics(
        @Field("total_count")
        override val totalCount: Int,
        @Field("ranked_count")
        override val rankedCount: Int,
        @Field("new_entries")
        override val newEntries: Int,
        @Field("dropped_entries")
        override val droppedEntries: Int,
        @Field("volatility_metrics")
        override val volatilityMetrics: VolatilityMetrics,
        @Field("industry_skill_correlation")
        val industrySkillCorrelation: IndustrySkillCorrelation,
    ) : RankingMetrics {
        data class IndustrySkillCorrelation(
            @Field("cross_industry_skills")
            val crossIndustrySkills: Map<Long, Map<Long, Double>>,
            @Field("skill_transition_patterns")
            val skillTransitionPatterns: List<SkillTransition>,
        ) {
            data class SkillTransition(
                val fromIndustryId: Long,
                val toIndustryId: Long,
                val commonSkillsCount: Int,
                val transitionScore: Double,
            )
        }
    }

    data class IndustrySkillRankingEntry(
       @Field("document_id")
        override val documentId: String,
        @Field("entity_id")
        override val entityId: Long,
        @Field("name")
        override val name: String,
        @Field("rank")
        override val rank: Int,
        @Field("previous_rank")
        override val previousRank: Int?,
        @Field("rank_change")
        override val rankChange: Int?,
        @Field("primary_entity_id")
        override val primaryEntityId: Long,
        @Field("primary_entity_name")
        override val primaryEntityName: String,
        @Field("related_rankings")
        override val relatedRankings: List<SkillRank>,
        @Field("total_postings")
        val totalPostings: Int,
        @Field("industry_penetration")
        val industryPenetration: Double,
    ) : RelationshipRankingEntry {
        data class SkillRank(
            @Field("entity_id")
            override val entityId: Long,
            @Field("name")
            override val name: String,
            @Field("rank")
            override val rank: Int,
            @Field("score")
            override val score: Double,
            @Field("demand_level")
            val demandLevel: Double,
            @Field("growth_rate")
            val growthRate: Double,
            @Field("industry_specificity")
            val industrySpecificity: Double,
        ) : RelatedEntityRank
    }

    override fun validate() {
        require(rankings.isNotEmpty()) { "Rankings must not be empty" }
        require(rankings.all { it.relatedRankings.isNotEmpty() }) { "All industries must have related skills" }
    }
}
