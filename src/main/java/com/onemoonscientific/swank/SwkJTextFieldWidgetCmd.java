/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;

class SwkJTextFieldWidgetCmd implements Command {
        enum SelMode {
             ADJUST(3,1,1,"index") {
                Object doTask(SwkJTextField swkjtextfield, final int index1, final int index2) {
                      int start = swkjtextfield.getSelectionStart();
                      int end = swkjtextfield.getSelectionEnd();
                      final int new1;
                      final int new2;
                      if ( Math.abs(start-index1) < Math.abs(end-index1)) {
                           new1 = end;
                           new2 = index1;
                      } else {
                           new1 = start;
                           new2 = index1;
                      }
                      swkjtextfield.setCaretPosition(new1);
                      swkjtextfield.moveCaretPosition(new2);
                      SelectionCmd.setSelectionWindow(swkjtextfield);

                      return null;
                }
             },
             CLEAR(3,0,0,"") {
                Object doTask(SwkJTextField swkjtextfield, final int index1, final int index2) {
                      swkjtextfield.setCaretPosition(swkjtextfield.getDocument().getLength());
                      swkjtextfield.moveCaretPosition(swkjtextfield.getDocument().getLength());
                      SelectionCmd.setSelectionWindow(swkjtextfield);
                      return null;
                }
             },
             FROM(3,1,1,"index") {
                Object doTask(SwkJTextField swkjtextfield, final int index1, final int index2) {
                      swkjtextfield.setCaretPosition(index1);
                      SelectionCmd.setSelectionWindow(swkjtextfield);
                      return null;
                }
             },
             PRESENT(3,0,0,"") {
                Object doTask(SwkJTextField swkjtextfield, final int index1, final int index2) {
                      Boolean value = Boolean.valueOf(true);
                      if (swkjtextfield.getDocument().getLength() == 0) {
                          value = Boolean.valueOf(false);
                      } else {
                          if ((swkjtextfield.getSelectionStart() - swkjtextfield.getSelectionEnd()) ==  0) {
                              value = Boolean.valueOf(false);
                          }
                          if (swkjtextfield.getSelectionStart()  >= swkjtextfield.getDocument().getLength()) {
                              value = Boolean.valueOf(false);
                          }
                      }
                      return value;
                }
             },
             RANGE(3,2,2,"start end") {
                Object doTask(SwkJTextField swkjtextfield, final int index1, final int index2) {
                      swkjtextfield.setCaretPosition(index1);
                      swkjtextfield.moveCaretPosition(index2);
                      SelectionCmd.setSelectionWindow(swkjtextfield);
                      return null;
                }
             },
             TO(3,1,1,"index") {
                Object doTask(SwkJTextField swkjtextfield, final int index1, final int index2) {
                      swkjtextfield.moveCaretPosition(index1);
                      SelectionCmd.setSelectionWindow(swkjtextfield);
                      return null;
                }
             },
             ;
             private int startOpt;
             private int argNumMin;
             private int argNumMax;
             private String argNumError;
             SelMode(final int startOpt, final int argNumMin,final int argNumMax,final String argNumError) {
                 this.startOpt = startOpt;
                 this.argNumMin = argNumMin;
                 this.argNumMax = argNumMax;
                 this.argNumError = argNumError;
             }
             void checkArgCount(final Interp interp, final TclObject[] argv) throws TclException {
                   int numOptArgs = argv.length-startOpt;
                   if ((numOptArgs < argNumMin) || (numOptArgs > argNumMax)) {
                         throw new TclNumArgsException(interp,startOpt,argv,argNumError);
                   }
             }
             static SelMode getSelMode(final Interp interp, final String modeName) throws TclException {
                  String modeNameUC = modeName.toUpperCase();
                  SelMode selModeMatch = null;
                  if (selModeMatch == null) {
                      for (SelMode selMode:SelMode.values()) {
                         if (modeNameUC.startsWith(selMode.toString())) {
                             selModeMatch = selMode;
                             break;
                         }
                      }
                  }
                  if (selModeMatch == null)  {
                      throw new TclException(interp,"bad selection option \"" + modeName + "\": must be adjust, clear, from, present, range, or to");
                  }
                  return selModeMatch;
             }
             void exec(final Interp interp, final SwkJTextField swkjtextfield, final TclObject[] argv,Selection selection) throws TclException {
                 String index1 = null;
                 String index2 = null;
                 if (argv.length > 3) {
                     index1 = argv[3].toString();
                 }
                 if (argv.length > 4) {
                     index2 = argv[4].toString();
                 }
                 Object objResult = selection.exec(swkjtextfield,this,index1,index2);
                 if (objResult instanceof Boolean) {
                      if (((Boolean) objResult).booleanValue()) {
                          interp.setResult("1");
                      } else {
                          interp.setResult("0");
                      }
                 }
             }
                abstract Object doTask(SwkJTextField swkjtextfield, final int index1, final int index2);

        }

