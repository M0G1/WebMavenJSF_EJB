package main.beans;

import main.dataBase.CustomerDML;
import main.dataBase.DataBase;
import main.model.Customer;

import javax.ejb.Stateless;
import java.sql.Connection;

@Stateless
public class CustomerEJB {
    public Customer validateUserLogin(String login, String password) {
        Connection connection = DataBase.getConnection();
        return CustomerDML.getExistCustomer(connection,login,password);
    }
}
