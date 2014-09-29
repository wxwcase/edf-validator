package tools.validator.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import tools.validator.ValidatorMain;
import tools.validator.table.EDFTable;
import tools.validator.table.EIATable;
import tools.validator.table.ESATable;

public class ValidatorUtility {
	
	/**
	 * Parses ESA table and records Incompliances
	 * @param esaTable the ESA table to be processed
	 * @param edfFile the EDF file name
	 * @return an array of Incompliances
	 */
	public static ArrayList<Incompliance> parseESATable(EDFTable esaTable, String edfFile) {
		
		ArrayList<Incompliance> esaIncompliances = new ArrayList<Incompliance>();
		String incomplianceType;
		int errorSrcTypeIndex = -1;
		int nrow = esaTable.getRowCount();
		String fileName = edfFile;
		Incompliance incomp;
		String description;
		boolean bASCII;
		int col;
		
		/////
		int COL_INDEX_LABEL;
		int COL_INDEX_CORRECTED_LABEL = -1;
		int COL_INDEX_TRANSDUCER_TYPE;
		int COL_INDEX_PHYSICAL_DIMENSION;
		int COL_INDEX_PHYSICAL_MINIMUM;
		int COL_INDEX_PHYSICAL_MAXIMUM;
		int COL_INDEX_DIGITAL_MINIMUM;
		int COL_INDEX_DIGITAL_MAXIMUM;
		int COL_INDEX_PREFILTERING;
		int COL_INDEX_NR_OF_SAMPLES;
		int COL_INDEX_RESERVED;
		/////
		
		if (esaTable instanceof ESATable) {
			errorSrcTypeIndex = Incompliance.index_incomp_src_esa;
			incomplianceType = Incompliance.typeOfErrorHeader[errorSrcTypeIndex];
			
			// Check "ESA Table" fields
			COL_INDEX_LABEL = 0;
			COL_INDEX_TRANSDUCER_TYPE = 1;
			COL_INDEX_PHYSICAL_DIMENSION = 2;
			COL_INDEX_PHYSICAL_MINIMUM = 3;
			COL_INDEX_PHYSICAL_MAXIMUM = 4;
			COL_INDEX_DIGITAL_MINIMUM = 5;
			COL_INDEX_DIGITAL_MAXIMUM = 6;
			COL_INDEX_PREFILTERING = 7;
			COL_INDEX_NR_OF_SAMPLES = 8;
			COL_INDEX_RESERVED = 9;
		} else {
			return esaIncompliances; // at this time, esaIncompliances.size() = 0;
		}
		
		for (int i = 0; i < nrow; i++) { // TODO
			
			/************************************************************
			 * ns * 16 ascii : ns * label (e.g. EEG Fpz-Cz or Body temp)
			 ************************************************************/
			col = COL_INDEX_LABEL;
			String alabel = (String) esaTable.getModel().getValueAt(i, col);
			if (alabel == null || alabel.equals("")) {
				//[Label](K.3) cannot be empty field
				description = Incompliance.error_esa_empty;
				incomp = new Incompliance(incomplianceType, description,
						fileName, i, col, errorSrcTypeIndex);
				esaIncompliances.add(incomp);
			} else {
				//[Label](K.1) check for ascii 
				bASCII = checkAsciiF(alabel);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
				
				//[Label](K.2) no duplicate signal labels
				boolean repeated = false;
				description = Incompliance.error_esa_label + (i + 1);
				for (int j = i + 1; j < nrow; j++) {
					String blabel = (String) esaTable.getModel().getValueAt(j, col);
					if (alabel.equalsIgnoreCase(blabel)) {
						repeated = true;
						description = description + ", " + (j + 1);
					}
				}
				if (repeated) {
					incomp = new Incompliance(incomplianceType, description, fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
			}			
			
			/************************************************************
			 * ns * 80 ascii : ns * transducer type (e.g. AgAgCl electrode) 
			 ************************************************************/
			col = COL_INDEX_TRANSDUCER_TYPE;
			String transducer_type = (String)esaTable.getModel().getValueAt(i, col);
			if (transducer_type==null || transducer_type.equals("")) {
				//[Transducer_Type](L.2) can be empty field
			} else {
				//[Transducer_Type](L.1) check for ascii
				bASCII = checkAsciiF(transducer_type);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description, fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
			}

			/************************************************************
			 * ns * 8 ascii : ns * physical dimension (e.g. uV or degreeC) 
			 ************************************************************/
			col = COL_INDEX_PHYSICAL_DIMENSION;
			String physical_dimension = (String)esaTable.getModel().getValueAt(i, col);
			if (physical_dimension==null || physical_dimension.equals("")){
				//[Physical_Dimension](M.2) can be empty field
			} else {
				//[Physical_Dimension](M.1) check for ascii
				bASCII = checkAsciiF(physical_dimension);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
			}

			/************************************************************
			 * ns * 8 ascii : ns * physical minimum (e.g. -500 or 34) 
			 ************************************************************/
			col = COL_INDEX_PHYSICAL_MINIMUM;
			String physical_minimum = (String)esaTable.getModel().getValueAt(i, col);
			boolean bGood_physical_minimum = false;
			if (physical_minimum==null || physical_minimum.equals("")){
				//[Physical_Minimum](N.5) cannot be empty field
				description = Incompliance.error_esa_empty;
				incomp = new Incompliance(incomplianceType, description,
						fileName, i, col, errorSrcTypeIndex);
				esaIncompliances.add(incomp);
			} else {
				//[Physical_Minimum](N.1) check for ascii
				bASCII = checkAsciiF(physical_minimum);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				} else {
					//[Physical_Minimum](N.2) is a floating point number
					try{
						Float.parseFloat(physical_minimum);
						bGood_physical_minimum = true;
					} catch (NumberFormatException e) {
						description = Incompliance.error_esa_phymin;
						incomp = new Incompliance(incomplianceType, description,
								fileName, i, col, errorSrcTypeIndex);
						esaIncompliances.add(incomp);
					}
				}
			}
			
			/************************************************************
			 * ns * 8 ascii : ns * physical maximum (e.g. 500 or 40) 
			 ************************************************************/
			col = COL_INDEX_PHYSICAL_MAXIMUM;
			String physical_maximum = (String)esaTable.getModel().getValueAt(i, col);
			boolean bGood_physical_maximum = false;
			if (physical_maximum == null || physical_maximum.equals("")){
				//[Physical_Maximum](O.4) cannot be empty field
				description = Incompliance.error_esa_empty;
				incomp = new Incompliance(incomplianceType, description,
						fileName, i, col, errorSrcTypeIndex);
				esaIncompliances.add(incomp);
			} else {
				//[Physical_Maximum](O.1) check for ascii
				bASCII = checkAsciiF(physical_maximum);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				} else {
					//[Physical_Maximum](O.2) is a floating point number
					try {
						Float.parseFloat(physical_maximum);
						bGood_physical_maximum = true;
					} catch (NumberFormatException e) {
						description = Incompliance.error_esa_phymax;
						incomp = new Incompliance(incomplianceType, description,
								fileName, i, col, errorSrcTypeIndex);
						esaIncompliances.add(incomp);
					}
				}
			}		
			
			if (bGood_physical_minimum && bGood_physical_maximum) {
				float phy_minimum = Float.parseFloat(physical_minimum);
				float phy_maximum = Float.parseFloat(physical_maximum);
				
				//[Physical_Maximum](O.3) physical maximum NOT = physical minimum
				if (phy_minimum == phy_maximum) {
					description = Incompliance.error_esa_phy_equal;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
				
				//[Physical_Maximum](O.5) physical minimum < physical maximum
				else if (phy_minimum > phy_maximum) {
					description = Incompliance.error_esa_phymaxmin;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
			}

			/************************************************************
			 * ns * 8 ascii : ns * digital minimum (e.g. -2048)
			 ************************************************************/
			col = COL_INDEX_DIGITAL_MINIMUM;
			String digital_minimum = (String)esaTable.getModel().getValueAt(i, col);
			boolean bGood_digital_minimum = false;
			if (digital_minimum == null || digital_minimum.equals("")) {
				//[Digital_Minimum](P.4) cannot be empty field
				description = Incompliance.error_esa_empty;
				incomp = new Incompliance(incomplianceType, description,
						fileName, i, col, errorSrcTypeIndex);
				esaIncompliances.add(incomp);
			} else {
				//[Digital_Minimum](P.1) check for ascii
				bASCII = checkAsciiF(digital_minimum);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				} else {
					//[Digital_Minimum](P.2) is an integer
					try {
						int dig_minimum = Integer.parseInt(digital_minimum);
						bGood_digital_minimum = true;
						
						//[Digital_Minimum](P.3) since each date sample is a 2-byte integer, range check [-32768,32767]
						boolean bRange = check_digital_range(dig_minimum);
						if (!bRange) {
							description = Incompliance.error_esa_digrange;
							incomp = new Incompliance(incomplianceType, description,
									fileName, i, col, errorSrcTypeIndex);
							esaIncompliances.add(incomp);
						}
					} catch (NumberFormatException e) {
						description = Incompliance.error_esa_digmin;
						incomp = new Incompliance(incomplianceType, description,
								fileName, i, col, errorSrcTypeIndex);
						esaIncompliances.add(incomp);
					}
				}
			}

			/************************************************************
			 * ns * 8 ascii : ns * digital maximum (e.g. 2047) 
			 ************************************************************/
			col = COL_INDEX_DIGITAL_MAXIMUM;
			String digital_maximum = (String)esaTable.getModel().getValueAt(i, col);
			boolean bGood_digital_maximum = false;
			if (digital_maximum == null || digital_maximum.equals("")) {
				//[Digital_Maximum](Q.4) cannot be empty field
				description = Incompliance.error_esa_empty;
				incomp = new Incompliance(incomplianceType, description,
						fileName, i, col, errorSrcTypeIndex);
				esaIncompliances.add(incomp);
			} else {
				//[Digital_Maximum](Q.1) check for ascii
				bASCII = checkAsciiF(digital_maximum);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				} else {
					//[Digital_Maximum](Q.2) is an integer
					try {
						int dig_maximum = Integer.parseInt(digital_maximum);
						bGood_digital_maximum = true;
						
						//[Digital_Maximum](Q.3) since each date sample is a 2-byte integer, range check [-32768,32767]
						boolean bRange = check_digital_range(dig_maximum);
						if (!bRange) {
							description = Incompliance.error_esa_digrange;
							incomp = new Incompliance(incomplianceType, description,
									fileName, i, col, errorSrcTypeIndex);
							esaIncompliances.add(incomp);
						}
					} catch (NumberFormatException e) {
						description = Incompliance.error_esa_digmax;
						incomp = new Incompliance(incomplianceType, description,
								fileName, i, col, errorSrcTypeIndex);
						esaIncompliances.add(incomp);
					}
				}
			}

			if (bGood_digital_minimum && bGood_digital_maximum) {
				int dig_minimum = Integer.parseInt(digital_minimum);
				int dig_maximum = Integer.parseInt(digital_maximum);
				
				//[Digital_Minimum](Q.6) digital minimum NOT = digital maximum (division-by-0 condition)
				if (dig_minimum == dig_maximum) {
					description = Incompliance.error_esa_dig_equal;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
				//[Digital_Minimum](Q.5) digital minimum < digital maximum
				else if (dig_minimum > dig_maximum) {
					description = Incompliance.error_esa_digmaxmin;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
			}

			/************************************************************
			 * ns * 80 ascii : ns * prefiltering (e.g. HP:0.1Hz LP:75Hz) 
			 ************************************************************/
			col = COL_INDEX_PREFILTERING;
			String prefiltering = (String)esaTable.getModel().getValueAt(i, col);
			if (prefiltering==null || prefiltering.equals("")) {
				//[Prefiltering](R.2) can be empty field
			} else {
				//[Prefiltering](R.1) check for ascii
				bASCII = checkAsciiF(prefiltering);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
			}
			
			/************************************************************
			 * ns * 8 ascii : ns * nr of samples in each data record 
			 ************************************************************/
			col = COL_INDEX_NR_OF_SAMPLES;
			String num_signals = (String)esaTable.getModel().getValueAt(i, col);
			if (num_signals == null || num_signals.equals("")){
				//[Num_signals](S.4) cannot be empty field
				description = Incompliance.error_esa_empty;
				incomp = new Incompliance(incomplianceType, description,
						fileName, i, col, errorSrcTypeIndex);
				esaIncompliances.add(incomp);
			} else {
				//[Num_signals](S.1) check for ascii
				bASCII = checkAsciiF(num_signals);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				} else {
					//[Num_signals](S.2) is an integer
					try {
						int n_signals = Integer.parseInt(num_signals);
						//[Num_signals](S.3) value is greater than 0
						if (n_signals <= 0){
							description = Incompliance.error_esa_nrSig_range;
							incomp = new Incompliance(incomplianceType, description,
									fileName, i, col, errorSrcTypeIndex);
							esaIncompliances.add(incomp);
						}
					} catch (NumberFormatException e) {
						description = Incompliance.error_esa_nrSig;
						incomp = new Incompliance(incomplianceType, description,
								fileName, i, col, errorSrcTypeIndex);
						esaIncompliances.add(incomp);
					}
				}
			}
			
			/************************************************************
			 * ns * 32 ascii : ns * reserved
			 ************************************************************/
			col = COL_INDEX_RESERVED;
			String reserved = (String)esaTable.getModel().getValueAt(i, col);
			if (reserved == null || reserved.equals("")){
				//[Reserved](T.2) can be empty field
			} else {
				//[Reserved](T.1) check for ascii
				bASCII = checkAsciiF(reserved);
				if (!bASCII) {
					description = Incompliance.error_esa_ascii;
					incomp = new Incompliance(incomplianceType, description,
							fileName, i, col, errorSrcTypeIndex);
					esaIncompliances.add(incomp);
				}
			}
			
		}//for loop ends
		
		if (esaIncompliances.size() > 0) {
			esaTable.setEdfValid(false);
		}
		
		return esaIncompliances;
	}
	
    /**
     * Parse EIA table and report the Incompliances
     * @param eiaTable the EIA table to be processed
     * @param edfFile a list of EDF files used to generate Incompliances
     * @return an array of Incompliances
     */
    public static ArrayList<Incompliance> parseEIATable(EIATable eiaTable, String edfFile) {
//    	System.out.println("parseEIATable >>>>" + eiaTable.toString()); // TODO
    	
    	ArrayList<Incompliance> eiaIncompliances = new ArrayList<Incompliance>();
    	
    	final int errorSrcTypeIndex = Incompliance.index_incomp_src_eia;
    	final String incomplianceType = Incompliance.typeOfErrorHeader[errorSrcTypeIndex];
    	Incompliance incomp;
    	String description;
    	boolean bASCII;
    	
        final int COL_INDEX_VERSION = 0;
        final int COL_INDEX_LOCAL_PATIENT_ID = 1;
        final int COL_INDEX_LOCAL_RECORDING_ID  = 2;
        final int COL_INDEX_START_DATE = 3;
        final int COL_INDEX_START_TIME = 4;
        final int COL_INDEX_NUMBER_OF_BYTES_IN_HEADER_RECORD = 5;
        final int COL_INDEX_RESERVED = 6;
        final int COL_INDEX_NUMBER_OF_DATA_RECORDS = 7;
        final int COL_INDEX_DURATION_OF_A_DATA_RECORD = 8;
        final int COL_INDEX_NUMBER_OF_SIGNALS_IN_DATA_RECORD = 9;
        
        int col;
        String fileName = edfFile;
        	
		/************************************************************
		 * 8 ascii : version of this data format (0) 
		 ************************************************************/
        col = COL_INDEX_VERSION + 1;
		String version = (String)eiaTable.getModel().getValueAt(0, col);
		System.out.println("Version: " + version); // TODO: test
		if (version == null || version.equals("")) {
			//[Version](A.3) cannot be empty field
			description = Incompliance.error_eia_empty;
			incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
			eiaIncompliances.add(incomp);
		} else {
			//[Version](A.1) check for ascii
			bASCII = checkAsciiF(version);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			} else {
				//[Version](A.2) is equal to 0
				try {
					int ver = Integer.parseInt(version);
					if (ver != 0) {
						description = Incompliance.error_eia_version;
						incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
						eiaIncompliances.add(incomp);
					}
				} catch (NumberFormatException e) {
					description = Incompliance.error_eia_version;
					incomp = new Incompliance(incomplianceType, description,
							fileName, 0, col, errorSrcTypeIndex);
					eiaIncompliances.add(incomp);
				}
			}
		}
			
		/************************************************************
		 * 80 ascii : local patient identification
		 ************************************************************/
		col = COL_INDEX_LOCAL_PATIENT_ID + 1;
		String partient_id = (String)eiaTable.getModel().getValueAt(0, col);
		if (partient_id == null || partient_id.equals("")){
			//[Partient_id](B.2) can be empty field
		} else {
			//[Partient_id](B.1) check for ascii
			bASCII = checkAsciiF(partient_id);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			}
		}

