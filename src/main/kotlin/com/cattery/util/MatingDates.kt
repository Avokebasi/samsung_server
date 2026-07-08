package com.cattery.util

import java.time.LocalDate

object MatingDates {
    private const val GESTATION_DAYS = 65L

    fun birthDueDate(matingDate: LocalDate?): LocalDate? =
        matingDate?.plusDays(GESTATION_DAYS)
}
