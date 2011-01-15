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


import javax.swing.*;

class SwkJMenuWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "add", "delete", "popup", "post",
        "invoke", "index", "insert", "entrycget", "entryconfigure"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_ADD = 2;
    static final private int OPT_DELETE = 3;
    static final private int OPT_POPUP = 4;
    static final private int OPT_POST = 5;
    static final private int OPT_INVOKE = 6;
    static final private int OPT_INDEX = 7;
    static final private int OPT_INSERT = 8;
    static final private int OPT_ENTRYCGET = 9;
    static final private int OPT_ENTRYCONFIGURE = 10;
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
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

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
                    swkjmenu.configure(interp, argv, 2);
                }

                break;
            case OPT_ENTRYCGET:
                if (argv.length != 4) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }
                String ecgetResult = (new EntryConfigure()).execGet(interp, swkjmenu, argv, 2);
                interp.setResult(ecgetResult);
                break;

            case OPT_ENTRYCONFIGURE:
                if (argv.length == 2) {
                    throw new TclNumArgsException(interp, 2, argv, "index ?options?");
                } else if (argv.length == 3) {
                    (new EntryConfigure()).execGetAll(interp, swkjmenu, argv, 2);
                } else if (argv.length == 4) {
                    String result = (new EntryConfigure()).execGet(interp, swkjmenu, argv, 2);
                    ResourceObject ro = (ResourceObject) SwkJMenu.resourceDB.get(argv[3].toString());

                    if (ro == null) {
                        throw new TclException(interp,
                                "unknown option \"" + argv[2].toString() + "\"");
                    }

                    TclObject list = TclList.newInstance();
                    TclList.append(interp, list,
                            TclString.newInstance(argv[3].toString()));
                    TclList.append(interp, list, TclString.newInstance(argv[3].toString()));
                    TclList.append(interp, list, TclString.newInstance(argv[3].toString()));
                    TclList.append(interp, list, TclString.newInstance(""));
                    TclList.append(interp, list, TclString.newInstance(result));
                    interp.setResult(list);
                } else {
                    (new EntryConfigure()).execConfigure(interp, swkjmenu, argv, 2);
                }

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
            case OPT_INSERT:
                insertmenu(interp, swkjmenu, argv);

                break;


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

            TclObject tObj2 = (TclObject) Widgets.getWidget(interp, argv[3].toString());

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
        TclObject tObj2 = (TclObject) Widgets.getWidget(interp, parent);

        if (tObj2 == null) {
            throw new TclException(interp,
                    "widget \"" + parent + "\" doesn't exist");
        }

        final int x = TclInteger.get(interp, argv[2]);
        final int y = TclInteger.get(interp, argv[3]);
        final SwkWidget widget = (SwkWidget) ReflectObject.get(interp, tObj2);
        (new Post()).exec(swkjmenu, widget, x, y);
    }

    void index(final Interp interp, final SwkJMenu swkjmenu,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        int index = (new Index()).exec(swkjmenu, argv[2]);

        if (index < 0) {
            interp.setResult("none");
        } else {
            interp.setResult(index);
        }
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
    public void insertmenu(Interp interp, SwkJMenu swkjmenu, TclObject[] argv) throws TclException {
        addOrInsertMenu(interp, swkjmenu, argv, true);
    }

    public void addmenu(Interp interp, SwkJMenu swkjmenu, TclObject[] argv) throws TclException {
        addOrInsertMenu(interp, swkjmenu, argv, false);
    }

    public void addOrInsertMenu(Interp interp, SwkJMenu swkjmenu, TclObject[] argv, boolean insertMode)
            throws TclException {
        int i;
        int start = 3;
        int insertPos = -1;
        if (insertMode) {
            start = 4;
            insertPos = TclInteger.get(interp, argv[2]);
        }
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        String itemType = argv[start - 1].toString();

        if (itemType.equals("cascade")) {
            String menuName = null;
            int j = 0;

            if (((argv.length - start) % 2) != 0) {
                throw new TclNumArgsException(interp, 1, argv,
                        "arguments not multiple of 2");
            }

            TclObject[] argNew = new TclObject[argv.length - start - 2];

            for (i = start; i < argv.length; i += 2) {
                if (argv[i].toString().equals("-menu")) {
                    menuName = argv[i + 1].toString();
                } else {
                    argNew[j] = TclString.newInstance(argv[i].toString());
                    argNew[j + 1] = TclString.newInstance(argv[i + 1].toString());
                    j += 2;
                }
            }
            TclObject tObj = (TclObject) Widgets.getWidget(interp, menuName);

            if (tObj == null) {
                SwkJMenu cascade = (new Add()).exec(swkjmenu, menuName, itemType, insertPos);
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
                (new Add()).exec(swkjmenu, menuName, cascade, insertPos);
                cascade.configure(interp, argNew, 0);
            }
        } else {
            SwkWidget swkWidget = (new Add()).exec(swkjmenu, itemType, insertPos);

            if (swkWidget != null) {
                swkWidget.configure(interp, argv, start);
            }
        }

        swkjmenu.revalidate();

        return;
    }

    static class Popup extends UpdateOnEventThread {

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

        @Override
        public void run() {
            swkjmenu.getPopupMenu().show((Component) widget, x, y);
        }
    }

    static class Post extends UpdateOnEventThread {

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

        @Override
        public void run() {
            Point point = ((Component) widget).getLocationOnScreen();
            swkjmenu.getPopupMenu().show((Component) widget, x - point.x,
                    y - point.y);
        }
    }

    class Index extends GetValueOnEventThread {

        SwkJMenu swkjmenu = null;
        int index = 0;
        String sItem = null;

        int exec(final SwkJMenu swkjmenu, final TclObject item) {
            this.swkjmenu = swkjmenu;

            try {
                index = TclInteger.get(interp, item);
            } catch (TclException tclE) {
                sItem = item.toString();
            }

            execOnThread();

            return index;
        }

        @Override
        public void run() {
            if (sItem != null) {
                index = swkjmenu.getIndex(sItem, -1);
            }
        }
    }

    class Invoke extends GetValueOnEventThread {

        SwkJMenu swkjmenu = null;
        String command = null;
        String sItem = null;
        int compIndex = 0;

        String exec(final SwkJMenu swkjmenu, final TclObject item) {
            this.swkjmenu = swkjmenu;

            try {
                compIndex = TclInteger.get(interp, item);
            } catch (TclException tclE) {
                sItem = item.toString();
            }

            execOnThread();

            return command;
        }

        @Override
        public void run() {
            if (sItem != null) {
                compIndex = swkjmenu.getIndex(sItem, -1);
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
        TclObject lastArg = null;
        String sIndex = null;
        String sIndexLast = null;
        int first = 0;
        int last = 0;

        void exec(final SwkJMenu swkjmenu, final TclObject firstArg,
                final TclObject lastArg) {
            this.lastArg = lastArg;
            this.swkjmenu = swkjmenu;

            try {
                first = TclInteger.get(interp, firstArg);
            } catch (TclException tclE) {
                sIndex = firstArg.toString();
            }

            if (lastArg != null) {
                try {
                    last = TclInteger.get(interp, lastArg);
                } catch (TclException tclE) {
                    sIndexLast = lastArg.toString();
                }
            }

            execOnThread();
        }

        public void run() {
            if (sIndex != null) {
                first = swkjmenu.getIndex(sIndex, -1);
            }

            if (lastArg == null) {
                last = first;
            } else {
                if (sIndexLast != null) {
                    last = swkjmenu.getIndex(sIndexLast, -1);
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
        String itemType = "";
        String menuName = "";
        SwkWidget swkWidget = null;
        SwkJMenu cascade = null;
        int insertPos = -1;

        SwkWidget exec(final SwkJMenu swkjmenu, final String itemType, final int insertPos) {
            this.swkjmenu = swkjmenu;
            this.itemType = itemType;
            this.insertPos = insertPos;
            execOnThread();

            return swkWidget;
        }

        SwkJMenu exec(final SwkJMenu swkjmenu, final String menuName,
                final String itemType, final int insertPos) {
            this.swkjmenu = swkjmenu;
            this.itemType = itemType;
            this.menuName = menuName;
            this.insertPos = insertPos;
            execOnThread();

            return cascade;
        }

        SwkJMenu exec(final SwkJMenu swkjmenu, final String menuName,
                SwkJMenu cascade, final int insertPos) {
            this.swkjmenu = swkjmenu;
            this.cascade = cascade;
            this.itemType = "cascade";
            this.menuName = menuName;
            this.insertPos = insertPos;
            execOnThread();

            return cascade;
        }

        @Override
        public void run() {
            //JComponent jcomp = null;
            if (itemType.equals("command")) {
                SwkJMenuItem jmenuItem = new SwkJMenuItem(interp, "");
                if (insertPos != -1) {
                    swkjmenu.insert(jmenuItem, insertPos);
                } else {
                    swkjmenu.add(jmenuItem);
                }
                swkWidget = (SwkWidget) jmenuItem;
            } else if (itemType.startsWith("check")) {
                SwkJCheckBoxMenuItem jmenuItem;
                jmenuItem = new SwkJCheckBoxMenuItem(interp, "");
                if (insertPos != -1) {
                    swkjmenu.insert(jmenuItem, insertPos);
                } else {
                    swkjmenu.add(jmenuItem);
                }
                swkWidget = (SwkWidget) jmenuItem;
            } else if (itemType.startsWith("radio")) {
                SwkJRadioButtonMenuItem jmenuItem = new SwkJRadioButtonMenuItem(interp, "");
                if (insertPos != -1) {
                    swkjmenu.insert(jmenuItem, insertPos);
                } else {
                    swkjmenu.add(jmenuItem);
                }
                swkWidget = (SwkWidget) jmenuItem;
            } else if (itemType.startsWith("sepa")) {
                SwkJMenuItem jmenuItem = null;
                if (insertPos != -1) {
                    swkjmenu.insertSeparator(insertPos);
                } else {
                    swkjmenu.addSeparator();
                }
            } else if (itemType.equals("cascade")) {
                if (cascade == null) {
                    cascade = new SwkJMenu(interp, menuName);
                }

                if (insertPos != -1) {
                    swkjmenu.insert(cascade, insertPos);
                } else {
                    swkjmenu.add(cascade);
                }
            }
        }
    }

    class EntryConfigure extends GetValueOnEventThread {

        SwkJMenu swkjmenu = null;
        TclObject entryArg = null;
        String sIndex = null;
        int index = 0;
        SwkJMenuItem jMenuItem = null;

        String execGet(Interp interp, final SwkJMenu swkjmenu, final TclObject[] argv, final int start) throws TclException {
            this.entryArg = argv[start];
            this.swkjmenu = swkjmenu;
            getEntry();
            if (jMenuItem != null) {
                return jMenuItem.jget(interp, argv[start + 1]);
            }
            return "";
        }

        void execConfigure(Interp interp, final SwkJMenu swkjmenu, final TclObject[] argv, final int start) throws TclException {
            this.entryArg = argv[start];
            this.swkjmenu = swkjmenu;
            getEntry();
            if (jMenuItem != null) {
                jMenuItem.configure(interp, argv, start + 1);
            }
        }

        void execGetAll(Interp interp, final SwkJMenu swkjmenu, final TclObject[] argv, final int start) throws TclException {
            this.entryArg = argv[start];
            this.swkjmenu = swkjmenu;
            getEntry();
            if (jMenuItem != null) {
                jMenuItem.jgetAll(interp);
            }
        }

        void getEntry() {
            try {
                index = TclInteger.get(interp, entryArg);
            } catch (TclException tclE) {
                sIndex = entryArg.toString();
            }
            execOnThread();
        }

        @Override
        public void run() {
            if (sIndex != null) {
                index = swkjmenu.getIndex(sIndex, -1);
            }
            Component comp = swkjmenu.getPopupMenu().getComponent(index);
            if (comp instanceof SwkJMenuItem) {
                jMenuItem = (SwkJMenuItem) comp;
            }
        }
    }
}
