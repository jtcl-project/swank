/*
 * Copyright (c) 2000 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.*;

import javax.imageio.ImageIO;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.SwingUtilities;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/** Class for objects which represent a Swank swkcanvas widget. */
public class SwkImageCanvas extends MouseAdapter implements SwkCanvasType {

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
    String className = null;
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
    Hashtable tagHash = new Hashtable();
    Vector tagVec = new Vector();
    int swkwidth = 1;
    int swkheight = 1;
    Component component = null;
    SimpleUniverse universe = null;
    TransformGroup objTransM = null;
    TransformGroup objTrans = null;
    Point3d eyePosition = new Point3d(0, 0, 100.0f);
    Point3d viewCenter = new Point3d(0, 0, 0);
    Vector3d upDirection = new Vector3d(0, 1, 0);
    Vector3f center = new Vector3f(0, 0, 0);
    Transform3D centerTrans = new Transform3D();
    Appearance defaultAppearance = null;
    Background bg = null;
    Color3f color3f = new Color3f();
    Canvas3D c3D;
    private PickCanvas pickCanvas;

    public SwkImageCanvas(final Interp interp, String name, String className) {
        this.name = name.intern();
        this.interp = interp;
        defaultAppearance = new Appearance();

        Material material = new Material();

        material.setAmbientColor(.0f, 1.0f, 0.0f);
        material.setEmissiveColor(0.0f, 1.0f, 0.0f);
        material.setDiffuseColor(0.0f, 1.0f, 0.0f);
        material.setSpecularColor(0.0f, 1.0f, 0.0f);
        material.setShininess(100.0f);
        defaultAppearance.setMaterial(material);

        //ColoringAttributes colorAttr = new ColoringAttributes(0.0f,0.0f,1.0f,ColoringAttributes.NICEST);
        //defaultAppearance.setColoringAttributes(colorAttr);
        // create the virtual univers
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        c3D = new Canvas3D(config);
        universe = new SimpleUniverse(c3D);

        // add a view to the universe
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.getViewer().getView().setBackClipDistance(300.0);
        universe.getViewer().getView().setFrontClipDistance(3.0);
        setEyePosition(0.0f, 0.0f, 100.0f);

        BranchGroup branchGroup = createSceneGraph2();
        addBranchGraph(branchGroup);
        //enablePicking(branchGroup);
        pickCanvas = new PickCanvas(c3D,branchGroup);
        pickCanvas.setTolerance(10.0f);
        pickCanvas.setMode(PickCanvas.GEOMETRY);
        c3D.addMouseListener(this);


        isCreated = true;
        tagList.add(name);
        tagList.add("swank");
        tagList.add("all");
    }

    public Canvas3D getCanvas3D() {
        return c3D;
    }
    public PickCanvas getPickCanvas() {
        return pickCanvas;
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

    public void setVirtualBindings(Vector bindings) {
        virtualBindings = bindings;
    }

    public Vector getVirtualBindings() {
        return (virtualBindings);
    }

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
        this.anchor = anchor.clone();
    }

