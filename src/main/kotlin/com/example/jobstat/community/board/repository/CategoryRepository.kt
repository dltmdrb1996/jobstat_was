package com.example.jobstat.community.board.repository

import com.example.jobstat.community.board.entity.BoardCategory

internal interface CategoryRepository {
    fun save(category: BoardCategory): BoardCategory

    fun findById(id: Long): BoardCategory

    fun findAll(): List<BoardCategory>

    fun findAllWithBoardCount(): List<Pair<BoardCategory, Long>>

    fun deleteById(id: Long)

    fun existsByName(name: String): Boolean

    fun findByName(name: String): BoardCategory
}
