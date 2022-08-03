package com.tomaszezula.make.server.plugins

import com.tomaszezula.make.common.MakeApi
import com.tomaszezula.make.common.model.AuthToken
import com.tomaszezula.make.common.model.IndefiniteScheduling
import com.tomaszezula.make.common.model.Scheduling
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(api: MakeApi) {
    routing {
        post("/blueprint/{scenarioId}") {
            call.handle {
                api.getBlueprint(call.toAuthToken(), call.toScenarioId())
            }
        }
        post("/scenario") {
            call.handle {
                api.createScenario(
                    call.toAuthToken(),
                    call.toTeamId(),
                    call.toFolderId(),
                    call.toBlueprint(),
                    call.toScheduling()
                )
            }
        }
    }
}

private suspend fun ApplicationCall.handle(f: suspend () -> Result<Any>) {
    runSuspendCatching {
        this.respond(f().getOrThrow())
    }.onFailure {
        // TODO log the exception
        this.respond(
            HttpStatusCode.InternalServerError, "Failed to handle request."
        )
    }
}
private fun ApplicationCall.toScenarioId(): Int =
    this.parameters[RequestParams.ScenarioId]!!.toInt()

private fun ApplicationCall.toTeamId(): Int =
    this.parameters[RequestParams.TeamId]!!.toInt()

private fun ApplicationCall.toFolderId(): Int =
    this.parameters[RequestParams.FolderId]!!.toInt()

private fun ApplicationCall.toBlueprint(): String =
    this.parameters[RequestParams.Blueprint]!!

private fun ApplicationCall.toScheduling(): Scheduling =
    IndefiniteScheduling(
        this.parameters[RequestParams.SchedulingSeconds]!!.toInt()
    )

private fun ApplicationCall.toAuthToken(): AuthToken =
    AuthToken(this.request.header(HttpHeaders.Authorization)!!)

object RequestParams {
    const val ScenarioId = "scenarioId"
    const val TeamId = "teamId"
    const val FolderId = "folderId"
    const val Blueprint = "blueprint"
    const val SchedulingSeconds = "schedulingSeconds"
}