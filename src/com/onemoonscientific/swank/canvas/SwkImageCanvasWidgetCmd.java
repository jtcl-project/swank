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

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.font.*;
import java.awt.geom.*;

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;


public class SwkImageCanvasWidgetCmd implements Command {
    static final private String[] validCmds = {
        "create", "itemconfigure", "coords", "hit", "itemcget", "find", "move",
        "scale", "delete", "addtag", "raise", "lower", "dtag", "gettags",
        "index", "newtype", "bbox", "type", "zoom", "transformer", "paint"
    };
    static final private int OPT_CREATE = 0;
    static final private int OPT_ITEMCONFIG = 1;
    static final private int OPT_COORDS = 2;
    static final private int OPT_HIT = 3;
    static final private int OPT_ITEMCGET = 4;
    static final private int OPT_FIND = 5;
    static final private int OPT_MOVE = 6;
    static final private int OPT_SCALE = 7;
    static final private int OPT_DELETE = 8;
    static final private int OPT_ADDTAG = 9;
    static final private int OPT_RAISE = 10;
    static final private int OPT_LOWER = 11;
    static final private int OPT_DTAG = 12;
    static final private int OPT_GETTAGS = 13;
    static final private int OPT_INDEX = 14;
    static final private int OPT_NEWTYPE = 15;
    static final private int OPT_BBOX = 16;
    static final private int OPT_TYPE = 17;
    static final private int OPT_ZOOM = 18;
    static final private int OPT_TRANSFORMER = 19;
    static final private int OPT_PAINT = 20;
    static boolean gotDefaults = false;
    static SwkImageCanvas swkImageCanvas = null;
    Map newTypes = new HashMap();
    Interp interp = null;

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        this.interp = interp;

        final int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        /*
                final TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[0].toString());

                if (tObj == null) {
                    throw new TclException(interp,
                        "bad window path name \"" + argv[0].toString() + "\"");
                }
        */

        //final SwkImageCanvas swkImageCanvas = (SwkImageCanvas) ReflectObject.get(interp, tObj);
        if (swkImageCanvas == null) {
            swkImageCanvas = new SwkImageCanvas(interp, "swkimagecanvas",
                    "SwkImageCanvas");
            swkImageCanvas.className = "SwkImageCanvas";
        }

        switch (opt) {
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

        case OPT_SCALE: {
            (new Scale()).exec(interp, swkImageCanvas, argv);

            break;
        }

        case OPT_ZOOM:
            (new Zoom()).exec(interp, swkImageCanvas, argv);

            break;

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
            (new TransformerSet()).exec(interp, swkImageCanvas, argv);

            break;
        }

