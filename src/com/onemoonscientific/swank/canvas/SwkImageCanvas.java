/*
 * Copyright (c) 2000 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.*;

import javax.imageio.ImageIO;


/** Class for objects which represent a Swank swkcanvas widget. */
public class SwkImageCanvas implements SwkCanvasType {
    static Hashtable resourceDB = null;
    static String[] validCmds = {
        "-alignmentx", "-alignmenty", "-anchor", "-autoscrolls", "-background",
        "-bd", "-bg", "-borderwidth", "-border", "-bounds", "-class", "-cursor",
        "-debuggraphicsoptions", "-doublebuffered", "-enabled", "-fg",
        "-focusable", "-focuscycleroot", "-focustraversalkeysenabled", "-font",
        "-foreground", "-height", "-highlightbackground", "-highlightcolor",
        "-highlightthickness", "-ignorerepaint", "-insertbackground",
        "-insertborderwidth", "-insertofftime", "-insertontime", "-insertwidth",
        "-location", "-maximumsize", "-minimumsize", "-name", "-opaque", "-padx",
        "-pady", "-preferredsize", "-relief", "-requestfocusenabled",
        "-scrollregion", "-size", "-tooltiptext", "-verifyinputwhenfocustarget",
        "-visible", "-width", "-xscrollcommand", "-yscrollcommand",
    };
    private static final int OPT_ALIGNMENTX = 0;
    private static final int OPT_ALIGNMENTY = 1;
    private static final int OPT_ANCHOR = 2;
    private static final int OPT_AUTOSCROLLS = 3;
    private static final int OPT_BACKGROUND = 4;
    private static final int OPT_BD = 5;
    private static final int OPT_BG = 6;
    private static final int OPT_BORDERWIDTH = 7;
    private static final int OPT_BORDER = 8;
    private static final int OPT_BOUNDS = 9;
    private static final int OPT_CLASS = 10;
    private static final int OPT_CURSOR = 11;
    private static final int OPT_DEBUGGRAPHICSOPTIONS = 12;
    private static final int OPT_DOUBLEBUFFERED = 13;
    private static final int OPT_ENABLED = 14;
    private static final int OPT_FG = 15;
    private static final int OPT_FOCUSABLE = 16;
    private static final int OPT_FOCUSCYCLEROOT = 17;
    private static final int OPT_FOCUSTRAVERSALKEYSENABLED = 18;
    private static final int OPT_FONT = 19;
    private static final int OPT_FOREGROUND = 20;
    private static final int OPT_HEIGHT = 21;
    private static final int OPT_HIGHLIGHTBACKGROUND = 22;
    private static final int OPT_HIGHLIGHTCOLOR = 23;
    private static final int OPT_HIGHLIGHTTHICKNESS = 24;
    private static final int OPT_IGNOREREPAINT = 25;
    private static final int OPT_INSERTBACKGROUND = 26;
    private static final int OPT_INSERTBORDERWIDTH = 27;
    private static final int OPT_INSERTOFFTIME = 28;
    private static final int OPT_INSERTONTIME = 29;
    private static final int OPT_INSERTWIDTH = 30;
    private static final int OPT_LOCATION = 31;
    private static final int OPT_MAXIMUMSIZE = 32;
    private static final int OPT_MINIMUMSIZE = 33;
    private static final int OPT_NAME = 34;
    private static final int OPT_OPAQUE = 35;
    private static final int OPT_PADX = 36;
    private static final int OPT_PADY = 37;
    private static final int OPT_PREFERREDSIZE = 38;
    private static final int OPT_RELIEF = 39;
    private static final int OPT_REQUESTFOCUSENABLED = 40;
    private static final int OPT_SCROLLREGION = 41;
    private static final int OPT_SIZE = 42;
    private static final int OPT_TOOLTIPTEXT = 43;
    private static final int OPT_VERIFYINPUTWHENFOCUSTARGET = 44;
    private static final int OPT_VISIBLE = 45;
    private static final int OPT_WIDTH = 46;
    private static final int OPT_XSCROLLCOMMAND = 47;
    private static final int OPT_YSCROLLCOMMAND = 48;
    String name = null;
    String className = null;
    LinkedList children = null;
    Vector virtualBindings = null;
    int active = 0;
    boolean created = false;
    TclObject tclObject = null;
    final Interp interp;
    boolean isCreated = false;
    Insets emptyBorderInsets = new Insets(0, 0, 0, 0);
    Vector tagList = new Vector();
    Color insertBackground;
    Color background = Color.WHITE;
    int insertWidth = 0;
    int insertBorderWidth = 0;
    int insertOffTime = 0;
    int insertOnTime = 0;
    int[][] scrollRegion = new int[2][2];
    float[] anchor = { 0.0f, 0.0f };
    double borderWidth = 0;
    Color highlightBackground = Color.white;
    Color highlightColor = Color.red;
    int highlightThickness;
    int padx;
    int pady;
    String relief = null;
    String xScrollCommand = null;
    String yScrollCommand = null;
    Graphics g1 = null;
    BasicStroke stroke = new BasicStroke();
    double zoom = 1.0;
    AffineTransform canvasTransform = new AffineTransform();
    Hashtable transformerHash = new Hashtable();
    Point2D transMouse = new Point2D.Double();
    Point2D origMouse = new Point2D.Double();
    FontRenderContext fRC = null;
    Hashtable swkShapes = new Hashtable(16);
    int lastShapeId = 0;
    SwkShape firstShape = null;
    SwkShape lastShape = null;
    SwkShape eventCurrentShape = null;
    SwkShape lastShapeScanned = null;
    Point currentPt = new Point(0, 0);
    String currentTag = null;
    String previousTag = null;
    TclObject[] currentTags = null;
    TclObject[] previousTags = null;
    Hashtable focusHash = null;
    Hashtable mouseHash = null;
    Hashtable mouseMotionHash = null;
    Hashtable keyHash = null;
    Hashtable tagHash = new Hashtable();
    Vector tagVec = new Vector();
    int swkwidth = 1;
    int swkheight = 1;
    int mouseX = 0;
    int mouseY = 0;
    Component component = null;

