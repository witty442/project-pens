package com.pens.util.parsehtml;

import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
public class ParseHtmlHelper {

	public static void main(String[] a){
		try{
			new ParseHtmlHelper().getContent("https://www.pensapi-98008.firebaseapp.com/getLocation.html");
			//getContent("https://vibible-ec389.firebaseapp.com/topic_items/moneytalk_weekly_items.txt");
			//new ParseHtmlHelper().getContent("https://board.thaivi.org/viewtopic.php?f=1&t=62608");
			// getContent("https://www.google.co.th/");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//
	public  void getContent(String https_url){
		URL url;
		try{
			System.out.println("https_url:"+https_url);
			
			 url = new URL(https_url);
		     HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
				
		     //dumpl all cert info
		     print_https_cert(con);
				
		     //dump all the content
		     print_content(con);
				
	      } catch (MalformedURLException e) {
		     e.printStackTrace();
	      } catch (IOException e) {
		     e.printStackTrace();
	      }
		
	}
	 private void print_https_cert(HttpsURLConnection con){
	     
    if(con!=null){
			
      try {	
		System.out.println("Response Code : " + con.getResponseCode());
		System.out.println("Cipher Suite : " + con.getCipherSuite());
		System.out.println("\n");
					
		Certificate[] certs = con.getServerCertificates();
		for(Certificate cert : certs){
		   System.out.println("Cert Type : " + cert.getType());
		   System.out.println("Cert Hash Code : " + cert.hashCode());
		   System.out.println("Cert Public Key Algorithm : " 
	                                    + cert.getPublicKey().getAlgorithm());
		   System.out.println("Cert Public Key Format : " 
	                                    + cert.getPublicKey().getFormat());
		   System.out.println("\n");
		}
				
	} catch (SSLPeerUnverifiedException e) {
		e.printStackTrace();
	} catch (IOException e){
		e.printStackTrace();
	}

     }
	
  }
			
   private void print_content(HttpsURLConnection con){
	if(con!=null){	
	try {
	   System.out.println("****** Content of the URL ********");			
	   BufferedReader br = 
		new BufferedReader(
			new InputStreamReader(con.getInputStream()));
				
	   String input;
				
	   while ((input = br.readLine()) != null){
	      System.out.println(input);
	   }
	   br.close();
				
	} catch (IOException e) {
	   e.printStackTrace();
	}
		
   }
  }
}
