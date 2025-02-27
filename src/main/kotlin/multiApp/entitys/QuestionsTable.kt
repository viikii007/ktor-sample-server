package org.example.multiApp.entitys


import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object QuestionsTable : Table("questions") {
    val id = integer("id").autoIncrement()
    val appId = integer("app_id")
    val categoryId = integer("category_id")
    val categoryName = text("category_name")
    val question = text("question")
    val answer = text("answer")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Question(
    val id: Int,
    val appId: Int,
    val categoryId: Int,
    val categoryName: String,
    val question: String,
    val answer: String
)


