package com.tomaszezula.make.client.js

import com.tomaszezula.make.client.js.model.Blueprint
import com.tomaszezula.make.client.js.model.Blueprint.Module
import com.tomaszezula.make.common.model.AuthToken
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.Promise

@JsExport
class MakeClient(private val token: AuthToken, private val httpClient: HttpClient) {

    companion object {
        private const val baseUrl = "http://localhost:8080"

        fun create(token: String): MakeClient =
            MakeClient(AuthToken(token), HttpClient(Js))

    }

    /**
     * Returns the scenario blueprint.
     *
     * @param scenarioId
     *
     */
    fun getBlueprint(scenarioId: Int): Promise<Blueprint> {
        return GlobalScope.promise {
            val res = httpClient.post("$baseUrl/blueprint/$scenarioId") {
                headers {
                    append(HttpHeaders.Authorization, token.value)
                }
                contentType(ContentType.Application.Json)
            }
            val api: com.tomaszezula.make.common.model.Blueprint = Json.decodeFromString(res.bodyAsText())
            Blueprint(
                api.name,
                api.modules.map { it.toModel() }.toTypedArray(),
                api.json
            )
        }
    }

    private fun com.tomaszezula.make.common.model.Blueprint.Module.toModel(): Module =
        Module(this.id, this.name)
}


