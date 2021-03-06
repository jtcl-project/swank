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
import javax.swing.*;
import java.lang.reflect.*;

/**
 *
 * @author brucejohnson
 */
public class WmCmd implements Command {

    private static final String[] validCmds = {"alwaysontop",
        "aspect", "client", "colormapwindows", "command", "deiconify",
        "focusmodel", "frame", "geometry", "grid", "group", "iconbitmap",
        "iconify", "iconmask", "iconname", "iconposition", "iconwindow",
        "maxsize", "minsize", "overrideredirect", "positionfrom", "protocol",
        "resizable", "sizefrom", "state", "title", "transient", "withdraw"
    };
    private static final int OPT_ALWAYSONTOP = 0;
    private static final int OPT_ASPECT = 1;
    private static final int OPT_CLIENT = 2;
    private static final int OPT_COLORMAPWINDOWS = 3;
    private static final int OPT_COMMAND = 4;
    private static final int OPT_DEICONIFY = 5;
    private static final int OPT_FOCUSMODEL = 6;
    private static final int OPT_FRAME = 7;
    private static final int OPT_GEOMETRY = 8;
    private static final int OPT_GRID = 9;
    private static final int OPT_GROUP = 10;
    private static final int OPT_ICONBITMAP = 11;
    private static final int OPT_ICONIFY = 12;
    private static final int OPT_ICONMASK = 13;
    private static final int OPT_ICONNAME = 14;
    private static final int OPT_ICONCOMPOSITION = 15;
    private static final int OPT_ICONWINDOW = 16;
    private static final int OPT_MAXSIZE = 17;
    private static final int OPT_MINSIZE = 18;
    private static final int OPT_OVERRIDEREDIRECT = 19;
    private static final int OPT_POSITIONFROM = 20;
    private static final int OPT_PROTOCOL = 21;
    private static final int OPT_RESIZABLE = 22;
    private static final int OPT_SIZEFROM = 23;
    private static final int OPT_STATE = 24;
    private static final int OPT_TITLE = 25;
    private static final int OPT_TRANSIENT = 26;
    private static final int OPT_WITHDRAW = 27;

    /**
     * 
     * @param interp
     * @param argv
     * @throws TclException
     */
    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option window ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[2].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[2].toString() + "\"");
        }

        final Object object = (Object) ReflectObject.get(interp, tObj);

        if (!(object instanceof JFrame) && !(object instanceof Window)) {
            throw new TclException(interp,
                    "must be toplevel window " + argv[2].toString() + " for "
                    + argv[1].toString());
        }

        switch (opt) {
            case OPT_ALWAYSONTOP:
                if (argv.length == 3) {
                    interp.setResult(getAlwaysOnTop(interp, object,argv));
                } else if (argv.length == 4) {
                    setAlwaysOnTop(interp, object, argv);
                } else {
                    throw new TclNumArgsException(interp, 2, argv, "window ?0|1?");
                }
                break;
            case OPT_ASPECT:
                break;

            case OPT_CLIENT:
                break;

            case OPT_COLORMAPWINDOWS:
                break;

            case OPT_COMMAND:
                break;

            case OPT_DEICONIFY:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }

                (new UpdateOnEventThread() {

            @Override
                    public void run() {
                        if (object instanceof Frame) {
                            ((Frame) object).setExtendedState(Frame.NORMAL);
                        }

                        ((Window) object).setVisible(true);
                        ((Window) object).toFront();
                    }
                }).execOnThread();

                break;

            case OPT_FOCUSMODEL:
                break;

            case OPT_FRAME:
                break;

            case OPT_GEOMETRY:

                if (argv.length == 3) {
                    getGeometry(interp, object, argv);
                } else {
                    setGeometry(interp, object, argv);
                }

                break;

            case OPT_GRID:
                break;

            case OPT_GROUP:
                break;

            case OPT_ICONBITMAP:
                break;

            case OPT_ICONIFY:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }

                (new UpdateOnEventThread() {

            @Override
                    public void run() {
                        if (object instanceof Frame) {
                            ((Frame) object).setExtendedState(Frame.ICONIFIED
                                    | ((Frame) object).getExtendedState());
                        }
                    }
                }).execOnThread();

                break;

            case OPT_ICONMASK:
                break;

            case OPT_ICONNAME:
                break;

            case OPT_ICONCOMPOSITION:
                break;

            case OPT_ICONWINDOW:
                break;

            case OPT_MAXSIZE:
                break;

            case OPT_MINSIZE:
                break;

            case OPT_OVERRIDEREDIRECT:
                break;

            case OPT_POSITIONFROM:
                break;

            case OPT_PROTOCOL:
                setProtocol(interp, object, argv);

                break;

            case OPT_RESIZABLE:
                break;

            case OPT_SIZEFROM:
                break;

            case OPT_STATE:
                break;

            case OPT_TITLE:

                if (!(object instanceof JFrame)) {
                    return;
                }

                if ((argv.length != 3) && (argv.length != 4)) {
                    throw new TclNumArgsException(interp, 2, argv, "window ?title?");
                }

                if (argv.length == 4) {
                    final String title = argv[3].toString().intern();
                    (new UpdateOnEventThread() {

                @Override
                    public void run() {
                        ((JFrame) object).setTitle(title);
                    }
                    }).execOnThread();
                }
                String currentTitle = (new GetTitle()).exec(object);
                interp.setResult(currentTitle);
                break;

            case OPT_TRANSIENT:
                break;

            case OPT_WITHDRAW:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "window");
                }

                (new UpdateOnEventThread() {

            @Override
                    public void run() {
                        ((Window) object).setVisible(false);
                    }
                }).execOnThread();

                break;

            default:
        }
    }

    void getGeometry(final Interp interp, final Object object,
            final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "window");
        }

        String widgetName = argv[2].toString();

        if (!(object instanceof JFrame) && !(object instanceof JWindow)) {
            throw new TclException(interp,
                    "invalid object type \"" + widgetName + "\"");
        }

        Rectangle rectangle = (new GeometryGet()).exec(object);
        interp.setResult(rectangle.width + "x" + rectangle.height + "+"
                + rectangle.x + "+" + rectangle.y);
    }

    void setGeometry(final Interp interp, final Object object,
            final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "window geometry");
        }

        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        String widgetName = argv[2].toString();

        if (!(object instanceof JFrame) && !(object instanceof JWindow)) {
            throw new TclException(interp,
                    "invalid object type \"" + widgetName + "\"");
        }
