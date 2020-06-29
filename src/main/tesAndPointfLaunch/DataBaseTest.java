package main.tesAndPointfLaunch;

import main.dataBase.CustomerDML;
import main.dataBase.DataBase;
import main.model.Customer;

import java.sql.Connection;

public class DataBaseTest {
    public static void main(String[] args) {

        Connection connection = DataBase.getConnection();

        Customer mayBeNull = CustomerDML.getExistCustomer(connection,"Mus","josdvf");
        System.out.println(mayBeNull.toString());
    }
}
