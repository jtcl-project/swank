/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;

import com.sun.j3d.utils.geometry.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.font.*;
import java.awt.geom.*;

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.media.j3d.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;

import javax.vecmath.*;


class SwkCanvasWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "object", "jadd", "create", "itemconfigure",
        "coords", "hit", "itemget", "find", "move", "scale", "delete", "addtag",
        "bind", "raise", "lower", "dtag", "gettags", "canvasx", "canvasy",
        "copy", "index", "view", "center", "transform"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_OBJECT = 2;
    static final private int OPT_JADD = 3;
    static final private int OPT_CREATE = 4;
    static final private int OPT_ITEMCONFIG = 5;
    static final private int OPT_COORDS = 6;
    static final private int OPT_HIT = 7;
    static final private int OPT_ITEMGET = 8;
    static final private int OPT_FIND = 9;
    static final private int OPT_MOVE = 10;
    static final private int OPT_SCALE = 11;
    static final private int OPT_DELETE = 12;
    static final private int OPT_ADDTAG = 13;
    static final private int OPT_BIND = 14;
    static final private int OPT_RAISE = 15;
    static final private int OPT_LOWER = 16;
    static final private int OPT_DTAG = 17;
    static final private int OPT_GETTAGS = 18;
    static final private int OPT_CANVASX = 19;
    static final private int OPT_CANVASY = 20;
    static final private int OPT_COPY = 21;
    static final private int OPT_INDEX = 22;
    static final private int OPT_VIEW = 23;
    static final private int OPT_CENTER = 24;
    static final private int OPT_TRANSFORM = 25;

    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        int i;
        boolean gotDefaults = false;
        Vector shapeList = null;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.theWidgets.get(argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        SwkCanvas3D swkcanvas = (SwkCanvas3D) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(jget(interp, swkcanvas, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkcanvas.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                String keyName;
                ResourceObject ro;
                String result;
                TclObject list2 = TclList.newInstance();
                Enumeration e = SwkCanvas3D.resourceDB.keys();

                while (e.hasMoreElements()) {
                    TclObject list1 = TclList.newInstance();
                    keyName = (String) e.nextElement();

                    if (keyName == null) {
                        continue;
                    }

                    ro = (ResourceObject) SwkCanvas3D.resourceDB.get(keyName);

                    if (ro == null) {
                        continue;
                    }

                    tObj = TclString.newInstance(keyName);

                    try {
                        result = jget(interp, swkcanvas, tObj);
                    } catch (TclException tclE) {
                        continue;
                    }

                    TclList.append(interp, list1, tObj);
                    TclList.append(interp, list1,
                        TclString.newInstance(ro.resource));
                    TclList.append(interp, list1,
                        TclString.newInstance(ro.className));

                    if (ro.defaultVal == null) {
                        TclList.append(interp, list1, TclString.newInstance(""));
                    } else {
                        TclList.append(interp, list1,
                            TclString.newInstance(ro.defaultVal));
                    }

                    if (result == null) {
                        result = "";
                    }

                    TclList.append(interp, list1, TclString.newInstance(result));
                    TclList.append(interp, list2, list1);
                }

                interp.setResult(list2);
            } else if (argv.length == 3) {
                String result = jget(interp, swkcanvas, argv[2]);
                ResourceObject ro = (ResourceObject) SwkCanvas3D.resourceDB.get(argv[2].toString());
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
                configure(interp, swkcanvas, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            jadd(interp, swkcanvas, argv[2]);

            break;

        case OPT_CREATE:

            SwkShape3D swkShape = null;

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv[2].toString().equals("sphere")) {
                if (argv.length < 6) {
                    throw new TclNumArgsException(interp, 3, argv, "option");
                }

                if (((argv.length - 6) % 2) != 0) {
                    throw new TclNumArgsException(interp, 3, argv, "option");
                }

                SwkSphere swkSphere = new SwkSphere(swkcanvas);
                swkSphere.coords(interp, swkcanvas, argv, 3);
                swkSphere.config(interp, argv, 6);
                swkSphere.genShape();
                swkcanvas.addShape(swkSphere);
                swkcanvas.repaint();
                interp.setResult(swkSphere.id);
            } else if ("cylinder".startsWith(argv[2].toString())) {
                if (argv.length < 9) {
                    throw new TclNumArgsException(interp, 3, argv, "option");
                }

                if (((argv.length - 9) % 2) != 0) {
                    throw new TclNumArgsException(interp, 3, argv, "option");
                }

                SwkCylinder swkCylinder = new SwkCylinder(swkcanvas);
                swkCylinder.coords(interp, swkcanvas, argv, 3);
                swkCylinder.config(interp, argv, 9);
                swkCylinder.genShape();
                swkcanvas.addShape(swkCylinder);
                swkcanvas.repaint();
                interp.setResult(swkCylinder.id);
            } else if ("text2d".startsWith(argv[2].toString())) {
                if (argv.length < 6) {
                    throw new TclNumArgsException(interp, 3, argv, "option");
                }

                if (((argv.length - 6) % 2) != 0) {
                    throw new TclNumArgsException(interp, 3, argv, "option");
                }

                System.out.println("create swkText2D");

                SwkText2D swkText2D = new SwkText2D(swkcanvas);
                System.out.println("created swkText2D " + swkText2D.toString());
                swkText2D.coords(interp, swkcanvas, argv, 3);
                System.out.println("coords");
                swkText2D.config(interp, argv, 6);
                System.out.println("configed");
                swkText2D.genShape();
                System.out.println("gened");
                swkcanvas.addShape(swkText2D);
                swkcanvas.repaint();
                interp.setResult(swkText2D.id);
            } else {
                Class newClass = null;

                try {
                    newClass = Class.forName(argv[2].toString());
                } catch (ClassNotFoundException cnfE) {
                    throw new TclException(interp,
                        "class " + argv[2].toString() + " doesn't exist");
                }

                Object newObject = null;

                try {
                    newObject = newClass.newInstance();
                } catch (InstantiationException iE) {
                    throw new TclException(interp,
                        "class " + argv[2].toString() +
                        " can't be instantiated");
                } catch (IllegalAccessException iaE) {
                    throw new TclException(interp,
                        "class " + argv[2].toString() +
                        " can't be illegal access ");
                }

                if (!(newObject instanceof SwkShape3D)) {
                    throw new TclException(interp,
                        "class " + argv[2].toString() +
                        " can't be illegal access");
                }

                swkShape = (SwkShape3D) newObject;
                swkShape.setCanvas(swkcanvas);
                swkShape.coords(interp, swkcanvas, argv, 3);
                swkShape.config(interp, argv, 6);
                swkcanvas.addShape(swkShape);
                swkcanvas.repaint();
                interp.setResult(swkShape.id);
            }

            break;

        case OPT_ITEMCONFIG:

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv[2]);

            for (i = 0; i < shapeList.size(); i++) {
                swkShape = (SwkShape3D) shapeList.elementAt(i);
                swkShape.config(interp, argv, 3);
            }

            swkcanvas.repaint();

            break;

        case OPT_INDEX:

            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "tagOrId arg");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv[2]);

            if (shapeList.size() == 0) {
                return;
            }

            swkShape = (SwkShape3D) shapeList.elementAt(0);
            interp.setResult(swkShape.getIndex(interp, argv[3]));

            break;

        case OPT_ITEMGET:

            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            //swkShape = (SwkShape3D) swkcanvas.getShape(interp, argv[2]);
            //interp.setResult(swkShape.itemGet(interp, argv[3]));
            break;

        case OPT_COORDS:

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv[2]);

            if (argv.length == 3) {
                if (shapeList.size() != 1) {
                    throw new TclException(interp,
                        "Must specify exactly one shape");
                }

                swkShape = (SwkShape3D) shapeList.elementAt(0);
                swkShape.coords(interp);
            } else {
                for (i = 0; i < shapeList.size(); i++) {
                    swkShape = (SwkShape3D) shapeList.elementAt(i);
                    swkShape.coords(interp, swkcanvas, argv, 3);
                }
            }

            swkcanvas.repaint();

            break;

        case OPT_HIT:

            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 2, argv, "item x y");
            }

            swkShape = null;

            // swkShape = (SwkShape3D) swkcanvas.getShape(interp, argv[2]);
            double scanX = TclDouble.get(interp, argv[3]);
            double scanY = TclDouble.get(interp, argv[4]);
            interp.setResult(swkShape.hit(scanX, scanY));

            //swkcanvas.scanCanvas(scanX,scanY);
            break;

        case OPT_FIND:

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swkcanvas.search(interp, argv, 2);

            break;

        case OPT_DELETE:

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            swkcanvas.delete(interp, argv, 2);
            swkcanvas.repaint();

            break;

        case OPT_RAISE:

            if ((argv.length != 3) && (argv.length != 4)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv.length == 3) {
                //  swkcanvas.raise(interp, argv[2], null);
            } else {
                // swkcanvas.raise(interp, argv[2], argv[3]);
            }

            swkcanvas.repaint();

            break;

        case OPT_LOWER:

            if ((argv.length != 3) && (argv.length != 4)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv.length == 3) {
                //  swkcanvas.lower(interp, argv[2], null);
            } else {
                //  swkcanvas.lower(interp, argv[2], argv[3]);
            }

            swkcanvas.repaint();

            break;

        case OPT_MOVE:

            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv[2]);

            double dX = SwankUtil.getTkSizeD(interp, (Component) swkcanvas,
                    argv[3]);
            double dY = SwankUtil.getTkSizeD(interp, (Component) swkcanvas,
                    argv[4]);

            for (i = 0; i < shapeList.size(); i++) {
                swkShape = (SwkShape3D) shapeList.elementAt(i);
                swkShape.move(dX, dY);
            }

            swkcanvas.repaint();

            break;

        case OPT_DTAG:

            if ((argv.length != 3) && (argv.length != 4)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv[2]);

            for (i = 0; i < shapeList.size(); i++) {
                swkShape = (SwkShape3D) shapeList.elementAt(i);

                if (argv.length == 3) {
                    //   swkcanvas.removeTags(interp, argv[2], false, swkShape);
                } else {
                    //   swkcanvas.removeTags(interp, argv[3], false, swkShape);
                }
            }

            swkcanvas.repaint();

            break;

        case OPT_ADDTAG:

            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv, 3);

            for (i = 0; i < shapeList.size(); i++) {
                System.out.println("add tag to " + i);
                swkShape = (SwkShape3D) shapeList.elementAt(i);
                swkcanvas.addTags(interp, argv[2], swkShape);
            }

            swkcanvas.repaint();

            break;

        case OPT_GETTAGS:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv[2]);

            if (shapeList.size() > 0) {
                swkShape = (SwkShape3D) shapeList.elementAt(0);
                interp.setResult(swkcanvas.getTags(interp, swkShape));
                swkcanvas.repaint();
            }

            break;

        case OPT_BIND:

            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            SwkBinding binding = SwkBind.getBinding(interp, argv, 3);

            /* FIXME
                        if (binding != null) {
                            swkcanvas.setupBinding(interp, binding, argv[2].toString());
                        }
            */
            break;

        case OPT_SCALE:

            if (argv.length != 7) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeList = swkcanvas.getShapesWithTags(interp, argv[2]);

            double xOrigin = TclDouble.get(interp, argv[3]);
            double yOrigin = TclDouble.get(interp, argv[4]);
            double xScale = TclDouble.get(interp, argv[5]);
            double yScale = TclDouble.get(interp, argv[6]);

            for (i = 0; i < shapeList.size(); i++) {
                swkShape = (SwkShape3D) shapeList.elementAt(i);
                swkShape.scale(xOrigin, yOrigin, xScale, yScale);
            }

            swkcanvas.repaint();

            break;

        case OPT_CANVASX:

            if ((argv.length != 3) && (argv.length != 4)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv.length == 4) {
                double grid = SwankUtil.getTkSizeD(interp,
                        (Component) swkcanvas, argv[3]);
                double x = TclDouble.get(interp, argv[2]);
                interp.setResult((int) (Math.round(x / grid) * grid));
            } else {
                interp.setResult(TclDouble.get(interp, argv[2]));
            }

            break;

        case OPT_CANVASY:

            if ((argv.length != 3) && (argv.length != 4)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv.length == 4) {
                double grid = SwankUtil.getTkSizeD(interp,
                        (Component) swkcanvas, argv[3]);
                double x = TclDouble.get(interp, argv[2]);
                interp.setResult((int) (Math.round(x / grid) * grid));
            } else {
                interp.setResult(TclDouble.get(interp, argv[2]));
            }

            break;

        case OPT_COPY: {
            final Clipboard clipboard = Toolkit.getDefaultToolkit()
                                               .getSystemClipboard();
            System.out.println("exporting to clipboard");

            // FIXME swkcanvas.copyImageToClipboard(clipboard);
            interp.setResult("");

            break;
        }

        case OPT_VIEW: {
            if ((argv.length != 2) && (argv.length != 6)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv.length == 6) {
                if (argv[2].toString().equals("eye")) {
                    float x = (float) TclDouble.get(interp, argv[3]);
                    float y = (float) TclDouble.get(interp, argv[4]);
                    float z = (float) TclDouble.get(interp, argv[5]);
                    swkcanvas.setEyePosition(x, y, z);
                } else if (argv[2].toString().equals("center")) {
                    float x = (float) TclDouble.get(interp, argv[3]);
                    float y = (float) TclDouble.get(interp, argv[4]);
                    float z = (float) TclDouble.get(interp, argv[5]);
                    swkcanvas.setViewCenter(x, y, z);
                } else if (argv[2].toString().equals("up")) {
                    float x = (float) TclDouble.get(interp, argv[3]);
                    float y = (float) TclDouble.get(interp, argv[4]);
                    float z = (float) TclDouble.get(interp, argv[5]);
                    swkcanvas.setUpDirection(x, y, z);
                }
            } else {
                Transform3D t3 = new Transform3D();
                TransformGroup tG = swkcanvas.universe.getViewingPlatform()
                                                      .getViewPlatformTransform();
                tG.getTransform(t3);
                interp.setResult(t3.toString());
            }

            break;
        }

        case OPT_CENTER: {
            if ((argv.length != 2) && (argv.length != 5)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv.length == 5) {
                double x = TclDouble.get(interp, argv[2]);
                double y = TclDouble.get(interp, argv[3]);
                double z = TclDouble.get(interp, argv[4]);
                swkcanvas.setCenter((float) x, (float) y, (float) z);
            } else {
                interp.setResult(TclDouble.get(interp, argv[2]));
            }

            break;
        }

        case OPT_TRANSFORM: {
            if ((argv.length != 3) && (argv.length != 6)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv[2].toString().equals("reset")) {
                swkcanvas.resetTransform();
            }

            break;
        }

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    public static void configure(Interp interp, SwkCanvas3D swkcanvas,
        TclObject[] argv, int start) throws TclException {
        int i;

        if (argv.length <= start) {
            return;
        }

        ResourceObject ro = null;

        for (i = start; i < argv.length; i += 2) {
            if ((i + 1) >= argv.length) {
                throw new TclException(interp,
                    "value for \"" + argv[i].toString() + "\" missing");
            }

            ro = (ResourceObject) SwkCanvas3D.resourceDB.get(argv[i].toString());

            if (ro == null) {
                throw new TclException(interp,
                    "unknown option \"" + argv[i].toString() + "\"");
            }

            if (ro.defaultVal == null) {
                ro.defaultVal = jget(interp, swkcanvas, argv[i]);
            }

            if (argv[i].toString().equals("-thread")) {
                System.out.println("Current thread is " +
                    Thread.currentThread().getName());
            } else if (argv[i].toString().equals("-bg")) {
                Color background = SwankUtil.getColor(interp, argv[i + 1]);
                swkcanvas.setBackground3D(background);
            } else if (argv[i].toString().equals("-background")) {
                Color background = SwankUtil.getColor(interp, argv[i + 1]);
                swkcanvas.setBackground3D(background);
            } else {
                throw new TclException(interp,
                    "unknown option \"" + argv[i].toString() + "\"");
            }
        }
    }

    public static String jget(Interp interp, SwkCanvas3D swkcanvas,
        TclObject tclObject) throws TclException {
        if (tclObject.toString().equals("-thread")) {
            return (Thread.currentThread().getName());
        } else if (tclObject.toString().equals("-bg")) {
            return (SwankUtil.parseColor(interp, swkcanvas.getBackground3D()));
        } else if (tclObject.toString().equals("-background")) {
            return (SwankUtil.parseColor(interp, swkcanvas.getBackground()));
        }

        throw new TclException(interp,
            "unknown option \"" + tclObject.toString() + "\"");
    }

    public static void jadd(Interp interp, SwkCanvas3D swkcanvas,
        TclObject tclObject) throws TclException {
        int i;
        TclObject tObj = (TclObject) Widgets.theWidgets.get(tclObject.toString());

        if (tObj != null) {
            Object object = ReflectObject.get(interp, tObj);

            if (object instanceof java.awt.PopupMenu) {
                swkcanvas.add((java.awt.PopupMenu) object);

                return;
            }
        }

        if (tObj == null) {
            throw new TclException(interp, "Object not found");
        }
    }
}
