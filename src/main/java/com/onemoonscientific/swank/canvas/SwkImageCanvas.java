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
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;

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
        "-visible", "-width", "-xscrollcommand", "-yscrollcommand",};
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
    String className = "SwkImageCanvas";
    LinkedList children = null;
    Vector virtualBindings = null;
    final Interp interp;
    boolean isCreated = false;
    Insets emptyBorderInsets = new Insets(0, 0, 0, 0);
    Vector tagList = new Vector();
    Color insertBackground;
    int insertWidth = 0;
    int insertBorderWidth = 0;
    int insertOffTime = 0;
    int insertOnTime = 0;
    int[][] scrollRegion = new int[2][2];
    float[] anchor = {0.0f, 0.0f};
    Graphics g1 = null;
    double zoom = 1.0;
    AffineTransform canvasTransform = new AffineTransform();
    Hashtable transformerHash = new Hashtable();
    Transformer fracTransformer = null;
    Transformer pfTransformer = null;
    Transformer fpTransformer = null;
    Point2D transMouse = new Point2D.Double();
    Point2D origMouse = new Point2D.Double();
    FontRenderContext fRC = null;
    Hashtable swkShapes = new Hashtable(16);
    ItemTreeNode rootNode = new ItemTreeNode();
    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    int lastShapeId = 0;
    HitShape eventCurrentShape = null;
    SwkShape lastShapeScanned = null;
    int handle = -1;
    Map tagHash = new LinkedHashMap(1);
    Vector tagVec = new Vector();
    int swkwidth = 1;
    int swkheight = 1;
    Component component = null;
    BufferedImage bufOffscreen = null;

    /**
     *
     * @param interp
     * @param name
     */
    public SwkImageCanvas(final Interp interp, String name) {
        this.name = name.intern();
        this.interp = interp;
        isCreated = true;
        tagList.add(name);
        tagList.add("swank");
        tagList.add("all");
        treeModel.addTreeModelListener(new MyTreeModelListener());

        fracTransformer = new Transformer("frac");
        transformerHash.put("frac", fracTransformer);

        pfTransformer = new Transformer("pf");
        transformerHash.put("pf", pfTransformer);

        fpTransformer = new Transformer("fp");
        transformerHash.put("fp", fpTransformer);
    }

    private static class MyTreeModelListener implements TreeModelListener {

        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */

            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) (node.getChildAt(index));

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }

        public void treeNodesInserted(TreeModelEvent e) {
            System.out.println("inserted");
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            System.out.println("removed");
        }

        public void treeStructureChanged(TreeModelEvent e) {
            System.out.println("changed");
        }
    }

    /**
     *
     * @return
     */
    public Component getComponent() {
        return component;
    }

    /**
     *
     * @param component
     */
    public void setComponent(Component component) {
        this.component = component;
    }

    /**
     *
     * @return
     */
    public boolean isCreated() {
        return isCreated;
    }

    /**
     *
     * @param state
     */
    public void setCreated(boolean state) {
        isCreated = state;
    }

    /**
     *
     * @return
     */
    public BufferedImage getBuffer() {
        return bufOffscreen;
    }

    /**
     *
     * @return
     */
    public Vector getTagList() {
        return (tagList);
    }

    /**
     *
     * @param interp
     * @param tagListObj
     * @throws TclException
     */
    public void setTagList(Interp interp, TclObject tagListObj)
            throws TclException {
        TclObject[] tagObjs = TclList.getElements(interp, tagListObj);
        tagList.setSize(0);

        for (int i = 0; i < tagObjs.length; i++) {
            tagList.add(tagObjs[i].toString());
        }
    }

    /**
     *
     * @param bindings
     */
    public void setVirtualBindings(Vector bindings) {
        virtualBindings = bindings;
    }

    /**
     *
     * @return
     */
    public Vector getVirtualBindings() {
        return (virtualBindings);
    }

    /**
     *
     * @return
     */
    public Insets getEmptyBorderInsets() {
        return (emptyBorderInsets);
    }

    /**
     *
     * @return
     */
    public LinkedList getChildrenList() {
        return (children);
    }

    /**
     *
     */
    public void initChildrenList() {
        children = new LinkedList();
    }

    /**
     *
     * @return
     */
    public String getName() {
        return (name);
    }

    /**
     *
     * @param insertBackground
     */
    public void setInsertBackground(Color insertBackground) {
        this.insertBackground = insertBackground;
    }

    /**
     *
     * @return
     */
    public Color getInsertBackground() {
        return (insertBackground);
    }

    /**
     *
     * @param insertWidth
     */
    public void setInsertWidth(int insertWidth) {
        this.insertWidth = insertWidth;
    }

    /**
     *
     * @return
     */
    public int getInsertWidth() {
        return (insertWidth);
    }

    /**
     *
     * @param insertBorderWidth
     */
    public void setInsertBorderWidth(int insertBorderWidth) {
        this.insertBorderWidth = insertBorderWidth;
    }

    /**
     *
     * @return
     */
    public int getInsertBorderWidth() {
        return (insertBorderWidth);
    }

    /**
     *
     * @param insertOffTime
     */
    public void setInsertOffTime(int insertOffTime) {
        this.insertOffTime = insertOffTime;
    }

    /**
     *
     * @return
     */
    public int getInsertOffTime() {
        return (insertOffTime);
    }

    /**
     *
     * @param insertOnTime
     */
    public void setInsertOnTime(int insertOnTime) {
        this.insertOnTime = insertOnTime;
    }

    /**
     *
     * @return
     */
    public int getInsertOnTime() {
        return (insertOnTime);
    }

    /**
     *
     * @param anchor
     */
    public void setAnchor(float[] anchor) {
        this.anchor = anchor.clone();
    }

    /**
     *
     * @return
     */
    public float[] getAnchor() {
        return (anchor.clone());
    }

    /**
     *
     * @param shape
     */
    public void setEventCurrentShape(HitShape shape) {
        eventCurrentShape = shape;
    }

    /**
     *
     * @return
     */
    public SwkShape getLastShapeScanned() {
        return lastShapeScanned;
    }

    /**
     *
     * @return
     */
    public int getHandle() {
        return handle;
    }

    /**
     *
     * @param transformerName
     * @param shape
     * @return
     */
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

    /**
     *
     * @param name
     * @return
     */
    public Transformer getTransformer(String name) {
        return (Transformer) transformerHash.get(name);
    }

    void removeTransformer(Interp interp, SwkShape shape)
            throws TclException {
        shape.transformer = null;
    }

    /**
     *
     * @param newZoom
     */
    public void setZoom(double newZoom) {
        zoom = newZoom;
    }

    /**
     *
     * @return
     */
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
    /**
     *
     * @return
     */
    public int getLastShapeId() {
        return lastShapeId;
    }

    /**
     *
     * @param shape
     * @throws SwkException
     */
    public void addShape(SwkShape shape) throws SwkException {
        swkShapes.put(Integer.valueOf(shape.id), shape);

        if (shape.tagNames != null) {
            setTags(shape.tagNames, shape);
        }
        ItemTreeNode node = new ItemTreeNode();
        node.setUserObject(shape);
        shape.node = node;
        rootNode.add(node);
        if (g1 != null) {
            shape.paintShape((Graphics2D) g1);
        }
        repaint(50);
        //treeModel.nodeStructureChanged(rootNode);
    }

    /**
     *
     * @param arg
     * @return
     * @throws SwkException
     */
    public SwkShape getShape(String arg) throws SwkException {
        int iElem;
        SwkShape swkShape = null;

        if (arg.equals("current")) {
            if (eventCurrentShape != null) {
                swkShape = eventCurrentShape.swkShape;
            } else {
                throw new SwkException("tag \"" + arg + "\"doesn't exist");
            }
        }

        if (swkShape == null) {
            if (SwankUtil.looksLikeInt(arg)) {
                try {
                    iElem = Integer.parseInt(arg);

                    if ((iElem < 0) || (iElem >= lastShapeId)) {
                        throw new SwkException("Invalid canvas item id "
                                + iElem + " " + lastShapeId);
                    } else {
                        swkShape = (SwkShape) swkShapes.get(Integer.valueOf(iElem));

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
            Tag tag = (Tag) tagHash.get(arg);

            if (tag == null) {
                throw new SwkException("tag \"" + arg + "\"doesn't exist");
            } else if (tag.tagShapes.size() != 1) {
                throw new SwkException("more than one shape for this tag");
            } else {
                Enumeration e = tag.tagShapes.elements();
                swkShape = ((SwkShape) e.nextElement());
            }
        }

        return swkShape;
    }

    void unlinkShape(SwkShape swkShape) {
        swkShape.node.removeFromParent();
    }

    void deleteAll() throws SwkException {
        String[] tags = {"all"};
        delete(tags);
    }

    void delete(final String[] tags) throws SwkException {
        Vector shapes = getShapesWithTags(tags);
        SwkShape swkShape = null;
        String tagString;
        Tag tag;

        for (int i = 0; i < shapes.size(); i++) {
            swkShape = (SwkShape) shapes.elementAt(i);
            swkShape.dispose();
            swkShapes.remove(Integer.valueOf(swkShape.id));

            for (Object o : swkShape.tags.entrySet()) {
                Entry entry = (Entry) o;
                tag = (Tag) entry.getValue();
                tag.tagShapes.remove(swkShape);
            }

            unlinkShape(swkShape);
        }
    }

    void raise(String raiseTag, String afterObj) throws SwkException {
        Vector shapes = getShapesWithTags(raiseTag);
        if (afterObj == null) {
            for (int i = 0; i < shapes.size(); i++) {
                SwkShape swkShape = (SwkShape) shapes.elementAt(i);
                ItemTreeNode parent = (ItemTreeNode) swkShape.node.getParent();
                parent.remove(swkShape.node);
                parent.add(swkShape.node);
            }
        } else {
            SwkShape afterShape = getShape(afterObj);
            ItemTreeNode afterParent = (ItemTreeNode) afterShape.node.getParent();
            for (int i = 0; i < shapes.size(); i++) {
                SwkShape swkShape = (SwkShape) shapes.elementAt(i);
                ItemTreeNode parent = (ItemTreeNode) swkShape.node.getParent();
                if (parent == afterParent) {
                    parent.remove(swkShape.node);
                    int index = parent.getIndex(afterShape.node);
                    parent.insert(swkShape.node, index + 1);
                }
            }
        }
    }

    void lower(String lowerTag, String beforeObj) throws SwkException {
        Vector shapes = getShapesWithTags(lowerTag);
        if (beforeObj == null) {
            for (int i = (shapes.size() - 1); i >= 0; i--) {
                SwkShape swkShape = (SwkShape) shapes.elementAt(i);
                ItemTreeNode parent = (ItemTreeNode) swkShape.node.getParent();
                parent.remove(swkShape.node);
                parent.insert(swkShape.node, 0);
            }
        } else {
            SwkShape beforeShape = getShape(beforeObj);
            ItemTreeNode beforeParent = (ItemTreeNode) beforeShape.node.getParent();
            for (int i = (shapes.size() - 1); i >= 0; i--) {
                SwkShape swkShape = (SwkShape) shapes.elementAt(i);
                ItemTreeNode parent = (ItemTreeNode) swkShape.node.getParent();
                if (parent == beforeParent) {
                    parent.remove(swkShape.node);
                    int index = parent.getIndex(beforeShape.node);
                    if (index < 0) {
                        index = 0;
                    }
                    parent.insert(swkShape.node, index);
                }
            }
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

            Rectangle2D thisBound = getShapeBounds(swkShape);

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

    Rectangle2D getShapeBounds(SwkShape swkShape) {
        Rectangle2D thisBound = null;
        if (swkShape.shape == null) {
        } else {
            if (swkShape instanceof ItemText) {
                thisBound = ((ItemText) swkShape).getBounds();
            } else {
                AffineTransform shapeTransform = swkShape.getTransform();
                if (shapeTransform != null) {
                    thisBound = shapeTransform.createTransformedShape(swkShape.shape).getBounds2D();
                } else {
                    thisBound = swkShape.shape.getBounds2D();
                }
            }
        }
        return thisBound;
    }

    Vector getShapesWithTags(String tag) throws SwkException {
        String[] tags = {tag};

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
            } else if (tagList[i].equals("hselect")) {
                Enumeration e = swkShapes.elements();

                while (e.hasMoreElements()) {
                    shape = (SwkShape) e.nextElement();
                    if (shape.isSelected()) {
                        shapeList.add(shape);
                    }
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
                    SwkShape swkShape = (SwkShape) swkShapes.get(Integer.valueOf(
                            iElem));

                    if (swkShape == null) {
                        throw new SwkException("Invalid canvas item id (null) "
                                + iElem);
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
                        shapeHash.put(eventCurrentShape.swkShape, eventCurrentShape.swkShape);
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

    /**
     *
     * @param tagList
     * @param shape
     * @throws SwkException
     */
    public void setTags(String[] tagList, SwkShape shape)
            throws SwkException {
        setTags(tagList, true, shape);
    }

    /**
     *
     * @param tagList
     * @param shape
     * @throws SwkException
     */
    public void addTags(String[] tagList, SwkShape shape)
            throws SwkException {
        setTags(tagList, false, shape);
    }

    /**
     *
     * @param tagList
     * @param clearFirst
     * @param shape
     * @throws SwkException
     */
    public void setTags(String[] tagList, boolean clearFirst, SwkShape shape)
            throws SwkException {
        Tag tag = null;

        for (int i = 0; i < tagList.length; i++) {
            if (SwankUtil.looksLikeInt(tagList[i])) {
                throw new SwkException("tag cannot be an integer");
            }
        }

        if (clearFirst) {
            for (Object o : shape.tags.entrySet()) {
                Entry entry = (Entry) o;
                tag = (Tag) entry.getValue();
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

    /**
     *
     * @param interp
     * @param argv
     * @param swkshape
     * @return
     * @throws TclException
     */
    public String itemGet(Interp interp, TclObject argv, SwkShape swkshape)
            throws TclException {
        return ("");
    }

    /**
     *
     * @param shape
     * @return
     */
    public ArrayList getTags(SwkShape shape) {
        Tag tag;
        String tagString = null;
        ArrayList list = new ArrayList();

        for (Object o : shape.tags.entrySet()) {
            Entry entry = (Entry) o;
            tag = (Tag) entry.getValue();
            list.add(tag.name);
        }

        return list;
    }

    /**
     *
     * @param mEvent
     */
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

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public Point transformPosition(double x, double y) {
        Point loc = getComponent().getLocationOnScreen();
        x -= loc.x;
        y -= loc.y;
        origMouse.setLocation(x, y);
        transMouse.setLocation(x, y);
        try {
            transMouse = canvasTransform.inverseTransform(origMouse, transMouse);
        } catch (java.awt.geom.NoninvertibleTransformException ntE) {
        }
        Point point = new Point((int) transMouse.getX(), (int) transMouse.getY());
        return point;
    }

    /**
     *
     * @param x1
     * @param y1
     * @return
     */
    public TclObject[] scanCanvasForTags(double x1, double y1) {


        LinkedHashSet shapeHash = new LinkedHashSet();
        String tagOrId = null;
        Enumeration tags = null;
        lastShapeScanned = null;

        for (int iMode = 0; iMode < 2; iMode++) {
            handle = -1;
            for (Enumeration e = rootNode.reverseDepthFirstEnumeration(); e.hasMoreElements();) {
                ItemTreeNode node = (ItemTreeNode) e.nextElement();
                SwkShape swkShape = (SwkShape) node.getUserObject();
                if ((swkShape == null) || ((swkShape.getState() != SwkShape.ACTIVE) && (swkShape.getState() != SwkShape.LOCKED))) {
                    continue;
                }
                if (swkShape instanceof ItemNode) {
                    continue;
                }
                if (iMode == 0) {
                    if (swkShape.isSelected()) {
                        handle = swkShape.hitHandles(x1, y1);
                        if (handle < 0) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    if (!swkShape.hitShape(x1, y1)) {
                        continue;
                    }
                }
                lastShapeScanned = swkShape;

                tagOrId = String.valueOf(swkShape.id);
                shapeHash.add(tagOrId);

                for (Object o : swkShape.tags.entrySet()) {
                    Entry entry = (Entry) o;
                    Tag tag = (Tag) entry.getValue();

                    tagOrId = tag.name + " "
                            + String.valueOf(swkShape.id);
                    shapeHash.add(tagOrId);
                }

                tagOrId = "all " + String.valueOf(swkShape.id);
                shapeHash.add(tagOrId);

                break;
            }
            if (handle >= 0) {
                break;
            }
        }

        if (shapeHash.size() == 0) {
            return null;
        }

        TclObject[] tagOrIds = new TclObject[shapeHash.size()];

        int i = 0;

        for (Object o : shapeHash) {
            String shapeTagOrID = (String) o;
            tagOrIds[i++] = TclString.newInstance(shapeTagOrID);
        }
        return tagOrIds;
    }

    FontRenderContext getFontRenderContext() {
        return fRC;
    }

    /**
     *
     * @param newSize
     */
    public void setSize(Dimension newSize) {
        swkwidth = newSize.width;
        swkheight = newSize.height;
    }

    Dimension getSize() {
        return new Dimension(swkwidth, swkheight);
    }

    Color getBackground() {
        return component.getBackground();
    }

    void setBackground(Color background) {
        component.setBackground(background);
    }

    /**
     *
     */
    public void repaint() {
        repaint(20);
        /*
        final Component component2 = component;

        SwingUtilities.invokeLater(new Runnable() {

        public void run() {
        if (component2 != null) {
        if (component2 instanceof SwkCanvas) {
        ((SwkCanvas) component2).repaint();
        }
        }
        }
        });
         */
    }

    /**
     *
     * @param delay
     */
    public void repaint(final int delay) {
        final Component component2 = component;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (component2 != null) {
                    if (component2 instanceof SwkCanvas) {
                        ((SwkCanvas) component2).changed = true;
                        ((SwkCanvas) component2).startTimer(delay);
                    }
                }
            }
        });
    }

    /**
     *
     * @param oWidth
     * @param oHeight
     * @param oStream
     */
    public void save(int oWidth, int oHeight, OutputStream oStream, final String imageMode) {
        Dimension size = getSize();
        if (oStream != null) {
            BufferedImage bufferedImage = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics offgraphics = bufferedImage.getGraphics();
            paintComponent(offgraphics, null);
            offgraphics.dispose();
            if (oWidth > 0) {
                if (oHeight <= 0) {
                    oHeight = (int) (oWidth * (double) size.getHeight() / size.getWidth());
                }
                BufferedImage thumbNailImage = new BufferedImage(oWidth, oHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2Thumb = thumbNailImage.createGraphics();
                g2Thumb.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2Thumb.drawImage(bufferedImage, 0, 0, oWidth, oHeight, null);
                g2Thumb.dispose();
                writeImage(thumbNailImage, oStream,imageMode);
            } else {
                writeImage(bufferedImage, oStream,imageMode);
            }
        }
    }

    /**
     *
     * @param rendImage
     * @param oStream
     */
    public void writeImage(BufferedImage rendImage, OutputStream oStream, final String imageMode) {
        // Write generated image to a file
        try {
            // Save as PNG
            ImageIO.write(rendImage, imageMode, oStream);
        } catch (IOException e) {
        }
    }

    private static class NodeBounds {

        final ItemTreeNode node;
        final Rectangle2D rect;

        NodeBounds(final ItemTreeNode node, final Rectangle2D rect) {
            this.node = node;
            this.rect = rect;
        }
    }

    /**
     *
     * @param g
     * @param bufOffscreen
     */
    public void paintComponent(Graphics g, BufferedImage bufOffscreen) {
        g1 = g;
        this.bufOffscreen = bufOffscreen;
        Dimension d = getSize();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform storeAT = g2.getTransform();
        fRC = g2.getFontRenderContext();

        g2.setColor(getBackground());
        g2.fillRect(0, 0, d.width, d.height);
        canvasTransform.setToIdentity();
        canvasTransform.translate(-scrollRegion[0][0], -scrollRegion[0][1]);
        canvasTransform.scale(zoom, zoom);
        g2.transform(canvasTransform);
        boolean resetUnion = true;
        Stack<NodeBounds> unionStack = new Stack<NodeBounds>();
        AffineTransform fracAT = fracTransformer.getTransform();
        fracAT.setToScale(d.getWidth(), d.getHeight());
        AffineTransform fpAT = fpTransformer.getTransform();
        fpAT.setToScale(d.getWidth(), 1.0);
        AffineTransform pfAT = pfTransformer.getTransform();
        pfAT.setToScale(d.getWidth(), 1.0);
        for (Enumeration e = rootNode.depthFirstEnumeration(); e.hasMoreElements();) {
            ItemTreeNode node = (ItemTreeNode) e.nextElement();
            SwkShape swkShape = (SwkShape) node.getUserObject();
            if ((swkShape == null) || (swkShape.getState() == SwkShape.HIDDEN)) {
                continue;
            }
            //System.out.println(swkShape.getId());

            if (swkShape instanceof ItemNode) {
                Rectangle2D unionRect = new Rectangle2D.Double();
                boolean firstRect = true;
                while (!unionStack.empty()) {
                    NodeBounds thisNodeBound = unionStack.peek();
                    if (thisNodeBound.node.getParent() != node) {
                        break;
                    }
                    thisNodeBound = unionStack.pop();
                    Rectangle2D thisBound = thisNodeBound.rect;
                    if (firstRect) {
                        unionRect.setRect(thisBound);
                        firstRect = false;
                    } else {
                        Rectangle2D.union(thisBound, unionRect, unionRect);
                    }
                }
                ((ItemNode) swkShape).rect2D.setRect(unionRect);
                swkShape.paintShape(g2);
                NodeBounds thisNodeBounds = new NodeBounds(node, unionRect);
                unionStack.push(thisNodeBounds);
            } else {
                Rectangle2D thisBound = getShapeBounds(swkShape);
                NodeBounds thisNodeBounds = new NodeBounds(node, thisBound);
                unionStack.push(thisNodeBounds);

                swkShape.paintShape(g2);
                if (swkShape.isSelected()) {
                    swkShape.drawHandles(g2);
                }
            }
        }
        g2.setTransform(storeAT);
    }

    /**
     *
     * @param className
     */
    public void setClassName(String className) {
        this.className = className.intern();
    }

    /**
     *
     * @return
     */
    public String getClassName() {
        return (className);
    }

    /**
     *
     * @throws TclException
     */
    public void close() throws TclException {
        try {
            deleteAll();
        } catch (Exception e) {
        }
    }

    private class Tag {

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
