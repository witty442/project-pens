package com.isecinc.pens.inf.helper;

import java.io.BufferedInputStream;
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Blob;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;




/**
 * @author WITTY
 *
 */
public class FileUtil {
	public static Logger logger = Logger.getLogger("PENS");

	public static void main(String[] s){
		
	}
	public static String writeImageFile(String path,InputStream in){
		try{
			OutputStream out = new FileOutputStream(path);
			byte[] buff = new byte[4096];  // how much of the blob to read/write at a time
			int len = 0;

			while ((len = in.read(buff)) != -1) {
			    out.write(buff, 0, len);
			}
			in.close();
			out.close();
		}catch(Exception e){
				
		}
		return "";
	}
	
	public static String writeImageFileTemp(String path,Blob imageFile){
		try{
			InputStream in = imageFile.getBinaryStream();
			OutputStream out = new FileOutputStream(path);
			byte[] buff = new byte[4096];  // how much of the blob to read/write at a time
			int len = 0;

			while ((len = in.read(buff)) != -1) {
			    out.write(buff, 0, len);
			}
			in.close();
			out.close();
		}catch(Exception e){
				
		}
		return "";
	}
	
	public static BufferedReader getBufferReaderFromClassLoader(String filename) throws Exception {
		logger.debug("fileName:"+filename);
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
	
	
	
	public static String readFile(String fileName,String encoding)  {
    
	   InputStream is = null;
	   Writer writer = new StringWriter();
       try{
    	   is = new FileInputStream(fileName);
           Reader reader = null;
	       char[] buffer = new char[1024];
            try {
            	reader = new BufferedReader(new InputStreamReader(is,encoding));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                     writer.write(buffer, 0, n);
                 }
            }finally {
                if(is != null){
                	is.close();
                	is =null;
                }
                if(reader != null){
                  reader.close();
                  reader = null;
                }
                writer.flush();
            }
	            
       }catch(Exception e){
    	   logger.error(e.getMessage(),e);
       }
       return writer.toString();
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
	
   public static void writeFile(String filename,String dataStr,String encoding) {
	   File backupFile = new File(filename);
        try {
            // Change Method TO Write File
            FileUtils.writeStringToFile(backupFile, dataStr,encoding);
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        } finally {
            //Close the BufferedOutputStream
        }
    }
   
   public static void writeFile(String fileName,StringBuffer dataStr,String encoding) {
	   OutputStreamWriter os = null;
	   BufferedWriter out = null;
	   FileOutputStream fs = null;
	   try{
		   fs = new FileOutputStream(fileName);
		   os = new OutputStreamWriter(fs,encoding);
		   out = new BufferedWriter(os);
		   out.write(dataStr.toString());
		  
		  }catch(UnsupportedEncodingException ue){
		     logger.error(ue.getMessage(),ue);
		  }catch(IOException e){
			  logger.error(e.getMessage(),e);
		  }finally{
			  try{
				  if(out != null){
					 out.close();out=null;
				  }
				  if(os != null){
					 os.close();os=null;
				  }
				  if(fs != null){
					 fs.close();fs = null;
				  }
			  }catch(Exception ee){}
		  }
		  
	}
   
   public static void writeFileOpt(String fileName,String dataStr) {
	   FileWriter fs =null;
	   BufferedWriter out = null;
	   try{
		    // Create file 
		    fs = new FileWriter(fileName);
		    out = new BufferedWriter(fs);
		    out.write(dataStr);
		    //Close the output stream
		    out.flush();
		    fs.flush();
	    }catch (Exception e){//Catch exception if any
	       logger.error("Write File Error:"+e.getMessage(),e);
	    }finally{
	    	try{
	    		if(out != null){
	    	       out.close();out = null;
	    		}
	    		if(fs != null){
			       fs.close();fs = null;
	    		}
	    	}catch(Exception ee){}
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
			if(fo != null)fo.close();
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

	public static void deleteFile(String fileName) {
		File f = null;
		try{
		   f = new File(fileName);
		   boolean success = f.delete();
		   if(!success){
			   logger.info("deleteFile "+fileName+" fail");
		   }
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}finally{
			if( f != null){
				f = null;
			}
		}
	}
	
	public static void deleteFile2(String fileName) {
		File f = null;
		try{
		   f = new File(fileName);
		   boolean success = f.delete();
		   if(!success){
			   logger.info("deleteFile "+fileName+" fail");
		   }
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}finally{
			if( f != null){
				f = null;
			}
		}
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
}
