package com.tomaszezula.make.client.jvm.examples

import com.tomaszezula.make.client.jvm.EmptyBlueprint
import com.tomaszezula.make.client.jvm.client
import com.tomaszezula.make.client.jvm.model.Blueprint
import com.tomaszezula.make.client.jvm.model.Folder
import com.tomaszezula.make.client.jvm.model.Team
import com.tomaszezula.make.common.model.IndefiniteScheduling
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        // Create a new scenario
        client.createScenario(
            Team.Id(55228),     // Replace with your team id
            Folder.Id(22143),   // Replace with your folder id
            Blueprint.Json(EmptyBlueprint),
            IndefiniteScheduling()
        ).onSuccess { scenario ->
            println(scenario)
        }
    }
//    client.getBlueprint(Scenario.Id(392961))
}