    public SwkImageCanvas(final Interp interp, String name, String className) {
        this.name = name.intern();
        this.interp = interp;
        isCreated = true;
        tagList.add(name);
        tagList.add("swank");
        tagList.add("all");
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean state) {
        isCreated = state;
    }

    public Vector getTagList() {
        return (tagList);
    }

    public void setTagList(Interp interp, TclObject tagListObj)
        throws TclException {
        TclObject[] tagObjs = TclList.getElements(interp, tagListObj);
        tagList.setSize(0);

        for (int i = 0; i < tagObjs.length; i++) {
            tagList.add(tagObjs[i].toString());
        }
    }

    public void setVirtualBindings(Vector bindings) {
        virtualBindings = bindings;
    }

    public Vector getVirtualBindings() {
        return (virtualBindings);
    }

    public Insets getEmptyBorderInsets() {
        return (emptyBorderInsets);
    }

    public LinkedList getChildrenList() {
        return (children);
    }

    public void initChildrenList() {
        children = new LinkedList();
    }

    public String getName() {
        return (name);
    }

    public void setInsertBackground(Color insertBackground) {
        this.insertBackground = insertBackground;
    }

    public Color getInsertBackground() {
        return (insertBackground);
    }

    public void setInsertWidth(int insertWidth) {
        this.insertWidth = insertWidth;
    }

    public int getInsertWidth() {
        return (insertWidth);
    }

    public void setInsertBorderWidth(int insertBorderWidth) {
        this.insertBorderWidth = insertBorderWidth;
    }

    public int getInsertBorderWidth() {
        return (insertBorderWidth);
    }

    public void setInsertOffTime(int insertOffTime) {
        this.insertOffTime = insertOffTime;
    }

    public int getInsertOffTime() {
        return (insertOffTime);
    }

    public void setInsertOnTime(int insertOnTime) {
        this.insertOnTime = insertOnTime;
    }

    public int getInsertOnTime() {
        return (insertOnTime);
    }

    public void setAnchor(float[] anchor) {
        this.anchor = anchor;
    }

    public float[] getAnchor() {
        return (anchor);
    }

