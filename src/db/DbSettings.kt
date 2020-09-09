package com.josancamon19.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DbSettings {
    val initDb by lazy {
        val url = "jdbc:postgresql://ec2-54-172-173-58.compute-1.amazonaws.com:5432/d7dokb84n45r9e?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
        val user = "kyjxbkmfybtuzh"
        val password = "226085995a4d0383cf99a8b71d3284e6fdfb118696ea8c1b5f96b30acb30e2cc"
        Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)
        val flyway = Flyway.configure().dataSource(url, user, password).load()
        flyway.migrate()
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}