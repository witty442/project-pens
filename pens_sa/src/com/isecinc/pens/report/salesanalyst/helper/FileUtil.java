package com.isecinc.pens.report.salesanalyst.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;



/**
 * @author WITTY
 *
 */
public class FileUtil {
	public static Logger logger = Logger.getLogger(FileUtil.class);

	
	
	public static BufferedReader getBufferReaderFromClassLoader(String filename) throws Exception {
		BufferedReader br = null;
		ClassLoader cl = FileUtil.class.getClassLoader();
		/*logger.debug("ClassLoader:"+cl);
		logger.debug("ClassLoader 2:"+cl.getClass());
		logger.debug("ClassLoader 3:"+cl.getParent());*/
		
		InputStream in = cl.getResourceAsStream(filename);
		InputStreamReader isr = new InputStreamReader(in);
		br = new BufferedReader(isr);

		return br;
	}

	@SuppressWarnings("deprecation")
	public static String readFile(String filename) throws Exception {
		File file = new File(filename);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;
        String str ="";
	    try {
		      fis = new FileInputStream(file);
	
		      // Here BufferedInputStream is added for fast reading.
		      bis = new BufferedInputStream(fis);
		      dis = new DataInputStream(bis);

		      // dis.available() returns 0 if the file does not have more lines.
		      while (dis.available() != 0) {
		         str +=dis.readLine()+"\n";
		      }
	
		      // dispose all the resources after using them.
		      fis.close();
		      bis.close();
		      dis.close();
	      return str;
	    } catch (FileNotFoundException e) {
	       throw e;
	    } catch (IOException e) {
	       throw e;
	    }
	}
	public static byte[] readFileToByte(InputStream is) throws IOException{
		byte[] bytes = null;
		try {
			bytes = IOUtils.toByteArray(is);
		}catch(IOException e){
			throw e;
	    }finally{
	   
	    }
       return bytes;
	}
	public static String readControlEnvFile(InputStream fis) throws Exception {
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;
        String str ="";
        String lineStr ="";
	    try {
		      // Here BufferedInputStream is added for fast reading.
		      bis = new BufferedInputStream(fis);
		      dis = new DataInputStream(bis);

		      // dis.available() returns 0 if the file does not have more lines.
		      while (dis.available() != 0) {
		    	  lineStr = dis.readLine();
		    	  //System.out.println("lineStr:"+lineStr);
		    	  if( !lineStr.startsWith("#")){
		            str  = lineStr;
		      	    //System.out.println("xx str:"+str);
		            break;
		    	  }
		      }
	         // System.out.println("str:"+str);
		      // dispose all the resources after using them.
		      fis.close();
		      bis.close();
		      dis.close();
	      return str;
	    } catch (FileNotFoundException e) {
	       throw e;
	    } catch (IOException e) {
	       throw e;
	    }
	}
	
	public static void writeFile(String fileName,String str) {
		FileWriter fstream =null;
		try{
		    // Create file 
		    fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write(str);
		    //Close the output stream
		    out.close();
		    fstream.close();
		    
	    }catch (Exception e){//Catch exception if any
	       logger.error("Write File Error:"+e.getMessage(),e);
	    }
	  }
	
	
	public static String getFileSize(String str) {
		File file = null;
		BufferedWriter out = null;
		String fileSize = "0";
		try{
			file = File.createTempFile("pattern", ".suffix"); 
			// Write to temp file 
			out = new BufferedWriter(new FileWriter(file)); 
			out.write(str); 
			out.close();
			
			fileSize = FileUtils.byteCountToDisplaySize(file.length());
	    }catch (Exception e){//Catch exception if any
	       logger.error("Write File Error:"+e.getMessage(),e);
	    }finally{
	    	// Delete temp file when program exits. 
			if( file != null){
				file.deleteOnExit(); 
				file = null;
			}
	    }
	    return fileSize;
	  }
	
   public static void writeFile(String filename,String dataStr,String encoding) {
        BufferedOutputStream bufferedOutput = null; 
        try {
            //Construct the BufferedOutputStream object
            bufferedOutput = new BufferedOutputStream(new FileOutputStream(filename));
            bufferedOutput.write(dataStr.getBytes(encoding));

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedOutputStream
            try {
                if (bufferedOutput != null) {
                    bufferedOutput.flush();
                    bufferedOutput.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
	
	public static void writeFile(String fileName,byte[] bytefile) throws Exception{
         FileOutputStream fos = null;
		try {
		    fos = new FileOutputStream( fileName );
		    fos.write( bytefile );		   
		}
	    catch (Exception ex){
	    	logger.error( ex.getMessage(), ex);
	    	throw ex;
	    }finally{
	        if(fos != null){
		        fos.close();	            
	        }
	    }	    
	  }
	/**
	 * get file content from classloader path
	 */
	public static String getClassloaderContentStr(String filename) throws Exception {

		return getClassloaderContentStr(filename,false);
	}
	
	
	public static String getClassloaderContentStr(String filename,boolean preserveNewLine) throws Exception {
		String r = "";
		String newLine = System.getProperty("line.separator");
		BufferedReader br = getBufferReaderFromClassLoader(filename);
		try {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				r += temp+" ";
				if (preserveNewLine) {
					r += newLine;
				}
			}
		} finally {
			close(br);
		}
		return r;
	}

	/**
	 * get file content full path
	 */
	

	public static void close(BufferedReader br) throws IOException {
		if (br != null) {
			br.close();br=null;
		}
	}

	

	public static File createFile(String fileName, byte[] fileData) throws FileNotFoundException, IOException {
		createParentDir(fileName);
		File destFile = new File(fileName);
		logger.debug("create file =" + destFile.getAbsolutePath());
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(destFile);
			fo.write(fileData);
		} finally {
			fo.close();
		}

		return destFile;
	}

	public static void createFile(String fileName) throws Exception {
		PrintWriter pOut = null;
		createParentDir(fileName);
		logger.debug("create file=" + fileName);
		try {
			pOut = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)));
			// pOut.println(out);
		} finally {
			if (pOut != null) {
				pOut.close();
			}
		}
	}

	public static void createDir(String dirName) {
		// create dir
		File f = new File(dirName);
		File dir = new File(f.getAbsolutePath());
		if (!dir.exists()) {
			logger.debug("create dir=" + dir);
			dir.mkdirs();
		}
	}

	public static void createParentDir(String fileName) {
		// create dir
		File f = new File(fileName);
		f = f.getParentFile();
		createDir(f.getAbsolutePath());
	}

	
	public static String getQueryStr(String path,String table) throws Exception {
		String r = "";
		// select query
		String location = path+"/" + table + ".sql";
		System.out.println("location=" + location);
		r = FileUtil.getClassloaderContentStr(location,true);
		//r = r.replaceAll("--(\\s*)(end_)*unload(\\s*)--", ""); // remove --unload--,--end_unload--
		//r = r.replaceAll("P_INF_NO", "" + intfNO + "");// replace output with intf no
		// r = r.replaceAll("(?i)where.*", " ");// mod orginal content to get only marked //<%=CHK BUG FOR THIS%>
		// r += "where conv_flag='N'";
		// if (table.equalsIgnoreCase("TEMP_TXN_TREATY_PERIOD") || table.equalsIgnoreCase("TEMP_TXN_REVENUE_RECOGNITION")) {
		// r += "1";
		// } else {
		// r += "'1'";
		// }
		return r;
	}
	
	
}
