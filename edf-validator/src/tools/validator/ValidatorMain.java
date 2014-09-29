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

	private String edfPath;
	private File edfFile;
	private EDFFileHeader edfHeader;
	private ESATable esaTable;
	private EIATable eiaTable;
	private ArrayList<Incompliance> eiaIncompliances;
	private ArrayList<Incompliance> esaIncompliances;
	private ArrayList<Incompliance> aggregateIncompliances;
	
	public static String log = "log.txt";
	
	public ValidatorMain(String edfPath, String output) {		
		this.edfPath = edfPath;  
		edfFile = new File(edfPath);
		log = output;				
	}
	
	/**
	 * Conducts validation using EDF file directory and the output directory
	 * @param edfPath the EDF file path
	 * @param output the output directory 
	 */
	public void conductValidity() {

		edfPath = ValidatorUtility.separatorReplacer(edfPath);
		log = ValidatorUtility.separatorReplacer(log);
		
		validate(edfPath, log);
		
		ValidatorUtility.addElementIntoLog("   + : " + edfFile.getAbsolutePath(), true, log);
	}
	
	private void validate(String edfPath, String output) {
		if(edfFile != null)
			ValidatorUtility.addElementIntoLog("   + : " + edfFile.getAbsolutePath(), true, output);
		
		if (edfFile.exists()) {
			
			yieldEDFHeader();
			System.out.println("EDF header: " + edfHeader);			
			yieldEiaTable();
			System.out.println("EIA Table: " + eiaTable);
			yieldEsaTable();
			System.out.println("ESA Table: " + esaTable);

			if(eiaTable == null)
				eiaIncompliances = null;
			else 
				eiaIncompliances = eiaTable.parseEIATable(edfPath);
			
			if(esaTable == null)
				esaIncompliances = null;
			else 
				esaIncompliances = esaTable.parseESATable(edfPath);	
			
			// Test:
			System.out.println("111111111111");
			for(Incompliance in : eiaIncompliances) {
				System.out.println(in);
			}
			System.out.println("111111111111");
			System.out.println("222222222222");
			for(Incompliance in : esaIncompliances) {
				System.out.println(in);
			}
			System.out.println("222222222222");
			
			if(!eiaIncompliances.isEmpty())
				aggregateIncompliances.addAll(eiaIncompliances); // eiaIncompliances is null
			System.out.println("After adding EIA compliances");
			if(!esaIncompliances.isEmpty()) {
				System.out.println("ESA incompliances size(): " + esaIncompliances.size()); // test
				for(Incompliance inc : esaIncompliances) {
					aggregateIncompliances.add(inc); // TODO: Problem
				}
			}				
			System.out.println("After adding ESA compliances");
			ValidatorUtility.generateInvalidReport(aggregateIncompliances);
			System.out.println("After adding ALL compliances");
		} else {
			System.out.println("EDF file: \n" + edfPath + "\ndoes not exist");
		}
	}
	
	/**
     * This method is relocated and minor modification was made
     * @author wei wang, 5/21/2014
     */
    private void yieldEDFHeader() {
        try {
            RandomAccessFile raf = new RandomAccessFile(edfFile, "r");
            edfHeader = new EDFFileHeader(raf, edfFile, false);
        } catch (IOException f) {
        	// Test:
        	edfHeader = null;
        	System.out.println("Cannot open EDF file");
        }
    }
    
    /**
     * Builds the EIA Table
     */
	private void yieldEiaTable() {
        // iniEiaTable of type EIATable
        eiaTable = new EIATable(edfHeader);
    }
    
    /**
     * Constructs ESA Tables, 
     * one esa header corresponds to one esa table
     */
    private void yieldEsaTable() {
//      algorithm is:
//      1. acquire the eiaHeader of the current file;
//      2. construct the ESA table one channel after another;
//      3. update the status.        	
    	
    	// Get ESAHeader from each EDF file header    	
        ESAHeader esaHeader = edfHeader.getEsaHeader(); //1.
        // Create ESATable using ESAHeader        
        esaTable = new ESATable(esaHeader);
        // configure the status        
        esaTable.setSourceMasterFile(edfFile); // set source file
    }
}
