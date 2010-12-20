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
import tcl.pkg.java.ReflectObject;
import java.awt.*;
import java.util.*;
import javax.swing.*;

public class PackCmd implements Command {

    static Hashtable columnTable = new Hashtable();
    static Hashtable rowTable = new Hashtable();
    /**
     * @param interp
     * @param argv
     * @throws TclException  */
    static final private String[] validCmds = {
        "configure", "forget", "info", "propagate", "slaves"
    };
    static final private int OPT_CONFIGURE = 0;
    static final private int OPT_FORGET = 1;
    static final private int OPT_INFO = 2;
    static final private int OPT_PROPAGATE = 3;
    static final private int OPT_SLAVES = 4;
    static TclObject configArg = TclString.newInstance("configure");
    Interp interp;

    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option arg ?arg ...?");
        }

        this.interp = interp;

        int firstWindow = 2;
        boolean debug = false;

        TclObject optionArg = null;

        if (!argv[1].toString().startsWith(".")
                && !argv[1].toString().startsWith("-")) {
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
            System.out.println("Pack option is " + optionArg.toString());
        }

        final int opt = TclIndex.get(interp, optionArg, validCmds, "option", 0);

        switch (opt) {
            case OPT_CONFIGURE:
                packConfigure(interp, argv, firstWindow);

                break;

            case OPT_FORGET:
                packForget(interp, argv, firstWindow);

                break;

            case OPT_INFO:
                packInfo(interp, argv);

                break;

            case OPT_PROPAGATE:
                packPropagate(interp, argv);

                break;

            case OPT_SLAVES:
                packSlaves(interp, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }


    PackerLayout getLayout(Container master) {
        LayoutManager layout = master.getLayout();

        if (layout != null) {
            if (layout instanceof PackerLayout) {
                return (PackerLayout) layout;
            }
        }

        return null;
    }

    void packInfo(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclException(interp,
                    "wrong # args: should be \"pack info window\"");
        }
        Component comp = Widgets.getComponent(interp, argv[2].toString());
        ArrayList<String> settings = (new Info()).exec(comp, argv[2].toString());

        if (settings.size() < 3) {
            throw new TclException(interp,
                    "window \"" + argv[2].toString() + "\" isn't placed");
        }
 
        TclObject result = TclList.newInstance();
        for (String value:settings) {
            TclList.append(interp,result,TclString.newInstance(value));
        }
        interp.setResult(result);


        return;
    }

    void packPropagate(Interp interp, TclObject[] argv)
            throws TclException {
        if ((argv.length != 3) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "window ?boolean?");
        }

        Component comp = Widgets.getComponent(interp, argv[2].toString());

        boolean propagate = false;
        boolean setPropagate = false;

        if (argv.length == 4) {
            setPropagate = true;
            propagate = TclBoolean.get(interp, argv[3]);
        }

        boolean result = (new Propagate()).exec(comp, propagate,
                setPropagate);
        interp.setResult(result);

        return;
    }

    void packSlaves(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
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

    void packForget(Interp interp, TclObject[] argv, int firstWindow)
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

    int initPackingWindow(Interp interp, TclObject[] argv, String[] args,
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
            if (args[i].equals("-after") || args[i].equals("-before")
                    || args[i].equals("-in")) {
                if (!Widgets.exists(interp, args[i + 1])) {
                    throw new TclException(interp,
                            "bad window path name \"" + args[i + 1] + "\"");
                }

                window1Special.add(args[i]);
                window1Special.add(args[i + 1]);
            } else {
                if (strippedArgs.length() != 0) {
                    strippedArgs.append(" ");
                }

                strippedArgs.append(" {" + args[i] + "} {" + args[i + 1] + "}");
            }
        }
    }

    /*
    void checkPackWindows(Interp interp, int firstWindow, int lastWindow) throws TclException {
    for (int i = firstWindow; i <= lastWindow; i++) {
    String windowName = args[i];
    if (windowName.equals(parentName)) {
    throw new TclException(interp, "can't pack \"" + windowName + "\" inside itself");
    }
    }
    }
     */
    void packConfigure(Interp interp, TclObject[] argv, int firstWindow)
            throws TclException {
        String[] args = new String[argv.length];

        for (int i = 0; i < argv.length; i++) {
            args[i] = argv[i].toString().intern();
        }

        int lastWindow = initPackingWindow(interp, argv, args, firstWindow);

        int firstArg = lastWindow + 1;
        int lastArg = argv.length - 1;
        int nArgs = lastArg - firstArg + 1;

        if ((nArgs % 2) != 0) {
            throw new TclException(interp,
                    "extra option \"" + argv[lastArg].toString()
                    + "\" (option with no value?)");
        }

        Vector window1Special = new Vector();
        StringBuffer strippedArgs = new StringBuffer();
        getSpecialArgs(interp, args, lastWindow, window1Special, strippedArgs);

        //checkPackWindows(interp,firstWindow,lastWindow);
        PackerLayout.checkPackArgs(interp, strippedArgs.toString(), null);
        (new Configure()).exec(window1Special, args, strippedArgs.toString(),
                firstWindow, lastWindow);
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

    class Info extends GetValueOnEventThread {

        Component component = null;
        String parentName = null;
        ArrayList<String> settings;

        ArrayList<String> exec(final Component component, final String parentName) {
            this.component = component;
            this.parentName = parentName;
            execOnThread();

            return settings;
        }

        @Override
        public void run() {
            Container master = Widgets.getMaster(component, true);
            PackerLayout packer = getLayout(master);
            SwkWidget swkParent = Widgets.swankParent(component);
            settings = new ArrayList<String>();
            settings.add("-in");
            settings.add(swkParent.getName());
            packer.getComponentSettings((Component) component,settings);


        }
    }

    class Propagate extends GetValueOnEventThread {

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

            PackerLayout packer = null;

            if (!(layoutManager instanceof PackerLayout)) {
                parent.removeAll();
                packer = new PackerLayout(interp);
                parent.setLayout(packer);
            } else {
                packer = (PackerLayout) layoutManager;
            }

            if (setPropagate) {
                packer.propagate = propagate;
                Widgets.relayoutContainer(parent);
            }

            propagate = packer.propagate;
        }
    }

    class Slaves extends GetValueOnEventThread {

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

    class Forget extends UpdateOnEventThread {

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
                    if (!(layoutManager instanceof PackerLayout)) {
                        continue;
                    }

                    parent.remove(comps[i]);
                    Widgets.relayoutContainer(parent);
                    parent.repaint();
                }
            }
        }
    }

    class Configure extends GetValueOnEventThread {

        String specialWindowName = null;
        String parentName = null;
        Container parent = null;
        String option = null;
        String strippedArgs = null;
        Vector window1Special = null;
        int firstWindow = 0;
        int lastWindow = 0;
        SwkWidget[] swkWidgets = null;
        String[] args = null;
        String errMsg = null;

        void exec(final Vector window1Special, final String[] args,
                String strippedArgs, int firstWindow, int lastWindow)
                throws TclException {
            this.args = args;
            this.window1Special = window1Special;
            this.strippedArgs = strippedArgs;
            this.firstWindow = firstWindow;
            this.lastWindow = lastWindow;
            doSpecial();
            execOnThread();

            if (errMsg != null) {
                throw new TclException(interp, errMsg);
            }
        }

        void doSpecial() throws TclException {
            if (window1Special.size() > 0) {
                for (int j = 0; j < window1Special.size(); j += 2) {
                    option = (String) window1Special.elementAt(j);

                    specialWindowName = (String) window1Special.elementAt(j
                            + 1);

                    if (option.equals("-after")) {
                        parentName = getParent(interp, specialWindowName);
                        parent = Widgets.getContainer(interp, parentName);

                        if (!(parent.getLayout() instanceof PackerLayout)) {
                            throw new TclException(interp,
                                    "window \"" + specialWindowName
                                    + "\" isn't packed");
                        }
                    } else if (option.equals("-before")) {
                        parentName = getParent(interp, specialWindowName);
                        parent = Widgets.getContainer(interp, parentName);

                        if (!(parent.getLayout() instanceof PackerLayout)) {
                            throw new TclException(interp,
                                    "window \"" + specialWindowName
                                    + "\" isn't packed");
                        }
                    } else if (option.equals("-in")) {
                        parentName = (String) window1Special.elementAt(j + 1);
                        parent = Widgets.getContainer(interp, parentName);
                    }
                }
            } else {
            }

            swkWidgets = new SwkWidget[lastWindow - firstWindow + 1];

            int j = 0;

            for (int i = firstWindow; i <= lastWindow; i++) {
                String windowName = args[i];

                if (window1Special.size() == 0) {
                    parentName = getParent(interp, windowName);
                    parent = Widgets.getContainer(interp, parentName);
                }

                int packPosition = -1;

                if (windowName.equals(parentName)) {
                    throw new TclException(interp,
                            "can't pack \"" + windowName + "\" inside itself");
                }

                SwkWidget window = (SwkWidget) Widgets.get(interp, windowName);

                if ((window instanceof JWindow) || (window instanceof JFrame)) {
                    throw new TclException(interp,
                            "can't pack \"" + windowName
                            + "\": it's a top-level window");
                }

                swkWidgets[j++] = window;
            }
        }

        void doSpecial1() {
            int j = 0;

            for (int i = firstWindow; i <= lastWindow; i++) {
                String windowName = args[i];

                if (!windowName.equals(specialWindowName)) {
                    SwkWidget window = swkWidgets[j];
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

        void addWindows() throws IllegalArgumentException {
            for (int i = 0; i < swkWidgets.length; i++) {
                int packPosition = -1;
                String windowName = args[i];

                if (window1Special.size() > 0) {
                    if (!option.equals("-in")) {
                        boolean isPacked = false;
                        int nMembers = parent.getComponentCount();

                        for (int iWin = 0; iWin < nMembers; iWin++) {
                            Component comp = parent.getComponent(iWin);

                            if (comp.getName().equals(specialWindowName)) {
                                if (option.equals("-after")) {
                                    packPosition = iWin + 1;
                                } else {
                                    packPosition = iWin;
                                }

                                isPacked = true;

                                if (windowName.equals(specialWindowName)) {
                                    packPosition = iWin;
                                }

                                break;
                            }
                        }

                        if (!isPacked) {
                            throw new IllegalArgumentException("window \""
                                    + specialWindowName + "\" isn't packed");
                        }
                    }
                }

                SwkWidget window = swkWidgets[i];

                LayoutManager layoutManager = parent.getLayout();
                PackerLayout packer = null;

                if (!(layoutManager instanceof PackerLayout)) {
                    parent.removeAll();
                    packer = new PackerLayout(interp);
                    parent.setLayout(packer);
                } else {
                    packer = (PackerLayout) layoutManager;
                }

                try {
                    packer.setIgnoreNextRemove(true);

                    //parent.add (strippedArgs.toString (), (Component) window);
                    parent.add((Component) window, strippedArgs, packPosition);

                    //           ((JComponent) window).revalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    packer.setIgnoreNextRemove(false);
                }
            }

            ((JComponent) parent).revalidate();
            LayoutHandler.addLayoutRequest(interp, parent);
        }

        @Override
        public void run() {
            try {
                doSpecial1();
                addWindows();
                parent.repaint();
            } catch (IllegalArgumentException iaE) {
                errMsg = iaE.getMessage();
            }
        }
    }
}
