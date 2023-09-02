package lk.ijse.jsp.filter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter(urlPatterns = "/*")

public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String auth = req.getHeader("Auth");
        if (auth != null && auth.equals("user=user,pass=admin")){
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            res.addHeader("Content-Type", "application/json");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().println(Json.createObjectBuilder()
                    .add("Status", "Auth-Error")
                    .add("message", "You are not Authenticated to use this Service.!")
                    .add("data", "")
                    .build()
                    .toString());

        }


            }

    @Override
    public void destroy() {

    }
}
