package lk.ijse.jsp.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter(urlPatterns = "/*")

public class CrossFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       /* HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String method = req.getMethod();
        filterChain.doFilter(servletRequest,servletResponse);
        if (method.equals("OPTIONS")){

            res.setStatus(200);
            res.addHeader("Access-Control-Allow-Origin", "*");
            res.addHeader("Access-Control-Allow-Methods", "PUT, DELETE");
            res.addHeader("Access-Control-Allow-Headers", "content-type,auth");
        }else{
            res.addHeader("Access-Control-Allow-Origin", "*");
        }*/
        filterChain.doFilter(servletRequest,servletResponse);

        HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", "PUT, DELETE");
        res.addHeader("Access-Control-Allow-Headers", "content-type,auth");
        res.addHeader("Access-Control-Allow-Origin", "*");

    }

    @Override
    public void destroy() {

    }
}
