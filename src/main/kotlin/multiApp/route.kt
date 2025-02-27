package org.example.multiApp


import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import org.example.multiApp.entitys.QuestionsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


@Serializable
data class CategoryResponse(val category_id: Int, val category_name: String)

fun Route.getCategories(route:String) {
    route(route) {
        get{
            val appId = call.parameters["app_id"]?.toIntOrNull()

            if (appId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid App ID")
                return@get
            }

            try {
                // Fetch categories for the given app_id
                val categories = transaction {
                    QuestionsTable
                        .slice(QuestionsTable.categoryId, QuestionsTable.categoryName)
                        .select { QuestionsTable.appId eq appId }
                        .distinct()
                        .map {
                            CategoryResponse(
                                category_id = it[QuestionsTable.categoryId],
                                category_name = it[QuestionsTable.categoryName]
                            )
                        }
                }

                call.respond(HttpStatusCode.OK, categories)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error fetching categories: ${e.localizedMessage}")
            }
        }
    }
}

fun Route.questionRoutes(questionDAO: QuestionDAO,route:String) {
    route(route) {
        get {
            val appId = call.parameters["appId"]?.toIntOrNull()
            val categoryId = call.parameters["categoryId"]?.toIntOrNull()
            if (appId != null && categoryId != null) {
                val questions = questionDAO.getQuestionsByAppAndCategory(appId, categoryId)
                call.respond(questions)
            } else {
                call.respond(mapOf("error" to "Invalid appId or categoryId"))
            }
        }
    }
}

@Serializable
data class AddQuestionRequest(
    val app_id: Int,
    val category_name: String,
    val question: String,
    val answer: String
)

fun Route.addQuestion(route: String) {
    post(route) {
        val request = call.receive<AddQuestionRequest>()

        try {
            transaction {
                // Check if the category already exists
                val existingCategory = QuestionsTable
                    .select { QuestionsTable.categoryName eq request.category_name }
                    .map { it[QuestionsTable.categoryId] }
                    .firstOrNull()

                // If the category exists, use its ID, else assign a new one
                val categoryId = existingCategory ?: (
                        QuestionsTable.slice(QuestionsTable.categoryId)
                            .selectAll()
                            .maxOfOrNull { it[QuestionsTable.categoryId] }?.plus(1) ?: 100
                        )

                // Insert the question into the database
                QuestionsTable.insert {
                    it[appId] = request.app_id
                    it[QuestionsTable.categoryId] = categoryId
                    it[categoryName] = request.category_name
                    it[question] = request.question
                    it[answer] = request.answer
                }
            }
            call.respond(HttpStatusCode.OK, "Question added successfully!")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error inserting data: ${e.localizedMessage}")
        }
    }
}
