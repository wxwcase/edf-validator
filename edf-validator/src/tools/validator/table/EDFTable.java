package tools.validator.table;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import tools.validator.utils.Incompliance;
import tools.validator.utils.ValidatorConfiguration;
import tools.validator.utils.ValidatorUtility;

/**
 * A customized JTable to represent the ESA, EIA table information 
 */
@SuppressWarnings("serial")
public class EDFTable extends JTable {
	
    protected File masterFile = null; // usage: keep the master file of the table
    private boolean edfValid = true; // if all fields conform to EDF specification
    
    static final String physical_maximum_nonnumber_error = "Physical Maximum field should contain scalar value. ";
    static final String physical_minimum_nonnumber_error = "Physical Minimum field should contain scalar value. ";
    static final String digital_order_error = "Digtial Maximum should be larger than Digital Minimum.";
    static final String digital_field_blank_error = "Digital field should contain integer value.";
    
    static final String start_date_error = "Start date needs to be in the form of xx.xx.xx where x are integers";
    static final String altName = (ValidatorConfiguration.mac_os)? "Command": "Alt";

    /**
     * Set the validity of EDF file fields according to the EDF specification
     * @param valid true if all fields conform to EDF specification
     */
    public void setEdfValid(boolean valid) {
        this.edfValid = valid;
    }

    /**
     * Return the validity of EDF file fields of this table
     * @return true if the EDF file fields are valid
     */
    public boolean isEdfValid() {
        return edfValid;
    }

    /**
     * Default EDFTable constructor
     */
    public EDFTable() {
        this.getTableHeader().setReorderingAllowed(false);
    }

    /**
     * Construct EDFTable using a TableModel
     * @param tableModel the TableModel used
     */
    public EDFTable(TableModel tableModel) {
        super(tableModel);
        this.getTableHeader().setReorderingAllowed(false);
    }
    
    /** 
     * Returns <code>false</code> to indicate that the height of the viewport does
     * not determine the height of the table
     * @return false if the height of the viewport does not determine the height of
     * the table
     * @see javax.swing.JTable#getScrollableTracksViewportHeight()
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * Set the master file of this table
     * @param masterFile the host file
     */
    public void setMasterFile(File masterFile) {
        this.masterFile = masterFile;
    }

    /**
     * Returns the master file of this table
     * @return the master file
     */
    public File getMasterFile() {
        return masterFile;
    }
    
    /**
     * Validates ESA table
     * used for both ESA and ESA template tables
     * @author Fangping, 09/29/2010
     * @author Gang Shu, 02/20/2014, Validation improvement for ESA tables
	 * @return An array of Incompliances
	 */
	public ArrayList<Incompliance> parseESATable(String edfPath) {
		
//		String wrkFileName = this.getMasterFile().getAbsolutePath();
//		String srcFileName = "";
//		if (afile.getAbsolutePath().equals(wrkFileName)) {
//			srcFileName = afile.getAbsolutePath();
//		}
		
		return ValidatorUtility.parseESATable(this, edfPath);
	}

	/**
	 * Validates EIA tables
	 * @return an array list of Incompliances generated during parsing
	 */
    public ArrayList<Incompliance> parseEIATable(String edfPath) {
    	// test
//    	System.out.println("EDFTable >> parseEIATable(String edfPath)");
    	// 1. get edf-file name
    	// 2. validate edf file
    	
//    	int nrow = this.getRowCount();
//    	String[] fileList = new String[nrow];
//        for (int i = 0; i < nrow; i++) {
//        	String fileName = MainWindow.getSrcEdfFiles().get(i).getAbsolutePath();
//        	fileList[i] = fileName;
//        }
        
        return ValidatorUtility.parseEIATable((EIATable)this, edfPath);
    }
    
    protected final int[] lowerbounds = {0, 0, 0};
    protected final int[] upperbounds = {31, 12, 99}; // 31 day, 12 month, 2099 years    
}
