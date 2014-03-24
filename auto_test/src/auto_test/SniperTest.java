package auto_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SniperTest {
	private String sh_test_path;
	private String sniper_path;
	private String option;
	private String option_value;
	private String result_dir;

	SniperTest(String test_path, String sniper_path, String result_dir, String option, String option_value) {
		this.sh_test_path = test_path;
		this.sniper_path = sniper_path;
		this.option = option;
		this.option_value = option_value;
		this.result_dir = result_dir;
	}

	public void execute() {
		ProcessBuilder pb = new ProcessBuilder(sh_test_path);
		Process p;
		try {
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
		SniperResultProcess rp = new SniperResultProcess(result_dir, option_value);
		rp.process();
	}
}
