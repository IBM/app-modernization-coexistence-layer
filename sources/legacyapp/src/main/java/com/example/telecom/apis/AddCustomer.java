package com.example.telecom.apis;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;


@Path("/addcustomer")
public class AddCustomer {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response addCustomer(@FormParam("salutation") String salutation, @FormParam("firstname") String firstname,
			@FormParam("middlename") String middlename, @FormParam("lastname") String lastname,
			@FormParam("address") String address, @FormParam("mobile") String mobile, @FormParam("plan") String plan,
			@FormParam("email") String email) {

		HashMap<String, String> custDetailsMap = new HashMap();
		custDetailsMap.put("salutation", salutation);
		custDetailsMap.put("firstname", firstname);
		custDetailsMap.put("middlename", middlename);
		custDetailsMap.put("lastname", lastname);
		custDetailsMap.put("address", address);
		custDetailsMap.put("mobile", mobile);
		custDetailsMap.put("email", email);
		custDetailsMap.put("plan", plan);

		String responseText = DMLUtils.addCustomer(custDetailsMap);

		return Response.ok(responseText.toString()).build();
	}

}