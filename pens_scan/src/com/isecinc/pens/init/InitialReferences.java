package com.isecinc.pens.init;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.isecinc.core.Database;
import com.isecinc.core.bean.References;
import com.isecinc.core.init.I_Initial;

/**
 * Initial References
 * 
 * @author Atiz.b
 * @version $Id: InitialReferences.java,v 1.0 28/09/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InitialReferences extends I_Initial {

	
	private static Hashtable<String, List<References>> referenes = new Hashtable<String, List<References>>();

	public void init() {}

	/**
	 * init with conn
	 */
	public void init(Connection conn) {
		try {
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	public static Hashtable<String, List<References>> getReferenes() {
		return referenes;
	}
	
	public static List<References> getReferenes(String key,String value) {
		List<References> filter = new ArrayList<References>();
		try{
			List<References> refs =  referenes.get(key);
			System.out.println("refs size:"+refs.size());
			if(refs != null && refs.size() > 0){
				for(int i=0;i<refs.size();i++){
					References r = (References)refs.get(i);
					System.out.println("value["+value+"]key["+r.getKey()+"]code["+r.getCode()+"]");
					if(value.equals(r.getKey())){
						filter.add(r);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return filter;
	}
}
