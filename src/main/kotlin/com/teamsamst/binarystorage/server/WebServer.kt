package com.teamsamst.binarystorage.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import java.util.*
import javax.servlet.DispatcherType
import javax.servlet.Filter

object WebServer {
    private lateinit var context: ServletContextHandler

    fun init() {
        context = ServletContextHandler().apply {
            contextPath = "/"
        }
    }

    fun addJerseySpec(servletClass: Class<out Any>, path: String = "/*") {
        val servletHolder = ServletHolder(ServletContainer(ResourceConfig().apply {
            packages(servletClass.`package`.name)
            register(MultiPartFeature::class.java)
        })).apply {
            initOrder = 1
            setInitParameter("jersey.config.server.provider.packages", servletClass.`package`.name)
        }
        context.addServlet(servletHolder, path)
    }

    fun addFilter(filterClass: Class<out Filter>, vararg paths: String, dispatchTypes: EnumSet<DispatcherType>
            = EnumSet.allOf(DispatcherType::class.java)) {
        for(path in paths) {
            context.addFilter(filterClass, path, dispatchTypes)
        }
    }

    fun start(port: Int) {
        Server(port).apply {
            handler = context
            start()
            join()
        }
    }
}