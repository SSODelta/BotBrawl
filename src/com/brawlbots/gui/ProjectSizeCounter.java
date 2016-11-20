package com.brawlbots.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProjectSizeCounter {

	private static final String path = "C:\\Users\\nikol\\Documents\\GitHub\\AIArena\\src";
	public static void main(String[] args) {
		try {
			System.out.println("Lines: "+processNode(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static int processNode(File f) throws FileNotFoundException{
		return processNode("", f);
	}
	
	public static int processNode(String prefix, File f) throws FileNotFoundException{
		if(f.exists()){
			if(f.isDirectory()){
				int count = 0;
				for(File ff : f.listFiles())
					count+=processNode(prefix+"\t", ff);
				System.out.println(prefix+count+"\tdir::"+f.getName());
				return count;
			}
			else if (f.isFile())
				return processFile(prefix+"\t",f);
		}
		return -1;
	}
	
	public static int processFile(String prefix, File f) throws FileNotFoundException{
		Scanner file = new Scanner(f);
	    int count = 0;
	    while(file.hasNextLine()){
	    	count++;
	    	file.nextLine();
	    }
	    file.close();
		System.out.println(prefix+count+"\tfile::"+f.getName());
	    return count;
	}

}
