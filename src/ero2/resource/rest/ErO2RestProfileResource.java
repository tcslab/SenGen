package ero2.resource.rest;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.sql.Connection;


public class ErO2RestProfileResource extends ServerResource {
	
	private Connection conn;
	
	public ErO2RestProfileResource(){
		/*conn = null;
		try {
		    conn =DriverManager.getConnection("jdbc:mysql://localhost/test?" +
		                                   "user=monty&password=greatsqldb");
		   
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}*/
	}
	
	@Get()
	public String getProfile() {
		//get
		return "";
	}
	
	@Post()
	public String addProfile(){
		//decode JSON
		System.out.println("hereeee");
		System.out.println(getContext().toString());
		return "";
	}
	
}
