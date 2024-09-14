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
		System.out.println("############ OrderPlacementandPayment Test :############");
		int customerNumber = getRandomValidCustomerNumber();
		String ProductCode = getRandomValidProudctCode();
		String CheckNumber = generateRandomcheckNumber();
		stmt = con.createStatement();

// Initialize numRowsAdded to track total rows inserted
		int numRowsAdded = 0;

// Insert into orders table
		//System.out.println("This is an orderNumber for new order = " + num1);
		String OrderQuery = "INSERT INTO orders (orderNumber, orderDate, requiredDate, shippedDate, status, customerNumber) VALUES ("
				+ num1 + ", '2024-09-11', '2024-09-18', '2024-09-12', 'In Process', " + customerNumber + ")";
		numRowsAdded += stmt.executeUpdate(OrderQuery);

// Insert into orderdetails table

		String detailsQuery = "INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) VALUES ("
				+ num1 + ",'" + ProductCode + "', 10, 50.0, 1)";
		numRowsAdded += stmt.executeUpdate(detailsQuery);

// Insert a Payment for the order into Payment Table

		String paymentsQuery = "INSERT INTO payments (customerNumber, checkNumber, paymentDate, amount) VALUES ("
				+ customerNumber + ", '" + CheckNumber + "', '2024-12-17', 14191.12)";

		numRowsAdded += stmt.executeUpdate(paymentsQuery);
		System.out.println("Number of rows added in the first test = " + numRowsAdded);

// Assert that the correct number of rows were added
		Assert.assertEquals(3, numRowsAdded,
				"Expected 3 rows to be inserted across orders, orderdetails, and payments tables");