		/************************************************************
		 * 80 ascii : local recording identification
		 ************************************************************/
		col = COL_INDEX_LOCAL_RECORDING_ID + 1;
		String recording_id = (String)eiaTable.getModel().getValueAt(0, col);
		if (recording_id == null || recording_id.equals("")) {
			//[Recording_id](C.2) can be empty field
		} else {
			//[Recording_id](C.1) check for ascii
			bASCII = checkAsciiF(recording_id);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			}
		}
		
		/************************************************************
		 * 8 ascii : startdate of recording (dd.mm.yy) 
		 ************************************************************/
		col = COL_INDEX_START_DATE + 1;
		String startdate = (String)eiaTable.getModel().getValueAt(0, col);
		if (startdate == null || startdate.equals("")) {
			//[Startdate](D.2) cannot be empty field
			description = Incompliance.error_eia_empty;
			incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
			eiaIncompliances.add(incomp);
		} else {
			//[Startdate](D.1) check for ascii
			bASCII = checkAsciiF(startdate);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			} else {
				//[Startdate](D.4) separator between digits should be only �period�
				String[] items = startdate.split("\\.");
				if (items.length != 3) {
					 description = Incompliance.error_eia_dateformat;
					 incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
					 eiaIncompliances.add(incomp);
				} else {
					//[Startdate](D.3) dd:00-31, mm:00-12, yy:00-99
					try {
						int dd = Integer.parseInt(items[0]);
						int mm = Integer.parseInt(items[1]);
						int yy = Integer.parseInt(items[2]);
						if (dd >=0 && dd <=31 && mm >= 0 && mm <= 12 && yy >= 00 && yy <= 99) {
							//valid date format
						} else {
							description = Incompliance.error_eia_daterange;
							incomp = new Incompliance(incomplianceType, description,
									fileName, 0, col, errorSrcTypeIndex);
							eiaIncompliances.add(incomp);
						}
					} catch (NumberFormatException e) {
						description = Incompliance.error_eia_daterange;
						incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
						eiaIncompliances.add(incomp);
					}
				}
			}
		}
			
		/************************************************************
		 * 8 ascii : starttime of recording (hh.mm.ss) 
		 ************************************************************/
		col = COL_INDEX_START_TIME + 1;
		String starttime = (String)eiaTable.getModel().getValueAt(0, col);
		if (starttime==null || starttime.equals("")) {
			//[Start-time](E.2) cannot be empty field
			description = Incompliance.error_eia_empty;
			incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
			eiaIncompliances.add(incomp);
		} else {
			//[Start-time](E.1) check for ascii
			bASCII = checkAsciiF(starttime);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			} else {
				//[Start-time](E.4) separator between digits should be only �period�
				String[] items = starttime.split("\\.");
				if (items.length != 3) {
					 description = Incompliance.error_eia_timeformat;
					 incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
					 eiaIncompliances.add(incomp);
				} else {
					//[Start-time](E.3) hh:00-23, mm:00-59, ss:00-59
					try {
						int hh = Integer.parseInt(items[0]);
						int mm = Integer.parseInt(items[1]);
						int ss = Integer.parseInt(items[2]);
						if (hh >=0 && hh <= 23 && mm >= 0 && mm <= 59 && ss >= 00 && ss <= 59) {
							//valid time format
						} else {
							description = Incompliance.error_eia_timerange;
							incomp = new Incompliance(incomplianceType, description,
									fileName, 0, col, errorSrcTypeIndex);
							eiaIncompliances.add(incomp);
						}
					} catch (NumberFormatException e) {
						description = Incompliance.error_eia_timerange;
						incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
						eiaIncompliances.add(incomp);
					}
				}
			}
		}
			
		/************************************************************
		 * 8 ascii : number of bytes in header record 
		 ************************************************************/
		col = COL_INDEX_NUMBER_OF_BYTES_IN_HEADER_RECORD + 1;
		String nBytes = (String)eiaTable.getModel().getValueAt(0, col);
		if (nBytes == null || nBytes.equals("")) {
			//[Number_of_bytes](F.2) should not be empty
			description = Incompliance.error_eia_empty;
			incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
			eiaIncompliances.add(incomp);
		} else {
			//[Number_of_bytes](F.1) check for ascii
			bASCII = checkAsciiF(nBytes);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			} else {
				//[Number_of_bytes](F.3) is an integer
				try {
					int nbytes = Integer.parseInt(nBytes);
					if (nbytes <= 0){
						description = Incompliance.error_eia_num_bytes;
						incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
						eiaIncompliances.add(incomp);
					}
				} catch (NumberFormatException e) {
					description = Incompliance.error_eia_num_bytes;
					incomp = new Incompliance(incomplianceType, description,
							fileName, 0, col, errorSrcTypeIndex);
					eiaIncompliances.add(incomp);
				}
			}
		}
			
		/************************************************************
		 * 44 ascii : reserved 
		 ************************************************************/
		col = COL_INDEX_RESERVED + 1;
		String reserved = (String)eiaTable.getModel().getValueAt(0, col);
		if (reserved == null || reserved.equals("")){
			//[Reserved](G.2) can be empty field
		} else {
			//[Reserved](G.1) check for ascii
			bASCII = checkAsciiF(reserved);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			}
		}
		
		/************************************************************
		 * 8 ascii : number of data records (-1 if unknown)
		 ************************************************************/
		col = COL_INDEX_NUMBER_OF_DATA_RECORDS + 1;
		String nDataRecords = (String)eiaTable.getModel().getValueAt(0, col);
		if (nDataRecords == null || nDataRecords.equals("")){
			//[Num_of_DataRecords](H.2) should not be empty
			description = Incompliance.error_eia_empty;
			incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
			eiaIncompliances.add(incomp);
		} else {
			//[Num_of_DataRecords](H.1) check for ascii
			bASCII = checkAsciiF(nDataRecords);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			} else {
				//[Num_of_DataRecords](H.3) is a positive integer
				try {
					int ndatarecords = Integer.parseInt(nDataRecords);
					if (ndatarecords > 0 || ndatarecords == -1){
						//valid values
					} else {
						description = Incompliance.error_eia_num_records;
						incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
						eiaIncompliances.add(incomp);
					}
				} catch (NumberFormatException e) {
					description = Incompliance.error_eia_num_records;
					incomp = new Incompliance(incomplianceType, description,
							fileName, 0, col, errorSrcTypeIndex);
					eiaIncompliances.add(incomp);
				}
			}
		}
		
		/************************************************************
		 * 8 ascii : duration of a data record, in seconds 
		 ************************************************************/
		col = COL_INDEX_DURATION_OF_A_DATA_RECORD + 1;
		String duration = (String)eiaTable.getModel().getValueAt(0, col);
		if (duration == null || duration.equals("")) {
			//[Duration_of_a_data_record](I.2) should not be empty field
			description = Incompliance.error_eia_empty;
			incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
			eiaIncompliances.add(incomp);
		} else {
			//[Duration_of_a_data_record](I.1) check for ascii
			bASCII = checkAsciiF(duration);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			} else {
				//[Duration_of_a_data_record](I.3) is a positive floating point number (eg. 1, 0.2, 0.001)
				try {
					float dur = Float.parseFloat(duration);
					if (dur < 0) {
						description = Incompliance.error_eia_duration;
						incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
						eiaIncompliances.add(incomp);
					}
				} catch (NumberFormatException e) {
					description = Incompliance.error_eia_duration;
					incomp = new Incompliance(incomplianceType, description,
							fileName, 0, col, errorSrcTypeIndex);
					eiaIncompliances.add(incomp);
				}
			}
		}
			
		/************************************************************
		 * 4 ascii : number of signals (ns) in data record
		 ************************************************************/
		col = COL_INDEX_NUMBER_OF_SIGNALS_IN_DATA_RECORD + 1;
		String nSignals = (String)eiaTable.getModel().getValueAt(0, col);
		if (nSignals == null || nSignals.equals("")){
			//[Number_of_signals](J.2) cannot be empty
			description = Incompliance.error_eia_empty;
			incomp = new Incompliance(incomplianceType, description,
					fileName, 0, col, errorSrcTypeIndex);
			eiaIncompliances.add(incomp);
		} else {
			//[Number_of_signals](J.1) check for ascii
			bASCII = checkAsciiF(nSignals);
			if (!bASCII) {
				description = Incompliance.error_eia_ascii;
				incomp = new Incompliance(incomplianceType, description,
						fileName, 0, col, errorSrcTypeIndex);
				eiaIncompliances.add(incomp);
			} else {
				//[Number_of_signals](J.3) is a positive integer
				try {
					int nsignals = Integer.parseInt(nSignals);
					if (nsignals <= 0){
						description = Incompliance.error_eia_nsignals;
						incomp = new Incompliance(incomplianceType, description,
								fileName, 0, col, errorSrcTypeIndex);
						eiaIncompliances.add(incomp);
					}
				} catch (NumberFormatException e) {
					description = Incompliance.error_eia_nsignals;
					incomp = new Incompliance(incomplianceType, description,
							fileName, 0, col, errorSrcTypeIndex);
					eiaIncompliances.add(incomp);
				}
			}
		}			

		if (eiaIncompliances.size() > 0) {
			eiaTable.setEdfValid(false);
		}
		
        return eiaIncompliances;    
    }
	
    private static boolean checkAsciiF(String text) {
    	if (text == null){
    		return false;
    	}
    	else{
    		return text.matches("\\A\\p{ASCII}*\\z");
    	}
    }
    
    private static boolean check_digital_range(int x) {
    	return x >= -32768 && x <= 32767;
    }
    
    /**
	 * Generates error summary for EDF Header 
	 * @param aggregateIncompliances a list of Incompliances
	 */
	public static void generateInvalidReport(ArrayList<Incompliance> aggregateIncompliances) {
		
		addElementIntoLog("===============================================================", true, ValidatorMain.log);
		addElementIntoLog("  => User start a validation task at " + MyDate.currentDateTime(), true, ValidatorMain.log);
		addElementIntoLog("  *  The total number of errors: " + aggregateIncompliances.size(), true, ValidatorMain.log);
		
		/************************************************************
		 * The below is improved code for output format of validation report
		 * By Gang Shu on Feb. 20, 2014
		 ************************************************************/
		HashMap<String, ArrayList<Incompliance>> map__edf_with_errors = new HashMap<String, ArrayList<Incompliance>>();
		for (Incompliance error : aggregateIncompliances){
			String filename = error.getFileName();
			ArrayList<Incompliance> errorAL = map__edf_with_errors.get(filename);
			if (errorAL == null)
				errorAL = new ArrayList<Incompliance>();
			errorAL.add(error);
			map__edf_with_errors.put(filename, errorAL);
		}
		
		Iterator<Entry<String, ArrayList<Incompliance>>> iterator = map__edf_with_errors.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, ArrayList<Incompliance>> entry = iterator.next();
			
			if (entry!=null && entry.getKey()!=null && entry.getValue()!=null){
				String message = "";
				message += "   ------------------" + "\r\n";
				message += "   EDF file: " + entry.getKey() + "\r\n";
				int i = 0;
				for (Incompliance error : entry.getValue()){
					message += "   +" + (++i) + " [Row: " + (error.getRowIndex() + 1) + ", Col: " + (error.getColumnIndex() + 1) + "] " + error.getDescription() + "\r\n";
				}
				addElementIntoLog(message, true, ValidatorMain.log);
			}
		}
		/************************************************************
		 * The above is improved code for output format of validation report
		 * By Gang Shu on Feb. 20, 2014
		 ************************************************************/
		
	}
	
	/**
     * Logs message. if showOnScreen is true, the message will also be printed on screen
     * @param message the message to be logged
     * @param showOnScreen true to print the message on screen
     * @param outfile output file name
     */
    public static void addElementIntoLog(String message, boolean showOnScreen, String outfile) {
    	
    	if (showOnScreen) {
			System.out.println(message);
    	}
    	
		BufferedWriter out = null;
		try {
			if (outfile.contains(File.separator))
				new File(outfile).getParentFile().mkdirs();
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile, (new File(outfile)).exists())));
			out.write(message + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
	 * Formalizes the file separator for the string 
	 * @param oldString the string to be formalized
	 * @return the formalized string
	 */
	public static String separatorReplacer(String oldString) {
		String newString = oldString;
		if (newString!=null) {
			newString = newString.replace("/", File.separator);
			newString = newString.replace("\\", File.separator);
			newString = newString.replace(File.separator + File.separator, File.separator);
		}
		return newString;
	}
}
