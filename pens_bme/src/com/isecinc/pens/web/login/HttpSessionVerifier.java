package com.isecinc.pens.web.login;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class HttpSessionVerifier implements HttpSessionListener {
	private Logger logger = Logger.getLogger("PENS");
	//private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

  public void sessionCreated(HttpSessionEvent event) {
      Date sessionCreationTime = new Date(event.getSession().getCreationTime());
      Date sessionLastAccessedTime = new Date(event.getSession().getLastAccessedTime());
      int sessionMaxInactiveInterval = event.getSession().getMaxInactiveInterval();
      logger.warn("Session: " + event.getSession().getId()
          + " createTime: " + sessionCreationTime
          + " lastAccess: " + sessionLastAccessedTime
          + " with maxInactiveInterval: " + sessionMaxInactiveInterval
          + " created.");
      
      HttpSession session = event.getSession();
      //sessions.put(session.getId(), session);
  }

  public void sessionDestroyed(HttpSessionEvent event) {
      Date sessionCreationTime = new Date(event.getSession().getCreationTime());
      Date sessionLastAccessedTime = new Date(event.getSession().getLastAccessedTime());
      int sessionMaxInactiveInterval = event.getSession().getMaxInactiveInterval();
      logger.warn("Session: " + event.getSession().getId()
          + " createTime: " + sessionCreationTime
          + " lastAccess: " + sessionLastAccessedTime
          + " with maxInactiveInterval: " + sessionMaxInactiveInterval
          + " destroyed.");
      
      //sessions.remove(event.getSession().getId());
  }
  
 /* public static HttpSession find(String sessionId) {
      return sessions.get(sessionId);
  }*/
}