    public float[] getAnchor() {
        return (anchor.clone());
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
    /**
     *
     * @param shape
     * @throws SwkException
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

        swkShapes.put(Integer.valueOf(shape.id), shape);

        if (shape.tagNames != null) {
            setTags(shape.tagNames, shape);
        }
        addChild(shape.bG);
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
            swkShapes.remove(Integer.valueOf(swkShape.id));
            e = swkShape.tags.elements();

            while (e.hasMoreElements()) {
                tag = (Tag) e.nextElement();
                tag.tagShapes.remove(swkShape);
            }
            removeChild(swkShape.bG.iChild);
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
        boolean gotRect = false;
        if (gotRect) {
            return unionRect;
        } else {
            return null;
        }
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
        return null;
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
        return component.getBackground();
    }

    void setBackground(Color background) {
        component.setBackground(background);
    }

    public void resetTransform() {
        Transform3D t3 = new Transform3D();
        objTransM.setTransform(t3);
    }

    public void setEyePosition(float x, float y, float z) {
        eyePosition.x = x;
        eyePosition.y = y;
        eyePosition.z = z;

        Transform3D t3 = new Transform3D();
        t3.lookAt(eyePosition, viewCenter, upDirection);
        t3.invert();

        TransformGroup tG = universe.getViewingPlatform().getViewPlatformTransform();
        tG.setTransform(t3);
    }

    public void setViewCenter(float x, float y, float z) {
        viewCenter.x = x;
        viewCenter.y = y;
        viewCenter.z = z;

        Transform3D t3 = new Transform3D();
        t3.lookAt(eyePosition, viewCenter, upDirection);
        t3.invert();

        TransformGroup tG = universe.getViewingPlatform().getViewPlatformTransform();
        tG.setTransform(t3);
    }

    public void setUpDirection(float x, float y, float z) {
        upDirection.x = x;
        upDirection.y = y;
        upDirection.z = z;

        Transform3D t3 = new Transform3D();
        t3.lookAt(eyePosition, viewCenter, upDirection);
        t3.invert();

        TransformGroup tG = universe.getViewingPlatform().getViewPlatformTransform();
        tG.setTransform(t3);
    }

    public void setCenter(float x, float y, float z) {
        center.x = x;
        center.y = y;
        center.z = z;
        centerTrans.setTranslation(center);
        objTrans.setTransform(centerTrans);
    }

    public Color getBackground3D() {
        bg.getColor(color3f);

        return color3f.get();
    }

    public void setBackground3D(Color bgColor) {
        try {
            bg.setColor(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
        } catch (Exception e) {
            System.out.println("e is " + e.toString());
        }
    }

    public BranchGroup createSceneGraph2() {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        // Create the transform group node and initialize it to the
        // identity.  Enable the TRANSFORM_WRITE capability so that
        // our behavior code can modify it at runtime.  Add it to the
        // root of the subgraph.
        objTransM = new TransformGroup();
        objTransM.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransM.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTransM.setCapability(Group.ALLOW_CHILDREN_READ);
        objTransM.setCapability(Group.ALLOW_CHILDREN_WRITE);
        objTransM.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        objRoot.addChild(objTransM);

        objTrans = new TransformGroup();
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTrans.setCapability(Group.ALLOW_CHILDREN_READ);
        objTrans.setCapability(Group.ALLOW_CHILDREN_WRITE);
        objTrans.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        objTransM.addChild(objTrans);

        // Create a simple shape leaf node, add it to the scene graph.
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                300.0);

        AmbientLight aL = new AmbientLight();
        aL.setInfluencingBounds(bounds);
        objRoot.addChild(aL);
        bg = new Background(0.1f, 0.1f, 0.2f);
        bg.setCapability(Background.ALLOW_COLOR_WRITE);
        bg.setCapability(Background.ALLOW_COLOR_READ);
        bg.setApplicationBounds(bounds);
        objRoot.addChild(bg);

        // Create a new Behavior object that will perform the desired
        // operation on the specified transform object and add it into
        // the scene graph.
        Transform3D yAxis = new Transform3D();
        Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0,
                4000, 0, 0, 0, 0, 0);

        RotationInterpolator rotator = new RotationInterpolator(rotationAlpha,
                objTransM, yAxis, 0.0f, (float) Math.PI * 2.0f);

        rotator.setSchedulingBounds(bounds);

        //objTransM.addChild(rotator);
        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(objTransM);
        myMouseRotate.setSchedulingBounds(bounds);
        objRoot.addChild(myMouseRotate);

        MouseZoom myMouseZoom = new MouseZoom();
        myMouseZoom.setTransformGroup(objTransM);
        myMouseZoom.setSchedulingBounds(bounds);
        objRoot.addChild(myMouseZoom);

        MouseTranslate myMouseTranslate = new MouseTranslate();
        myMouseTranslate.setTransformGroup(objTransM);
        myMouseTranslate.setSchedulingBounds(bounds);
        objRoot.addChild(myMouseTranslate);

        //Color3f lColor1 = new Color3f(0.9f,0.9f,0.9f);
        //Vector3f lDir1 = new Vector3f(-1.0f,-1.0f,-1.0f);
        //DirectionalLight lgt1 = new DirectionalLight(lColor1,lDir1);
        PointLight lgt1 = new PointLight();
        lgt1.setPosition(new Point3f(100.0f, 100.0f, 100.0f));
        lgt1.setAttenuation(new Point3f(1.0f, 0.001f, 0.0f));
        lgt1.setInfluencingBounds(bounds);

        objRoot.addChild(lgt1);

        // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    }

    public void addBranchGraph(BranchGroup scene) {
        if ((universe != null) && (scene != null)) {
            universe.addBranchGraph(scene);
        }
    }

    public int addChild(NvBranchGroup bG) {
        if ((objTrans != null) && (bG != null)) {
            objTrans.addChild(bG);

            int iChild = objTrans.numChildren() - 1;
            bG.iChild = iChild;

            return bG.id;
        }

        return (-1);
    }

    public int setChild(Shape3D shape, int iChild) {
        if ((iChild >= 0) && (iChild < objTrans.numChildren())) {
            if (shape != null) {
                NvBranchGroup newBG = new NvBranchGroup();

                // fixme newBG.setCapability(NvBranchGroup.ALLOW_DETACH);
                newBG.addChild(shape);
                objTrans.setChild(newBG, iChild);
                newBG.iChild = iChild;

                return (iChild);
            }
        }

        return (-1);
    }

    public int removeChild(int iChild) {
        if ((iChild >= 0) && (iChild < objTrans.numChildren())) {
            objTrans.removeChild(iChild);

            NvBranchGroup nvBG;

            for (int i = 0; i < objTrans.numChildren(); i++) {
                nvBG = (NvBranchGroup) objTrans.getChild(i);
                nvBG.iChild = i;
            }

            return (objTrans.numChildren());

        }

        return (-1);
    }

    /*
    public int removeChild(String tag) {
    Vector children = NvBranchGroup.getTagList(tag);
    for (int i=0;i<children.size();i++) {
    NvBranchGroup nvBG = (NvBranchGroup) children.elementAt(i);
    nvBG.remove();
    removeChild(nvBG.iChild);
    }
    return(objTrans.numChildren());
    }
    
    public void listChildren(String tag) {
    Vector children = NvBranchGroup.getTagList(tag);
    for (int i=0;i<children.size();i++) {
    NvBranchGroup nvBG = (NvBranchGroup) children.elementAt(i);
    System.out.println(nvBG.iChild+" "+nvBG.id);
    }
    }
    
    public void addTag(String tag,String newTag) {
    Vector children = NvBranchGroup.getTagList(tag);
    for (int i=0;i<children.size();i++) {
    NvBranchGroup nvBG = (NvBranchGroup) children.elementAt(i);
    nvBG.addTag(newTag);
    }
    }
     */
    public int numChildren() {
        if (objTrans != null) {
            return (objTrans.numChildren());
        }

        return (-1);
    }

    public int addSpheres(float[] coords, float[] colors, float[] radii) {
        TransformGroup tG;
        Transform3D t3D = new Transform3D();
        Vector3d v3d = new Vector3d();
        int nSpheres = coords.length / 3;
        NvBranchGroup bG = new NvBranchGroup();

        for (int i = 0; i < nSpheres; i++) {
            Appearance ap = new Appearance();
            v3d.x = coords[i * 3];
            v3d.y = coords[(i * 3) + 1];
            v3d.z = coords[(i * 3) + 2];
            t3D.setTranslation(v3d);
            tG = new TransformGroup(t3D);

            Sphere sphere = new Sphere(radii[i]);
            Material material = new Material();
            material.setAmbientColor(colors[i * 3], colors[(i * 3) + 1],
                    colors[(i * 3) + 2]);
            material.setEmissiveColor(1.0f, 1.0f, 1.0f);
            material.setDiffuseColor(colors[i * 3], colors[(i * 3) + 1],
                    colors[(i * 3) + 2]);
            material.setSpecularColor(1.0f, 1.0f, 1.0f);
            material.setShininess(30.0f);
            ap.setMaterial(material);

            ColoringAttributes colorAttr = new ColoringAttributes(colors[i * 3],
                    colors[(i * 3) + 1], colors[(i * 3) + 2],
                    ColoringAttributes.NICEST);
            ap.setColoringAttributes(colorAttr);
            sphere.setAppearance(ap);
            tG.addChild(sphere);
            bG.addChild(tG);
        }

        bG.setCapability(NvBranchGroup.ALLOW_DETACH);
        bG.compile();

        return (addChild(bG));
    }

    public int addLines2(float[] coords, float[] colors, int start, int nElem) {
        int nVertices = nElem / 3;
        LineArray lineArray = new LineArray(nVertices,
                LineArray.COORDINATES | LineArray.COLOR_3);
        lineArray.setCoordinates(start, coords);
        lineArray.setColors(start, colors);

        Shape3D shape = new Shape3D(lineArray);

        if ((objTrans != null) && (shape != null)) {
            NvBranchGroup newBG = new NvBranchGroup();
            TransformGroup localTrans = new TransformGroup();
            localTrans.addChild(shape);
            newBG.addChild(localTrans);

            return (addChild(newBG));
        } else {
            return -1;
        }
    }

    /**
     *
     */
    public void repaint() {
        final Component component2 = component;

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (component2 != null) {
                    component2.repaint();
                }
            }
        });
    }

    /**
     *
     * @param delay
     */
    public void repaint(int delay) {
        final Component component2 = component;
        final int delay2 = delay;

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (component2 != null) {
                    component2.repaint(delay2);
                }
            }
        });
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
           public void mouseClicked(MouseEvent mEvent) {
                String pickResult = pickCanvas(mEvent);
            }


    String pickCanvas(MouseEvent e) {
        String pickResult = "";
        pickCanvas.setShapeLocation(e); 
        PickResult result = pickCanvas.pickClosest(); 
        if (result == null) { 
          // System.out.println("Nothing picked");
        } else { 
          TransformGroup tg = (TransformGroup) result.getNode( PickResult.TRANSFORM_GROUP );
//System.out.println(tg);
          if (tg instanceof NvTransformGroup) {
              SwkShape swkShape = ((NvTransformGroup) tg).getShape();
              int index = ((NvTransformGroup) tg).getIndex();
             // System.out.println("swksh " + swkShape.getType() + " " + swkShape.getId() + " " + index);
          }

           Primitive p = (Primitive)result.getNode(PickResult.PRIMITIVE); 
           Shape3D s = (Shape3D)result.getNode(PickResult.SHAPE3D); 
           GeometryArray geomArray = result.getGeometryArray();
           if (geomArray != null) {
              // System.out.println(" geom count " + geomArray.getVertexCount());
           }
          // System.out.println("ni " + result.numIntersections());
           int nInter = result.numIntersections();
           for (int i=0;i<nInter;i++) {
//System.out.println("i " + i);
           PickIntersection pickIntersection = result.getIntersection(i);
           if (pickIntersection != null) { 
         //      System.out.println("pv " + pickIntersection.getClosestVertexIndex());
         //      System.out.println("pv " + pickIntersection.getClosestVertexCoordinates());
          //     System.out.println("pv " + pickIntersection.getClosestVertexCoordinatesVW());
          //     System.out.println("ga " + pickIntersection.getGeometryArrayIndex());
               for (int pickIndex:pickIntersection.getPrimitiveCoordinateIndices()) {
                   //  System.out.println(pickIndex);
               }
               for (int pickIndex:pickIntersection.getPrimitiveVertexIndices()) {
                   //  System.out.println(pickIndex);
               }
           }
           }
           
           if (p != null) { 
            //  System.out.println(p.getClass().getName());
               pickResult = p.getClass().getName();
           } else if (s != null) { 
               //  System.out.println(s.getClass().getName());
                 pickResult = s.getClass().getName();
           } else{ 
            //  System.out.println("null");
              pickResult = "null pick";
           } 
        } 
        return pickResult;
    }

   public static void enablePicking(Node node) {
    if (node == null) {
         return;
    }
    try {
    node.setCapability(Node.ALLOW_PICKABLE_WRITE);
    node.setPickable(true);
    node.setCapability(Node.ENABLE_PICK_REPORTING);

    if (node instanceof Group) {
       Group group = (Group) node;
       for (Enumeration e = group.getAllChildren(); e.hasMoreElements();) {
          enablePicking((Node)e.nextElement());
       }

    }
    if (node instanceof Shape3D) {
          Shape3D shape = (Shape3D) node;

          PickTool.setCapabilities(node, PickTool.INTERSECT_FULL);

          for (Enumeration e = shape.getAllGeometries(); e.hasMoreElements();) {

             Geometry g = (Geometry)e.nextElement();

             g.setCapability(g.ALLOW_INTERSECT);

          }

    }
    } catch (Exception e) {
        System.out.println("enable picking shape error " +e.getMessage());
    }

}

    /**
     *
     * @param g
     */
    public void paintComponent(Graphics g) {
    }

    /**
     *
     * @param className
     */
    public void setClassName(String className) {
        this.className = className.intern();
    }

    public String getClassName() {
        return (className);
    }

    /**
     *
     * @throws TclException
     */
    public void close() throws TclException {
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
