package main.model;

import com.sun.istack.internal.NotNull;
import main.model.Order;

import javax.xml.bind.annotation.*;
import java.util.*;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id","login","name","orders"})
public class Customer implements Iterable<Order> {
    @XmlElement(name = "customerID")
    private UUID id;
    @XmlElement(name = "customerName")
    private String name;
    @XmlTransient
    private String password;
    @XmlElement(name = "customerLogin")
    private String login;

    @XmlElementWrapper(name = "orderList")
    @XmlElement(name = "order")
    private List<Order> orders;

    //==================================================Constructor=========================================================
    public Customer() {
        this.name = "";
        this.login = "";
        this.password = "";
        orders = new ArrayList<>();
    }

    public Customer(@NotNull String name, @NotNull String login, @NotNull String password) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.login = login;
        this.password = password;
        orders = new ArrayList<>();
    }

//==============================================Getters=and=Setters=====================================================

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

//================================================Object=Method=========================================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Customer))
            return false;
        Customer cust = (Customer) obj;
        if (!cust.name.equals(this.name))
            return false;
        if (!cust.login.equals(this.login))
            return false;
        return cust.password.equals(this.password);
    }

    @Override
    public String toString() {
        StringBuilder orderStr = new StringBuilder();
        for(Order order:orders){
            orderStr.append("\n\t");
            orderStr.append(order.toString(1));
        }
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", login='" + login + '\'' +
                ", orders=" + orderStr.toString() +
                '}';
    }

    //================================================List=Methods==========================================================

    public int size() {
        return this.orders.size();
    }

    public boolean add(Order order) {
        return orders.add(order);
    }

    public Order get(int index) {
        return orders.get(index);
    }

    public Order set(int index, Order order) {
        return orders.set(index, order);
    }

    public void add(int index, Order order) {
        orders.add(index, order);
    }

    public Order remove(int index) {
        return orders.remove(index);
    }

    public boolean addAll(Collection<? extends Order> c) {
        return orders.addAll(c);
    }

    @Override
    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }

}
