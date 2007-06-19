/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;


class SwkJMenuWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "object", "jadd", "add", "delete", "popup", "post",
        "invoke", "index"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_OBJECT = 2;
    static final private int OPT_JADD = 3;
    static final private int OPT_ADD = 4;
    static final private int OPT_DELETE = 5;
    static final private int OPT_POPUP = 6;
    static final private int OPT_POST = 7;
    static final private int OPT_INVOKE = 8;
    static final private int OPT_INDEX = 9;
    static boolean gotDefaults = false;
    Interp interp;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        this.interp = interp;

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.theWidgets.get(argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJMenu swkjmenu = (SwkJMenu) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjmenu.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjmenu.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjmenu.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjmenu.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJMenu.resourceDB.get(argv[2].toString());
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
                swkjmenu.configure(interp, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swkjmenu.jadd(interp, argv[2]);

            break;

        case OPT_ADD:
            addmenu(interp, swkjmenu, argv);

            break;

        case OPT_DELETE:
            delete(interp, swkjmenu, argv);

            break;

        case OPT_POPUP: {
            popup(interp, swkjmenu, argv);

            break;
        }

        case OPT_POST: {
            post(interp, swkjmenu, argv);

            break;
        }

        case OPT_INVOKE: {
            invoke(interp, swkjmenu, argv);

            break;
        }

        case OPT_INDEX: {
            index(interp, swkjmenu, argv);

            break;
        }

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void popup(final Interp interp, final SwkJMenu swkjmenu,
        final TclObject[] argv) throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        if (argv[2].toString().equals("show")) {
            if (argv.length != 6) {
                throw new TclNumArgsException(interp, 3, argv, "widget x y");
            }

            TclObject tObj2 = (TclObject) Widgets.theWidgets.get(argv[3].toString());

            if (tObj2 == null) {
                throw new TclException(interp,
                    "widget \"" + argv[3].toString() + "\" doesn't exist");
            }

            final SwkWidget widget = (SwkWidget) ReflectObject.get(interp, tObj2);
            final int x = TclInteger.get(interp, argv[4]);
            final int y = TclInteger.get(interp, argv[5]);
            (new Popup()).exec(swkjmenu, widget, x, y);
        }
    }

    void post(final Interp interp, final SwkJMenu swkjmenu,
        final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        String parent = Widgets.pathParent(interp, argv[0].toString());
        TclObject tObj2 = (TclObject) Widgets.theWidgets.get(parent);

        if (tObj2 == null) {
            throw new TclException(interp,
                "widget \"" + parent + "\" doesn't exist");
        }

        final int x = TclInteger.get(interp, argv[2]);
        final int y = TclInteger.get(interp, argv[3]);
        final SwkWidget widget = (SwkWidget) ReflectObject.get(interp, tObj2);

        Point point = ((Component) widget).getLocationOnScreen();

        (new Post()).exec(swkjmenu, widget, x, y);
    }

    void index(final Interp interp, final SwkJMenu swkjmenu,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        int index = (new Index()).exec(swkjmenu, argv[2]);
        interp.setResult(index);
    }

    void invoke(final Interp interp, final SwkJMenu swkjmenu,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        String command = (new Invoke()).exec(swkjmenu, argv[2]);

        if (command != null) {
            SwkExceptionCmd.doExceptionCmd(interp, command);
        }
    }

    void delete(final Interp interp, final SwkJMenu swkjmenu,
        final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "first ?last");
        }

        TclObject firstArg = argv[2].duplicate();
        TclObject lastArg = null;

        if (argv.length == 4) {
            lastArg = argv[3].duplicate();
        }

        (new Delete()).exec(swkjmenu, firstArg, lastArg);
    }

    /*      public SwkWidget Addrun(SwkJMenu swkjmenu,String itemType) {
          //JComponent jcomp = null;
         if (itemType.equals("command")) {
              SwkJMenuItem jmenuItem = new SwkJMenuItem(interp, "", "SwkJMenuItem");
              return (SwkWidget)jmenuItem;

          } else if (itemType.startsWith("check")) {
              SwkJCheckBoxMenuItem jmenuItem = new SwkJCheckBoxMenuItem(interp,
                      "", "SwkJMenuItem");

          } else if (itemType.startsWith("radio")) {
              SwkJRadioButtonMenuItem jmenuItem = new SwkJRadioButtonMenuItem(interp,
                      "", "SwkJMenuItem");

          } else if (itemType.startsWith("sepa")) {
              SwkJMenuItem jmenuItem = null;

          } else if (itemType.equals("cascade")) {
              String menuName = null;
              int j = 0;
          }

            return jmenuItem;
          }

     */
    public void addmenu(Interp interp, SwkJMenu swkjmenu, TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        String itemType = argv[2].toString();

        if (itemType.equals("cascade")) {
            String menuName = null;
            int j = 0;

            if (((argv.length - 3) % 2) != 0) {
                throw new TclNumArgsException(interp, 1, argv,
                    "arguments not multiple of 2");
            }

            TclObject[] argNew = new TclObject[argv.length - 5];

            for (i = 3; i < argv.length; i += 2) {
                if (argv[i].toString().equals("-menu")) {
                    menuName = argv[i + 1].toString();
                } else {
                    argNew[j] = TclString.newInstance(argv[i].toString());
                    argNew[j + 1] = TclString.newInstance(argv[i + 1].toString());
                    j += 2;
                }
            }

            TclObject tObj = (TclObject) Widgets.theWidgets.get(menuName);

            if (tObj == null) {
                SwkJMenu cascade = (new Add()).exec(swkjmenu, menuName, itemType);
                interp.createCommand(menuName, new SwkJMenuWidgetCmd());
                tObj = ReflectObject.newInstance(interp, SwkJMenu.class, cascade);
                tObj.preserve();
                cascade.children = null;
                cascade = (SwkJMenu) ReflectObject.get(interp, tObj);
                cascade.configure(interp, argNew, 0);
                Widgets.addNewWidget(interp, menuName, tObj);
                cascade.setCreated(false);
            } else {
                SwkJMenu cascade = (SwkJMenu) ReflectObject.get(interp, tObj);
                (new Add()).exec(swkjmenu, menuName, cascade);
                cascade.configure(interp, argNew, 0);
            }
        } else {
            SwkWidget swkWidget = (new Add()).exec(swkjmenu, itemType);

            if (swkWidget != null) {
                swkWidget.configure(interp, argv, 3);
            }
        }

        swkjmenu.revalidate();

        return;
    }

    class Popup extends UpdateOnEventThread {
        int x = 0;
        int y = 0;
        SwkJMenu swkjmenu = null;
        SwkWidget widget = null;

        void exec(final SwkJMenu swkjmenu, final SwkWidget widget, final int x,
            final int y) {
            this.x = x;
            this.y = y;
            this.widget = widget;
            this.swkjmenu = swkjmenu;
            execOnThread();
        }

        public void run() {
            swkjmenu.getPopupMenu().show((Component) widget, x, y);
        }
    }

    class Post extends UpdateOnEventThread {
        int x = 0;
        int y = 0;
        SwkJMenu swkjmenu = null;
        SwkWidget widget = null;

        void exec(final SwkJMenu swkjmenu, final SwkWidget widget, final int x,
            final int y) {
            this.x = x;
            this.y = y;
            this.widget = widget;
            this.swkjmenu = swkjmenu;
            execOnThread();
        }

        public void run() {
            Point point = ((Component) widget).getLocationOnScreen();
            swkjmenu.getPopupMenu().show((Component) widget, x - point.x,
                y - point.y);
        }
    }

    class Index extends GetValueOnEventThread {
        SwkJMenu swkjmenu = null;
        TclObject item = null;
        int index = 0;

        int exec(final SwkJMenu swkjmenu, final TclObject item) {
            this.item = item;
            this.swkjmenu = swkjmenu;
            execOnThread();

            return index;
        }

        public void run() {
            int compIndex = 0;

            try {
                index = swkjmenu.getIndex(interp, item, -1);

                if (index < 0) {
                    interp.setResult("none");
                } else {
                    interp.setResult(compIndex);
                }
            } catch (TclException tclE) {
                interp.backgroundError();
            }
        }
    }

    class Invoke extends GetValueOnEventThread {
        SwkJMenu swkjmenu = null;
        TclObject item = null;
        String command = null;

        String exec(final SwkJMenu swkjmenu, final TclObject item) {
            this.item = item;
            this.swkjmenu = swkjmenu;
            execOnThread();

            return command;
        }

        public void run() {
            int compIndex = 0;

            try {
                compIndex = swkjmenu.getIndex(interp, item, -1);
            } catch (TclException tclE) {
                interp.backgroundError();
            }

            if (compIndex < 0) {
                return;
            }

            // FIXME  not thread correct
            SwkJMenuItem menuItem = (SwkJMenuItem) swkjmenu.getMenuComponent(compIndex);

            if (menuItem != null) {
                command = menuItem.getCommand();
            }
        }
    }

    class Delete extends GetValueOnEventThread {
        SwkJMenu swkjmenu = null;
        TclObject firstArg = null;
        TclObject lastArg = null;

        void exec(final SwkJMenu swkjmenu, final TclObject firstArg,
            final TclObject lastArg) {
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            this.swkjmenu = swkjmenu;
            execOnThread();
        }

        public void run() {
            int first = 0;

            try {
                first = swkjmenu.getIndex(interp, firstArg, -1);
            } catch (TclException tclE) {
                System.out.println("exception in jmenu index " +
                    tclE.getMessage());
                interp.backgroundError();

                //FIXME
                return;
            }

            int last = first;

            if (lastArg != null) {
                try {
                    last = swkjmenu.getIndex(interp, lastArg, -1);
                } catch (TclException tclE) {
                    System.out.println("exception in jmenu last index " +
                        tclE.getMessage());
                    interp.backgroundError();

                    //FIXME
                    return;
                }
            }

            if (last < first) {
                int hold;
                hold = last;
                last = first;
                first = hold;
            }

            for (int i = last; i >= first; i--) {
                if ((i >= 0) && (i < swkjmenu.getMenuComponentCount())) {
                    swkjmenu.remove(i);
                }
            }
        }
    }

    class Add extends GetValueOnEventThread {
        SwkJMenu swkjmenu = null;
        JComponent jcomp = null;
        String itemType = "";
        String menuName = "";
        SwkWidget swkWidget = null;
        SwkJMenu cascade = null;

        SwkWidget exec(final SwkJMenu swkjmenu, final String itemType) {
            this.swkjmenu = swkjmenu;
            this.itemType = itemType;
            execOnThread();

            return swkWidget;
        }

        SwkJMenu exec(final SwkJMenu swkjmenu, final String menuName,
            final String itemType) {
            this.swkjmenu = swkjmenu;
            this.itemType = itemType;
            this.menuName = menuName;
            execOnThread();

            return cascade;
        }

        SwkJMenu exec(final SwkJMenu swkjmenu, final String menuName,
            SwkJMenu cascade) {
            this.swkjmenu = swkjmenu;
            this.cascade = cascade;
            this.itemType = "cascade";
            this.menuName = menuName;
            execOnThread();

            return cascade;
        }

        public void run() {
            //JComponent jcomp = null;
            if (itemType.equals("command")) {
                SwkJMenuItem jmenuItem = new SwkJMenuItem(interp, "",
                        "SwkJMenuItem");
                swkjmenu.add(jmenuItem);
                swkWidget = (SwkWidget) jmenuItem;
            } else if (itemType.startsWith("check")) {
                SwkJCheckBoxMenuItem jmenuItem = new SwkJCheckBoxMenuItem(interp,
                        "", "SwkJMenuItem");
                swkjmenu.add(jmenuItem);
                swkWidget = (SwkWidget) jmenuItem;
            } else if (itemType.startsWith("radio")) {
                SwkJRadioButtonMenuItem jmenuItem = new SwkJRadioButtonMenuItem(interp,
                        "", "SwkJMenuItem");
                swkjmenu.add(jmenuItem);
                swkWidget = (SwkWidget) jmenuItem;
            } else if (itemType.startsWith("sepa")) {
                SwkJMenuItem jmenuItem = null;
                swkjmenu.addSeparator();
                ;
            } else if (itemType.equals("cascade")) {
                if (cascade == null) {
                    cascade = new SwkJMenu(interp, menuName, "Menu");
                }

                swkjmenu.add(cascade);
            }
        }
    }
}