    public void setEventCurrentShape(SwkShape shape) {
        eventCurrentShape = shape;
    }

    public SwkShape getLastShapeScanned() {
        return lastShapeScanned;
    }

    public Transformer setTransformer(String transformerName, SwkShape shape) {
        Transformer transformer = null;
        transformer = (Transformer) transformerHash.get(transformerName);

        if (transformer == null) {
            transformer = new Transformer(transformerName);
            transformerHash.put(transformerName, transformer);
        }

        if (shape != null) {
            shape.transformer = transformer;
        }

        return transformer;
    }

    public Transformer getTransformer(String name) {
        return (Transformer) transformerHash.get(name);
    }

    void removeTransformer(Interp interp, SwkShape shape)
        throws TclException {
        shape.transformer = null;
    }

    public void setZoom(double newZoom) {
        zoom = newZoom;
    }

    public double getZoom() {
        return zoom;
    }

    String getTagOrIDFromTagID(String tagID) {
        int spacePos = tagID.indexOf(" ");

        if (spacePos == -1) {
            return tagID;
        } else {
            return tagID.substring(0, spacePos);
        }
    }

    /*

        public Image paintImage() {
            Dimension d = getSize();
            Image offscreen = createImage(d.width, d.height);
            Graphics2D offgraphics = (Graphics2D) offscreen.getGraphics();
            boolean wasBuffered = SwankUtil.disableDoubleBuffering(this);
            paint(offgraphics);
            offgraphics.dispose();
            SwankUtil.restoreDoubleBuffering(this, wasBuffered);

            return (offscreen);
        }
    */
    public void addShape(SwkShape shape) throws SwkException {
        shape.previous = lastShape;

        if (firstShape == null) {
            firstShape = shape;
        }

        if (lastShape != null) {
            lastShape.next = shape;
        }

        lastShape = shape;

        swkShapes.put(new Integer(shape.id), shape);

        if (shape.tagNames != null) {
            setTags(shape.tagNames, shape);
        }
    }

    public SwkShape getShape(String arg) throws SwkException {
        int iElem;
        SwkShape swkShape = null;

        if (arg.equals("current")) {
            if (eventCurrentShape != null) {
                swkShape = eventCurrentShape;
            } else {
                throw new SwkException("tag doesn't exist");
            }
        }

        if (swkShape == null) {
            if (SwankUtil.looksLikeInt(arg)) {
                try {
                    iElem = Integer.parseInt(arg);

                    if ((iElem < 0) || (iElem >= lastShapeId)) {
                        throw new SwkException("Invalid canvas item id " +
                            iElem + " " + lastShapeId);
                    } else {
                        swkShape = (SwkShape) swkShapes.get(new Integer(iElem));

                        if (swkShape == null) {
                            throw new SwkException(
                                "Invalid canvas item id null" + iElem);
                        }
                    }
                } catch (NumberFormatException nfeE) {
                }
            }
        }

        if (swkShape == null) {
            Tag tag = (Tag) tagHash.get(arg.toString());

            if (tag == null) {
                throw new SwkException("tag doesn't exist");
            } else if (tag.tagShapes.size() != 1) {
                throw new SwkException("more than one shape for this tag");
            } else {
                Enumeration e = tag.tagShapes.elements();
                swkShape = ((SwkShape) e.nextElement());
            }
        }

        return swkShape;
    }

    //void delete(Interp interp, TclObject[] argv, int start)
    //  if (argv.length < (start + 1)) {
    //   throw new TclNumArgsException(interp, 0, argv, "option");
    //}
    void unlinkShape(SwkShape swkShape) {
        if (firstShape == swkShape) {
            firstShape = swkShape.next;
        }

        if (lastShape == swkShape) {
            lastShape = swkShape.previous;
        }

        if (swkShape.previous != null) {
            swkShape.previous.next = swkShape.next;
        }

        if (swkShape.next != null) {
            swkShape.next.previous = swkShape.previous;
        }
    }

