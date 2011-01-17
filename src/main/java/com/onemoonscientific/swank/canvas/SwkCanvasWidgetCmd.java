/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;
import tcl.lang.*;
import tcl.pkg.java.ReflectObject;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;


/**
 *
 * @author brucejohnson
 */
public class SwkCanvasWidgetCmd implements Command {

    private static final String[] validCmds = {
        "cget", "configure", "object", "jadd", "create", "itemconfigure",
        "coords", "hit", "itemcget", "find", "move", "scale", "delete", "addtag",
        "bind", "raise", "lower", "dtag", "gettags", "canvasx", "canvasy",
        "copy", "index", "newtype", "bbox", "type", "zoom", "transformer", "hselect", "shapexy", "invshapexy", "save"
    };
    private static final int OPT_CGET = 0;
    private static final int OPT_CONFIGURE = 1;
    private static final int OPT_OBJECT = 2;
    private static final int OPT_JADD = 3;
    private static final int OPT_CREATE = 4;
    private static final int OPT_ITEMCONFIG = 5;
    private static final int OPT_COORDS = 6;
    private static final int OPT_HIT = 7;
    private static final int OPT_ITEMCGET = 8;
    private static final int OPT_FIND = 9;
    private static final int OPT_MOVE = 10;
    private static final int OPT_SCALE = 11;
    private static final int OPT_DELETE = 12;
    private static final int OPT_ADDTAG = 13;
    private static final int OPT_BIND = 14;
    private static final int OPT_RAISE = 15;
    private static final int OPT_LOWER = 16;
    private static final int OPT_DTAG = 17;
    private static final int OPT_GETTAGS = 18;
    private static final int OPT_CANVASX = 19;
    private static final int OPT_CANVASY = 20;
    private static final int OPT_COPY = 21;
    private static final int OPT_INDEX = 22;
    private static final int OPT_NEWTYPE = 23;
    private static final int OPT_BBOX = 24;
    private static final int OPT_TYPE = 25;
    private static final int OPT_ZOOM = 26;
    private static final int OPT_TRANSFORMER = 27;
    private static final int OPT_HSELECT = 28;
    private static final int OPT_SHAPEXY = 29;
    private static final int OPT_INVSHAPEXY = 30;
    private static final int OPT_SAVE = 31;
    static boolean gotDefaults = false;
    Map newTypes = new HashMap();
    Interp interp = null;

    /**
     *
     * @return
     */
    public static String[] getValidCmds() {
        return validCmds;
    }

    /**
     *
     * @param interp
     * @param argv
     * @throws TclException
     */
    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        int i;

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

        final SwkCanvas swkcanvas = (SwkCanvas) ReflectObject.get(interp, tObj);
        final SwkImageCanvas swkImageCanvas = swkcanvas.getSwkImageCanvas();

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkcanvas.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkcanvas.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkcanvas.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkcanvas.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkCanvas.resourceDB.get(argv[2].toString());
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
                    swkcanvas.configure(interp, argv, 2);
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
                create(interp, swkImageCanvas, argv);

                break;

