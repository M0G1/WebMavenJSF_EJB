package main.dataBase;

import com.sun.istack.internal.NotNull;
import main.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class ItemDML {
    private static final String TABLE_NAME = "\"Item\"";
    private static final String ITEM_ID = "item_id";
    private static final String NAME = "item_name";
    private static final String COUNT = "item_count";
    private static final String PRICE = "price";
    private static final String DATE_OF_RECEIPT = "\"dateOfReceipt\"";


    public static int createTable(Connection connection) {
        int res = -1;
        try {
            Statement stmt = connection.createStatement();
            String create_request = "-- Table: public.\"Item\"\n" +
                    "\n" +
                    "-- DROP TABLE public.\"Item\";\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS public.\"Item\"\n" +
                    "(\n" +
                    "    item_id uuid NOT NULL,\n" +
                    "    item_name text COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                    "    item_count integer NOT NULL,\n" +
                    "    \"dateOfReceipt\" date NOT NULL,\n" +
                    "    price double precision NOT NULL,\n" +
                    "    CONSTRAINT \"Item_pkey\" PRIMARY KEY (item_id)\n" +
                    ")\n" +
                    "\n" +
                    "TABLESPACE pg_default;\n" +
                    "\n" +
                    "ALTER TABLE public.\"Item\"\n" +
                    "    OWNER to ssau_web;\n" +
                    "COMMENT ON TABLE public.\"Item\"\n" +
                    "    IS 'lab web programming';";
            res = stmt.executeUpdate(create_request);
            System.out.println("TABLE " + TABLE_NAME + "- created ");
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            res = -1;
        }
        return res;
    }

    public static int insertItem(@NotNull Connection connection, @NotNull Item item) {
        int res = -1;
        try {
            if (getIdExistItem(connection, item) == null) {
                DateFormat dateFormat = DateFormat.getDateInstance();
                String insert_request = "INSERT INTO " + TABLE_NAME + " VALUES(" +
                        "uuid('" + item.getId().toString() + "')," +
                        '\'' + item.getName() + "'," +
                        item.getCount() + "," + "DATE '" + dateFormat.format(item.getDateOfReceipt()) + "'," +
                        item.getPrice() +
                        ");";
                System.out.print(insert_request);

                Statement stmt = connection.createStatement();
                res = stmt.executeUpdate(insert_request);
                System.out.println(" result: " + res);
                //autocommit
                stmt.close();
            } else
                res = 0;
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    /**
     * return: первый id  с такими параметрами полей в записи таблицы как у item, если есть иначе null
     */
    public static UUID getIdExistItem(@NotNull Connection connection, @NotNull Item item) {
        UUID res = null;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
            String select_request = "SELECT " + ITEM_ID + " FROM " + TABLE_NAME +
                    "WHERE " + NAME + "='" + item.getName() +
                    "' AND " + COUNT + '=' + item.getCount() +
                    " AND " + PRICE + '=' + item.getPrice() +
                    " AND " + DATE_OF_RECEIPT + "= DATE '" + dateFormat.format(item.getDateOfReceipt()) + "';";
            System.out.println(select_request);

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(select_request);
            if (resultSet.next())
                res = UUID.fromString(resultSet.getString(ITEM_ID));
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static int deleteItem(@NotNull Connection connection, @NotNull Item item) {
        int res = -1;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
//            UUID res_id = getIdExistItem(connection, item);
//            if (res_id == null)
//                return true;
            String delete_request = "DELETE FROM " + TABLE_NAME +
                    "WHERE " + NAME + "='" + item.getName() +
                    "' AND " + COUNT + '=' + item.getCount() +
                    " AND " + PRICE + '=' + item.getPrice() +
                    "AND " + DATE_OF_RECEIPT + "= DATE '" + dateFormat.format(item.getDateOfReceipt()) + "';";
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

    /**
     * элемент полностью заменяется на данный, если такой есть
     */
    public static int updateItem(@NotNull Connection connection, @NotNull Item oldItem, @NotNull Item newItem) {
        int res = -1;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
            UUID res_id = getIdExistItem(connection, oldItem);
            if (res_id == null)
                return res;
            String update_request = "UPDATE " + TABLE_NAME +
                    "SET " +
                    ITEM_ID + "= uuid('" + newItem.getId().toString() + "')," +
                    NAME + "='" + newItem.getName() + "'," +
                    COUNT + '=' + newItem.getCount() +
                    PRICE + '=' + newItem.getPrice() + ',' +
                    DATE_OF_RECEIPT + "= DATE '" + dateFormat.format(newItem.getDateOfReceipt()) + ' ' +
                    "WHERE " + ITEM_ID + "=uuid('" + res_id.toString() + "');";
            System.out.println(update_request);

            Statement stmt = connection.createStatement();
            res = stmt.executeUpdate(update_request);
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static Item getItem(@NotNull Connection connection, @NotNull UUID itemUUID) {
        Item res = null;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
            String select_request = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + ITEM_ID + " =uuid('" + itemUUID.toString() + "');";
            System.out.println(select_request);

            Statement stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(select_request);
            if (resSet.next()) {
                res = new Item();
                res.setId(UUID.fromString(resSet.getString(ITEM_ID)));
                res.setCount(resSet.getInt(COUNT));
                res.setName(resSet.getString(NAME));
                res.setPrice(resSet.getDouble(PRICE));
                res.setDateOfReceipt(new Date(resSet.getDate(DATE_OF_RECEIPT).getTime()));
            }
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

}
