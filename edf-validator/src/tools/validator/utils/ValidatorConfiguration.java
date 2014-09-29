package tools.validator.utils;

public class ValidatorConfiguration {
	public static final boolean mac_os;
    static {
        if (System.getProperty("os.name").contains("Mac OS")) {
            mac_os = true;
            System.setProperty("apple.laf.useScreenMenuBar", "true");   
        } else {
            mac_os = false;
        }
    }
}
