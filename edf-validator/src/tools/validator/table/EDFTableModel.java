package tools.validator.table;

import javax.swing.table.DefaultTableModel;

/**
 * An EDFTableModel is a DefaultTableModel that support undoable events
 */
@SuppressWarnings("serial")
public class EDFTableModel extends DefaultTableModel {
	
	/**
	 * Construct this EDFTableModel with a undo manager
	 */
    public EDFTableModel() {
        super();
    }
    
    /**
     * Construct an EDFTableModel with as many columns as there are
     * elements in <code>columnNames</code> and <code>rowCount</code> 
     * of <code>null</code> object values.  Each column's name will 
     * be taken from the <code>columnNames</code> array.
     * @param columnNames <code>array</code> containing the names of
     *                    the new columns; if this is <code>null</code>
     *                    then the model has no columns
     * @param nrow the number of rows the table holds
     */
    public EDFTableModel(Object[] columnNames, int nrow) {
        super(columnNames, nrow);
    }

    /**
     * Constructs a <code>EDFTableModel</code> and initializes the table
     * by passing <code>data</code> and <code>columnNames</code>
     * to the <code>setDataVector</code>
     * method. The first index in the <code>Object[][]</code> array is
     * the row index and the second is the column index.
     * @param data           the data of the table
     * @param columnNames    the names of the columns
     */
    public EDFTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    /**
     * Sets the object value for the cell at column and row. aValue is the new value
     * @param value the new value 
     * @param row the row whose value is to be changed
     * @param column the column whose value is to be changed
     * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object value, int row, int column) {
        super.setValueAt(value, row, column);
    }
}
