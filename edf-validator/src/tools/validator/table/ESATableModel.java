package tools.validator.table;

import tools.validator.header.ESA;
import tools.validator.header.ESAChannel;
import tools.validator.header.ESAHeader;

/**
 * An EDFTableModel that is used for ESA table data
 */
@SuppressWarnings("serial")
public class ESATableModel extends EDFTableModel {
	
	/**
	 * Construct default ESA table model
	 */
	public ESATableModel() {
        super();
    }
    
    /**
     * Construct ESA table model using specified row number
     * @param rows the number of rows this model contains
     */
    public ESATableModel(int rows) {
        super(ESA.getESAAttributes(), rows);
    }
    
    /**
     * Construct ESA table model using specified column names and 
     * number of rows
     * @param columnNames the column names of this model
     * @param nrow the number of rows
     */
    public ESATableModel(Object[] columnNames, int nrow) {
    	super(columnNames, nrow);
    }

    /**
     * get the ESAHeader image of the table model
     * @return an ESA header generated from this table model
     */
    public ESAHeader toESAHeader() {
        // TODO: transform table data to ESA header
        return new ESAHeader();
    }

     /**
      * Returns the ESA channel from a specified row index
      * usage: tableModel.toESAChannel(1)
      * @param rowIndex the index of the row
      * @return the ESA channel generated from the row
      */
    public ESAChannel toESAChannel(int rowIndex) {
        return new ESAChannel();
    }
}
