package org.example.wallapaperApp

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.example.ImageData

object ImagesTable : Table("images") {
    val id = integer("id").autoIncrement()
    val category=varchar("category",255)
    val url = varchar("url", 255)
    override val primaryKey = PrimaryKey(id)
}

object DatabaseFactory {
    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:sqlite:images.db"
            driverClassName = "org.sqlite.JDBC"
            maximumPoolSize = 3
        }
        return HikariDataSource(config)
    }

    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(ImagesTable) // Creates table if not exists
        }
    }

    fun addImage(url: String,category:String) {
        transaction {
            ImagesTable.insert {
                it[ImagesTable.url] = url
                it[ImagesTable.category]=category
            }
        }
    }

    fun getImages(page: Int, pageSize: Int): List<String> {
        val offset = (page - 1) * pageSize
        return transaction {
            ImagesTable.selectAll()
                .limit(pageSize, offset.toLong())
                .map { it[ImagesTable.url] }
        }
    }

    fun getImagesByCategory(category: String, page: Int, pageSize: Int): List<ImageData> {
        val offset = (page - 1) * pageSize
        return transaction {
            ImagesTable.select {
                if (category.isNotEmpty()) {
                    ImagesTable.category eq category
                } else {
                    Op.TRUE // If no category is provided, return all
                }
            }
                .limit(pageSize, offset.toLong())
                .map { ImageData(it[ImagesTable.url], it[ImagesTable.category]) }
        }
    }
}
