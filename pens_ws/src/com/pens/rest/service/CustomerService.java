package com.pens.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pens.rest.Customer;
import com.pens.rest.CustomerMessage;


@Path("/customer")
public class CustomerService {

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomerInJSON() {
		Customer cust = new Customer();
		cust.setCustomerCode("P001");
		cust.setCustomerName("Pens Company");
		return cust; 
	}
	
	//Input class Customer
	@POST
	@Path("/send")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTrackInJSON(Customer cust) {
		String result = "Customer saved : " + cust.getCustomerCode();
		System.out.println(result);
		
		CustomerMessage custMsg = new CustomerMessage();
		custMsg.setCustomerCode(cust.getCustomerCode());
		custMsg.setCustomerName(cust.getCustomerName());
		custMsg.setStatus("SUCCESS");
		
		return Response.status(201).entity(custMsg).build();
		//return custMsg;
	}
	
	//input json string {customerCode:Pens,customerName:pennn}
	@POST
	@Path("/send2")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTrackInJSON2(Customer cust) {
		String result = "Customer saved : " + cust.getCustomerCode();
		System.out.println(result);
		
		CustomerMessage custMsg = new CustomerMessage();
		custMsg.setCustomerCode(cust.getCustomerCode());
		custMsg.setCustomerName(cust.getCustomerName());
		custMsg.setStatus("SUCCESS");
		
		return Response.status(201).entity(custMsg).build();
		//return custMsg;
	}
}