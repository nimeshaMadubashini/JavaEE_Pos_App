package lk.ijse.jsp.servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(urlPatterns = {"/pages/item"})
public class ItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {


            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany?useSSL=true&requireSSL=true", "root", "1234");
            String option = req.getParameter("option");

            switch (option) {
                case "getAll":
                    PreparedStatement pstm = connection.prepareStatement("select * from item");
                    ResultSet rst = pstm.executeQuery();
                    resp.addHeader("Content-Type", "application/json");
                    resp.addHeader("Access-Control-Allow-Origin", "*");
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    while (rst.next()) {
                        String code = rst.getString(1);
                        String name = rst.getString(2);
                        String qty = rst.getString(3);
                        String price = rst.getString(4);

                        JsonObjectBuilder customerObject = Json.createObjectBuilder();
                        customerObject.add("code", code);
                        customerObject.add("name", name);
                        customerObject.add("qty", qty);
                        customerObject.add("price", price);
                        jsonArrayBuilder.add(customerObject.build());
                    }
                    resp.setContentType("application/json");
                    JsonObjectBuilder responseObj = Json.createObjectBuilder();
                    responseObj.add("Status", "ok");
                    responseObj.add("message", "Successfully Loaded...!");
                    responseObj.add("data", jsonArrayBuilder.build());
                    resp.getWriter().print(responseObj.build());


                    break;
                case "search":
                    String itemCode = req.getParameter("code");
                    if (itemCode != null && !itemCode.isEmpty()) {
                        PreparedStatement pstm1 = connection.prepareStatement("SELECT * FROM item WHERE code = ?");
                        pstm1.setString(1, itemCode);
                        ResultSet rst1 = pstm1.executeQuery();
                        JsonArrayBuilder jsonArrayBuilder1 = Json.createArrayBuilder();
                        while (rst1.next()) {
                            JsonObjectBuilder itemObject = Json.createObjectBuilder();
                            itemObject.add("code", rst1.getString("code"));
                            itemObject.add("description", rst1.getString("name"));
                            itemObject.add("qty", rst1.getDouble("qty"));
                            itemObject.add("price", rst1.getDouble("price"));

                            jsonArrayBuilder1.add(itemObject.build());
                        }
                        resp.setContentType("application/json");
                        JsonObjectBuilder responseObj1 = Json.createObjectBuilder();
                        responseObj1.add("Status", "ok");
                        responseObj1.add("message", "Successfully Loaded...!");
                        responseObj1.add("data", jsonArrayBuilder1.build());
                        resp.getWriter().print(responseObj1.build());


                    }
                    break;
                case "loadCode":
                    PreparedStatement pstm2 = connection.prepareStatement("SELECT code FROM item");
                        try (ResultSet rst2= pstm2.executeQuery()) {
                            JsonArrayBuilder jsonArrayBuilder2 = Json.createArrayBuilder();
                            while (rst2.next()) {
                                String id = rst2.getString("code");

                                JsonObjectBuilder itemObject = Json.createObjectBuilder();
                                itemObject.add("code", id);

                                jsonArrayBuilder2.add(itemObject);
                            }

                            resp.setContentType("application/json");
                            JsonObjectBuilder responseObj1 = Json.createObjectBuilder();
                            responseObj1.add("Status", "ok");
                            responseObj1.add("message", "Successfully Loaded...!");
                            responseObj1.add("data", jsonArrayBuilder2.build());
                            resp.getWriter().print(responseObj1.build());
                        }

            }
        } catch (ClassNotFoundException | SQLException e) {
            JsonObjectBuilder error = Json.createObjectBuilder();
            error.add("Status", "Error");
            error.add("message", e.getLocalizedMessage());
            error.add("data", "");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(error.build());

        }
    }
    private void fetchItemDetails(Connection connection, String itemCode, JsonObjectBuilder responseObj) throws SQLException {
        try (PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code = ?")) {
            pstm.setString(1, itemCode);
            try (ResultSet rst = pstm.executeQuery()) {
                JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                while (rst.next()) {
                    JsonObjectBuilder itemObject = Json.createObjectBuilder();
                    itemObject.add("code", rst.getString("code"));
                    itemObject.add("description", rst.getString("name"));
                    itemObject.add("qty", rst.getDouble("qty"));
                    itemObject.add("price", rst.getDouble("price"));

                    jsonArrayBuilder.add(itemObject);
                }

                responseObj.add("data", jsonArrayBuilder);
            }
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String description = req.getParameter("description");
        String qty = req.getParameter("qty");
        String unitPrice = req.getParameter("unitPrice");

        resp.addHeader("Access-Control-Allow-Origin", "*");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("insert into item values(?,?,?,?)");
            pstm.setObject(1, code);
            pstm.setObject(2, description);
            pstm.setObject(3, qty);
            pstm.setObject(4, unitPrice);

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
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Content-Type", "application/json");
        String code = req.getParameter("code");


        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
            PreparedStatement pstm2 = connection.prepareStatement("delete from item where code=?");
            pstm2.setObject(1, code);
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
        resp.addHeader("Access-Control-Allow-Origin", "*");
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject cusObj = reader.readObject();
        String code = cusObj.getString("code");
        String name = cusObj.getString("name");
        String qty = cusObj.getString("qty");
        String price = cusObj.getString("price");


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
            PreparedStatement pstm3 = connection.prepareStatement("update item set  name=?,qty=?,price=? where code=?");
            pstm3.setObject(4, code);
            pstm3.setObject(1, name);
            pstm3.setObject(2, qty);
            pstm3.setObject(3, price);


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

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods","PUT,DELETE");
        resp.addHeader("Access-Control-Allow-Headers","content-type");

    }
}
