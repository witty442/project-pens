package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Test {

	/**
	 * @param args
	 * Output 
	    *
	   ***
	  *****
	 *******
	  *****
	   ***
	    *
	 */
	public static void main(String[] args) {
		
		try{
		   for(int i=0;i<=7;i++){
			  
			   if(i<=3){
				   System.out.println("*");
				   for(int j=0;j<=i+1;j++){
					   //System.out.println("m["+(i%j)+"]");
				        System.out.print("*");
				   }		  
			   }else{
				   System.out.println("*");
				   for(int j=8;j>=i+1;j--){
					   //System.out.println("m["+(i%j)+"]");
				        System.out.print("*");
				   }
			   }
		   }
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
