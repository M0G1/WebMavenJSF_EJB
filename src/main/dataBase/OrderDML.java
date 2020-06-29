package main.dataBase;

import com.sun.istack.internal.NotNull;
import main.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDML {
    private static final String O_TABLE_NAME = "\"Order\"";
    private static final String ORDER_ID = "order_id";
    private static final String ADDRESS = "address";

    public static int createTable(Connection connection) {
        int res = -1;
        try {
            Statement stmt = connection.createStatement();
            String create_request = "-- Table: public.\"Order\"\n" +
                    "\n" +
                    "-- DROP TABLE public.\"Order\";\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS public.\"Order\"\n" +
                    "(\n" +
                    "    order_id uuid NOT NULL,\n" +
                    "    address text COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                    "    CONSTRAINT \"Order_pkey\" PRIMARY KEY (order_id)\n" +
                    ")\n" +
                    "\n" +
                    "TABLESPACE pg_default;\n" +
                    "\n" +
                    "ALTER TABLE public.\"Order\"\n" +
                    "    OWNER to ssau_web;\n" +
                    "COMMENT ON TABLE public.\"Order\"\n" +
                    "    IS 'lab for web programming';";
            res = stmt.executeUpdate(create_request);
            System.out.println("TABLE " + O_TABLE_NAME + "- created ");
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            res = -1;
        }
        return res;
    }

    public static int insert(@NotNull Connection connection, @NotNull Order order) {
        int res = -1;
        try {
            if (getIdExistOrder(connection, order) == null) {
                String insert_request = "INSERT INTO " + O_TABLE_NAME + " VALUES(" +
                        "uuid('" + order.getId().toString() + "'), '" +
                        order.getAddress()
                        + "');";

                System.out.print(insert_request);

                Statement stmt = connection.createStatement();
                res = stmt.executeUpdate(insert_request);
                System.out.println(" result: " + res);

                stmt.close();
            } else
                res = 0;
            for (Item item : order) {
//                UUID item_id = ItemDML.getIdExistItem(connection, item);
//                if (item_id == null)
//                    ItemDML.insertItem(connection, item);
//                else
//                    item.setId(item_id);
                ItemDML.insertItem(connection, item);
                OrderItemDML.insert(connection, order.getId(), item.getId());
            }
            //autocommit
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    /**
     * return: первый id  с такими параметрами полей в записи таблицы как у order, если есть иначе null
     */
    public static UUID getIdExistOrder(@NotNull Connection connection, @NotNull Order order) {
        UUID res = null;
        try {
            String select_request = "SELECT " + ORDER_ID + " FROM " + O_TABLE_NAME +
                    "WHERE " + ADDRESS + "='" + order.getAddress() + "';";
            System.out.println(select_request);

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(select_request);
            if (resultSet.next())
                res = UUID.fromString(resultSet.getString(ORDER_ID));
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    /**
     * удаляет список с заказами и элементы заказа из БД
     */
    public static int deleteOrder(@NotNull Connection connection, @NotNull Order order, boolean isDeleteItems) {
        int res = -1;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
//            UUID res_id = getIdExistOrder(connection, order);
//            if (res_id == null)
//                return 0;
            String delete_request = "DELETE FROM " + O_TABLE_NAME +
                    " WHERE " + ORDER_ID + " =uuid('" + order.getId() + "');";
            OrderItemDML.deleteOrder(connection, order.getId());
            if (isDeleteItems)
                for (Item item : order)
                    ItemDML.deleteItem(connection, item);

            System.out.print(delete_request);

            Statement stmt = connection.createStatement();
            res = stmt.executeUpdate(delete_request);
            System.out.println(" result: " + res);
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }


    public static int updateAddressOrder(@NotNull Connection connection, @NotNull Order order, @NotNull String address) {
        int res = -1;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
            UUID res_id = getIdExistOrder(connection, order);
            if (res_id == null)
                return res;
            String update_request = "UPDATE " + O_TABLE_NAME +
                    "SET " + ADDRESS + "= '" + address + "' " +
                    "WHERE " + ORDER_ID + "=uuid('" + order.getId().toString() + "')," + "';";

            System.out.print(update_request);

            Statement stmt = connection.createStatement();
            res = stmt.executeUpdate(update_request);
            System.out.println(" result: " + res);
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static Order getOrder(@NotNull Connection connection, @NotNull UUID orderUUID) {
        Order res = null;
        try {
            Statement stmt = connection.createStatement();
            String select_request = "SELECT * FROM " + O_TABLE_NAME +
                    " WHERE " + ORDER_ID + " =uuid('" + orderUUID.toString() + "');";
            System.out.println(select_request);

            ResultSet resSet = stmt.executeQuery(select_request);
            if (resSet.next()) {
                res = new Order();
                res.setId(UUID.fromString(resSet.getString(ORDER_ID)));
                res.setAddress(resSet.getString(ADDRESS));
                List items = OrderItemDML.getItem(connection, orderUUID);
                res.addAll(items);
            }
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static List<UUID> getOrdersID(@NotNull Connection connection, @NotNull String address) {
        List<UUID> res = null;
        try {
            Statement stmt = connection.createStatement();
            String select_request = "SELECT * FROM " + O_TABLE_NAME +
                    " WHERE " + ADDRESS + " like '%" + address + "%';";
            System.out.println(select_request);

            ResultSet resSet = stmt.executeQuery(select_request);

            res = new ArrayList<>();
            for (int i = 0; resSet.next(); ++i)
                res.add(UUID.fromString(resSet.getString(ORDER_ID)));
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static Order[] getOrders(@NotNull Connection connection, @NotNull String address) {
        List<UUID> uuids = getOrdersID(connection, address);
        Order[] res = new Order[uuids.size()];
        for (int i = 0; i < res.length; ++i)
            res[i] = (getOrder(connection, uuids.get(i)));
        return res;
    }
}
