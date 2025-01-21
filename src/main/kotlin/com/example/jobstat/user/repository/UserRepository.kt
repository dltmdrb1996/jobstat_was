package com.example.jobstat.user.repository

import com.example.jobstat.user.entity.User

internal interface UserRepository {
    // 기본 CRUD 메소드 (JpaRepository에서 제공)
    fun save(user: User): User // 생성 및 업데이트

    fun findById(id: Long): User // 단일 엔티티 조회

    fun findByUsername(username: String): User

    fun findByEmail(email: String): User

    fun findAll(): List<User> // 모든 엔티티 조회

    fun deleteById(id: Long) // ID로 삭제

    fun delete(user: User) // 엔티티로 삭제

    fun existsById(id: Long): Boolean // ID 존재 여부 확인

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
