/*
 * SwkJHelpCmd.java
 *
 * Created on Jul 3, 2007, 4:25:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.onemoonscientific.swank.jhelp;

import tcl.lang.*;

import java.awt.*;
import java.net.URL;

import java.util.ArrayList;

import javax.swing.*;
import javax.help.*;
import javax.help.Map.ID;
import com.onemoonscientific.swank.*;

public class SwkJHelpCmd implements Command {
    
    static final private String[] validCmds = {
        "init", "show" , "enable" , "enablekey" , "popup"
    };
    static final private int OPT_INIT = 0;
    static final private int OPT_SHOW = 1;
    static final private int OPT_ENABLE = 2;
    static final private int OPT_ENABLEKEY = 3;
    static final private int OPT_POPUP = 4;
    
    HelpSet mainHS = null;
    HelpBroker mainHB;
    
    static final String helpsetName = "javahelpjhelpset";
    static final String helpsetLabel = "Demo NVJ - Help";
    
    static boolean gotDefaults = false;
    int index;
    Interp interp = null;
    
    public static String[] getValidCmds() {
        return validCmds;
    }
    
    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }
        
        this.interp = interp;
        
        final int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        switch (opt) {
        case OPT_INIT:
            
            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }
            
            //          interp.setResult(swkjlist.jget(interp, argv[2]));
            
            try {
                ClassLoader cl = SwkJHelpCmd.class.getClassLoader();
                URL url = HelpSet.findHelpSet(cl, helpsetName);
                mainHS = new HelpSet(cl, url);
            } catch (Exception ee) {
                System.out.println("Help Set "+helpsetName+" not found");
                return;
            } catch (ExceptionInInitializerError ex) {
                System.err.println("initialization error:");
                ex.getException().printStackTrace();
            }
            mainHB = mainHS.createHelpBroker();
            
            break;
            
        case OPT_SHOW:
            String targetName = argv[2].toString();
            (new Show()).exec(targetName);
            break;
            
        case OPT_ENABLE:{
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "option option");
            }
            
            final TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[2].toString());
            
            if (tObj == null) {
                throw new TclException(interp,
                        "bad window path name \"" + argv[2].toString() + "\"");
            }
            
            final Object swkjobject = (Object) ReflectObject.get(interp, tObj);
            
            targetName =argv[3].toString();
            
  /*      if (swkjobject instanceof JFrame) {
            JFrame jf = (JFrame) swkjobject;
            JRootPane jrp = jf.getRootPane();
   
        Component jcomp = (Component) jrp;
        mainHB.enableHelpKey(jcomp,targetName,mainHS);
        }
   */
            //Presentation pre = mainHS.getPresentation(targetName);
            Map m =mainHS.getCombinedMap();
            if(m.isValidID(targetName, mainHS)) {
                mainHB.enableHelp((Component)swkjobject,targetName,null);
            } else{
                throw new TclException(interp,"Tcl Exception: "+targetName +" is not valid Target");
            }
            //  mainHB.enableHelpOnButton((Component)swkjobject,targetName,null);
        }
        break;
        
        case OPT_ENABLEKEY:{
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "option option");
            }
            
            final TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[2].toString());
            
            if (tObj == null) {
                throw new TclException(interp,
                        "bad window path name \"" + argv[2].toString() + "\"");
            }
            
            final Object swkjobject = (Object) ReflectObject.get(interp, tObj);
            
            targetName =argv[3].toString();
            
  /*      if (swkjobject instanceof JFrame) {
            JFrame jf = (JFrame) swkjobject;
            JRootPane jrp = jf.getRootPane();
   
        Component jcomp = (Component) jrp;
        mainHB.enableHelpKey(jcomp,targetName,mainHS);
        }
   */
            //Presentation pre = mainHS.getPresentation(targetName);
            Map m =mainHS.getCombinedMap();
            if(m.isValidID(targetName, mainHS)) {
                mainHB.enableHelpKey((Component)swkjobject,targetName,null);
            } else{
                throw new TclException(interp,"Tcl Exception: "+targetName +" is not valid Target");
            }
            //  mainHB.enableHelpOnButton((Component)swkjobject,targetName,null);
        }
        break;
        
        case OPT_POPUP:
            
            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option option");
            }
            
            final TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[2].toString());
            
            if (tObj == null) {
                throw new TclException(interp,
                        "bad window path name \"" + argv[2].toString() + "\"");
            }
            
            final Object swkjobject = (Object) ReflectObject.get(interp, tObj);
            
            JMenu comp = (JMenu)swkjobject;
            JMenuItem jcomp = comp.getItem(1);
            jcomp.addActionListener(new CSH.DisplayHelpAfterTracking(mainHS, "javax.help.Popup", null));
            
            break;
        }
        
    }
    
    public static void setTarget(Component comp, String targetName) {
        
        CSH.setHelpIDString(comp,targetName);
    }
    
    class Show extends UpdateOnEventThread {
        String errMessage = null;
        String targetName = null;
        
        void exec(final String targetName) throws TclException {
            this.targetName = targetName;
            execOnThread();
            
            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }
        }
        
        public void run() {
            mainHB.setDisplayed(true);
            try {
                mainHB.setCurrentID(targetName);
                //      mainHB.setCurrentView(targetName);
            } catch (BadIDException be) {
                System.out.println("Error: Target is not found!");
                mainHB.setCurrentID("default");
            }
            
            
        }
    }
    
}

