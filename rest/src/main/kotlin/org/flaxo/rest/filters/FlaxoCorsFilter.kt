package org.flaxo.rest.filters

import javax.servlet.http.HttpServletResponse
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.FilterChain
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.http.HttpServletRequest

class FlaxoCorsFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        response.apply {
            setHeader("Access-Control-Allow-Origin", "*")
            setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            setHeader("Access-Control-Max-Age", "3600")
            setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token")
            addHeader("Access-Control-Expose-Headers", "xsrf-token")
        }
        if ("OPTIONS" == request.method) {
            response.status = HttpServletResponse.SC_OK
        } else {
            filterChain.doFilter(request, response)
        }
    }
}