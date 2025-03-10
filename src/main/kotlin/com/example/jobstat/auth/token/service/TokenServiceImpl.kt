package com.example.jobstat.auth.token.service

import com.example.jobstat.core.error.AppException
import com.example.jobstat.core.error.ErrorCode
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
internal class TokenServiceImpl(
    private val stringRedisTemplate: StringRedisTemplate,
) : TokenService {
    companion object {
        private const val REFRESH_TOKEN_PREFIX = "refresh_token:"
    }

    override fun saveToken(
        refreshToken: String,
        userId: Long,
        expirationInSeconds: Long,
    ) {
        val userKey = "$REFRESH_TOKEN_PREFIX$userId"
        // 새로운 리프레시 토큰을 저장합니다 (기존 토큰이 있다면 자동으로 덮어씁니다)
        stringRedisTemplate.opsForValue().set(userKey, refreshToken, expirationInSeconds, TimeUnit.SECONDS)
    }

    override fun getUserIdFromToken(refreshToken: String): Long {
        val userIdKey =
            findUserKeyByRefreshToken(refreshToken)
                ?: throw AppException.fromErrorCode(ErrorCode.AUTHENTICATION_FAILURE, "유효하지 않은 리프레시 토큰입니다.")

        return extractUserIdFromKey(userIdKey)
    }

    private fun findUserKeyByRefreshToken(refreshToken: String): String? =
        stringRedisTemplate
            .keys("$REFRESH_TOKEN_PREFIX*")
            .find { key -> stringRedisTemplate.opsForValue().get(key) == refreshToken }

    private fun extractUserIdFromKey(userIdKey: String): Long = userIdKey.substringAfter(REFRESH_TOKEN_PREFIX).toLong()

    override fun removeToken(userId: Long) {
        val userKey = "$REFRESH_TOKEN_PREFIX$userId"
        stringRedisTemplate.delete(userKey)
    }

    override fun invalidateRefreshToken(refreshToken: String) {
        val userId = getUserIdFromToken(refreshToken)
        removeToken(userId)
    }
}
