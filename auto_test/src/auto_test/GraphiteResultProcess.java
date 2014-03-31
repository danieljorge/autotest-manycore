package auto_test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GraphiteResultProcess {
	String result_dir;
	
    
	GraphiteResultProcess(String result_dir){
		this.result_dir = result_dir;
	}
	
	private String cleanLine(String line, String text){
		line = line.replace(text, "");
		line = line.replace(" ", "");
		line = line.replace("|", ",");
		return line;
	}
	
	public void process(){
		//Blank workbook
	    XSSFWorkbook workbook = new XSSFWorkbook(); 
	    //Create a blank sheet
	    XSSFSheet sheet = workbook.createSheet("Simulation Results");
		//This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
		Integer cores=1, line=1;
		data.put(line.toString(), new Object[] {"Cores", "Execution time - Main core", "Execution time - Second Core", "Main Core - Cache Write Miss Rate", "Main Core - Cache Read Miss Rate", "Second Core - Cache Write Miss Rate", "Second Core - Cache Read Miss Rate","Total Interconnect Network contention (ms)", "Total Directory entries", "Total DRAM access delay (ns)","Total DRAM contention delay (ns)"});
		String simout_line, write_cache_line,read_cache_line, total_dram_accesses, dram_access_latency, dram_contention_delay, total_num_packets_sent, total_interconnect_network_contention;
		boolean finished = false;
		while(!finished){
			simout_line= FileUtils.getLineStartingWith("Completion Time", result_dir+cores+"/sim.out");
			write_cache_line= FileUtils.getLineStartingWith("Write Miss Rate (%)", result_dir+cores+"/sim.out");
			read_cache_line= FileUtils.getLineStartingWith("Read Miss Rate (%)", result_dir+cores+"/sim.out");
			total_dram_accesses= FileUtils.getLineStartingWith("Total Dram Accesses", result_dir+cores+"/sim.out");
			dram_access_latency= FileUtils.getLineStartingWith("Average Dram Access Latency (in nanoseconds)", result_dir+cores+"/sim.out");
			dram_contention_delay= FileUtils.getLineStartingWith("Average Dram Contention Delay (in nanoseconds)", result_dir+cores+"/sim.out");
			total_num_packets_sent = FileUtils.getSecondLineStartingWith("Total Packets Sent", result_dir+cores+"/sim.out");
			total_interconnect_network_contention = FileUtils.getSecondLineStartingWith("Average EMesh Router Contention Delay", result_dir+cores+"/sim.out");
			if(simout_line.equals("")){
				if(cores > 100){
				finished = true;
				break;
				}
				else{
					line++;
					cores++;
					continue;}
			}
			
			simout_line = cleanLine(simout_line, "");
			write_cache_line = cleanLine(write_cache_line, "");
			read_cache_line = cleanLine(read_cache_line, "");
			total_dram_accesses = cleanLine(total_dram_accesses, "Total Dram Accesses");
			dram_access_latency = cleanLine(dram_access_latency, "Average Dram Access Latency (in nanoseconds)");
			dram_contention_delay = cleanLine(dram_contention_delay, "Average Dram Contention Delay (in nanoseconds)");
			total_num_packets_sent = cleanLine(total_num_packets_sent, "Total Packets Sent");
			total_interconnect_network_contention = cleanLine(total_interconnect_network_contention, "Average EMesh Router Contention Delay");
			
			Long time_main = Long.valueOf(simout_line.split(",")[1]);
			Long time_second = time_main;
			if(cores > 1)
				time_second = Long.valueOf(simout_line.split(",")[2]);
			
			Integer cache_write_miss_main =  Integer.valueOf((write_cache_line.split(",")[1]).split("\\.")[0]);
			Integer cache_write_miss_second = cache_write_miss_main;
			System.out.println(write_cache_line);
			if(cores > 1 && write_cache_line.split(",").length>2)
				cache_write_miss_second = Integer.valueOf((write_cache_line.split(",")[2]).split("\\.")[0]);
			
			Integer cache_read_miss_main =  Integer.valueOf((read_cache_line.split(",")[1]).split("\\.")[0]);
			Integer cache_read_miss_second = cache_read_miss_main;
			if(cores > 1 && read_cache_line.split(",").length>2)
				cache_read_miss_second = Integer.valueOf((read_cache_line.split(",")[2]).split("\\.")[0]);
			
			Float total_dram_access_latency = (float) 0.0, total_dram_contention_delay = (float) 0.0;
			
			String[] total_dram_accesses_array = total_dram_accesses.split(",");
			String[] dram_access_latency_array = dram_access_latency.split(",");
			String[] dram_contention_delay_array = dram_contention_delay.split(",");
			
			for(int i=1;i<total_dram_accesses_array.length;i++){
				if(!total_dram_accesses_array[i].isEmpty()){
					System.out.println("AQUI! :"+total_dram_accesses_array[i]+"     "+total_dram_accesses_array[i]+"     "+dram_access_latency_array[i]);
					total_dram_access_latency+=
							Float.valueOf(total_dram_accesses_array[i])*
							Float.valueOf(dram_access_latency_array[i]);
					total_dram_contention_delay+=
							Float.valueOf(total_dram_accesses_array[i])*
							Float.valueOf(dram_contention_delay_array[i]);
				}
			}
			System.out.println("DRAM latency:"+total_dram_access_latency+" DRAM contention:"+total_dram_contention_delay);
			
			String[] total_num_packets_sent_array = total_num_packets_sent.split(",");
			String[] total_interconnect_network_contention_array = total_interconnect_network_contention.split(",");
			Float total_interconnect_contention = (float) 0.0;
			
			for(int i=1;i<total_num_packets_sent_array.length;i++){
				if(!total_num_packets_sent_array[i].isEmpty()){
					//System.out.println("AQUI! :"+total_dram_accesses_array[i]+"     "+total_dram_accesses_array[i]+"     "+dram_access_latency_array[i]);
					total_interconnect_contention+=
							Integer.valueOf(total_num_packets_sent_array[i])*
							Float.valueOf(total_interconnect_network_contention_array[i]);
				}
			}
			
			line++;
			System.out.println("LINE:" +line);
	        data.put(line.toString(), new Object[] {cores, ""+time_main, ""+time_second, cache_write_miss_main+1, cache_read_miss_main+1, cache_write_miss_second+1, cache_read_miss_second+1,total_interconnect_contention.intValue(),0,total_dram_access_latency.intValue(),total_dram_contention_delay.intValue()});
	        cores = (int) Math.pow(2, line - 1);
	    }
		
		//Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(result_dir+"SimulationResults.xlsx"));
            workbook.write(out);
            out.close();
            
            System.out.println(result_dir+"SimulationResults.xlsx written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
}
