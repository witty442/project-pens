package com.pens.rest.api.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.isecinc.pens.bean.Customer;

public class CustomerAPIClient {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		//GET
		//new CustomerAPIClient().getCustomer();
		
		//POST 
		
		Customer cust = new Customer();
		cust.setCode("P002");
		cust.setName("P002 Name");
		new CustomerAPIClient().createCustomer(cust);
	}
	private Customer getCustomer() {
		Customer cust = null;
        HttpURLConnection conn = null;
		try {

			//dd-server test
			//URL url = new URL("http://192.168.202.7:8082/pensso/service/customer/get");
			
			URL url = new URL("http://localhost:8080/pensso/service/customer/get");
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			System.out.println("conn response:"+conn.getResponseCode());
			if (conn.getResponseCode() != 200) {
				//throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String jsonResultData = "";
			while ((output = br.readLine()) != null) {
				jsonResultData +=output;
			}
			logger.debug("jsonResultData:\n "+jsonResultData);
			
			Gson g = new Gson();
			Customer p = g.fromJson(jsonResultData, Customer.class);
			
			logger.debug("Result Customer Code:"+p.getCode());
			
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally {
			if(conn != null) {
				conn.disconnect();conn=null;
			}
		}
		return cust;
	}
	
	private Customer createCustomer(Customer cust) {
        HttpURLConnection conn = null;
    	Gson gson = new Gson();
		try {

			//dd-server test
			//URL url = new URL("http://192.168.202.7:8082/pensso/service/customer/post");
			
			URL url = new URL("http://localhost:8080/pensso/service/customer/post");
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			//"{\"customerCode\":\"Witty Company\"}";
			String custInputJson = gson.toJson(cust);
			logger.debug("input cust json data : \n"+custInputJson);
			
			OutputStream os = conn.getOutputStream();
			os.write(custInputJson.getBytes());
			os.flush();

			logger.debug("conn response:"+conn.getResponseCode());
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			//get return message
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String jsonResultData = "";
			while ((output = br.readLine()) != null) {
				jsonResultData +=output;
			}
			logger.debug("jsonResultData:\n "+jsonResultData);
			
			Customer p = gson.fromJson(jsonResultData, Customer.class);
			logger.debug("Result Customer id["+p.getId()+"] Code["+p.getCode()+"]");
			
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally {
			if(conn != null) {
				conn.disconnect();conn=null;
			}
		}
		return cust;
	}
}
