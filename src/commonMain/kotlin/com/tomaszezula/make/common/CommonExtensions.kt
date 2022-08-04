package com.tomaszezula.make.common

import com.tomaszezula.make.common.model.IndefiniteScheduling
import com.tomaszezula.make.common.model.Scheduling
import com.tomaszezula.make.common.model.Scheduling.Companion.MinSchedulingInterval

fun Scheduling.validate(): Result<Scheduling> = when (this) {
    is IndefiniteScheduling -> {
        if (this.interval >= MinSchedulingInterval) Result.success(this)
        else Result.failure(IllegalStateException("The scheduling interval must be at least ${MinSchedulingInterval}s!"))
    }
}