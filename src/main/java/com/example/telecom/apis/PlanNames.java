package com.example.telecom.apis;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;


@Path("/get-plan-names")
public class PlanNames {

	@GET
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response getPlanNames(@FormParam("status") String status) {
		String responseText = DMLUtils.getPlanNames(status);
		return Response.ok(responseText.toString()).build();
	}

}
