package auto_test;

import java.util.ArrayList;

public class GrahpiteTestGenerator {

	private String graphite_path;
	private String test_folder_path; 
	
	GrahpiteTestGenerator(String graphite_path, String test_folder_path){
		this.graphite_path = graphite_path;
		this.test_folder_path = test_folder_path;
	}
	
	public GraphiteTest generateTest(String command, ArrayList<Integer> num_cores, String option, String option_value){
		//creates the contents of the test sh 
		ArrayList<String> new_test = new ArrayList<String>();
		new_test.add("echo \"Automatic test by AutoTest for: "+command+"\"\n");
		new_test.add("mkdir "+test_folder_path+command+"\n");
		new_test.add("cd "+graphite_path+"\n");
		
		for(Integer cores_tmp: num_cores){
			new_test.add("");
			new_test.add("echo \""+cores_tmp+" CORES\""+"\n");
			new_test.add("make "+ command +"_app_test CORES="+cores_tmp+"\n");
			new_test.add("mkdir "+test_folder_path+command+"/"+cores_tmp+"\n");
			new_test.add("cp /home/inescid/graphite/results/latest/* "+test_folder_path+command+"/"+cores_tmp+"/\n");
		}
		
		new_test.add("echo \"Automatic test by AutoTest for: "+command+" finshed!\""+"\n");
		
		//writes them into a file
		String new_test_path = test_folder_path+command+"-"+option+"-"+option_value+".sh";
		FileUtils.writeFile(new_test, new_test_path);
		return new GraphiteTest(new_test_path, graphite_path, test_folder_path+command+"/", option, option_value);
	}
}
