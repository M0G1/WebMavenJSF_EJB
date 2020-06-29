package main.tesAndPointfLaunch;

import main.model.Order;
import main.model.OrderListInterface;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.util.Arrays;

public class ClientLab1 {
    //source https://javarush.ru/groups/posts/2283-rmi-praktika-ispoljhzovanija

    public static final String UNIQUE_BINDING_NAME = "server.OrderListInterface";

    //сначала откуда, а потом куда
    public static void main(String[] args) {

        FileWriter writer;
        BufferedReader reader;
        try {
            //System.out.print;
            reader = new BufferedReader(new FileReader(new File(args[0])));
            writer = new FileWriter(new File(args[1]));

            try {
                // получаем доступ к регистру удаленных объектов.
                final Registry registry = LocateRegistry.getRegistry("127.0.0.1", 8080);
                /*
                    Получаем из регистра нужный объект
                    Работа RMI основана на использовании прокси,
                    поэтому удаленный вызов доступен только для методов интерфейсов,
                    а не классов.*/

                OrderListInterface server = (OrderListInterface) registry.lookup(UNIQUE_BINDING_NAME);

                //считываем
                Order readOrder = Order.readOrder(reader, ';');

                writer.write("OrderList before: " + Arrays.toString(readOrder.toArray()) + "\n");

                //передаем на сервер и урудаленно вызов метода на сервере
                Order ansOfServer = server.sortAndSaveUnique(readOrder);

                System.out.println("ansOfServer " + Arrays.toString(readOrder.toArray()));
                System.out.println("ansOfServer " + Arrays.toString(ansOfServer.toArray()));
                //записываем в файл тут)
                Order.writeList(writer, ansOfServer, ';');

            } catch (RemoteException e) {
                writer.write("Exception: " + e.getMessage());
            } catch (NotBoundException e) {
                writer.write("Object under name " + UNIQUE_BINDING_NAME + " is not binded\n");
                writer.write(e.getMessage());
            } catch (ParseException e) {
                writer.write("Can not read the OrderList from " + args[0]);
                writer.write(e.getMessage());
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

}
