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
import java.util.ArrayList;
import javax.swing.*;

class SwkJListWidgetCmd implements Command {

    private static final String[] validCmds = {
"activate",
"bbox",
"cget",
"configure",
"curselection",
"delete",
"get",
"index",
"insert",
"itemcget",
"itemconfigure",
"nearest",
"scan",
"see",
"selection",
"size",
"xview",
"yview"
    };
    private static final int OPT_ACTIVATE = 0;
    private static final int OPT_BBOX = 1;
    private static final int OPT_CGET = 2;
    private static final int OPT_CONFIGURE = 3;
    private static final int OPT_CURSELECTION = 4;
    private static final int OPT_DELETE = 5;
    private static final int OPT_GET = 6;
    private static final int OPT_INDEX = 7;
    private static final int OPT_INSERT = 8;
    private static final int OPT_ITEMCGET = 9;
    private static final int OPT_ITEMCONFIGURE = 10;
    private static final int OPT_NEAREST = 11;
    private static final int OPT_SCAN = 12;
    private static final int OPT_SEE = 13;
    private static final int OPT_SELECTION = 14;
    private static final int OPT_SIZE = 15;
    private static final int OPT_XVIEW = 16;
    private static final int OPT_YVIEW = 17;
    static boolean gotDefaults = false;
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
        final TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJList swkjlist = (SwkJList) ReflectObject.get(interp, tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjlist.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjlist.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjlist.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjlist.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJList.resourceDB.get(argv[2].toString());

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
                    swkjlist.configure(interp, argv, 2);
                }

                break;

            case OPT_BBOX:
                getBoundingBox(interp, swkjlist, argv);

                break;

            case OPT_CURSELECTION:
                getCurrentSelection(interp, swkjlist, argv);

                break;

            case OPT_DELETE:
                deleteItems(interp, swkjlist, argv);

                break;

            case OPT_GET:
                getItems(interp, swkjlist, argv);

                break;

            case OPT_SIZE:
                getSize(interp, swkjlist, argv);

                break;

            case OPT_SEE:
                seeItem(interp, swkjlist, argv);

                break;

            case OPT_INDEX:
                getIndex(interp, swkjlist, argv);

                break;
            case OPT_ITEMCGET:
                itemCGet(interp, swkjlist, argv);
                break;
            case OPT_ITEMCONFIGURE:
                itemConfigure(interp, swkjlist, argv);
                break;


            case OPT_ACTIVATE:
                activate(interp, swkjlist, argv);

                break;

            case OPT_NEAREST:
                nearest(interp, swkjlist, argv);

                break;

            case OPT_INSERT:
                insert(interp, swkjlist, argv);

                break;

            case OPT_SELECTION:
                doSelection(interp, swkjlist, argv);

                break;

            case OPT_SCAN:
            case OPT_XVIEW:
            case OPT_YVIEW:
                viewStuff(interp, swkjlist, argv, opt);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void getBoundingBox(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        Rectangle rect = (new BoundingBox()).exec(swkjlist, argv[2].toString());

        if (rect != null) {
            interp.setResult(SwankUtil.parseRectangle(rect));
        }
    }

    void getCurrentSelection(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        int[] selected = (new CurrentSelection()).exec(swkjlist);
        TclObject list = TclList.newInstance();

        for (int i = 0; i < selected.length; i++) {
            TclList.append(interp, list, TclInteger.newInstance(selected[i]));
        }

        interp.setResult(list);
    }

