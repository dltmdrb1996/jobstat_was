package com.example.jobstat.statistics.rankings.repository

import com.example.jobstat.statistics.rankings.model.rankingtype.RankingType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RankingRepositoryType(
    val type: RankingType,
)
