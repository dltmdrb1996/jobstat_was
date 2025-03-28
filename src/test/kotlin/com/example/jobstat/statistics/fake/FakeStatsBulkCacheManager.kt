package com.example.jobstat.statistics.fake

import com.example.jobstat.core.base.mongo.stats.BaseStatsDocument
import com.example.jobstat.core.cache.StatsBulkCacheManager
import com.example.jobstat.core.state.BaseDate
import com.example.jobstat.statistics.stats.registry.StatsType

class FakeStatsBulkCacheManager : StatsBulkCacheManager {
    private val inMemoryCache = mutableMapOf<String, BaseStatsDocument>()

    override fun put(
        key: String,
        document: BaseStatsDocument,
    ) {
        inMemoryCache[key] = document
    }

    override fun putAll(documentsMap: Map<String, BaseStatsDocument>) {
        inMemoryCache.putAll(documentsMap)
    }

    override fun <T : BaseStatsDocument> get(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return inMemoryCache[key] as T?
    }

    override fun <T : BaseStatsDocument> getAll(keys: Collection<String>): Map<String, T> {
        @Suppress("UNCHECKED_CAST")
        return inMemoryCache.filterKeys { it in keys } as Map<String, T>
    }

    override fun createCacheKey(
        baseDate: BaseDate,
        statsType: StatsType,
        entityId: Long,
    ): String = "$baseDate:$statsType:$entityId"

    override fun invalidateAll() {
        inMemoryCache.clear()
    }

    override fun getCacheStats(): String = "테스트용 캐시 - ${inMemoryCache.size}개 항목"
}
