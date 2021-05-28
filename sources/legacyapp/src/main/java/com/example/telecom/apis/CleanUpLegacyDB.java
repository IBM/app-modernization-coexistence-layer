package com.example.telecom.apis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
// JDBC base
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


@Path("/cleanup-legacy-db")
public class CleanUpLegacyDB {

	@GET
	public Response cleanUpDB() {
		Connection con = null;
		StringBuffer responseText = new StringBuffer();
		try {

			Class.forName("com.ibm.db2.jcc.DB2Driver");
			// Get credentials
			JSONObject creds = readCred();
			con = DriverManager.getConnection(creds.get("jdbcurl").toString(), creds.get("username").toString(), creds.get("password").toString());
            String schemaName = creds.get("username").toString();
			
            // Read DDL file
            List<String> ddlStatements = getDDLStatements();
            Statement stmt = con.createStatement();
            int rs = 1;
            responseText.append("<h2>Database cleanup</h2>");
			// Execute a query and generate a ResultSet instance
            for (String statement: ddlStatements) {
            	System.out.println(statement);
            	rs = stmt.executeUpdate(statement);
            	if (rs==0) {
            		responseText.append("Table " + statement.substring(11) + " dropped!" + "<br/>");
            	}
            	con.commit();
            }

		} catch (Exception e) {
			e.printStackTrace();
			responseText.append(e.getMessage());
			return Response.ok(responseText.toString()).build();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return Response.ok(responseText.toString()).build();
	}

	public JSONObject readCred() {

		FileInputStream inputStream = null;
		// JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();
		try {
			// Getting ClassLoader obj
			ClassLoader classLoader = SetupLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("credentials-db2.json").getFile());
			inputStream = new FileInputStream(configFile);
			FileReader reader = new FileReader(configFile);
			JSONObject obj = (JSONObject) jsonParser.parse(reader);
			return obj;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}
	
	public List getDDLStatements() {
		FileInputStream inputStream = null;
		ArrayList<String> statements = new ArrayList<String>();
		try {
			// Getting ClassLoader obj
			ClassLoader classLoader = CleanUpLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("legacy-tables-drop.ddl").getFile());
			inputStream = new FileInputStream(configFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {	
					statements.add(line);	
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return statements;
	}
}
;