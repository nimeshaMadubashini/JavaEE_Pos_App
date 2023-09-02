package lk.ijse.jsp.servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(urlPatterns = {"/pages/customer"})
public class CustomerServletAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany?useSSL=true&requireSSL=true", "root", "1234");
            String option = req.getParameter("option");
            switch (option) {
                case "getAll":
                    PreparedStatement pstm = connection.prepareStatement("select * from customer");
                    ResultSet rst = pstm.executeQuery();

                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    while (rst.next()) {
                        String id = rst.getString(1);
                        String name = rst.getString(2);
                        String address = rst.getString(3);

                        JsonObjectBuilder customerObject = Json.createObjectBuilder();
                        customerObject.add("id", id);
                        customerObject.add("name", name);
                        customerObject.add("address", address);
                        jsonArrayBuilder.add(customerObject.build());
                    }

                    JsonObjectBuilder responseObj = Json.createObjectBuilder();
                    responseObj.add("Status", "ok");
                    responseObj.add("message", "Successfully Loaded...!");
                    responseObj.add("data", jsonArrayBuilder.build());
                    resp.getWriter().print(responseObj.build());
                    break;
                case "search":
                    String cusId = req.getParameter("cusId");
                    PreparedStatement pstm1 = connection.prepareStatement("SELECT * FROM customer WHERE id = ?");
                    pstm1.setString(1, cusId);
                    ResultSet rst1 = pstm1.executeQuery();
                    JsonArrayBuilder jsonArrayBuilder1 = Json.createArrayBuilder();
                    while (rst1.next()) {
                        JsonObjectBuilder itemObject = Json.createObjectBuilder();
                        itemObject.add("id", rst1.getString("id"));
                        itemObject.add("name", rst1.getString("name"));
                        itemObject.add("address", rst1.getString("address"));

                        jsonArrayBuilder1.add(itemObject);
                    }

                    JsonObjectBuilder responseObj1 = Json.createObjectBuilder();
                    responseObj1.add("Status", "ok");
                    responseObj1.add("message", "Successfully Loaded...!");
                    responseObj1.add("data", jsonArrayBuilder1.build());
                    resp.getWriter().print(responseObj1.build());
                    break;
                case "loadCode":
                    PreparedStatement pstm2 = connection.prepareStatement("SELECT id FROM customer");
                    try (ResultSet rst2 = pstm2.executeQuery()) {
                        JsonArrayBuilder jsonArrayBuilder2 = Json.createArrayBuilder();
                        while (rst2.next()) {
                            String id = rst2.getString("id");

                            JsonObjectBuilder itemObject = Json.createObjectBuilder();
                            itemObject.add("id", id);

                            jsonArrayBuilder2.add(itemObject);
                        }

                        JsonObjectBuilder responseObj2 = Json.createObjectBuilder();
                        responseObj2.add("Status", "ok");
                        responseObj2.add("message", "Successfully Loaded...!");
                        responseObj2.add("data", jsonArrayBuilder2.build());
                        resp.getWriter().print(responseObj2.build());
                    }

            }
        }  catch (ClassNotFoundException |SQLException e) {
            JsonObjectBuilder error = Json.createObjectBuilder();
            error.add("Status","Error");
            error.add("message",e.getLocalizedMessage());
            error.add("data","");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(error.build());


        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("insert into customer values(?,?,?)");
            pstm.setObject(1, cusID);
            pstm.setObject(2, cusName);
            pstm.setObject(3, cusAddress);
            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder responseObj = Json.createObjectBuilder();
                responseObj.add("Status","ok");
                responseObj.add("message","Successfully Added...!");
                responseObj.add("data","");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(responseObj.build());


            }
        } catch (ClassNotFoundException |SQLException e) {
            JsonObjectBuilder error = Json.createObjectBuilder();
            error.add("Status","Error");
            error.add("message",e.getLocalizedMessage());
            error.add("data","");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(error.build());


        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String cusID = req.getParameter("id");


        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
            PreparedStatement pstm2 = connection.prepareStatement("delete from customer where id=?");
            pstm2.setObject(1, cusID);
            if (pstm2.executeUpdate() > 0) {
                JsonObjectBuilder responseObj = Json.createObjectBuilder();
                responseObj.add("Status","ok");
                responseObj.add("message","Successfully Deleted...!");
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject cusObj = reader.readObject();
        String id = cusObj.getString("id");
        String name = cusObj.getString("name");
        String address = cusObj.getString("address");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
            PreparedStatement pstm3 = connection.prepareStatement("update customer set name=?,address=? where id=?");
            pstm3.setObject(3, id);
            pstm3.setObject(1, name);
            pstm3.setObject(2, address);

            if (pstm3.executeUpdate() > 0) {
                JsonObjectBuilder responseObj = Json.createObjectBuilder();
                responseObj.add("Status","ok");
                responseObj.add("message","Successfully Updated...!");
                responseObj.add("data","");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(responseObj.build());
            }

        } catch (SQLException | ClassNotFoundException e){
            JsonObjectBuilder error = Json.createObjectBuilder();
            error.add("Status","Error");
            error.add("message",e.getLocalizedMessage());
            error.add("data","");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(error.build());



        }
    }


}
