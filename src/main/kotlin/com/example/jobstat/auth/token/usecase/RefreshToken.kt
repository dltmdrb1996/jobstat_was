package com.example.jobstat.auth.token.usecase

import com.example.jobstat.auth.token.service.TokenService
import com.example.jobstat.auth.user.service.UserService
import com.example.jobstat.core.security.AccessPayload
import com.example.jobstat.core.security.JwtTokenGenerator
import com.example.jobstat.core.security.RefreshPayload
import com.example.jobstat.core.usecase.impl.ValidUseCase
import jakarta.transaction.Transactional
import jakarta.validation.Validator
import jakarta.validation.constraints.NotBlank
import org.springframework.stereotype.Service

@Service
internal class RefreshToken(
    private val userService: UserService,
    private val tokenService: TokenService,
    private val jwtTokenGenerator: JwtTokenGenerator,
    validator: Validator,
) : ValidUseCase<RefreshToken.Request, RefreshToken.Response>(validator) {
    @Transactional
    override fun execute(request: Request): Response {
        val id = tokenService.getUserIdFromToken(request.refreshToken)
        val user = userService.getUserWithRoles(id)
        val refreshToken = jwtTokenGenerator.createRefreshToken(RefreshPayload(user.id, user.getRolesString()))
        val accessToken = jwtTokenGenerator.createAccessToken(AccessPayload(user.id, user.getRolesString()))
        tokenService.saveToken(refreshToken, id, jwtTokenGenerator.getRefreshTokenExpiration())
        return Response(accessToken, refreshToken)
    }

    data class Request(
        @field:NotBlank(message = "리프레시 토큰은 필수 값입니다")
        val refreshToken: String,
    )

    data class Response(
        val accessToken: String,
        val refreshToken: String,
    )
}
