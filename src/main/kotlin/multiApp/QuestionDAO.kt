package org.example.multiApp

import org.example.multiApp.entitys.Question
import org.example.multiApp.entitys.QuestionsTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class QuestionDAO {
    fun getCategoriesByAppId(appId: Int): List<Pair<Int, String>> {
        return transaction {
            QuestionsTable
                .slice(QuestionsTable.categoryId, QuestionsTable.categoryName)
                .select { QuestionsTable.appId eq appId }
                .distinct()
                .map {
                    Pair(it[QuestionsTable.categoryId], it[QuestionsTable.categoryName])
                }
        }
    }

    fun getQuestionsByAppAndCategory(appId: Int, categoryId: Int): List<Question> {
        return transaction {
            QuestionsTable.select {
                (QuestionsTable.appId eq appId) and (QuestionsTable.categoryId eq categoryId)
            }.map {
                Question(
                    id = it[QuestionsTable.id],
                    appId = it[QuestionsTable.appId],
                    categoryId = it[QuestionsTable.categoryId],
                    categoryName = it[QuestionsTable.categoryName],
                    question = it[QuestionsTable.question],
                    answer = it[QuestionsTable.answer]
                )
            }
        }
    }
}