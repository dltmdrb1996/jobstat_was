package com.example.jobstat.core.converter

import com.example.jobstat.core.base.mongo.stats.RankingScore
import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.Document
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class RankingScoreWriteConverter(
    private val objectMapper: ObjectMapper,
) : Converter<RankingScore, Document> {
    override fun convert(source: RankingScore): Document = Document.parse(objectMapper.writeValueAsString(source))
}
