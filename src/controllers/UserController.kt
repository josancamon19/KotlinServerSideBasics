package com.josancamon19.controllers

import com.josancamon19.db.DbSettings.dbQuery
import com.josancamon19.models.User
import com.josancamon19.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserController {
    suspend fun getAll(): List<User> = dbQuery {
        Users.selectAll().map { toUser(it) }
    }

    suspend fun getUserById(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }.mapNotNull { toUser(it) }.singleOrNull()
    }

    suspend fun createUser(user: User) = dbQuery {
        Users.insert {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[age] = user.age
        }
    }

    suspend fun updateUser(id: Int, user: User) = dbQuery {
        Users.update({ Users.id eq id }) {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[age] = user.age
        }
    }

    suspend fun deleteUser(id: Int) = dbQuery {
        Users.deleteWhere { Users.id eq id }
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            age = row[Users.age]
        )
}