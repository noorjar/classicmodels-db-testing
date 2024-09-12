package DBPackage;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import net.bytebuddy.build.Plugin.Factory.UsingReflection.Priority;

public class MainClass extends Parameters {

	@BeforeTest
	public void mysetup() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/classicmodels", "root", "noor1234");

	}

	@Test(priority = 1)
	public void OrderPlacementandPayment() throws SQLException {

		int customerNumber = getRandomValidCustomerNumber();
		String ProductCode = getRandomValidProudctCode();
		stmt = con.createStatement();

// Initialize numRowsAdded to track total rows inserted
		int numRowsAdded = 0;

// Insert into orders table

		String OrderQuery = "INSERT INTO orders (orderNumber, orderDate, requiredDate, shippedDate, status, customerNumber) VALUES ("
				+ customerNumber + ", '2024-09-11', '2024-09-18', '2024-09-12', 'In Process', " + customerNumber + ")";
		numRowsAdded += stmt.executeUpdate(OrderQuery);

// Insert into orderdetails table

		String detailsQuery = "INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) VALUES ("
				+ customerNumber + ",'" + ProductCode + "', 10, 50.0, 1)";
		numRowsAdded += stmt.executeUpdate(detailsQuery);

// Insert a Payment for the order into Payment Table

		String paymentsQuery = "INSERT INTO payments (customerNumber, checkNumber, paymentDate, amount) VALUES ("
				+ customerNumber + ", 'BO864028', '2024-12-17', 14191.12)";
		numRowsAdded += stmt.executeUpdate(paymentsQuery);
		System.out.println(numRowsAdded);
// Assert that the correct number of rows were added
		Assert.assertEquals(3, numRowsAdded,
				"Expected 3 rows to be inserted across orders, orderdetails, and payments tables");
// Verify the inserted data in Orders table
		ResultSet ordersResultSet = stmt.executeQuery("SELECT * FROM orders WHERE orderNumber = " + customerNumber);
		Assert.assertTrue(ordersResultSet.next(), "Order not found in Orders table");// to check that the order was
																						// inserted.
		Assert.assertEquals(ordersResultSet.getInt("customerNumber"), customerNumber);
	}

}
