package com.teamsamst.binarystorage.servlet

import com.teamsamst.binarystorage.service.StorageService
import com.teamsamst.binarystorage.util.JS
import org.eclipse.jetty.http.HttpStatus
import org.glassfish.jersey.media.multipart.FormDataParam
import java.io.InputStream
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/storage")
class StorageServlet {
    private val storageService = StorageService.INSTANCE

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun storeFile(@FormDataParam("file") inputStream: InputStream, @FormDataParam("path") path: String,
                @FormDataParam("name") name: String): Response {
        storageService.storeFile(inputStream, path, name)
        return JS.message(HttpStatus.OK_200 ,"Uploaded: $path")
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getFile(@QueryParam("path") path: String, @QueryParam("name") name: String): Response {
        return Response.ok(storageService.getFile(path, name), MediaType.APPLICATION_OCTET_STREAM).build()
    }
}