package com.tomaszezula.make.client.js.model

import kotlinx.serialization.Serializable

@JsExport
@Serializable
data class Blueprint(
    @JsName("name") val name: String,
    @JsName("modules") val modules: Array<Module>,
    @JsName("json") val json: String
) {
    @Serializable
    data class Module(val id: Int, val name: String)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Blueprint

        if (name != other.name) return false
        if (!modules.contentEquals(other.modules)) return false
        if (json != other.json) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + modules.contentHashCode()
        result = 31 * result + json.hashCode()
        return result
    }
}