    static final private String[] validCmds = {
        "bbox","cget", "configure", "delete", "get", "icursor","index",
        "insert", "scan", "selection", "validate","xview"
    };
    static final private int OPT_BBOX = 0;
    static final private int OPT_CGET = 1;
    static final private int OPT_CONFIGURE = 2;
    static final private int OPT_DELETE = 3;
    static final private int OPT_GET = 4;
    static final private int OPT_ICURSOR = 5;
    static final private int OPT_INDEX = 6;
    static final private int OPT_INSERT = 7;
    static final private int OPT_SCAN = 8;
    static final private int OPT_SELECTION = 9;
    static final private int OPT_VALIDATE = 10;
    static final private int OPT_XVIEW = 11;
    static boolean gotDefaults = false;
    int index;
    Interp interp = null;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        this.interp = interp;

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJTextField swkjtextfield = (SwkJTextField) ReflectObject.get(interp,
                tObj);

        switch (opt) {
             case OPT_BBOX:
                bbox(interp, swkjtextfield, argv);
                break;

            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjtextfield.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjtextfield.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjtextfield.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjtextfield.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJTextField.resourceDB.get(argv[2].toString());

                    if (ro == null) {
                        throw new TclException(interp,
                                "unknown option \"" + argv[2].toString() + "\"");
                    }

                    TclObject list = TclList.newInstance();
                    TclList.append(interp, list,
                            TclString.newInstance(argv[2].toString()));
                    TclList.append(interp, list, TclString.newInstance(ro.resource));
                    TclList.append(interp, list, TclString.newInstance(ro.className));
                    TclList.append(interp, list,
                            TclString.newInstance(ro.defaultVal));
                    TclList.append(interp, list, TclString.newInstance(result));
                    interp.setResult(list);
                } else {
                    swkjtextfield.configure(interp, argv, 2);
                }

                break;

             case OPT_DELETE:
                delete(interp, swkjtextfield, argv);
                break;