    SwkShape linkShapeAfter(SwkShape swkShape, SwkShape afterShape) {
        SwkShape nextShape = afterShape.next;

        swkShape.previous = afterShape;
        swkShape.next = nextShape;

        if (nextShape != null) {
            nextShape.previous = swkShape;
        }

        afterShape.next = swkShape;

        if (afterShape == lastShape) {
            lastShape = swkShape;
        }

        return swkShape;
    }

    void linkShapeBefore(SwkShape swkShape, SwkShape beforeShape) {
    }

    void delete(final String[] tags) throws SwkException {
        Vector shapes = getShapesWithTags(tags);
        SwkShape swkShape = null;
        Enumeration e;
        String tagString;
        Tag tag;

        for (int i = 0; i < shapes.size(); i++) {
            swkShape = (SwkShape) shapes.elementAt(i);
            swkShapes.remove(new Integer(swkShape.id));
            e = swkShape.tags.elements();

            while (e.hasMoreElements()) {
                tag = (Tag) e.nextElement();
                tag.tagShapes.remove(swkShape);
            }

            unlinkShape(swkShape);
        }
    }

    void raise(String raiseTag, String afterObj) throws SwkException {
        Vector shapes = getShapesWithTags(raiseTag);
        SwkShape swkShape = null;
        SwkShape nextShape = null;
        SwkShape afterShape = lastShape;

        if (afterObj != null) {
            afterShape = getShape(afterObj);
        }

        SwkShape shape;

        for (int i = 0; i < shapes.size(); i++) {
            swkShape = (SwkShape) shapes.elementAt(i);

            if (swkShape == afterShape) {
                continue;
            }

            unlinkShape(swkShape);
            afterShape = linkShapeAfter(swkShape, afterShape);
        }
    }

    void lower(String lowerTag, String beforeObj) throws SwkException {
        Vector shapes = getShapesWithTags(lowerTag);
        SwkShape swkShape = null;
        SwkShape nextShape = null;
        SwkShape beforeShape = firstShape;

        if (beforeObj != null) {
            beforeShape = getShape(beforeObj);
        }

        SwkShape shape;

        for (int i = (shapes.size() - 1); i >= 0; i--) {
            swkShape = (SwkShape) shapes.elementAt(i);

            if (swkShape == beforeShape) {
                continue;
            }

            if (swkShape.next != null) {
                swkShape.next.previous = swkShape.previous;
            }

            if (swkShape.previous != null) {
                swkShape.previous.next = swkShape.next;
            }

            nextShape = beforeShape.previous;

            if (nextShape != null) {
                nextShape.next = swkShape;
            }

            if (beforeShape == firstShape) {
                firstShape = swkShape;
            }

            beforeShape.previous = swkShape;

            if (swkShape == lastShape) {
                lastShape = swkShape.previous;
            }

            swkShape.next = beforeShape;
            swkShape.previous = nextShape;
            beforeShape = swkShape;
        }
    }

    Rectangle2D getShapeBounds(String[] tagNames) throws SwkException {
        Rectangle2D unionRect = new Rectangle2D.Double();

        Vector shapeList = getShapesWithTags(tagNames);
        boolean gotRect = false;

        for (int i = 0; i < shapeList.size(); i++) {
            SwkShape swkShape = (SwkShape) shapeList.elementAt(i);

            if (swkShape == null) {
                continue;
            }

            Rectangle2D thisBound = null;

            if (swkShape.shape == null) {
                if (swkShape instanceof SwkCanvText) {
                    thisBound = ((SwkCanvText) swkShape).getBounds();
                }
            } else {
                thisBound = swkShape.shape.getBounds2D();
            }

            if (thisBound == null) {
                continue;
            }

            gotRect = true;

            if (i == 0) {
                unionRect.setRect(thisBound);
            } else {
                Rectangle2D.union(thisBound, unionRect, unionRect);
            }
        }

        if (gotRect) {
            return unionRect;
        } else {
            return null;
        }
    }

    Vector getShapesWithTags(String tag) throws SwkException {
        String[] tags = { tag };

        return getShapesWithTags(tags, 0);
    }

    Vector getShapesWithTags(String[] tags) throws SwkException {
        return (getShapesWithTags(tags, 0));
    }

