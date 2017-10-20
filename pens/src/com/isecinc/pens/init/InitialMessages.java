package com.isecinc.pens.init;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.List;

import com.isecinc.core.Database;
import com.isecinc.core.bean.Messages;
import com.isecinc.core.init.I_Initial;

/**
 * Initial Messages
 * 
 * @author Atiz.b
 * @version $Id: InitialMessages.java,v 1.0 21/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InitialMessages extends I_Initial {

	private static Hashtable<String, Messages> messages = new Hashtable<String, Messages>();

	public void init() {}

	/**
	 * init with conn
	 */
	public void init(Connection conn) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM c_message ");
			List<Messages> refList = Database.query(sql.toString(), null, Messages.class, conn);
			// logger.debug(refList);
			for (Messages r : refList) {
				//logger.debug(r);
				messages.put(r.getMessageCode(), r);
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	public static Hashtable<String, Messages> getMessages() {
		return messages;
	}

}
