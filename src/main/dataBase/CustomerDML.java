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

public class CustomerDML {
    private static final String ADDRESS = "address";
    private static final String O_TABLE_NAME = "\"Order\"";
    private static final String TABLE_NAME = "\"CustomerOrders\"";

    private static final String C_TABLE_NAME = "\"Customer\"";
    private static final String CUSTOMER_ID = "customer_id";
    private static final String NAME = "char_name";
    private static final String LOGIN = "char_login";
    private static final String PASSWORD = "char_password";
    private static final String ORDER_ID = "order_id";

    public static int createTable(Connection connection) {
        int res = -1;
        try {
            Statement stmt = connection.createStatement();
            String create_request = "-- Table: public.\"Customer\"\n" +
                    "\n" +
                    "-- DROP TABLE IF NOT EXIST public.\"Customer\";\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS public.\"Customer\"\n" +
                    "(\n" +
                    "    customer_id uuid NOT NULL,\n" +
                    "    char_name text COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                    "    char_login character varying(255) COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                    "    char_password character varying(255) COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                    "    CONSTRAINT \"Customer_pkey\" PRIMARY KEY (customer_id)\n" +
                    ")\n" +
                    "\n" +
                    "TABLESPACE pg_default;\n" +
                    "\n" +
                    "ALTER TABLE public.\"Customer\"\n" +
                    "    OWNER to ssau_web;\n" +
                    "COMMENT ON TABLE public.\"Customer\"\n" +
                    "    IS 'lab for web programming';";
            res = stmt.executeUpdate(create_request);
            System.out.println("TABLE " + C_TABLE_NAME + "- created ");
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            res = -1;
        }
        return res;
    }

    public static int insertCustomer(@NotNull Connection connection, @NotNull Customer customer) {
        int res = -1;
        try {
            if (getIdExistCustomer(connection, customer.getLogin(), customer.getPassword()) == null) {
                String insert_request = "INSERT INTO " + C_TABLE_NAME + " VALUES(" +
                        "uuid('" + customer.getId().toString() + "'), '" +
                        customer.getName() + "','" +
                        customer.getLogin() + "','" +
                        customer.getPassword() + "');";

                System.out.print(insert_request);
                Statement stmt = connection.createStatement();
                res = stmt.executeUpdate(insert_request);
                System.out.println(" result: " + res);
                stmt.close();

            } else
                res = 0;

            for (Order order : customer) {
//                UUID item_id = ItemDML.getIdExistItem(connection, item);
//                if (item_id == null)
//                    ItemDML.insertItem(connection, item);
//                else
//                    item.setId(item_id);
                OrderDML.insert(connection, order);
                //добавить связи между ними
                CustomerOrdersDML.insert(connection, customer.getId(), order.getId());
            }
            //autocommit
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    /**
     * удаляет список с заказами и элементы заказа из БД
     */
    public static int deleteCustomer(@NotNull Connection connection, @NotNull Customer customer, boolean isDeleteItems) {
        int res = -1;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
            String delete_request = "DELETE FROM " + C_TABLE_NAME +
                    "WHERE " + CUSTOMER_ID + "=uuid('" + customer.getId().toString() + "');";

            CustomerOrdersDML.deleteCustomer(connection, customer.getId());
            if (isDeleteItems)
                for (Order order : customer)
                    OrderDML.deleteOrder(connection, order, isDeleteItems);

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

    public static int updateCustomer(@NotNull Connection connection, @NotNull Customer oldCustomer, @NotNull Customer newCustomer) {
        int res = -1;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
            String update_request = "UPDATE " + C_TABLE_NAME +
                    "SET " +
                    CUSTOMER_ID + "= uuid('" + newCustomer.getId().toString() + "')," +
                    NAME + "='" + newCustomer.getName() + "'," +
                    LOGIN + '=' + newCustomer.getLogin() +
                    PASSWORD + '=' + newCustomer.getPassword() + ',' +
                    "WHERE " + CUSTOMER_ID + "=uuid('" + oldCustomer.getId().toString() + "');";
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

    public static Customer getCustomer(@NotNull Connection connection, @NotNull UUID customerUUID) {
        Customer res = null;
        try {
            DateFormat dateFormat = DateFormat.getDateInstance();
            Statement stmt = connection.createStatement();
            String select_request = "SELECT * FROM " + C_TABLE_NAME +
                    " WHERE " + CUSTOMER_ID + " =uuid('" + customerUUID.toString() + "');";
            System.out.println(select_request);

            ResultSet resSet = stmt.executeQuery(select_request);
            if (resSet.next()) {
                res = new Customer();
                res.addAll(CustomerOrdersDML.getOrder(connection, customerUUID));
            }
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static UUID getIdExistCustomer(Connection connection, String login, String password) {
        UUID res = null;
        try {
            Statement stmt = connection.createStatement();
            String select_request = "SELECT " + CUSTOMER_ID + " FROM " + C_TABLE_NAME +
                    "WHERE " + LOGIN + "='" + login + "' AND " +
                    PASSWORD + "='" + password + "';";
            System.out.println(select_request);

            ResultSet resultSet = stmt.executeQuery(select_request);
            if (resultSet.next())
                res = UUID.fromString(resultSet.getString(CUSTOMER_ID));
            //autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
        }
        return res;
    }

    public static Customer getExistCustomer(Connection connection, String login, String password) {
        Customer res = null;
        try {
            Statement stmt = connection.createStatement();
            String select_request = "SELECT  * FROM " + C_TABLE_NAME +
                    " WHERE " + LOGIN + "='" + login + "' AND " +
                    PASSWORD + "='" + password + "';";
            System.out.println(select_request);

            ResultSet resultSet = stmt.executeQuery(select_request);
            if (resultSet.next()) {
                res = new Customer();
                res.setName(resultSet.getString(NAME));
                res.setLogin(resultSet.getString(LOGIN));
                res.setPassword(resultSet.getString(PASSWORD));
                res.setId(UUID.fromString(resultSet.getString(CUSTOMER_ID)));
                List<Order> orders = CustomerOrdersDML.getOrder(connection, res.getId());
                res.addAll(orders);
            }//autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            res = null;
        }
        return res;
    }

    public static ArrayList<Customer> getCustomerForAddress(Connection connection, String address) {
        ArrayList<Customer> arrCustomer = null;
        try {
            Statement stmt = connection.createStatement();
            String select_request = "SELECT  * FROM " + C_TABLE_NAME +
                    " WHERE " + CUSTOMER_ID + " in (" +
                    "SELECT " + CUSTOMER_ID + " FROM " + TABLE_NAME + " WHERE " + ORDER_ID + " in (" +
                    "SELECT " + ORDER_ID + " FROM " + O_TABLE_NAME +
                    " WHERE " + ADDRESS + " like '%" + address + "%'));";
            System.out.println(select_request);

            ResultSet resultSet = stmt.executeQuery(select_request);
            arrCustomer = new ArrayList<>();
            for (int i = 0; resultSet.next(); ++i) {
                Customer temp = new Customer();
                temp.setId(UUID.fromString(resultSet.getString(CUSTOMER_ID)));
                List<Order> orders = CustomerOrdersDML.getOrder(connection, temp.getId());
                temp.addAll(orders);
                arrCustomer.add(temp);
            }//autocommit
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            arrCustomer = null;
        }
        return arrCustomer;
    }

}
