package DBPackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
			System.out.println("This is random number from Customer Table = "+ number);
		}
		return number;
		 }	
	
	
	
    public  String generateRandomcheckNumber() {
        // Generate two random uppercase letters
    	char letter1 = (char) (rand.nextInt(26) + 'A'); 
    	char letter2 = (char) (rand.nextInt(26) + 'A'); 
        
        // Generate six random digits
        int digits = ThreadLocalRandom.current().nextInt(100000, 1000000); // Six digits between 100000 and 999999
        String CN = String.valueOf(letter1) + String.valueOf(letter2) + Integer.toString(digits);
		System.out.println("This Ranom Number for Check Number = "+ CN);

        // Combine the letters and digits into the desired format
        return CN ;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////
	public String getRandomValidProudctCode() throws SQLException {
		
		String Code = null ;
		stmt = con.createStatement();
		String query1 = "SELECT productCode FROM products ORDER BY RAND() LIMIT 1";	
		rs  = stmt.executeQuery(query1);
		while(rs.next()) {
			Code = rs.getString("productCode");
			System.out.println("This is random code from Proudcts table = "+ Code);

		}
		return Code ;	
	}
	
	public String generateRandomCode() {
		char letter = (char) (rand.nextInt(26) + 'A'); 
		int number1 = rand.nextInt(90) + 10;
        int number2 = rand.nextInt(10000);
        String myNewCode = letter + Integer.toString(number1) + "_" + number2;
		System.out.println("The new code that will be added in test 2 = "+ myNewCode);

        return myNewCode;
	}
	public String getRandomproductLine() throws SQLException {
		
		String productLine =null;
		stmt=con.createStatement();
		rs = stmt.executeQuery("SELECT productLine FROM productlines ORDER BY RAND() LIMIT 1");
		while(rs.next()) {
			productLine = rs.getString("productLine");
			System.out.println("product Line = " + productLine);
			
		}
		return productLine;
	}
	
}
