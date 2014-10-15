package tools.validator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import tools.validator.header.EDFFileHeader;
import tools.validator.header.ESAHeader;
import tools.validator.table.EIATable;
import tools.validator.table.ESATable;
import tools.validator.utils.Incompliance;
import tools.validator.utils.ValidatorUtility;

public class ValidatorMain {
	
	public static String log = "log.txt";
	
	public ValidatorMain() {		
		
	}
	
	/**
	 * Sets up the environment for conduct validation 
	 * @param edfPath EDF file path
	 * @param logPath log file path
	 * @return
	 */
	public boolean setup(String edfPath, String logPath) {
		if(edfPath == null || edfPath == "") {
			ValidatorUtility.addElementIntoLog("   + : EDF file path is not valid", true, log);
			return false;
		}
		this.edfPath = edfPath;
		edfFile = new File(edfPath);
		log = logPath;
		aggregateIncompliances = new ArrayList<Incompliance>();
		return true;
	}
	
	/**
	 * Conducts validation using EDF file directory and the output directory
	 * @param edfPath the EDF file path
	 * @param output the output directory 
	 */
	public void conductValidation() {
		// standardize path
		edfPath = ValidatorUtility.separatorReplacer(edfPath);
		log = ValidatorUtility.separatorReplacer(log);
		
		// perform validation
		validate(edfPath, log);
	}
	
		
	private void validate(String edfPath, String log) {
		
		if(edfFile == null || edfPath == null ||edfPath == "") {
			ValidatorUtility.addElementIntoLog("   + : EDF file path is not valid", true, log);	
			return;
		}			
		
		if (edfFile.exists()) {
			// produce EDF header
			yieldEDFHeader();
			// produce EIA table
			yieldEIATable();
			// produce ESA table
			yieldESATable();

			if(eiaTable == null)
				eiaIncompliances = null;
			else 
				eiaIncompliances = eiaTable.parseEIATable(edfPath);
			
			if(esaTable == null)
				esaIncompliances = null;
			else 
				esaIncompliances = esaTable.parseESATable(edfPath);		
			
			if(!eiaIncompliances.isEmpty())
				aggregateIncompliances.addAll(eiaIncompliances); // eiaIncompliances is null
			System.out.println(">>> ----EIA incompliances size(): " + eiaIncompliances.size() + "----"); // test
			if(!esaIncompliances.isEmpty()) {
				System.out.println(">>> ----ESA incompliances size(): " + esaIncompliances.size() + "----"); // test
				for(Incompliance inc : esaIncompliances) {
					aggregateIncompliances.add(inc);
				}
			}				
			if(aggregateIncompliances != null)
				ValidatorUtility.generateInvalidReport(aggregateIncompliances);			
		} else {
			System.out.println("EDF file: \n" + edfPath + "\ndoes not exist"); // test
			// add the result to log
		}
	}
	
    private void yieldEDFHeader() {
        try {
            RandomAccessFile raf = new RandomAccessFile(edfFile, "r");
            edfHeader = new EDFFileHeader(raf, edfFile);
            System.out.println(">>> Yield EDF header done"); // test
        } catch (IOException f) {
        	edfHeader = null;
        	ValidatorUtility.addElementIntoLog("   + : Cannot open EDF file", true, log); // true: show on screen
        }
    }
    
    /**
     * Builds the EIA Table
     */
	private void yieldEIATable() {
        // iniEiaTable of type EIATable
        eiaTable = new EIATable(edfHeader);
        System.out.println(">>> Yield EIA table done"); // test
    }
    
    /**
     * Constructs ESA Tables, 
     * one esa header corresponds to one esa table
     */
    private void yieldESATable() {
    	// algorithm is:
    	// 1. acquire the eiaHeader of the current file;
    	// 2. construct the ESA table one channel after another;
    	// 3. update the status.        	
    	
    	// Get ESAHeader from each EDF file header    	
        ESAHeader esaHeader = edfHeader.getEsaHeader(); //1.
        // Create ESATable using ESAHeader        
        esaTable = new ESATable(esaHeader);
        // configure the status        
        esaTable.setSourceMasterFile(edfFile); // set source file
        System.out.println(">>> Yield ESA table done"); // test
    }
    
    private String edfPath;
	private File edfFile;
	private EDFFileHeader edfHeader;
	private ESATable esaTable;
	private EIATable eiaTable;
	private ArrayList<Incompliance> eiaIncompliances;
	private ArrayList<Incompliance> esaIncompliances;
	private ArrayList<Incompliance> aggregateIncompliances;
}
