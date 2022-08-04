package com.tomaszezula.make.client.jvm

import com.tomaszezula.make.common.MakeApiImpl
import com.tomaszezula.make.common.config.MakeConfig
import com.tomaszezula.make.common.model.AuthToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json

val EmptyBlueprint =
    """
        {
            "name": "New scenario",
            "flow": [
                {
                    "id": null,
                    "module": "placeholder:Placeholder",
                    "metadata": {
                        "designer": {
                            "x": 0,
                            "y": 0
                        }
                    }
                }
            ],
            "metadata": {
                "instant": false,
                "version": 1,
                "scenario": {
                    "roundtrips": 1,
                    "maxErrors": 3,
                    "autoCommit": true,
                    "autoCommitTriggerLast": true,
                    "sequential": false,
                    "confidential": false,
                    "dataloss": false,
                    "dlq": false
                },
                "designer": {
                    "orphans": []
                },
                "zone": "eu1.make.com"
            }
        }
    """.trimIndent()

val client = MakeClient(
    AuthToken("REPLACE WITH YOUR OWN TOKEN"),
    MakeApiImpl(
        MakeConfig.eu(),
        HttpClient(CIO),
        Json {
            ignoreUnknownKeys = true
        }
    )
)