package com.onemoonscientific.swank.chart;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.MessageFormat;
import java.util.Date;

import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;
import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;

/**
 * A standard tool tip generator for use with an 
 * {@link org.jfree.chart.renderer.xy.XYItemRenderer}.
 */
public class DCXYToolTipGenerator extends AbstractXYItemLabelGenerator  
                                        implements XYToolTipGenerator,
                                                   Cloneable, 
                                                   PublicCloneable,
                                                   Serializable {

    /** The string used to represent 'null' for the x-value. */
    private String nullXString = "null";

    /** The string used to represent 'null' for the y-value. */
    private String nullYString = "null";

    /** For serialization. */
    private static final long serialVersionUID = -3564164459039540784L;    
    
    /** The default tooltip format. */
    public static final String DEFAULT_TOOL_TIP_FORMAT = "{0}: ({1}, {2})";

    /**
     * Returns a tool tip generator that formats the x-values as dates and the 
     * y-values as numbers.
     * 
     * @return A tool tip generator (never <code>null</code>).
     */
    public static DCXYToolTipGenerator getTimeSeriesInstance() {
        return new DCXYToolTipGenerator(
            DEFAULT_TOOL_TIP_FORMAT, DateFormat.getInstance(), 
            NumberFormat.getInstance()
        );
    }
    
    /**
     * Creates a tool tip generator using default number formatters.
     */
    public DCXYToolTipGenerator() {
        this(
            DEFAULT_TOOL_TIP_FORMAT,
            NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance()
        );
    }

    /**
     * Creates a tool tip generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not
     *                      permitted).
     * @param xFormat  the format object for the x values (<code>null</code> 
     *                 not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> 
     *                 not permitted).
     */
    public DCXYToolTipGenerator(String formatString,
                                      NumberFormat xFormat, 
                                      NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }
    /**
     * Generates a label string for an item in the dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The label (possibly <code>null</code>).
     */
    public String generateLabelString(XYDataset dataset, int series, int item) {
        String result = null;
        Object[] items = createItemArray(dataset, series, item);
        result = MessageFormat.format(getFormatString(), items);
        return result;
    }
 

    /**
     * Creates a tool tip generator using the specified number formatters.
     *
     * @param formatString  the label format string (<code>null</code> not 
     *                      permitted).
     * @param xFormat  the format object for the x values (<code>null</code> 
     *                 not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> 
     *                 not permitted).
     */
    public DCXYToolTipGenerator(String formatString,
                                      DateFormat xFormat, 
                                      NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }

    /**
     * Creates a tool tip generator using the specified date formatters.
     *
     * @param formatString  the label format string (<code>null</code> not 
     *                      permitted).
     * @param xFormat  the format object for the x values (<code>null</code> 
     *                 not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> 
     *                 not permitted).
     */
    public DCXYToolTipGenerator(String formatString,
                                      DateFormat xFormat, 
                                      DateFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }
    /**
     * Creates the array of items that can be passed to the
     * {@link MessageFormat} class for creating labels.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The items (never <code>null</code>).
     */
    protected Object[] createItemArray(XYDataset dataset, int series,
                                       int item) {
        Object[] result = new Object[4];
        result[0] = dataset.getSeriesKey(series).toString();
        int row = item;
        if (dataset instanceof XYTableGroupData) {
             row = ( (XYTableGroupData) dataset).getTableRow(series, item);
        }

        result[1] = String.valueOf(row);

        double x = dataset.getXValue(series, item);
        if (Double.isNaN(x) && dataset.getX(series, item) == null) {
            result[2] = this.nullXString;
        }
        else {
            if (getXDateFormat() != null) {
                result[2] = getXDateFormat().format(new Date((long) x));
            }
            else {
                result[2] = getXFormat().format(x);
            }
        }

        double y = dataset.getYValue(series, item);
        if (Double.isNaN(y) && dataset.getY(series, item) == null) {
            result[3] = this.nullYString;
        }
        else {
            if (getYDateFormat() != null) {
                result[3] = getYDateFormat().format(new Date((long) y));
            }
            else {
                result[3] = getYFormat().format(y);
            }
        }
        return result;
    }

    /**
     * Generates the tool tip text for an item in a dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The tooltip text (possibly <code>null</code>).
     */
    public String generateToolTip(XYDataset dataset, int series, int item) {
        return generateLabelString(dataset, series, item);
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param obj  the other object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DCXYToolTipGenerator) {
            return super.equals(obj);
        }
        return false;
    }

    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    public Object clone() throws CloneNotSupportedException { 
        return super.clone();
    }
    
}
