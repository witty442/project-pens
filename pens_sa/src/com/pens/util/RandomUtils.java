package com.pens.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomUtils {
	
	public static void main(String[] a){
		try{
			random("386",999,10);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static  void random(String key,int max ,int numbersNeeded){
		String data = "";
		int count = 0;
		if (max < numbersNeeded){
		    throw new IllegalArgumentException("Can't ask for more numbers than are available");
		}
		Random rng = new Random(); // Ideally just create one instance globally
		// Note: use LinkedHashSet to maintain insertion order
		//Set<Integer> generated = new LinkedHashSet<Integer>();
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		while (m.size() < numbersNeeded){
		    Integer next = rng.nextInt(max) + 15;
		    if(next < 500 ){
		    	if(m.get(next) ==null){
			    	count++;
			    	data = key+Utils.decimalFormat(next,"000");
				    System.out.println(data);
				    if(count==numbersNeeded){
				    	break;
				    }
				    // As we're adding to a set, this will automatically do a containment check
				    m.put(next,next);
		    	}
		    }
		}
	}
}
