package com.tomaszezula.make.client.jvm.examples

import com.tomaszezula.make.client.jvm.client
import com.tomaszezula.make.client.jvm.model.Scenario
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        client.getBlueprint(Scenario.Id(392961))    // Replace with your own scenario id
            .onSuccess { blueprint ->
                println(blueprint)
            }
    }
}