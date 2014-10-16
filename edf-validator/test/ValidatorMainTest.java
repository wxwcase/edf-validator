import tools.validator.ValidatorMain;

/**
 * @author wei wang, 2014-10-14
 */
public class ValidatorMainTest {
	
	public static String edf = "/Users/wei/Desktop/NSRR_Current_Working/A-Test-Resource/123.edf";
	public static String out = "/Users/wei/Desktop/NSRR_Current_Working/A-Test-Resource/ValidatorTest/log.txt";	
	public static String edf2 = "/Users/wei/git/edf-editor-translator/resource-wei/Compumedics/100022.EDF";
	public static String edf3 = "/Users/wei/git/edf-editor-translator/resource-wei/EmblaNew/10007_01262010s1.edf";
	
	public static void main(String[] args) {
		run();
	}
	
	public static void run() {
		ValidatorMain vm = new ValidatorMain();
		vm.setup(edf, out);
		vm.conductValidation();		
	}
}
