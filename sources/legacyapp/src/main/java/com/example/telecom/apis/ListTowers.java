package com.example.telecom.apis;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;

@Path("/listtowers")
public class ListTowers {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response getTowers() {
		String responseText = DMLUtils.getTowers();
		return Response.ok(responseText.toString()).build();
	}

}
