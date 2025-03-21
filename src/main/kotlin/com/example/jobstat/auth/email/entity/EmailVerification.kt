package com.example.jobstat.auth.email.entity

import com.example.jobstat.core.base.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import kotlin.random.Random

interface ReadEmailVerification {
    val id: Long
    val email: String
    val code: String
    val expiresAt: LocalDateTime

    fun isValid(): Boolean
}

// 이메일 인증 엔티티
@Entity
@Table(name = "email_verifications")
class EmailVerification(
    email: String,
    code: String,
    expiresAt: LocalDateTime,
) : BaseEntity(),
    ReadEmailVerification {
    @Column(nullable = false)
    override val email: String = email

    @Column(nullable = false, length = VERIFICATION_CODE_LENGTH)
    override val code: String = code

    @Column(nullable = false)
    override val expiresAt: LocalDateTime = expiresAt

    // 인증 코드 유효성 검사
    override fun isValid() = expiresAt.isAfter(LocalDateTime.now())

    companion object {
        private const val VERIFICATION_CODE_LENGTH = 6
        private const val EXPIRATION_MINUTES = 30L
        private const val MIN_CODE_VALUE = 100000
        private const val MAX_CODE_VALUE = 999999

        fun create(email: String): EmailVerification {
            val code = generateVerificationCode()
            val expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)
            return EmailVerification(email, code, expiresAt)
        }

        private fun generateVerificationCode(): String = Random.nextInt(MIN_CODE_VALUE, MAX_CODE_VALUE).toString()
    }
}
