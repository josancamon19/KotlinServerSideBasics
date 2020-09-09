package com.josancamon19

import com.josancamon19.controllers.UserController
import com.josancamon19.db.DbSettings
import com.josancamon19.models.User
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import org.jetbrains.exposed.sql.Database


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.users(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    DbSettings.initDb
    routes()

}

fun Application.routes() {
    val usersController = UserController()
    routing {
        route("/users") {
            get {
                call.respond(usersController.getAll())
            }
            get("/{id}") {
                when (val userId = call.parameters["id"]?.toIntOrNull()) {
                    null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        when (val user = usersController.getUserById(userId)) {
                            null -> call.respond(HttpStatusCode.NotFound, message = "User not found")
                            else -> call.respond(user)
                        }
                    }
                }
            }
            post {
                usersController.createUser(call.receive())
                call.respond(HttpStatusCode.Created)
            }
            put("/{id}") {
                val newUser = call.receive<User>()
                when (val userId = call.parameters["id"]?.toIntOrNull()) {
                    null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        if (usersController.updateUser(userId, newUser) == 0) {
                            call.respond(HttpStatusCode.NotFound)
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }

            delete("/{id}") {
                when (val userId = call.parameters["id"]?.toIntOrNull()) {
                    null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        if (usersController.deleteUser(userId) == 0) {
                            call.respond(HttpStatusCode.NotFound)
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}