    void deleteItems(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "first ?last");
        }

        String firstArg = argv[2].toString();
        String lastArg = null;

        if (argv.length == 4) {
            lastArg = argv[3].toString();
        }

        (new Delete()).exec(swkjlist, firstArg, lastArg);
    }

    void getItems(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if ((argv.length < 3) || (argv.length > 4)) {
            throw new TclNumArgsException(interp, 2, argv, "first ?last");
        }

        String firstArg = argv[2].toString();
        String lastArg = null;

        if (argv.length == 4) {
            lastArg = argv[3].toString();
        }

        (new Items()).exec(swkjlist, firstArg, lastArg);
    }

    void getSize(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        int size = (new Size()).exec(swkjlist);
        interp.setResult(size);
    }

    void seeItem(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        (new See()).exec(swkjlist, argv[2].toString());
    }

    void getIndex(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        int index = (new Index()).exec(swkjlist, argv[2].toString());
        interp.setResult(index);
    }

    void activate(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        int index = (new Activate()).exec(swkjlist, argv[2].toString());
        interp.setResult(index);
    }
    void itemCGet(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "index option");
        }

        ListItem listItem = (new ItemGet()).exec(swkjlist, argv[2].toString());
        String result = "";
        if (listItem != null) {
            String option = argv[3].toString();
            if ("-background".startsWith(option) || option.equals("-bg")) {
                  if (listItem.background != null) {
                      result = SwankUtil.parseColor(listItem.background);
                  }
            } else if ("-foreground".startsWith(option) || option.equals("-fg")) {
                  if (listItem.foreground != null) {
                      result = SwankUtil.parseColor(listItem.foreground);
                  }
            } else if ("-selectbackground".startsWith(option)) {
                  if (listItem.selectBackground != null) {
                      result = SwankUtil.parseColor(listItem.selectBackground);
                  }
            } else if ("-selectforeground".startsWith(option)) {
                  if (listItem.selectForeground != null) {
                      result = SwankUtil.parseColor(listItem.selectForeground);
                  }
            } else {
                  throw new TclException(interp,"unknown option \"" + option + "\"");
            }
        }
        interp.setResult(result);
    }
    void itemConfigure(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "index ?option? ?value? ?option value ...?");
        }
        if ((argv.length == 3) || (argv.length == 4)){
            ListItem listItem = (new ItemGet()).exec(swkjlist, argv[2].toString());
            TclObject resultList = TclList.newInstance();
            if ((argv.length == 3) || "-background".startsWith(argv[3].toString())) {
                TclObject itemList = TclList.newInstance();
                TclList.append(interp,itemList,TclString.newInstance("-background"));
                TclList.append(interp,itemList,TclString.newInstance("background"));
                TclList.append(interp,itemList,TclString.newInstance("Background"));
                TclList.append(interp,itemList,TclString.newInstance(SwankUtil.parseColor(listItem.getBackground())));
                TclList.append(interp,resultList,itemList);
            }
            if ((argv.length == 3) || "-foreground".startsWith(argv[3].toString())) {
                TclObject itemList = TclList.newInstance();
                TclList.append(interp,itemList,TclString.newInstance("-foreground"));
                TclList.append(interp,itemList,TclString.newInstance("foreground"));
                TclList.append(interp,itemList,TclString.newInstance("Foreground"));
                TclList.append(interp,itemList,TclString.newInstance(SwankUtil.parseColor(listItem.getForeground())));
                TclList.append(interp,resultList,itemList);
            }
            if ((argv.length == 3) || "-selectforeground".startsWith(argv[3].toString())) {
                TclObject itemList = TclList.newInstance();
                TclList.append(interp,itemList,TclString.newInstance("-selectforeground"));
                TclList.append(interp,itemList,TclString.newInstance("selectforeground"));
                TclList.append(interp,itemList,TclString.newInstance("Foreground"));
                TclList.append(interp,itemList,TclString.newInstance(SwankUtil.parseColor(listItem.getSelectForeground())));
                TclList.append(interp,resultList,itemList);
            }
            if ((argv.length == 3) || "-selectbackground".startsWith(argv[3].toString())) {
                TclObject itemList = TclList.newInstance();
                TclList.append(interp,itemList,TclString.newInstance("-selectbackground"));
                TclList.append(interp,itemList,TclString.newInstance("selectbackground"));
                TclList.append(interp,itemList,TclString.newInstance("Background"));
                TclList.append(interp,itemList,TclString.newInstance(SwankUtil.parseColor(listItem.getSelectBackground())));
                TclList.append(interp,resultList,itemList);
            }
            interp.setResult(resultList);
        } else {
            if ((argv.length % 2) != 1) {
                  throw new TclException(interp,"value for \"" + argv[argv.length-1].toString() + "\" missing ");
            }
            ListItem listItem = (new ItemGet()).exec(swkjlist, argv[2].toString());
            String listItemText = listItem.toString();
            Color background = listItem.getBackground();
            Color foreground = listItem.getForeground();
            Color selectBackground = listItem.getSelectBackground();
            Color selectForeground = listItem.getSelectForeground();
            for (int i=3;i<argv.length;i += 2) {
                String option = argv[i].toString();
                if ("-background".startsWith(option) || option.equals("-bg")) {
                    background = SwankUtil.getColor(interp,argv[i+1]);
                } else if ("-foreground".startsWith(option) || option.equals("-fg")) {
                    foreground = SwankUtil.getColor(interp,argv[i+1]);
                } else if ("-selectbackground".startsWith(option)) {
                    selectBackground = SwankUtil.getColor(interp,argv[i+1]);
                } else if ("-selectforeground".startsWith(option)) {
                    selectForeground = SwankUtil.getColor(interp,argv[i+1]);
                }
            }
            ListItem newValues = new ListItem(listItemText,background,foreground,selectBackground,selectForeground);
            (new ItemConfigure()).exec(swkjlist, argv[2].toString(),newValues);
        }
    }

    void nearest(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "y");
        }

        int y = TclInteger.get(interp, argv[2]);
        int index = (new Nearest()).exec(swkjlist, y);
        interp.setResult(index);
    }

    void insert(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        boolean atEnd = false;
        int start = 3;

        if (argv[2].toString().equals("end")) {
            atEnd = true;
        }

        String[] items = new String[argv.length - start];

        for (int i = start; i < argv.length; i++) {
            items[i - start] = argv[i].toString();
        }

        int size = (new Insert()).exec(swkjlist, argv[2].toString(), items,
                atEnd);
        interp.setResult(size);
    }

    void doSelection(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv) throws TclException {
        if ((argv.length < 4) || (argv.length > 5)) {
            throw new TclNumArgsException(interp, 2, argv, "option ?arg");
        }

        if (argv[2].toString().equals("anchor")) {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 3, argv, "index");
            }

            (new Selection()).execAnchor(swkjlist, argv[3].toString());
        } else if (argv[2].toString().equals("clear")) {
            String firstArg = argv[3].toString();
            String lastArg = null;

            if (argv.length == 5) {
                lastArg = argv[4].toString();
            }

            (new Selection()).execClear(swkjlist, firstArg, lastArg);
        } else if (argv[2].toString().equals("includes")) {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 3, argv, "index");
            }

            boolean includes = (new Includes()).exec(swkjlist,
                    argv[3].toString());
            interp.setResult(includes);
        } else if (argv[2].toString().equals("set")) {
            String firstArg = argv[3].toString();
            String lastArg = null;

            if (argv.length == 5) {
                lastArg = argv[4].toString();
            }

            (new Selection()).execSet(swkjlist, firstArg, lastArg);
        } else {
            throw new TclException(interp,
                    "bad selection option \"" + argv[2].toString()
                    + "\": must be anchor, clear, includes, or set");
        }
    }

    void viewStuff(final Interp interp, final SwkJList swkjlist,
            final TclObject[] argv, final int opt) throws TclException {
        Result idxResult = new Result();

        switch (opt) {
            case OPT_SCAN:

                if (argv.length != 5) {
                    throw new TclNumArgsException(interp, 2, argv, "mark|dragto x y");
                }

                int scanX = TclInteger.get(interp, argv[3]);
                int scanY = TclInteger.get(interp, argv[4]);

                if (argv[2].toString().equals("mark")) {
                    int markX = scanX;
                    int markY = scanY;
                } else if (argv[2].toString().equals("dragto")) {
                    int dragX = scanX;
                    int dragY = scanY;
                    double fy1 = 0.0;

                    if (fy1 < 0.0) {
                        fy1 = 0.0;
                    }

                    JViewport jvp = Widgets.getViewport((Component) swkjlist);

                    if (jvp != null) {
                        Point pt = jvp.getViewPosition();

                        Dimension listSize = swkjlist.getSize();
                        pt.y = (int) (fy1 * listSize.height);

                        Dimension extentSize = jvp.getExtentSize();

                        if ((pt.y + extentSize.height) > listSize.height) {
                            pt.y = listSize.height - extentSize.height;
                        }

                        jvp.setViewPosition(pt);
                    }
                } else {
                    throw new TclException(interp,
                            "bad scan option \"" + argv[2].toString()
                            + "\": must be mark or dragto");
                }

                break;

            case OPT_XVIEW: {
                int maxSize = 1;
                int len = 0;
                JViewport jvp = Widgets.getViewport((Component) swkjlist);

                for (int i = 0; i < swkjlist.model.getSize(); i++) {
                    len = ((String) swkjlist.model.elementAt(i)).length();

                    if (len > maxSize) {
                        maxSize = len;
                    }
                }

                if (argv.length == 2) {
                    if (jvp != null) {
                        Point pt = jvp.getViewPosition();

                        Dimension viewSize = jvp.getViewSize();
                        Dimension listSize = swkjlist.getSize();
                        Dimension extentSize = jvp.getExtentSize();

                        double fx1 = (1.0 * pt.x) / listSize.width;
                        double fx2 = (1.0 * (pt.x + extentSize.width)) / listSize.width;
                        TclObject list = TclList.newInstance();
                        TclList.append(interp, list, TclDouble.newInstance(fx1));
                        TclList.append(interp, list, TclDouble.newInstance(fx2));
                        interp.setResult(list);
                    }
                } else if (argv.length == 3) {
                    swkjlist.getIndex(argv[2].toString(), -1, idxResult);
                    idxResult.checkError(interp);
                    int index = idxResult.i;

                    double fx1 = (1.0 * index) / maxSize;

                    if (fx1 < 0.0) {
                        fx1 = 0.0;
                    }

                    Point pt = Widgets.getViewport((Component) swkjlist).getViewPosition();

                    Dimension listSize = swkjlist.getSize();
                    pt.y = (int) (fx1 * listSize.width);

                    if (jvp != null) {
                        Dimension extentSize = jvp.getExtentSize();

                        if ((pt.x + extentSize.width) > listSize.width) {
                            pt.x = listSize.width - extentSize.width;
                        }

                        jvp.setViewPosition(pt);
                    }
                } else if (argv[2].toString().equals("moveto")) {
                    if (argv.length != 4) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    double fx1 = TclDouble.get(interp, argv[3]);

                    if (fx1 < 0.0) {
                        fx1 = 0.0;
                    }

                    if (jvp != null) {
                        Point pt = jvp.getViewPosition();

                        Dimension listSize = swkjlist.getSize();
                        pt.x = (int) (fx1 * listSize.height);

                        Dimension extentSize = jvp.getExtentSize();

                        if ((pt.x + extentSize.width) > listSize.width) {
                            pt.x = listSize.width - extentSize.width;
                        }

                        jvp.setViewPosition(pt);
                    }
                } else if (argv[2].toString().equals("scroll")) {
                    if (argv.length != 5) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    if (argv[4].toString().equals("units")) {
                        if (jvp != null) {
                            Point pt = jvp.getViewPosition();

                            Dimension viewSize = jvp.getViewSize();
                            Dimension listSize = swkjlist.getSize();
                            Dimension extentSize = jvp.getExtentSize();

                            double fx1 = (1.0 * pt.x) / listSize.width;
                            int units = TclInteger.getInt(interp, argv[3]);
                            int incrX = listSize.width / maxSize;
                            pt.x = (int) ((fx1 * listSize.width) + (incrX * units));

                            if ((pt.x + extentSize.width) > listSize.width) {
                                pt.x = listSize.width - extentSize.width;
                            }

                            if (pt.x < 0) {
                                pt.x = 0;
                            }

                            jvp.setViewPosition(pt);
                        }
                    } else if (argv[4].toString().equals("pages")) {
                        if (jvp != null) {
                            Point pt = jvp.getViewPosition();

                            Dimension viewSize = jvp.getViewSize();
                            Dimension listSize = swkjlist.getSize();
                            Dimension extentSize = jvp.getExtentSize();

                            double fx1 = (1.0 * pt.x) / listSize.width;
                            int units = TclInteger.get(interp, argv[3]);
                            double incrX = extentSize.width;
                            pt.x = (int) ((fx1 * listSize.width) + (incrX * units));

                            if ((pt.x + extentSize.width) > listSize.width) {
                                pt.x = listSize.width - extentSize.width;
                            }

                            if (pt.x < 0) {
                                pt.x = 0;
                            }

                            jvp.setViewPosition(pt);
                        }
                    }
                } else {
                    throw new TclException(interp,
                            "unknown option \"" + argv[2].toString()
                            + "\": must be moveto or scroll");
                }

                break;
            }

            case OPT_YVIEW: {
                double fy1;
                double fy2;
                JViewport jvp = Widgets.getViewport((Component) swkjlist);

                if (argv.length == 2) {
                    if (jvp != null) {
                        Point pt = jvp.getViewPosition();

                        Dimension viewSize = jvp.getViewSize();
                        Dimension listSize = swkjlist.getSize();
                        Dimension extentSize = jvp.getExtentSize();
                        fy1 = (1.0 * pt.y) / listSize.height;
                        fy2 = (1.0 * (pt.y + extentSize.height)) / listSize.height;

                        TclObject list = TclList.newInstance();
                        TclList.append(interp, list, TclDouble.newInstance(fy1));
                        TclList.append(interp, list, TclDouble.newInstance(fy2));
                        interp.setResult(list);
                    }
                } else if (argv.length == 3) {
                    swkjlist.getIndex(argv[2].toString(), -1, idxResult);
                    idxResult.checkError(interp);
                    int index = idxResult.i;

                    if (swkjlist.model.getSize() == 0) {
                        fy1 = 1.0;
                    } else {
                        fy1 = (1.0 * index) / swkjlist.model.getSize();
                    }

                    if (fy1 < 0.0) {
                        fy1 = 0.0;
                    }

                    if (jvp != null) {
                        Point pt = jvp.getViewPosition();

                        Dimension listSize = swkjlist.getSize();
                        pt.y = (int) (fy1 * listSize.height);

                        Dimension extentSize = jvp.getExtentSize();

                        if ((pt.y + extentSize.height) > listSize.height) {
                            pt.y = listSize.height - extentSize.height;
                        }

                        jvp.setViewPosition(pt);
                    }
                } else if (argv[2].toString().equals("moveto")) {
                    if (argv.length != 4) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    fy1 = TclDouble.get(interp, argv[3]);

                    if (fy1 < 0.0) {
                        fy1 = 0.0;
                    }

                    if (jvp != null) {
                        Point pt = jvp.getViewPosition();

                        Dimension listSize = swkjlist.getSize();
                        pt.y = (int) (fy1 * listSize.height);

                        Dimension extentSize = jvp.getExtentSize();

                        if ((pt.y + extentSize.height) > listSize.height) {
                            pt.y = listSize.height - extentSize.height;
                        }

                        jvp.setViewPosition(pt);
                    }
                } else if (argv[2].toString().equals("scroll")) {
                    if (argv.length != 5) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "option ?arg arg ...?");
                    }

                    if (argv[4].toString().equals("units")) {
                        if (jvp != null) {
                            Point pt = jvp.getViewPosition();

                            Dimension viewSize = jvp.getViewSize();
                            Dimension listSize = swkjlist.getSize();
                            Dimension extentSize = jvp.getExtentSize();
                            fy1 = (1.0 * pt.y) / listSize.height;

                            int units = TclInteger.get(interp, argv[3]);

                            if (swkjlist.model.getSize() == 0) {
                                pt.y = 0;
                            } else {
                                double incrY = listSize.height / swkjlist.model.getSize();
                                pt.y = (int) ((fy1 * listSize.height)
                                        + (incrY * units));

                                if ((pt.y + extentSize.height) > listSize.height) {
                                    pt.y = listSize.height - extentSize.height;
                                }

                                if (pt.y < 0) {
                                    pt.y = 0;
                                }
                            }

                            jvp.setViewPosition(pt);
                        }
                    } else if (argv[4].toString().equals("pages")) {
                        if (jvp != null) {
                            Point pt = jvp.getViewPosition();

                            Dimension viewSize = jvp.getViewSize();
                            Dimension listSize = swkjlist.getSize();
                            Dimension extentSize = jvp.getExtentSize();
                            fy1 = (1.0 * pt.y) / listSize.height;

                            int units = TclInteger.get(interp, argv[3]);
                            double incrY = extentSize.height;
                            pt.y = (int) ((fy1 * listSize.height)
                                    + (incrY * units));

                            if ((pt.y + extentSize.height) > listSize.height) {
                                pt.y = listSize.height - extentSize.height;
                            }

                            if (pt.y < 0) {
                                pt.y = 0;
                            }

                            jvp.setViewPosition(pt);
                        }
                    }
                } else {
                    throw new TclException(interp,
                            "unknown option \"" + argv[2].toString()
                            + "\": must be moveto or scroll");
                }

                break;
            }

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    private class BoundingBox extends GetValueOnEventThread {

        SwkJList swkjlist;
        String item = null;
        Rectangle rect = null;
        String errMessage = null;

        Rectangle exec(final SwkJList swkjlist, final String item)
                throws TclException {
            this.swkjlist = swkjlist;
            this.item = item;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return rect;
        }

        @Override
        public void run() {
            Result result = new Result();
            int index = 0;
            swkjlist.getIndex(item, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            if ((index < 0) || (index >= swkjlist.model.getSize())) {
                return;
            }

            rect = swkjlist.getCellBounds(index, index);

            JViewport jvp = null;

            try {
                jvp = Widgets.getViewport((Component) swkjlist);
            } catch (TclException tclE) {
                return;
            }

            if (jvp != null) {
                Dimension extentSize = jvp.getExtentSize();
                Point pt = jvp.getViewPosition();
                rect.x -= pt.x;
                rect.y -= pt.y;

                if (((rect.y + rect.height) <= 0)
                        || (rect.y >= extentSize.height)) {
                    rect = null;
                }
            }
        }
    }

    private static class CurrentSelection extends GetValueOnEventThread {

        SwkJList swkjlist;
        int[] selected = null;

        int[] exec(final SwkJList swkjlist) {
            this.swkjlist = swkjlist;
            execOnThread();

            return selected;
        }

        @Override
        public void run() {
            selected = swkjlist.getSelectedIndices();
        }
    }

    private class Delete extends UpdateOnEventThread {

        SwkJList swkjlist = null;
        String firstArg = null;
        String lastArg = null;
        String errMessage = null;

        void exec(final SwkJList swkjlist, final String firstArg,
                final String lastArg) throws TclException {
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            this.swkjlist = swkjlist;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }
        }

        @Override
        public void run() {
            int first = 0;
            Result result = new Result();

            swkjlist.getIndex(firstArg, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            first = result.i;

            int last = first;

            if (lastArg != null) {
                swkjlist.getIndex(lastArg, -1, result);

                if (result.hasError()) {
                    errMessage = result.getErrorMsg();

                    return;
                }

                last = result.i;
            }

            if (last < first) {
                int hold;
                hold = last;
                last = first;
                first = hold;
            }

            for (int i = last; i >= first; i--) {
                if ((i >= 0) && (i < swkjlist.model.getSize())) {
                    swkjlist.model.removeElementAt(i);
                }
            }
        }
    }

    private class Items extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        String firstArg = null;
        String lastArg = null;
        String errMessage = null;
        ArrayList<ListItem> resultItems = new ArrayList<ListItem>();

        void exec(final SwkJList swkjlist, final String firstArg,
                final String lastArg) throws TclException {
            this.swkjlist = swkjlist;
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            interp.setResult("");

            int n = resultItems.size();

            if (n == 1) {
                interp.setResult(resultItems.get(0).toString());
            } else if (n > 1) {
                TclObject list = TclList.newInstance();

                for (int i = 0; i < n; i++) {
                    TclList.append(interp, list,
                            TclString.newInstance(resultItems.get(i).toString()));
                }

                interp.setResult(list);
            }
        }

        @Override
        public void run() {
            int first = 0;
            Result result = new Result();

            swkjlist.getIndex(firstArg, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            first = result.i;

            int last = first;

            if (lastArg != null) {
                swkjlist.getIndex(lastArg, -1, result);

                if (result.hasError()) {
                    errMessage = result.getErrorMsg();

                    return;
                }

                last = result.i;
            }

            if (first == last) {
                int i = first;

                if ((i >= 0) && (i < swkjlist.model.getSize())) {
                    Object modelObject = swkjlist.model.elementAt(i);
                    ListItem modelItem = null;
                    if (modelObject instanceof ListItem) {
                        modelItem = (ListItem) modelObject;
                    } else {
                        modelItem = new ListItem(modelObject.toString());
                    }
                    resultItems.add(modelItem);
                }
            } else {
                for (int i = first; i <= last; i++) {
                    if ((i >= 0) && (i < swkjlist.model.getSize())) {
                        Object modelObject = swkjlist.model.elementAt(i);
                        ListItem modelItem = null;
                        if (modelObject instanceof ListItem) {
                            modelItem = (ListItem) modelObject;
                        } else {
                            modelItem = new ListItem(modelObject.toString());
                        }
                        resultItems.add(modelItem);
                    }
                }
            }
        }
    }

    private static class Size extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        int size = 0;

        int exec(final SwkJList swkjlist) {
            this.swkjlist = swkjlist;
            execOnThread();

            return size;
        }

        @Override
        public void run() {
            size = swkjlist.model.getSize();
        }
    }

    private class See extends UpdateOnEventThread {

        SwkJList swkjlist = null;
        String item = null;
        String errMessage = null;

        void exec(final SwkJList swkjlist, final String item)
                throws TclException {
            this.item = item;
            this.swkjlist = swkjlist;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }
        }

        @Override
        public void run() {
            int index = 0;
            Result result = new Result();

            swkjlist.getIndex(item, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            if (index < 0) {
                index = 0;
            }

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }

            swkjlist.ensureIndexIsVisible(index);
            swkjlist.revalidate();
        }
    }

    private class Index extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        String item = null;
        int index = 0;
        String errMessage = null;

        int exec(final SwkJList swkjlist, final String item)
                throws TclException {
            this.item = item;
            this.swkjlist = swkjlist;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return index;
        }

        @Override
        public void run() {
            Result result = new Result();

            swkjlist.getIndex(item, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;
        }
    }

    private class Activate extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        String item = null;
        int index = 0;
        String errMessage = null;

        int exec(final SwkJList swkjlist, final String item)
                throws TclException {
            this.item = item;
            this.swkjlist = swkjlist;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return index;
        }

        @Override
        public void run() {

            Result result = new Result();

            swkjlist.getIndex(item, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            if (index < 0) {
                index = 0;
            }

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }

            swkjlist.active = index;
        }
    }
   private class ItemGet extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        String item = null;
        ListItem listItem = null;
        String errMessage = null;

        ListItem exec(final SwkJList swkjlist, final String item)
                throws TclException {
            this.item = item;
            this.swkjlist = swkjlist;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return listItem;
        }

        @Override
        public void run() {

            Result result = new Result();

            swkjlist.getIndex(item, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            int index = result.i;

            if (index < 0) {
                index = 0;
            }

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }
            Object listObject  = swkjlist.model.get(index);
            if (listObject instanceof ListItem) {
                 listItem = (ListItem) listObject;
            }
        }
    }

    private static class Nearest extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        int y = 0;
        int index = 0;

        int exec(final SwkJList swkjlist, final int y) {
            this.swkjlist = swkjlist;
            execOnThread();

            return index;
        }

        @Override
        public void run() {
            Point p = new Point(2, 0);
            p.y = y;

            JViewport jvp = null;

            try {
                jvp = Widgets.getViewport((Component) swkjlist);
            } catch (TclException tclE) {
                return;
            }

            if (jvp != null) {
                Dimension extentSize = jvp.getExtentSize();

                if (y > extentSize.height) {
                    y = extentSize.height - 4;
                }
            }

            index = swkjlist.locationToIndex(p);

            if (index < 0) {
                index = 0;
            }

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }
        }
    }

    private class Insert extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        int size = 0;
        String[] items = null;
        String idxString = null;
        String errMessage = null;
        boolean atEnd = false;

        int exec(final SwkJList swkjlist, String idxString,
                final String[] items, boolean atEnd) throws TclException {
            this.swkjlist = swkjlist;
            this.items = items;
            this.atEnd = atEnd;
            this.idxString = idxString;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return size;
        }

        @Override
        public void run() {
            int index = 0;

            Result result = new Result();

            swkjlist.getIndex(idxString, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            if (index < 0) {
                index = 0;
            }

            if (index > swkjlist.model.getSize()) {
                index = swkjlist.model.getSize();
            }

            for (int i = 0; i < items.length; i++) {
                if (atEnd) {
                    swkjlist.model.addElement(new ListItem(items[i]));
                } else {
                    swkjlist.model.insertElementAt(new ListItem(items[i]), index);
                    index++;
                }
            }

            size = swkjlist.model.getSize();
        }
    }

    private class Selection extends GetValueOnEventThread {

        static final int CLEAR = 0;
        static final int ANCHOR = 1;
        static final int SET = 2;
        SwkJList swkjlist = null;
        String firstArg = null;
        String lastArg = null;
        int mode = 0;
        String errMessage = null;

        void execClear(final SwkJList swkjlist, final String firstArg,
                final String lastArg) throws TclException {
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            this.swkjlist = swkjlist;
            mode = CLEAR;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }
        }

        void execSet(final SwkJList swkjlist, final String firstArg,
                final String lastArg) {
            this.firstArg = firstArg;
            this.lastArg = lastArg;
            this.swkjlist = swkjlist;
            mode = SET;
            execOnThread();
        }

        void execAnchor(final SwkJList swkjlist, final String indexArg) {
            this.firstArg = indexArg;
            this.swkjlist = swkjlist;
            mode = ANCHOR;
            execOnThread();
        }

        void anchor() {
            int index = 0;

            Result result = new Result();

            swkjlist.getIndex(firstArg, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }

            swkjlist.selectionModel.setSelectionInterval(index, index);
        }

        void clear() {
            int index = 0;
            Result result = new Result();

            swkjlist.getIndex(firstArg, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            int index2 = index;

            if (lastArg != null) {
                swkjlist.getIndex(lastArg, -1, result);

                if (result.hasError()) {
                    errMessage = result.getErrorMsg();
                    errMessage = result.getErrorMsg();

                    return;
                }

                index2 = result.i;
            }

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }

            if (index2 >= swkjlist.model.getSize()) {
                index2 = swkjlist.model.getSize() - 1;
            }

            swkjlist.selectionModel.removeSelectionInterval(index, index2);
        }

        void set() {
            int index = 0;

            Result result = new Result();

            swkjlist.getIndex(firstArg, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            int index2 = index;

            if (lastArg != null) {
                swkjlist.getIndex(lastArg, -1, result);

                if (result.hasError()) {
                    errMessage = result.getErrorMsg();

                    return;
                }

                index2 = result.i;
            }

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }

            if (index2 >= swkjlist.model.getSize()) {
                index2 = swkjlist.model.getSize() - 1;
            }

            swkjlist.selectionModel.addSelectionInterval(index, index2);
        }

        @Override
        public void run() {
            if (mode == SET) {
                set();
            } else if (mode == CLEAR) {
                clear();
            } else if (mode == ANCHOR) {
                anchor();
            }
        }
    }

    private class Includes extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        String item = null;
        boolean includes = false;
        String errMessage = null;

        boolean exec(final SwkJList swkjlist, final String item)
                throws TclException {
            this.item = item;
            this.swkjlist = swkjlist;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return includes;
        }

        @Override
        public void run() {
            int index = 0;

            Result result = new Result();

            swkjlist.getIndex(item, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            if ((index >= swkjlist.model.getSize()) || (index < 0)) {
                includes = false;
            } else {
                includes = swkjlist.selectionModel.isSelectedIndex(index);
            }
        }
    }
    private class ItemConfigure extends GetValueOnEventThread {

        SwkJList swkjlist = null;
        ListItem newItem = null;
        String item = null;
        int index = 0;
        String errMessage = null;

        int exec(final SwkJList swkjlist, final String item,final ListItem newItem)
                throws TclException {
            this.item = item;
            this.newItem = newItem;
            this.swkjlist = swkjlist;
            execOnThread();

            if (errMessage != null) {
                throw new TclException(interp, errMessage);
            }

            return index;
        }

        @Override
        public void run() {

            Result result = new Result();

            swkjlist.getIndex(item, -1, result);

            if (result.hasError()) {
                errMessage = result.getErrorMsg();

                return;
            }

            index = result.i;

            if (index < 0) {
                index = 0;
            }

            if (index >= swkjlist.model.getSize()) {
                index = swkjlist.model.getSize() - 1;
            }
            swkjlist.model.set(index,newItem);
        }
    }

    public class ListItem {
        private final String text;
        private final Color background;
        private final Color foreground;
        private final Color selectBackground;
        private final Color selectForeground;
        ListItem (final String text) {
             this(text,null,null,null,null);
        }
        ListItem (final String text, final Color background, final Color foreground, final Color selectBackground, final Color selectForeground) {
              if (text == null) {
                  this.text = "";
              } else {
                  this.text = text;
              }
              this.foreground = foreground;
              this.background = background;
              this.selectForeground = selectForeground;
              this.selectBackground = selectBackground;
        }
        public Color getBackground() {
            return background;
        }
        public Color getForeground() {
            return foreground;
        }
        public Color getSelectForeground() {
            return selectForeground;
        }
        public Color getSelectBackground() {
            return selectBackground;
        }
        public String toString() {
            return text;
        }
    }
}
