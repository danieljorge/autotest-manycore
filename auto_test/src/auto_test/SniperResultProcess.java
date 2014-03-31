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

public class SniperResultProcess {
	String result_dir;
	String option_value;
	String[] parsec;
    
	SniperResultProcess(String result_dir, String option_value){
		this.result_dir = result_dir;
		this.option_value = option_value;
		parsec = new String[]{"dedup"/*"facesim","raytrace","vips","x264"/*"canneal"/*,"swaptions","blackscholes"*/};//"facesim","raytrace","vips","x264","bodytrack","canneal","dedup","fluidanimate","freqmine","streamcluster"};
	}
	
	private static Integer coreNumberSniper(Integer i) {
		Integer result= -1;
			if(i == 5){
				result = 35;
			} else if(i == 7){
				result = 132;
			} else if(i == 9){
				result = 528;
			}
			else if(i == 11){
				result = 2070;
			}
			else
				result = (int) Math.pow(2, i);
		return result;
	}
	
	
	public void process(){
		for(String parsec_elem : parsec){
		{
			option_value = parsec_elem;
		//Blank workbook
	    XSSFWorkbook workbook = new XSSFWorkbook(); 
	    //Create a blank sheet
	    XSSFSheet sheet = workbook.createSheet("Simulation Results");
		//This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
		Integer cores=1, line=1;
		data.put(line.toString(), new Object[] {"Cores", "Execution time - Main core", "Execution time - Second Core", "Main Core - Cache Write Miss Rate", "Main Core - Cache Read Miss Rate", "Second Core - Cache Write Miss Rate", "Second Core - Cache Read Miss Rate", "Total Interconnect Network contention (ms)", "Total Directory entries", "Total DRAM access delay"});
		String simout_line, write_cache_line,read_cache_line, mesh_contention, directory_entries, dram_access_latency;
		boolean finished = false;
		while(!finished){
			simout_line= FileUtils.getLineStartingWith("  Time", result_dir+"parsec-"+parsec_elem+"-"+cores+"/sim.out");
			write_cache_line= FileUtils.getSecondLineStartingWith("    write miss rate", result_dir+"parsec-"+parsec_elem+"-"+cores+"/sim.out");
			read_cache_line= FileUtils.getSecondLineStartingWith("    read miss rate", result_dir+"parsec-"+parsec_elem+"-"+cores+"/sim.out");
			mesh_contention = FileUtils.getLineStartingWith("network.shmem-1.mesh.contention-delay =",result_dir+"parsec-"+parsec_elem+"-"+cores+"/stats_out.txt");
			directory_entries = FileUtils.getLineStartingWith("directory.entries-allocated =",result_dir+"parsec-"+parsec_elem+"-"+cores+"/stats_out.txt");
			dram_access_latency = FileUtils.getLineStartingWith("dram.total-access-latency =",result_dir+"parsec-"+parsec_elem+"-"+cores+"/stats_out.txt");
			if(simout_line.equals("") ){
				if(cores > 1000){
				finished = true;
				break;
				}
				else{
					line++;
					cores = coreNumberSniper(line - 1);
					continue;}
			}
			directory_entries= directory_entries.replace(" ", "");
			directory_entries= directory_entries.replace("directory.entries-allocated=", "");
			Long total_directory_entries = Long.valueOf(0);
			String[] split_directory_entries = directory_entries.split(",");
			for(int i = 0; i<split_directory_entries.length;i++){
				total_directory_entries += Long.valueOf(directory_entries.split(",")[i]);
			}
			
			
			mesh_contention= mesh_contention.replace(" ", "");
			mesh_contention= mesh_contention.replace("network.shmem-1.mesh.contention-delay=", "");
			Double total_mesh_contention = Double.valueOf(0);
			if(!mesh_contention.equals("NONE")){
			String[] split_mesh_contention = mesh_contention.split(",");
			System.out.println("NUMBER:"+split_mesh_contention+"   "+mesh_contention);
			for(int i = 0; i<split_mesh_contention.length;i++){
				System.out.println("NUMBER:"+i+" content: "+ mesh_contention.split(",")[i]);
				total_mesh_contention += Double.valueOf(mesh_contention.split(",")[i])/1000000000;
			}
			}
			
			
			dram_access_latency= dram_access_latency.replace(" ", "");
			dram_access_latency= dram_access_latency.replace("dram.total-access-latency=", "");
			Double  total_dram_access_latency = Double.valueOf(0);
			String[] split_dram_access_latency = dram_access_latency.split(",");
			for(int i = 0; i<split_dram_access_latency.length;i++){
			//	System.out.println("NUMBER:"+i+" content: "+ mesh_contention.split(",")[i]);
				total_dram_access_latency += Double.valueOf(dram_access_latency.split(",")[i])/1000000000;
			}
			
			
			simout_line = simout_line.replace(" ", "");
			simout_line = simout_line.replace("|", ",");
			
			write_cache_line = write_cache_line.replace(" ", "");
			write_cache_line = write_cache_line.replace("|", ",");
			read_cache_line = read_cache_line.replace(" ", "");
			read_cache_line = read_cache_line.replace("|", ",");
			//System.out.println("###"+simout_line+"@@@"+simout_line.split(",")[1]);
			//for(String tmp : simout_line.split(","))
				//System.out.println("$$$ "+tmp);
			String time_main = simout_line.split(",")[1];
			String time_second = time_main;
			if(cores > 1)
				time_second = simout_line.split(",")[2];
			/*String time_main = simout_line.split(",")[1];
			System.out.println("TIME:"+time_main);
			String time_second = time_main;
			if(cores > 1)
				time_second = simout_line.split(",")[2];
			*/
			
			String cache_write_miss_main =  (write_cache_line.split(",")[1]).replace("inf%", "-1").replace("%", "");
			String cache_write_miss_second = cache_write_miss_main;
			if(cores > 1)
				cache_write_miss_second = (write_cache_line.split(",")[2]).replace("inf%", "-1").replace("%", "");
			
			String cache_read_miss_main =  (read_cache_line.split(",")[1]).replace("inf%", "-1").replace("%", "");
			String cache_read_miss_second = cache_read_miss_main;
			if(cores > 1)
				cache_read_miss_second = (read_cache_line.split(",")[2]).replace("inf%", "-1").replace("%", "");
			
			line++;
			System.out.println("LINE:" +line);
	        data.put(line.toString(), new Object[] {cores, time_main, time_second, cache_write_miss_main, cache_read_miss_main, cache_write_miss_second, cache_read_miss_second, 
	        	""+total_mesh_contention, ""+total_directory_entries,""+total_dram_access_latency});
	        cores = coreNumberSniper(line - 1);
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
            FileOutputStream out = new FileOutputStream(new File(result_dir+option_value+"-simulationResults.xlsx"));
            workbook.write(out);
            out.close();
            
            System.out.println(result_dir+"SimulationResults.xlsx written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }}
	}
		}
}
