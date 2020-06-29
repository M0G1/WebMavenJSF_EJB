package main.tesAndPointfLaunch;

import main.model.Order;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerLab1 {
    //source https://javarush.ru/groups/posts/2283-rmi-praktika-ispoljhzovanija

    //По этому имени программа-клиент сможет найти наш сервер
    public static final String UNIQUE_BINDING_NAME = "server.OrderListInterface";

    public static void main(String[] args) {

            try {
                Order list = new Order();
                //Registry — реестр удаленных объектов. По порту будет лежать  наш объект
                final Registry registry = LocateRegistry.createRegistry(8080);
                //заглушка - инкапсулирует внутри себя весь процесс удаленного вызова.
                /*
                Принимает всю информацию об удаленном вызове какого-то метода.

                Если у метода есть параметры, заглушка десериализует их.
                Параметры, которые ты передаешь методам для удаленного вызова, должны быть сериализуемыми
                (ведь они будут передаваться по сети).

                После этого она вызывает нужный метод.
                */

                Remote stub = UnicastRemoteObject.exportObject(list,0);
                /*Мы «регистрируем» нашу заглушку в реестре удаленных объектов под  именем UNIQUE_BINDING_NAME.
                  Теперь клиент сможет ее найти!
                */
                registry.bind(UNIQUE_BINDING_NAME,list);
                //Thread.sleep(Integer.MAX_VALUE);
            }
            catch (/*InterruptedException|*/ AlreadyBoundException | RemoteException e){
                e.printStackTrace();
                System.err.println(e.getMessage());
            }

    }
}
