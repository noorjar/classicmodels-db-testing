package DBPackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Parameters {
	//WebDriver driver =  new ChromeDriver();
	Connection con;
	Statement stmt;
	ResultSet rs;
	Random rand = new Random();
	int num1 = rand.nextInt(33333);
	
	public int  getRandomValidCustomerNumber() throws SQLException
	{
		int number =0;
		stmt= con.createStatement();
		String query = "SELECT customerNumber FROM customers ORDER BY RAND() LIMIT 1";	
		rs  = stmt.executeQuery(query);
		while(rs.next()) {
			 number = rs.getInt("customerNumber");
			System.out.println(number);
		}
		return number;
		 }	
	public String getRandomValidProudctCode() throws SQLException {
		
		String Code = null ;
		stmt = con.createStatement();
		String query1 = "SELECT productCode FROM products ORDER BY RAND() LIMIT 1";	
		rs  = stmt.executeQuery(query1);
		while(rs.next()) {
			Code = rs.getString("productCode");
			System.out.println(Code);

		}
		return Code ;	
	}
}
