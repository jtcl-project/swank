set body {/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/
package com.onemoonscientific.swank$subdir;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;
import java.awt.print.*;

import java.lang.*;

import java.util.*;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutionException;

$specialImports

/** This class implements the ${widgetType} command. */
public class ${widgetType}Cmd implements Command {
    /** Method creates the new widget object and calls configuration methods to process
     * command arguments.
     * @param interp The interpreter in which to create the command.
     * @param argv Argument array provides name of widget and command to be created in the first array element
     * and configuration options in the subsequent elements.
     * @throws TclException Exception thrown if an error occurs in creating the widget and the corresponding
     * Jacl command.
     */
    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        if ((argv.length > 2) && ("help".equals(argv[1].toString()))) {
               if ("options".startsWith(argv[2].toString())) {
                   ${widgetType}.getWidgetOptions(interp);
               } else if ("commands".startsWith(argv[2].toString())) {
                   ${widgetType}.getWidgetCmds(interp);
               }
        } else {
            if (!EventQueue.isDispatchThread()) {
		 cmdProcNotET(interp,argv);    
	    } else {
               throw new TclException(interp,"Can't create widgets on event queue");
	    }
       }
    }

   static class CmdProc implements Runnable {
	Interp interp;
        String widgetName  = "";
        ${widgetType} ${widgetVar} = null;

        CmdProc(Interp interp, String widgetName) {
		this.interp = interp;
                this.widgetName = widgetName;
        }

        public ${widgetType} getwidget () {
            return ${widgetVar};
        }
 
        public void run() {
                       ${widgetVar} = new ${widgetType}(interp, widgetName);
 	}
   }
// following based on suggestions in http://blog.palantir.com/2008/02/21/invokeandnotwaiting

   public static void runAndBlockSilently(final Interp interp, final Runnable r) throws TclException {
       final FutureTask ft = new FutureTask(r, null);
       boolean wasInterrupted = false;
       SwingUtilities.invokeLater(ft);
       while (! ft.isDone() ) {
           try {
               ft.get();
           }
           catch ( InterruptedException e ) {
               wasInterrupted = true;
            // Continue ...
           }
           catch(ExecutionException exEx) {
               Throwable cause = exEx.getCause();
               throw new TclException(interp,cause.getMessage());
           }
        }

       if(wasInterrupted) {
           Thread.currentThread().interrupt();
       }
    }

        public void cmdProcNotET(Interp interp, TclObject[] argv) throws TclException {
        int i;
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv, "pathName ?options?");
        }

        String  widgetName = argv[1].toString();
        if (!widgetName.startsWith(".")) {
            throw new TclException(interp,
                "bad window path name \"" + widgetName + "\"");
        }
        boolean exists = Widgets.exists(interp,widgetName);
        final $widgetType ${widgetVar};
        if (exists) {
            ${widgetVar} = (${widgetType}) Widgets.get(interp, widgetName);

            if (${widgetVar}.isCreated()) {
                throw new TclException(interp,
                    "window name \"" + widgetName +
                    "\" already exists in parent");
            }

        } else {
            if ((widgetName.length() > 1) &&
                Character.isUpperCase(widgetName.charAt(1))) {
                throw new TclException(interp,
                "window name starts with an upper-case letter: \"" +
                widgetName.charAt(1) + "\"");
            }
            CmdProc cmdProc = new CmdProc(interp,widgetName);
            runAndBlockSilently(interp,cmdProc);
      	     ${widgetVar} = cmdProc.getwidget();
             $specialVisible 

            LinkedList children = null;
            interp.createCommand(argv[1].toString(), new ${widgetType}WidgetCmd());
            TclObject tObj = ReflectObject.newInstance(interp, ${widgetType}.class, ${widgetVar});
            tObj.preserve();
            ${widgetVar}.configure(interp,  argv, 2);
            Widgets.addNewWidget(interp, widgetName, tObj);
        }

        ${widgetVar}.setCreated(true);
        BindCmd.addDefaultListeners(interp, ${widgetVar});
        interp.setResult(argv[1].toString());
    }
}
}
