package com.tomaszezula.make.client.jvm.model

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(val name: String, val modules: List<Module>, val json: Json) {
    @Serializable
    data class Module(val id: Id, val name: String) {
        @Serializable
        @JvmInline
        value class Id(val value: Int)
    }
    @Serializable
    @JvmInline
    value class Json(val value: String)
}
