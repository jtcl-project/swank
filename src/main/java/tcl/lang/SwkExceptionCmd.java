/*
 * SwkShell.java --
 *
 *        Implements the start up shell for Tcl.
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: SwkExceptionCmd.java,v 1.1 2005/11/07 03:49:31 bruce_johnson Exp $
 */
package tcl.lang;

/**
 * The SwkShell class is similar to the Tclsh program: you can use it to
 * execute a Tcl script or enter Tcl command interactively at the
 * command prompt.
 */
public class SwkExceptionCmd {

    public static void doExceptionCmd(Interp interp, String cmd)
            throws TclException {
        interp.allowExceptions();

        try {
            interp.eval(cmd);
        } catch (TclException tclE) {
            throw new TclException(interp, tclE.getMessage(),
                    tclE.getCompletionCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new TclException(interp,
                    "Error executing:\n" + cmd + "\n" + e.getMessage());
        }
    }

    public static void doExceptionCmdBG(Interp interp, String cmd) {
        interp.allowExceptions();

        try {
            interp.eval(cmd);
        } catch (TclException tclE) {
            if (tclE.getCompletionCode() == TCL.BREAK) {
                return;
            } else {
                System.out.println("throw " + tclE.toString());
                interp.addErrorInfo(tclE.getMessage());
                interp.backgroundError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("except in CmdBG" + e.getMessage());
            interp.addErrorInfo(e.getMessage());
            interp.backgroundError();
        }
    }

    public static void setVar(Interp interp, String var1, String var2,
            double value) {
        TclObject tObj = TclDouble.newInstance(value);
        setVar(interp, var1, var2, tObj);
    }

    public static void setVar(Interp interp, String var1, String var2, int value) {
        TclObject tObj = TclInteger.newInstance(value);
        setVar(interp, var1, var2, tObj);
    }

    public static void setVar(Interp interp, String var1, String var2,
            boolean value) {
        TclObject tObj = TclBoolean.newInstance(value);
        setVar(interp, var1, var2, tObj);
    }

    public static void setVar(Interp interp, String var1, String var2,
            String value) {
        TclObject tObj = TclString.newInstance(value);
        setVar(interp, var1, var2, tObj);
    }

    public static void setVar(Interp interp, String var1, String var2,
            TclObject tObj) {
        interp.allowExceptions();

        try {
            interp.setVar(var1, var2, tObj, TCL.GLOBAL_ONLY);
        } catch (TclException tclE) {
            if (tclE.getCompletionCode() == TCL.BREAK) {
                return;
            } else {
                System.out.println("throw " + tclE.toString());
                interp.addErrorInfo(tclE.getMessage());
                interp.backgroundError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("except in setVarBG" + e.getMessage());
            interp.addErrorInfo(e.getMessage());
            interp.backgroundError();
        }
    }
}
