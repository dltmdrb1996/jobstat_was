package com.example.jobstat.statistics.stats.repository

import com.example.jobstat.core.base.repository.StatsMongoRepository
import com.example.jobstat.core.base.repository.StatsMongoRepositoryImpl
import com.example.jobstat.statistics.stats.document.CompanyStatsDocument
import com.example.jobstat.statistics.stats.registry.StatsRepositoryType
import com.example.jobstat.statistics.stats.registry.StatsType
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository

@StatsRepositoryType(StatsType.COMPANY)
@NoRepositoryBean
interface CompanyStatsRepository : StatsMongoRepository<CompanyStatsDocument, String> {
    fun findByCompanySizeAndBaseDateBetween(
        companySize: String,
        startDate: String,
        endDate: String,
    ): List<CompanyStatsDocument>

    fun findByRemoteWorkRatioGreaterThan(ratio: Double): List<CompanyStatsDocument>

    fun findByGlobalHiringPercentageGreaterThan(percentage: Double): List<CompanyStatsDocument>

    fun findTopByBenefitsCoverageRate(limit: Int): List<CompanyStatsDocument>
}

@Repository
class CompanyStatsRepositoryImpl(
    private val entityInformation: MongoEntityInformation<CompanyStatsDocument, String>,
    private val mongoOperations: MongoOperations,
) : StatsMongoRepositoryImpl<CompanyStatsDocument, String>(entityInformation, mongoOperations),
    CompanyStatsRepository {
    override fun findByCompanySizeAndBaseDateBetween(
        companySize: String,
        startDate: String,
        endDate: String,
    ): List<CompanyStatsDocument> {
        val collection = mongoOperations.getCollection(entityInformation.collectionName)

        return collection
            .find(
                Filters.and(
                    Filters.eq("company_size", companySize),
                    Filters.gte("base_date", startDate),
                    Filters.lte("base_date", endDate),
                ),
            ).map { doc -> mongoOperations.converter.read(entityInformation.javaType, doc) }
            .toList()
    }

    override fun findByRemoteWorkRatioGreaterThan(ratio: Double): List<CompanyStatsDocument> {
        val collection = mongoOperations.getCollection(entityInformation.collectionName)

        return collection
            .find(
                Filters.gt("stats.remote_work_ratio", ratio),
            ).map { doc -> mongoOperations.converter.read(entityInformation.javaType, doc) }
            .toList()
    }

    override fun findByGlobalHiringPercentageGreaterThan(percentage: Double): List<CompanyStatsDocument> {
        val collection = mongoOperations.getCollection(entityInformation.collectionName)

        return collection
            .find(
                Filters.gt("stats.global_hiring_percentage", percentage),
            ).map { doc -> mongoOperations.converter.read(entityInformation.javaType, doc) }
            .toList()
    }

    override fun findTopByBenefitsCoverageRate(limit: Int): List<CompanyStatsDocument> {
        val collection = mongoOperations.getCollection(entityInformation.collectionName)

        return collection
            .find()
            .sort(Sorts.descending("stats.benefits_coverage_rate"))
            .limit(limit)
            .map { doc -> mongoOperations.converter.read(entityInformation.javaType, doc) }
            .toList()
    }
}
