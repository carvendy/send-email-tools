package com.youboy.tools.email.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class MyFileUtils{

	public static String read(String path){
		return read(new File(path));
	}
	
	public static String read(File file){
		StringBuffer sb=new StringBuffer();
		FileReader fr=null;
		BufferedReader br=null;
		
		try {
			fr=new FileReader(file);
			br=new BufferedReader(fr);
			
			String data=null;
			while((data=br.readLine())!=null){
				if(StringUtils.isBlank(StringUtils.trim(data))){
					continue;
				}
				sb.append(StringUtils.trim(data)+"\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			close(br);
			close(fr);
		}
		
		return sb.toString();
	}
	
	
	public static void write(String data,String path){
		FileWriter fw=null;
		BufferedWriter bw=null;
		
		try {
			fw=new FileWriter(path);
			bw=new BufferedWriter(fw);
			
			bw.write(data);
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			close(bw);
			close(fw);
		}
	}
	
	private static void close(Closeable closeable){
		if(closeable!=null){
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				closeable=null;
			}
		}
	}
	
	public static void main(String[] args) {
	}
	
}
