package com.pens.rest.api.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.isecinc.pens.bean.Customer;
import com.pens.rest.api.mapping.CustomerMapping;



@Path("/customer")
public class CustomerService {

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomer() {
		Customer cust = new Customer();
		cust.setCode("P001");
		cust.setName("Pens Company");
		return cust; 
	}
	
	//Input new Customer
	@POST
	@Path("/post")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createCustomer(Customer cust) {
		String result = "Customer Create : " + cust.getCode();
		System.out.println(result);
		
		Customer custMsg = new Customer();
		custMsg.setId(999);
		custMsg.setCode(cust.getCode());
		custMsg.setName(cust.getName());
		custMsg.setStatusMessage("SUCCESS");
		
		return Response.status(201).entity(custMsg).build();
		//return custMsg;
	}
	
	//Update Customer
	@POST
	@Path("/put")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateCustomer(Customer cust) {
		String result = "Customer update : " + cust.getCode();
		System.out.println(result);
		
		Customer custMsg = new Customer();

		custMsg.setCode(cust.getCode());
		custMsg.setName(cust.getName());
		custMsg.setStatusMessage("SUCCESS");
		
		return Response.status(201).entity(custMsg).build();
		//return custMsg;
	}
	
}