package main.dataBase;

import com.sun.istack.internal.NotNull;
import main.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerOrdersDML {

    private static final String TABLE_NAME = "\"CustomerOrders\"";

    private static final String C_TABLE_NAME = "\"Customer\"";
    private static final String CUSTOMER_ID = "customer_id";

    private static final String O_TABLE_NAME = "\"Order\"";
    private static final String ORDER_ID = "order_id";
    private static final String ADDRESS = "address";

    public static int createTable(Connection connection) {
        int res = -1;
        try {
            Statement stmt = connection.createStatement();
            String create_request = "-- Table: public.\"CustomerOrders\"\n" +
                    "\n" +
                    "-- DROP TABLE public.\"CustomerOrders\";\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS public.\"CustomerOrders\"\n" +
                    "(\n" +
                    "    customer_id uuid NOT NULL,\n" +
                    "    order_id uuid NOT NULL,\n" +
                    "    CONSTRAINT \"CustomerOrders_pkey\" PRIMARY KEY (customer_id, order_id),\n" +
                    "    CONSTRAINT customer_id FOREIGN KEY (customer_id)\n" +
                    "        REFERENCES public.\"Customer\" (customer_id) MATCH SIMPLE\n" +
                    "        ON UPDATE NO ACTION\n" +
                    "        ON DELETE NO ACTION,\n" +
                    "    CONSTRAINT order_id FOREIGN KEY (order_id)\n" +
                    "        REFERENCES public.\"Order\" (order_id) MATCH SIMPLE\n" +
                    "        ON UPDATE NO ACTION\n" +
                    "        ON DELETE NO ACTION\n" +
                    ")\n" +
                    "\n" +
                    "TABLESPACE pg_default;\n" +
                    "\n" +
                    "ALTER TABLE public.\"CustomerOrders\"\n" +
                    "    OWNER to ssau_web;\n" +
                    "COMMENT ON TABLE public.\"CustomerOrders\"\n" +
                    "    IS 'lab for web programming';";
            res = stmt.executeUpdate(create_request);
            System.out.println("TABLE " + TABLE_NAME + "- created ");
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            res = -1;
        }
        return res;
    }

    public static int insert(@NotNull Connection connection, @NotNull UUID customerId, @NotNull UUID orderId) {
        int res = 0;
        try {
            String insert_request = "INSERT INTO " + TABLE_NAME + " VALUES(" +
                    "uuid('" + customerId.toString() + "'), " +
                    "uuid('" + orderId.toString() + "'));";
            System.out.print(insert_request);

            Statement stmt = connection.createStatement();
            res = stmt.executeUpdate(insert_request);
            System.out.println(" result: " + res);
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    /**
     * удаляет все связи список-элемент из БД для заданного списка
     */
    public static int deleteCustomer(@NotNull Connection connection, @NotNull UUID customerId) {
        int res = -1;
        try {
            Statement stmt = connection.createStatement();
            String delete_request = "DELETE FROM " + TABLE_NAME +
                    " WHERE " + CUSTOMER_ID + "=uuid('" + customerId.toString() + "');";
            System.out.print(delete_request);
            //autocommit
            res = stmt.executeUpdate(delete_request);
            System.out.println(" result: " + res);
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static int delete(@NotNull Connection connection, @NotNull UUID customerId, @NotNull UUID orderId) {
        int res = -1;
        try {
            Statement stmt = connection.createStatement();
            String delete_request = "DELETE FROM " + TABLE_NAME +
                    "WHERE " + ORDER_ID + "=uuid(" + orderId.toString() + "') AND " +
                    CUSTOMER_ID + "=uuid(" + customerId.toString() + "');";
            System.out.print(delete_request);
            //autocommit
            res = stmt.executeUpdate(delete_request);
            System.out.println(" result: " + res);
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }


    public static ArrayList<Order> getOrder(@NotNull Connection connection, @NotNull UUID customerId) {
        ArrayList<Order> arrOrder = null;
        try {
            Statement stmt = connection.createStatement();
            String select_request = "SELECT * FROM " + O_TABLE_NAME +
                    "WHERE " + ORDER_ID + " in " +
                    "(SELECT " + ORDER_ID + " FROM" + TABLE_NAME +
                    " WHERE " + CUSTOMER_ID + "=uuid('" + customerId.toString() + "'));";
            System.out.println(select_request);

            ResultSet resSet = stmt.executeQuery(select_request);
            arrOrder = new ArrayList<>();
            for (int i = 0; resSet.next(); ++i) {
                Order temp = new Order();
                temp.setAddress(resSet.getString(ADDRESS));

                temp.setId(UUID.fromString(resSet.getString(ORDER_ID)));

                List<Item> items = OrderItemDML.getItem(connection, temp.getId());
                temp.addAll(items);
                arrOrder.add(temp);
            }
            if (arrOrder.size() == 0)
                arrOrder = null;
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return arrOrder;
    }
}
