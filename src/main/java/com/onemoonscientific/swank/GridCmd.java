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
    //  Interp interp = null;

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
        // this.interp = interp;

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

    static SwkGridBagLayout getLayout(Container master) {
        LayoutManager layout = master.getLayout();

        if (layout != null) {
            if (layout instanceof SwkGridBagLayout) {
                return (SwkGridBagLayout) layout;
            }
        }

        return null;
    }
    /*
    TclObject tObj = (TclObject) Widgets.getWidget(interp, windowName);

    if (tObj == null) {
    return null;
    }

    try {
    widgetObj = (Object) ReflectObject.get(interp, tObj);
    } catch (TclException tclE) {
    return null;
    }
     */

     private void configure(Interp interp, TclObject[] argv, int start)
            throws TclException {
        int lastSlave = 0;
        String masterName = null;
        int nSlaves = 0;
        boolean lastArgSlave = false;
        boolean endOfWindowArgs = false;
        boolean gotExtender = false;
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
                if (thisArg.length() == 1) {
                    throw new TclException(interp,
                            "invalid window shortcut, \"" + thisArg
                            + "\" should be '-', 'x', or '^'");

                } else {
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
                }
            } else {
                if (thisArg.equals("^")) {
                    gotExtender = true;
                }
                lastArgSlave = false;
                lastSlave = i;
            }
        }
        if (gotExtender && (nSlaves == 0)) {
            throw new TclException(interp, "can't use '^', cant find master");
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
        String[] windowNames = new String[lastSlave - start + 1];
        JComponent[] compObjects = new JComponent[windowNames.length];
        for (int i = start; i <= lastSlave; i++) {
            String thisArg = argv[i].toString();
            windowNames[i - start] = thisArg;
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
                compObjects[i - start] = (JComponent) compObject;
            } else {
                throw new TclException(interp,
                        "can't manage \"" + thisArg
                        + "\": it's a top-level window");
            }
            if (thisArg.equals(masterName)) {
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



        String errMsg = (new Configure()).exec(masterName, compObjects, windowNames, masterObject, gconstr, modOptions);
        if (errMsg != null) {
            throw new TclException(interp, errMsg);
        }
        Container master = Widgets.getMasterContainer(masterObject);
        LayoutHandler.addLayoutRequest(interp, master);

    }
//           errMsg = doit( masterObject, gconstr, jcomponents, modOptions);

    private String doit(final String masterName, Object masterObject,
            GridBagConstraints gconstr,
            JComponent[] jComps, final String[] windowNames, int modOptions)
            throws IllegalArgumentException {
        Container master = Widgets.getMasterContainer(masterObject);

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
            //gbag.lastRow = 0;
            master.setMinimumSize(new Dimension(0, 0));
            if (masterObject instanceof SwkJFrame) {
                ((SwkJFrame) masterObject).geometry = new Dimension(1, 1);
            }
            //  xxx cheap trick to reset gbag when frame empty
            boolean propagate = gbag.propagate;
            int[] widths = gbag.columnWidths;
            int[] heights = gbag.rowHeights;
            double[] columnWeights = gbag.columnWeights;
            double[] rowWeights = gbag.rowWeights;

            gbag = new SwkGridBagLayout();
            gbag.propagate = propagate;
            gbag.columnWeights = columnWeights;
            gbag.rowWeights = rowWeights;
            gbag.columnWidths = widths;
            gbag.rowHeights = heights;
            master.setLayout(gbag);
        }

        TclObject tObj2 = null;
        JComponent slave = null;
        JComponent packSlave;

        for (int i = 0; i < jComps.length; i++) {
            if (jComps[i] == null) {
                String thisArg = (String) windowNames[i];


                if (thisArg.equals("^")) {
                    try {
                        if (gconstr.gridy == -1) {
                            gconstr.gridy = gbag.lastRow;
                        }
                        extendRow(master, gbag, gconstr.gridx, gconstr.gridy);
                    } catch (IllegalArgumentException iaE) {
                        return iaE.getMessage();
                    }
                    gconstr.gridx++;

                    continue;
                } else if (thisArg.equals("-")) {
                    if (slave != null) {
                        GridBagConstraints gconstr2 = gbag.getConstraints(slave);
                        gconstr2.gridwidth++;
                        gbag.setConstraints(slave, gconstr2);
                        gconstr.gridx = gconstr2.gridx + gconstr2.gridwidth;
                    } else {
                        gconstr.gridx++;
                    }


                    continue;
                } else if (thisArg.equals("x")) {
                    gconstr.gridx++;

                    continue;
                }
            } else {
                try {
                    slave = gridSlave(master, masterName, gbag, gconstr,
                            modOptions, jComps[i], windowNames[i]);
                } catch (IllegalArgumentException iaE) {
                    return iaE.getMessage();
                }

                gconstr.gridx++;
            }
        }

        gbag.lastRow = gconstr.gridy + 1;

        return null;
    }

    private void configureRowColumn(Interp interp, TclObject[] argv, boolean columnMode)
            throws TclException {
        int nArgs = argv.length - 4;

        if ((((nArgs % 2) != 0) && (nArgs > 2)) || (nArgs < 0)) {
            throw new TclNumArgsException(interp, 2, argv,
                    "master index ?-option value...?");
        }

        Component component = Widgets.getComponent(interp, argv[2].toString());

        if (argv.length == 5) {
            int index = TclInteger.get(interp, argv[3]);

            if (index < 0) {
                throw new TclException(interp, "index must be positive");
            }

            String key = "" + index;
            GridRowColumnProps rcProps = (new RowColumnGet()).exec(component,
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
            GridRowColumnProps rcProps = (new RowColumnGet()).exec(component,
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
                        argValues.add(Integer.valueOf(value));
                    } else if (argv[i].toString().equals("-minsize")) {
                        // FIXME null component for getScreenDistance (won't get proper resolution?)
                        int value = getScreenDistance(interp, null, argv[i],
                                argv[i + 1]);
                        argTypes[k++] = RowColumnConfigure.MINSIZE;
                        argValues.add(Integer.valueOf(value));
                    } else if (argv[i].toString().equals("-pad")) {
                        int value = getScreenDistance(interp, null, argv[i],
                                argv[i + 1]);
                        argTypes[k++] = RowColumnConfigure.PAD;
                        argValues.add(Integer.valueOf(value));
                    } else if (argv[i].toString().equals("-uniform")) {
                        argTypes[k++] = RowColumnConfigure.UNIFORM;
                        argValues.add(argv[i + 1].toString().intern());
                    }
                }

                (new RowColumnConfigure()).exec(component, columnMode, index,
                        argTypes, argValues);
            }
        }
    }

    private int getScreenDistance(Interp interp, Component component,
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

    private void getBoundingBox(Interp interp, TclObject[] argv)
            throws TclException {
        if ((argv.length != 3) && (argv.length != 5) && (argv.length != 7)) {
            throw new TclNumArgsException(interp, 2, argv,
                    "master ?column row ?column row??");
        }

        Component component = Widgets.getComponent(interp, argv[2].toString());

        TclObject list = TclList.newInstance();

        if (argv.length == 3) {
            Rectangle rect = (new BoundingBox()).exec(component,
                    argv.length, null);

            if (rect == null) {
                return;
            }

            TclList.append(interp, list, TclInteger.newInstance(rect.x));
            TclList.append(interp, list, TclInteger.newInstance(rect.y));
            TclList.append(interp, list, TclInteger.newInstance(rect.width));
            TclList.append(interp, list, TclInteger.newInstance(rect.height));
        } else {

            int[][] rcVals = new int[2][2];
            rcVals[0][0] = TclInteger.get(interp, argv[3]);
            rcVals[0][1] = TclInteger.get(interp, argv[4]);
            rcVals[1][0] = rcVals[0][0];
            rcVals[1][1] = rcVals[0][1];


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
            Rectangle rect = (new BoundingBox()).exec(component,
                    argv.length, rcVals);

            if (rect == null) {
                return;
            }

            TclList.append(interp, list, TclInteger.newInstance(rect.x));
            TclList.append(interp, list, TclInteger.newInstance(rect.y));
            TclList.append(interp, list, TclInteger.newInstance(rect.width));
            TclList.append(interp, list, TclInteger.newInstance(rect.height));
        }

        interp.setResult(list);
    }

    private void gridForget(Interp interp, TclObject[] argv, int firstWindow)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        String[] names = new String[argv.length - firstWindow];
        int j = 0;

        for (int i = firstWindow; i < argv.length; i++) {
            names[j++] = argv[i].toString();
        }
        Component[] comps = new Component[names.length];
        for (int i = 0; i < names.length; i++) {
            comps[i] = Widgets.getComponent(interp, names[i]);
        }

        (new Forget()).exec(comps);
    }

    private void getLocation(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 5) {
            throw new TclNumArgsException(interp, 2, argv, "master x y");
        }

        if (!Widgets.exists(interp, argv[2].toString())) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }
        Component component = Widgets.getComponent(interp, argv[2].toString());

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

        Point pt = (new Location()).exec(component, x, y);
        TclObject list = TclList.newInstance();

        TclList.append(interp, list, TclInteger.newInstance(pt.x));
        TclList.append(interp, list, TclInteger.newInstance(pt.y));
        interp.setResult(list);

        return;
    }

    private void getSlaves(Interp interp, TclObject[] argv) throws TclException {
        if ((argv.length != 3) && (argv.length != 5)) {
            throw new TclNumArgsException(interp, 2, argv,
                    "window ?-option value...?");
        }

        Component component = Widgets.getComponent(interp, argv[2].toString());

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



        //Object constrObject = (new Info()).exec(argv[2].toString(), component);



        ArrayList<String> names = (new Slaves()).exec(component, iColumn, iRow);
        TclObject list = TclList.newInstance();

        for (String name : names) {
            TclList.append(interp, list, TclString.newInstance(name));
        }



        interp.setResult(list);
        return;

    }


    private void getInfo(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");


        }
        Component component = Widgets.getComponent(interp, argv[2].toString());
        Object constrObject = (new Info()).exec(component);



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

    private void getSizes(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");


        }

        Component component = Widgets.getComponent(interp, argv[2].toString());

        Dimension dim = (new Sizes()).exec(component);



        if (dim == null) {
            throw new TclException(interp, "grid sizes exception");


        }

        TclObject list = TclList.newInstance();
        TclList.append(interp, list, TclInteger.newInstance(dim.width));
        TclList.append(interp, list, TclInteger.newInstance(dim.height));
        interp.setResult(list);


    }

    private void propagate(Interp interp, TclObject[] argv) throws TclException {
        if ((argv.length != 3) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "window ?boolean?");


        }

        Component component = Widgets.getComponent(interp, argv[2].toString());



        boolean propagate = false;


        boolean setPropagate = false;



        if (argv.length == 4) {
            setPropagate = true;
            propagate = TclBoolean.get(interp, argv[3]);


        }

        boolean result = (new Propagate()).exec(component, propagate,
                setPropagate);
        interp.setResult(result);



        return;


    }

    private void extendRow(Container master, SwkGridBagLayout gbag, int x, int y)
            throws IllegalArgumentException {
        Component[] comps = master.getComponents();


        boolean foundComp = false;


        for (int i = 0; i
                < comps.length; i++) {
            Object constr = gbag.getConstraints(comps[i]);


            if (constr != null) {
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

            for (int iArg = 0; iArg
                    < padArgs.length; iArg++) {
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



            for (int j = 0; j
                    < stickyStuff.length(); j++) {
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

    JComponent gridSlave(Container master, String masterName,
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
        if (applyConstraints.gridy == -1) {
            applyConstraints.gridy = gbag.lastRow;
        }
        if (gconstr.gridy == -1) {
            if (currentConstraints == null) {
                gconstr.gridy = gbag.lastRow;
            } else {
                gconstr.gridy = currentConstraints.gridy;
            }
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

    private class Configure extends GetValueOnEventThread {

        GridBagConstraints gconstr = null;
        Object masterObject = null;
        JComponent[] jComps = null;
        int modOptions = 0;
        String errMsg = null;
        String masterName;
        String[] windowNames;

        String exec(final String masterName, final JComponent[] jComps, final String[] windowNames, final Object masterObject,
                final GridBagConstraints gconstr,
                final int modOptions) throws TclException {
            this.masterName = masterName;
            this.jComps = jComps;
            this.windowNames = windowNames;
            this.masterObject = masterObject;
            this.gconstr = gconstr;
            this.modOptions = modOptions;

            execOnThread();
            return errMsg;
        }

        @Override
        public void run() {
            errMsg = doit(masterName, masterObject, gconstr, jComps, windowNames, modOptions);
        }
    }

    private static class RowColumnGet extends GetValueOnEventThread {

        Component component = null;
        int index = 0;
        boolean columnMode = false;
        GridRowColumnProps rcProps = null;

        GridRowColumnProps exec(final Component component, final boolean columnMode,
                final int index) {
            this.component = component;
            this.columnMode = columnMode;
            this.index = index;
            execOnThread();

            return rcProps;
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, false);
            SwkGridBagLayout gbag = getLayout(master);

            if (gbag == null) {
                gbag = new SwkGridBagLayout();
                master.setLayout(gbag);
            }

            String key = "" + index;
            rcProps = gbag.getProps(columnMode, key, index);
        }
    }

    private static class RowColumnConfigure extends UpdateOnEventThread {

        static final int WEIGHT = 1;
        static final int MINSIZE = 2;
        static final int PAD = 3;
        static final int UNIFORM = 4;
        Component component = null;
        int index = 0;
        boolean columnMode = false;
        int[] argTypes = null;
        Vector argValues = null;

        void exec(final Component component, final boolean columnMode, final int index,
                final int[] argTypes, final Vector argValues) {
            this.component = component;
            this.columnMode = columnMode;
            this.index = index;
            this.argTypes = argTypes;
            this.argValues = argValues;
            execOnThread();
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, false);
            SwkGridBagLayout gbag = getLayout(master);

            if (gbag == null) {
                gbag = new SwkGridBagLayout();
                master.setLayout(gbag);
            }

            String key = "" + index;
            GridRowColumnProps rcProps = gbag.getProps(columnMode, key, index);

            if (rcProps == null) {
                System.out.println("rcProps null");
                return;
            }
            final GridRowColumnProps newProps;
            try {
                newProps = (GridRowColumnProps) rcProps.clone();
            } catch (CloneNotSupportedException cnsE) {
                return;
            }

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

    private static class BoundingBox extends GetValueOnEventThread {

        Component component = null;
        int nArgs = 0;
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        int[][] rcVals = null;

        Rectangle exec(final Component component, final int nArgs, final int[][] rcVals) {
            this.component = component;
            this.rcVals = rcVals;
            this.nArgs = nArgs;
            execOnThread();

            return rect;
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, true);
            SwkGridBagLayout gbag = getLayout(master);

            if (master == null) {
                return;
            }
            if (nArgs > 3) {
                Component[] comps = master.getComponents();
                boolean outOfRange = true;
                for (int i = 0; (comps != null) && (i < comps.length); i++) {
                    Object constr = gbag.getConstraints(comps[i]);

                    if (constr != null) {
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

    private static class Forget extends UpdateOnEventThread {

        Component[] comps = null;

        void exec(Component[] comps) {
            this.comps = comps;
            execOnThread();
        }

        @Override
        public void run() {

            for (int i = 0; i < comps.length; i++) {
                if (comps[i] != null) {
                    Container master = Widgets.getMaster(comps[i], true);

                    Container parent = Widgets.getMasterContainer(master);
                    LayoutManager layoutManager = parent.getLayout();
                    if (!(layoutManager instanceof SwkGridBagLayout)) {
                        continue;
                    }

                    parent.remove(comps[i]);
                    Widgets.relayoutContainer(parent);
                    parent.repaint();
                }
            }
        }
    }

    private static class Location extends GetValueOnEventThread {

        Component component = null;
        int x = -1;
        int y = -1;
        Point pt = new Point();

        Point exec(final Component component, final int x, final int y) {
            this.component = component;
            this.x = x;
            this.y = y;
            execOnThread();

            return pt;
        }

        Point getLocation(SwkGridBagLayout gbag) {
            Point loc = new Point();
            int[][] dim = gbag.getLayoutDimensions();
            int[] minWidth = dim[0];
            int[] minHeight = dim[1];
            int width = minWidth.length;
            int height = minHeight.length;
            int i = 0;
            int d = 0;
            Point start = gbag.getLayoutOrigin();

            //  System.out.println("sx " + start.x+ " " + start.y + " " + width + " " + height + " "+ minWidth[0] + " " + minHeight[0]);
            // System.out.println(gbag.minimumLayoutSize(null));
            d = start.x;

            for (i = 0; i < width; i++) {
                // System.out.println(minWidth[i]);
                d += minWidth[i];
                if (d > x) {
                    break;
                }
            }
            loc.x = i;

            d = start.y;
            for (i = 0; i < height; i++) {
                // System.out.println(minHeight[i]);
                d += minHeight[i];
                if (d > y) {
                    break;
                }
            }
            loc.y = i;
            return loc;
        }

        @Override
        public void run() {
            Container parent = Widgets.getMasterContainer(component);
            LayoutManager layoutManager = parent.getLayout();
            Dimension dim = parent.getSize();

            if (!(layoutManager instanceof SwkGridBagLayout)) {
                pt.x = -1;
                pt.y = -1;

                return;
            }
            SwkGridBagLayout gbag = getLayout(parent);

            if (x >= dim.width) {
                x -= 1;
            }

            if (y >= dim.height) {
                y -= 1;
            }

            //pt = gbag.location(x, y);
            pt = getLocation(gbag);
            //          System.out.println(x + " " + y + " " + pt.x + " " + pt.y);
            if (x < pt.x) {
                pt.x = -1;
            }

            if (y < pt.y) {
                pt.y = -1;
            }
            int colMax = gbag.getRCMax(true);
            if (pt.x > (colMax + 1)) {
                pt.x = colMax + 1;
            }

            int rowMax = gbag.getRCMax(false);

            if (pt.y > (rowMax + 1)) {
                pt.y = rowMax + 1;
            }


            //        System.out.println(x + " " + y + " " + pt.x + " " + pt.y);
        }
    }

    private static class Slaves extends GetValueOnEventThread {

        Component component = null;
        ArrayList<String> names = new ArrayList<String>();
        int iColumn = -1;
        int iRow = -1;

        ArrayList<String> exec(final Component component, final int iColumn, final int iRow) {
            this.component = component;
            this.iColumn = iColumn;
            this.iRow = iRow;
            execOnThread();

            return names;
        }

        @Override
        public void run() {
            Container parent = Widgets.getMasterContainer(component);
            SwkGridBagLayout gbag = getLayout(parent);


            if (gbag == null) {
                System.out.println("null gb");
                return;
            }

            int nMembers = parent.getComponentCount();

            for (int i = 0; i < nMembers; i++) {
                Component comp = parent.getComponent(nMembers - i - 1);

                if ((iRow >= 0) || (iColumn >= 0)) {

                    Object constr = gbag.getConstraints(comp);

                    if (constr != null) {
                        GridBagConstraints gconstr = (GridBagConstraints) constr;

                        if ((iRow >= 0) && (gconstr.gridy == iRow)) {
                            names.add(comp.getName());
                        } else if ((iColumn >= 0)
                                && (gconstr.gridx == iColumn)) {
                            names.add(comp.getName());
                        }
                    }
                } else {
                    names.add(comp.getName());
                }
            }
        }
    }

    private static class Info extends GetValueOnEventThread {

        Component component = null;
        Object constrObject = null;

        Object exec(final Component component) {
            this.component = component;
            execOnThread();

            return constrObject;
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, true);
            SwkGridBagLayout gbag = getLayout(master);

            if (gbag == null) {
                return;
            }
            constrObject = gbag.getConstraints(component);
        }
    }

    private static class Sizes extends GetValueOnEventThread {

        Component component = null;
        Dimension dim = null;

        Dimension exec(final Component component) {
            this.component = component;
            execOnThread();

            return dim;
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, true);
            SwkGridBagLayout gbag = getLayout(master);
            int nX = 0;
            int nY = 0;
            Component[] comps = master.getComponents();

            for (int i = 0; (comps != null) && (i < comps.length); i++) {
                Object constr = gbag.getConstraints(comps[i]);

                if (constr != null) {
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

    private static class Propagate extends GetValueOnEventThread {

        Component component = null;
        boolean propagate = false;
        boolean setPropagate = false;

        boolean exec(final Component component, final boolean propagate,
                final boolean setPropagate) {
            this.component = component;
            this.propagate = propagate;
            this.setPropagate = setPropagate;
            execOnThread();

            return this.propagate;
        }

        @Override
        public void run() {
            Container parent = Widgets.getMasterContainer(component);
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
                if (propagate) {
                    if (component instanceof SwkJPanel) {
                        ((SwkJPanel) component).swkheight = 0;
                        ((SwkJPanel) component).swkwidth = 0;

                    }
                }
                Widgets.relayoutContainer(parent);
            }

            propagate = gbag.propagate;
        }
    }
}
