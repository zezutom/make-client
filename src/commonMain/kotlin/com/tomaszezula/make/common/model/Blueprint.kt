package com.tomaszezula.make.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(val name: String, val modules: List<Module>, val json: String) {
    @Serializable
    data class Module(val id: Int, val name: String)
}
