package com.cattery.services

import com.cattery.database.dao.UserDao
import com.cattery.requests.LoginRequest
import com.cattery.requests.RegisterRequest
import com.cattery.responses.AuthResponse
import com.cattery.security.JwtService
import com.cattery.security.PasswordHasher

class AuthService(
    private val userDao: UserDao,
    private val jwtService: JwtService,
) {
    fun login(request: LoginRequest): AuthResponse {
        val user = userDao.findByLogin(request.username.trim())
            ?: throw IllegalArgumentException("Неверный логин или пароль")
        if (!PasswordHasher.verify(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Неверный логин или пароль")
        }
        val token = jwtService.generateToken(user.id.toLong(), user.login, user.role.name)
        return AuthResponse(
            token = token,
            user = com.cattery.models.User(
                id = user.id.toLong(),
                username = user.login,
                name = user.name,
                avatarUrl = user.avatarUrl,
                role = user.role,
            ),
        )
    }

    fun register(request: RegisterRequest): AuthResponse {
        if (request.username.isBlank() || request.password.length < 4) {
            throw IllegalArgumentException("Некорректные данные регистрации")
        }
        if (userDao.findByLogin(request.username.trim()) != null) {
            throw IllegalArgumentException("Пользователь уже существует")
        }
        val created = userDao.create(
            login = request.username.trim(),
            passwordHash = PasswordHasher.hash(request.password),
            name = request.name.trim(),
            role = request.role,
        )
        val token = jwtService.generateToken(created.id, created.username, created.role.name)
        return AuthResponse(token = token, user = created)
    }
}