            case OPT_GET:
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }
                interp.setResult(swkjtextfield.getText());
                break;
             case OPT_ICURSOR:
                icursor(interp, swkjtextfield, argv);
                break;

            case OPT_INDEX:
                getIndex(interp, swkjtextfield, argv, 0);

                break;

            case OPT_INSERT:
                insert(interp, swkjtextfield, argv);

                break;

            case OPT_SCAN:
                break;

            case OPT_SELECTION:
                selection(interp, swkjtextfield, argv);
                break;

            case OPT_VALIDATE:
                break;


            case OPT_XVIEW:

                if (argv.length == 2) {
                    (new ViewValues()).exec(swkjtextfield);
                } else if (argv.length == 3) {
                    index = getIndex2(interp, swkjtextfield, argv, -1);

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            BoundedRangeModel brm = swkjtextfield.getHorizontalVisibility();
                            int maxSize = swkjtextfield.getText().length();
                            double fx1 = (1.0 * index) / maxSize;

                            if (fx1 < 0.0) {
                                fx1 = 0.0;
                            }

                            final int x = (int) ((fx1 * (brm.getMaximum()
                                    - brm.getMinimum())) + brm.getMinimum());
                            swkjtextfield.setScrollOffset(x);
                        }
                    });
                } else if (argv[2].toString().equals("moveto")) {
                    if (argv.length != 4) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    final double fx1 = TclDouble.get(interp, argv[3]);

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            BoundedRangeModel brm = swkjtextfield.getHorizontalVisibility();
                            double fx = fx1;

                            if (fx1 < 0.0) {
                                fx = 0.0;
                            }

                            final int x = (int) ((fx * (brm.getMaximum()
                                    - brm.getMinimum())) + brm.getMinimum());

                            //            System.out.println("Moveto is called for TextField " + x);
                            swkjtextfield.setScrollOffset(x);
                        }
                    });
                } else if (argv[2].toString().equals("scroll")) {
                    if (argv.length != 5) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    if (argv[4].toString().equals("units")) {
                        final int units = TclInteger.get(interp, argv[3]);

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                int x = swkjtextfield.getScrollOffset();
                                swkjtextfield.setScrollOffset(x + units);
                            }
                        });
                    } else if (argv[4].toString().equals("pages")) {
                        final int units = TclInteger.get(interp, argv[3]);

                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                int x = swkjtextfield.getScrollOffset();
                                swkjtextfield.setScrollOffset(x + units);
                            }
                        });
                    }
                } else {
                    throw new TclException(interp,
                            "unknown option \"" + argv[2].toString()
                            + "\": must be moveto or scroll");
                }

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    int getIndex2(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv, int offset) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "string");
        }

        int index = (new Index()).exec(swkjtextfield, argv[2].toString(), offset);

        return index;
    }

    void getIndex(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv, int offset) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "string");
        }

        int index = (new Index()).exec(swkjtextfield, argv[2].toString(), offset);
        interp.setResult(index);
    }
   void bbox(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }
        Rectangle rect = (new Bbox()).exec(swkjtextfield, argv[2].toString());
        TclObject rectResult = TclList.newInstance();
        TclList.append(interp,rectResult,TclInteger.newInstance(rect.x));
        TclList.append(interp,rectResult,TclInteger.newInstance(rect.y));
        TclList.append(interp,rectResult,TclInteger.newInstance(rect.width));
        TclList.append(interp,rectResult,TclInteger.newInstance(rect.height));
        interp.setResult(rectResult);
    }
    void delete(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "firstIndex ?lastIndex?");
        }
        final String firstPos = argv[2].toString();
        final String lastPos;
        if (argv.length == 4) {
            lastPos = argv[3].toString();
        } else {
            lastPos = null;
        }

        String errMessage = (new Delete()).exec(swkjtextfield,firstPos,lastPos);
        if (errMessage != null) {
             throw new TclException(interp,errMessage);
        }
    }
   void icursor(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "pos");
        }
        (new Icursor()).exec(swkjtextfield, argv[2].toString());
    }

    void insert(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "index text");
        }

        (new Insert()).exec(swkjtextfield, argv[2].toString(),
                argv[3].toString());
    }
    void selection(final Interp interp, final SwkJTextField swkjtextfield,
            final TclObject[] argv) throws TclException {
        if (argv.length  < 3) {
            throw new TclNumArgsException(interp, 2, argv, "option ?index?");
        }
        String modeArg = argv[2].toString();
        final SelMode mode = SelMode.getSelMode(interp,modeArg);
        mode.checkArgCount(interp,argv);
        mode.exec(interp,swkjtextfield,argv, new Selection());
    }
 class Bbox extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        String item = null;
        String errMessage = null;
        Rectangle rectangle = null;
        Rectangle exec(final SwkJTextField swkjtextfield, final String item) throws TclException {
            this.item = item;
            this.swkjtextfield = swkjtextfield;
            execOnThread();
            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }
            return rectangle;

        }

        public void run() {
            Result result = new Result();
            swkjtextfield.getIndex(item, 0, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();
                return;
            }
            try {
                rectangle = swkjtextfield.modelToView(result.i);
                Rectangle rectangle2 = swkjtextfield.modelToView(result.i+1);
                rectangle = rectangle.union(rectangle2);
            } catch (BadLocationException bLE) {
                 errMessage = bLE.getMessage();
            }
        }
    }

   class Icursor extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        String item = null;
        String errMessage = null;

        void exec(final SwkJTextField swkjtextfield, final String item) throws TclException {
            this.item = item;
            this.swkjtextfield = swkjtextfield;
            execOnThread();
            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

        }

        public void run() {
            if (!swkjtextfield.isEditable()) {
                return;
            }
            Result result = new Result();
            swkjtextfield.getIndex(item, 0, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();
                return;
            }
            swkjtextfield.setCaretPosition(result.i); 
        }
    }
  class Selection extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        String item1 = null;
        String item2 = null;
        SelMode selMode = null;
        String errMessage = null;
        Object objResult = null;
        Object exec(final SwkJTextField swkjtextfield, SelMode selMode, final String item1, final String item2) throws TclException {
            this.selMode = selMode;
            this.item1 = item1;
            this.item2 = item2;
            this.swkjtextfield = swkjtextfield;
            execOnThread();
            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }
            return objResult;
        }

        public void run() {
            if (!swkjtextfield.isEnabled()) {
                return;
            }
            Result result = new Result();
            int index1 = 0;
            int index2 = 0;
            if (item1 != null) {
                swkjtextfield.getIndex(item1, 0, result);
            }
            if (result.hasError()) {
                errMessage = result.getErrorMsg();
                return;
            }
            index1 = result.i;
            if (item2 != null) {
                swkjtextfield.getIndex(item2, 0, result);
                if (result.hasError()) {
                    errMessage = result.getErrorMsg();
                    return;
                }
                index2 = result.i;
            }
            objResult = selMode.doTask(swkjtextfield,index1, index2);
        }
    }

    class Index extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        String item = null;
        int index = 0;
        int endVal = 0;
        String errMessage = null;

        int exec(final SwkJTextField swkjtextfield, final String item,
                int endVal) throws TclException {
            this.item = item;
            this.endVal = endVal;
            this.swkjtextfield = swkjtextfield;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return index;
        }

        public void run() {
            Result result = new Result();
            swkjtextfield.getIndex(item, endVal, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;
        }
    }
   class Delete extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        String firstPos = null;
        String lastPos = null;
        String errMessage = null;

        String exec(final SwkJTextField swkjtextfield, final String firstPos,
                final String lastPos) throws TclException {
            this.swkjtextfield = swkjtextfield;
            this.firstPos = firstPos;
            this.lastPos = lastPos;
            execOnThread();
            return errMessage;
        }

        public void run() {
            if (!swkjtextfield.isEditable()) {
                return;
            }
            Result result1 = new Result();
            Result result2 = new Result();
            swkjtextfield.getIndex(firstPos, 0, result1);
            if (result1.hasError()) {
                errMessage = result1.getErrorMsg();
            } else {
                int nChar = 0;
                if (lastPos != null) {
                    swkjtextfield.getIndex(lastPos, 0, result2);
                    if (result2.hasError()) {
                        errMessage = result2.getErrorMsg();
                        return;
                    }
                    nChar = result2.i-result1.i;
                } else {
                    nChar = 1;
                }
                try {
                    swkjtextfield.getDocument().remove(result1.i,nChar);
                } catch (BadLocationException bLE) {
                     System.out.println(result1.i + " " + result2.i + " " + nChar);
                     errMessage = bLE.getMessage();
                }
            }
        }
    }

    class Insert extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        String strIndex = null;
        String text = null;
        String errMessage = null;

        void exec(final SwkJTextField swkjtextfield, final String strIndex,
                final String text) throws TclException {
            this.strIndex = strIndex;
            this.text = text;
            this.swkjtextfield = swkjtextfield;
            execOnThread();
        }

        public void run() {
            if (!swkjtextfield.isEditable()) {
                return;
            }
            Result result = new Result();
            swkjtextfield.getIndex(strIndex, 0, result);

            if (!result.hasError()) {
                try {
                    swkjtextfield.getDocument().insertString(result.i, text,
                            null);
                } catch (BadLocationException bLE) {
                    // throw new TclException(interp, bLE.toString());
                    // FIXME need to do something like add background error
                }
            }
        }
    }

    class ViewValues extends GetValueOnEventThread {

        SwkJTextField swkjtextfield = null;
        double fx1 = 0.0;
        double fx2 = 0.0;

        void exec(final SwkJTextField swkjtextfield) throws TclException {
            this.swkjtextfield = swkjtextfield;
            execOnThread();

            TclObject list = TclList.newInstance();
            TclList.append(interp, list, TclDouble.newInstance(fx1));
            TclList.append(interp, list, TclDouble.newInstance(fx2));
            interp.setResult(list);
        }

        public void run() {
            BoundedRangeModel brm = swkjtextfield.getHorizontalVisibility();
            fx1 = (1.0 * brm.getValue()) / (brm.getMaximum()
                    - brm.getMinimum());
            fx2 = (1.0 * (brm.getValue() + brm.getExtent())) / (brm.getMaximum()
                    - brm.getMinimum());
        }
    }
}
