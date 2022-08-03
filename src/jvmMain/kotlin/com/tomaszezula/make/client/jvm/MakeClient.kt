package com.tomaszezula.make.client.jvm

import com.tomaszezula.make.client.jvm.model.Blueprint
import com.tomaszezula.make.client.jvm.model.Blueprint.Module
import com.tomaszezula.make.client.jvm.model.Blueprint.Module.Id
import com.tomaszezula.make.client.jvm.model.Folder
import com.tomaszezula.make.client.jvm.model.Scenario
import com.tomaszezula.make.client.jvm.model.Team
import com.tomaszezula.make.common.MakeApi
import com.tomaszezula.make.common.MakeApiImpl
import com.tomaszezula.make.common.config.MakeConfig
import com.tomaszezula.make.common.model.AuthToken
import com.tomaszezula.make.common.model.Scheduling
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json

class MakeClient(private val token: AuthToken, private val api: MakeApi) {

    companion object {

        fun eu(token: String): MakeClient = makeClient(token, MakeConfig.eu())
        fun us(token: String): MakeClient = makeClient(token, MakeConfig.us())

        private fun makeClient(token: String, config: MakeConfig): MakeClient =
            MakeClient(
                AuthToken(token),
                MakeApiImpl(
                    config,
                    HttpClient(CIO),
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            )
    }

    /**
     * Creates a new scenario.
     *
     * @param teamId
     * @param folderId
     * @param blueprintJson
     * @param scheduling
     *
     */
    suspend fun createScenario(
        teamId: Team.Id,
        folderId: Folder.Id,
        blueprintJson: Blueprint.Json,
        scheduling: Scheduling
    ): Result<Scenario> =
        api.createScenario(
            this.token,
            teamId.value,
            folderId.value,
            blueprintJson.value, scheduling).map { response ->
                Scenario(Scenario.Id(response.id))
        }

    /**
     * Returns the scenario blueprint.
     *
     * @param scenarioId
     *
     */
    suspend fun getBlueprint(scenarioId: Scenario.Id): Result<Blueprint> =
        api.getBlueprint(this.token, scenarioId.value).map { response ->
            Blueprint(
                response.name,
                response.modules.map { it.toModel() },
                Blueprint.Json(response.json)
            )
        }

    private fun com.tomaszezula.make.common.model.Blueprint.Module.toModel(): Module =
        Module(Id(this.id), this.name)
}