        case OPT_PAINT: {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 2, argv,
                    "width height fileName");
            }

            int width = TclInteger.get(interp, argv[2]);
            int height = TclInteger.get(interp, argv[3]);
            String fileName = argv[4].toString();
            swkImageCanvas.paint(width, height, fileName);

            break;
        }

        default:
            throw new TclRuntimeError("TclIndex.get() error");
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
                swkShape = new SwkArc(arc2D, swkcanvas);
            } else if (argv[2].toString().startsWith("rect")) {
                Rectangle2D rect2D = new Rectangle2D.Double();
                swkShape = new SwkRectangle(rect2D, swkcanvas);
            } else if (argv[2].toString().equals("oval")) {
                Ellipse2D ellipse2D = new Ellipse2D.Double();
                swkShape = new SwkEllipse(ellipse2D, swkcanvas);
            } else if (argv[2].toString().equals("text")) {
                swkShape = new SwkCanvText(null, swkcanvas);
            } else if (argv[2].toString().equals("image")) {
                swkShape = new SwkCanvImage(null, swkcanvas);
            } else if (argv[2].toString().equals("bitmap")) {
                swkShape = new SwkCanvBitmap(null, swkcanvas);
            } else if (argv[2].toString().equals("line")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new SwkLine(gPath, swkcanvas);
            } else if (argv[2].toString().startsWith("sym")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new SwkSymbols(gPath, swkcanvas);
            } else if (argv[2].toString().startsWith("seg")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new SwkSegments(gPath, swkcanvas);
            } else if (argv[2].toString().startsWith("poly")) {
                GeneralPath gPath = new GeneralPath();
                swkShape = new SwkPolygon(gPath, swkcanvas);
            } else {
                CanvasType canvasType = (CanvasType) newTypes.get(argv[2].toString());

                if (canvasType == null) {
                    throw new TclException(interp,
                        "canvas type \"" + argv[2].toString() +
                        "\" doesn't exist");
                }

                Object newObject = null;

                try {
                    newObject = canvasType.myTypeClass.newInstance();
                } catch (InstantiationException iE) {
                    throw new TclException(interp,
                        "class " + argv[2].toString() +
                        " can't be instantiated");
                } catch (IllegalAccessException iaE) {
                    throw new TclException(interp,
                        "illegal access to class for object \"" +
                        argv[2].toString() + "\"");
                }

                if (!(newObject instanceof SwkShape)) {
                    throw new TclException(interp,
                        "class for object \"" + argv[2].toString() +
                        "\" is not instance of SwkShape");
                }

                if (coordArray.length < canvasType.myNCoords) {
                    throw new TclException(interp,
                        "wrong # coordinates: expected at least 4, got " +
                        coordArray.length);
                }

                if ((coordArray.length % 2) != 0) {
                    throw new TclException(interp,
                        "wrong # coordinates: expected an even number, got " +
                        coordArray.length);
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

        swkcanvas.repaint(50);
    }

    void addShape(final SwkImageCanvas swkcanvas, final SwkShape swkShape) {
        swkShape.id = swkcanvas.lastShapeId;
        swkcanvas.lastShapeId++;

        (new AddShape()).exec(swkcanvas, swkShape);

        //  UpdateCmd.addRepaintRequest((JComponent) swkcanvas);
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
                "class " + argv[3].toString() + " doesn't exist " +
                cnfE.toString());
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

    class CoordsGet extends GetValueOnEventThread {
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

    class CoordsSet extends UpdateOnEventThread {
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

    class RaiseOrLower extends UpdateOnEventThread {
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

    class Hit extends GetValueOnEventThread {
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
            x = TclDouble.get(interp, argv[3]);
            y = TclDouble.get(interp, argv[4]);
            tagName = argv[4].toString();
            execOnThread();
            interp.setResult(result);
        }

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

    class Type extends GetValueOnEventThread {
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

    class BBox extends GetValueOnEventThread {
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

        public void run() {
            try {
                rect = swkcanvas.getShapeBounds(tags);
            } catch (SwkException swkE) {
            }
        }
    }

    class Scale extends UpdateOnEventThread {
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

    class Zoom extends GetValueOnEventThread {
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

        public void run() {
            if (setValue) {
                swkcanvas.setZoom(zoom);
            }

            zoom = swkcanvas.getZoom();
        }
    }

    class Move extends GetValueOnEventThread {
        SwkImageCanvas swkcanvas = null;
        double dX = 0;
        double dY = 0;
        boolean setValue = false;
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

        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(tagName);

                for (int i = 0; i < shapeList.size(); i++) {
                    SwkShape swkShape2 = (SwkShape) shapeList.elementAt(i);
                    swkShape2.move(dX, dY);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    class GetTags extends GetValueOnEventThread {
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

    class Index extends GetValueOnEventThread {
        SwkImageCanvas swkcanvas = null;
        Vector shapeList = null;
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

        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(tagName);

                if (shapeList.size() == 0) {
                    return;
                }

                swkShape = (SwkShape) shapeList.elementAt(0);
            } catch (SwkException swkE) {
            }
        }
    }

    class Delete extends UpdateOnEventThread {
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

        public void run() {
            try {
                swkcanvas.delete(tags);
            } catch (SwkException swkE) {
            }
        }
    }

    class DTags extends UpdateOnEventThread {
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

    class AddTag extends UpdateOnEventThread {
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

        public void run() {
            try {
                Vector shapeList = swkcanvas.getShapesWithTags(args);
                String[] tagStrings = { tagString };

                for (int i = 0; i < shapeList.size(); i++) {
                    SwkShape swkShape2 = (SwkShape) shapeList.elementAt(i);
                    swkcanvas.addTags(tagStrings, swkShape2);
                }
            } catch (SwkException swkE) {
            }
        }
    }

    class TransformerSet extends UpdateOnEventThread {
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

            double[] values = new double[6];

            for (int i = 4; i < 10; i++) {
                values[i - 4] = TclDouble.get(interp, argv[i]);
            }

            name = argv[3].toString();
            execOnThread();
            swkcanvas.repaint();
        }

        public void run() {
            Transformer transformer = swkcanvas.getTransformer(name);

            if (transformer == null) {
                transformer = swkcanvas.setTransformer(name, null);
            }

            transformer.getTransform().setTransform(values[0], values[1],
                values[2], values[3], values[4], values[5]);
        }
    }

    class CanvasType {
        Class myTypeClass = null;
        int myNCoords = 0;

        CanvasType(Class typeClass, int nCoords) {
            myTypeClass = typeClass;
            myNCoords = nCoords;
        }
    }

    class AddShape extends UpdateOnEventThread {
        SwkImageCanvas swkcanvas = null;
        SwkShape swkShape = null;

        void exec(final SwkImageCanvas swkcanvas, final SwkShape swkShape) {
            this.swkcanvas = swkcanvas;
            this.swkShape = swkShape;
            execOnThread();
        }

        public void run() {
            try {
                swkcanvas.addShape(swkShape);
            } catch (SwkException swkE) {
                System.out.println("error adding shape " + swkE.getMessage());
            }

            swkcanvas.repaint();
        }
    }

    class Search extends GetValueOnEventThread {
        public static final int NEXT = 0;
        public static final int PREVIOUS = 1;
        public static final int ALL = 2;
        public static final int ENCLOSED = 3;
        public static final int OVERLAPPING = 4;
        public static final int WITHTAG = 5;
        public static final int CLOSEST = 6;
        SwkImageCanvas swkcanvas = null;
        int iSearch = 0;
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

            SwkShape swkShape = null;
            SwkShape nextShape = null;
            int start = 2;

            if (argv.length < (start + 1)) {
                throw new TclNumArgsException(interp, 0, argv, "option");
            }

            if (argv[start].toString().equals("above")) {
                if (argv.length != (start + 2)) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                iSearch = TclInteger.get(interp, argv[start + 1]);
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

                iSearch = TclInteger.get(interp, argv[start + 1]);
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

                refTag = "";

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
            } else if (argv[start].toString().equals("enclosed") ||
                    argv[start].toString().equals("overlapping")) {
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

                default:}
            } catch (SwkException swkE) {
            }
        }

        void getOne() throws SwkException {
            swkShape = (SwkShape) swkcanvas.swkShapes.get(new Integer(iSearch));

            if (swkShape != null) {
                if (mode == NEXT) {
                    swkShape = swkShape.next;
                } else {
                    swkShape = swkShape.previous;
                }
            }
        }

        void getSome() {
            SwkShape swkShape = swkcanvas.firstShape;
            SwkShape nextShape = swkcanvas.firstShape;

            if (swkcanvas.firstShape != null) {
                shapeList = new ArrayList();

                while (nextShape != null) {
                    swkShape = nextShape;
                    nextShape = swkShape.next;
                    shapeList.add(new Integer(swkShape.id));
                }
            }
        }

        void withTag() throws SwkException {
            Vector shapes = swkcanvas.getShapesWithTags(refTag);
            shapeList = new ArrayList();

            for (int i = 0, n = shapes.size(); i < n; i++) {
                shapeList.add(new Integer(((SwkShape) shapes.elementAt(i)).id));
            }
        }

        void getClosest() throws SwkException {
            SwkShape bestShape = null;
            SwkShape startShape = null;
            Rectangle2D bounds = null;
            double max = Double.MAX_VALUE;
            Line2D line = new Line2D.Double();
            SwkShape swkShape = swkcanvas.lastShape;

            SwkShape previousShape = swkcanvas.lastShape;
            boolean below = false;
            Vector shapes = swkcanvas.getShapesWithTags(refTag);

            if (shapes != null) {
                SwkShape refShape = (SwkShape) shapes.elementAt(0);

                if (refShape != null) {
                    startShape = refShape.previous;
                }
            }

            while (previousShape != null) {
                swkShape = previousShape;
                previousShape = swkShape.previous;

                if (swkShape.shape != null) {
                    bounds = swkShape.shape.getBounds2D();

                    if (halo != 0.0f) {
                        bounds.setRect(bounds.getX() - halo,
                            bounds.getY() - halo, bounds.getWidth() + halo,
                            bounds.getHeight() + halo);
                    }
                } else {
                    bounds = null;
                }

                if (bounds != null) {
                    if (bounds.contains(pt)) {
                        max = 0;
                        bestShape = swkShape;

                        if (startShape == null) {
                            break;
                        } else if (below) {
                            break;
                        }
                    } else {
                        line.setLine(bounds.getMaxX(), bounds.getMaxY(),
                            bounds.getMaxX(), bounds.getMinY());

                        double dis = line.ptLineDist(pt.getX(), pt.getY());

                        if (dis < max) {
                            max = dis;
                            bestShape = swkShape;
                        }

                        line.setLine(bounds.getMinX(), bounds.getMaxY(),
                            bounds.getMinX(), bounds.getMinY());
                        dis = line.ptLineDist(pt.getX(), pt.getY());

                        if (dis < max) {
                            max = dis;
                            bestShape = swkShape;
                        }

                        line.setLine(bounds.getMinX(), bounds.getMaxY(),
                            bounds.getMaxX(), bounds.getMaxY());
                        dis = line.ptLineDist(pt.getX(), pt.getY());

                        if (dis < max) {
                            max = dis;
                            bestShape = swkShape;
                        }

                        line.setLine(bounds.getMinX(), bounds.getMinY(),
                            bounds.getMaxX(), bounds.getMinY());
                        dis = line.ptLineDist(pt.getX(), pt.getY());

                        if (dis < max) {
                            max = dis;
                            bestShape = swkShape;
                        }
                    }

                    if (swkShape == startShape) {
                        below = true;
                    }
                }
            }
        }

        void getRectShapes() {
            Rectangle2D bounds = null;
            Enumeration e = swkcanvas.swkShapes.elements();

            while (e.hasMoreElements()) {
                swkShape = (SwkShape) e.nextElement();

                if (swkShape.shape != null) {
                    bounds = swkShape.shape.getBounds2D();
                } else {
                    bounds = null;
                }

                if (bounds != null) {
                    if (((mode == ENCLOSED) && rect.contains(bounds)) ||
                            ((mode == OVERLAPPING) && rect.intersects(bounds))) {
                        if (shapeList == null) {
                            shapeList = new ArrayList();
                        }

                        shapeList.add(new Integer(swkShape.id));
                    }
                }
            }
        }
    }
}
