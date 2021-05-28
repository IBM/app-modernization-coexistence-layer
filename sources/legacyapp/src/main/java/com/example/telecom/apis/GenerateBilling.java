package com.example.telecom.apis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;


@Path("/generatebill")
public class GenerateBilling {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response generateBill(@FormParam("mobile") String mobile) {
		String customerid = DMLUtils.getCustomerID(mobile);
		loadCustomerUsageData(customerid);
		DMLUtils.generateBillingData(customerid);
		return Response.ok("Generated Billing Data").build();
	}
	
	public void loadCustomerUsageData(String customerid) {
		FileInputStream inputStream = null;
		try {
			// Getting ClassLoader obj
			ClassLoader classLoader = CleanUpLegacyDB.class.getClassLoader();
			// Getting resource(File) from class loader
			File configFile = new File(classLoader.getResource("usagetemplate.csv").getFile());
			inputStream = new FileInputStream(configFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			// Read the first line with Headers
			line = reader.readLine();
			System.out.println("Reading data - " + line);
		
			System.out.println("The customer ID for mobile is" +customerid);
			while ((line = reader.readLine()) != null) {
				String[] res = line.split("[,]", 0);
				HashMap<String, String> usageMap = new HashMap();
				usageMap.put("customerid",customerid);
				usageMap.put("imei", res[0]);
				usageMap.put("towerid", res[1]);
				usageMap.put("starttime", formatDate(res[2]));
				usageMap.put("endtime", formatDate(res[3]));
				usageMap.put("usage", res[4]);

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
