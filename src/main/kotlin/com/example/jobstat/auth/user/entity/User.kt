package com.example.jobstat.auth.user.entity

import com.example.jobstat.auth.user.UserConstants
import com.example.jobstat.core.base.Address
import com.example.jobstat.core.base.SoftDeleteBaseEntity
import jakarta.persistence.*
import java.time.LocalDate

interface ReadUser {
    val id: Long
    val username: String
    val email: String
    val birthDate: LocalDate
    val address: Address?
    val password: String
    val isActive: Boolean
    val roles: Set<ReadRole>

    fun getRolesString(): List<String> = roles.map { it.name }

    fun hasRole(roleName: String): Boolean

    fun isAdmin(): Boolean
}

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_email", columnList = "email"),
    ],
)
internal class User private constructor(
    username: String,
    email: String,
    password: String,
    birthDate: LocalDate,
) : SoftDeleteBaseEntity(),
    ReadUser {
    @Column(
        name = "username",
        nullable = false,
        unique = true,
        length = UserConstants.MAX_USERNAME_LENGTH,
    )
    override var username: String = username
        protected set

    @Column(
        name = "email",
        nullable = false,
        unique = true,
        length = UserConstants.MAX_EMAIL_LENGTH,
    )
    override var email: String = email
        protected set

    @Column(
        name = "password",
        nullable = false,
        length = UserConstants.MAX_PASSWORD_LENGTH,
    )
    override var password: String = password
        protected set

    @Column(name = "birth_date", nullable = false)
    override var birthDate: LocalDate = birthDate
        protected set

    @Embedded
    override var address: Address? = null
        protected set

    @Column(name = "is_active", nullable = false)
    override var isActive: Boolean = true
        protected set

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    private val userRoles: MutableSet<UserRole> = mutableSetOf()

    override val roles: Set<Role>
        get() = userRoles.map { it.role }.toSet()

    override fun hasRole(roleName: String): Boolean = roles.any { it.name.equals(roleName, ignoreCase = true) }

    override fun isAdmin(): Boolean = hasRole("ADMIN")

    fun hasRole(role: Role): Boolean = userRoles.any { it.role.id == role.id }

    fun getUserRole(role: Role): UserRole? = userRoles.find { it.role.id == role.id }

    fun assignRole(userRole: UserRole) {
        require(userRole.user == this) { UserConstants.ErrorMessages.INVALID_ROLE }
        if (hasRole(userRole.role)) return
        userRoles.add(userRole)
    }

    fun revokeRole(
        role: Role,
        removeFromRole: Boolean = true,
    ) {
        val userRole = getUserRole(role)
        userRole?.let {
            userRoles.remove(it)
            if (removeFromRole) {
                role.revokeRole(this, false)
            }
        }
    }

    fun clearRoles() {
        val currentRoles = roles.toSet() // 복사본 생성
        currentRoles.forEach { role ->
            revokeRole(role)
        }
    }

    fun updatePassword(newPassword: String) {
        require(newPassword.isNotBlank()) { UserConstants.ErrorMessages.PASSWORD_REQUIRED }
        this.password = newPassword
    }

    fun updateEmail(newEmail: String) {
        require(newEmail.matches(Regex(UserConstants.Patterns.EMAIL_PATTERN))) {
            UserConstants.ErrorMessages.INVALID_EMAIL
        }
        this.email = newEmail
    }

    fun updateAddress(newAddress: Address?) {
        this.address = newAddress
    }

    fun enableAccount() {
        isActive = true
    }

    fun disableAccount() {
        isActive = false
    }

    override fun restore() {
        super.restore()
        enableAccount()
    }

    companion object {
        fun create(
            username: String,
            email: String,
            password: String,
            birthDate: LocalDate,
        ): User {
            require(username.matches(Regex(UserConstants.Patterns.USERNAME_PATTERN))) {
                UserConstants.ErrorMessages.INVALID_USERNAME
            }
            require(email.matches(Regex(UserConstants.Patterns.EMAIL_PATTERN))) {
                UserConstants.ErrorMessages.INVALID_EMAIL
            }
            require(birthDate.isBefore(LocalDate.now().minusDays(1))) {
                UserConstants.ErrorMessages.INVALID_BIRTH_DATE
            }
            require(password.isNotBlank()) { UserConstants.ErrorMessages.PASSWORD_REQUIRED }

            return User(username, email, password, birthDate)
        }
    }
}
