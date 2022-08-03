package com.tomaszezula.make.client.jvm.model

import kotlinx.serialization.Serializable

@Serializable
data class Team(val id: Id) {
    @Serializable
    @JvmInline
    value class Id(val value: Int)
}
