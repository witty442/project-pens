package com.pens.rest.client.jersey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class BOTJerseyClientGet {

	public static void main(String[] args) {
		try {
            String param = "?start_period=2002-01-12&end_period=2002-01-15";
			Client client = Client.create();
			WebResource webResource = client.resource("https://iapi.bot.or.th/Stat/Stat-ReferenceRate/DAILY_REF_RATE_V1/"+param);
			webResource.header("api-key","U9G1L457H6DCugT7VmBaEacbHV9RX0PySO05cYaGsm");
			//ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
	
		/*	if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}*/

			/*String output = response.getEntity(String.class);
			System.out.println("Output from Server .... \n");
			System.out.println(output);
*/
			String jsonStr = webResource.get(String.class);
            System.out.println("Testing:");
            System.out.println(jsonStr);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}