package com.teamsamst.binarystorage.filter

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthFilter: Filter {
    override fun init(config: FilterConfig?) {}

    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse

        val token = request.getHeader("Authorization")
        if(token == null || token != System.getenv("STORAGE_SERVER_SECRET")) {
            response.status = 401
            return
        }

        chain?.doFilter(req, res)
    }

    override fun destroy() {}
}