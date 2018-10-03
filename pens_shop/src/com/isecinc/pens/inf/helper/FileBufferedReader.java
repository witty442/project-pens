package com.isecinc.pens.inf.helper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

/**
 * @author WITTY
 *
 */
public class FileBufferedReader extends BufferedReader {
	FileInputStream fs;
	DataInputStream din;
	Reader r;
	public FileBufferedReader(Reader in, DataInputStream din, FileInputStream fs) {
		super(in);
		this.fs =fs;
		this.din = din;
		this.r =in;
		
	}
	
	@Override
	public void close() throws IOException {
		fs.close();
		din.close();
		r.close();
		super.close();
	}

}