// Verify the inserted data in Orders table
		ResultSet ordersResultSet = stmt.executeQuery("SELECT * FROM orders WHERE orderNumber = " + num1);
		Assert.assertTrue(ordersResultSet.next(), "Order not found in Orders table");// to check that the order was
																						// inserted.
		Assert.assertEquals(ordersResultSet.getInt("customerNumber"), customerNumber);
	}

	@Test(priority = 2)
	public void ProductManagement() throws SQLException {
		System.out.println("############ Product Management Test : ############");
		String myProductCode = generateRandomCode();
		String myProductLine = getRandomproductLine();
		String insertProductQuery = "INSERT INTO products (productCode, productName, productLine, productScale, productVendor, productDescription, quantityInStock, buyPrice, MSRP) VALUES ('"
				+ myProductCode + "', '1969 Harley Davidson Ultimate Chopper', '" + myProductLine
				+ "', '1:10', 'Min Lin Diecast', 'This is the ultimate Harley Davidson chopper.', 7933, 48.81, 95.70)";
		String updateProductQuery = "UPDATE products SET quantityInStock = 6000, buyPrice = 55 WHERE productCode = '"
				+ myProductCode + "'";
		String DeleteProductQuery = "DELETE FROM products WHERE productCode = '" + myProductCode + "'";

		stmt = con.createStatement();

		int numberOfRowsAdded = stmt.executeUpdate(insertProductQuery);
		System.out.println(numberOfRowsAdded + "rows has been added");

		int numberOfRowsUpdated = stmt.executeUpdate(updateProductQuery);
		System.out.println(numberOfRowsUpdated + "rows has been updated");

		int numberOfRowsDeleted = stmt.executeUpdate(DeleteProductQuery);
		System.out.println(numberOfRowsDeleted + "rows has been deleted");

	}

	@Test(priority = 3)
	public void ReferentialIntegrityTest() throws SQLException {
		System.out.println("############ Referential Integrity Test : ############");
		stmt = con.createStatement();
		String deleteCustomerQuery = "DELETE FROM customers WHERE customerNumber = 319";

		try {
			// Try to delete the customer
			int rowsAffected = stmt.executeUpdate(deleteCustomerQuery);

			// If no exception was thrown, the deletion succeeded, which means referential
			// integrity failed
			Assert.fail("Customer was deleted despite having pending orders.");

		} catch (SQLException e) {
			// Check if the exception is due to referential integrity (constraint violation)
			int errorCode = e.getErrorCode(); // MySQL error code for foreign key violation is typically 1451

			// Assert the SQL state and error code (adjust based on your database system)
			Assert.assertTrue(errorCode == 1451, "Expected error code for foreign key constraint violation");

			// Additional message to confirm the correct behavior
			System.out.println("Referential integrity maintained: Customer cannot be deleted due to pending orders.");
		}

		String deleteProductrQuery = "delete from products where productCode = 'S12_1666'";

		try {
			// Try to delete the customer
			int rowsAffected2 = stmt.executeUpdate(deleteProductrQuery);

			// If no exception was thrown, the deletion succeeded, which means referential
			// integrity failed
			Assert.fail("Product was deleted despite it is ordered.");

		} catch (SQLException e) {
			// Check if the exception is due to referential integrity (constraint violation)
			int errorCode1 = e.getErrorCode(); // MySQL error code for foreign key violation is typically 1451

			// Assert the SQL state and error code (adjust based on your database system)
			Assert.assertTrue(errorCode1 == 1451, "Expected error code for foreign key constraint violation");

			// Additional message to confirm the correct behavior
			System.out.println("Referential integrity maintained: The product is not deleted because it is ordered.");
		}
	}

	@Test(priority = 4)
	public void OrderRollback() throws SQLException {
		System.out.println("###########Order Rollback Test :############");
		try {
			con.setAutoCommit(false); // Begin transaction
			stmt = con.createStatement();

			// Insert into orders and orderdetails
			stmt.executeUpdate(
					"INSERT INTO orders (orderNumber, orderDate, requiredDate, status, comments, customerNumber) "
							+ "VALUES (101271, '2024-09-14', '2024-09-21', 'Pending', null, 103)");
			stmt.executeUpdate(
					"INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) "
							+ "VALUES (101271, 'S700_1691', 20, 80, 1)");

			// Simulate payment failure with invalid amount
			stmt.executeUpdate("INSERT INTO payments (customerNumber, checkNumber, paymentDate, amount) "
					+ "VALUES (103, 'CHK12345', '2024-09-14', 'INVALID_AMOUNT')");

			con.commit(); // Commit if no exception

		} catch (SQLException e) {
			con.rollback(); // Rollback on error
			System.out.println("Transaction rolled back due to payment failure.");
		}

	}

	@Test(priority = 5)
	public void PaymentAmountConsistency() throws SQLException {
		stmt = con.createStatement();
		   
		        // Query to get the total value of all orders for the customer
		        String totalOrderValueQuery = "SELECT SUM(od.quantityOrdered * od.priceEach) AS total_order_value " +
		                                      "FROM orderdetails od " +
		                                      "JOIN orders o ON od.orderNumber = o.orderNumber " +
		                                      "WHERE o.customerNumber = 353;";
		        rs = stmt.executeQuery(totalOrderValueQuery);
		        double totalOrderValue = 0;
		        if (rs.next()) {
		            totalOrderValue = rs.getDouble("total_order_value");
		        }
		        rs.close(); // Close the ResultSet before executing the next query

		        // Query to get the total amount paid by the customer
		        String totalAmountPaidQuery = "SELECT SUM(amount) AS total_amount_paid " +
		                                      "FROM payments " +
		                                      "WHERE customerNumber = 353;";
		        rs = stmt.executeQuery(totalAmountPaidQuery);
		        double totalAmountPaid = 0;
		        if (rs.next()) {
		            totalAmountPaid = rs.getDouble("total_amount_paid");
		        }

		        // Assert that the total order value matches the total amount paid
		        Assert.assertEquals(totalOrderValue, totalAmountPaid, "Total amount paid does not match the total order value.");
	}
	}
	

