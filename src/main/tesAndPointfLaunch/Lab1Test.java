package main.tesAndPointfLaunch;

import main.model.*;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


public class Lab1Test {
    public static int number = 1;

    public static void main(String[] args) {
        //System.out.println("Is list instance of Serializable" + new ArrayList() instanceof Serializable);

        readWriteOrders();
    }

    public static void testOrderList() {
        try {
            Order order = new Order("adr"),
                    newOrder;
            for (int i = 0; i < 2; ++i)
                order.add(createItem());

//            for (int i = 0; i < 2; ++i)
//                orderList.add((Order) orderList.get(i).clone());
//
//
            File file = new File("ord.txt");
//
//            FileWriter writer = new FileWriter(file);
//            OrderList.writeList(writer, orderList, ';');
//            writer.close();

            BufferedReader reader = new BufferedReader(new FileReader(file));
            newOrder = Order.readOrder(reader, ';');
            reader.close();

            System.out.println("orderList " + Arrays.toString(newOrder.toArray()));
            newOrder = order.sortAndSaveUnique(newOrder);
            System.out.println("newOrderList " + Arrays.toString(newOrder.toArray()));

            Item item = newOrder.get(0);
            Item item2 = (Item) item.clone();

            System.out.println(item);
            System.out.println(item2);
            System.out.println("equals " + item.equals(item2));

        } catch (IOException | CloneNotSupportedException | ParseException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public static void testOrder() {
        try {
            Item r = createItem();
            File file = new File("ord.txt");

            FileWriter writer = new FileWriter(file);

            Item.writeItem(writer, r, ';');
            Item.writeItem(writer, r, ';');
            writer.close();

            FileReader reader = new FileReader(file);
            Item r1 = Item.readItem(reader, ';'),
                    r2 = Item.readItem(reader, ';');

            reader.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public static Item createItem() {
        String[] dataStr = {"1 th", "2 th", "3 th", "4 th", "5 th", "6 th", "7 th", "8 th"};
        Random rnd = new Random();
        Date data = new Date(System.currentTimeMillis()
                + 365L * 24L * 60L * 60L * 1000L + 24L * 60L * 60L * 1000L * (rnd.nextInt(15)));
        return new Item(UUID.randomUUID(),
                dataStr[rnd.nextInt(dataStr.length)],
                rnd.nextInt(100) + 1,
                rnd.nextInt(10) + 1,
                data
        );
    }

    public static Order createOrder() {
        String[] addressData = {"st.1 h1", "st.2 h2", "st.3 h3",
                "st.4 h4", "st.5 h5", "st.6 h6"};
        Random rnd = new Random();
        Order order = new Order("City " + number++ + " "
                + addressData[rnd.nextInt(addressData.length)]);

        for (int i = 0; i < rnd.nextInt(5) + 5; ++i)
            order.add(createItem());
        return order;
    }

    public static Order[] readOrders(File file) {
        Order[] ans = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int n = Integer.parseInt(reader.readLine());
            ans = new Order[n];
            for (int i = 0; i < n; ++i)
                ans[i] = Order.readOrder(reader, ';');
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            ans = null;
        }
        return ans;
    }

    public static void writeOrders(File file, Order[] orders) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(orders.length + "\n");
            for (int i = 0; i < orders.length; ++i)
                Order.writeList(writer, orders[i], ';');
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public static Object[] writeToFile() {
        int n = 10;
        Order[] orders = new Order[n];

        for (int i = 0; i < n; ++i)
            orders[i] = createOrder();
        File file = new File("orders.txt");
        try {
            System.out.println("Is file created(if not exist) " + file.getName() + " " + file.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        writeOrders(file, orders);
        System.out.println("END");
        Object[] answer = {n, file};
        return answer;
    }

    public static void readWriteOrders() {
        Object[] ans = writeToFile();
        int n = (int) ans[0];
        File file = (File) ans[1];
        Order[] orders = readOrders(file);
        System.out.println(Arrays.toString(orders));
    }
}
