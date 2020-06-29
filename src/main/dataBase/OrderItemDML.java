package main.dataBase;

import com.sun.istack.internal.NotNull;
import main.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class OrderItemDML {

    private static final String TABLE_NAME = "\"OrderItem\"";
    private static final String ORDER_ID = "order_id";

    private static final String ITEM_TABLE_NAME = "\"Item\"";
    private static final String ITEM_ID = "item_id";
    private static final String NAME = "item_name";
    private static final String COUNT = "item_count";
    private static final String PRICE = "price";
    private static final String DATE_OF_RECEIPT = "dateOfReceipt";

    public static int createTable(Connection connection) {
        int res = -1;
        try {
            Statement stmt = connection.createStatement();
            String create_request = "-- Table: public.\"OrderItem\"\n" +
                    "\n" +
                    "-- DROP TABLE public.\"OrderItem\";\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS public.\"OrderItem\"\n" +
                    "(\n" +
                    "    item_id uuid NOT NULL,\n" +
                    "    order_id uuid NOT NULL,\n" +
                    "    CONSTRAINT \"OrderItem_pkey\" PRIMARY KEY (item_id, order_id),\n" +
                    "    CONSTRAINT item_id FOREIGN KEY (item_id)\n" +
                    "        REFERENCES public.\"Item\" (item_id) MATCH SIMPLE\n" +
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
                    "ALTER TABLE public.\"OrderItem\"\n" +
                    "    OWNER to ssau_web;\n" +
                    "COMMENT ON TABLE public.\"OrderItem\"\n" +
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

    public static int insert(@NotNull Connection connection, @NotNull UUID orderId, @NotNull UUID itemId) {
        int res = -1;
        try {
            String insert_request = "INSERT INTO " + TABLE_NAME + " VALUES(" +
                    "uuid('" + itemId.toString() + "'), " +
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
    public static int deleteOrder(@NotNull Connection connection, @NotNull UUID orderId) {
        int res = -1;
        try {
            String delete_request = "DELETE FROM " + TABLE_NAME +
                    " WHERE " + ORDER_ID + "=uuid('" + orderId.toString() + "') ;";
            System.out.println(delete_request);
            //autocommit
            Statement stmt = connection.createStatement();
            res = stmt.executeUpdate(delete_request);
            System.out.println(" result: " + res);

            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static int delete(@NotNull Connection connection, @NotNull UUID orderId, @NotNull UUID itemId) {
        int res = -1;
        try {
            String delete_request = "DELETE FROM " + TABLE_NAME +
                    "WHERE " + ORDER_ID + "=uuid(" + orderId.toString() + "') AND " +
                    ITEM_ID + "=uuid(" + itemId.toString() + "');";
            System.out.print(delete_request);
            //autocommit
            Statement stmt = connection.createStatement();
            res = stmt.executeUpdate(delete_request);
            System.out.println(" result: " + res);

            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static long getCountOfLinksWithOrder(@NotNull Connection connection, @NotNull UUID itemUUID) {
        long res = 0L;
        try {
            String select_request = "SELECT count(*) FROM" + TABLE_NAME +
                    " WHERE " + ITEM_ID + "=uuid(" + itemUUID.toString() + "');";
            System.out.println(select_request);

            Statement stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(select_request);
            res = resSet.getLong("count");
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static UUID[] getItemUUID(@NotNull Connection connection, @NotNull UUID orderUUID) {
        UUID[] res = null;
        try {
            String select_request = "SELECT " + ITEM_ID + " FROM" + TABLE_NAME +
                    " WHERE " + ORDER_ID + "=uuid(" + orderUUID.toString() + "');";
            System.out.println(select_request);

            Statement stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(select_request);
            res = new UUID[resSet.getFetchSize()];
            for (int i = 0; resSet.next(); ++i)
                res[i] = UUID.fromString(resSet.getString(ITEM_ID));
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static ArrayList<Item> getItem(@NotNull Connection connection, @NotNull UUID orderUUID) {
        ArrayList<Item> arrItem = null;
        try {
            String select_request = "SELECT * FROM " + ITEM_TABLE_NAME +
                    " WHERE " + ITEM_ID + " in " +
                    "(SELECT " + ITEM_ID + " FROM" + TABLE_NAME +
                    " WHERE " + ORDER_ID + "=uuid('" + orderUUID.toString() + "'));";
            System.out.println(select_request);

            DateFormat dateFormat = DateFormat.getDateInstance();
            Statement stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(select_request);
            arrItem = new ArrayList<>();
            for (int i = 0; resSet.next(); ++i) {
                int count = resSet.getInt(COUNT);
                UUID id = UUID.fromString(resSet.getString(ITEM_ID));
                String name = resSet.getString(NAME);
                double price = resSet.getDouble(PRICE);
                Date date = new Date(resSet.getDate(DATE_OF_RECEIPT).getTime());

                arrItem.add(new Item(
                                id,
                                name,
                                price,
                                count,
                                date
                        )
                );
            }
            if (arrItem.size() == 0) {
                arrItem = null;
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }/* catch (ParseException e) {
//            System.err.println(e.getMessage());
//        }*/
        return arrItem;
    }
}
