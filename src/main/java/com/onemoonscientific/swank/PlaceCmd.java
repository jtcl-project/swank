/*

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
import java.awt.*;
import java.util.*;
import javax.swing.*;

public class PlaceCmd implements Command {

    static Hashtable columnTable = new Hashtable();
    static Hashtable rowTable = new Hashtable();
    static final private String[] validCmds = {
        "configure", "forget", "info", "slaves"
    };
    static final private int OPT_CONFIGURE = 0;
    static final private int OPT_FORGET = 1;
    static final private int OPT_INFO = 2;
    static final private int OPT_SLAVES = 3;
    static TclObject configArg = TclString.newInstance("configure");
    Interp interp;

    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option|pathName args");
        }

        this.interp = interp;

        int firstWindow = 2;
        boolean debug = false;
        int nSlaves = 0;
        String option = null;
        String masterName = null;
        String parentName = null;
        String windowName = null;
        LayoutManager layoutManager;
        PlacerLayout placer = null;
        Container parent = null;

        TclObject optionArg = null;

        if (!argv[1].toString().startsWith(".")
                && !argv[1].toString().startsWith("-")) {
            optionArg = argv[1];

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option|pathName args");
            }
        } else {
            optionArg = configArg;
            firstWindow = 1;
        }

        if (debug) {
            System.out.println("Place option is " + optionArg.toString());
        }

        final int opt = TclIndex.get(interp, optionArg, validCmds, "option", 0);

        switch (opt) {
            case OPT_CONFIGURE:
                placeConfigure(interp, argv, firstWindow);

                break;

            case OPT_FORGET:
                placeForget(interp, argv, firstWindow);

                break;

            case OPT_INFO:
                placeInfo(interp, argv);

                break;

            case OPT_SLAVES:
                placeSlaves(interp, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void placeInfo(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclException(interp,
                    "wrong # args: should be \"place info pathName\"");
        }
        Component comp = Widgets.getComponent(interp, argv[2].toString());

        ArrayList<String> settings = (new Info()).exec(comp);

        if (settings.size() < 3) {
            throw new TclException(interp,
                    "window \"" + argv[2].toString() + "\" isn't placed");
        }
        TclObject result = TclList.newInstance();
        for (String value : settings) {
            TclList.append(interp, result, TclString.newInstance(value));
        }
        interp.setResult(result);

        return;
    }

    void placeSlaves(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "pathName");
        }

        Component component = Widgets.getComponent(interp, argv[2].toString());

        String[] names = (new Slaves()).exec(component);
        TclObject list = TclList.newInstance();

        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                TclList.append(interp, list, TclString.newInstance(names[i]));
            }
        }

        interp.setResult(list);

        return;
    }

    void placeForget(Interp interp, TclObject[] argv, int firstWindow)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "pathName");
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

    Component[] initPlaceingWindow(Interp interp, TclObject[] argv, String[] args,
            int firstWindow) throws TclException {
        int lastWindow = 0;

        if (args.length <= firstWindow) {
            throw new TclNumArgsException(interp, 1, argv,
                    "slave ?slave ...? ?options?");
        }

        if (!Widgets.exists(interp, args[firstWindow])) {
            if (firstWindow == 1) {
                throw new TclException(interp,
                        "bad window path name \"" + args[firstWindow] + "\"");
            } else {
                throw new TclException(interp,
                        "bad argument \"" + args[firstWindow]
                        + "\": must be name of window");
            }
        }
        ArrayList<String> names = new ArrayList<String>();
        for (int i = firstWindow; i < args.length; i++) {
            if (!args[i].startsWith(".")) {
                break;
            } else {
                SwkWidget window = (SwkWidget) Widgets.get(interp, args[i]);

                if (window == null) {
                    throw new TclException(interp,
                            "bad window path name \"" + args[i] + "\"");
                }
                if ((window instanceof JWindow) || (window instanceof JFrame)) {
                    throw new TclException(interp,
                            "can't Place \"" + args[i]
                            + "\": it's a top-level window");
                }
                names.add(args[i].toString());
                lastWindow = i;
            }
        }
        Component[] components = new Component[names.size()];
        int i = 0;
        for (String name : names) {
            components[i++] = Widgets.getComponent(interp, name);
        }

        if (lastWindow < firstWindow) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        return components;
    }

    Container getSpecialArgs(Interp interp, String[] args, int lastWindow,
            StringBuffer strippedArgs)
            throws TclException {
        int firstArg = lastWindow + 1;
        int lastArg = args.length - 1;
        Container parent = null;
        for (int i = firstArg; i <= lastArg; i += 2) {
            /*
            if (argv[i + 1].toString ().startsWith ("-") && !Character.isDigit(argv[i+1].toString().charAt(1))) {
            throw new TclException (interp, "argument without value");
            }
             */
            if (args[i].equals("-in")) {
                if (!Widgets.exists(interp, args[i + 1].toString())) {
                    throw new TclException(interp,
                            "bad window path name \"" + args[i + 1] + "\"");
                }
                parent = Widgets.getContainer(interp, args[i + 1].toString());
            } else {
                if (strippedArgs.length() != 0) {
                    strippedArgs.append(" ");
                }

                strippedArgs.append(args[i] + " {" + args[i + 1] + "}");
            }
        }
        return parent;
    }

    void placeConfigure(Interp interp, TclObject[] argv, int firstWindow)
            throws TclException {
        String[] args = new String[argv.length];

        for (int i = 0; i < argv.length; i++) {
            args[i] = argv[i].toString().intern();
        }

        Component[] comps = initPlaceingWindow(interp, argv, args, firstWindow);
        int lastWindow = firstWindow + comps.length - 1;
        int firstArg = lastWindow + 1;
        int lastArg = argv.length - 1;
        int nArgs = lastArg - firstArg + 1;

        if ((nArgs % 2) != 0) {
            throw new TclException(interp,
                    "extra option \"" + argv[lastArg].toString()
                    + "\" (option with no value?)");
        }

        StringBuffer strippedArgs = new StringBuffer();
        Container parent = getSpecialArgs(interp, args, lastWindow, strippedArgs);
        for (Component window : comps) {
            if (window == parent) {
                throw new TclException(interp,
                        "can't place \"" + window.getName() + "\" relative to itself");
            }
            if (parent == null) {
                String parentName = getParent(interp, window.getName());
                parent = Widgets.getContainer(interp, parentName);
            }

        }
        PlacerLayout.checkPlaceArgs(interp, strippedArgs.toString(), null);
        (new Configure()).exec(parent, comps, strippedArgs.toString());
    }

    PlacerLayout getLayout(Container master) {
        LayoutManager layout = master.getLayout();

        if (layout != null) {
            if (layout instanceof PlacerLayout) {
                return (PlacerLayout) layout;
            }
        }

        return null;
    }

    String getParent(Interp interp, String widgetName)
            throws TclException {
        if (!Widgets.exists(interp, widgetName)) {
            throw new TclException(interp,
                    "bad window path name \"" + widgetName + "\"");
        }

        String masterName = null;
        int lastDot = widgetName.lastIndexOf(".");

        if (lastDot == 0) {
            masterName = ".";
        } else if (lastDot == -1) {
            throw new TclException(interp,
                    "bad window path name \"" + widgetName + "\"");
        } else {
            masterName = widgetName.substring(0, lastDot);
        }

        return (masterName);
    }

    private class Info extends GetValueOnEventThread {

        Component component = null;
        ArrayList<String> settings;

        ArrayList<String> exec(final Component component) {
            this.component = component;
            execOnThread();

            return settings;
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, true);
            PlacerLayout placer = getLayout(master);
            SwkWidget swkParent = Widgets.swankParent(component);
            settings = new ArrayList<String>();
            settings.add("-in");
            settings.add(swkParent.getName());
            placer.getComponentSettings((Component) component, settings);


        }
    }

    private static class Slaves extends GetValueOnEventThread {

        Component component = null;
        String[] names = null;

        String[] exec(final Component component) {
            this.component = component;
            execOnThread();

            return names;
        }

        @Override
        public void run() {
            Container parent = Widgets.getMasterContainer(component);

            int nMembers = parent.getComponentCount();
            names = new String[nMembers];

            for (int i = 0; i < nMembers; i++) {
                Component comp = parent.getComponent(i);
                names[i] = comp.getName();
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
                    if (!(layoutManager instanceof PlacerLayout)) {
                        continue;
                    }

                    parent.remove(comps[i]);
                    Widgets.relayoutContainer(parent);
                    parent.repaint();
                }
            }
        }
    }

    private class Configure extends UpdateOnEventThread {

        Container parent = null;
        String strippedArgs = null;
        Component[] comps = null;

        void exec(final Container parent, final Component[] comps,
                String strippedArgs) {
            this.comps = comps;
            this.parent = parent;
            this.strippedArgs = strippedArgs;
            execOnThread();
        }

        void doSpecial() {
            if (parent != null) {
                for (Component window : comps) {
                    int nMembers = parent.getComponentCount();

                    for (int iWin = 0; iWin < nMembers; iWin++) {
                        Component comp = parent.getComponent(iWin);

                        if (comp == (Component) window) {
                            parent.remove((Component) window);

                            break;
                        }
                    }
                }
            }
        }

        void addWindows() {
            for (Component window : comps) {
                int PlacePosition = -1;
                LayoutManager layoutManager = parent.getLayout();
                PlacerLayout Place = null;

                if (!(layoutManager instanceof PlacerLayout)) {
                    parent.removeAll();
                    Place = new PlacerLayout(interp);
                    parent.setLayout(Place);
                } else {
                    Place = (PlacerLayout) layoutManager;
                }

                try {
                    Place.setIgnoreNextRemove(true);

                    //parent.add (strippedArgs.toString (), (Component) window);
                    parent.add((Component) window, strippedArgs.toString(),
                            PlacePosition);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Place.setIgnoreNextRemove(false);
                }
            }
            LayoutHandler.addLayoutRequest(interp, parent);
        }

        @Override
        public void run() {
            doSpecial();
            addWindows();
            parent.repaint();
        }
    }
}
