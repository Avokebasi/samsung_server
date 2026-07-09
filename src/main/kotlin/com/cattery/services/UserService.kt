package com.cattery.services

import com.cattery.database.dao.UserDao
import com.cattery.models.User
import com.cattery.models.UserRole

class UserService(
    private val userDao: UserDao,
) {
    fun getById(userId: Int): User =
        userDao.findById(userId) ?: throw IllegalArgumentException("Пользователь не найден")

    fun updateAvatar(userId: Int, avatarUrl: String?): User {
        if (avatarUrl.isNullOrBlank()) {
            throw IllegalArgumentException("Аватар не задан")
        }
        if (!userDao.updateAvatar(userId, avatarUrl)) {
            throw IllegalArgumentException("Не удалось обновить аватар")
        }
        return getById(userId)
    }

    fun requireBreeder(userId: Int): User {
        val user = getById(userId)
        if (user.role != UserRole.BREEDER) {
            throw IllegalArgumentException("Доступ только для заводчика")
        }
        return user
    }
}
