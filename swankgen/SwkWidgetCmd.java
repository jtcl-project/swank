set body {/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/
package com.onemoonscientific.swank$subdir;

import tcl.lang.*;

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

   class CmdProc implements Runnable {
	Interp interp;
	TclObject[] argv = null;
        String className = "";
        String widgetName  = "";
        ${widgetType} ${widgetVar} = null;

        CmdProc(Interp interp, String className, String widgetName) {
		this.interp = interp;
                this.className = className;
                this.widgetName = widgetName;
        }

        public ${widgetType} getwidget () {
            return ${widgetVar};
        }
 
        public void run() {
                       ${widgetVar} = new ${widgetType}(interp, widgetName, className);
 	}
   }
        public void cmdProcNotET(Interp interp, TclObject[] argv) throws TclException {
        int i;
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv, "pathName ?options?");
        }

        if (!argv[1].toString().startsWith(".")) {
            throw new TclException(interp,
                "bad window path name \"" + argv[1].toString() + "\"");
        }

       String cmdName = argv[0].toString();
       String className = argv[0].toString().substring(0, 1).toUpperCase() +
            argv[0].toString().substring(1);
       String  widgetName = argv[1].toString();

        CmdProc cmdProc = new CmdProc(interp,className,widgetName);
                try {
	            SwingUtilities.invokeAndWait(cmdProc);
                } catch (InterruptedException iE) {
                    throw new TclException(interp,iE.toString());
                } catch (Exception  e) {
                    throw new TclException(interp,e.toString());
                }
      	 ${widgetType} ${widgetVar} = cmdProc.getwidget();
 
        if (Widgets.exists(interp,argv[1].toString())) {
            ${widgetVar} = (${widgetType}) Widgets.get(interp, argv[1].toString());

            if (${widgetVar}.isCreated()) {
                throw new TclException(interp,
                    "window name \"" + argv[1].toString() +
                    "\" already exists in parent");
            }

            ${widgetVar}.className = className.intern();
        } else {
            if ((argv[1].toString().length() > 1) &&
                    Character.isUpperCase(argv[1].toString().charAt(1))) {
                throw new TclException(interp,
                    "window name starts with an upper-case letter: \"" +
                    argv[1].toString().charAt(1) + "\"");
            }

 
            ${widgetVar}.className = className.intern();

            LinkedList children = null;
            interp.createCommand(argv[1].toString(), new ${widgetType}WidgetCmd());
            TclObject tObj = ReflectObject.newInstance(interp, ${widgetType}.class, ${widgetVar});
            tObj.preserve();
            ${widgetVar}.configure(interp,  argv, 2);
            Widgets.addNewWidget(interp, argv[1].toString(), tObj);
        }

        ${widgetVar}.setCreated(true);
        BindCmd.addDefaultListeners(interp, ${widgetVar});
        interp.setResult(argv[1].toString());
    }
}
}