            case OPT_ITEMCONFIG:
                if (argv.length < 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                if (argv.length == 3) {
                    interp.setResult((new SwkShapeItemGet(interp, swkImageCanvas,
                            argv[2].toString(), null)).exec(true));
                } else if (argv.length == 4) {
                    interp.setResult((new SwkShapeItemGet(interp, swkImageCanvas,
                            argv[2].toString(), argv[3].toString())).exec(true));
                } else {
                    SwkShape.config(interp, swkImageCanvas, argv, 3);
                }

                break;

            case OPT_INDEX:
                (new Index()).exec(interp, swkImageCanvas, argv);

                break;

            case OPT_ITEMCGET: {
                if (argv.length != 4) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult((new SwkShapeItemGet(interp, swkImageCanvas,
                        argv[2].toString(), argv[3].toString())).exec(false));

                break;
            }

            case OPT_COORDS:
                coords(interp, swkImageCanvas, argv);

                break;

            case OPT_HIT:
                (new Hit()).exec(interp, swkImageCanvas, argv);

                break;

            case OPT_FIND:
                (new Search()).exec(interp, swkImageCanvas, argv);

                break;

            case OPT_DELETE: {
                (new Delete()).exec(interp, swkImageCanvas, argv);

                break;
            }

            case OPT_RAISE:
                (new RaiseOrLower()).raise(interp, swkImageCanvas, argv);

                break;

            case OPT_LOWER:
                (new RaiseOrLower()).lower(interp, swkImageCanvas, argv);

                break;

            case OPT_MOVE: {
                (new Move()).exec(interp, swkImageCanvas, argv);

                break;
            }

            case OPT_DTAG: {
                (new DTags()).exec(interp, swkImageCanvas, argv);

                break;
            }

            case OPT_ADDTAG: {
                (new AddTag()).exec(interp, swkImageCanvas, argv);

                break;
            }

            case OPT_GETTAGS:
                (new GetTags()).exec(interp, swkImageCanvas, argv);

                break;

            case OPT_BIND:

                if (argv.length < 4) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                SwkBinding binding = SwkBind.getBinding(interp, argv, 3);

                if (binding != null) {
                    SwkBind.updateBindingCommand(interp, binding, argv, 3);
                    swkcanvas.setupBinding(interp, binding, argv[2].toString());
                }

                break;

            case OPT_SCALE: {
                (new Scale()).exec(interp, swkImageCanvas, argv);

                break;
            }

            case OPT_ZOOM:
                (new Zoom()).exec(interp, swkImageCanvas, argv);

                break;

            case OPT_CANVASY:
            case OPT_CANVASX:
                canvasXY(interp, swkcanvas, argv);

                break;

            case OPT_COPY: {
                (new Copy()).exec(interp, swkcanvas, argv);

                break;
            }

            case OPT_NEWTYPE: {
                newType(interp, argv);

                break;
            }

            case OPT_BBOX: {
                (new BBox()).exec(interp, swkImageCanvas, argv);

                break;
            }

            case OPT_TYPE: {
                (new Type()).exec(interp, swkImageCanvas, argv);

                break;
            }

            case OPT_TRANSFORMER: {
                if (argv.length < 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }
                if (argv[2].toString().equals("set")) {
                    (new TransformerSet()).exec(interp, swkImageCanvas, argv);
                } else if (argv[2].toString().equals("get")) {
                    (new TransformerGet()).exec(interp, swkImageCanvas, argv);
                } else if (argv[2].toString().equals("switch")) {
                    (new TransformerSwitch()).exec(interp, swkImageCanvas, argv);
                } else {
                    throw new TclException(interp, "Invalid transformer subcommand \"" + argv[2].toString() + "\"");
                }

                break;
            }
            case OPT_HSELECT: {
                (new HSelect()).exec(interp, swkImageCanvas, argv);

                break;
            }
            case OPT_SHAPEXY: {
                (new ShapeXY()).exec(interp, swkImageCanvas, argv, false);
                break;
            }
            case OPT_INVSHAPEXY: {
                (new ShapeXY()).exec(interp, swkImageCanvas, argv, true);
                break;
            }
            case OPT_SAVE: {
                if (argv.length < 3) {
                    throw new TclNumArgsException(interp, 2, argv,
                            "fileName ?owidth oheight?");
                }
                int owidth = 0;
                int oheight = 0;
                if (argv.length > 3) {
                    owidth = TclInteger.get(interp, argv[3]);
                }
                if (argv.length > 4) {
                    oheight = TclInteger.get(interp, argv[4]);
                }
                OutputStream oStream = null;
                Object object = null;
                try {
                    object = ReflectObject.get(interp, argv[2]);
                } catch (TclException tclE) {
                }
                if ((object != null)) {
                    oStream = (OutputStream) object;
                } else {
                    String fileName = argv[2].toString();
                    File file = new File(fileName);
                    try {
                        oStream = new FileOutputStream(file);
                    } catch (FileNotFoundException fE) {
                        throw new TclException(interp, fE.getMessage());
                    }
                }
                if (oStream != null) {
                    (new Save()).exec(interp, swkImageCanvas, owidth, oheight, oStream);
                } else {
                    System.out.println("null");

                    throw new TclException(interp, "Not an output stream or fle");
                }

                break;
            }


            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    /**
     *
     * @param interp
     * @param swkcanvas
     * @param tclObject
     * @throws TclException
     */
    public static void jadd(Interp interp, SwkCanvas swkcanvas,
            TclObject tclObject) throws TclException {
        int i;
        TclObject tObj = (TclObject) Widgets.getWidget(interp, tclObject.toString());

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

    int findCoords(Interp interp, TclObject[] argv, int start)
            throws TclException {
        int lastCoord = 0;

        for (int i = start; i < argv.length; i++) {
            lastCoord = i;

            if (argv[i].toString().startsWith("-")) {
                if (!Character.isDigit(argv[i].toString().charAt(1))) {
                    lastCoord = i - 1;

                    break;
                }
            }
        }

        return lastCoord;
    }

    double[] getCoords(Interp interp, SwkImageCanvas canvas, TclObject[] argv,
            int start, int lastCoord) throws TclException {
        int nCoords = lastCoord - start + 1;
        double[] coords = null;

        if (nCoords == 1) {
            TclObject[] coordList = TclList.getElements(interp, argv[start]);

            if ((coordList == null) || (coordList.length == 0)) {
                return null;
            }

            coords = new double[coordList.length];

            for (int i = 0; i < coords.length; i++) {
                coords[i] = SwankUtil.getTkSizeD(interp, canvas.getComponent(),
                        coordList[i]);
            }
        } else {
            coords = new double[nCoords];

            for (int i = start; i <= lastCoord; i++) {
                coords[i - start] = SwankUtil.getTkSizeD(interp,
                        canvas.getComponent(), argv[i]);
            }
        }

        return coords;
    }

    void coords(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        if (argv.length == 3) {
            (new CoordsGet()).exec(interp, swkcanvas, argv);
        } else {
            (new CoordsSet()).exec(interp, swkcanvas, argv);
        }
    }

    void create(final Interp interp, final SwkImageCanvas swkcanvas,
            final TclObject[] argv) throws TclException {
        final SwkShape swkShape;
        Vector shapeList = null;

        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv,
                    "type coords ?arg arg ...?");
        }

        if (argv.length < 4) {
            throw new TclNumArgsException(interp, 3, argv,
                    "coords ?arg arg ...?");
        }

        int lastCoord = findCoords(interp, argv, 3);
        double[] coordArray = getCoords(interp, swkcanvas, argv, 3, lastCoord);

        try {
            if (argv[2].toString().equals("arc")) {
                Arc2D arc2D = new Arc2D.Double();
                swkShape = new ItemArc(arc2D, swkcanvas);
            } else if (argv[2].toString().startsWith("rect")) {
                Rectangle2D rect2D = new Rectangle2D.Double();
                swkShape = new ItemRectangle(rect2D, swkcanvas);
            } else if (argv[2].toString().startsWith("node")) {
                Rectangle2D rect2D = new Rectangle2D.Double();
                swkShape = new ItemNode(rect2D, swkcanvas);
            } else if (argv[2].toString().equals("oval")) {
                Ellipse2D ellipse2D = new Ellipse2D.Double();
                swkShape = new ItemEllipse(ellipse2D, swkcanvas);
            } else if (argv[2].toString().equals("text")) {
                swkShape = new ItemText(null, swkcanvas);
            } else if (argv[2].toString().equals("htext")) {
                Rectangle2D rect2D = new Rectangle2D.Double();
                swkShape = new ItemHTML(rect2D, swkcanvas);
            } else if (argv[2].toString().equals("image")) {
                swkShape = new ItemImage(null, swkcanvas);
            } else if (argv[2].toString().equals("bitmap")) {
                swkShape = new ItemBitmap(null, swkcanvas);
            } else if (argv[2].toString().equals("window")) {
                swkShape = new ItemWindow(null, swkcanvas);
            } else if (argv[2].toString().equals("line")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new ItemLine(gPath, swkcanvas);
            } else if (argv[2].toString().equals("annotation")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new ItemAnnotation(gPath, swkcanvas);
            } else if (argv[2].toString().equals("connector")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new ItemConnector(gPath, swkcanvas);
            } else if (argv[2].toString().startsWith("sym")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new ItemSymbols(gPath, swkcanvas);
            } else if (argv[2].toString().startsWith("seg")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new ItemSegments(gPath, swkcanvas);
            } else if (argv[2].toString().startsWith("poly")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new ItemPolygon(gPath, swkcanvas);
            } else {
                CanvasType canvasType = (CanvasType) newTypes.get(argv[2].toString());

                if (canvasType == null) {
                    throw new TclException(interp,
                            "canvas type \"" + argv[2].toString()
                            + "\" doesn't exist");
                }

                Object newObject = null;

                try {
                    newObject = canvasType.myTypeClass.newInstance();
                } catch (InstantiationException iE) {
                    throw new TclException(interp,
                            "class " + argv[2].toString()
                            + " can't be instantiated");
                } catch (IllegalAccessException iaE) {
                    throw new TclException(interp,
                            "illegal access to class for object \""
                            + argv[2].toString() + "\"");
                }

                if (!(newObject instanceof SwkShape)) {
                    throw new TclException(interp,
                            "class for object \"" + argv[2].toString()
                            + "\" is not instance of SwkShape");
                }

                if (coordArray.length < canvasType.myNCoords) {
                    throw new TclException(interp,
                            "wrong # coordinates: expected at least 4, got "
                            + coordArray.length);
                }

                if ((coordArray.length % 2) != 0) {
                    throw new TclException(interp,
                            "wrong # coordinates: expected an even number, got "
                            + coordArray.length);
                }

                swkShape = (SwkShape) newObject;
                swkShape.setCanvas(swkcanvas);
            }

            if (swkShape != null) {
                swkShape.coords(swkcanvas, coordArray);
                swkShape.configShape(interp, swkcanvas, argv, lastCoord + 1);
                addShape(swkcanvas, swkShape); //swkcanvas.repaint();
                interp.setResult(swkShape.id);
            }
        } catch (SwkException swkE) {
            throw new TclException(interp, swkE.getMessage());
        }
    }