    Vector getShapesWithTags(String[] tagList, int start)
        throws SwkException {
        Vector shapeList = new Vector();
        Hashtable shapeHash = new Hashtable();

        Tag tag = null;
        SwkShape shape;

        for (int i = start; i < tagList.length; i++) {
            if (tagList[i].equals("all")) {
                Enumeration e = swkShapes.elements();

                while (e.hasMoreElements()) {
                    shape = (SwkShape) e.nextElement();
                    shapeList.add(shape);
                }

                return (shapeList);
            }
        }

        for (int i = 0; i < tagList.length; i++) {
            boolean intValid = true;

            if (SwankUtil.looksLikeInt(tagList[i])) {
                int iElem = 0;

                try {
                    iElem = Integer.parseInt(tagList[i]);
                } catch (NumberFormatException iE) {
                    intValid = false;
                }

                if (intValid) {
                    SwkShape swkShape = (SwkShape) swkShapes.get(new Integer(
                                iElem));

                    if (swkShape == null) {
                        throw new SwkException("Invalid canvas item id (null) " +
                            iElem);
                    } else {
                        shapeHash.put(swkShape, swkShape);
                    }
                }
            } else {
                intValid = false;
            }

            if (!intValid) {
                if (tagList[i].equals("current")) {
                    if (eventCurrentShape != null) {
                        shapeHash.put(eventCurrentShape, eventCurrentShape);
                    }

                    continue;
                }

                tag = (Tag) tagHash.get(tagList[i]);

                if (tag != null) {
                    Enumeration e = tag.tagShapes.elements();

                    while (e.hasMoreElements()) {
                        shape = (SwkShape) e.nextElement();
                        shapeHash.put(shape, shape);
                    }
                }
            }
        }

        Enumeration e = shapeHash.elements();

        while (e.hasMoreElements()) {
            shapeList.addElement(e.nextElement());
        }

        return (shapeList);
    }

    public void setTags(String[] tagList, SwkShape shape)
        throws SwkException {
        setTags(tagList, true, shape);
    }

    public void addTags(String[] tagList, SwkShape shape)
        throws SwkException {
        setTags(tagList, false, shape);
    }

    public void setTags(String[] tagList, boolean clearFirst, SwkShape shape)
        throws SwkException {
        Tag tag = null;

        for (int i = 0; i < tagList.length; i++) {
            if (SwankUtil.looksLikeInt(tagList[i])) {
                throw new SwkException("tag cannot be an integer");
            }
        }

        if (clearFirst) {
            Enumeration e = shape.tags.elements();

            while (e.hasMoreElements()) {
                tag = (Tag) e.nextElement();
                tag.tagShapes.remove(shape);
            }

            shape.tags.clear();
        }

        for (int i = 0; i < tagList.length; i++) {
            tag = (Tag) tagHash.get(tagList[i].toString());

            if (tag == null) {
                tag = new Tag(tagList[i]);
                tagHash.put(tagList[i], tag);
            }

            tag.tagShapes.put(shape, shape);
            shape.tags.put(tag, tag);
        }
    }

    void removeTags(String[] tagList, boolean clearFirst, SwkShape shape) {
        Tag tag = null;

        for (int i = 0; i < tagList.length; i++) {
            tag = (Tag) tagHash.get(tagList[i]);

            if (tag != null) {
                tag.tagShapes.remove(shape);
                shape.tags.remove(tag);
            }
        }
    }

    public String itemGet(Interp interp, TclObject argv, SwkShape swkshape)
        throws TclException {
        return ("");
    }

    public ArrayList getTags(SwkShape shape) {
        Tag tag;
        String tagString = null;
        ArrayList list = new ArrayList();
        Enumeration e = shape.tags.elements();

        while (e.hasMoreElements()) {
            tag = (Tag) e.nextElement();
            list.add(tag.name);
        }

        return list;
    }
    public void transformMouse(MouseEvent mEvent) {
        double x = mEvent.getX();
        double y = mEvent.getY();
        origMouse.setLocation(x, y);
        transMouse.setLocation(x, y);

        try {
            transMouse = canvasTransform.inverseTransform(origMouse, transMouse);
        } catch (java.awt.geom.NoninvertibleTransformException ntE) {
        }

        mEvent.translatePoint((int) (transMouse.getX() - x),
            (int) (transMouse.getY() - y));
    }

