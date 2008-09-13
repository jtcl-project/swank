package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;

import javax.media.j3d.*;

import javax.swing.*;

import javax.vecmath.*;


public class SwkCanvas3D extends JPanel implements SwkWidget {
    static Hashtable resourceDB = null;
    String name = null;
    String className = null;
    int active = 0;
    TclObject tclObject = null;
    Interp interp;
    double borderWidth = 0;
    LinkedList children = null;
    Vector virtualBindings = null;
    Insets emptyBorderInsets = new Insets(0, 0, 0, 0);
    SwkMouseListener mouseListener = null;
    SwkKeyListener keyListener = null;
    SwkKeyCommandListener keyCommandListener = null;
    SwkFocusListener focusListener = null;
    SwkComponentListener componentListener = null;
    SwkChangeListener changeListener = null;
    SwkMouseMotionListener mouseMotionListener = null;
    boolean created = false;
    SimpleUniverse universe = null;
    View view = null;
    TransformGroup objTransM = null;
    TransformGroup objTrans = null;
    Point3d eyePosition = new Point3d(0, 0, 100.0f);
    Point3d viewCenter = new Point3d(0, 0, 0);
    Vector3d upDirection = new Vector3d(0, 1, 0);
    Vector3f center = new Vector3f(0, 0, 0);
    Transform3D centerTrans = new Transform3D();
    int mouseX = 0;
    int mouseY = 0;
    Hashtable tagHash = new Hashtable();
    Vector tagVec = new Vector();
    Hashtable swkShapes = new Hashtable(16);
    int lastShapeId = 0;
    SwkShape3D firstShape = null;
    SwkShape3D lastShape = null;
    SwkShape3D currentShape = null;
    SwkShape3D lastShapeScanned = null;
    Point currentPt = new Point(0, 0);
    String currentTag = null;
    String previousTag = null;
    TclObject[] currentTags = null;
    TclObject[] previousTags = null;
    Vector tagList = new Vector();
    Appearance defaultAppearance = null;
    Background bg = null;
    Color3f color3f = new Color3f();
    Rectangle scrollRegion = new Rectangle(0, 0, 0, 0);
    int swkwidth = 1;
    int swkheight = 1;

