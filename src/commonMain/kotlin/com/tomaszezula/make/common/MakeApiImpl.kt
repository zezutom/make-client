package com.tomaszezula.make.common

import com.tomaszezula.make.common.config.MakeConfig
import com.tomaszezula.make.common.model.AuthToken
import com.tomaszezula.make.common.model.Blueprint
import com.tomaszezula.make.common.model.Blueprint.Module
import com.tomaszezula.make.common.model.Scenario
import com.tomaszezula.make.common.model.UpdateResult
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
        const val SchedulingKey = "scheduling"
        const val FolderIdKey = "folderId"
        const val TeamIdKey = "teamId"
        const val Separator = ""

        object SetModuleData {
            const val ContentField = "content"
        }
    }

    private val createScenarioUrl = "${config.baseUrl}/scenarios?confirmed=true"

    override suspend fun createScenario(
        token: AuthToken,
        teamId: Int,
        folderId: Int,
        blueprintJson: String,
        schedulingInterval: Int
    ): Result<Scenario> {
        TODO("Not yet implemented")
    }

    override suspend fun getBlueprint(token: AuthToken, scenarioId: Int): Result<Blueprint> =
        get(token, "${config.baseUrl}/scenarios/$scenarioId/blueprint") { response ->
            jsonObject(response, "response", "blueprint")?.let { blueprint ->
                blueprint["name"]?.jsonPrimitive?.content?.let { name ->
                    Blueprint(
                        name,
                        blueprint["flow"]?.jsonArray?.mapNotNull { it.toModule() } ?: emptyList(),
                        blueprint.toString()
                    )
                }
            }
        }

    override suspend fun setModuleData(
        token: AuthToken,
        scenarioId: Int,
        moduleId: Int,
        fieldName: String,
        data: Any
    ): Result<UpdateResult> {
        TODO("Not yet implemented")
    }

    private fun JsonElement.toModule(): Module? =
        this.jsonObject["id"]?.jsonPrimitive?.intOrNull?.let { moduleId ->
            this.jsonObject["module"]?.jsonPrimitive?.content?.let { name ->
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

    private suspend fun <T> patch(token: AuthToken, url: String, body: JsonObject, f: (JsonObject) -> T?): Result<T> =
        httpClient.patch(url) {
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