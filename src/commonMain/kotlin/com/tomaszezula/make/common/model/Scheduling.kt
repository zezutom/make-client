package com.tomaszezula.make.common.model

import com.tomaszezula.make.common.model.Scheduling.Companion.MinSchedulingInterval

sealed interface Scheduling {
    fun toJson(): String

    companion object {
        const val MinSchedulingInterval = 900
    }
}

data class IndefiniteScheduling(val interval: Int = MinSchedulingInterval) : Scheduling {
    override fun toJson(): String =
        """
            {"type":"indefinitely","interval":$interval}            
        """.trimIndent()
}