    public SwkCanvas3D(Interp interp, String name, String className) {
        this.name = new String(name);
        this.interp = interp;

        if (resourceDB == null) {
            resourceDB = new Hashtable();
            initResources();
        }

        defaultAppearance = new Appearance();

        Material material = new Material();

        /* material.setAmbientColor(.0f,1.0f,0.0f);
         material.setEmissiveColor(0.0f,0.0f,0.0f);
         material.setDiffuseColor(0.0f, 0.0f, 0.0f);
         material.setSpecularColor(0.0f,0.0f,0.0f);
         material.setShininess(30.0f);
         */
        defaultAppearance.setMaterial(material);

        //ColoringAttributes colorAttr = new ColoringAttributes(0.0f,0.0f,1.0f,ColoringAttributes.NICEST);
        //defaultAppearance.setColoringAttributes(colorAttr);
        // create the virtual univers
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D c3D = new Canvas3D(config);
        setLayout(new BorderLayout());
        add("Center", c3D);

        universe = new SimpleUniverse(c3D);

        // add a view to the universe
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.getViewer().getView().setBackClipDistance(300.0);
        universe.getViewer().getView().setFrontClipDistance(3.0);
        setEyePosition(0.0f, 0.0f, 100.0f);

        addBranchGraph(createSceneGraph2());
        tagList.add(name);
        tagList.add("swank");
        tagList.add("all");
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

    public void setCreated(boolean state) {
        created = state;
    }

    public boolean isCreated() {
        return created;
    }

    public Dimension getMinimumSize() {
        Dimension dSize = new Dimension(scrollRegion.width, scrollRegion.height);

        if (dSize.width < swkwidth) {
            dSize.width = swkwidth;
        }

        if (dSize.height < swkheight) {
            dSize.height = swkheight;
        }

        return (dSize);
    }

    public Dimension getPreferredSize() {
        return (getMinimumSize());
    }

    public void setSwkHeight(int height) {
        this.swkheight = height;
    }

    public int getSwkHeight() {
        Dimension size = getSize();

        return (size.height);
    }

    public void setSwkWidth(int width) {
        this.swkwidth = width;
    }

    public int getSwkWidth() {
        Dimension size = getSize();

        return (size.width);
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

        TransformGroup tG = universe.getViewingPlatform()
                                    .getViewPlatformTransform();
        tG.setTransform(t3);
    }

    public void setViewCenter(float x, float y, float z) {
        viewCenter.x = x;
        viewCenter.y = y;
        viewCenter.z = z;

        Transform3D t3 = new Transform3D();
        t3.lookAt(eyePosition, viewCenter, upDirection);
        t3.invert();

        TransformGroup tG = universe.getViewingPlatform()
                                    .getViewPlatformTransform();
        tG.setTransform(t3);
    }

    public void setUpDirection(float x, float y, float z) {
        upDirection.x = x;
        upDirection.y = y;
        upDirection.z = z;

        Transform3D t3 = new Transform3D();
        t3.lookAt(eyePosition, viewCenter, upDirection);
        t3.invert();

        TransformGroup tG = universe.getViewingPlatform()
                                    .getViewPlatformTransform();
        tG.setTransform(t3);
    }

    public void setCenter(float x, float y, float z) {
        center.x = x;
        center.y = y;
        center.z = z;
        centerTrans.setTranslation(center);
        objTrans.setTransform(centerTrans);
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

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
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
            if ((objTrans != null) && (shape != null)) {
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
            if (objTrans != null) {
                objTrans.removeChild(iChild);

                NvBranchGroup nvBG;

                for (int i = 0; i < objTrans.numChildren(); i++) {
                    nvBG = (NvBranchGroup) objTrans.getChild(i);
                    nvBG.iChild = i;
                }

                return (objTrans.numChildren());
            }
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

    public void close() throws TclException {
    }

    public int addLines(float[] coords, float[] colors, int start, int nElem) {
        TransformGroup tG;
        Transform3D t3D = new Transform3D();
        Vector3d v3d = new Vector3d();
        int nSpheres = 2;
        Color white = Color.WHITE;
        NvBranchGroup bG = new NvBranchGroup();

        for (int i = 0; i < nSpheres; i++) {
            Appearance ap = new Appearance();
            v3d.x = coords[i * 3];
            v3d.y = coords[(i * 3) + 1];
            v3d.z = coords[(i * 3) + 2];
            t3D.setTranslation(v3d);
            tG = new TransformGroup(t3D);

            Sphere sphere = new Sphere(0.02f);
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

    public void addShape(SwkShape3D shape) {
        shape.id = lastShapeId;
        lastShapeId++;
        shape.previous = lastShape;

        if (firstShape == null) {
            firstShape = shape;
        }

        if (lastShape != null) {
            lastShape.next = shape;
        }

        lastShape = shape;

        swkShapes.put(new Integer(shape.id), shape);
        addChild(shape.bG);
    }

    public SwkShape3D getShape(Interp interp, TclObject arg)
        throws TclException {
        int iElem;

        try {
            iElem = TclInteger.get(interp, arg);

            if ((iElem < 0) || (iElem >= lastShapeId)) {
                throw new TclException(interp, "Invalid canvas item id");
            }

            SwkShape3D SwkShape3D = (SwkShape3D) swkShapes.get(new Integer(
                        iElem));

            if (SwkShape3D == null) {
                throw new TclException(interp, "Invalid canvas item id");
            } else {
                return SwkShape3D;
            }
        } catch (TclException tclE) {
            Tag tag = (Tag) tagHash.get(arg.toString());

            if (tag == null) {
                throw new TclException(interp, "tag doesn't exist");
            }

            if (tag.tagShapes.size() != 1) {
                throw new TclException(interp,
                    "more than one shape for this tag");
            }

            Enumeration e = tag.tagShapes.elements();

            return ((SwkShape3D) e.nextElement());
        }
    }

    void delete(Interp interp, TclObject[] argv, int start)
        throws TclException {
        if (argv.length < (start + 1)) {
            throw new TclNumArgsException(interp, 0, argv, "option");
        }

        Vector shapes = getShapesWithTags(interp, argv[start]);
        SwkShape3D SwkShape3D = null;
        Enumeration e;
        String tagString;
        Tag tag;

        for (int i = 0; i < shapes.size(); i++) {
            SwkShape3D = (SwkShape3D) shapes.elementAt(i);
            swkShapes.remove(new Integer(SwkShape3D.id));
            e = SwkShape3D.tags.elements();
            removeChild(SwkShape3D.bG.iChild);

            while (e.hasMoreElements()) {
                tag = (Tag) e.nextElement();
                tag.tagShapes.remove(SwkShape3D);
            }

            if (firstShape == SwkShape3D) {
                firstShape = SwkShape3D.next;
            }

            if (lastShape == SwkShape3D) {
                lastShape = SwkShape3D.previous;
            }

            if (SwkShape3D.previous != null) {
                SwkShape3D.previous.next = SwkShape3D.next;
            }

            if (SwkShape3D.next != null) {
                SwkShape3D.next.previous = SwkShape3D.previous;
            }
        }
    }

    void raise(Interp interp, TclObject raiseShapes, TclObject afterObj)
        throws TclException {
        Vector shapes = getShapesWithTags(interp, raiseShapes);
        SwkShape3D SwkShape3D = null;
        SwkShape3D nextShape = null;
        SwkShape3D afterShape = lastShape;

        if (afterObj != null) {
            afterShape = getShape(interp, afterObj);
        }

        SwkShape3D shape;

        Enumeration e = swkShapes.elements();

        while (e.hasMoreElements()) {
            shape = (SwkShape3D) e.nextElement();
        }

        for (int i = 0; i < shapes.size(); i++) {
            SwkShape3D = (SwkShape3D) shapes.elementAt(i);

            if (SwkShape3D == afterShape) {
                continue;
            }

            if (SwkShape3D.previous != null) {
                SwkShape3D.previous.next = SwkShape3D.next;
            }

            if (SwkShape3D.next != null) {
                SwkShape3D.next.previous = SwkShape3D.previous;
            }

            nextShape = afterShape.next;

            if (nextShape != null) {
                nextShape.previous = SwkShape3D;
            }

            if (afterShape == lastShape) {
                lastShape = SwkShape3D;
            }

            afterShape.next = SwkShape3D;

            if (SwkShape3D == firstShape) {
                firstShape = SwkShape3D.next;
            }

            SwkShape3D.previous = afterShape;
            SwkShape3D.next = nextShape;
            afterShape = SwkShape3D;
        }

        e = swkShapes.elements();

        while (e.hasMoreElements()) {
            shape = (SwkShape3D) e.nextElement();
        }
    }

    void lower(Interp interp, TclObject lowerShapes, TclObject beforeObj)
        throws TclException {
    }

    void search(Interp interp, TclObject[] argv, int start)
        throws TclException {
        SwkShape3D SwkShape3D = null;
        SwkShape3D nextShape = null;

        if (argv.length < (start + 1)) {
            throw new TclNumArgsException(interp, 0, argv, "option");
        }

        if (argv[start].toString().equals("above")) {
            if (argv.length != (start + 2)) {
                throw new TclNumArgsException(interp, 1, argv, "option");
            }

            int iSearch = TclInteger.get(interp, argv[start + 1]);
            interp.resetResult();

            SwkShape3D = (SwkShape3D) swkShapes.get(new Integer(iSearch));

            if (SwkShape3D != null) {
                SwkShape3D = SwkShape3D.next;

                if (SwkShape3D != null) {
                    interp.setResult(SwkShape3D.id);
                }
            }

            return;
        } else if (argv[start].toString().equals("all")) {
            if (argv.length != (start + 1)) {
                throw new TclNumArgsException(interp, 1, argv, "option");
            }

            TclObject list = TclList.newInstance();
            SwkShape3D = firstShape;
            nextShape = firstShape;

            while (nextShape != null) {
                SwkShape3D = nextShape;
                nextShape = SwkShape3D.next;
                TclList.append(interp, list,
                    TclInteger.newInstance(SwkShape3D.id));
            }

            interp.setResult(list);
        } else if (argv[start].toString().equals("below")) {
            if (argv.length != (start + 2)) {
                throw new TclNumArgsException(interp, 1, argv, "option");
            }

            if (argv.length != (start + 2)) {
                throw new TclNumArgsException(interp, 1, argv, "option");
            }

            int iSearch = TclInteger.get(interp, argv[start + 1]);
            interp.resetResult();

            SwkShape3D = (SwkShape3D) swkShapes.get(new Integer(iSearch));

            if (SwkShape3D != null) {
                SwkShape3D = SwkShape3D.previous;

                if (SwkShape3D != null) {
                    interp.setResult(SwkShape3D.id);
                }
            }

            return;
        } else if (argv[start].toString().equals("closest")) {
            if (argv.length != (start + 3)) {
                throw new TclNumArgsException(interp, 1, argv, "option");
            }
        } else if (argv[start].toString().equals("enclosed")) {
            SwkShape3D = null;

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

            Rectangle2D rect = new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
            TclObject list = TclList.newInstance();
            Enumeration e = swkShapes.elements();

            /* FIXME
                        while (e.hasMoreElements()) {
                            SwkShape3D = (SwkShape3D) e.nextElement();

                            if (SwkShape3D.shape != null) {
                                bounds = SwkShape3D.shape.getBounds2D();
                            } else {
                                bounds = null;
                            }

                            if (bounds != null) {
                                if (rect.contains(bounds)) {
                                    TclList.append(interp, list,
                                        TclInteger.newInstance(SwkShape3D.id));
                                }
                            }
                        }
             */
            interp.setResult(list);
        } else if (argv[start].toString().equals("overlapping")) {
            if (argv.length != (start + 5)) {
                throw new TclNumArgsException(interp, 1, argv, "option");
            }

            SwkShape3D = null;

            Rectangle2D bounds = null;

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

            Rectangle2D rect = new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
            TclObject list = TclList.newInstance();
            Enumeration e = swkShapes.elements();

            while (e.hasMoreElements()) {
                SwkShape3D = (SwkShape3D) e.nextElement();

                /* FIXME
                                if (SwkShape3D.shape != null) {
                                    bounds = SwkShape3D.shape.getBounds2D();
                                } else {
                                    bounds = null;
                                }

                                if (bounds != null) {
                                    if (rect.intersects(bounds)) {
                                        TclList.append(interp, list,
                                            TclInteger.newInstance(SwkShape3D.id));
                                    }
                                }
                 */
            }

            interp.setResult(list);
        } else if (argv[start].toString().equals("withtag")) {
            if (argv.length != (start + 2)) {
                throw new TclNumArgsException(interp, 1, argv, "option");
            }

            Vector shapes = getShapesWithTags(interp, argv[start + 1]);
            TclObject list = TclList.newInstance();

            for (int i = 0; i < shapes.size(); i++) {
                TclList.append(interp, list,
                    TclInteger.newInstance(
                        ((SwkShape3D) shapes.elementAt(i)).id));
            }

            interp.setResult(list);
        }
    }

    Vector getShapesWithTags(Interp interp, TclObject tagList)
        throws TclException {
        TclObject[] argv = TclList.getElements(interp, tagList);

        return (getShapesWithTags(interp, argv, 0));
    }

    Vector getShapesWithTags(Interp interp, TclObject[] tagList, int start)
        throws TclException {
        Vector shapeList = new Vector();
        Hashtable shapeHash = new Hashtable();

        Tag tag = null;
        SwkShape3D shape;

        for (int i = start; i < tagList.length; i++) {
            if (tagList[i].toString().equals("all")) {
                Enumeration e = swkShapes.elements();

                while (e.hasMoreElements()) {
                    shape = (SwkShape3D) e.nextElement();
                    shapeList.add(shape);
                }

                return (shapeList);
            }
        }

        for (int i = 0; i < tagList.length; i++) {
            int iElem;

            try {
                iElem = TclInteger.get(interp, tagList[i]);

                SwkShape3D SwkShape3D = (SwkShape3D) swkShapes.get(new Integer(
                            iElem));

                if (SwkShape3D == null) {
                    throw new TclException(interp, "Invalid canvas item id");
                } else {
                    shapeHash.put(SwkShape3D, SwkShape3D);
                }
            } catch (TclException tclE) {
                interp.resetResult();

                if (tagList[i].toString().equals("current")) {
                    if (currentShape != null) {
                        shapeHash.put(currentShape, currentShape);
                    }

                    continue;
                }

                tag = (Tag) tagHash.get(tagList[i].toString());

                if (tag != null) {
                    Enumeration e = tag.tagShapes.elements();

                    while (e.hasMoreElements()) {
                        shape = (SwkShape3D) e.nextElement();
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

    void setTags(Interp interp, TclObject arg, SwkShape3D shape)
        throws TclException {
        setTags(interp, arg, true, shape);
    }

    void addTags(Interp interp, TclObject arg, SwkShape3D shape)
        throws TclException {
        setTags(interp, arg, false, shape);
    }

    void setTags(Interp interp, TclObject arg, boolean clearFirst,
        SwkShape3D shape) throws TclException {
        TclObject[] tagList = TclList.getElements(interp, arg);
        Tag tag = null;

        for (int i = 0; i < tagList.length; i++) {
            try {
                TclInteger.get(interp, tagList[i]);
                throw new TclException(interp, "tag cannot be an integer");
            } catch (TclException tclE) {
            }
        }

        interp.resetResult();

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
                tag = new Tag(tagList[i].toString());
                tagHash.put(tagList[i].toString(), tag);
            }

            tag.tagShapes.put(shape, shape);
            shape.tags.put(tag, tag);
        }
    }

    void removeTags(Interp interp, TclObject arg, boolean clearFirst,
        SwkShape3D shape) throws TclException {
        TclObject[] tagList = TclList.getElements(interp, arg);
        Tag tag = null;

        for (int i = 0; i < tagList.length; i++) {
            tag = (Tag) tagHash.get(tagList[i].toString());
            tag.tagShapes.remove(shape);
            shape.tags.remove(tag);
        }
    }

    public String itemGet(Interp interp, TclObject argv, SwkShape3D SwkShape3D)
        throws TclException {
        return ("");
    }

    public TclObject getTags(Interp interp, SwkShape3D shape)
        throws TclException {
        Tag tag;
        TclObject list = TclList.newInstance();

        String tagString = null;

        Enumeration e = shape.tags.elements();

        while (e.hasMoreElements()) {
            tag = (Tag) e.nextElement();
            TclList.append(interp, list, TclString.newInstance(tag.name));
        }

        return list;
    }

    public TclObject[] scanCanvasForTags(double x1, double y1) {
        SwkShape3D SwkShape3D = null;
        Hashtable shapeHash = new Hashtable();
        String tagOrId = null;
        Enumeration tags = null;
        Enumeration e = null;
        lastShapeScanned = null;

        SwkShape3D nextShape = null;
        SwkShape3D = firstShape;
        nextShape = firstShape;

        while (nextShape != null) {
            SwkShape3D = nextShape;
            nextShape = SwkShape3D.next;

            if (SwkShape3D.shape == null) {
                if (!SwkShape3D.hitShape(x1, y1)) {
                    continue;
                }
            } else {
                /* FIXME
                Rectangle bounds = SwkShape3D.shape.getBounds();

                if (!bounds.contains(x1, y1)) {
                    continue;
                }

                if (!SwkShape3D.shape.contains(x1, y1)) {
                    continue;
                }
                 **/
            }

            lastShapeScanned = SwkShape3D;

            tagOrId = String.valueOf(SwkShape3D.id);
            shapeHash.put(tagOrId, tagOrId);
            tags = SwkShape3D.tags.elements();

            while (tags.hasMoreElements()) {
                tagOrId = new String(((Tag) tags.nextElement()).name + " " +
                        String.valueOf(SwkShape3D.id));
                shapeHash.put(tagOrId, tagOrId);
            }
        }

        if (shapeHash.size() == 0) {
            return null;
        }

        TclObject[] tagOrIds = new TclObject[shapeHash.size()];

        e = shapeHash.elements();

        int i = 0;

        while (e.hasMoreElements()) {
            tagOrIds[i++] = TclString.newInstance((String) e.nextElement());
        }

        return tagOrIds;
    }

    void setResourceDefaults() {
        String keyName;
        TclObject tObj;

        if (resourceDB == null) {
            return;
        }

        Enumeration e = SwkCanvas3D.resourceDB.keys();

        while (e.hasMoreElements()) {
            TclObject list1 = TclList.newInstance();
            keyName = (String) e.nextElement();

            if (keyName == null) {
                continue;
            }

            ResourceObject ro = (ResourceObject) SwkCanvas3D.resourceDB.get(keyName);

            if (ro == null) {
                continue;
            }

            tObj = TclString.newInstance(keyName);

            try {
                try {
                    ro.defaultVal = SwkCanvas3DWidgetCmd.jget(interp, this, tObj);
                } catch (IllegalComponentStateException icsE) {
                    continue;
                }
            } catch (TclException tclE) {
                continue;
            }
        }
    }

    public void setBorderWidth(double borderWidth) {
        this.borderWidth = borderWidth;
    }

    public double getBorderWidth() {
        return (borderWidth);
    }

    public String getRelief() {
        return ("");
    }

    public boolean isFocusTraversable() {
        return true;
    }

    public SwkMouseListener getMouseListener() {
        return (mouseListener);
    }

    public void setMouseListener(SwkMouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public SwkFocusListener getFocusListener() {
        return (focusListener);
    }

    public void setFocusListener(SwkFocusListener focusListener) {
        this.focusListener = focusListener;
    }

    public SwkComponentListener getComponentListener() {
        return (componentListener);
    }

    public void setComponentListener(SwkComponentListener componentListener) {
        this.componentListener = componentListener;
    }

    public SwkChangeListener getChangeListener() {
        return (changeListener);
    }

    public void setChangeListener(SwkChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public SwkKeyListener getKeyListener() {
        return (keyListener);
    }

    public void setKeyListener(SwkKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    public SwkKeyCommandListener getKeyCommandListener() {
        return (keyCommandListener);
    }

    public void setKeyCommandListener(SwkKeyCommandListener keyCommandListener) {
        this.keyCommandListener = keyCommandListener;
    }

    public SwkMouseMotionListener getMouseMotionListener() {
        return (mouseMotionListener);
    }

    public void setMouseListener(SwkMouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

    public void setClassName(String className) {
        this.className = new String(className);
    }

    public String getClassName() {
        return (className);
    }

    private static void initResources() {
        ResourceObject resourceObject = null;

        resourceObject = new ResourceObject("background", "Background");
        resourceDB.put("-bg", resourceObject);
    }

    class Tag {
        int id = -1;
        String name = null;
        Hashtable tagShapes = new Hashtable();

        Tag(String name) {
            this.name = new String(name);
            tagVec.addElement(this);
            id = tagVec.size() - 1;
        }
    }
}
