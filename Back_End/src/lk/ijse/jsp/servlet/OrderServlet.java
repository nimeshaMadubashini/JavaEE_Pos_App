package lk.ijse.jsp.servlet;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/pages/purchase-order"})

public class OrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusName = req.getParameter("cusName");
        String date = req.getParameter("date");
        String id = req.getParameter("id");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("insert into orders values(?,?,?)");
            pstm.setObject(1, id);
            pstm.setObject(2, date);
            pstm.setObject(3, cusName);
            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder responseObj = Json.createObjectBuilder();
                responseObj.add("Status","ok");
                responseObj.add("message","Successfully Added...!");
                responseObj.add("data","");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(responseObj.build());


            }
        } catch (ClassNotFoundException | SQLException e) {
            JsonObjectBuilder error = Json.createObjectBuilder();
            error.add("Status","Error");
            error.add("message",e.getLocalizedMessage());
            error.add("data","");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(error.build());


        }
    }
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods","PUT,DELETE");
        resp.addHeader("Access-Control-Allow-Headers","content-type");

    }
}
