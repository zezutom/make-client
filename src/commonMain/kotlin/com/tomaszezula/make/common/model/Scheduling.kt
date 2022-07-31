package com.tomaszezula.make.common.model

sealed interface Scheduling {
    fun toJson(): String
}

data class IndefiniteScheduling(private val interval: Int) : Scheduling {
    override fun toJson(): String =
        """
            {"type":"indefinitely","interval":$interval}            
        """.trimIndent()
}