    void addShape(final SwkImageCanvas swkcanvas, final SwkShape swkShape) {
        swkShape.id = swkcanvas.lastShapeId;
        swkcanvas.lastShapeId++;

        (new AddShape()).exec(swkcanvas, swkShape);

        //  UpdateCmd.addRepaintRequest((JComponent) swkcanvas);
    }

    void canvasXY(Interp interp, SwkCanvas swkcanvas, TclObject[] argv)
            throws TclException {
        if ((argv.length != 3) && (argv.length != 4)) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        if (argv.length == 4) {
            double grid = SwankUtil.getTkSizeD(interp, (Component) swkcanvas,
                    argv[3]);
            double x = TclDouble.get(interp, argv[2]);
            interp.setResult((int) (Math.round(x / grid) * grid));
        } else {
            interp.setResult(TclDouble.get(interp, argv[2]));
        }
    }

    void newType(Interp interp, TclObject[] argv) throws TclException {
        if (argv.length != 5) {
            throw new TclNumArgsException(interp, 2, argv, "name class nCoords");
        }

        if (newTypes.containsKey(argv[2].toString())) {
            throw new TclException(interp,
                    "type \"" + argv[2].toString() + "\" already exists");
        }

        Class newClass = null;

        try {
            newClass = Class.forName(argv[3].toString());
        } catch (ClassNotFoundException cnfE) {
            throw new TclException(interp,
                    "class " + argv[3].toString() + " doesn't exist "
                    + cnfE.toString());
        }

        int nCoords = TclInteger.get(interp, argv[4]);
        CanvasType canvasType = new CanvasType(newClass, nCoords);
        newTypes.put(argv[2].toString(), canvasType);
    }

