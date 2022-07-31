package com.tomaszezula.make.server

import com.tomaszezula.make.common.MakeApiImpl
import com.tomaszezula.make.common.config.MakeConfig
import com.tomaszezula.make.server.plugins.configureRouting
import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.serialization.json.Json

fun main() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    embeddedServer(Netty, port = config.port, host = config.host) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
        }
        configureRouting(
            MakeApiImpl(MakeConfig.eu(), HttpClient(CIO), Json {
                ignoreUnknownKeys = true
            })
        )
    }.start(wait = true)
}