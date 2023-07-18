package com.sk.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomerRegistrationServlet extends HttpServlet {
	
	private static final String REG_QUERY = "INSERT INTO CUSTOMER_REGISTRATION VALUES(CUSTOMER_SEQ.NEXTVAL,?,?,?,?)";
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		//get PrintWriter 
		PrintWriter pw = res.getWriter();
		
		//set response content type
		res.setContentType("text/html");
		
		//read form data received from the request
		String name = req.getParameter("name");
		name = name.toUpperCase();
		String email = req.getParameter("email");
		email = email.toLowerCase();
		String address = req.getParameter("address");
		long phone = Long.parseLong(req.getParameter("phone"));
		
		//get Pooled JDBC Connection Object
		try(Connection con = getPooledConnection();
				PreparedStatement ps = con.prepareStatement(REG_QUERY);
				) {
			
			if(ps != null) {
				ps.setString(1, name);
				ps.setString(2, email);
				ps.setString(3, address);
				ps.setLong(4, phone);
				
				//execute the Query
				int result = ps.executeUpdate();
				
				//process the result
				if(result == 1)
					pw.println("<h1 style='color:green; text-align:center;'>User Registered</h1>");
				else
					pw.println("<h1 style='color:red; text-align:center;'>User Not Registered</h1>");
			}//if
			
			//add Home hyperlink
			pw.println("<br><br><a href='cust_reg.html'>Home</a>");
			
			//close the stream
			pw.close();
			
		}//try1
		catch(SQLException se) {
			se.printStackTrace();
			pw.println("<h1 style='color:red; text-align:center;'>Internal Problem Try Again::  "+se.getMessage()+"</h1>");
			//add Home hyperlink
			pw.println("<br><br><a href='cust_reg.html'>Home</a>");
		}
		catch(Exception e) {
			e.printStackTrace();
			pw.println("<h1 style='color:red; text-align:center;'>Internal Problem Try Again::  "+e.getMessage()+"</h1>");
			//add Home hyperlink
			pw.println("<br><br><a href='cust_reg.html'>Home</a>");
		}
	}//doGet
	
	//helper method
	private Connection getPooledConnection() throws Exception {
		//create initialContext object
		InitialContext ic = new InitialContext();
		
		//get DataSource object ref through lookup operation
		//DataSource ds = (DataSource)ic.lookup("DSJndi"); //For GlassFish Server
		
		//DataSource ds = (DataSource)ic.lookup("java:/comp/env/DSJndi"); //hard coding (Also usable)
		
		//get DsJndi name from Servlet init-param
		String jndiName = getServletConfig().getInitParameter("jndi");
		DataSource ds = (DataSource)ic.lookup(jndiName);
		
		//get Pooled JDBC Connection 
		Connection con = ds.getConnection();
		
		return con;
	}//getPooledConnection
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}//doPost
}
