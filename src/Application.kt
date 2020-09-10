package com.josancamon19

import com.josancamon19.controllers.UserController
import com.josancamon19.db.DbSettings
import com.josancamon19.models.User
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.basic
import io.ktor.auth.jwt.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database
import java.util.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
val hashedUserTable = UserHashedTableAuth(
    getDigestFunction("SHA-256") { "ktor${it.length}" },
    table = mapOf(
        "test" to Base64.getDecoder().decode("GSjkHCHGAxTTbnkEDBbVYd+PUFRlcWiumc4+MWE9Rvw=") // sha256 for "test"
    )
)
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.users(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication) {
        basic("basicAuthExample") {
            realm = "ktor"
            validate { credentials ->
                // Basically, you decide what is a valid user, does not matter how
                println(credentials)
                println(UserIdPrincipal(credentials.name))
                if (credentials.password == "${credentials.name}123") UserIdPrincipal(credentials.name) else null
            }
        }
    }
    DbSettings.initDb
    routes()

}

fun Application.routes() {
    val usersController = UserController()
    routing {
        route("/users") {
            authenticate("basicAuthExample") {
                get {
                    call.respond(usersController.getAll())
                }
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