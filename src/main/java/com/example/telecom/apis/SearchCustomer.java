package com.example.telecom.apis;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;

@Path("/searchcustomer")
public class SearchCustomer {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response searchCustomer(@FormParam("mobile") String mobile) {
		String responseText = DMLUtils.searchCustomer(mobile);
		return Response.ok(responseText.toString()).build();
	}

}
