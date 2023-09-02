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

        String oid = req.getParameter("oid");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234")) {
            PreparedStatement pstm = connection.prepareStatement(
                    "SELECT orders.OrderId, orders.OrderDate, orders.CusId, orderdetails.itemCode, orderdetails.qty, orderdetails.unitPrice " +
                            "FROM orders INNER JOIN orderdetails ON orders.OrderId = orderdetails.Oid WHERE orders.OrderId=?"
            );
            pstm.setString(1, oid);
            ResultSet rst = pstm.executeQuery();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

            while (rst.next()) {
                JsonObjectBuilder order = Json.createObjectBuilder()
                        .add("oid", rst.getString(1))
                        .add("date", rst.getString(2))
                        .add("customerID", rst.getString(3))
                        .add("itemCode", rst.getString(4))
                        .add("qty", rst.getString(5))
                        .add("unitPrice", rst.getString(6));

                arrayBuilder.add(order);
                System.out.println(arrayBuilder);
            }

            resp.getWriter().print(arrayBuilder.build().toString());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(Json.createObjectBuilder()
                    .add("Status", "Error")
                    .add("message", e.getMessage())
                    .add("data", "")
                    .build()
                    .toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/AssCompany", "root", "1234")) {
            connection.setAutoCommit(false);
            JsonReader reader = Json.createReader(req.getReader());
            JsonObject jsonObject = reader.readObject();

            String oid = jsonObject.getString("oid");
            String date = jsonObject.getString("date");
            String odCusId = jsonObject.getString("odCusId");

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO orders VALUES (?,?,?)");
            pstm.setString(1, oid);
            pstm.setString(2, date);
            pstm.setString(3, odCusId);
            System.out.println(oid+date+odCusId);
            if (pstm.executeUpdate() <= 0) {
                connection.rollback();
                connection.setAutoCommit(true);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println(Json.createObjectBuilder()
                        .add("Status", "Error")
                        .add("message", "Order Not Added")
                        .add("data", "")
                        .build()
                        .toString());
                return;
            }

            JsonArray odDetail = jsonObject.getJsonArray("odDetail");

            for (JsonValue orderDetail : odDetail) {
                JsonObject odObject = orderDetail.asJsonObject();
                String itemCode = odObject.getString("code");
                String qty = odObject.getString("qty");
                String byQty = odObject.getString("byQty");
                String unitPrice = odObject.getString("price");

                PreparedStatement pstm2 = connection.prepareStatement("INSERT INTO orderdetails VALUES (?,?,?,?)");
                pstm2.setString(1, oid);
                pstm2.setString(2, itemCode);
                pstm2.setString(3, byQty);
                pstm2.setString(4, unitPrice);
                System.out.println(itemCode+qty+byQty+unitPrice);
                if (pstm2.executeUpdate() <= 0) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().println(Json.createObjectBuilder()
                            .add("Status", "Error")
                            .add("message", "Order Details Not added")
                            .add("data", "")
                            .build()
                            .toString());
                    return;
                }

                PreparedStatement pstm3 = connection.prepareStatement("UPDATE item SET qty=? WHERE code=?");
                pstm3.setString(2, itemCode);
                int bvQty = Integer.parseInt(byQty);
                int avQty = Integer.parseInt(qty);
                pstm3.setInt(1, avQty - bvQty);
                System.out.println(bvQty+avQty);
                if (pstm3.executeUpdate() <= 0) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().println(Json.createObjectBuilder()
                            .add("Status", "Error")
                            .add("message", "Order Details Not updated")
                            .add("data", "")
                            .build()
                            .toString());
                    return;
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(Json.createObjectBuilder()
                    .add("Status", "OK")
                    .add("message", "Successfully Added")
                    .add("data", "")
                    .build()
                    .toString());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(Json.createObjectBuilder()
                    .add("Status", "Error")
                    .add("message", e.getMessage())
                    .add("data", "")
                    .build()
                    .toString());
        }
    }



    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods","");
        resp.addHeader("Access-Control-Allow-Headers","content-type");

    }
}
