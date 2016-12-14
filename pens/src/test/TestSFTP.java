package test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class TestSFTP {

	
	public static void testSFTP(){
		 Session session = null;
		   Channel channel = null;
		    try {
		        JSch ssh = new JSch();
		        ssh.setKnownHosts("/path/of/known_hosts/file");
		        session = ssh.getSession("username", "host", 22);
		        session.setPassword("password");
		        session.connect();
		        channel = session.openChannel("sftp");
		        channel.connect();
		        ChannelSftp sftp = (ChannelSftp) channel;
		        sftp.put("/path/of/local/file", "/path/of/ftp/file");
		        
		    } catch (JSchException e) {
		        e.printStackTrace();
		    } catch (SftpException e) {
		        e.printStackTrace();
		    } finally {
		        if (channel != null) {
		            channel.disconnect();
		        }
		        if (session != null) {
		            session.disconnect();
		        }
		    }
	}
}
