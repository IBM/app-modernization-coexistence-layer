package com.example.telecom.apis;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;

@Path("/querybilling")
public class QueryBilling {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response getTowers(@FormParam("mobile") String mobile) {
		String responseText = DMLUtils.queryBilling(mobile);
		return Response.ok(responseText.toString()).build();
	}

}
