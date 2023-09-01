package lk.ijse.jsp.servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = {"/pages/purchase-order"})

public class PurchaseOrderAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader=Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String oid = jsonObject.getString("oid");
        String date = jsonObject.getString("date");
        String odCusId = jsonObject.getString("odCusId");
        resp.addHeader("Access-Control-Allow-Origin", "*");
       /* try {
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


        }*/

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234");
                connection.setAutoCommit(false);
                PreparedStatement pstm = connection.prepareStatement("insert into orders values(?,?,?)");
                pstm.setObject(1, oid);
                pstm.setObject(2, date);
                pstm.setObject(3, odCusId);
                if (pstm.executeUpdate() > 0) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    throw new SQLException("Order Not Added");

                }
                JsonArray odDetail = jsonObject.getJsonArray("odDetail");
                for (JsonValue orderDetail: odDetail) {
                    JsonObject odObject = orderDetail.asJsonObject();
                    String itemCode = odObject.getString("code");
                    String qty = odObject.getString("qty");
                    String byQty = odObject.getString("byQty");
                    String unitPrice = odObject.getString("price");

                    PreparedStatement pstm2 = connection.prepareStatement("insert into orderdetails values(?,?,?,?)");
                    pstm2.setObject(1, oid);
                    pstm2.setObject(2, itemCode);
                    pstm2.setObject(3,byQty );
                    pstm2.setObject(4, unitPrice);

                    if (!(pstm2.executeUpdate() > 0)) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                        throw new SQLException("Order Details Not added.!");
                    }


                    PreparedStatement pstm3 = connection.prepareStatement("update item set qty=? where code=?");
                    pstm3.setObject(2, itemCode);
                    int bvQty=Integer.parseInt(byQty);
                    int avQty=Integer.parseInt(qty);
                    pstm3.setObject(1,(avQty-bvQty));
                    if (!(pstm3.executeUpdate() > 0)) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                        throw new SQLException("Order Details Not added.!");
                    }
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