// FIXME doesn't work properly for negative locations values
        String geometry = argv[3].toString();
        String locPattern = "^([\\+\\-])([0-9]*)(\\+)([0-9]*)$";
        String sizePattern = "^([0-9]*)(x)([0-9]*)$";
        String bothPattern = "^([0-9]*)(x)([0-9]*)(\\+)([0-9]*)(\\+)([0-9]*)$";

        boolean setSize = false;
        boolean setLocation = false;

        if (geometry.equals("")) {
            setSize = false;
            setLocation = false;
        } else if (geometry.matches(bothPattern)) {
            int xPos = geometry.indexOf('x');
            int pPos1 = geometry.indexOf('+');
            int pPos2 = geometry.lastIndexOf('+');
            w = Integer.parseInt(geometry.substring(0, xPos));
            h = Integer.parseInt(geometry.substring(xPos + 1, pPos1));
            x = Integer.parseInt(geometry.substring(pPos1 + 1, pPos2));
            y = Integer.parseInt(geometry.substring(pPos2 + 1));
            setSize = true;
            setLocation = true;
        } else if (geometry.matches(sizePattern)) {
            int xPos = geometry.indexOf('x');
            w = Integer.parseInt(geometry.substring(0, xPos));
            h = Integer.parseInt(geometry.substring(xPos + 1));
            setSize = true;
        } else if (geometry.matches(locPattern)) {
            int pPos = geometry.lastIndexOf('+');
            if (geometry.charAt(0) == '-') {
                x = Integer.parseInt(geometry.substring(0, pPos));
            } else {
                x = Integer.parseInt(geometry.substring(1, pPos));
            }
            y = Integer.parseInt(geometry.substring(pPos + 1));
            setLocation = true;
        } else {
            throw new TclException(interp, "bad geometry \"" + geometry + "\"");
        }

        (new GeometrySet()).exec(object, setLocation, setSize, x, y, w, h);
    }

    void setProtocol(final Interp interp, final Object object,
            final TclObject[] argv) throws TclException {
        if (argv.length != 5) {
            throw new TclNumArgsException(interp, 2, argv, "window name command");
        }

        String widgetName = argv[2].toString().intern();
        String protocolName = argv[3].toString().intern();
        String command = argv[4].toString().intern();

        if (!(object instanceof JFrame) && !(object instanceof JWindow)) {
            throw new TclException(interp,
                    "invalid object type \"" + widgetName + "\"");
        }

        int closeMode = 0;

        if (protocolName.equals("WM_DELETE_WINDOW")) {
            if (command.equals("")) {
                closeMode = WindowConstants.DISPOSE_ON_CLOSE;
            } else {
                closeMode = WindowConstants.DO_NOTHING_ON_CLOSE;
            }
        }

        (new ProtocolSet()).exec(object, closeMode, command);
    }

    private static class GeometryGet extends GetValueOnEventThread {

        Object object = null;
        Rectangle rectangle = null;

        Rectangle exec(final Object object) throws TclException {
            this.object = object;
            execOnThread();

            return rectangle;
        }

        @Override
        public void run() {
            Dimension size = null;
            Point location = null;
            JRootPane jRoot;

            if (object instanceof JFrame) {
                jRoot = ((JFrame) object).getRootPane();
            } else {
                jRoot = ((JWindow) object).getRootPane();
            }

            Window window = (Window) object;
            size = jRoot.getSize();
            location = window.getLocation();

            rectangle = new Rectangle();
            rectangle.x = location.x;
            rectangle.y = location.y;
            rectangle.width = size.width;
            rectangle.height = size.height;

            if (rectangle.x < 0) {
                rectangle.x = 0;
            }

            if (rectangle.y < 0) {
                rectangle.y = 0;
            }

            if (rectangle.width < 0) {
                rectangle.width = 0;
            }

            if (rectangle.height < 0) {
                rectangle.height = 0;
            }
        }
    }

    private static class GeometrySet extends UpdateOnEventThread {

        boolean setLocation = false;
        boolean setSize = false;
        Object object = null;
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;

        void exec(final Object object, final boolean setLocation,
                final boolean setSize, final int x, final int y, final int w,
                final int h) {
            this.setLocation = setLocation;
            this.setSize = setSize;
            this.object = object;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            execOnThread();
        }

        @Override
        public void run() {
            JRootPane jRoot = null;
            Window window = (Window) object;

            if (object instanceof JFrame) {
                jRoot = ((JFrame) object).getRootPane();
            } else if (object instanceof JWindow) {
                jRoot = ((JWindow) object).getRootPane();
            }

            if (!setSize && !setLocation) {
                if (object instanceof JFrame) {
                    ((SwkJFrame) object).setGeometryInactive();
                } else {
                    jRoot.setMinimumSize(new Dimension(0, 0));
                }

                window.pack();
            } else {
                if (setSize) {
                    if (object instanceof JFrame) {
                        ((SwkJFrame) object).setGeometry(w, h);
                    }

                    jRoot.setSize(new Dimension(w, h));
                    window.pack();
                }

                if (setLocation) {
                    window.setLocation(x, y);
                }
            }
        }
    }

    private static class ProtocolSet extends UpdateOnEventThread {

        Object object = null;
        int closeOp = 0;
        String command = "";

        void exec(final Object object, final int closeOp, final String command) {
            this.object = object;
            this.closeOp = closeOp;
            this.command = command;

            execOnThread();
        }

        @Override
        public void run() {
            if (object instanceof SwkJFrame) {
                SwkJFrame jframe = (SwkJFrame) object;
                jframe.setDefaultCloseOperation(closeOp);
                jframe.setCloseCommand(command);
            }
        }
    }
   private static class GetAlwaysOnTop extends GetValueOnEventThread {

        Object object = null;
        boolean value = false;

        boolean exec(final Object object) {
            this.object = object;
            execOnThread();
            return value;
        }

        @Override
        public void run() {
            if (object instanceof JFrame) {
                JFrame jframe = (JFrame) object;
                value = jframe.isAlwaysOnTop();  
            }
        }
    }

    boolean getAlwaysOnTop(final Interp interp, final Object object,
            final TclObject[] argv) throws TclException {
        if (!(object instanceof JFrame) && !(object instanceof JWindow)) {
            throw new TclException(interp,
                    "invalid object type \"" + argv[2].toString() + "\"");
        }
        return (new GetAlwaysOnTop()).exec(object);
    }

    private static class AlwaysOnTop extends UpdateOnEventThread {

        Object object = null;
        boolean value = false;

        void exec(final Object object, final boolean value) {
            this.object = object;
            this.value = value;
            execOnThread();
        }

        @Override
        public void run() {
            if (object instanceof JFrame) {
                JFrame jframe = (JFrame) object;
                Class jfClass = jframe.getClass();
                try {
                    Method m = jfClass.getMethod("setAlwaysOnTop", new Class[]{boolean.class});
                    m.invoke(jframe, value);
                } catch (Exception e) {
                    System.out.println("always on top failed " + e.getMessage());
                }
            }
        }
    }

    void setAlwaysOnTop(final Interp interp, final Object object,
            final TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "window 0|1");
        }

        if (!(object instanceof JFrame) && !(object instanceof JWindow)) {
            throw new TclException(interp,
                    "invalid object type \"" + argv[2].toString() + "\"");
        }
        boolean value = TclBoolean.get(interp, argv[3]);
        (new AlwaysOnTop()).exec(object, value);
    }
   private static class GetTitle extends GetValueOnEventThread {
        Object object = null;
        String value = "";

        String exec(final Object object) {
            this.object = object;
            execOnThread();
            return value;
        }

        @Override
        public void run() {
            if (object instanceof JFrame) {
                JFrame jframe = (JFrame) object;
                value = jframe.getTitle();  
            }
        }
    }
}
