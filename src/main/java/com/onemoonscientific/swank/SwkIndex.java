/*
 * SwkIndex.java
 *
 *	This file implements objects of type "index".  This object type
 *	is used to lookup a keyword in a table of valid values and cache
 *	the index of the matching entry.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: SwkIndex.java,v 1.6 2005/10/11 20:03:23 mdejong Exp $
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.util.*;

public class SwkIndex implements InternalRep {

    /**
     * The variable slots for this object.
     */
    private int index;
    /**
     * Table of valid options.
     */
    private String[] table;
    TreeMap TM;

    /**
     * Construct a SwkIndex representation with the given index & table.
     */
    private SwkIndex(int i, String[] tab) {
        index = i;
        table = tab;
    }

    private SwkIndex(int i, TreeMap TM) {
        index = i;
        TM = TM;
    }

    /**
     * Returns a dupilcate of the current object.
     * @param obj the TclObject that contains this internalRep.
     */
    public InternalRep duplicate() {
        return new SwkIndex(index, table);
    }

    /**
     * Implement this no-op for the InternalRep interface.
     */
    public void dispose() {
    }

    /**
     * Called to query the string representation of the Tcl object. This
     * method is called only by TclObject.toString() when
     * TclObject.stringRep is null.
     *
     * @return the string representation of the Tcl object.
     */
    public String toString() {
        return table[index];
    }

    /**
     * Tcl_GetIndexFromObj -> get
     *
     * Gets the index into the table of the object.  Generate an error
     * it it doesn't occur.  This also converts the object to an index
     * which should catch the lookup for speed improvement.
     *
     * @param interp the interperter or null
     * @param tobj the object to operate on.
     * @paran table the list of commands
     * @paran msg used as part of any error messages
     * @paran flags may be TCL.EXACT.
     */
    public static int get(Interp interp, TclObject tobj, String[] table,
            String msg, int flags) throws TclException {
        InternalRep rep = tobj.getInternalRep();

        if (rep instanceof SwkIndex) {
            if (((SwkIndex) rep).table == table) {
                return ((SwkIndex) rep).index;
            }
        }

        String str = tobj.toString();
        int strLen = str.length();
        int tableLen = table.length;
        int index = -1;
        int numAbbrev = 0;

        checking:
        {
            if (strLen > 0) {

                for (int i = 0; i < tableLen; i++) {
                    String option = table[i];

                    if (((flags & TCL.EXACT) == TCL.EXACT)
                            && (option.length() != strLen)) {
                        continue;
                    }
                    if (option.equals(str)) {
                        // Found an exact match already. Return it.

                        index = i;
                        break checking;
                    }
                    if (option.startsWith(str)) {
                        numAbbrev++;
                        index = i;
                    }
                }
            }
            if (numAbbrev != 1) {
                StringBuffer sbuf = new StringBuffer();
                if (numAbbrev > 1) {
                    sbuf.append("ambiguous ");
                } else {
                    sbuf.append("bad ");
                }
                sbuf.append(msg);
                sbuf.append(" \"");
                sbuf.append(str);
                sbuf.append("\"");
                sbuf.append(": must be ");
                sbuf.append(table[0]);
                for (int i = 1; i < tableLen; i++) {
                    if (i == (tableLen - 1)) {
                        sbuf.append((i > 1) ? ", or " : " or ");
                    } else {
                        sbuf.append(", ");
                    }
                    sbuf.append(table[i]);
                }
                throw new TclException(interp, sbuf.toString());
            }
        }
        if (index == -1) {
             throw new TclException(interp, "unknown option \"" + str + "\"");
        }
        // Create a new index object.

        tobj.setInternalRep(new SwkIndex(index, table));
        return index;
    }

    public static int get(Interp interp, TclObject tobj, TreeMap TM,
            String msg, int flags) throws TclException {

        InternalRep rep = tobj.getInternalRep();

        if (rep instanceof SwkIndex) {
            if (((SwkIndex) rep).TM == TM) {
                return ((SwkIndex) rep).index;
            }
        }

        String str = tobj.toString();
        int strLen = str.length();
        int tableLen = TM.size();
        int index = -1;
        int numAbbrev = 0;

        checking:
        {
            Integer IObj = (Integer) TM.get(str);

            if (IObj != null) {
                index = IObj.intValue();
                break checking;
            }
            if ((flags & TCL.EXACT) != TCL.EXACT) {
                Iterator it = TM.keySet().iterator();
                Object obj;
                while (it.hasNext()) {
                    obj = it.next();
                    String option = (String) obj;
                    if (option.startsWith(str)) {
                        numAbbrev++;
                        index = ((Integer) TM.get(option)).intValue();
                    }
                    if (option.compareTo(str) > 0) {   //option > str
                        break checking;
                        //System.out.println(obj + ": " + TM.get(obj));
                    }
                }
            }

            if (numAbbrev != 1) {
                StringBuffer sbuf = new StringBuffer();
                if (numAbbrev > 1) {
                    sbuf.append("ambiguous ");
                } else {
                    sbuf.append("bad ");
                }
                sbuf.append(msg);
                sbuf.append(" \"");
                sbuf.append(str);
                sbuf.append("\"");
                sbuf.append(": must be ");

                Iterator it = TM.keySet().iterator();
                Object obj;

                while (it.hasNext()) {
                    obj = it.next();
                    String option = (String) obj;
                    sbuf.append(option);

                    if (it.hasNext()) {

                        sbuf.append(", ");
                    }

                }

                throw new TclException(interp, sbuf.toString());
            }
        }
        if (index == -1) {
             throw new TclException(interp, "unknown option \"" + str + "\"");
        }

        // Create a new index object.

        tobj.setInternalRep(new SwkIndex(index, TM));
        return index;
    }

    /**
     * Invoked only when testing the SwkIndex implementation in TestObjCmd.java
     */
    void testUpdateIndex(int index) {
        this.index = index;
    }
}

