package com.example.telecom.apis;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.example.telecom.util.DMLUtils;

@Path("/addtower")
public class AddTower {

	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response addTower(@FormParam("startdate") String startdate,
			@FormParam("location") String location, @FormParam("city") String city) {

		HashMap<String, String> detailsMap = new HashMap();
		detailsMap.put("startdate", startdate);
		detailsMap.put("status", "ACTIVE");
		detailsMap.put("location", location);
		detailsMap.put("city", city);

		String responseText = DMLUtils.addTower(detailsMap);

		return Response.ok(responseText.toString()).build();
	}

}