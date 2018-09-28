package com.isecinc.pens.process;

import com.pens.util.Utils;


public class BarcodeGenerate {

	public BarcodeGenerate() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String barcodeInput12 ="111115897894";
		System.out.println(genBarcode(barcodeInput12));
		barcodeInput12 ="911000000078";
		System.out.println(genBarcode(barcodeInput12));
	}
	
	
	//1 2 3 4 1 2 3 4 1 2 3 4 C
	
	public static int genBarcode(String barcodeInput){
		if (barcodeInput.length() < 12) return 0;
		char[] code = barcodeInput.toCharArray();
	   
	    int sum1 = 3 * (charToInt(code[1]) + charToInt(code[3]) + charToInt(code[5]) +charToInt(code[7]) + charToInt(code[9]) +charToInt(code[11]));
	    int sum2 =     (charToInt(code[0]) + charToInt(code[2]) + charToInt(code[4]) + charToInt(code[6])+ charToInt(code[8])+ charToInt(code[10]));
	
	    int checksum_value = sum1 + sum2;
	   // System.out.println("checksum_value:"+checksum_value);
	   // System.out.println("checksum_value % 10:"+checksum_value % 10);
	    
	    int checksum_digit = 10 - (checksum_value % 10);
	    
	    if (checksum_digit == 10) checksum_digit = 0;
	
	    return checksum_digit;
	}
	
	public static int charToInt(char t){
		return Integer.parseInt(String.valueOf(t));
	}
	
	public static int validateBarcode(String barcodeInput){
		int r = 0;
		try{
		   char[] a = barcodeInput.toCharArray();
		   int len=0,i=0, sum=0, mul=1, mod10, checksum;
		    
		   len = a.length;
		   if(len!=13)
		       return 0;
		    
		    checksum = Integer.parseInt( String.valueOf(a[len-1]));
		    for(i=0; i<len-1; i++)
		      {
		        sum += Integer.parseInt( String.valueOf(a[i])) * mul;
		        if(mul==1){
		        	mul = 3;
		        }else{
		        	mul = 1;
		        }
		        
		      }
		    
		     mod10 = sum % 10; 
		     mod10 = 10 - mod10;
		     
		     if(mod10 == checksum){
		        r= 1;
		     }else{
		        r= 0;
		     }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return r;
	}

}
