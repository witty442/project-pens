package com.pens.rest.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BOTNetClientGet {

	// http://localhost:8080/RESTfulExample/json/product/get
	public static void main(String[] args) {

		try {

			URL url = new URL("https://iapi.bot.or.th/Developer/Home/Api/9");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			System.out.println("conn response:"+conn.getResponseCode());
			if (conn.getResponseCode() != 200) {
				//throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {

				System.out.println(output);
			}
			
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
			
		}

	}

}