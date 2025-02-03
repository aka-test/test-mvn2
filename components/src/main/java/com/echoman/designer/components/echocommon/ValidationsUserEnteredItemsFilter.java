/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.RowFilter;

/**
 *
 * @author david.morin
 */
public class ValidationsUserEnteredItemsFilter extends RowFilter {
    JTable table;

    /**
     * 
     * @param table
     */
    ValidationsUserEnteredItemsFilter(JTable table) {
        this.table = table;
    }

    /**
     * 
     * @param date
     * @return
     */
    public boolean isDate(String date) {
        DateFormat format = new SimpleDateFormat("d/M/yyyy");
        format.setLenient(false);
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 
     * @param filter
     * @param value
     * @param comparison
     * @return
     */
    public boolean compareDates(String filter, String value, String comparison) {
        String myFormatString = "d/M/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(myFormatString);
        Date filterDate;
        Date valueDate;
        int result = 0;
        try {
            filterDate = df.parse(filter);
            valueDate = df.parse(value);
            result = valueDate.compareTo(filterDate);
            if ((comparison.equals("<")) && (result < 0))
                return true;
            else if ((comparison.equals(">")) && (result > 0))
                return true;
            else if ((comparison.equals("=")) && (result == 0))
                return true;
            else if ((comparison.equals(">=")) && ((result == 0) || result > 0))
                return true;
            else if ((comparison.equals("<=")) && ((result == 0) || result < 0))
                return true;
            return false;
        } catch (ParseException ex) {
            return false;
        }
    }

    /**
     * 
     * @param filter
     * @param value
     * @return
     */
    public boolean itMatches(String filter, String value) {
        String quotedValue = "'" + value + "'";
        if (filter.contains("<>")) {
            filter = filter.substring(filter.indexOf("<>")+2);
            return (!quotedValue.equalsIgnoreCase(filter));
        } else if (filter.contains("<=")) {
            filter = filter.substring(filter.indexOf("<=")+2);
            if (isDate(filter) && isDate(value))
                return compareDates(filter, value, "<=");
            else
                return (Integer.valueOf(value) <= Integer.valueOf(filter));
        } else if (filter.contains(">=")) {
            filter = filter.substring(filter.indexOf(">=")+2);
            if (isDate(filter) && isDate(value))
                return compareDates(filter, value, ">=");
            else
                return (Integer.valueOf(value) >= Integer.valueOf(filter));
        } else if (filter.contains("<")) {
            filter = filter.substring(filter.indexOf("<")+1);
            if (isDate(filter) && isDate(value))
                return compareDates(filter, value, "<");
            else
                return (Integer.valueOf(value) < Integer.valueOf(filter));
        } else if (filter.contains(">")) {
            filter = filter.substring(filter.indexOf(">")+1);
            if (isDate(filter) && isDate(value))
                return compareDates(filter, value, ">");
            else
                return (Integer.valueOf(value) > Integer.valueOf(filter));
        } else if (filter.contains("=")) {
            filter = filter.substring(filter.indexOf("=")+1);
            if (isDate(filter) && isDate(value))
                return compareDates(filter, value, "=");
            else
                return (quotedValue.equalsIgnoreCase(filter));
        } else if (filter.contains("%")) {
            if ((filter.indexOf("%") == 0) && (filter.lastIndexOf("%") == filter.length()-1)) {
                filter = filter.substring(filter.indexOf("%")+1,filter.lastIndexOf("%"));
                return (quotedValue.contains(filter));
            } else if (filter.indexOf("%") == filter.length()-1) {
                filter = filter.substring(0,filter.indexOf("%"));
                return (quotedValue.startsWith(filter));
            } else if (filter.indexOf("%") == 0) {
                filter = filter.substring(filter.indexOf("%")+1);
                return (quotedValue.endsWith(filter));
            }
        }
        return true;
    }

    /**
     * 
     * @param entry
     * @return
     */
    @Override
    public boolean include(Entry entry) {
        ArrayList<Boolean> allCriteria = new ArrayList<Boolean>();
        boolean goodRow = false;
        boolean haveResult = false;
        if (!(table.getRowCount() == 0)) {
            for (int i = 0; i < 3; i++) {
                goodRow = true;
                // Start at one because the values model has the extra UID column and the criteria does not.
                // Use j-1 when accessing the stored filter value for the same reason.
                for (int j = 1; j < entry.getValueCount(); j++) {
                    if (!((((ValidationsUserEnteredItemsModel)table.getModel()).getStoredFilter(i,j-1).equals("")) ||
                         (entry.getStringValue(j).equals("")))) {
                        haveResult = true;
                        // Criteria columns for each record are and'd together
                        goodRow = goodRow && itMatches(((ValidationsUserEnteredItemsModel)table.getModel()).getStoredFilter(i,j-1),entry.getStringValue(j));
                    }
                }
                if (haveResult) {
                    allCriteria.add(goodRow);
                    haveResult = false;
                }
                if (goodRow) break;
            }
        }
        if (allCriteria.size() > 0)
            goodRow =  allCriteria.get(0);
        for (int k = 1; k < allCriteria.size(); k++)
            // Criteria rows for each record are or'd together
            goodRow = goodRow || allCriteria.get(k);
        return goodRow;
   }
 };
