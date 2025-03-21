package com.example.jobstat.auth.user.service

import com.example.jobstat.auth.user.UserConstants
import com.example.jobstat.auth.user.entity.ReadUser
import com.example.jobstat.auth.user.entity.RoleData
import com.example.jobstat.auth.user.entity.User
import com.example.jobstat.auth.user.entity.UserRole
import com.example.jobstat.auth.user.repository.RoleRepository
import com.example.jobstat.auth.user.repository.UserRepository
import com.example.jobstat.core.error.AppException
import com.example.jobstat.core.error.ErrorCode
import com.example.jobstat.core.extension.trueOrThrow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
) : UserService {
    private val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    override fun createUser(
        username: String,
        email: String,
        password: String,
        birthDate: LocalDate,
    ): User {
        val user = User.create(username, email, password, birthDate)

        validateEmail(user.email).trueOrThrow {
            AppException.fromErrorCode(
                ErrorCode.DUPLICATE_RESOURCE,
                UserConstants.ErrorMessages.DUPLICATE_EMAIL,
            )
        }
        validateUsername(user.username).trueOrThrow {
            AppException.fromErrorCode(
                ErrorCode.DUPLICATE_RESOURCE,
                UserConstants.ErrorMessages.DUPLICATE_USERNAME,
            )
        }

        val role = roleRepository.findById(RoleData.USER.id)
        UserRole.create(user, role)
        return userRepository.save(user)
    }

    override fun getUserById(id: Long): User = userRepository.findById(id)

    override fun getUserByUsername(username: String): User = userRepository.findByUsername(username)

    override fun getUserByEmail(email: String): User = userRepository.findByEmail(email)

    override fun getAllUsers(): List<User> = userRepository.findAll()

    override fun deleteUser(id: Long) = userRepository.deleteById(id)

    override fun validateUsername(username: String): Boolean {
        if (!username.matches(Regex(UserConstants.Patterns.USERNAME_PATTERN))) {
            throw AppException.fromErrorCode(
                ErrorCode.INVALID_ARGUMENT,
                UserConstants.ErrorMessages.INVALID_USERNAME,
            )
        }
        return !userRepository.existsByUsername(username)
    }

    override fun validateEmail(email: String): Boolean {
        if (!email.matches(Regex(UserConstants.Patterns.EMAIL_PATTERN))) {
            throw AppException.fromErrorCode(
                ErrorCode.INVALID_ARGUMENT,
                UserConstants.ErrorMessages.INVALID_EMAIL,
            )
        }
        return !userRepository.existsByEmail(email)
    }

    override fun isAccountEnabled(id: Long): Boolean = userRepository.findById(id).isActive

    override fun getUserWithRoles(id: Long): ReadUser {
        val user = userRepository.findByIdWithRoles(id)
        return user
    }

    override fun getUserRoles(id: Long): List<String> {
        val user = userRepository.findByIdWithRoles(id)
        return user.getRolesString()
    }

    override fun updateUser(command: Map<String, Any>): ReadUser {
        val userId = command["id"] as Long
        val user = userRepository.findById(userId)

        command.forEach { (key, value) ->
            when (key) {
                "password" -> user.updatePassword(value as String)
                "email" -> user.updateEmail(value as String)
                "isActive" -> if (value as Boolean) user.enableAccount() else user.disableAccount()
                // 추가 필드가 있다면 여기에 추가
            }
        }

        return userRepository.save(user)
    }

    override fun enableUser(id: Long) {
        updateUser(
            mapOf(
                "id" to id,
                "isActive" to true,
            ),
        )
    }

    override fun disableUser(id: Long) {
        updateUser(
            mapOf(
                "id" to id,
                "isActive" to false,
            ),
        )
    }

    override fun updateUserPassword(
        userId: Long,
        newPassword: String,
    ) {
        require(newPassword.matches(Regex(UserConstants.Patterns.PASSWORD_PATTERN))) {
            UserConstants.ErrorMessages.INVALID_PASSWORD
        }
        updateUser(
            mapOf(
                "id" to userId,
                "password" to newPassword,
            ),
        )
    }

    override fun updateUserEmail(
        userId: Long,
        newEmail: String,
    ) {
        updateUser(
            mapOf(
                "id" to userId,
                "email" to newEmail,
            ),
        )
    }
}
