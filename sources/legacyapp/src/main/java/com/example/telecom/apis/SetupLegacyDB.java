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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.example.telecom.util.DMLUtils;

@Path("/setup-legacy-db")
public class SetupLegacyDB {

	@GET
	public Response getSetupDB() {
		Connection con = null;
		StringBuffer responseText = new StringBuffer();
		try {

			Class.forName("com.ibm.db2.jcc.DB2Driver");
			// Get credentials
			JSONObject creds = readCred();
			con = DriverManager.getConnection(creds.get("jdbcurl").toString(), creds.get("username").toString(),
					creds.get("password").toString());
			String schemaName = creds.get("username").toString();

			// Read DDL file
			List<String> ddlStatements = getDDLStatements();
			Statement stmt = con.createStatement();
			int rs = 1;
			responseText.append("<h2>Database setup</h2>");
			// Execute a query and generate a ResultSet instance
			for (String statement : ddlStatements) {
				System.out.println(statement);
				rs = stmt.executeUpdate(statement);
				if (rs == 0) {
					responseText
							.append("Table " + statement.substring(13, statement.indexOf("(")) + " created!" + "<br/>");
				}
				con.commit();
			}
			responseText.append("<br/><h2>Loading sample data into the database</h2>");
			loadTowerData();
			responseText.append("Sample data put in Table TOWER successfully!<br/>");
			loadPlanData();
			responseText.append("Sample data put in Table PLAN successfully!<br/>");
			loadCustomerData();
			responseText.append("Sample data put in Table CUSTOMER successfully!<br/>");
			loadCustomerUsageData();
			responseText.append("Sample data put in Table CUSTOMER_USAGE_DETAIL successfully!<br/>");
			generateBillingData();
			responseText.append("Sample data put in Table BILLING successfully!<br/>");
			generatePaymentData();
			responseText.append("Sample data put in Table PAYMENT successfully!<br/>");

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
	
	public void generateBillingData() {
		DMLUtils.generateBillingData();
	}
	
	public void generatePaymentData() {
		DMLUtils.generatePaymentData();
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
			ClassLoader classLoader = SetupLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("legacy-tables.ddl").getFile());
			inputStream = new FileInputStream(configFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			StringBuffer statementBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				statementBuffer.append(line);
				if (line.contains(";")) {
					String tempStr = statementBuffer.toString();
					tempStr = tempStr.replaceAll(System.getProperty("line.separator"), " ");
					tempStr = tempStr.replaceAll(";", " ");
					statements.add(tempStr);
					statementBuffer = new StringBuffer();
				}
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

	public void loadPlanData() {
		FileInputStream inputStream = null;
		try {
			// Getting ClassLoader obj
			ClassLoader classLoader = CleanUpLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("plans.csv").getFile());
			inputStream = new FileInputStream(configFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			// Read the first line with Headers
			line = reader.readLine();
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String introdate = simpleDateFormat.format(new Date(new Date().getTime() - 150 * 24 * 3600 * 1000));

			System.out.println("Reading data - " + line);
			while ((line = reader.readLine()) != null) {
				String[] res = line.split("[,]", 0);
				HashMap<String, String> planDetailsMap = new HashMap();
				planDetailsMap.put("name", res[0]);
				planDetailsMap.put("description", res[3]);
				planDetailsMap.put("datalimit", res[1]);
				planDetailsMap.put("introdate", introdate);
				planDetailsMap.put("status", "ACTIVE");
				planDetailsMap.put("cost", res[2]);
				String responseText = DMLUtils.addPlan(planDetailsMap);
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

	}

	public void loadTowerData() {
		FileInputStream inputStream = null;
		try {
			// Getting ClassLoader obj
			ClassLoader classLoader = CleanUpLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("towers.csv").getFile());
			inputStream = new FileInputStream(configFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			// Read the first line with Headers
			line = reader.readLine();
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String introdate = simpleDateFormat.format(new Date(new Date().getTime() - 150 * 24 * 3600 * 1000));

			System.out.println("Reading data - " + line);
			while ((line = reader.readLine()) != null) {
				String[] res = line.split("[,]", 0);
				HashMap<String, String> towerDetailsMap = new HashMap();
				towerDetailsMap.put("startdate", res[0]);
				towerDetailsMap.put("status", res[1]);
				towerDetailsMap.put("location", res[2]);

				String responseText = DMLUtils.addTower(towerDetailsMap);
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

	}

	public void loadCustomerData() {
		FileInputStream inputStream = null;
		try {
			// Getting ClassLoader obj
			ClassLoader classLoader = CleanUpLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("customers.csv").getFile());
			inputStream = new FileInputStream(configFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			// Read the first line with Headers
			line = reader.readLine();
			System.out.println("Reading data - " + line);
			while ((line = reader.readLine()) != null) {
				String[] res = line.split("[,]", 0);
				HashMap<String, String> custDetailsMap = new HashMap();
				custDetailsMap.put("salutation", res[0]);
				custDetailsMap.put("firstname", res[1]);
				custDetailsMap.put("middlename", res[2]);
				custDetailsMap.put("lastname", res[3]);
				custDetailsMap.put("address", res[4]);
				custDetailsMap.put("mobile", res[5]);
				custDetailsMap.put("email", res[6]);
				custDetailsMap.put("plan", res[7]);

				String responseText = DMLUtils.addCustomer(custDetailsMap);
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

	}

	public void loadCustomerUsageData() {
		FileInputStream inputStream = null;
		try {
			// Getting ClassLoader obj
			ClassLoader classLoader = CleanUpLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("customers-usage-detail.csv").getFile());
			inputStream = new FileInputStream(configFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			// Read the first line with Headers
			line = reader.readLine();
			System.out.println("Reading data - " + line);

			while ((line = reader.readLine()) != null) {
				String[] res = line.split("[,]", 0);
				HashMap<String, String> usageMap = new HashMap();
				usageMap.put("customerid", res[0]);
				usageMap.put("imei", res[1]);
				usageMap.put("towerid", res[2]);
				usageMap.put("starttime", formatDate(res[3]));
				usageMap.put("endtime", formatDate(res[4]));
				usageMap.put("usage", res[5]);

				String responseText = DMLUtils.addCustomerUsage(usageMap);
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

	}
	
	private String formatDate(String datetimeStr) {
		String pattern = "yyyy-MM";
		String resDate = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        
		Date mydate = new Date();
		
		if (datetimeStr.contains("<YYYY>-<MM>"))
		{
		  cal.add(Calendar.MONTH, -1);
		  mydate = cal.getTime();
		  String reqdate = simpleDateFormat.format(mydate);
		  resDate = datetimeStr.replace("<YYYY>-<MM>", reqdate);
		
		}
		else if (datetimeStr.contains("<YYYY>-<MM-1>"))
		{
		  cal.add(Calendar.MONTH, -2);
		  mydate = cal.getTime();
		  String reqdate = simpleDateFormat.format(mydate);
		  resDate = datetimeStr.replace("<YYYY>-<MM-1>", reqdate);
		}
		
		else if (datetimeStr.contains("<YYYY>-<MM-2>"))
		{
		  cal.add(Calendar.MONTH, -3);
		  mydate = cal.getTime();
		  String reqdate = simpleDateFormat.format(mydate);
		  resDate = datetimeStr.replace("<YYYY>-<MM-2>", reqdate);  
		}
		System.out.println("Input date:" + datetimeStr);
		System.out.println("Changed date:" + resDate);
		return resDate;
	}

}
