package auto_test;

import java.util.ArrayList;

public class AutoTest {
	private static ArrayList<Integer> numberCores(Integer min, Integer max) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = min; i < max; i++) {
			result.add((int) Math.pow(2, i));
		}
		return result;
	}

	private static ArrayList<Integer> numberCoresSniper(Integer min, Integer max) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = min; i < max; i++) {
			if(i == 5){
				result.add(35);
			} else if(i == 7){
				result.add(132);
			} else if(i == 9){
				result.add(528);
			}else if(i == 11){
				result.add(2070);
			}
			else
			result.add((int) Math.pow(2, i));
		}
		return result;
	}
	
	public static void main(String[] args) {
		final String graphite_path = "/home/inescid/graphite/";
		final String sniper_path = "/home/inescid/sniper-5.3/";
		final String test_folder_path = "/home/inescid/Desktop/autotests/";

		if(args.length  == 0 || args.length > 6){
			System.out.println("use --help option to obtain a listing of the availables options");
		}
		else{
			if (args[0].equals("--help")) {
				System.out.println("AutoTest sim_name cores_from cores_to option value");
				System.out.println(" sim_name: graphite, sniper    simulator to use");
				System.out.println(" cores_from: 0,1,...           number of cores for the first simulation (2^cores_from)");
				System.out.println(" cores_to: 1,2...              number of cores for the last simulation (2^cores_to), simulations are ran from (2^cores_from) up to (2^cores_to)");
				System.out.println(" option: cores,..              option of the sim.cfg to change");
				System.out.println(" value: 10,256..               value to assign to the option of the sim.cfg");
				System.out.println("");
				System.out.println("Example: AutoTest sniper parsec 0 10 --config-file 2d-mesh");
			}
			else if (args[0].equals("graphite")) {
				System.out
						.println("###################### STARTING GRAPHITE AUTOTESTS ######################");
				GrahpiteTestGenerator testgen = new GrahpiteTestGenerator(
						graphite_path, test_folder_path+"/graphite/");
				System.out.println(args[2] + " " + args[3]);
				GraphiteTest test = testgen.generateTest(
						args[1],
						numberCores(Integer.valueOf(args[2]),
								Integer.valueOf(args[3])), args[4], args[5]);
				test.execute();
				System.out
						.println("###################### END OF GRAPHITE AUTOTESTS ######################");
			}
			else if(args[0].equals("graphite-process")){
				GraphiteResultProcess rp = new GraphiteResultProcess(args[1]);
				rp.process();
				//graphite-process folder-with-tests
			}
			else if(args[0].equals("sniper")){
				System.out.println("###################### STARTING SNIPER AUTOTESTS ######################");
				//System.out.println("##### SNIPER requires BENCHMARKS_ROOT and SNIPER_ROOT to be set");
				SniperTestGenerator testgen = new SniperTestGenerator(
						sniper_path, test_folder_path+"sniper/");
				System.out.println(args[1] + " " + args[2]);
				SniperTest test = testgen.generateTest(
						args[1], // command
						numberCoresSniper(Integer.valueOf(args[2]), 
								Integer.valueOf(args[3])), 
								args[4], //option
								args[5]); //value
				test.execute();
				System.out
				.println("###################### END OF SNIPER AUTOTESTS ######################");
				//sniper parsec 2 3 --config-file 2d-mesh
			}
			else if(args[0].equals("sniper-process")){
				SniperResultProcess rp = new SniperResultProcess(args[1], args[2]);
				rp.process();
				//sniper-process folder-with-tests swaptions
			}
		}
		
		
		

	}
}
