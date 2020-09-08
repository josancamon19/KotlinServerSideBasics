package com.josancamon19

import com.josancamon19.models.User
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }
    routes()
}

val users = mutableListOf<User>()

fun Application.routes() {
    routing {
        route("/users") {
            get {
                call.respond(users)
            }
            get("/{id}") {
                when (val userId = call.parameters["id"]?.toIntOrNull()) {
                    null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        when (val user = users.firstOrNull { it.id == userId }) {
                            null -> call.respond(HttpStatusCode.NotFound, message = "User not found")
                            else -> call.respond(user)
                        }
                    }
                }
            }
            post {
                users.add(call.receive())
                call.respond(HttpStatusCode.Created)
            }
            put("/{id}") {
                val newUser = call.receive<User>()
                when (val userId = call.parameters["id"]?.toIntOrNull()) {
                    null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        when (val user = users.firstOrNull { it.id == userId }) {
                            null -> call.respond(HttpStatusCode.NotFound)
                            else -> {
                                users[users.indexOf(user)] = newUser
                                call.respond(HttpStatusCode.OK)
                            }
                        }
                    }
                }

            }

            delete("/{id}") {
                when (val userId = call.parameters["id"]?.toIntOrNull()) {
                    null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        when (val user = users.firstOrNull { it.id == userId }) {
                            null -> call.respond(HttpStatusCode.NotFound, message = "User not found")
                            else -> {
                                users.remove(user)
                                call.respond(HttpStatusCode.OK)
                            }
                        }
                    }
                }
            }
        }
    }
}