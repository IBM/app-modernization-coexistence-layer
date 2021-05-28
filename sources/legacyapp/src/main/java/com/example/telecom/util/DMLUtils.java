package com.example.telecom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.example.telecom.apis.SetupLegacyDB;

public class DMLUtils {
	private static final String SINGLE_QUOTE = "'";
	private static final String DOT = ".";
	private static final String SPACE = " ";
	private static final String BR = "<BR/>";
	public static final String ERROR_MSG = "An error ocuured. Please check server logs";
	private static Connection con = null;
	private static String schemaName = "";
	private static final String QUOTES = SINGLE_QUOTE;
	private static final String CLOSE_BRACKET = ")";
	private static final String COMMA = ",";

	static {
		JSONObject creds = null;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			// Get credentials
			creds = readCred();

			con = DriverManager.getConnection(creds.get("jdbcurl").toString(), creds.get("username").toString(),
					creds.get("password").toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		schemaName = creds.get("username").toString().toUpperCase();
	}

	private static JSONObject readCred() {

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

	public static String getPlanDescription(String customerid) {
		String planDescription = "";
		String planAmount = "";

		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT PLAN.COST,PLAN.DESCRIPTION FROM " + schemaName + DOT + "CUSTOMER, " + schemaName
					+ DOT + "CUSTOMER_PLAN, " + schemaName + DOT + "PLAN " + "WHERE CUSTOMER.ID = " + customerid
					+ " AND CUSTOMER_PLAN.CUSTOMER_ID = CUSTOMER.ID AND PLAN.ID=CUSTOMER_PLAN.PLAN_ID AND CUSTOMER_PLAN.END_DATE='9999-12-31'";
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);

			while (rs.next()) {
				planAmount = rs.getString("cost");
				planDescription = rs.getString("description");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Fixed Charge:" + planAmount + ",Description:" + planDescription;
	}

	public static String addCustomer(Map custDetailsMap) {
		try {
			Statement stmt = con.createStatement();

			String statement = "INSERT INTO  " + schemaName + DOT + "CUSTOMER "
					+ "(\"SALUTATION\",\"FIRST_NAME\",\"MIDDLE_NAME\",\"LAST_NAME\",\"ADDRESS\",\"E_MAIL\",\"MOBILE\")"
					+ " VALUES(" + QUOTES + custDetailsMap.get("salutation") + QUOTES + COMMA + QUOTES
					+ custDetailsMap.get("firstname") + QUOTES + COMMA + QUOTES + custDetailsMap.get("middlename")
					+ QUOTES + COMMA + QUOTES + custDetailsMap.get("lastname") + QUOTES + COMMA + QUOTES
					+ custDetailsMap.get("address") + QUOTES + COMMA + QUOTES + custDetailsMap.get("email") + QUOTES
					+ COMMA + QUOTES + custDetailsMap.get("mobile") + QUOTES + CLOSE_BRACKET;
			System.out.println(statement);
			boolean rs = false;
			rs = stmt.execute(statement);
			con.commit();
			assignPlan(custDetailsMap.get("mobile").toString(), custDetailsMap.get("plan").toString());
			return "Customer added sucessfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}

	}

	public static String addCustomerUsage(Map usageMap) {
		try {
			Statement stmt = con.createStatement();

			String statement = "INSERT INTO  " + schemaName + DOT + "CUSTOMER_USAGE_DETAIL "
					+ "(\"CUSTOMER_ID\",\"IMEI\",\"TOWER_ID\",\"START_TIME\",\"END_TIME\",\"USAGE\")" + " VALUES("
					+ QUOTES + usageMap.get("customerid") + QUOTES + COMMA + QUOTES + usageMap.get("imei") + QUOTES
					+ COMMA + QUOTES + usageMap.get("towerid") + QUOTES + COMMA + SINGLE_QUOTE
					+ usageMap.get("starttime") + SINGLE_QUOTE + COMMA + QUOTES + usageMap.get("endtime") + QUOTES
					+ COMMA + usageMap.get("usage") + CLOSE_BRACKET;
			System.out.println(statement);
			boolean rs = false;
			rs = stmt.execute(statement);
			con.commit();
			return "Customer usage added sucessfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String addPlan(Map planDetailsMap) {
		try {
			Statement stmt = con.createStatement();

			String statement = "INSERT INTO  " + schemaName + DOT + "PLAN "
					+ "(\"NAME\",\"DESCRIPTION\",\"INTRO_DATE\",\"STATUS\",\"DATA_LIMIT\",\"COST\")" + " VALUES("
					+ QUOTES + planDetailsMap.get("name") + QUOTES + COMMA + QUOTES + planDetailsMap.get("description")
					+ QUOTES + COMMA + SINGLE_QUOTE + planDetailsMap.get("introdate") + SINGLE_QUOTE + COMMA + QUOTES
					+ planDetailsMap.get("status") + QUOTES + COMMA + planDetailsMap.get("datalimit") + COMMA
					+ planDetailsMap.get("cost") + CLOSE_BRACKET;
			System.out.println(statement);
			boolean rs = false;
			rs = stmt.execute(statement);
			con.commit();
			return "Plan added sucessfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String addTower(Map towerDetailsMap) {
		try {
			Statement stmt = con.createStatement();

			String statement = "INSERT INTO  " + schemaName + DOT + "TOWER "
					+ "(\"START_DATE\",\"STATUS\",\"LOCATION\")" + " VALUES(" + QUOTES
					+ towerDetailsMap.get("startdate") + QUOTES + COMMA + QUOTES + towerDetailsMap.get("status")
					+ QUOTES + COMMA + QUOTES + towerDetailsMap.get("location") + QUOTES + CLOSE_BRACKET;
			System.out.println(statement);
			boolean rs = false;
			rs = stmt.execute(statement);
			con.commit();
			return "Tower added sucessfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String assignPlan(String mobile, String plan) {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "CUSTOMER WHERE CUSTOMER.MOBILE = " + SINGLE_QUOTE
					+ mobile + SINGLE_QUOTE;

			ResultSet rs = stmt.executeQuery(statement);
			String custid = "1";

			if (rs.next()) {
				custid = rs.getString("id");
			}
			System.out.println(statement);
			System.out.println("Customer ID is " + custid);

			statement = "SELECT * FROM " + schemaName + DOT + "PLAN " + "WHERE PLAN.NAME = " + SINGLE_QUOTE + plan
					+ SINGLE_QUOTE;
			System.out.println(statement);
			rs = stmt.executeQuery(statement);
			String planid = "1";

			if (rs.next()) {
				planid = rs.getString("id");
			}

			System.out.println("Plan ID is " + planid);

			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String currentdate = simpleDateFormat.format(new Date());
			String yesterday = simpleDateFormat.format(new Date(new Date().getTime() - 24 * 3600 * 1000));

			statement = "UPDATE " + schemaName + DOT + "CUSTOMER_PLAN " + " SET END_DATE=" + SINGLE_QUOTE + yesterday
					+ SINGLE_QUOTE + " where END_DATE='9999-12-31' AND CUSTOMER_ID = " + custid;

			System.out.println(statement);
			stmt.execute(statement);

			statement = "INSERT INTO  " + schemaName + DOT + "CUSTOMER_PLAN "
					+ "(\"CUSTOMER_ID\",\"PLAN_ID\",\"START_DATE\",\"END_DATE\")" + " VALUES(" + custid + COMMA + planid
					+ COMMA + SINGLE_QUOTE + currentdate + SINGLE_QUOTE + COMMA + SINGLE_QUOTE + "9999-12-31"
					+ SINGLE_QUOTE + CLOSE_BRACKET;
			System.out.println(statement);

			stmt.execute(statement);

			con.commit();
			return "Plan changed sucessfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String searchCustomer(String mobile) {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "CUSTOMER, " + schemaName + DOT + "CUSTOMER_PLAN, "
					+ schemaName + DOT + "PLAN " + "WHERE CUSTOMER.MOBILE = " + SINGLE_QUOTE + mobile + SINGLE_QUOTE
					+ " AND CUSTOMER_PLAN.CUSTOMER_ID = CUSTOMER.ID AND PLAN.ID=CUSTOMER_PLAN.PLAN_ID AND CUSTOMER_PLAN.END_DATE='9999-12-31'";
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append(
					"<TABLE class=\"table table-bordered\"><TR><TH>Salutation</TH><TH>First name</TH><TH>Middle name</TH><TH>Last name</TH><TH>Address</TH><TH>Mobile</TH><TH>E-mail</TH><TH>Plan</TH></TR>");

			while (rs.next()) {
				responseBuffer.append("<TR><TD>" + rs.getString("salutation") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("first_name") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("middle_name") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("last_name") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("address") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("mobile") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("e_mail") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("name") + "</TD></TR>");
			}
			responseBuffer.append("</TABLE>");

			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String getCustomerID(String mobile) {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT ID FROM " + schemaName + DOT + "CUSTOMER WHERE CUSTOMER.MOBILE = " + SINGLE_QUOTE
					+ mobile + SINGLE_QUOTE;
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();
			String id = "";
			while (rs.next()) {
				id = rs.getString("id");
			}

			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String queryBilling(String mobile) {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "CUSTOMER, " + schemaName + DOT + "BILLING "
					+ "WHERE CUSTOMER.MOBILE = " + SINGLE_QUOTE + mobile + SINGLE_QUOTE
					+ " AND BILLING.CUSTOMER_ID = CUSTOMER.ID";
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append(
					"<TABLE class=\"table table-bordered\"><TR><TH>Bill Date</TH><TH>Usage</TH><TH>Amount</TH></TR>");

			while (rs.next()) {
				responseBuffer.append("<TR><TD>" + rs.getString("bill_date") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("usage") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("amount") + "</TD></TR>");
			}
			responseBuffer.append("</TABLE>");

			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String searchPlan(String description) {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "PLAN " + "WHERE PLAN.DESCRIPTION = "
					+ description;
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();

			while (rs.next()) {
				String introdate = rs.getString("intro_date");
				String datalimit = rs.getString("data_limit");
				String cost = rs.getString("cost");

				responseBuffer.append(introdate + BR);
				responseBuffer.append(datalimit + BR);
				responseBuffer.append(cost + BR);
			}
			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String getPlanNames(String status) {
		try {
			Statement stmt = con.createStatement();
			String statement = "";

			if (status.toUpperCase().trim().contentEquals("ACTIVE"))
				statement = "SELECT NAME FROM " + schemaName + DOT + "PLAN " + "WHERE PLAN.STATUS = \'ACTIVE\'";
			else
				statement = "SELECT NAME FROM " + schemaName + DOT + "PLAN ";

			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();

			if (rs.next()) {
				String desc = rs.getString("name");
				responseBuffer.append(desc);
				if (rs.next()) {
					do {
						responseBuffer.append(COMMA);
						desc = rs.getString("name");
						responseBuffer.append(desc);
					} while (rs.next());
				}
			}
			System.out.println(responseBuffer.toString());
			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String getPlans() {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "PLAN";
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append(
					"<TABLE class=\"table table-bordered\"><TR><TH>Name</TH><TH>Description</TH><TH>Intro Date</TH><TH>Data Limit</TH><TH>Cost</TH><TH>Status</TH></TR>");

			while (rs.next()) {
				responseBuffer.append("<TR><TD>" + rs.getString("name") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("description") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("intro_date") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("data_limit") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("cost") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("status") + "</TD></TR>");
			}
			responseBuffer.append("</TABLE>");

			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String getTowers() {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "TOWER";
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append(
					"<TABLE class=\"table table-bordered\"><TR><TH>ID</TH><TH>Start Date</TH><TH>Status</TH><TH>Location</TH></TR>");

			while (rs.next()) {
				responseBuffer.append("<TR><TD>" + rs.getString("id") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("start_date") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("status") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("location") + "</TD></TR>");
			}
			responseBuffer.append("</TABLE>");

			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static String getCustomers() {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "CUSTOMER";
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();
			responseBuffer.append(
					"<TABLE class=\"table table-bordered\"><TR><TH>ID</TH><TH>SALUTATION</TH><TH>FIRST NAME</TH><TH>MIDDLE NAME</TH><TH>LAST NAME</TH><TH>ADDRESS</TH><TH>MOBILE</TH><TH>E-MAIL</TH></TR>");

			while (rs.next()) {
				responseBuffer.append("<TR><TD>" + rs.getString("id") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("salutation") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("first_name") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("middle_name") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("last_name") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("address") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("mobile") + "</TD>");
				responseBuffer.append("<TD>" + rs.getString("e_mail") + "</TD></TR>");
			}
			responseBuffer.append("</TABLE>");

			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static Map<String, CustomerUsageObject> getCustomerUsage() {
		try {

			System.out.println("Inside the getCustomerUsage method.... ");
			Statement stmt = con.createStatement();

			HashMap usageMap = new HashMap<String, CustomerUsageObject>();

			String statement = "SELECT * FROM " + schemaName + DOT + "CUSTOMER_USAGE_DETAIL";
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);

			while (rs.next()) {
				String customerId = rs.getString("CUSTOMER_ID");
				Date startTime = rs.getDate("START_TIME");
				Calendar calendar = Calendar.getInstance();
				System.out.println("Extracted start time:" + startTime);
				calendar.setTime(startTime);

				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

				Date lastDayOfMonth = calendar.getTime();

				SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");

				String strDate = formatter.format(lastDayOfMonth);
				System.out.println("Extracted the year and month:" + strDate);
				String used = rs.getString("USAGE");
				System.out.println("Used from the database: " + used);
				CustomerUsageObject useObj = new CustomerUsageObject();
				if (usageMap.containsKey(customerId)) {
					useObj = (CustomerUsageObject) usageMap.get(customerId);
				} else {
					usageMap.put(customerId, useObj);
				}
				useObj.addUsage(strDate, Integer.parseInt(used));
			}

			return usageMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, CustomerUsageObject> getCustomerUsage(String customerid) {
		try {

			System.out.println("Inside the getCustomerUsage method.... ");
			Statement stmt = con.createStatement();

			HashMap usageMap = new HashMap<String, CustomerUsageObject>();

			String statement = "SELECT * FROM " + schemaName + DOT + "CUSTOMER_USAGE_DETAIL WHERE CUSTOMER_ID = "
					+ SINGLE_QUOTE + customerid + SINGLE_QUOTE;
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);

			while (rs.next()) {
				String customerId = rs.getString("CUSTOMER_ID");
				Date startTime = rs.getDate("START_TIME");
				Calendar calendar = Calendar.getInstance();
				System.out.println("Extracted start time:" + startTime);
				calendar.setTime(startTime);

				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

				Date lastDayOfMonth = calendar.getTime();

				SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");

				String strDate = formatter.format(lastDayOfMonth);
				System.out.println("Extracted the year and month:" + strDate);
				String used = rs.getString("USAGE");
				System.out.println("Used from the database: " + used);
				CustomerUsageObject useObj = new CustomerUsageObject();
				if (usageMap.containsKey(customerId)) {
					useObj = (CustomerUsageObject) usageMap.get(customerId);
				} else {
					usageMap.put(customerId, useObj);
				}
				useObj.addUsage(strDate, Integer.parseInt(used));
			}

			return usageMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String addBilling(Map billMap) {
		try {
			Statement stmt = con.createStatement();

			String statement = "INSERT INTO  " + schemaName + DOT + "BILLING "
					+ "(\"CUSTOMER_ID\",\"BILL_DATE\",\"USAGE\",\"AMOUNT\")" + " VALUES(" + QUOTES
					+ billMap.get("customerid") + QUOTES + COMMA + QUOTES + billMap.get("billdate") + QUOTES + COMMA
					+ QUOTES + billMap.get("usage") + QUOTES + COMMA + QUOTES + billMap.get("amount") + QUOTES
					+ CLOSE_BRACKET;
			System.out.println(statement);
			boolean rs = false;
			rs = stmt.execute(statement);
			con.commit();
			return "Billing added sucessfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static void generateBillingData() {

		try {
			Map<String, CustomerUsageObject> usageMap = getCustomerUsage();
			for (Map.Entry<String, CustomerUsageObject> entry : usageMap.entrySet()) {
				String customerId = entry.getKey();
				CustomerUsageObject usageObj = entry.getValue();
				for (Map.Entry<String, Integer> usageEntry : usageObj.getMonUsage().entrySet()) {
					Map billMap = new HashMap();
					billMap.put("customerid", customerId);
					billMap.put("billdate", usageEntry.getKey());
					System.out.println("Usage - " + usageEntry.getValue());
					billMap.put("usage", usageEntry.getValue());
					String planDescription = getPlanDescription(customerId);
					billMap.put("amount", computeBillAmount(planDescription, usageEntry.getValue()));
					addBilling(billMap);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateBillingData(String customerid) {

		try {
			Map<String, CustomerUsageObject> usageMap = getCustomerUsage(customerid);
			for (Map.Entry<String, CustomerUsageObject> entry : usageMap.entrySet()) {
				String customerId = entry.getKey();
				CustomerUsageObject usageObj = entry.getValue();
				for (Map.Entry<String, Integer> usageEntry : usageObj.getMonUsage().entrySet()) {
					Map billMap = new HashMap();
					billMap.put("customerid", customerId);
					billMap.put("billdate", usageEntry.getKey());
					System.out.println("Usage - " + usageEntry.getValue());
					billMap.put("usage", usageEntry.getValue());
					String planDescription = getPlanDescription(customerId);
					billMap.put("amount", computeBillAmount(planDescription, usageEntry.getValue()));
					addBilling(billMap);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String computeBillAmount(String planDescription, Integer dataUsed) {
		String[] res = planDescription.split("[,]", 0);
		String fixedcharge = res[0].substring(res[0].indexOf(":") + 1);
		String datalimit = res[1].substring(res[1].indexOf(":") + 1, res[1].indexOf("GB"));
		String variableCharge = res[1].substring(res[1].indexOf("$") + 1, res[1].indexOf(" per"));
		System.out.println(fixedcharge + " " + datalimit + " " + variableCharge);
		Integer billamount = 0;
		if (dataUsed < Integer.valueOf(datalimit)) {
			billamount = Integer.valueOf(fixedcharge);
		} else {
			billamount = (dataUsed - Integer.valueOf(datalimit)) * Integer.valueOf(variableCharge)
					+ Integer.valueOf(fixedcharge);
		}
		return String.valueOf(billamount);
	}

	public static String searchTower(String location) {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT + "TOWER " + "WHERE PLAN.DESCRIPTION = " + location;
			System.out.println(statement);

			ResultSet rs = stmt.executeQuery(statement);
			StringBuffer responseBuffer = new StringBuffer();

			while (rs.next()) {
				String startdate = rs.getString("start_date");
				String status = rs.getString("status");

				responseBuffer.append(startdate + BR);
				responseBuffer.append(status + BR);
			}
			return responseBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage() + BR + ERROR_MSG;
		}
	}

	public static void generatePaymentData() {
		try {
			Statement stmt = con.createStatement();

			String statement = "SELECT * FROM " + schemaName + DOT
					+ "BILLING WHERE BILL_DATE <> (SELECT MAX(BILL_DATE) FROM " + schemaName + DOT + "BILLING)";
			System.out.println(statement);
			ResultSet rs = stmt.executeQuery(statement);
			ArrayList<String> billingData = new ArrayList<String>();
			while (rs.next()) {
				String customerId = rs.getString("CUSTOMER_ID");
				String billDate = rs.getString("BILL_DATE");
				String amount = rs.getString("AMOUNT");
				String data = customerId + COMMA + billDate + COMMA + amount;
				billingData.add(data);
			}

			for (String entry : billingData) {
				String arr[] = entry.split(COMMA, 0);
				statement = "INSERT INTO  " + schemaName + DOT + "PAYMENT "
						+ "(\"CUSTOMER_ID\",\"PAYMENT_DATE\",\"AMOUNT\",\"MODE\")" + " VALUES(" + QUOTES + arr[0]
						+ QUOTES + COMMA + QUOTES + arr[1] + QUOTES + COMMA + QUOTES + arr[2] + QUOTES + COMMA + QUOTES
						+ "ECS" + QUOTES + CLOSE_BRACKET;
				System.out.println(statement);
				stmt.execute(statement);
				con.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		generatePaymentData();
	}

}
