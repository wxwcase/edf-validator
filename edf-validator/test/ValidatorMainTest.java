import tools.validator.ValidatorMain;

public class ValidatorMainTest {
	
	public static String edf = "/Users/wei/Desktop/NSRR_Current_Working/A-Test-Resource/123.edf";
	public static String out = "/Users/wei/Desktop/NSRR_Current_Working/A-Test-Resource/ValidatorTest/TestLog.txt";	
	
	public static void main(String[] args) {
		run();
	}
	
	public static void run() {
		ValidatorMain vm = new ValidatorMain(edf, out);
		vm.conductValidity();		
	}
}