    static void getResult(Interp interp, ArrayList shapeList)
            throws TclException {
        TclObject list = TclList.newInstance();

        if (shapeList != null) {
            for (int j = 0; j < shapeList.size(); j++) {
                int id = ((Integer) shapeList.get(j)).intValue();
                TclList.append(interp, list, TclInteger.newInstance(id));
            }
        }

        interp.setResult(list);
    }

    private static class CoordsGet extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String tagName = "";
        double[] coords = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "item");
            }

            this.swkcanvas = swkcanvas;
            tagName = argv[2].toString();
            execOnThread();

            TclObject list = TclList.newInstance();

            if (coords != null) {
                for (int i = 0; i < coords.length; i++) {
                    TclList.append(interp, list,
                            TclDouble.newInstance(coords[i]));
                }
            }

            interp.setResult(list);
        }

        @Override
        public void run() {
            try {
                Vector shapes = swkcanvas.getShapesWithTags(tagName);

                if ((shapes != null) && (shapes.size() != 0)) {
                    SwkShape swkShape = (SwkShape) shapes.elementAt(0);

                    if (swkShape != null) {
                        coords = swkShape.coords();
                    }
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private class CoordsSet extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String tagName = "";
        double[] coords = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            this.swkcanvas = swkcanvas;
            tagName = argv[2].toString();

            int lastCoord = findCoords(interp, argv, 3);
            coords = getCoords(interp, swkcanvas, argv, 3, lastCoord);
            execOnThread();
        }

        @Override
        public void run() {
            try {
                Vector shapes = swkcanvas.getShapesWithTags(tagName);

                if ((shapes != null) && (shapes.size() != 0)) {
                    SwkShape swkShape = (SwkShape) shapes.elementAt(0);

                    if (swkShape != null) {
                        swkShape.coords(swkcanvas, coords);
                        swkcanvas.repaint();
                    }
                }
            } catch (SwkException swkE) {
                System.out.println("error in coordset");
            }
        }
    }

    private class RaiseOrLower extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        boolean raiseMode = false;
        String shapeArg = null;
        String relativeArg = null;

        void raise(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            raiseMode = true;
            doIt(interp, swkcanvas, argv);
        }

        void lower(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            raiseMode = false;
            doIt(interp, swkcanvas, argv);
        }

        void doIt(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            this.swkcanvas = swkcanvas;

            if ((argv.length != 3) && (argv.length != 4)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            shapeArg = argv[2].toString();

            if (argv.length == 4) {
                relativeArg = argv[3].toString();
            }

            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                if (raiseMode) {
                    swkcanvas.raise(shapeArg, relativeArg);
                } else {
                    swkcanvas.lower(shapeArg, relativeArg);
                }
            } catch (SwkException swkE) {
                interp.addErrorInfo(swkE.getMessage());
                interp.backgroundError();
            }
        }
    }

    private static class Hit extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        double x = 0;
        double y = 0;
        String tagName = "";
        String result = "";

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 2, argv, "item x y");
            }

            this.swkcanvas = swkcanvas;
            tagName = argv[2].toString();
            x = TclDouble.get(interp, argv[3]);
            y = TclDouble.get(interp, argv[4]);
            execOnThread();
            interp.setResult(result);
        }

        @Override
        public void run() {
            try {
                SwkShape swkShape = (SwkShape) swkcanvas.getShape(tagName);

                if (swkShape != null) {
                    result = swkShape.hit(x, y);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class Save extends GetValueOnEventThread {

        SwkImageCanvas swkImageCanvas = null;
        int width = 0;
        int height = 0;
        OutputStream oStream = null;

        void exec(Interp interp, SwkImageCanvas swkImageCanvas, int width, int height, OutputStream oStream)
                throws TclException {
            this.swkImageCanvas = swkImageCanvas;
            this.width = width;
            this.height = height;
            this.oStream = oStream;
            execOnThread();
        }

        @Override
        public void run() {

            swkImageCanvas.save(width, height, oStream);
        }
    }

    private static class Type extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String tagName = "";
        String result = "";

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            this.swkcanvas = swkcanvas;

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "tagOrId");
            }

            tagName = argv[2].toString();
            execOnThread();
            interp.setResult(result);
        }

        @Override
        public void run() {
            try {
                Vector shapes = swkcanvas.getShapesWithTags(tagName);

                if ((shapes != null) && (shapes.size() != 0)) {
                    SwkShape swkShape = (SwkShape) shapes.elementAt(0);

                    if (swkShape != null) {
                        result = swkShape.getType();
                    }
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class BBox extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String[] tags = null;
        Rectangle2D rect = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            this.swkcanvas = swkcanvas;

            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv,
                        "tagOrId ?tagOrId tagOrId ...?");
            }

            tags = SwankUtil.argvToStrings(argv, 2);
            rect = null;
            execOnThread();

            if (rect != null) {
                TclObject list = TclList.newInstance();
                TclList.append(interp, list, TclDouble.newInstance(rect.getX()));
                TclList.append(interp, list, TclDouble.newInstance(rect.getY()));
                TclList.append(interp, list,
                        TclDouble.newInstance(rect.getX() + rect.getWidth()));
                TclList.append(interp, list,
                        TclDouble.newInstance(rect.getY() + rect.getHeight()));
                interp.setResult(list);
            } else {
                interp.setResult("");
            }
        }

        @Override
        public void run() {
            try {
                rect = swkcanvas.getShapeBounds(tags);
            } catch (SwkException swkE) {
            }
        }
    }

    private static class Scale extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        double xOrigin = 0;
        double yOrigin = 0;
        double xScale = 0;
        double yScale = 0;
        String tagName = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 7) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            tagName = argv[2].toString();
            this.swkcanvas = swkcanvas;
            xOrigin = TclDouble.get(interp, argv[3]);
            yOrigin = TclDouble.get(interp, argv[4]);
            xScale = TclDouble.get(interp, argv[5]);
            yScale = TclDouble.get(interp, argv[6]);
            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(tagName);

                for (int i = 0; i < shapeList.size(); i++) {
                    SwkShape swkShape2 = (SwkShape) shapeList.elementAt(i);
                    swkShape2.scale(xOrigin, yOrigin, xScale, yScale);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class Zoom extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        double zoom = 0;
        boolean setValue = false;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if ((argv.length != 2) && (argv.length != 3)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            this.swkcanvas = swkcanvas;

            if (argv.length == 3) {
                zoom = TclDouble.get(interp, argv[2]);
                setValue = true;
            }

            execOnThread();

            interp.setResult(zoom);

            // FIXME swkcanvas.revalidate();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            if (setValue) {
                swkcanvas.setZoom(zoom);
            }

            zoom = swkcanvas.getZoom();
        }
    }

    private static class Move extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        double dX = 0;
        double dY = 0;
        String tagName = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            this.swkcanvas = swkcanvas;
            tagName = argv[2].toString();
            dX = SwankUtil.getTkSizeD(interp, swkcanvas.getComponent(), argv[3]);
            dY = SwankUtil.getTkSizeD(interp, swkcanvas.getComponent(), argv[4]);
            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(tagName);

                for (int i = 0; i < shapeList.size(); i++) {
                    SwkShape swkShape2 = (SwkShape) shapeList.elementAt(i);
                    AffineTransform shpTrans = swkShape2.getTransform();

                    if (shpTrans != null) {
                        double scaleX = shpTrans.getScaleX();
                        double scaleY = shpTrans.getScaleY();
                        double shpdX = dX / scaleX;
                        double shpdY = dY / scaleY;
                        swkShape2.move(shpdX, shpdY);
                    } else {
                        swkShape2.move(dX, dY);
                    }
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class ShapeXY extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        double[] xy;
        String tagName = null;
        boolean inverted = false;
        boolean couldNotInvert = false;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv, boolean inverted)
                throws TclException {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            this.swkcanvas = swkcanvas;
            this.inverted = inverted;
            tagName = argv[2].toString();
            TclObject[] coordList = TclList.getElements(interp, argv[3]);
            int nCoords = coordList.length;
            if ((nCoords <= 0) || ((nCoords % 2) != 0)) {
                throw new TclException(interp, "shapexy must have even number of coordinates");
            }
            xy = new double[nCoords];
            for (int i = 0; i < nCoords; i++) {
                xy[i] = TclDouble.get(interp, coordList[i]);
            }
            execOnThread();
            if (couldNotInvert) {
                throw new TclException(interp, "Couldn't do inverse transform for shape  \"" + tagName + "\"");
            }
            if (xy == null) {
                throw new TclException(interp, "shape \"" + tagName + "\" doesn't exist");
            }
            TclObject resultList = TclList.newInstance();
            for (int i = 0; i < xy.length; i++) {
                TclList.append(interp, resultList, TclDouble.newInstance(xy[i]));
            }
            interp.setResult(resultList);
        }

        @Override
        public void run() {
            try {
                SwkShape swkShape = (SwkShape) swkcanvas.getShape(tagName);
                if (swkShape != null) {
                    AffineTransform shpTrans = swkShape.getTransform();
                    if (shpTrans != null) {
                        if (inverted) {
                            try {
                                shpTrans.inverseTransform(xy, 0, xy, 0, xy.length / 2);
                            } catch (NoninvertibleTransformException niTE) {
                                couldNotInvert = true;
                            }
                        } else {
                            shpTrans.transform(xy, 0, xy, 0, xy.length / 2);
                        }
                    }
                } else {
                    xy = null;
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class TransformerSwitch extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String tagName = null;
        String transformName = null;
        boolean couldNotInvert = false;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 2, argv, "tag transform");
            }
            tagName = argv[3].toString();
            transformName = argv[4].toString();
            this.swkcanvas = swkcanvas;
            execOnThread();
            if (couldNotInvert) {
                throw new TclException(interp, "Couldn't do inverse transform for shape  \"" + tagName + "\"");
            }
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                SwkShape swkShape = (SwkShape) swkcanvas.getShape(tagName);
                if (swkShape != null) {
                    AffineTransform shpTrans = swkShape.getTransform();
                    double[] coords = swkShape.coords();
                    if (shpTrans != null) {
                        shpTrans.transform(coords, 0, coords, 0, coords.length / 2);
                    }
                    swkcanvas.setTransformer(transformName, swkShape);
                    shpTrans = swkShape.getTransform();
                    if (shpTrans != null) {
                        try {
                            shpTrans.inverseTransform(coords, 0, coords, 0, coords.length / 2);
                        } catch (NoninvertibleTransformException niTE) {
                            couldNotInvert = true;
                        }
                    }
                    swkShape.coords(swkcanvas, coords);

                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class HSelect extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        boolean setValue = false;
        String tagName = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            this.swkcanvas = swkcanvas;
            tagName = argv[2].toString();
            setValue = TclBoolean.get(interp, argv[3]);
            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(tagName);

                for (int i = 0; i < shapeList.size(); i++) {
                    SwkShape swkShape2 = (SwkShape) shapeList.elementAt(i);
                    swkShape2.setSelected(setValue);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class GetTags extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        ArrayList tagList = null;
        String tagName = "";

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            this.swkcanvas = swkcanvas;

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            tagName = argv[2].toString();
            execOnThread();

            TclObject list = TclList.newInstance();

            if (tagList != null) {
                for (int i = 0, n = tagList.size(); i < n; i++) {
                    TclList.append(interp, list,
                            TclString.newInstance((String) tagList.get(i)));
                }
            }

            interp.setResult(list);
        }

        @Override
        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(tagName);

                if (shapeList.size() > 0) {
                    SwkShape swkShape = (SwkShape) shapeList.elementAt(0);
                    tagList = swkcanvas.getTags(swkShape);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class Index extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String tagName = "";
        int index = 0;
        SwkShape swkShape = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            this.swkcanvas = swkcanvas;

            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "tagOrId arg");
            }

            tagName = argv[2].toString();

            if (swkShape != null) {
                index = swkShape.getIndex(interp, argv[3]);
            }

            execOnThread();
            interp.setResult(index);
        }

        @Override
        public void run() {
            try {
                Vector indexShapeList = swkcanvas.getShapesWithTags(tagName);

                if (indexShapeList.size() == 0) {
                    return;
                }

                swkShape = (SwkShape) indexShapeList.elementAt(0);
            } catch (SwkException swkE) {
            }
        }
    }

    private static class Delete extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String[] tags = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            tags = SwankUtil.argvToStrings(argv, 2);
            this.swkcanvas = swkcanvas;
            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                swkcanvas.delete(tags);
            } catch (SwkException swkE) {
            }
        }
    }

    private static class Copy extends UpdateOnEventThread {

        SwkCanvas swkcanvas = null;

        void exec(Interp interp, SwkCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            this.swkcanvas = swkcanvas;
            execOnThread();
        }

        @Override
        public void run() {
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            swkcanvas.copyImageToClipboard(clipboard);
        }
    }

    private static class DTags extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String tagName = null;
        String[] tagStrings = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if ((argv.length != 3) && (argv.length != 4)) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            this.swkcanvas = swkcanvas;
            tagName = argv[2].toString();

            TclObject[] tagList = TclList.getElements(interp,
                    argv[argv.length - 1]);
            tagStrings = SwankUtil.argvToStrings(tagList, 0);
            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(tagName);

                for (int i = 0; i < shapeList.size(); i++) {
                    SwkShape swkShape2 = (SwkShape) shapeList.elementAt(i);
                    swkcanvas.removeTags(tagStrings, false, swkShape2);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class AddTag extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        String[] args = null;
        String tagString = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            this.swkcanvas = swkcanvas;
            tagString = argv[2].toString();
            args = SwankUtil.argvToStrings(argv, 3);
            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(args);
                String[] tagStrings = {tagString};

                for (int i = 0; i < shapeList.size(); i++) {
                    SwkShape swkShape2 = (SwkShape) shapeList.elementAt(i);
                    swkcanvas.addTags(tagStrings, swkShape2);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    private static class TransformerSet extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        double[] values = null;
        String name = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 10) {
                throw new TclNumArgsException(interp, 2, argv,
                        "transformerName m00 m10 m01 m11 m02 m12");
            }

            this.swkcanvas = swkcanvas;

            values = new double[6];

            for (int i = 4; i < 9; i++) {
                values[i - 4] = TclDouble.get(interp, argv[i]);
            }

            name = argv[3].toString();
            execOnThread();
            swkcanvas.repaint();
        }

        @Override
        public void run() {
            Transformer transformer = swkcanvas.getTransformer(name);

            if (transformer == null) {
                transformer = swkcanvas.setTransformer(name, null);
            }

            transformer.getTransform().setTransform(values[0], values[1],
                    values[2], values[3], values[4], values[5]);
        }
    }

    private static class TransformerGet extends GetValueOnEventThread {

        SwkImageCanvas swkcanvas = null;
        double[] values = null;
        String name = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv,
                        "transformerName");
            }
            this.swkcanvas = swkcanvas;
            name = argv[3].toString();
            execOnThread();
            if (values != null) {
                TclObject transformList = TclList.newInstance();
                for (int i = 0; i < values.length; i++) {
                    TclList.append(interp, transformList,
                            TclDouble.newInstance(values[i]));
                }
                interp.setResult(transformList);
            } else {
                throw new TclException(interp, "Transform \"" + name + "\" doesn't exist");
            }

        }

        @Override
        public void run() {
            Transformer transformer = swkcanvas.getTransformer(name);
            if (transformer != null) {
                values = new double[6];
                transformer.getTransform().getMatrix(values);
            }
        }
    }

    private static class CanvasType {

        Class myTypeClass = null;
        int myNCoords = 0;

        CanvasType(Class typeClass, int nCoords) {
            myTypeClass = typeClass;
            myNCoords = nCoords;
        }
    }

    private static class AddShape extends UpdateOnEventThread {

        SwkImageCanvas swkcanvas = null;
        SwkShape swkShape = null;

        void exec(final SwkImageCanvas swkcanvas, final SwkShape swkShape) {
            this.swkcanvas = swkcanvas;
            this.swkShape = swkShape;
            execOnThread();
        }

        @Override
        public void run() {
            try {
                swkcanvas.addShape(swkShape);
            } catch (SwkException swkE) {
                System.out.println("error adding shape " + swkE.getMessage());
            }

        }
    }

    private static class Search extends GetValueOnEventThread {

        public static final int NEXT = 0;
        public static final int PREVIOUS = 1;
        public static final int ALL = 2;
        public static final int ENCLOSED = 3;
        public static final int OVERLAPPING = 4;
        public static final int WITHTAG = 5;
        public static final int CLOSEST = 6;
        SwkImageCanvas swkcanvas = null;
        String tagName = null;
        SwkShape swkShape = null;
        SwkShape bestShape = null;
        ArrayList shapeList = null;
        String refTag = "";
        int mode = NEXT;
        Point2D pt = null;
        float halo = 0.0f;
        Rectangle2D rect = null;

        void exec(Interp interp, SwkImageCanvas swkcanvas, TclObject[] argv)
                throws TclException {
            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            this.swkcanvas = swkcanvas;

            int start = 2;

            if (argv.length < (start + 1)) {
                throw new TclNumArgsException(interp, 0, argv, "option");
            }

            if (argv[start].toString().equals("above")) {
                if (argv.length != (start + 2)) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }
                tagName = argv[start + 1].toString();
                interp.resetResult();
                mode = NEXT;
                execOnThread();

                if (swkShape != null) {
                    interp.setResult(swkShape.id);
                }
            } else if (argv[start].toString().equals("all")) {
                if (argv.length != (start + 1)) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                mode = ALL;
                execOnThread();
                getResult(interp, shapeList);
            } else if (argv[start].toString().equals("below")) {
                if (argv.length != (start + 2)) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                if (argv.length != (start + 2)) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                tagName = argv[start + 1].toString();
                interp.resetResult();
                mode = PREVIOUS;
                execOnThread();

                if (swkShape != null) {
                    interp.setResult(swkShape.id);
                }
            } else if (argv[start].toString().equals("closest")) {
                if ((argv.length < (start + 3)) || (argv.length > (start + 5))) {
                    throw new TclNumArgsException(interp, 1, argv,
                            "x y ?halo? ?start?");
                }

                float x1 = (float) TclDouble.get(interp, argv[start + 1]);
                float y1 = (float) TclDouble.get(interp, argv[start + 2]);

                refTag = null;

                if (argv.length > (start + 3)) {
                    halo = (float) TclDouble.get(interp, argv[start + 3]);

                    if (halo < 0.0) {
                        throw new TclException(interp,
                                "closest: halo cannot be less than 0.0");
                    }

                    if (argv.length > (start + 4)) {
                        refTag = argv[start + 4].toString();
                    }
                }

                pt = new Point2D.Float(x1, y1);
                mode = CLOSEST;
                execOnThread();

                if (bestShape == null) {
                    interp.setResult("");
                } else {
                    interp.setResult(bestShape.id);
                }
            } else if (argv[start].toString().equals("enclosed")
                    || argv[start].toString().equals("overlapping")) {
                Rectangle2D bounds = null;

                if (argv.length != (start + 5)) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                float x1 = (float) TclDouble.get(interp, argv[start + 1]);
                float y1 = (float) TclDouble.get(interp, argv[start + 2]);
                float x2 = (float) TclDouble.get(interp, argv[start + 3]);
                float y2 = (float) TclDouble.get(interp, argv[start + 4]);

                if (x1 > x2) {
                    throw new TclException(interp,
                            "enclosed: x1 must be less than x2");
                }

                if (y1 > y2) {
                    throw new TclException(interp,
                            "enclosed: y1 must be less than y2");
                }

                rect = new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
                mode = ENCLOSED;

                if (argv[start].toString().equals("overlapping")) {
                    mode = OVERLAPPING;
                }

                execOnThread();
                getResult(interp, shapeList);
            } else if (argv[start].toString().equals("withtag")) {
                if (argv.length != (start + 2)) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                refTag = argv[start + 1].toString();
                mode = WITHTAG;
                execOnThread();
                getResult(interp, shapeList);
            }
        }

        @Override
        public void run() {
            try {
                switch (mode) {
                    case NEXT:
                    case PREVIOUS:
                        getOne();

                        break;

                    case ALL:
                        getSome();

                        break;

                    case CLOSEST:
                        getClosest();

                        break;

                    case ENCLOSED:
                    case OVERLAPPING:
                        getRectShapes();

                        break;

                    case WITHTAG:
                        withTag();

                        break;

                    default:
                }
            } catch (SwkException swkE) {
            }
        }

        void getOne() throws SwkException {
            swkShape = (SwkShape) swkcanvas.getShape(tagName);
            if (swkShape != null) {
                ItemTreeNode node = swkShape.node;
                if (mode == NEXT) {
                    ItemTreeNode nextNode = (ItemTreeNode) node.getNextNode();
                    if (nextNode != null) {
                        swkShape = (SwkShape) nextNode.getUserObject();
                    }
                } else {
                    ItemTreeNode previousNode = (ItemTreeNode) node.getPreviousNode();
                    if (previousNode != null) {
                        swkShape = (SwkShape) previousNode.getUserObject();
                    }
                }
            } else {
            }
        }

        void getSome() {
            shapeList = new ArrayList();
            for (Enumeration e = swkcanvas.rootNode.depthFirstEnumeration(); e.hasMoreElements();) {
                ItemTreeNode node = (ItemTreeNode) e.nextElement();
                SwkShape nodeShape = (SwkShape) node.getUserObject();
                if (nodeShape != null) {
                    shapeList.add(Integer.valueOf(nodeShape.id));
                }
            }
        }

        void withTag() throws SwkException {
            Vector shapes = swkcanvas.getShapesWithTags(refTag);
            shapeList = new ArrayList();

            for (int i = 0, n = shapes.size(); i < n; i++) {
                shapeList.add(Integer.valueOf(((SwkShape) shapes.elementAt(i)).id));
            }
        }

        void getClosest() throws SwkException {
            bestShape = null;

            SwkShape startShape = null;
            Rectangle2D bounds = null;
            double max = Double.MAX_VALUE;
            double[] tcoords = new double[6];
            double tx1 = 0.0;
            double ty1 = 0.0;
            double tx2 = 0.0;
            double ty2 = 0.0;

            boolean below = false;

            if (refTag != null) {
                Vector shapes = swkcanvas.getShapesWithTags(refTag);

                if (shapes != null) {
                    startShape = (SwkShape) shapes.elementAt(0);
                }
            }
            boolean gotStart = false;
            double fillHit = 5.0;
            for (Enumeration e = swkcanvas.rootNode.reverseDepthFirstEnumeration(); e.hasMoreElements();) {
                ItemTreeNode node = (ItemTreeNode) e.nextElement();
                SwkShape nodeShape = (SwkShape) node.getUserObject();
                if (nodeShape == null) {
                    continue;
                }
                if (!gotStart && (startShape != null)) {
                    continue;
                }
                if (nodeShape == startShape) {
                    gotStart = true;
                    continue;
                }
                bounds = null;

                if (nodeShape.shape == null) {
                    if (nodeShape.hitShape(pt.getX(), pt.getY())) {
                        double dis = fillHit;
                        if (dis < max) {
                            max = dis;
                            bestShape = nodeShape;
                        }
                    }
                } else {
                    if ((nodeShape.fill != null) || (nodeShape.fillGradient != null) || (nodeShape.texturePaint != null)) {
// fixme should use shape.contains for some shapes
                        bounds = nodeShape.shape.getBounds2D();

                        if (halo != 0.0f) {
                            bounds.setRect(bounds.getX() - halo,
                                    bounds.getY() - halo, bounds.getWidth() + halo,
                                    bounds.getHeight() + halo);
                        }

                        if (bounds.contains(pt)) {
                            double dis = fillHit;
                            if (dis < max) {
                                max = dis;
                                bestShape = nodeShape;
                            }
                        }
                    } else {
                        PathIterator pI = nodeShape.shape.getPathIterator(null);
                        boolean intersects = false;
                        double dis2 = Double.MAX_VALUE;

                        while (!pI.isDone()) {
                            int type = pI.currentSegment(tcoords);

                            if (type == PathIterator.SEG_LINETO) {
                                tx2 = tcoords[0];
                                ty2 = tcoords[1];
                                dis2 = Line2D.ptSegDistSq(tx1, ty1, tx2, ty2,
                                        pt.getX(), pt.getY());
//System.out.println(dis2+" "+halo+" "+tx1+" "+ty1+" "+tx2+" "+ty2);
                                tx1 = tx2;
                                ty1 = ty2;

                                if (dis2 <= halo) {
                                    intersects = true;

                                    break;
                                }
                            } else if (type == PathIterator.SEG_MOVETO) {
                                tx1 = tcoords[0];
                                ty1 = tcoords[1];
                            }

                            pI.next();
                        }

                        if (!intersects) {
                            continue;
                        }

                        double dis = Math.sqrt(dis2);
//System.out.println(dis+" "+max);
                        if (dis < max) {
                            max = dis;
                            bestShape = nodeShape;
                        }
                    }

                }
//System.out.println(swkShape.id+" "+max);
            }
        }

        void getRectShapes() {
            Enumeration e = swkcanvas.swkShapes.elements();

            while (e.hasMoreElements()) {
                swkShape = (SwkShape) e.nextElement();
                Rectangle2D bounds = swkcanvas.getShapeBounds(swkShape);
                if (bounds != null) {
                    if (((mode == ENCLOSED) && rect.contains(bounds))
                            || ((mode == OVERLAPPING) && rect.intersects(bounds))) {
                        if (shapeList == null) {
                            shapeList = new ArrayList();
                        }

                        shapeList.add(Integer.valueOf(swkShape.id));
                    }
                }
            }
        }
    }
}
