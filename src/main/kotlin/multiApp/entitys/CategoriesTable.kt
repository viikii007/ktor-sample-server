package org.example.multiApp.entitys


import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object CategoriesTable : Table("categories") {
    val id = integer("id").autoIncrement()
    val appId = integer("app_id")
    val categoryName = text("category_name")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Category(
    val id: Int,
    val appId: Int,
    val categoryName: String
)
