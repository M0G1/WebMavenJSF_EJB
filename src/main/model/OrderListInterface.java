package main.model;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OrderListInterface extends Remote, Serializable {
    public Order sortAndSaveUnique(Order order) throws RemoteException;

}
