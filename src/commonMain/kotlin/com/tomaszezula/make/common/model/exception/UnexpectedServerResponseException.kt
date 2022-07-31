package com.tomaszezula.make.common.model.exception

import io.ktor.http.*

class UnexpectedServerResponseException(responseCode: HttpStatusCode) :
    Exception("Unexpected response code: $responseCode")
