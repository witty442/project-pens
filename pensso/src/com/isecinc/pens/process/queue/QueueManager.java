package com.isecinc.pens.process.queue;

import java.util.Iterator;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

public class QueueManager {
	private static Logger logger = Logger.getLogger("PENS");
	private static QueueManager _instance;
	private PriorityQueue<String> queue;
	
	public static void main(String args[]){  
		PriorityQueue<String> queue=new PriorityQueue<String>();  
		queue.add("Amit");  
		queue.add("Vijay");  
		queue.add("Karan");  
		queue.add("Jai");  
		queue.add("Rahul");  
		System.out.println("head:"+queue.element());  
		System.out.println("head:"+queue.peek());  
		System.out.println("iterating the queue elements:");  
		Iterator itr=queue.iterator();  
		while(itr.hasNext()){  
		   System.out.println(itr.next());  
		}  
		queue.remove();  
		queue.poll();  
		System.out.println("after removing two elements:");  
		Iterator<String> itr2=queue.iterator();  
		while(itr2.hasNext()){  
		  System.out.println(itr2.next());  
		  }  
	}   

	public static  QueueManager getIns(){
	  if(_instance ==null)
		  _instance = new QueueManager();
	  return _instance;
	}
	
	
	
}
