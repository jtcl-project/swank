set body {/*
 * Copyright (c) 2000 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/
package com.onemoonscientific.swank$subdir;

import tcl.lang.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

//import java.io.IOException;
//import java.net.URL;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.tree.*;
import javax.swing.event.*; 
//  SpecialImports follow
$specialImports
//  end of SpecialImports

/** Class for objects which represent a Swank ${widgetVar} widget. */
public class ${widgetType} extends ${widget} implements SwkWidget, Printable$specialListeners
    {
    static Hashtable resourceDB = null;
    String name = null;
    String className = "$widgetClass";
    LinkedList children = null;
    ArrayList<SwkBinding> virtualBindings = null;
    int active = 0;
    boolean created = false;
    TclObject tclObject = null;
    final Interp interp;
    Insets emptyBorderInsets = new Insets(0, 0, 0, 0);
    Vector tagList = new Vector();
    Dimension minimumSize = null;
    $specialVars
    $configOptions
    $configOPTs
    static TreeMap validCmdsTM = new TreeMap();
    static {
         for (int i=0;i< validCmds.length;i++) {
               validCmdsTM.put(validCmds[i],Integer.valueOf(i));
         }
    }


    public ${widgetType}(final Interp interp, String name) {
        $specialSuper
        this.name = name.intern();
        this.interp = interp;

        if (resourceDB == null) {
            resourceDB = new Hashtable();
            initResources();
        }

        $specialInits

        tagList.add(name);
        tagList.add(className);
        tagList.add("swank");
        tagList.add("all");
    }
    public String getClassName() {
        return className;
    }
    public static void getWidgetOptions(Interp interp) throws TclException {
           TclObject result = TclList.newInstance();
           for (int i=0, n=validCmds.length;i<n;i++) {
                 TclList.append(interp,result,TclString.newInstance(validCmds[i]));
           }
           interp.setResult(result);
    }
    public static void getWidgetCmds(Interp interp) throws TclException {
           TclObject result = TclList.newInstance();
           String[] validCmds = ${widgetType}WidgetCmd.getValidCmds();
           for (int i=0, n=validCmds.length;i<n;i++) {
                 TclList.append(interp,result,TclString.newInstance(validCmds[i]));
           }
           interp.setResult(result);
    }

    public int print(Graphics g, final PageFormat pageFormat, int pageIndex)
        throws PrinterException {
        int result = NO_SUCH_PAGE;
        if (pageIndex == 0) {
            Graphics2D g2 = (Graphics2D) g;
            double pX = pageFormat.getImageableX();
            double pY = pageFormat.getImageableY();
            double pW = pageFormat.getImageableWidth();
            double pH = pageFormat.getImageableHeight();

            Dimension dimSize = getSize();
            double sx = 1.0;
            double sy = 1.0;

            if (dimSize.width > pW) {
                sx = pW / dimSize.width;
                sy = pW / dimSize.width;
            }
            if ((dimSize.height*sx) > pH) {
                sy = sx*pH / (dimSize.height*sx);
                sx = sx*pH / (dimSize.height*sx);
            }

            g2.translate(pX,pY);
            g2.scale(sx, sy);
            boolean wasBuffered = SwankUtil.disableDoubleBuffering(this);
            $specialPrints
            SwankUtil.restoreDoubleBuffering(this, wasBuffered);
            result = PAGE_EXISTS;
        } 
        return result;
    }


    public Vector getTagList() {
        return (tagList);
    }
   
    public void setTagList(Interp interp, TclObject tagListObj)
        throws TclException {
        TclObject[] tagObjs = TclList.getElements(interp, tagListObj);
        tagList.setSize(0);

        for (int i = 0; i < tagObjs.length; i++) {
            tagList.add(tagObjs[i].toString());
        }
    }

    public void setVirtualBindings(ArrayList<SwkBinding> bindings) {
        virtualBindings = bindings;
    }

    public ArrayList<SwkBinding> getVirtualBindings() {
        return (virtualBindings);
    }

    public Insets getEmptyBorderInsets() {
        return (emptyBorderInsets);
    }

    public LinkedList getChildrenList() {
        return (children);
    }

    public void initChildrenList() {
        children = new LinkedList();
    }

    public String getName() {
        return (name);
    }

    $specialMethods


    void jgetAll(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
              System.out.println("never  run on event thread");
        }
            JGetAll jgetAll = new JGetAll(this);
	    try {
                SwingUtilities.invokeAndWait(jgetAll);
	    } catch (InterruptedException iE) {
		throw new TclException(interp,iE.toString());
	    } catch (Exception  e) {
		throw new TclException(interp,e.toString());
	    }
            getAllConfigurations(interp,jgetAll.roValues);
    }
   class JGetAll implements Runnable {
        ArrayList roValues = null;
        JGetAll( ${widgetType} ${widgetVar}) {
        }
        public void run() {
		roValues = getAllConfigurations();
        }
    }
    ArrayList getAllConfigurations() {
           ArrayList results = new ArrayList();
           Enumeration e = ${widgetType}.resourceDB.keys();
           while (e.hasMoreElements()) {
                 String keyName = (String) e.nextElement();
                 ResourceObject ro = (ResourceObject) resourceDB.get(keyName);
                 if (ro == null) {
                    continue;
                 }
                 String value = jget(ro.optNum);
                 results.add(keyName);
                 results.add(ro);
                 results.add(value);
           }
           return results;
    }
    void getAllConfigurations(Interp interp,ArrayList roValues) throws TclException {
                TclObject list2 = TclList.newInstance();
                for (int i=0,n=roValues.size();i<n;i += 3) {
                    TclObject list1 = TclList.newInstance();
                    String keyName = (String) roValues.get(i);
                    ResourceObject ro = (ResourceObject) roValues.get(i+1);
                    String value = (String) roValues.get(i+2);

                    if (ro == null) {
                        continue;
                    }

                    TclObject tObj = TclString.newInstance(keyName);

                    TclList.append(interp, list1, tObj);
                    TclList.append(interp, list1, TclString.newInstance(ro.resource));
                    TclList.append(interp, list1, TclString.newInstance(ro.className));

                    if (ro.defaultVal == null) {
                        TclList.append(interp, list1, TclString.newInstance(""));
                    } else {
                        TclList.append(interp, list1, TclString.newInstance(ro.defaultVal));
                    }

                    if (value == null) {
                        value = "";
                    }

                    TclList.append(interp, list1, TclString.newInstance(value));
                    TclList.append(interp, list2, list1);
                }

                interp.setResult(list2);
    }

    public void setValues(Setter setter,int opt) {
         $setterCASEs
    }
    public void configure(Interp interp, TclObject[] argv, int start) throws TclException { 
        if (EventQueue.isDispatchThread()) {
             throw new RuntimeException("Configure on eventQueue");
        }
        int i; 
     
        if (argv.length <= start) {
            return;
        }
        
        ResourceObject ro = null;

        for (i = start; i < argv.length; i += 2) {
            if ((i + 1) >= argv.length) {
                throw new TclException(interp,
                    "value for \"" + argv[i].toString() + "\" missing");
            }

            ro = (ResourceObject) ${widgetType}.resourceDB.get(argv[i].toString());
            
            if (ro == null) {
                throw new TclException(interp,
                    "unknown option \"" + argv[i].toString() + "\"");
            }
           /* 
            if (ro.defaultVal == null) {
                ro.defaultVal = ${widgetType}Configure.jget(interp, ${widgetVar},
                        argv[i]);
            }       
          */
                 $configCASEs

        }
        SwankUtil.doWait();
        this.repaint();
    }
    String jget(final int opt) {
            $getCASEs
            return "";
    }

    String jget(final Interp interp, final TclObject arg) throws TclException {
       int opt = 0;
       // XXX SwkIndex doesn't throw correct error for hear 
       try {
           opt = SwkIndex.get(interp, arg, validCmdsTM, "option", 0);
       } catch (TclException tclE) {
           throw new TclException(interp, "unknown option \"" + arg + "\"");
       }
        String result = "";
        if (!EventQueue.isDispatchThread()) {
            JGet jget = new JGet(this, opt);
            try {
                SwingUtilities.invokeAndWait(jget);
            } catch (InterruptedException iE) {
                throw new TclException(interp,iE.toString());
            } catch (Exception  e) {
                throw new TclException(interp,e.toString());
            }
            result =  jget.result;
        } else {
            result =  jget(opt);
        }
        return result;
    }
   static class JGet implements Runnable {
        ${widgetType} ${widgetVar};
        int opt = 0;
	String result = "";
        JGet(${widgetType} ${widgetVar}, int opt) {
                this.${widgetVar} = ${widgetVar};
                this.opt = opt;
        }

        public void run() {
                        result = ${widgetVar}.jget(opt);
        }

    }
   void setResourceDefaults() throws TclException {
        if (!EventQueue.isDispatchThread()) {
            ResourceDefaultsSetter resourceDefaultsSetter = new ResourceDefaultsSetter(interp,this);
            try {
                SwingUtilities.invokeAndWait(resourceDefaultsSetter);
            } catch (InterruptedException iE) {
                throw new TclException(interp,iE.toString());
            } catch (Exception  e) {
                throw new TclException(interp,e.toString());
            }
        } else {
            setResourceDefaultsET();
        }
    }

  class ResourceDefaultsSetter implements Runnable {
        ResourceDefaultsSetter(Interp interp, ${widgetType} ${widgetVar}) {
        }

        public void run() {
			setResourceDefaultsET();
        }

    }

    void setResourceDefaultsET() {
        String keyName;
        TclObject tObj;

        Enumeration e = ${widgetType}.resourceDB.keys();

        while (e.hasMoreElements()) {
            keyName = (String) e.nextElement();

            if (keyName == null) {
                continue;
            }

            ResourceObject ro = (ResourceObject) ${widgetType}.resourceDB.get(keyName);

            if (ro == null) {
                continue;
            }

                try {
                   ro.defaultVal = jget(ro.optNum);
                } catch (IllegalComponentStateException icsE) {
                    continue;
                }
        }
    }
    $resources
}

}
