package com.cattery.services

import com.cattery.models.UserRole

object AccessScope {
    fun ownerFilter(userId: Int, role: UserRole): Int? =
        if (role == UserRole.BREEDER) userId else null
}
