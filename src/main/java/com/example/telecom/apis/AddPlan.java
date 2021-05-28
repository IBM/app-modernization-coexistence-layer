package com.example.telecom.apis;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;

@Path("/addplan")
public class AddPlan {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response addCustomer(@FormParam("name") String name,@FormParam("description") String description, @FormParam("introdate") String introdate,
			@FormParam("datalimit") String datalimit, @FormParam("cost") String cost, @FormParam("status") String status) {
	
		HashMap<String, String> planDetailsMap = new HashMap();
		planDetailsMap.put("name", name);
		planDetailsMap.put("description", description);
		planDetailsMap.put("introdate", introdate);
		planDetailsMap.put("datalimit", datalimit);
		planDetailsMap.put("status", status);
		planDetailsMap.put("cost", cost);

		String responseText = DMLUtils.addPlan(planDetailsMap);

		return Response.ok(responseText.toString()).build();
	}

}