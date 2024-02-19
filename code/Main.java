package code;

import rs.etf.sab.operations.*;
import org.junit.Test;
import code.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

import java.util.Calendar;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new _ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new _BuyerOperations();
        CityOperations cityOperations = new _CityOperations();
        GeneralOperations generalOperations = new _GeneralOperations();
        OrderOperations orderOperations = new _OrderOperations();
        ShopOperations shopOperations = new _ShopOperations();
        TransactionOperations transactionOperations = new _TransactionOperations();

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
