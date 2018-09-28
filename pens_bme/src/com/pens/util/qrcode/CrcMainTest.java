package com.pens.util.qrcode;

import java.util.Scanner;

import static java.lang.System.out;

public class CrcMainTest {

    public static void main(String[] args) {
    
       // Check(Crc16.Params);
    	//String input ="00020101021153037645802TH29370016A000000677010111021334010000704776304";
    	String input ="00020101021129370016A000000677010111021334010000704775802TH540319853037646304";
    	CheckOne(input); 
    }

    private static void Check(AlgoParams[] params)
    {
        for (int i = 0; i < params.length; i++) {
            CrcCalculator calculator = new CrcCalculator(params[i]);
            long result = calculator.Calc(CrcCalculator.TestBytes, 0, CrcCalculator.TestBytes.length);
            if (result != calculator.Parameters.Check)
                System.out.println(calculator.Parameters.Name + " - BAD ALGO!!! " + Long.toHexString(result).toUpperCase());
        }
    }
    
    private static void CheckOne(String input)
    {
            CrcCalculator calculator = new CrcCalculator(Crc16.Crc16CcittFalse);
            long result = calculator.Calc(input.getBytes(), 0, input.getBytes().length);
            if (result != calculator.Parameters.Check){
                 System.out.println(calculator.Parameters.Name + " - BAD ALGO!!! " + Long.toHexString(result).toUpperCase());
            }
    }
}