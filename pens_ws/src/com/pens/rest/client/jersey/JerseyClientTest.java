package com.pens.rest.client.jersey;

import com.owlike.genson.Genson;
import com.pens.rest.Customer;
import com.pens.rest.CustomerMessage;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JerseyClientTest {

	public static void main(String[] args) {
		//testGet();
		testPost();
	}
	
	public static void testGet() {
		String url = "";
		try {
			System.out.println("Test Call Get Service");
			
			url ="http://localhost:8080/pens_ws/service/customer/get";
			//url ="http://localhost:8080/pensws/service/hello/Test";
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

			System.out.println("response message:"+response.getStatus());
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			Customer output = response.getEntity(Customer.class);

			System.out.println("Output from Server ....");
			System.out.println("customerCode:"+output.getCustomerCode());
			System.out.println("customerName:"+output.getCustomerName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testPost() {
		String url = "";
		String inputJson = "";//"{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";
		try {
			System.out.println("Test Call Post Service");
			
			//prepare Customer
			Customer cust = new Customer();
			cust.setCustomerCode("P001");
			cust.setCustomerName("Pens Company");
			//convert to json string
			Genson genson = new Genson();
		    inputJson = genson.serialize(cust);
		    System.out.println("inputJson:"+inputJson);
		    
			url ="http://192.168.202.7:8082/pens_ws/service/customer/send";
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			//Pass By object
			ClientResponse response = webResource.accept("application/json").type("application/json").post(ClientResponse.class,cust);
			//Pass By json str not work
			//ClientResponse response = webResource.accept("application/json").type("application/json").post(ClientResponse.class,inputJson);

			System.out.println("response message:"+response.getStatus());
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			/*String output = response.getEntity(String.class);
			System.out.println("response from Server ...."+output);*/
			
			CustomerMessage output = response.getEntity(CustomerMessage.class);

			System.out.println("response from Server ....");
			System.out.println("->customerCode:"+output.getCustomerCode());
			System.out.println("->customerName:"+output.getCustomerName());
			System.out.println("->status:"+output.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}