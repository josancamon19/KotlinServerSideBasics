package com.josancamon19.models

import io.ktor.auth.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val age: Int
):Principal

object Users : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val firstName: Column<String> = text("first_name")
    val lastName: Column<String> = text("last_name")
    val age: Column<Int> = integer("age")
}