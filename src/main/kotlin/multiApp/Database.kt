package org.example.multiApp

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init() {
        val dbFile = File("database.db").absolutePath
        Database.connect("jdbc:sqlite:$dbFile", driver = "org.sqlite.JDBC")

        transaction {
//            SchemaUtils.create(CategoriesTable, QuestionsTable)
        }
    }
}
