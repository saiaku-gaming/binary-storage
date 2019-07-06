package com.teamsamst.binarystorage.servlet

import com.teamsamst.binarystorage.util.JS
import org.eclipse.jetty.http.HttpStatus
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class RootServlet {
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun ping(): Response {
        return JS.message(HttpStatus.OK_200, "Pong")
    }
}