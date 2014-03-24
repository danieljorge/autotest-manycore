package auto_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
	
	public static ArrayList<String> getFileLines(String filename) {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br;
		try{
				br = new BufferedReader(new FileReader(filename));
				try {
					String line_temp = br.readLine();
					while (line_temp != null) {
						lines.add(line_temp);
						line_temp = br.readLine();
					}
				} finally {
					br.close();
				}
		} catch (IOException e){ System.out.println("ERROR: unable to obtain lines from file: "+ filename +e.toString());}
			return lines;
	}
	
	public static void writeFile(ArrayList<String> lines, String dest_path) {
		FileWriter writer;
		try {
			writer = new FileWriter(dest_path);
		for(String str: lines) {
		  writer.write(str);
		}
		writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: FileUtils: writeFile: Error in the I/O! exception was: "+e.toString());
		} 
		File dest = new File(dest_path);
		dest.setExecutable(true);
	}
	
	public static void copyFileUsingStream(String source_path, String dest_path) {
		File source = new File(source_path);
		File dest = new File(dest_path);
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } catch (FileNotFoundException e) {
			System.out.println("ERROR: FileUtils: copyFileUsingStream: File not found! exception was: "+e.toString());
		} catch (IOException e) {
			System.out.println("ERROR: FileUtils: copyFileUsingStream: Error in the I/O! exception was: "+e.toString());
		} finally {
	        try {
				is.close();
				os.close();
			} catch (IOException e) {
				System.out.println("ERROR: FileUtils: copyFileUsingStream: Error while closing the stream! exception was: " + e.toString());
			}
	    } 
	}
	
	public static void changeOption(String option, String new_value, String file_path){
		Pattern pattern = Pattern.compile(option+" *=");
		ArrayList<String> lines = getFileLines(file_path);
		
		int end = lines.size(), line_to_edit=0;
		for (int i = 0; i <= end; i++){
			String tmp = lines.get(i);
			//System.out.println("DEBUG: find: "+tmp);
			Matcher matcher = pattern.matcher(tmp);
			if(matcher.find()){
				line_to_edit=i;
				i=end;
			}
		}
		if(line_to_edit != 0){
		String[] line_split= lines.get(line_to_edit).split("=",2);
		lines.set(line_to_edit, line_split[0]+" = "+new_value+"#"+line_split[1]);
		writeFile(lines, file_path);
		}
		System.out.println("ERROR: FileUtils: changeOption: Line to edit not found");
		return;
	}
	
	public static String getLineStartingWith(String start, String file_path){
		ArrayList<String> lines = getFileLines(file_path);
		if(lines.isEmpty())
			return "";
		int end = lines.size();
		for (int i = 0; i < end; i++){
			String tmp = lines.get(i);
			if(tmp.contains(start))
				return tmp;
		}
		return "NONE";
	}
	
	public static String getSecondLineStartingWith(String start, String file_path){
		boolean second = false;
		ArrayList<String> lines = getFileLines(file_path);
		if(lines.isEmpty())
			return "";
		int end = lines.size();
		for (int i = 0; i <= end; i++){
			String tmp = lines.get(i);
			if(tmp.contains(start)){
				if(second)
					return tmp;
				else
					second = true;
			}
				
		}
		return "";
	}
	
	
}
