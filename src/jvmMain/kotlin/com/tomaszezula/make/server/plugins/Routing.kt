package com.tomaszezula.make.server.plugins

import com.tomaszezula.make.common.MakeApi
import com.tomaszezula.make.common.model.AuthToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(api: MakeApi) {
    routing {
        post("/blueprint/{scenarioId}") {
            runSuspendCatching {
                call.respond(
                    api.getBlueprint(call.toAuthToken(), call.toScenarioId()).getOrThrow()
                )
            }.onFailure {
                // TODO log the exception
                call.respond(
                    HttpStatusCode.InternalServerError, "Failed to handle request."
                )
            }
        }
    }
}

private fun ApplicationCall.toScenarioId(): Int =
    this.parameters[RequestParams.ScenarioId]!!.toInt()

private fun ApplicationCall.toAuthToken(): AuthToken =
    AuthToken(this.request.header(HttpHeaders.Authorization)!!)

object RequestParams {
    const val ScenarioId = "scenarioId"
}