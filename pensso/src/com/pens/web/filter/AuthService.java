package com.pens.web.filter;
import java.io.IOException;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
 
//import java.util.Base64;

public class AuthService {
	private static Logger logger = Logger.getLogger("PENS");

	public boolean isUserAuthenticated(String authString){
        
        String decodedAuth = "";
        // Header is in the format "Basic 5tyc0uiDat4"
        // We need to extract data before decoding it back to original string
        String[] authParts = authString.split("\\s+");
        String authInfo = authParts[1];
        // Decode the data back to original string
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(authInfo);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        decodedAuth = new String(bytes);
        System.out.println("decodedAuth:"+decodedAuth);
         
        /**
         * here you include your logic to validate user authentication.
         * it can be using ldap, or token exchange mechanism or your 
         * custom authentication mechanism.
         */
        // your validation code goes here....
        if(decodedAuth.equals("admin:1234!")){
        	return true;
        }
         
        return false;
    }
}
