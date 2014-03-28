package auto_test;

import java.util.ArrayList;
import java.util.Calendar;

public class SniperTestGenerator {

	private String sniper_path;
	private String sniper_benchmarks_path;
	private String test_folder_path; 
	
	SniperTestGenerator(String sniper_path, String test_folder_path){
		this.sniper_path = sniper_path;
		this.sniper_benchmarks_path = sniper_path+"benchmarks/";
		this.test_folder_path = test_folder_path;
	}
	
	public SniperTest generateTest(String command, ArrayList<Integer> num_cores, String option, String option_value){
		//creates the contents of the test sh 
		ArrayList<String> new_test = new ArrayList<String>();
		String test_name = "standard_value_for_test_name";
		String test_folder_with_date = "standard_value_for_test_folder_with_date";
		if(command.equals("parsec")){
			String[] parsec_workloads = new String[]{"blackscholes","canneal","dedup","ferret","fluidanimate","freqmine","raytrace","swaptions","vips","x264"/*"canneal"/*"swaptions","blackscholes"*/};//,"raytrace","vips","x264","bodytrack","canneal","dedup","fluidanimate","freqmine","facesim"};//falta streamcluster
			String config_file="base";
			test_name = new String(command+"-"+option_value+"-"+Calendar.getInstance().getTime()).replace(" ", "");
			test_folder_with_date = test_folder_path+test_name+"/";
			if(!option_value.equals("none")) 
				config_file = option_value;
			
			new_test.add("echo \"Automatic test by AutoTest for Sniper with to run the "+command+" benchmark \"\n");
			new_test.add("mkdir '"+test_folder_with_date+"'\n");
			new_test.add("cd "+sniper_benchmarks_path+"\n");
			
			for(String workload : parsec_workloads)
				for(Integer cores_tmp: num_cores){
					new_test.add("");
					new_test.add("echo \"EXECUTING WORKLOAD:"+workload+" WITH "+cores_tmp+" CORES\"\n");
					new_test.add("time ./run-sniper -p parsec-"+workload+" -i medium -n "+cores_tmp+" -c "+config_file+" -d "+test_folder_with_date+"parsec-"+workload+"-"+cores_tmp+"\n");
					new_test.add("cd "+test_folder_with_date+"parsec-"+workload+"-"+cores_tmp+"\n");
					new_test.add("python /home/inescid/sniper-5.2/tools/dumpstats.py > stats_out.txt\n");
					new_test.add("python /home/inescid/sniper-5.2/tools/cpistack.py\n");
					new_test.add("python /home/inescid/sniper-5.2/tools/gen_topology.py\n");
					new_test.add("cd "+sniper_benchmarks_path+"\n");
				}
		}
		
		new_test.add("echo \"Automatic test by AutoTest for: "+command+" finshed!\""+"\n");
		
		//writes them into a file
		String new_sh_test_path = test_folder_path+test_name+".sh";
		FileUtils.writeFile(new_test, new_sh_test_path);
		return new SniperTest(new_sh_test_path, sniper_path, test_folder_with_date, option, option_value);
	}
}