package org.example.wallapaperApp

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun numberToWord(num: Int): String {
    val numberMap = mapOf(
        1 to "one",
        2 to "two",
        3 to "three",
        4 to "four",
        5 to "five"
        // Add more numbers as needed
    )
    return numberMap[num] ?: "unknown"
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    getNumberRoute()

    routing {
        // GET endpoint
        get("/greet") {
            call.respond(GreetResponse(message = "Hello, welcome to Ktor!", responceCode = 200))
        }

        // POST endpoint
        post("/echo") {
            val request = call.receive<EchoRequest>()
            call.respond(EchoResponse(message = "You said: ${request.message}"))
        }
    }
}


fun Application.getNumberRoute(){
    routing {
        get("/number/{num}") {
            val numStr = call.parameters["num"]
            val num = numStr?.toIntOrNull()

            if (num == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid number")
                return@get
            }

            val word = numberToWord(num)
            call.respond(HttpStatusCode.OK, word)
        }
    }

}

// Data classes for JSON response
@Serializable
data class GreetResponse(val message: String,val responceCode:Int)

@Serializable
data class EchoRequest(val message: String)

@Serializable
data class EchoResponse(val message: String)

@Serializable
data class NumberRequest(val number: Int)