package com.example.telecom.apis;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;


@Path("/changeplan")
public class AssignPlan {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response assignPlan(@FormParam("mobile") String mobile, @FormParam("plan") String plan) {
		String responseText = DMLUtils.assignPlan(mobile,plan);
		return Response.ok(responseText.toString()).build();
	}

}
