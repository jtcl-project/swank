/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;

import java.io.*;

import java.util.*;

import javax.swing.*;

public class GridCmd implements Command {

    /**
     * @param interp
     * @param argv
     * @throws TclException  */
    static final private int GRID_X = 1;
    static final private int GRID_Y = 2;
    static final private int GRID_HEIGHT = 4;
    static final private int GRID_WIDTH = 8;
    static final private int GRID_PADX = 16;
    static final private int GRID_PADY = 32;
    static final private int GRID_IPADX = 64;
    static final private int GRID_IPADY = 128;
    static final private int GRID_ANCHOR = 256;
    static final private String[] validCmds = {
        "bbox", "columnconfigure", "configure", "forget", "info", "location",
        "propagate", "remove", "rowconfigure", "size", "slaves"
    };
    static final private int OPT_BBOX = 0;
    static final private int OPT_COLUMNCONFIGURE = 1;
    static final private int OPT_CONFIGURE = 2;
    static final private int OPT_FORGET = 3;
    static final private int OPT_INFO = 4;
    static final private int OPT_LOCATION = 5;
    static final private int OPT_PROPAGATE = 6;
    static final private int OPT_REMOVE = 7;
    static final private int OPT_ROWCONFIGURE = 8;
    static final private int OPT_SIZE = 9;
    static final private int OPT_SLAVES = 10;
    Interp interp = null;

    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        int i;
        String masterName = null;
        Container master = null;
        GridBagConstraints gconstr = null;
        SwkGridBagLayout gbag = null;
        Object widgetObj = null;
        TclObject tObj = null;
        Component component1 = null;
        this.interp = interp;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option arg ?arg ...?");
        }

        if (argv[1].toString().charAt(0) == '.') {
            configure(interp, argv, 1);

            return;
        } else {
            String thisArg = argv[1].toString();

            if (thisArg.equals("x") || thisArg.equals("-")
                    || thisArg.equals("^")) {
                configure(interp, argv, 1);

                return;
            }
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        switch (opt) {
            case OPT_BBOX:
                getBoundingBox(interp, argv);

                break;

            case OPT_CONFIGURE:
                configure(interp, argv, 2);

                break;

            case OPT_COLUMNCONFIGURE:
                configureRowColumn(interp, argv, true);

                break;

            case OPT_FORGET:
                gridForget(interp, argv, 2);

                break;

            case OPT_LOCATION:
                getLocation(interp, argv);

                break;

            case OPT_SLAVES:
                getSlaves(interp, argv);

                break;

            case OPT_ROWCONFIGURE:
                configureRowColumn(interp, argv, false);

                break;

            case OPT_INFO:
                getInfo(interp, argv);

                break;

            case OPT_SIZE:
                getSizes(interp, argv);

                return;

            case OPT_PROPAGATE:
                propagate(interp, argv);

                break;

            case OPT_REMOVE:
                break;
        }
    }

    SwkGridBagLayout getLayout(Container master) {
        LayoutManager layout = master.getLayout();

        if (layout != null) {
            if (layout instanceof SwkGridBagLayout) {
                return (SwkGridBagLayout) layout;
            }
        }

        return null;
    }

    Container getMaster(String windowName, boolean useParent) {
        String masterName = null;
        Container master = null;
        LayoutManager layout = null;
        GridBagConstraints gconstr = null;
        SwkGridBagLayout gbag = null;
        Object widgetObj = null;
        Component component1 = null;

        TclObject tObj = (TclObject) Widgets.getWidget(interp, windowName);

        if (tObj == null) {
            return null;
        }

        try {
            widgetObj = (Object) ReflectObject.get(interp, tObj);
        } catch (TclException tclE) {
            return null;
        }

        component1 = (Component) widgetObj;

        Component component = null;

        if (useParent) {
            component = ((Component) component1).getParent();
        } else {
            component = component1;
        }

        if (component == null) {
            component = component1;
        }

        if (component instanceof JFrame) {
            master = ((JFrame) component).getContentPane();

            if (master == null) {
                System.out.println("mnull");
            }
        } else if (component instanceof JWindow) {
            master = ((JWindow) component).getContentPane();

            if (master == null) {
                System.out.println("mnull");
            }
        } else if (component instanceof JInternalFrame) {
            master = ((JInternalFrame) component).getContentPane();
        } else {
            master = (Container) component;
        }

        return master;
    }

    private void configure(Interp interp, TclObject[] argv, int start)
            throws TclException {
        int lastSlave = 0;
        String masterName = null;
        int nSlaves = 0;
        boolean lastArgSlave = false;
        boolean endOfWindowArgs = false;

        for (int i = start; i < argv.length; i++) {
            String thisArg = argv[i].toString();

            if (thisArg.startsWith("-") && (thisArg.length() > 1)) {
                if (thisArg.equals("-in") && (argv.length > (i + 1))) {
                    masterName = argv[i + 1].toString();

                    break;
                }

                endOfWindowArgs = true;
                lastArgSlave = false;
            }

            if (endOfWindowArgs) {
                continue;
            }

            if ((thisArg.length() > 0) && (thisArg.charAt(0) == '.')) {
                if (masterName == null) {
                    int lastDot = thisArg.lastIndexOf(".");

                    if (lastDot == 0) {
                        masterName = ".";
                    } else {
                        masterName = thisArg.substring(0, lastDot);
                    }
                }

                nSlaves++;
                lastArgSlave = true;
                lastSlave = i;
            } else if (thisArg.equals("-")) {
                if (!lastArgSlave) {
                    throw new TclException(interp,
                            "Must specify window before shortcut '-'.");
                }

                lastArgSlave = true;
                lastSlave = i;
            } else if (!thisArg.equals("x") && !thisArg.equals("^")) {
                if (nSlaves > 0) {
                    throw new TclException(interp,
                            "unexpected parameter, \"" + thisArg
                            + "\", in configure list. Should be window name or option");
                } else if (argv.length == 3) {
                    throw new TclException(interp,
                            "bad argument \"" + thisArg
                            + "\": must be name of window");
                } else {
                    throw new TclException(interp,
                            "invalid window shortcut, \"" + thisArg
                            + "\" should be '-', 'x', or '^'");
                }
            } else {
                lastArgSlave = false;
                lastSlave = i;
            }
        }

        if (nSlaves == 0) {
            throw new TclException(interp, "can't determine master window");
        }

        int nArgs = argv.length - lastSlave - 1;

        if ((nArgs % 2) != 0) {
            throw new TclException(interp,
                    "extra option or option with no value");
        }

        GridBagConstraints gconstr = new GridBagConstraints();

        gconstr.gridy = -1;
        gconstr.gridx = 0;
        gconstr.weightx = 0.0;
        gconstr.weighty = 0.0;
        gconstr.fill = GridBagConstraints.NONE;

        int modOptions = 0;
        String[] windows = new String[lastSlave - start + 1];

        for (int i = start; i <= lastSlave; i++) {
            windows[i - start] = argv[i].toString().intern();

            if (windows[i - start].equals(masterName)) {
                throw new TclException(interp,
                        "Window can't be managed in itself");
            }
        }

        for (int i = (lastSlave + 1); i < argv.length; i += 2) {
            modOptions |= parseGridOptions(interp, argv, i, gconstr, null);
        }

        TclObject tObj = (TclObject) Widgets.getWidget(interp, masterName);

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + masterName + "\"");
        }

        Object masterObject = ReflectObject.get(interp, tObj);
        (new Configure()).exec(masterName, masterObject, gconstr, windows,
                modOptions);
    }

    String doit(String masterName, Object masterObject,
            GridBagConstraints gconstr, String[] windowNames,
            JComponent[] jcomponents, int modOptions)
            throws IllegalArgumentException {
        Container master = getMasterContainer(masterObject);

        //    SwkGridBagLayout gbag = getLayout(master);
        SwkGridBagLayout gbag = null;

        //   Component component = (Component) master;
        LayoutManager layout = master.getLayout();

        if (layout == null) {
            gbag = new SwkGridBagLayout();
            master.setLayout(gbag);
        } else {
            if (layout instanceof SwkGridBagLayout) {
                gbag = (SwkGridBagLayout) layout;
            } else {
                gbag = new SwkGridBagLayout();
                master.setLayout(gbag);
            }
        }

        if (master.getComponentCount() == 0) {
            gbag.lastRow = 0;
        }

        if (gconstr.gridy == -1) {
            gconstr.gridy = gbag.lastRow;
        }

        TclObject tObj2 = null;
        JComponent slave = null;
        JComponent packSlave;

        for (int i = 0; i < jcomponents.length; i++) {
            String thisArg = windowNames[i];

            if (thisArg.equals("^")) {
                extendRow(master, gbag, gconstr.gridx, gconstr.gridy);
                gconstr.gridx++;

                continue;
            } else if (thisArg.equals("-")) {
                if (slave != null) {
                    GridBagConstraints gconstr2 = gbag.getConstraints(slave);
                    gconstr2.gridwidth++;
                    gbag.setConstraints(slave, gconstr2);
                }

                gconstr.gridx++;

                continue;
            } else if (thisArg.equals("x")) {
                gconstr.gridx++;

                continue;
            }

            try {
                slave = gridSlave(interp, master, masterName, gbag, gconstr,
                        modOptions, jcomponents[i], windowNames[i]);
            } catch (IllegalArgumentException iaE) {
                return iaE.getMessage();
            }

            gconstr.gridx++;
        }

        LayoutHandler.addLayoutRequest(interp, master);
        gbag.lastRow = gconstr.gridy + 1;

        return null;
    }

    void configureRowColumn(Interp interp, TclObject[] argv, boolean columnMode)
            throws TclException {
        int nArgs = argv.length - 4;

        if ((((nArgs % 2) != 0) && (nArgs > 2)) || (nArgs < 0)) {
            throw new TclNumArgsException(interp, 2, argv,
                    "master index ?-option value...?");
        }

        String masterName = argv[2].toString();

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + masterName + "\"");
        }

        if (argv.length == 5) {
            int index = TclInteger.get(interp, argv[3]);

            if (index < 0) {
                throw new TclException(interp, "index must be positive");
            }

            String key = "" + index;
            GridRowColumnProps rcProps = (new RowColumnGet()).exec(masterName,
                    columnMode, index);
            String thisArg = argv[4].toString();

            if (thisArg.equals("-minsize")) {
                interp.setResult(rcProps.minSize);
            } else if (thisArg.equals("-pad")) {
                interp.setResult(rcProps.pad);
            } else if (thisArg.equals("-uniform")) {
                interp.setResult(rcProps.uniform);
            } else if (thisArg.equals("-weight")) {
                interp.setResult(rcProps.weight);
            } else {
                throw new TclException(interp,
                        "bad option \"" + thisArg
                        + "\": must be -minsize, -pad, -uniform, or -weight");
            }
        } else if (argv.length == 4) {
            int index = TclInteger.get(interp, argv[3]);

            if (index < 0) {
                throw new TclException(interp, "index must be positive");
            }

            String key = "" + index;
            GridRowColumnProps rcProps = (new RowColumnGet()).exec(argv[2].toString(),
                    columnMode, index);
            TclObject list = TclList.newInstance();
            TclList.append(interp, list, TclString.newInstance("-minsize"));
            TclList.append(interp, list, TclInteger.newInstance(rcProps.minSize));
            TclList.append(interp, list, TclString.newInstance("-pad"));
            TclList.append(interp, list, TclInteger.newInstance(rcProps.pad));
            TclList.append(interp, list, TclString.newInstance("-uniform"));
            TclList.append(interp, list, TclString.newInstance(rcProps.uniform));
            TclList.append(interp, list, TclString.newInstance("-weight"));
            TclList.append(interp, list, TclInteger.newInstance(rcProps.weight));
            interp.setResult(list);
        } else {
            TclObject[] indexObjects = null;
            indexObjects = TclList.getElements(interp, argv[3]);

            for (int j = 0; j < indexObjects.length; j++) {
                int index = TclInteger.get(interp, indexObjects[j]);

                if (index < 0) {
                    if (columnMode) {
                        throw new TclException(interp,
                                "grid columnconfigure: \"" + indexObjects[j]
                                + "\" is out of range");
                    } else {
                        throw new TclException(interp,
                                "grid rowconfigure: \"" + indexObjects[j]
                                + "\" is out of range");
                    }
                }

                int[] argTypes = new int[(argv.length - 4) / 2];
                Vector argValues = new Vector();
                int k = 0;

                for (int i = 4; i < argv.length; i += 2) {
                    if (argv[i].toString().equals("-weight")) {
                        int value = TclInteger.get(interp, argv[i + 1]);

                        if (value < 0) {
                            throw new TclException(interp,
                                    "invalid arg \"-weight\": should be non-negative");
                        }

                        argTypes[k++] = RowColumnConfigure.WEIGHT;
                        argValues.add(new Integer(value));
                    } else if (argv[i].toString().equals("-minsize")) {
                        // FIXME null component for getScreenDistance (won't get proper resolution?)
                        int value = getScreenDistance(interp, null, argv[i],
                                argv[i + 1]);
                        argTypes[k++] = RowColumnConfigure.MINSIZE;
                        argValues.add(new Integer(value));
                    } else if (argv[i].toString().equals("-pad")) {
                        int value = getScreenDistance(interp, null, argv[i],
                                argv[i + 1]);
                        argTypes[k++] = RowColumnConfigure.PAD;
                        argValues.add(new Integer(value));
                    } else if (argv[i].toString().equals("-uniform")) {
                        argTypes[k++] = RowColumnConfigure.UNIFORM;
                        argValues.add(argv[i + 1].toString().intern());
                    }
                }

                (new RowColumnConfigure()).exec(masterName, columnMode, index,
                        argTypes, argValues);
            }
        }
    }

    int getScreenDistance(Interp interp, Component component,
            TclObject argName, TclObject arg) throws TclException {
        int value = 0;

        try {
            value = SwankUtil.getTkSize(interp, component, arg);
        } catch (TclException tclE) {
            throw new TclException(interp, "bad screen distance \"" + arg
                    + "\"");
        }

        if (value < 0) {
            throw new TclException(interp,
                    "invalid arg \"" + argName.toString()
                    + "\": should be non-negative");
        }

        return value;
    }

    void getBoundingBox(Interp interp, TclObject[] argv)
            throws TclException {
        if ((argv.length != 3) && (argv.length != 5) && (argv.length != 7)) {
            throw new TclNumArgsException(interp, 2, argv,
                    "master ?column row ?column row??");
        }

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        TclObject list = TclList.newInstance();

        if (argv.length == 3) {
            Rectangle rect = (new BoundingBox()).exec(argv[2].toString(),
                    argv.length, null);

            if (rect == null) {
                return;
            }

            TclList.append(interp, list, TclInteger.newInstance(rect.x));
            TclList.append(interp, list, TclInteger.newInstance(rect.y));
            TclList.append(interp, list, TclInteger.newInstance(rect.width));
            TclList.append(interp, list, TclInteger.newInstance(rect.height));
        } else {
            if (!Widgets.exists(interp, argv[2].toString())) {
                throw new TclException(interp,
                        "bad window path name \"" + argv[2].toString() + "\"");
            }

            int[][] rcVals = new int[2][2];
            rcVals[0][0] = TclInteger.get(interp, argv[3]);
            rcVals[0][1] = TclInteger.get(interp, argv[4]);
            rcVals[1][0] = rcVals[0][0];
            rcVals[1][1] = rcVals[0][1];

            Rectangle rect = (new BoundingBox()).exec(argv[2].toString(),
                    argv.length, rcVals);

            if (rect == null) {
                return;
            }

            if (argv.length == 7) {
                rcVals[1][0] = TclInteger.get(interp, argv[5]);
                rcVals[1][1] = TclInteger.get(interp, argv[6]);

                if (rcVals[1][0] < rcVals[0][0]) {
                    int hold = rcVals[0][0];
                    rcVals[0][0] = rcVals[1][0];
                    rcVals[1][0] = hold;
                }

                if (rcVals[1][1] < rcVals[0][1]) {
                    int hold = rcVals[0][1];
                    rcVals[0][1] = rcVals[1][1];
                    rcVals[1][1] = hold;
                }
            }

            TclList.append(interp, list, TclInteger.newInstance(rect.x));
            TclList.append(interp, list, TclInteger.newInstance(rect.y));
            TclList.append(interp, list, TclInteger.newInstance(rect.width));
            TclList.append(interp, list, TclInteger.newInstance(rect.height));
        }

        interp.setResult(list);
    }

    void gridForget(Interp interp, TclObject[] argv, int firstWindow)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        String[] names = new String[argv.length - firstWindow];
        int j = 0;

        for (int i = firstWindow; i < argv.length; i++) {
            names[j++] = argv[i].toString();
        }

        (new Forget()).exec(names);
    }

    void getLocation(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 5) {
            throw new TclNumArgsException(interp, 2, argv, "master x y");
        }

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        int x = 0;
        int y = 0;

        try {
            // FIXME  won't get size correctly on multiple screens?
            x = SwankUtil.getTkSize(interp, null, argv[3]);
        } catch (TclException tclE) {
            throw new TclException(interp,
                    "bad screen distance \"" + argv[3].toString() + "\"");
        }

        try {
            y = SwankUtil.getTkSize(interp, null, argv[4]);
        } catch (TclException tclE) {
            throw new TclException(interp,
                    "bad screen distance \"" + argv[4].toString() + "\"");
        }

        Point pt = (new Location()).exec(argv[2].toString(), x, y);
        TclObject list = TclList.newInstance();

        TclList.append(interp, list, TclInteger.newInstance(pt.x));
        TclList.append(interp, list, TclInteger.newInstance(pt.y));
        interp.setResult(list);

        return;
    }

    void getSlaves(Interp interp, TclObject[] argv) throws TclException {
        if ((argv.length != 3) && (argv.length != 5)) {
            throw new TclNumArgsException(interp, 2, argv,
                    "window ?-option value...?");
        }

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        int iRow = -1;
        int iColumn = -1;

        if (argv.length == 5) {
            if (argv[3].toString().equals("-row")) {
                iRow = TclInteger.get(interp, argv[4]);

                if (iRow < 0) {
                    throw new TclException(interp,
                            argv[3] + " is an invalid value: should NOT be < 0");
                }
            } else if (argv[3].toString().equals("-column")) {
                iColumn = TclInteger.get(interp, argv[4]);

                if (iColumn < 0) {
                    throw new TclException(interp,
                            argv[3] + " is an invalid value: should NOT be < 0");
                }
            } else {
                throw new TclException(interp,
                        "bad option \"" + argv[3].toString()
                        + "\": must be -column or -row");
            }
        }

        String[] names = (new Slaves()).exec(argv[2].toString(), iColumn, iRow);
        TclObject list = TclList.newInstance();

        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                TclList.append(interp, list, TclString.newInstance(names[i]));
            }
        }

        interp.setResult(list);

        return;
    }

    void getInfo(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        interp.resetResult();

        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[2].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        Object widgetObj = (Object) ReflectObject.get(interp, tObj);

        Component component = (Component) widgetObj;

        Object constrObject = (new Info()).exec(argv[2].toString(), component);

        if ((constrObject == null)
                || !(constrObject instanceof GridBagConstraints)) {
            return;
        }

        GridBagConstraints gconstr = (GridBagConstraints) constrObject;
        TclObject list = TclList.newInstance();
        TclList.append(interp, list, TclString.newInstance("-in"));
        TclList.append(interp, list,
                TclString.newInstance(Widgets.parent(interp, argv[2].toString())));
        TclList.append(interp, list, TclString.newInstance("-column"));
        TclList.append(interp, list, TclInteger.newInstance(gconstr.gridx));
        TclList.append(interp, list, TclString.newInstance("-row"));
        TclList.append(interp, list, TclInteger.newInstance(gconstr.gridy));
        TclList.append(interp, list, TclString.newInstance("-columnspan"));
        TclList.append(interp, list, TclInteger.newInstance(gconstr.gridwidth));
        TclList.append(interp, list, TclString.newInstance("-rowspan"));
        TclList.append(interp, list, TclInteger.newInstance(gconstr.gridheight));
        TclList.append(interp, list, TclString.newInstance("-ipadx"));
        TclList.append(interp, list, TclInteger.newInstance(gconstr.ipadx / 2));
        TclList.append(interp, list, TclString.newInstance("-ipady"));
        TclList.append(interp, list, TclInteger.newInstance(gconstr.ipady / 2));
        TclList.append(interp, list, TclString.newInstance("-padx"));
        TclList.append(interp, list,
                TclInteger.newInstance(
                (gconstr.insets.left + gconstr.insets.right) / 2));
        TclList.append(interp, list, TclString.newInstance("-pady"));
        TclList.append(interp, list,
                TclInteger.newInstance(
                (gconstr.insets.top + gconstr.insets.bottom) / 2));

        TclList.append(interp, list, TclString.newInstance("-sticky"));

        StringBuffer sbuf = new StringBuffer();
        String anchorStr = null;

        if (gconstr.fill == GridBagConstraints.BOTH) {
            anchorStr = "nesw";
        } else if (gconstr.fill == GridBagConstraints.NONE) {
            if (gconstr.anchor == GridBagConstraints.NORTHEAST) {
                anchorStr = "ne";
            } else if (gconstr.anchor == GridBagConstraints.SOUTHEAST) {
                anchorStr = "es";
            } else if (gconstr.anchor == GridBagConstraints.NORTHWEST) {
                anchorStr = "nw";
            } else if (gconstr.anchor == GridBagConstraints.SOUTHWEST) {
                anchorStr = "sw";
            } else if (gconstr.anchor == GridBagConstraints.WEST) {
                anchorStr = "w";
            } else if (gconstr.anchor == GridBagConstraints.NORTH) {
                anchorStr = "n";
            } else if (gconstr.anchor == GridBagConstraints.EAST) {
                anchorStr = "e";
            } else if (gconstr.anchor == GridBagConstraints.SOUTH) {
                anchorStr = "s";
            }
        } else if (gconstr.fill == GridBagConstraints.HORIZONTAL) {
            anchorStr = "ew";

            if (gconstr.anchor == GridBagConstraints.NORTH) {
                anchorStr = "new";
            } else if (gconstr.anchor == GridBagConstraints.SOUTH) {
                anchorStr = "esw";
            }
        } else if (gconstr.fill == GridBagConstraints.VERTICAL) {
            anchorStr = "ns";

            if (gconstr.anchor == GridBagConstraints.EAST) {
                anchorStr = "nes";
            } else if (gconstr.anchor == GridBagConstraints.WEST) {
                anchorStr = "nsw";
            }
        }

        TclList.append(interp, list, TclString.newInstance(anchorStr));

        interp.setResult(list);
    }

    void getSizes(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        Dimension dim = (new Sizes()).exec(argv[2].toString());

        if (dim == null) {
            throw new TclException(interp, "grid sizes exception");
        }

        TclObject list = TclList.newInstance();
        TclList.append(interp, list, TclInteger.newInstance(dim.width));
        TclList.append(interp, list, TclInteger.newInstance(dim.height));
        interp.setResult(list);
    }

    void propagate(Interp interp, TclObject[] argv) throws TclException {
        if ((argv.length != 3) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "window ?boolean?");
        }

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        boolean propagate = false;
        boolean setPropagate = false;

        if (argv.length == 4) {
            setPropagate = true;
            propagate = TclBoolean.get(interp, argv[3]);
        }

        boolean result = (new Propagate()).exec(argv[2].toString(), propagate,
                setPropagate);
        interp.setResult(result);

        return;
    }

    void extendRow(Container master, SwkGridBagLayout gbag, int x, int y)
            throws IllegalArgumentException {
        Component[] comps = master.getComponents();
        boolean foundComp = false;

        for (int i = 0; i < comps.length; i++) {
            Object constr = gbag.getConstraints(comps[i]);

            if ((constr != null) && (constr instanceof GridBagConstraints)) {
                GridBagConstraints gconstr = (GridBagConstraints) constr;

                if ((gconstr.gridy < y)
                        && ((gconstr.gridy + gconstr.gridheight) > y)
                        && (gconstr.gridx < x)
                        && ((gconstr.gridx + gconstr.gridwidth) > x)) {
                    return;
                }

                if (((gconstr.gridy + gconstr.gridheight) == y)
                        && (gconstr.gridx == x)) {
                    foundComp = true;
                    gconstr.gridheight = (y - gconstr.gridy + 1);
                    gbag.setConstraints(comps[i], gconstr);

                    break;
                }
            }
        }

        if (!foundComp) {
            throw new IllegalArgumentException(
                    "can't find slave to extend with \"^\".");
        }
    }

    int parseGridOptions(Interp interp, TclObject[] argv, int i,
            GridBagConstraints gconstr, Component component)
            throws TclException {
        String thisArg = argv[i].toString();
        int optionSet = 0;

        if (thisArg.equals("-gridx")) {
            int iValue = TclInteger.get(interp, argv[i + 1]);

            if (iValue < 0) {
                throw new TclException(interp,
                        "bad grid value \"" + argv[i + 1].toString()
                        + "\": must be a non-negative integer");
            }

            gconstr.gridx = iValue;
            optionSet = GRID_X;
        } else if (thisArg.equals("-gridy")) {
            int iValue = TclInteger.get(interp, argv[i + 1]);

            if (iValue < 0) {
                throw new TclException(interp,
                        "bad grid value \"" + argv[i + 1].toString()
                        + "\": must be a non-negative integer");
            }

            optionSet = GRID_Y;

            gconstr.gridy = iValue;
        } else if (thisArg.equals("-row")) {
            int iValue = TclInteger.get(interp, argv[i + 1]);

            if (iValue < 0) {
                throw new TclException(interp,
                        "bad grid value \"" + argv[i + 1].toString()
                        + "\": must be a non-negative integer");
            }

            optionSet = GRID_Y;

            gconstr.gridy = iValue;
        } else if (thisArg.equals("-column")) {
            int iValue = TclInteger.get(interp, argv[i + 1]);

            if (iValue < 0) {
                throw new TclException(interp,
                        "bad column value \"" + argv[i + 1].toString()
                        + "\": must be a non-negative integer");
            }

            optionSet = GRID_X;

            gconstr.gridx = iValue;
        } else if (thisArg.equals("-rowspan")) {
            int iValue = TclInteger.get(interp, argv[i + 1]);

            if (iValue < 1) {
                throw new TclException(interp,
                        "bad rowspan value \"" + argv[i + 1].toString()
                        + "\": must be a positive integer");
            }

            optionSet = GRID_HEIGHT;

            gconstr.gridheight = iValue;
        } else if (thisArg.equals("-columnspan")) {
            int iValue = TclInteger.get(interp, argv[i + 1]);

            if (iValue < 1) {
                throw new TclException(interp,
                        "bad columnspan value \"" + argv[i + 1].toString()
                        + "\": must be a positive integer");
            }

            optionSet = GRID_WIDTH;

            gconstr.gridwidth = iValue;
        } else if (thisArg.equals("-ipadx")) {
            int iValue = 0;

            try {
                iValue = SwankUtil.getTkSize(interp, component, argv[i + 1]);
            } catch (TclException tclE) {
                throw new TclException(interp,
                        "bad ipadx value \"" + argv[i + 1].toString()
                        + "\": must be positive screen distance");
            }

            if (iValue < 0) {
                throw new TclException(interp,
                        "bad ipadx value \"" + argv[i + 1].toString()
                        + "\": must be positive screen distance");
            }

            // fixme ipad seems to need to be doubled
            optionSet = GRID_IPADX;

            gconstr.ipadx = iValue * 2;
        } else if (thisArg.equals("-ipady")) {
            int iValue = 0;

            try {
                iValue = SwankUtil.getTkSize(interp, component, argv[i + 1]);
            } catch (TclException tclE) {
                throw new TclException(interp,
                        "bad ipady value \"" + argv[i + 1].toString()
                        + "\": must be positive screen distance");
            }

            if (iValue < 0) {
                throw new TclException(interp,
                        "bad ipady value \"" + argv[i + 1].toString()
                        + "\": must be positive screen distance");
            }

            // fixme ipad seems to need to be doubled
            optionSet = GRID_IPADY;
            gconstr.ipady = iValue * 2;
        } else if (thisArg.equals("-padx") || thisArg.equals("-pady")) {
            double dValue = 0.0;
            TclObject[] padArgs = TclList.getElements(interp, argv[i + 1]);

            if ((padArgs.length < 1) || (padArgs.length > 2)) {
                throw new TclException(interp,
                        "bad pad value \"" + argv[i + 1].toString()
                        + "\": must be positive screen distance");
            }

            for (int iArg = 0; iArg < padArgs.length; iArg++) {
                String[] argErrors = {"", " 2nd"};

                try {
                    dValue = SwankUtil.getTkSizeD(interp, component,
                            padArgs[iArg]);
                } catch (TclException tclE) {
                    throw new TclException(interp,
                            "bad" + argErrors[iArg] + " pad value \""
                            + padArgs[iArg].toString()
                            + "\": must be positive screen distance");
                }

                if (dValue < 0) {
                    throw new TclException(interp,
                            "bad" + argErrors[iArg] + " pad value \""
                            + padArgs[iArg].toString()
                            + "\": must be positive screen distance");
                }

                if (thisArg.equals("-padx")) {
                    if (iArg == 0) {
                        gconstr.insets.left = (int) dValue;
                        gconstr.insets.right = (int) dValue;
                    } else {
                        gconstr.insets.right = (int) dValue;
                    }

                    optionSet = GRID_PADX;
                } else {
                    if (iArg == 0) {
                        gconstr.insets.top = (int) dValue;
                        gconstr.insets.bottom = (int) dValue;
                    } else {
                        gconstr.insets.bottom = (int) dValue;
                    }

                    optionSet = GRID_PADY;
                }
            }
        } else if (thisArg.equals("-sticky")) {
            String stickyStuff = argv[i + 1].toString();

            for (int j = 0; j < stickyStuff.length(); j++) {
                if ("nsew, ".indexOf(stickyStuff.charAt(j)) == -1) {
                    throw new TclException(interp,
                            "bad stickyness value \"" + stickyStuff
                            + "\": must be a string containing n, e, s, and/or w");
                }
            }

            // regexp {^[nsew ,]*$} nsewq
            if ((stickyStuff.indexOf('w') >= 0)
                    && (stickyStuff.indexOf('e') >= 0)) {
                if ((stickyStuff.indexOf('n') >= 0)
                        && (stickyStuff.indexOf('s') >= 0)) {
                    gconstr.fill = GridBagConstraints.BOTH;
                } else {
                    gconstr.fill = GridBagConstraints.HORIZONTAL;

                    if (stickyStuff.indexOf('s') >= 0) {
                        gconstr.anchor = GridBagConstraints.SOUTH;
                    } else if (stickyStuff.indexOf('n') >= 0) {
                        gconstr.anchor = GridBagConstraints.NORTH;
                    }
                }
            } else if ((stickyStuff.indexOf('n') >= 0)
                    && (stickyStuff.indexOf('s') >= 0)) {
                gconstr.fill = GridBagConstraints.VERTICAL;

                if (stickyStuff.indexOf('e') >= 0) {
                    gconstr.anchor = GridBagConstraints.EAST;
                } else if (stickyStuff.indexOf('w') >= 0) {
                    gconstr.anchor = GridBagConstraints.WEST;
                }
            } else {
                gconstr.fill = GridBagConstraints.NONE;

                if (stickyStuff.indexOf('n') >= 0) {
                    if (stickyStuff.indexOf('e') >= 0) {
                        gconstr.anchor = GridBagConstraints.NORTHEAST;
                    } else if (stickyStuff.indexOf('w') >= 0) {
                        gconstr.anchor = GridBagConstraints.NORTHWEST;
                    } else {
                        gconstr.anchor = GridBagConstraints.NORTH;
                    }
                } else if (stickyStuff.indexOf('s') >= 0) {
                    if (stickyStuff.indexOf('e') >= 0) {
                        gconstr.anchor = GridBagConstraints.SOUTHEAST;
                    } else if (stickyStuff.indexOf('w') >= 0) {
                        gconstr.anchor = GridBagConstraints.SOUTHWEST;
                    } else {
                        gconstr.anchor = GridBagConstraints.SOUTH;
                    }
                } else if (stickyStuff.indexOf('e') >= 0) {
                    gconstr.anchor = GridBagConstraints.EAST;
                } else if (stickyStuff.indexOf('w') >= 0) {
                    gconstr.anchor = GridBagConstraints.WEST;
                } else {
                    gconstr.anchor = GridBagConstraints.CENTER;
                }
            }

            optionSet = GRID_ANCHOR;
        } else if (thisArg.equals("-in")) {
        } else {
            throw new TclException(interp,
                    "unexpected parameter, \"" + thisArg
                    + "\", in configure list. Should be window name or option");
        }

        return optionSet;
    }

    JComponent gridSlave(Interp interp, Container master, String masterName,
            SwkGridBagLayout gbag, GridBagConstraints gconstr, int modOptions,
            JComponent slave, String windowName) throws IllegalArgumentException {
        GridBagConstraints currentConstraints = gbag.getConstraints(slave);
        GridBagConstraints applyConstraints = null;

        if (currentConstraints.gridx != -1) {
            applyConstraints = updateConstraints(gconstr, currentConstraints,
                    modOptions);
        } else {
            applyConstraints = gconstr;
        }

        slave.invalidate();

        String column = "" + applyConstraints.gridx;
        GridRowColumnProps columnProps = gbag.getColumnProps(column,
                applyConstraints.gridx);
        applyConstraints.weightx = columnProps.weight;

        //gbag.setColumnMinSize(gconstr.gridx,columnProps.minSize);
        String row = "" + applyConstraints.gridy;
        GridRowColumnProps rowProps = gbag.getRowProps(row,
                applyConstraints.gridy);
        applyConstraints.weighty = rowProps.weight;

        //gbag.setRowMinSize(gconstr.gridy,rowProps.minSize);
        gbag.setConstraints(slave, applyConstraints);

        if (!((Container) master).isAncestorOf(slave)) {
            try {
                master.add(slave);
            } catch (java.lang.IllegalArgumentException iaE) {
                throw new IllegalArgumentException("can't put \"" + windowName
                        + "\" inside \"" + masterName + "\"");
            }
        }

        return slave;
    }

    Container getMasterContainer(Object masterObj) {
        Container master = null;

        if (masterObj instanceof JFrame) {
            master = ((JFrame) masterObj).getContentPane();
        } else if (masterObj instanceof JWindow) {
            master = ((JWindow) masterObj).getContentPane();
        } else if (masterObj instanceof JInternalFrame) {
            master = ((JInternalFrame) masterObj).getContentPane();
        } else {
            master = (Container) masterObj;
        }

        return master;
    }

    GridBagConstraints updateConstraints(GridBagConstraints gconstr,
            GridBagConstraints currentConstraints, int modOptions) {
        if (currentConstraints != null) {
            if ((modOptions & GRID_X) == GRID_X) {
                currentConstraints.gridx = gconstr.gridx;
            }

            if ((modOptions & GRID_Y) == GRID_Y) {
                currentConstraints.gridy = gconstr.gridy;
            }

            if ((modOptions & GRID_HEIGHT) == GRID_HEIGHT) {
                currentConstraints.gridheight = gconstr.gridheight;
            }

            if ((modOptions & GRID_WIDTH) == GRID_WIDTH) {
                currentConstraints.gridwidth = gconstr.gridwidth;
            }

            if ((modOptions & GRID_PADX) == GRID_PADX) {
                currentConstraints.insets.left = gconstr.insets.left;
                currentConstraints.insets.right = gconstr.insets.right;
            }

            if ((modOptions & GRID_PADY) == GRID_PADY) {
                currentConstraints.insets.top = gconstr.insets.top;
                currentConstraints.insets.bottom = gconstr.insets.bottom;
            }

            if ((modOptions & GRID_IPADX) == GRID_IPADX) {
                currentConstraints.ipadx = gconstr.ipadx;
            }

            if ((modOptions & GRID_IPADY) == GRID_IPADY) {
                currentConstraints.ipady = gconstr.ipady;
            }

            if ((modOptions & GRID_ANCHOR) == GRID_ANCHOR) {
                currentConstraints.anchor = gconstr.anchor;
                currentConstraints.fill = gconstr.fill;
            }
        }

        return currentConstraints;
    }

    class Configure extends GetValueOnEventThread {

        String masterName = null;
        GridBagConstraints gconstr = null;
        Object masterObject = null;
        String[] windowNames = null;
        JComponent[] jcomponents = null;
        int modOptions = 0;
        String errMsg = null;

        void exec(final String masterName, final Object masterObject,
                final GridBagConstraints gconstr, final String[] windowNames,
                final int modOptions) throws TclException {
            this.masterName = masterName;
            this.masterObject = masterObject;
            this.gconstr = gconstr;
            this.windowNames = windowNames;
            this.modOptions = modOptions;
            jcomponents = new JComponent[windowNames.length];

            for (int i = 0; i < windowNames.length; i++) {
                String thisArg = windowNames[i];

                if (thisArg.equals("^") || thisArg.equals("-")
                        || thisArg.equals("x")) {
                    continue;
                }

                TclObject tObj2 = (TclObject) Widgets.getWidget(interp, thisArg);

                if (tObj2 == null) {
                    throw new TclException(interp,
                            "bad window path name \"" + thisArg + "\"");
                }

                Object compObject = ReflectObject.get(interp, tObj2);

                if (compObject instanceof JComponent) {
                    jcomponents[i] = (JComponent) compObject;
                } else {
                    throw new TclException(interp,
                            "can't manage \"" + thisArg
                            + "\": it's a top-level window");
                }
            }

            execOnThread();

            if (errMsg != null) {
                throw new TclException(interp, errMsg);
            }
        }

        public void run() {
            errMsg = doit(masterName, masterObject, gconstr, windowNames,
                    jcomponents, modOptions);
        }
    }

    class RowColumnGet extends GetValueOnEventThread {

        String item = null;
        String[] names = null;
        int index = 0;
        boolean columnMode = false;
        GridRowColumnProps rcProps = null;

        GridRowColumnProps exec(final String item, final boolean columnMode,
                final int index) {
            this.item = item;
            this.columnMode = columnMode;
            this.index = index;
            execOnThread();

            return rcProps;
        }

        public void run() {
            Container master = getMaster(item, false);
            SwkGridBagLayout gbag = getLayout(master);

            if (gbag == null) {
                gbag = new SwkGridBagLayout();
                master.setLayout(gbag);
            }

            String key = "" + index;
            rcProps = gbag.getProps(columnMode, key, index);
        }
    }

    class RowColumnConfigure extends UpdateOnEventThread {

        static final int WEIGHT = 1;
        static final int MINSIZE = 2;
        static final int PAD = 3;
        static final int UNIFORM = 4;
        String item = null;
        int index = 0;
        boolean columnMode = false;
        int[] argTypes = null;
        Vector argValues = null;

        void exec(final String item, final boolean columnMode, final int index,
                final int[] argTypes, final Vector argValues) {
            this.item = item;
            this.columnMode = columnMode;
            this.index = index;
            this.argTypes = argTypes;
            this.argValues = argValues;
            execOnThread();
        }

        public void run() {
            Container master = getMaster(item, false);
            SwkGridBagLayout gbag = getLayout(master);

            if (gbag == null) {
                gbag = new SwkGridBagLayout();
                master.setLayout(gbag);
            }

            String key = "" + index;
            GridRowColumnProps rcProps = gbag.getProps(columnMode, key, index);

            if (rcProps == null) {
                System.out.println("rcProps null");
            }

            GridRowColumnProps newProps = (GridRowColumnProps) rcProps.clone();

            for (int i = 0; i < argTypes.length; i++) {
                switch (argTypes[i]) {
                    case WEIGHT:
                        newProps.weight = ((Integer) argValues.elementAt(i)).intValue();
                        gbag.setWeight(columnMode, index, newProps.weight);

                        break;

                    case MINSIZE:
                        newProps.minSize = ((Integer) argValues.elementAt(i)).intValue();
                        gbag.setMinSize(columnMode, index, newProps.minSize);

                        break;

                    case PAD:
                        newProps.pad = ((Integer) argValues.elementAt(i)).intValue();

                        break;

                    case UNIFORM:
                        newProps.uniform = (String) argValues.elementAt(i);

                        break;

                    default:
                        System.out.println("invalid argType " + argTypes[i]);

                    // FIXME  should throw error
                }
            }

            if (columnMode) {
                gbag.putColumnProps(key, newProps);
            } else {
                gbag.putRowProps(key, newProps);
            }
        }
    }

    class BoundingBox extends GetValueOnEventThread {

        String item = null;
        Component component = null;
        Object constrObject = null;
        int nArgs = 0;
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        int[][] rcVals = null;

        Rectangle exec(final String item, final int nArgs, final int[][] rcVals) {
            this.item = item;
            this.component = component;
            this.rcVals = rcVals;
            this.nArgs = 0;
            execOnThread();

            return rect;
        }

        public void run() {
            Container master = getMaster(item, true);
            SwkGridBagLayout gbag = getLayout(master);

            if (master == null) {
                return;
            }

            if (nArgs > 3) {
                Component[] comps = master.getComponents();
                boolean outOfRange = true;

                for (int i = 0; (comps != null) && (i < comps.length); i++) {
                    Object constr = gbag.getConstraints(comps[i]);

                    if ((constr != null)
                            && (constr instanceof GridBagConstraints)) {
                        GridBagConstraints gconstr = (GridBagConstraints) constr;

                        if (nArgs == 5) {
                            if ((gconstr.gridx == rcVals[0][0])
                                    && (gconstr.gridy == rcVals[0][1])) {
                                rect = comps[i].getBounds();

                                break;
                            }
                        } else {
                            if ((gconstr.gridx >= rcVals[0][0])
                                    && (gconstr.gridy >= rcVals[0][1])) {
                                outOfRange = false;

                                if ((gconstr.gridx <= rcVals[1][0])
                                        && (gconstr.gridy <= rcVals[1][1])) {
                                    rect = (Rectangle) rect.createUnion(comps[i].getBounds());
                                }
                            }
                        }
                    }
                }

                if ((nArgs == 7) && outOfRange) {
                    rect.x = master.getBounds().width;
                    rect.y = master.getBounds().height;
                    rect.width = 0;
                    rect.height = 0;
                }
            } else {
                Component[] comps = master.getComponents();

                for (int i = 0; (comps != null) && (i < comps.length); i++) {
                    if (i == 0) {
                        rect.setBounds(comps[i].getBounds());
                    } else {
                        rect = (Rectangle) rect.createUnion(comps[i].getBounds());
                    }
                }
            }
        }
    }

    class Forget extends UpdateOnEventThread {

        String[] names = null;

        void exec(String[] names) {
            this.names = names;
            execOnThread();
        }

        public void run() {
            try {
                for (int i = 0; i < names.length; i++) {
                    if (!Widgets.exists(interp, names[i])) {
                        continue;
                    }

                    String parentName = Widgets.parent(interp, names[i]);

                    if (parentName.equals("")) {
                        continue;
                    }

                    Container parent = Widgets.getContainer(interp, parentName);
                    LayoutManager layoutManager = parent.getLayout();

                    if (!(layoutManager instanceof SwkGridBagLayout)) {
                        continue;
                    }

                    SwkWidget window = (SwkWidget) Widgets.get(interp, names[i]);
                    parent.remove((Component) window);
                    Widgets.relayoutContainer(parent);
                    parent.repaint();
                }
            } catch (TclException tclE) {
                //FIXME
            }
        }
    }

    class Location extends GetValueOnEventThread {

        String item = null;
        String[] names = null;
        int x = -1;
        int y = -1;
        Point pt = new Point();

        Point exec(final String item, final int x, final int y) {
            this.item = item;
            this.x = x;
            this.y = y;
            execOnThread();

            return pt;
        }

        public void run() {
            Container parent = null;

            try {
                parent = Widgets.getContainer(interp, item);
            } catch (TclException tclE) {
                //FIXME
                interp.backgroundError();

                return;
            }

            LayoutManager layoutManager = parent.getLayout();
            Dimension dim = parent.getSize();

            if (!(layoutManager instanceof SwkGridBagLayout)) {
                pt.x = -1;
                pt.y = -1;

                return;
            }

            Rectangle rect = parent.getBounds();

            if (x >= dim.width) {
                x -= 1;
            }

            if (y >= dim.height) {
                y -= 1;
            }

            Point pt = ((SwkGridBagLayout) layoutManager).location(x, y);

            if (x < pt.x) {
                pt.x = -1;
            }

            if (y < pt.y) {
                pt.y = -1;
            }
        }
    }

    class Slaves extends GetValueOnEventThread {

        String item = null;
        String[] names = null;
        int iColumn = -1;
        int iRow = -1;

        String[] exec(final String item, final int iColumn, final int iRow) {
            this.item = item;
            this.iColumn = iColumn;
            this.iRow = iRow;
            execOnThread();

            return names;
        }

        public void run() {
            Container parent = null;
            Container master = null;
            SwkGridBagLayout gbag = null;

            try {
                parent = Widgets.getContainer(interp, item);
                master = getMaster(item, true);

                if (master == null) {
                    return;
                }

                gbag = getLayout(master);
            } catch (TclException tclE) {
                //FIXME
                interp.backgroundError();

                return;
            }

            if (gbag == null) {
                return;
            }

            int nMembers = parent.getComponentCount();
            names = new String[nMembers];

            for (int i = (nMembers - 1); i >= 0; i--) {
                Component comp = parent.getComponent(i);

                if ((iRow >= 0) || (iColumn >= 0)) {
                    if (gbag == null) {
                        System.out.println("null gbag");
                        interp.resetResult();

                        return;
                    }

                    Object constr = gbag.getConstraints(comp);

                    if ((constr != null)
                            && (constr instanceof GridBagConstraints)) {
                        GridBagConstraints gconstr = (GridBagConstraints) constr;

                        if ((iRow >= 0) && (gconstr.gridy == iRow)) {
                            names[i] = comp.getName();
                        } else if ((iColumn >= 0)
                                && (gconstr.gridx == iColumn)) {
                            names[i] = comp.getName();
                        }
                    }
                } else {
                    names[i] = comp.getName();
                }
            }
        }
    }

    class Info extends GetValueOnEventThread {

        String item = null;
        Component component = null;
        Object constrObject = null;

        Object exec(final String item, final Component component) {
            this.item = item;
            this.component = component;
            execOnThread();

            return constrObject;
        }

        public void run() {
            Container master = getMaster(item, true);
            SwkGridBagLayout gbag = getLayout(master);

            if (gbag == null) {
                return;
            }

            constrObject = gbag.getConstraints(component);
        }
    }

    class Sizes extends GetValueOnEventThread {

        String item = null;
        Component component = null;
        Object constrObject = null;
        Dimension dim = null;

        Dimension exec(final String item) {
            this.item = item;
            this.component = component;
            execOnThread();

            return dim;
        }

        public void run() {
            Container master = getMaster(item, true);
            SwkGridBagLayout gbag = getLayout(master);
            int nX = 0;
            int nY = 0;
            Component[] comps = master.getComponents();

            for (int i = 0; (comps != null) && (i < comps.length); i++) {
                Object constr = gbag.getConstraints(comps[i]);

                if ((constr != null) && (constr instanceof GridBagConstraints)) {
                    GridBagConstraints gconstr = (GridBagConstraints) constr;

                    if (gconstr.gridx >= nX) {
                        nX = gconstr.gridx + 1;
                    }

                    if (gconstr.gridy >= nY) {
                        nY = gconstr.gridy + 1;
                    }
                }
            }

            if (gbag != null) {
                int colMax = gbag.getRCMax(true);

                if (colMax >= nX) {
                    nX = colMax + 1;
                }

                int rowMax = gbag.getRCMax(false);

                if (rowMax >= nY) {
                    nY = rowMax + 1;
                }
            }

            dim = new Dimension(nX, nY);
        }
    }

    class Propagate extends GetValueOnEventThread {

        String item = null;
        boolean propagate = false;
        boolean setPropagate = false;

        boolean exec(final String item, final boolean propagate,
                final boolean setPropagate) {
            this.item = item;
            this.propagate = propagate;
            this.setPropagate = setPropagate;
            execOnThread();

            return this.propagate;
        }

        public void run() {
            Container parent = null;

            try {
                parent = Widgets.getContainer(interp, item);
            } catch (TclException tclE) {
                interp.backgroundError();
                System.out.println("error " + tclE.getMessage());

                //FIXME
            }

            LayoutManager layoutManager = parent.getLayout();
            SwkGridBagLayout gbag = null;

            if (!(layoutManager instanceof SwkGridBagLayout)) {
                parent.removeAll();
                gbag = new SwkGridBagLayout();
                parent.setLayout(gbag);
            } else {
                gbag = (SwkGridBagLayout) layoutManager;
            }

            if (setPropagate) {
                gbag.propagate = propagate;
                Widgets.relayoutContainer(parent);
            }

            propagate = gbag.propagate;
        }
    }
}
