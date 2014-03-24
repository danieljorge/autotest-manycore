package auto_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraphiteTest {
	private String test_path;
	private String graphite_path;
	private String option;
	private String option_value;
	private String result_dir;

	GraphiteTest(String test_path, String graphite_path, String result_dir, String option, String option_value) {
		this.test_path = test_path;
		this.graphite_path = graphite_path;
		this.option = option;
		this.option_value = option_value;
		this.result_dir = result_dir;
	}
	
	private void saveCarbonCFG() {
		FileUtils.copyFileUsingStream(graphite_path + "carbon_sim.cfg",
				graphite_path + "carbon_sim_orig.cfg");
	}

	private void restoreCarbonCFG() {
		FileUtils.copyFileUsingStream(graphite_path + "carbon_sim_orig.cfg",
				graphite_path + "carbon_sim.cfg");
	}

	public void execute() {
		saveCarbonCFG();
		if(!option.equals(option_value))
			FileUtils.changeOption(option, option_value, graphite_path+"carbon_sim.cfg");
		ProcessBuilder pb = new ProcessBuilder(test_path);
		Process p;
		try {
			//Runtime.getRuntime().exec(new String[]{"/bin/chmod", " 777 "+test_path});
			p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out
					.println("ERROR: Test: execute: Error in the I/O! exception was: "
							+ e.toString());
		}
		restoreCarbonCFG();
		
		GraphiteResultProcess rp = new GraphiteResultProcess(result_dir);
		rp.process();
	}
}
