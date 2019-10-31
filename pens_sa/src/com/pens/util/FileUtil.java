package com.pens.util;

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
import java.net.InetAddress;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.isecinc.pens.report.salesanalyst.helper.EnvProperties;



/**
 * @author WITTY
 *
 */
public class FileUtil {
	static Logger logger = Logger.getLogger("PENS");

	public static void writeExcel(String path,HSSFWorkbook xssfWorkbook) throws Exception{
		FileOutputStream outPutStream = null;
        try {
            outPutStream = new FileOutputStream(path);
            xssfWorkbook.write(outPutStream);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (outPutStream != null) {
                try {
                    outPutStream.flush();
                    outPutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
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
	
	public static void zipFile(String sourceFile ,String destFile,String fileName) throws Exception{
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];
		ZipOutputStream out = null;
		FileInputStream in = null;
		try {
		    // Create the ZIP file
		    out = new ZipOutputStream(new FileOutputStream(destFile));
		    // Compress the files
	         in = new FileInputStream(sourceFile);
	        out.putNextEntry(new ZipEntry(fileName));
	        
	        // Transfer bytes from the file to the ZIP file
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }

	        //close entry file
	        out.closeEntry();
		   
		} catch (IOException e) {
			throw e;
		}finally{
			if(in != null){
				in.close();in=null;
			}
			if(out != null){
			   out.close();out = null;
			}
		}
	}

	 public static void compress(String filein,  String fileout ) {
	      FileInputStream fis = null;
	      FileOutputStream fos = null;
	      try {
	        fis = new FileInputStream(filein);
	        fos = new FileOutputStream(fileout);
	        ZipOutputStream zos =new ZipOutputStream(fos);
	        ZipEntry ze = new ZipEntry("temp.sql");
	        zos.putNextEntry(ze);
	        final int BUFSIZ = 4096;
	        byte inbuf[] = new byte[BUFSIZ];
	        int n;
	        while ((n = fis.read(inbuf)) != -1)
	          zos.write(inbuf, 0, n);
	        fis.close();
	        fis = null;
	        zos.close();
	        fos = null;
	      }
	      catch (IOException e) {
	        System.err.println(e);
	      }
	      finally {
	        try {
	          if (fis != null)
	            fis.close();
	          if (fos != null)
	            fos.close();
	        }
	        catch (IOException e) {
	        }
	      }
	    }
	 
	/** Get Root Temp fro write file temp */
	/** UAT:d://dev_temp/ **/
	/** PROD:/PENS/ERP/apps/inst/saleonline/dev_temp/  **/
	
	public static String getRootPathTemp(EnvProperties env) {
		String rootPathTemp = "";
		//logger.debug("getRootPathTemp");
		try{
			rootPathTemp = env.getProperty("path.temp");
			
			//Case rootPath prouctType=production (but deploy for test on 192.168.202.7or8)
			// use path: d://dev_temp/
			logger.debug("product.type["+env.getProperty("product.type")+"]");
			logger.debug("localAddress["+InetAddress.getLocalHost().getHostAddress()+"]");
			if(env.getProperty("product.type").equalsIgnoreCase("Production")){
				if(InetAddress.getLocalHost().getHostAddress().equals("192.168.202.7") //witty
					|| InetAddress.getLocalHost().getHostAddress().equals("192.168.202.8")){
					rootPathTemp = "d://dev_temp//";
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return rootPathTemp;
	}
	
}
