package com.teamsamst.binarystorage.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import javax.ws.rs.core.Response

object JS {
    private val mapper = ObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())

    fun message(status: Int, message: String, vararg args: Any): Response {
        return Response.status(status).entity(message(String.format(message, args))).build()
    }

    fun message(status: Int, a: Any): Response {
        return Response.status(status).entity(parse(if(a is String) message(a) else a)).build()
    }

    private fun message(message: String) = parse(ObjectWrapperForString(message))
    private fun parse(a: Any) = mapper.valueToTree<JsonNode>(a)

    private data class ObjectWrapperForString(
            val message: String
    )
}