package main.beans;

import main.model.Customer;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class CustomerBean {
    private static final String USER_FOUND = "found";

    @EJB
    private final CustomerEJB customerEJB;
    private Customer customer;

    private boolean error = false;

    private String login;
    private String password;

//===============================================Constructor============================================================

    public CustomerBean() {
        customerEJB = new CustomerEJB();
    }

//===============================================Getter=Setter==========================================================

    public Customer getCustomer() {
        return customer;
    }

    public String getPassword() {
        return password;
    }


    public boolean isError() {
        return error;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//===================================================Methods============================================================

    public String validateUserLogin() {

        customer = customerEJB.validateUserLogin(login, password);

        if (customer == null) {
            error = true;
            return null;
        } else {
            error = false;
            return USER_FOUND;
        }
    }

    public void debag(String s){
        System.out.println(s);
    }

}
