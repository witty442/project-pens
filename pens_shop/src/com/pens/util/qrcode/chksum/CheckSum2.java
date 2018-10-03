package com.pens.util.qrcode.chksum;

public class CheckSum2 {
	

	  private static int crc16(final byte[] buffer) {
		    int crc = 0xFFFF;
		 
		    for (int j = 0; j < buffer.length ; j++) {
		        crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
		        crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
		        crc ^= ((crc & 0xff) >> 4);
		        crc ^= (crc << 12) & 0xffff;
		        crc ^= ((crc & 0xFF) << 5) & 0xffff;
		    }
		    crc &= 0xffff;
		    return crc;
		 
	  }
	  
	  // Compute the MODBUS RTU CRC
	  private static int ModRTU_CRC(byte[] buf, int len)
	  {
	    int crc = 0xFFFF;
	 
	    for (int pos = 0; pos < len; pos++) {
	      crc ^= (int)buf[pos];          // XOR byte into least sig. byte of crc
	 
	      for (int i = 8; i != 0; i--) {    // Loop over each bit
	        if ((crc & 0x0001) != 0) {      // If the LSB is set
	          crc >>= 1;                    // Shift right and XOR 0xA001
	          crc ^= 0xA001;
	        }
	        else                            // Else LSB is not set
	          crc >>= 1;                    // Just shift right
	      }
	    }
	    // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
	    return crc;  
	  }
	  
	  public static void main (String[] args) throws java.lang.Exception
	  {
		//output D9FD
	    String input = "00020101021129370016A000000677010111011300660000000005802TH53037646304";
	    	
	     byte[] buf = input.getBytes();
	     int crc = ModRTU_CRC(buf, input.getBytes().length);
	     //java.io.BufferedReader r = new java.io.BufferedReader (new java.io.InputStreamReader (System.in));
	     System.out.println(crc);
	     crc = crc16(buf);
	     System.out.println(crc);
	 
	     //while (!(s=r.readLine()).startsWith("42")) System.out.print(crc);
	  }
}
