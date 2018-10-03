package com.isecinc.pens.inf.helper; 

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;



/**
 * @author WITTY
 *
 */
public final class EnvProperties extends Properties{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(EnvProperties.class);
    private static EnvProperties _instance;
	private static final String VAR_PATTERN = "\\$\\{([^\\}]+)\\}";
	private static final int START_VAR_PATTERN_CHAR_COUNT = 2;
	private static final int END_VAR_PATTERN_CHAR_COUNT = 1;
	private static final Pattern pat = Pattern.compile(VAR_PATTERN);
	private static final String propName ="inf-config/inf-env.properties";
	
	private EnvProperties() throws IOException {
		reload();
	}

	private void fillUpVariable() throws IOException{	
		
		for(Iterator it = keySet().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			String val = (String)get(key);
			Matcher mat = pat.matcher(val);

			String [] tokenMsg = pat.split(val);
			List tokenVar = new ArrayList(); 
			String tk;
			
			while(mat.find()) {
				tk = val.substring(mat.start() + START_VAR_PATTERN_CHAR_COUNT, 
					mat.end() - END_VAR_PATTERN_CHAR_COUNT);
				if(!containsKey(tk))
					throw new IllegalArgumentException("No reference key : " + tk);
				tokenVar.add(get(tk));
			}

			StringBuffer buff = new StringBuffer();
			for(int i = 0; i < tokenVar.size(); i++) {
				buff.append(tokenMsg[i]);
				buff.append(tokenVar.get(i));
			}
			if(tokenMsg.length > tokenVar.size())
				buff.append(tokenMsg[tokenMsg.length - 1]);

			put(key, buff.toString());
		}		
	}

	/**
	 *	Gets a single instance of EnvProperties. Properties are readen from abc.txt file.
	 *	@return a single instance of EnvProperties
	 */
	public static synchronized EnvProperties getInstance() {
		try {
			if(_instance == null)
				_instance = new EnvProperties();
		}catch(IOException e) {
			logger.error("Cannot load properties file");
			e.printStackTrace();
		}
		return _instance;
	}

	/**
	 *	Reads a property list (key and element pairs) from the file that used to load before.
	 *	@throws IOException if an error occurred when reading from the file
	 */
	public synchronized void reload() throws IOException {
		try{
			ClassLoader cl = FileUtil.class.getClassLoader();
			InputStream is = cl.getResourceAsStream(propName);

			load(is);
//			is.close();
			fillUpVariable();
		}catch (IOException e){
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
	  
	
	public static void main(String[] argv) {
		String value= EnvProperties.getInstance().getProperty("DB2.SCHEMA");
		System.out.println(value);
		//logger.debug(value);
	}

	
}