    public TclObject[] scanCanvasForTags(double x1, double y1) {
        Hashtable shapeHash = new Hashtable();
        String tagOrId = null;
        Enumeration tags = null;
        Enumeration e = null;
        lastShapeScanned = null;

        SwkShape swkShape = null;
        SwkShape nextShape = lastShape;
        int closeEnough = 2;
        int closeEnough2 = closeEnough*closeEnough;
        double[] tcoords = new double[6];
        double tx1=0.0,ty1=0.0,tx2=0.0,ty2=0.0;

        while (nextShape != null) {
            swkShape = nextShape;
            nextShape = swkShape.previous;
            if (swkShape.getState() != SwkShape.ACTIVE) {
                continue;
            }

            if (swkShape.shape == null) {
                if (!swkShape.hitShape(x1, y1)) {
                    continue;
                }
            } else {
                boolean hit = false;
                if (swkShape.fill != null) {
                    Rectangle bounds = swkShape.shape.getBounds();
                    if (!bounds.contains(x1, y1)) {
                        continue;
                    }  else {
                       hit = true;
                    }
                }
                if (!hit) {
                    PathIterator pI = swkShape.shape.getPathIterator(null);
                    boolean intersects = false;
                    while (!pI.isDone()) {
                        int type = pI.currentSegment(tcoords);
                        if (type == PathIterator.SEG_LINETO) {
                           tx2 = tcoords[0];
                           ty2 = tcoords[0];
                           double dis = Line2D.ptSegDistSq(tx1,ty1,tx2,ty2,x1,y1);
                           tx1 = tx2;
                           ty1 = ty2;
                           if (dis <= closeEnough2) {
                                intersects = true;
                                break;
                           }
                        
                        } else if (type == PathIterator.SEG_MOVETO) {
                           tx1 = tcoords[0];
                           ty1 = tcoords[0];
                        }
                        pI.next();
                    }
                    if (!intersects) {
                          continue;
                    }
                    /*
                    if (!swkShape.shape.intersects(x1 - closeEnough,
                                y1 - closeEnough, 2 * closeEnough, 2 * closeEnough)) {
                        continue;
                    }
                   */
                }
            }

            lastShapeScanned = swkShape;

            tagOrId = String.valueOf(swkShape.id);
            shapeHash.put(tagOrId, tagOrId);
            tags = swkShape.tags.elements();

            while (tags.hasMoreElements()) {
                tagOrId = ((Tag) tags.nextElement()).name + " " +
                    String.valueOf(swkShape.id);
                shapeHash.put(tagOrId, tagOrId);
            }

            tagOrId = "all " + String.valueOf(swkShape.id);
            shapeHash.put(tagOrId, tagOrId);

            break;
        }

        if (shapeHash.size() == 0) {
            return null;
        }

        TclObject[] tagOrIds = new TclObject[shapeHash.size()];
        e = shapeHash.elements();

        int i = 0;

        while (e.hasMoreElements()) {
            String shapeTagOrID = (String) e.nextElement();
            tagOrIds[i++] = TclString.newInstance(shapeTagOrID);
        }

        return tagOrIds;
    }

    FontRenderContext getFontRenderContext() {
        return fRC;
    }

    public void setSize(Dimension newSize) {
        swkwidth = newSize.width;
        swkheight = newSize.height;
    }

    Dimension getSize() {
        return new Dimension(swkwidth, swkheight);
    }

    Color getBackground() {
        return background;
    }
    void setBackground(Color background) {
        this.background =  background;
    }

    public void repaint() {
        if (component != null) {
            component.repaint();
        }
    }

    public void repaint(int delay) {
        if (component != null) {
            component.repaint(delay);
        }
    }

    public void paint(int width, int height, String fileName) {
        swkwidth = width;
        swkheight = height;

        BufferedImage bufferedImage = new BufferedImage(swkwidth, swkheight,
                BufferedImage.TYPE_INT_RGB);
        Graphics offgraphics = bufferedImage.getGraphics();
        paintComponent(offgraphics);
        offgraphics.dispose();

        if (fileName != null) {
            writeImage(bufferedImage, fileName);
        }
    }

