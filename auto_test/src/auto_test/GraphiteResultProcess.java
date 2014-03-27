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
	
	public void process(){
		//Blank workbook
	    XSSFWorkbook workbook = new XSSFWorkbook(); 
	    //Create a blank sheet
	    XSSFSheet sheet = workbook.createSheet("Simulation Results");
		//This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
		Integer cores=1, line=1;
		data.put(line.toString(), new Object[] {"Cores", "Execution time - Main core", "Execution time - Second Core", "Main Core - Cache Write Miss Rate", "Main Core - Cache Read Miss Rate", "Second Core - Cache Write Miss Rate", "Second Core - Cache Read Miss Rate","Total Interconnect Network contention (ms)", "Total Directory entries", "Total DRAM access delay (ns)","Total DRAM contention delay (ns)"});
		String simout_line, write_cache_line,read_cache_line, total_dram_accesses, dram_access_latency, dram_contention_delay;
		boolean finished = false;
		while(!finished){
			simout_line= FileUtils.getLineStartingWith("Completion Time", result_dir+cores+"/sim.out");
			write_cache_line= FileUtils.getLineStartingWith("Write Miss Rate (%)", result_dir+cores+"/sim.out");
			read_cache_line= FileUtils.getLineStartingWith("Read Miss Rate (%)", result_dir+cores+"/sim.out");
			total_dram_accesses= FileUtils.getLineStartingWith("Total Dram Accesses", result_dir+cores+"/sim.out");
			dram_access_latency= FileUtils.getLineStartingWith("Average Dram Access Latency (in nanoseconds)", result_dir+cores+"/sim.out");
			dram_contention_delay= FileUtils.getLineStartingWith("Average Dram Contention Delay (in nanoseconds)", result_dir+cores+"/sim.out");
			if(simout_line.equals("")){
				if(cores > 100){
				finished = true;
				break;
				}else{
					line++;
					cores++;
					continue;}
			}
			simout_line = simout_line.replace(" ", "");
			simout_line = simout_line.replace("|", ",");
			write_cache_line = write_cache_line.replace(" ", "");
			write_cache_line = write_cache_line.replace("|", ",");
			read_cache_line = read_cache_line.replace(" ", "");
			read_cache_line = read_cache_line.replace("|", ",");
			total_dram_accesses = total_dram_accesses.replace("Total Dram Accesses", "");
			total_dram_accesses = total_dram_accesses.replace(" ", "");
			total_dram_accesses = total_dram_accesses.replace("|", ",");
			dram_access_latency = dram_access_latency.replace("Average Dram Access Latency (in nanoseconds)", "");
			dram_access_latency = dram_access_latency.replace(" ", "");
			dram_access_latency = dram_access_latency.replace("|", ",");
			dram_contention_delay = dram_contention_delay.replace("Average Dram Contention Delay (in nanoseconds)", "");
			dram_contention_delay = dram_contention_delay.replace(" ", "");
			dram_contention_delay = dram_contention_delay.replace("|", ",");
			//System.out.println("###"+simout_line+"@@@"+simout_line.split(",")[1]);
			//for(String tmp : simout_line.split(","))
				//System.out.println("$$$ "+tmp);
			Long time_main = Long.valueOf(simout_line.split(",")[1]);
			Long time_second = time_main;
			if(cores > 1)
				time_second = Long.valueOf(simout_line.split(",")[2]);
			
			Integer cache_write_miss_main =  Integer.valueOf((write_cache_line.split(",")[1]).split("\\.")[0]);
			Integer cache_write_miss_second = cache_write_miss_main;
			if(cores > 1)
				cache_write_miss_second = Integer.valueOf((write_cache_line.split(",")[2]).split("\\.")[0]);
			
			Integer cache_read_miss_main =  Integer.valueOf((read_cache_line.split(",")[1]).split("\\.")[0]);
			Integer cache_read_miss_second = cache_read_miss_main;
			if(cores > 1)
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
			line++;
			System.out.println("LINE:" +line);
	        data.put(line.toString(), new Object[] {cores, time_main, time_second, cache_write_miss_main+1, cache_read_miss_main+1, cache_write_miss_second+1, cache_read_miss_second+1,0,0,total_dram_access_latency.intValue(),total_dram_contention_delay.intValue()});
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
