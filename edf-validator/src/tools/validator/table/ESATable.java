package tools.validator.table;

import java.io.File;

import javax.swing.table.TableModel;

import tools.validator.header.ESA;
import tools.validator.header.ESAChannel;
import tools.validator.header.ESAHeader;

/**
 * An ESA table used to represent the data model of the EDF Signal Attributes
 */
@SuppressWarnings("serial")
public class ESATable extends EDFTable {
	public String debugString = "ESA DEBUG:\n";
	
    private File hostFile; // keep the location of the source File for this header.
//    private TableColumn[] allTableColumns;
    private final static int index_of_immutablie = 8;
    protected static final int[] immutableFieldIndices = { index_of_immutablie };

    /**
     * This constructor builds a table with 1 empty row
     */
    public ESATable() {
        super(new ESATableModel(1));  
    }
    
    /**
     * Constructs ESATable using an ESATableModel
     * @param esa the table model
     */
    public ESATable(ESATableModel esa) {
    	super(esa);
    }
    
    public ESATable(ESAHeader esaHeader) {
    	createESATable(esaHeader);
    }
    
    /**
     * Updates the ESATable using an ESAHeader
     * @param edfFileHeader the ESAHeader used to update this ESATable
     */
    public void updateTable(ESAHeader edfFileHeader) {

    	TableModel model = this.getModel();
        int nrows = model.getRowCount();
        int ncols = model.getColumnCount();
        
        String value;
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                value = edfFileHeader.getValueAt(i, j);
                model.setValueAt(value, i, j);
            }
        }
    }
    
    /**
     * Creates the ESATable using an ESAHeader, one header corresponds to one ESA Table
     * @param esaHeader ESA header the ESA header used
     */
    public void createESATable(ESAHeader esaHeader) {
    	// algorithm: map each attribute value of each channel to each cell
        int nChannels = esaHeader.getNumberOfChannels();
        this.setModel(new ESATableModel(nChannels)); //this line is required; otherwise, getValueAt does not work
        ESAChannel esaChannel;
        String key, aValue;
        for (int nrow = 0; nrow < nChannels; nrow++)
            for (int ncolumn = 0; ncolumn < ESA.NUMBER_OF_ATTRIBUTES; ncolumn++) {
                esaChannel = esaHeader.getEsaChannelAt(nrow);
                key = ESA.getESAAttributes()[ncolumn];
                aValue = (String) esaChannel.getSignalAttributeValueAt(key);
                debugString += "Key: " + key + "; Value " + aValue + "\n";
                //System.out.println(aValue);
                this.setValueAt(aValue, nrow, ncolumn); //map attribute values to cells
            }
    }
    
    /**
     * Sets the master file of this ESATable
     * @param sourceOfMasterFile the file this ESATable belongs to
     */
    public void setSourceMasterFile(File sourceOfMasterFile) {
        this.hostFile = sourceOfMasterFile;
    }

    /**
     * Gets the master file of this ESATable
     * @return the master file this ESATable belongs to 
     */
    public File getSourceMasterFile() {
        return hostFile;
    }
    
    /**
     * For debug
     */
    public String toString() {
    	
    	return debugString;
    }
}