    public void writeImage(BufferedImage rendImage, String fileName) {
        // Write generated image to a file
        try {
            // Save as PNG
            File file = new File(fileName);
            ImageIO.write(rendImage, "png", file);
        } catch (IOException e) {
        }
    }

    public void paintComponent(Graphics g) {
        g1 = g;

        Dimension d = getSize();
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform storeAT = g2.getTransform();
        fRC = g2.getFontRenderContext();

        SwkShape swkShape = null;
        SwkShape nextShape = null;
        g2.setColor(getBackground());
        g2.fillRect(0, 0, d.width, d.height);
        canvasTransform.setToIdentity();
        canvasTransform.translate(-scrollRegion[0][0], -scrollRegion[0][1]);
        canvasTransform.scale(zoom, zoom);
        g2.transform(canvasTransform);
        swkShape = firstShape;
        nextShape = firstShape;

        int i = 0;

        while (nextShape != null) {
            swkShape = nextShape;

            nextShape = swkShape.next;

            if (swkShape.getState() == SwkShape.HIDDEN) {
                continue;
            }

            if (swkShape.xorMode) {
                g.setXORMode(getBackground());
            } else {
                g.setPaintMode();
            }

            i++;

            if (swkShape instanceof SwkCanvText) {
                SwkCanvText text = (SwkCanvText) swkShape;
                text.paint(g2, fRC);
            } else if (swkShape instanceof SwkCanvImage) {
                SwkCanvImage swkImage = (SwkCanvImage) swkShape;
                AffineTransform aT = new AffineTransform();
                aT.translate((int) swkShape.storeCoords[0],
                    swkShape.storeCoords[1]);

                if (swkImage.bufferedImage != null) {
                    // FIXME can last argument be null?
                    g2.drawImage(swkImage.bufferedImage, aT, null);
                }
            } else if (swkShape instanceof SwkCanvBitmap) {
                SwkCanvBitmap swkImage = (SwkCanvBitmap) swkShape;

                if (swkImage.image != null) {
                    int imageWidth = swkImage.image.getIconWidth();
                    int imageHeight = swkImage.image.getIconHeight();
                    g.drawImage(swkImage.image.getImage(),
                        (int) swkShape.storeCoords[0] - (imageWidth / 2),
                        (int) swkShape.storeCoords[1] - (imageHeight / 2), null);
                }
            } else {
                if (swkShape.stroke != null) {
                    g2.setStroke(swkShape.stroke);
                } else {
                    g2.setStroke(stroke);
                }

                if (swkShape.shape == null) {
                    swkShape.paintShape(g2);
                } else {
                    if (swkShape instanceof SwkPolygon) {
                    }

                    AffineTransform shapeTransform = swkShape.getTransform();
                    Shape shape = swkShape.shape;

                    if (shapeTransform != null) {
                        shape = shapeTransform.createTransformedShape(shape);
                    }

                    if (swkShape.texturePaint != null) {
                        g2.setPaint(swkShape.texturePaint);
                        g2.fill(shape);

                        //g.drawImage(swkShape.textureImage.getImage(),0,0,null);
                    } else if (swkShape.fillGradient != null) {
                        g2.setPaint(swkShape.fillGradient);
                        g2.fill(shape);
                    } else if (swkShape.fill != null) {
                        g2.setPaint(swkShape.fill);
                        g2.fill(shape);
                    }

                    if (swkShape.outline != null) {
                        g2.setPaint(swkShape.outline);
                        g2.draw(shape);
                    }
                }
            }
        }

        g2.setTransform(storeAT);
    }

    public void setClassName(String className) {
        this.className = className.intern();
    }

    public String getClassName() {
        return (className);
    }

    public void close() throws TclException {
    }

    class Tag {
        int id = -1;
        String name = null;
        Hashtable tagShapes = new Hashtable();

        Tag(String name) {
            this.name = name.intern();
            tagVec.addElement(this);
            id = tagVec.size() - 1;
        }
    }
}
