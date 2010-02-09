/*
 * Copyright (c) 2000-2004 One Moon Scientific., Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;
import java.awt.image.*;

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;


class SwkJComboBoxWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "remove", "item", "index"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_REMOVE = 2;
    static final private int OPT_ITEM = 3;
    static final private int OPT_INDEX = 4;
    static boolean gotDefaults = false;
    int index;
    Interp interp = null;
    SwkJComboBox swkjcombobox = null;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        this.interp = interp;

        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJComboBox swkjcombobox = (SwkJComboBox) ReflectObject.get(interp,
                tObj);
        this.swkjcombobox = swkjcombobox;

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjcombobox.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjcombobox.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjcombobox.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjcombobox.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJComboBox.resourceDB.get(argv[2].toString());

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
                swkjcombobox.configure(interp, argv, 2);
            }

            break;

        case OPT_REMOVE:
            remove(interp, swkjcombobox, argv);

            break;

        case OPT_ITEM:
            item(interp, swkjcombobox, argv);

            break;

        case OPT_INDEX:
            index(interp, swkjcombobox, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void index(final Interp interp, final SwkJComboBox swkjcombobox,
        final TclObject[] argv) throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        (new GetIndex()).exec(argv[2].toString());
    }

    void remove(final Interp interp, final SwkJComboBox swkjcombobox,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        final int rIndex;

        if (!argv[2].toString().equals("all")) {
            rIndex = TclInteger.get(interp, argv[2]);
        } else {
            rIndex = -1;
        }

        (new RemoveItems()).exec(rIndex);
    }

    void item(final Interp interp, final SwkJComboBox swkjcombobox,
        final TclObject[] argv) throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv,
                "index ?element element ...?");
        }

        if (argv[2].toString().equals("count")) {
            (new GetItemCount()).exec();
        } else if (argv[2].toString().equals("name")) {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            index = TclInteger.get(interp, argv[3]);

            String result = (new GetItemAt()).exec(index);
            interp.setResult(result);
        } else if (argv[2].toString().equals("append")) {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            String[] itemStrings = new String[argv.length - 3];

            for (int i = 3; i < argv.length; i++) {
                itemStrings[i - 3] = argv[i].toString();
            }

            (new AddItems()).exec(itemStrings);
        } else {
            throw new TclException(interp, "Invalid item argument");
        }
    }

    class GetIndex extends GetValueOnEventThread {
        int index = -1;
        String item = null;

        void exec(String item) {
            this.item = item;
            execOnThread();
            interp.setResult(index);
        }

        public void run() {
            for (int i = 0; i < swkjcombobox.getItemCount(); i++) {
                if (swkjcombobox.getItemAt(i).toString().equals(item)) {
                    index = i;

                    break;
                }
            }
        }
    }

    class RemoveItems extends UpdateOnEventThread {
        int index = -1;

        void exec(final int index) {
            this.index = index;
            execOnThread();
        }

        public void run() {
            swkjcombobox.setCreated(false);

            if (index == -1) {
                swkjcombobox.removeAllItems();
            } else {
                swkjcombobox.removeItemAt(index);
            }

            swkjcombobox.setCreated(true);
        }
    }

    class GetItemCount extends GetValueOnEventThread {
        int intResult;

        void exec() {
            execOnThread();
            interp.setResult(intResult);
        }

        public void run() {
            intResult = swkjcombobox.getItemCount();
        }
    }

    class GetItemAt extends GetValueOnEventThread {
        String result;
        int index = 0;

        String exec(int index) {
            this.index = index;
            execOnThread();

            return result;
        }

        public void run() {
            result = swkjcombobox.getItemAt(index).toString();
        }
    }

    class AddItems extends GetValueOnEventThread {
        Object[] itemObjects = null;
        String s1 = null;

        void exec(final String[] itemStrings) {
            getObjects(itemStrings);
            execOnThread();
            swkjcombobox.commandListener.setVarValue(s1);
            swkjcombobox.setCreated(true);
        }

        void getObjects(String[] itemStrings) {
            itemObjects = new Object[itemStrings.length];

            for (int i = 0; i < itemStrings.length; i++) {
                if (itemStrings[i].startsWith(".")) {
                    TclObject tObj2 = (TclObject) Widgets.getWidget(interp,itemStrings[i]);

                    if (tObj2 != null) {
                        try {
                            SwkWidget widget = (SwkWidget) ReflectObject.get(interp,
                                    tObj2);
                            itemObjects[i] = widget;
                        } catch (TclException tclE) {
                            itemObjects[i] = itemStrings[i];
                        }
                    } else {
                        itemObjects[i] = itemStrings[i];
                    }
                } else if (itemStrings[i].startsWith("image:") && (itemStrings[i].length() > 7)) {
                   Object imageObject =  ImageCmd.getImage(itemStrings[i].substring(6));
                   if ((imageObject != null) && (imageObject instanceof BufferedImage)) {
                       itemObjects[i] = (BufferedImage) imageObject;
                   } else if ((imageObject != null) && (imageObject instanceof ImageIcon)) {
                       itemObjects[i] = (ImageIcon) imageObject;
                   } else {
                       itemObjects[i] = itemStrings[i];
                   }
                } else {
                    itemObjects[i] = itemStrings[i];
                }
            }
        }

        public void run() {
            swkjcombobox.setCreated(false);

            for (int i = 0; i < itemObjects.length; i++) {
                if (itemObjects[i] instanceof SwkWidget) {
                    swkjcombobox.addItem((SwkWidget) itemObjects[i]);
                } else if (itemObjects[i] instanceof BufferedImage) {
                    swkjcombobox.addItem(itemObjects[i]);
                } else if (itemObjects[i] instanceof ImageIcon) {
                    swkjcombobox.addItem(itemObjects[i]);
                } else {
                    swkjcombobox.addItem((String) itemObjects[i]);
                }
            }

            s1 = swkjcombobox.getSelectedItem().toString();

            //   System.out.println("*****getselected Item :"+ s1);
            //   swkjcombobox.setCreated(true);
        }
    }
}
