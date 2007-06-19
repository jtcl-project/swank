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

import java.io.*;

import java.lang.*;

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
                "option arg ?arg ...?");
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
        ;

        TclObject optionArg = null;

        if (!argv[1].toString().startsWith(".") &&
                !argv[1].toString().startsWith("-")) {
            optionArg = argv[1];

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option arg ?arg ...?");
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
                "wrong # args: should be \"place info window\"");
        }

        SwkWidget window = (SwkWidget) Widgets.get(interp, argv[2].toString());

        if (window == null) {
            throw new TclException(interp,
                "window \"" + argv[2].toString() + "\" doesn't exist");
        }

        String result = (new Info()).exec(window, argv[2].toString());

        if (result == null) {
            throw new TclException(interp,
                "window \"" + argv[2].toString() + "\" isn't Placeed");
        }

        interp.setResult(result);

        return;
    }

    void placeSlaves(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        if (!Widgets.exists(argv[2].toString())) {
            throw new TclException(interp,
                "bad window path name \"" + argv[2].toString() + "\"");
        }

        String[] names = (new Slaves()).exec(argv[2].toString());
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
        String[] names = new String[argv.length - firstWindow];
        int j = 0;

        for (int i = firstWindow; i < argv.length; i++) {
            names[j++] = argv[i].toString();
        }

        (new Forget()).exec(names);
    }

    int initPlaceingWindow(Interp interp, TclObject[] argv, String[] args,
        int firstWindow) throws TclException {
        int lastWindow = 0;

        if (args.length <= firstWindow) {
            throw new TclNumArgsException(interp, 1, argv,
                "slave ?slave ...? ?options?");
        }

        if (!Widgets.exists(args[firstWindow])) {
            if (firstWindow == 1) {
                throw new TclException(interp,
                    "bad window path name \"" + args[firstWindow] + "\"");
            } else {
                throw new TclException(interp,
                    "bad argument \"" + args[firstWindow] +
                    "\": must be name of window");
            }
        }

        for (int i = firstWindow; i < args.length; i++) {
            if (!args[i].startsWith(".")) {
                break;
            } else {
                SwkWidget window = (SwkWidget) Widgets.get(interp, args[i]);

                if (window == null) {
                    throw new TclException(interp,
                        "bad window path name \"" + args[i] + "\"");
                }

                lastWindow = i;
            }
        }

        if (lastWindow < firstWindow) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        return lastWindow;
    }

    void getSpecialArgs(Interp interp, String[] args, int lastWindow,
        Vector window1Special, StringBuffer strippedArgs)
        throws TclException {
        int firstArg = lastWindow + 1;
        int lastArg = args.length - 1;

        for (int i = firstArg; i <= lastArg; i += 2) {
            /*
            if (argv[i + 1].toString ().startsWith ("-") && !Character.isDigit(argv[i+1].toString().charAt(1))) {
                throw new TclException (interp, "argument without value");
            }
             */
            if (args[i].equals("-in")) {
                if (!Widgets.exists(args[i + 1].toString())) {
                    throw new TclException(interp,
                        "bad window path name \"" + args[i + 1] + "\"");
                }

                window1Special.add(args[i]);
                window1Special.add(args[i + 1]);
            } else {
                if (strippedArgs.length() != 0) {
                    strippedArgs.append(" ");
                }

                strippedArgs.append(args[i] + " {" + args[i + 1] + "}");
            }
        }
    }

    void placeConfigure(Interp interp, TclObject[] argv, int firstWindow)
        throws TclException {
        String[] args = new String[argv.length];

        for (int i = 0; i < argv.length; i++) {
            args[i] = argv[i].toString().intern();
        }

        int lastWindow = initPlaceingWindow(interp, argv, args, firstWindow);

        int firstArg = lastWindow + 1;
        int lastArg = argv.length - 1;
        int nArgs = lastArg - firstArg + 1;

        if ((nArgs % 2) != 0) {
            throw new TclException(interp,
                "extra option \"" + argv[lastArg].toString() +
                "\" (option with no value?)");
        }

        Vector window1Special = new Vector();
        StringBuffer strippedArgs = new StringBuffer();
        getSpecialArgs(interp, args, lastWindow, window1Special, strippedArgs);
        PlacerLayout.checkPlaceArgs(interp, strippedArgs.toString(), null);
        (new Configure()).exec(window1Special, args, strippedArgs.toString(),
            firstWindow, lastWindow);
    }

    String getParent(Interp interp, String widgetName)
        throws TclException {
        if (!Widgets.exists(widgetName)) {
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

    class Info extends GetValueOnEventThread {
        String result = null;
        String item = null;
        SwkWidget window = null;

        String exec(SwkWidget window, String item) {
            this.item = item;
            this.window = window;
            execOnThread();

            return result;
        }

        public void run() {
            String parentName = null;
            Container parent = null;

            try {
                parentName = Widgets.parent(interp, item);
                parent = Widgets.getContainer(interp, parentName);
            } catch (TclException tclE) {
                interp.backgroundError();

                return;

                //FIXME
            }

            LayoutManager layoutManager = parent.getLayout();

            if (!(layoutManager instanceof PlacerLayout)) {
                //throw new TclException(interp,"window \"" + item + "\" isn't Placeed");
                //FIXME
                interp.backgroundError();

                return;
            }

            PlacerLayout placer = (PlacerLayout) layoutManager;

            Object settings = placer.getComponentSettings((Component) window);

            result = settings.toString() + " -in " + parentName;
        }
    }

    class Slaves extends GetValueOnEventThread {
        String item = null;
        String[] names = null;

        String[] exec(String item) {
            this.item = item;
            execOnThread();

            return names;
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

            int nMembers = parent.getComponentCount();
            names = new String[nMembers];

            for (int i = 0; i < nMembers; i++) {
                Component comp = parent.getComponent(i);
                names[i] = comp.getName();
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
                    if (!Widgets.exists(names[i])) {
                        continue;
                    }

                    String parentName = Widgets.parent(interp, names[i]);

                    if (parentName.equals("")) {
                        continue;
                    }

                    Container parent = Widgets.getContainer(interp, parentName);
                    LayoutManager layoutManager = parent.getLayout();

                    if (!(layoutManager instanceof PlacerLayout)) {
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

    class Configure extends UpdateOnEventThread {
        String specialWindowName = null;
        String parentName = null;
        Container parent = null;
        String option = null;
        String strippedArgs = null;
        Vector window1Special = null;
        int firstWindow = 0;
        int lastWindow = 0;
        String[] args = null;

        void exec(final Vector window1Special, final String[] args,
            String strippedArgs, int firstWindow, int lastWindow) {
            this.args = args;
            this.window1Special = window1Special;
            this.strippedArgs = strippedArgs;
            this.firstWindow = firstWindow;
            this.lastWindow = lastWindow;
            execOnThread();
        }

        void doSpecial() throws TclException {
            if (window1Special.size() > 0) {
                for (int j = 0; j < window1Special.size(); j += 2) {
                    option = (String) window1Special.elementAt(j);

                    specialWindowName = (String) window1Special.elementAt(j +
                            1);

                    if (option.equals("-in")) {
                        parentName = (String) window1Special.elementAt(j + 1);
                        parent = Widgets.getContainer(interp, parentName);
                    }
                }

                for (int i = firstWindow; i <= lastWindow; i++) {
                    String windowName = args[i];

                    if (!windowName.equals(specialWindowName)) {
                        SwkWidget window = (SwkWidget) Widgets.get(interp,
                                windowName);

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
        }

        void addWindows() throws TclException {
            String window1 = args[firstWindow];

            for (int i = firstWindow; i <= lastWindow; i++) {
                int PlacePosition = -1;
                String windowName = args[i];

                if (windowName.equals(parentName)) {
                    throw new TclException(interp,
                        "can't Place \"" + windowName + "\" inside itself");
                }

                if (window1Special.size() > 0) {
                } else {
                    parentName = getParent(interp, windowName);
                    parent = Widgets.getContainer(interp, parentName);
                }

                SwkWidget window = (SwkWidget) Widgets.get(interp, windowName);

                if ((window instanceof JWindow) || (window instanceof JFrame)) {
                    throw new TclException(interp,
                        "can't Place \"" + windowName +
                        "\": it's a top-level window");
                }

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

        public void run() {
            try {
                doSpecial();
                addWindows();
            } catch (TclException tclE) {
                interp.backgroundError();
            }

            parent.repaint();
        }
    }
}
