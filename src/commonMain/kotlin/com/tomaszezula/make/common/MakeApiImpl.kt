package com.tomaszezula.make.common

import com.tomaszezula.make.common.config.MakeConfig
import com.tomaszezula.make.common.model.*
import com.tomaszezula.make.common.model.Blueprint.Module
import com.tomaszezula.make.common.model.exception.BadRequestException
import com.tomaszezula.make.common.model.exception.NotFoundException
import com.tomaszezula.make.common.model.exception.ServerErrorException
import com.tomaszezula.make.common.model.exception.UnexpectedServerResponseException
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class MakeApiImpl(
    private val config: MakeConfig,
    private val httpClient: HttpClient,
    private val json: Json
) : MakeApi {

    companion object {
        const val BlueprintKey = "blueprint"
        const val FlowKey = "flow"
        const val FolderIdKey = "folderId"
        const val IdKey = "id"
        const val ModuleKey = "module"
        const val NameKey = "name"
        const val ResponseKey = "response"
        const val SchedulingKey = "scheduling"
        const val ScenarioKey = "scenario"
        const val Separator = ""
        const val TeamIdKey = "teamId"
        const val UpdatedKey = "module"
    }

    private val createScenarioUrl = "${config.baseUrl}/scenarios?confirmed=true"

    override suspend fun createScenario(
        token: AuthToken,
        teamId: Int,
        folderId: Int,
        blueprintJson: String,
        scheduling: Scheduling
    ): Result<Scenario> {
        return scheduling.validate().map { validScheduling ->
            post(token, createScenarioUrl, buildJsonObject {
                put(BlueprintKey, blueprintJson.lineSequence().map { it.trim() }.joinToString(Separator))
                put(SchedulingKey, validScheduling.toJson())
                put(FolderIdKey, folderId)
                put(TeamIdKey, teamId)
            }) { response ->
                jsonObject(response, ScenarioKey)?.let { scenario ->
                    scenario[IdKey]?.jsonPrimitive?.content?.let { id ->
                        Scenario(id.toInt())
                    }
                }
            }
        }.getOrThrow()
    }


    override suspend fun getBlueprint(token: AuthToken, scenarioId: Int): Result<Blueprint> =
        getBlueprintJson(token, scenarioId) { blueprint ->
            blueprint[NameKey]?.jsonPrimitive?.content?.let { name ->
                Blueprint(
                    name,
                    blueprint[FlowKey]?.jsonArray?.mapNotNull { it.toModule() } ?: emptyList(),
                    blueprint.toString()
                )
            }
        }

    override suspend fun setModuleData(
        token: AuthToken,
        scenarioId: Int,
        moduleId: Int,
        fieldName: String,
        data: Any
    ): Result<UpdateResult> =
        put(token, "${config.baseUrl}/scenarios/$scenarioId/data", buildJsonObject {
            put(moduleId.toString(), json.encodeToJsonElement(data))
        }) {
            UpdateResult(it[UpdatedKey]?.jsonPrimitive?.boolean ?: false)
        }

    private suspend fun <T> getBlueprintJson(token: AuthToken, scenarioId: Int, f: (JsonObject) -> T?): Result<T> =
        get(token, "${config.baseUrl}/scenarios/$scenarioId/blueprint") { response ->
            jsonObject(response, ResponseKey, BlueprintKey)?.let { f(it) }
        }

    private fun JsonElement.toModule(): Module? =
        this.jsonObject[IdKey]?.jsonPrimitive?.intOrNull?.let { moduleId ->
            this.jsonObject[ModuleKey]?.jsonPrimitive?.content?.let { name ->
                Module(moduleId, name)
            }
        }

    private fun jsonObject(jsonObject: JsonObject?, vararg path: String): JsonObject? {
        return if (path.isEmpty()) jsonObject
        else jsonObject(
            jsonObject?.let { it[path.first()]?.jsonObject },
            *path.drop(1).toTypedArray()
        )
    }

    private suspend fun <T> get(token: AuthToken, url: String, f: (JsonObject) -> T?): Result<T> =
        httpClient.get(url) {
            setHeaders(token)
        }.toResult(f)

    private suspend fun <T> post(token: AuthToken, url: String, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.post(url) {
            setHeaders(token)
            setBody(body.toString())
        }.toResult(f)

    private suspend fun <T> put(token: AuthToken, url: String, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.put(url) {
            setHeaders(token)
            setBody(body.toString())
        }.toResult(f)

    private fun HttpRequestBuilder.setHeaders(token: AuthToken) {
        headers {
            append(HttpHeaders.Authorization, "Token ${token.value}")
        }
        contentType(ContentType.Application.Json)
    }

    private suspend fun <T> HttpResponse.toResult(f: (JsonObject) -> T?): Result<T> =
        when (this.status) {
            HttpStatusCode.OK -> {
                f(json.parseToJsonElement(bodyAsText()).jsonObject)?.let {
                    Result.success(it)
                } ?: Result.failure(UnexpectedServerResponseException(HttpStatusCode.ExpectationFailed))
            }
            HttpStatusCode.BadRequest -> Result.failure(BadRequestException(this.bodyAsText()))
            HttpStatusCode.NotFound -> Result.failure(NotFoundException())
            HttpStatusCode.InternalServerError -> Result.failure(ServerErrorException(this.bodyAsText()))
            else -> Result.failure(UnexpectedServerResponseException(this.status))
        }
}