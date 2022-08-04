package com.tomaszezula.make.client.jvm

import com.tomaszezula.make.client.jvm.model.Blueprint
import com.tomaszezula.make.client.jvm.model.Folder
import com.tomaszezula.make.client.jvm.model.Team
import com.tomaszezula.make.common.MakeApiImpl
import com.tomaszezula.make.common.config.MakeConfig
import com.tomaszezula.make.common.model.AuthToken
import com.tomaszezula.make.common.model.IndefiniteScheduling
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main() {
    val client = MakeClient(
        AuthToken("REPLACE_WITH_YOUR_AUTH_TOKEN"),
        MakeApiImpl(
            MakeConfig.eu(),
            HttpClient(CIO),
            Json {
                ignoreUnknownKeys = true
            }
        )
    )

    runBlocking {
        client.createScenario(
            Team.Id(1),     // Replace with your team ID.
            Folder.Id(2),   // Replace with your folder ID.
            Blueprint.Json(EmptyBlueprint),
            IndefiniteScheduling()
        ).map { scenario ->
            println(scenario)
        }.onFailure {
            it.printStackTrace()
        }
    }
}