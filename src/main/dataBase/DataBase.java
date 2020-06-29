package main.dataBase;

import main.model.Customer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DataBase {
    private static final String CLASS_FOR_LOADING = "org.postgresql.Driver";
    private static final String DATA_BASE_LOCATION = "jdbc:postgresql://localhost:5432/pickup_points";
    private static final String[] USER_INFO = {"ssau_web", "ssau_web"};

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(CLASS_FOR_LOADING);
            connection = DriverManager.getConnection(DATA_BASE_LOCATION, USER_INFO[0], USER_INFO[1]);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            System.err.println("Class for name: " + CLASS_FOR_LOADING + " not found");

        } catch (SQLException e) {
            System.err.println("Connection to" + DATA_BASE_LOCATION + " with owner " + USER_INFO[0] + " failed");
        }
        return connection;
    }

    public static int deleteAllData(Connection connection) {
        int sum = 0;
        int res = -1;
        try {
            String[] delete_requests = {
                    "delete from \"OrderItem\"; ",
                    "delete from \"CustomerOrders\"; ",
                    "delete from \"Item\"; ",
                    "delete from \"Order\"; ",
                    "delete from \"Customer\"; "
            };
            Statement stmt = connection.createStatement();
            for (int i = 0; i < delete_requests.length; ++i) {
                System.out.print(delete_requests[i]);
                res = stmt.executeUpdate(delete_requests[i]);
                System.out.println(" result: " + res);
                sum += res;
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection to" + DATA_BASE_LOCATION + " with owner " + USER_INFO[0] + " failed");
        }
        return sum;
    }

    public static int deleteAndDropAll(Connection connection) {
        deleteAllData(connection);
        int sum = 0;
        int res = -1;
        try {
            String[] drop_requests = {
                    "Drop TABLE IF EXISTS \"OrderItem\" CASCADE;",
                    "Drop TABLE IF EXISTS \"CustomerOrders\" CASCADE;",
                    "Drop TABLE IF EXISTS \"Item\" CASCADE;",
                    "Drop TABLE IF EXISTS \"Order\" CASCADE;",
                    "Drop TABLE IF EXISTS \"Customer\" CASCADE;"
            };
            Statement stmt = connection.createStatement();
            for (int i = 0; i < drop_requests.length; ++i) {
                System.out.print(drop_requests[i]);
                res = stmt.executeUpdate(drop_requests[i]);
//                System.out.println(" result: " + res);
                sum += res;
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Connection to" + DATA_BASE_LOCATION + " with owner " + USER_INFO[0] + " failed");
        }
        return sum;
    }

    public static int createTables(Connection connection) {
        int sum = 0;
        sum += ItemDML.createTable(connection);
        sum += OrderDML.createTable(connection);
        sum += CustomerDML.createTable(connection);
        sum += OrderItemDML.createTable(connection);
        sum += CustomerOrdersDML.createTable(connection);

        System.out.println("Result of creating TABLES: " + sum);

        return sum;

    }
}
