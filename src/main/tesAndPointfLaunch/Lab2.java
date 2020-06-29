package main.tesAndPointfLaunch;

import main.dataBase.*;
import main.model.*;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lab2 {


//  своеобразный лог
    public static void main(String[] args) {
        File file = new File("orders.txt");

        Order[] orders = Lab1Test.readOrders(file);
        //не знаю зачем, но пусть будет
        List<Item> items = getAllItems(orders);
        Customer[] cust = createCustomers(orders);

        System.out.println(Arrays.toString(cust));

        Connection connection = DataBase.getConnection();
        if (connection == null)
            return;
//        boolean isDeleteAll = false;
//        убираем талицы и данные
        DataBase.deleteAndDropAll(connection);
//        создаем таблицы
        DataBase.createTables(connection);
        System.out.println("delete all: " + DataBase.deleteAllData(connection) + "\n");
//        добавляем в бд данные
        insert(connection, cust);
        System.out.println();
//получить пользователя по паролю и логину
        Customer customer = CustomerDML.getExistCustomer(connection, cust[2].getLogin(), cust[2].getPassword());
        System.out.println(customer + "\n");
        System.out.println(cust[2] + "\n");
//        список заказов пользователя
        List<Order> orders2 = CustomerOrdersDML.getOrder(connection, cust[2].getId());
        System.out.println(Arrays.toString(orders2.toArray()));
//      список элементов заказа
        List<Item> items2 = OrderItemDML.getItem(connection, orders2.get(0).getId());
        System.out.println(Arrays.toString(items2.toArray()));
//      Получить заказы по совпадающему по подстроке адресу
        Order[] orders3 = OrderDML.getOrders(connection, "City");
        System.out.println(Arrays.toString(orders3));
        System.out.println();
        System.out.println(Arrays.toString(orders));
//        получить полльзователей по аддрессу доставки(для 1 точки выдачи всех пользователей)
        ArrayList<Customer> customers = CustomerDML.getCustomerForAddress(connection, "st.1");
        System.out.println(Arrays.toString(customers.toArray()));
        System.out.println();

        Customer mayBeNull = CustomerDML.getExistCustomer(connection,"w","");
        System.out.println(mayBeNull==null ? "Null is null":mayBeNull.toString());
//        удаление данных с бд по существующим пользователям(переданным как параметр)
//        delete(connection, cust);

    }

    public static void insert(Connection connection, Customer[] customers) {
        for (int i = 0; i < customers.length; ++i)
            CustomerDML.insertCustomer(connection, customers[i]);
        System.out.println("INSERT COMPLETE");
    }

    public static void delete(Connection connection, Customer[] customers) {
        for (int i = 0; i < customers.length; ++i)
            CustomerDML.deleteCustomer(connection, customers[i], true);
        System.out.println("DELETE COMPLETE");
    }

    public static Customer[] createCustomers(Order[] orders) {
        String[] passwords = {"ags", "josdvf", "oerk", "cvkbj", "wope"};
        String[] logins = {"Alex", "Mus", "Ann", "Quan_Zhi_Gao_Shou", "JoJo"};

        Customer[] cust = new Customer[logins.length];
        for (int i = 0; i < cust.length; ++i) {
            cust[i] = new Customer(logins[i], logins[i], passwords[i]);
            for (int j = 0; i * 2 + j < orders.length && j < 2; ++j)
                cust[i].add(orders[i * 2 + j]);
        }

        return cust;
    }

    public static List<Item> getAllItems(Order[] orders) {
//        int n = 0;
//        for (int j = 0; j < orders.length; ++j)
//            n += orders[j].size();
        List<Item> items = new ArrayList<>();
//        n = 0;
        for (Order order : orders) {
            items.addAll(order);
        }
        return items;
    }


}
