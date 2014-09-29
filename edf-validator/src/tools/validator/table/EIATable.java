package tools.validator.table;

import java.util.ArrayList;

import javax.swing.table.TableModel;

import tools.validator.header.EDFFileHeader;
import tools.validator.header.EIA;
import tools.validator.header.EIAHeader;

/**
 * An EIATable is a customized EDFTable
 */
@SuppressWarnings("serial")
public class EIATable extends EDFTable {   
	
	public EDFFileHeader debugEdfHeader;
    
	public static final int number_local_patientID = 2;
    public static final int number_local_recordingID = 3;
    public static final int number_start_date_recording = 4;
    protected int[] immutableFieldIndices = {
    		EIA.index_version, // EIA.index_filename
    		EIA.index_start_time,
    		EIA.index_of_bytes, 
    		EIA.index_reserved, 
    		EIA.index_number_of_datarecord, 
    		EIA.index_duration,
    		EIA.index_number_of_channels
    };

    /**
     * Default constructor of EIATable using customized look of "eia"
     */
    public EIATable() { 
        super(new EIATableModel(1)); 
    }
    
    /**
     * Constructs EIATable using an EDF file header
     * @param edfHeader an EDF file header 
     */
    public EIATable(EDFFileHeader edfHeader) {    	
        super(new EIATableModel(1));
        debugEdfHeader = edfHeader;
        
        String key, value;
        EIAHeader eiaHeader;
        for (int ncolumn = 0; ncolumn < EIA.NUMBER_OF_ATTRIBUTES + 1; ncolumn++) {
            key = EIA.getEIAAttributeAt(ncolumn);
            eiaHeader = edfHeader.getEiaHeader();
            value = eiaHeader.getAttributeValueAt(key);
            this.getModel().setValueAt(value, 0, ncolumn);
        } 
    }
    
    /**
     * Using EDF headers to construct EIA tables
     * @param edfHeaders EDF headers used to construct EIA table
     * @param numberOfHeaders number of header rows
     */
    public EIATable(ArrayList<EDFFileHeader> edfHeaders, int numberOfHeaders) {
        super(new EIATableModel(numberOfHeaders));
       
        for (int nrow = 0; nrow < numberOfHeaders; nrow++)   
            for (int ncolumn = 0; ncolumn < EIA.NUMBER_OF_ATTRIBUTES + 1; ncolumn++) {
                String key = EIA.getEIAAttributeAt(ncolumn);
                EIAHeader eiaHeader = edfHeaders.get(nrow).getEiaHeader();
                String value = eiaHeader.getAttributeValueAt(key);
                this.getModel().setValueAt(value, nrow, ncolumn);
            }          
     }
    
    /**
     * Updates specified table row value
     * @param header the EIA header used to construct this EIA table
     * @param rowIndex the row to be updated
     */
    public void updateTableRow(EIAHeader header, int rowIndex) {
        TableModel model = this.getModel();
        String value;
        for (int i = 0; i < EIA.NUMBER_OF_ATTRIBUTES; i++) {
            value = header.getAttributeValueAt(EIA.getEIAAttributeAt(i + 1));
            model.setValueAt(value, rowIndex, i + 1);
        }
    }
    
    /**
     * For debug 
     */
    public String toString() {
    	String key = "", value = "";
        EIAHeader eiaHeader;
        String result = "DEBUG>>>>>>>>>>:\n";

    	for (int ncolumn = 0; ncolumn < EIA.NUMBER_OF_ATTRIBUTES + 1; ncolumn++) {
            key = EIA.getEIAAttributeAt(ncolumn);
            eiaHeader = debugEdfHeader.getEiaHeader();
            value = eiaHeader.getAttributeValueAt(key);
            result += "Key: " + key + "; Value: " + value + "\n";
        } 
    	return result;
    }
}
