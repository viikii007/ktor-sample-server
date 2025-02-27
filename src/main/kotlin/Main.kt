package org.example
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.multiApp.QuestionDAO
import org.example.multiApp.addQuestion
import org.example.multiApp.getCategories
import org.example.multiApp.questionRoutes
import org.example.wallapaperApp.DatabaseFactory

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {

    val questionRoute="/questions/{appId}/{categoryId}"
    val getCategoryRoute="/categories/{app_id}"
    val addQuestionRoute="/add-question"

    DatabaseFactory.init()
    val questionDAO = QuestionDAO()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        questionRoutes(questionDAO = questionDAO, route = questionRoute)
    }
    routing {
        getCategories(route = getCategoryRoute)
    }
    routing {
        addQuestion(route = addQuestionRoute)
    }
}


//fun main() {
//    DatabaseFactory.init()
//    embeddedServer(Netty, port = 8080) {
//        install(ContentNegotiation) {
//            json(Json { prettyPrint = true })
//        }
////        apiKeyAuthentication() // for api key check
//        getImages() // for get all images
//        home()
//    }.start(wait = true)
//}
//
//fun Application.getImages(){
//    routing {
//        get("/getimage") {
//            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
//            val category = call.request.queryParameters["category"] ?: ""
//
//            if (page < 1) {
//                call.respond(ImageResponse(status = 0, message = "No Images Found", page = 0, images = null))
//                return@get
//            }
//
//            val images = DatabaseFactory.getImagesByCategory(category, page, 10)
//
//            if (images.isEmpty()) {
//                call.respond(ImageResponse(status = 0, message = "No Images Found", page = 0, images = null))
//                return@get
//            }
//
//            call.respond(ImageResponse(page = page, images = images, status = 1, message = "success"))
//        }
//    }
//}


@Serializable
data class ImageResponse(
    val status:Int,
    val message:String,
    val page: Int=0,
    val images: List<ImageData>?=null
)

@Serializable
data class ImageData(
    val url: String,
    val category: String
)


fun Application.apiKeyAuthentication() {
    intercept(Plugins) {
        val apiKey = call.request.headers["X-API-Key"]
        if (apiKey == null || apiKey != "123456") {
            call.respond(ImageResponse(status = 0, message = "Unauthorized"))
            finish()
        }
    }
}


fun Application.home(){
    routing {
        get("/home"){
            call.respond("Home screen")
        }
    }
}



