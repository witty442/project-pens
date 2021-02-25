package com.pens.rest.api.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Invoice;
import com.isecinc.pens.model.MInvoice;
import com.pens.rest.api.mapping.InvoiceAPIBean;
import com.pens.util.EnvProperties;
import com.sun.jersey.core.util.Base64;

import sun.misc.BASE64Encoder;
public class InvoiceAPIClient {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		String invoiceIdArr = "";
        try {
			//Get invoice is no export 
			List<Invoice>  invoiceList = new InvoiceAPIClient().getInvoiceListToSalesApp();
			
			if(invoiceList != null && invoiceList.size() >0){
				//insert invoice to DB
				boolean r = new MInvoice().insertInvoice(invoiceList);
				
				//put update export_to_sale =Y
				if(r){
					for(int i=0;i<invoiceList.size();i++){
						invoiceIdArr +=invoiceList.get(i).getInvoiceId()+",";
					}
					invoiceIdArr = invoiceIdArr.substring(0,invoiceIdArr.length()-1);
	
				    Invoice invResult = new InvoiceAPIClient().updateExportToSalesInvoice(invoiceIdArr);
				    logger.debug("invResult Message:"+invResult.getStatusMessage());
				}
			}
        }catch(Exception e) {
        	logger.error(e.getMessage(),e);
        }
	}
	
	public List<Invoice> getInvoiceListToSalesApp() {
		List<Invoice> invoiceList = null;
        HttpURLConnection conn = null;
        EnvProperties env = EnvProperties.getInstance();
		try {
			 logger.debug("start getInvoiceListToSalesApp");
			 
			//dd-server test
			//URL url = new URL("http://192.168.202.7:8082/pensso/service/customer/get");
			 
			String server = env.getProperty("api.server");
			String userName = env.getProperty("api.user");//"admin";
	        String password = env.getProperty("api.password");//"1234!";
	        
	        String authString = userName + ":" + password;
	        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
	        System.out.println("Base64 encoded auth string: " + authStringEnc);
		        
			URL url = new URL(server+"invoice/getAll");
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			
			//System.out.println("conn response:"+conn.getResponseCode());
			if (conn.getResponseCode() != 200) {
				//throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));

			String output;
			String jsonResultData = "";
			while ((output = br.readLine()) != null) {
				jsonResultData +=output;
			}
			logger.debug("jsonResultData:\n "+jsonResultData);
			
			Gson g = new Gson();
			InvoiceAPIBean invoiceAPIBean = g.fromJson(jsonResultData, InvoiceAPIBean.class);
			invoiceList = invoiceAPIBean.getInvoiceList();
			
			logger.debug("Result InvoiceAPIBean invoiceList:"+invoiceAPIBean.getInvoiceList());

		} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally {
			if(conn != null) {
				conn.disconnect();conn=null;
			}
		}
		return invoiceList;
	}
	
	public Invoice updateExportToSalesInvoice(String invoiceIdArr) {
        HttpURLConnection conn = null;
    	Gson gson = new Gson();
    	Invoice inv = null;
    	EnvProperties env = EnvProperties.getInstance();
		try {
            logger.debug("start updateExportToSalesInvoice");
			//dd-server test
			//URL url = new URL("http://192.168.202.7:8082/pensso/service/customer/post");
            
            String server = env.getProperty("api.server");
			String userName = env.getProperty("api.user");//"admin";
	        String password = env.getProperty("api.password");//"1234!";
	        
	        String authString = userName + ":" + password;
	        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
	        System.out.println("Base64 encoded auth string: " + authStringEnc);
	        
			URL url = new URL(server+"invoice/put");
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			
			//"{\"customerCode\":\"Witty Company\"}";
			String inputJson = gson.toJson(invoiceIdArr);
			logger.debug("input json data : \n"+inputJson);
			
			OutputStream os = conn.getOutputStream();
			os.write(inputJson.getBytes());
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
			
			inv = gson.fromJson(jsonResultData, Invoice.class);
			logger.debug("Result Update Exported_to_sales["+inv.getStatusMessage()+"]");
			
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally {
			if(conn != null) {
				conn.disconnect();conn=null;
			}
		}
		return inv;
	}
	
	/**
	 * problem :thai charecter ?? 
	 * @return
	 */
	public List<Invoice> getInvoiceListToSalesApp_MT2() {
		List<Invoice> invoiceList = null;
		try {
			logger.debug("start getInvoiceListToSalesApp");
			String name = "admin";
	        String password = "1234!";
	        String authString = name + ":" + password;
	        String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
	        System.out.println("Base64 encoded auth string: " + authStringEnc);
	        
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("admin", "password"));
	        WebResource webResource = client.resource("http://localhost:8080/pensso/service/invoice/getAll");
	
	        //ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
	       
	        ClientResponse response = webResource.accept("application/json")
                    .header("Authorization", "Basic " + authStringEnc)
                    .get(ClientResponse.class);
	        
	        int responseCode= response.getStatus();
            if (responseCode != 200) {
                // throw new RuntimeException("Failed : HTTP error code : "
                     // + response.getStatus());
            }
            String output = response.getEntity(String.class);

            System.out.println("Output from Server .... \n");
            System.out.println(output);
			
			Gson g = new Gson();
			InvoiceAPIBean invoiceAPIBean = g.fromJson(output, InvoiceAPIBean.class);
			invoiceList = invoiceAPIBean.getInvoiceList();
			
		    logger.debug("Result InvoiceAPIBean invoiceList:"+invoiceAPIBean.getInvoiceList());

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally {
			
		}
		return invoiceList;
	}
	//POST
	 public static void mainXX(String[] args) {

		    try {

		        Client client = Client.create();

		        WebResource webResource = client
		           .resource("http://localhost:8080/RESTfulExample/rest/json/metallica/post");

		        String input = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";

		        ClientResponse response = webResource.type("application/json")
		           .post(ClientResponse.class, input);

		        if (response.getStatus() != 201) {
		            throw new RuntimeException("Failed : HTTP error code : "
		                 + response.getStatus());
		        }

		        System.out.println("Output from Server .... \n");
		        String output = response.getEntity(String.class);
		        System.out.println(output);

		      } catch (Exception e) {

		        e.printStackTrace();

		      }

		    }
}
