package com.cattery.plugins

import com.cattery.database.dao.CatFemaleDao
import com.cattery.database.dao.CatMaleDao
import com.cattery.database.dao.KittenDao
import com.cattery.database.dao.LitterDao
import com.cattery.database.dao.ReservationDao
import com.cattery.database.dao.UserDao
import com.cattery.routes.authRoutes
import com.cattery.routes.catFemaleRoutes
import com.cattery.routes.catMaleRoutes
import com.cattery.routes.kittenRoutes
import com.cattery.routes.litterRoutes
import com.cattery.routes.reservationRoutes
import com.cattery.security.JwtService
import com.cattery.services.AuthService
import com.cattery.services.CatFemaleService
import com.cattery.services.CatMaleService
import com.cattery.services.KittenService
import com.cattery.services.LitterService
import com.cattery.services.ReservationService
import com.cattery.services.UserService
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(jwtService: JwtService) {
    val userDao = UserDao()
    val catFemaleDao = CatFemaleDao()
    val catMaleDao = CatMaleDao()
    val litterDao = LitterDao()
    val kittenDao = KittenDao()
    val reservationDao = ReservationDao()

    val authService = AuthService(userDao, jwtService)
    val userService = UserService(userDao)
    val catFemaleService = CatFemaleService(catFemaleDao)
    val catMaleService = CatMaleService(catMaleDao)
    val litterService = LitterService(litterDao, catFemaleDao, catMaleDao)
    val kittenService = KittenService(kittenDao, litterDao, catFemaleDao, catMaleDao)
    val reservationService = ReservationService(reservationDao, kittenDao)

    routing {
        get("/") {
            call.respondText("Cattery API")
        }
        authRoutes(authService, userService)
        catFemaleRoutes(catFemaleService, litterService)
        catMaleRoutes(catMaleService, litterService)
        litterRoutes(litterService, kittenService)
        kittenRoutes(kittenService, reservationService)
        reservationRoutes(reservationService)
    }
}
