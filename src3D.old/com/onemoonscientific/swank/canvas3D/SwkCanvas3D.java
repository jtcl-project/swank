/*
 * Copyright (c) 2000 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
*/
package com.onemoonscientific.swank.canvas3D;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

//import java.io.IOException;
//import java.net.URL;

import javax.swing.*;

import java.awt.geom.*;
import java.awt.font.*;
import java.awt.datatransfer.*;
import tcl.lang.*;
import com.onemoonscientific.swank.*;


import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;

//  end of SpecialImports
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/** Class for objects which represent a Swank swkcanvas widget. */
public class SwkCanvas3D extends javax.swing.JPanel implements SwkWidget, Printable,Scrollable
    {
    static Hashtable resourceDB = null;
    String name = null;
    String className = null;
    LinkedList children = null;
    Vector virtualBindings = null;
    int active = 0;
    boolean created = false;
    TclObject tclObject = null;
    final Interp interp;
    Insets emptyBorderInsets = new Insets(0, 0, 0, 0);
    Vector tagList = new Vector();
    Dimension minimumSize = null;
    
            String jhelptarget="";
        
            Color insertBackground;
        
        int insertWidth = 0;
        
        int insertBorderWidth = 0;
        
        int insertOffTime = 0;
        
        int insertOnTime = 0;
        
            float anchor[] = {0.0f,0.0f};
        
            int borderWidth = 0;
        
            Color highlightBackground=Color.white;
        
            Color highlightColor=Color.red;
        
            int highlightThickness;
        
            int padx;
        
            int pady;
        
        String relief=null;
        
            String xScrollCommand=null;
        
            String yScrollCommand=null;
        
	Graphics g1=null;
    BasicStroke stroke = new BasicStroke();
   double zoom = 1.0;
    AffineTransform canvasTransform = new AffineTransform();
    Hashtable transformerHash = new Hashtable();
    Point2D transMouse = new Point2D.Double();
    Point2D origMouse = new Point2D.Double();


FontRenderContext fRC = null;
int lastShapeId=0;
SwkShape3D currentShape = null;
Point currentPt = new Point(0,0);
String currentTag = null;
String previousTag = null;
TclObject currentTags[]=null;
TclObject previousTags[]=null;

Hashtable focusHash=null;
Hashtable mouseHash=null;
Hashtable mouseMotionHash = null;
Hashtable keyHash=null;
Hashtable tagHash= new Hashtable();
Vector tagVec = new Vector();


            int swkwidth=1;
            int swkheight=1;
        
        SwkMouseListener mouseListener = null;
        SwkKeyListener keyListener = null;
        SwkKeyCommandListener keyCommandListener = null;
        SwkFocusListener focusListener = null;
        SwkComponentListener componentListener = null;
        SwkChangeListener changeListener = null;
        SwkMouseMotionListener mouseMotionListener = null;
	int mouseX = 0;
	int mouseY = 0;
   SimpleUniverse universe = null;
    View view = null;
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

    
    static String validCmds[] = {"-alignmentx",
"-alignmenty",
"-anchor",
"-autoscrolls",
"-background",
"-bd",
"-bg",
"-border",
"-borderwidth",
"-bounds",
"-class",
"-cursor",
"-debuggraphicsoptions",
"-doublebuffered",
"-enabled",
"-fg",
"-focusable",
"-focuscycleroot",
"-focustraversalkeysenabled",
"-focustraversalpolicyprovider",
"-font",
"-foreground",
"-height",
"-highlightbackground",
"-highlightcolor",
"-highlightthickness",
"-ignorerepaint",
"-inheritspopupmenu",
"-insertbackground",
"-insertborderwidth",
"-insertofftime",
"-insertontime",
"-insertwidth",
"-jhelptarget",
"-location",
"-maximumsize",
"-minimumsize",
"-name",
"-opaque",
"-padx",
"-pady",
"-preferredsize",
"-relief",
"-requestfocusenabled",
"-scrollregion",
"-size",
"-tooltiptext",
"-verifyinputwhenfocustarget",
"-visible",
"-width",
"-xscrollcommand",
"-yscrollcommand",
};
    private static final int OPT_ALIGNMENTX = 0;
private static final int OPT_ALIGNMENTY = 1;
private static final int OPT_ANCHOR = 2;
private static final int OPT_AUTOSCROLLS = 3;
private static final int OPT_BACKGROUND = 4;
private static final int OPT_BD = 5;
private static final int OPT_BG = 6;
private static final int OPT_BORDER = 7;
private static final int OPT_BORDERWIDTH = 8;
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
private static final int OPT_FOCUSTRAVERSALPOLICYPROVIDER = 19;
private static final int OPT_FONT = 20;
private static final int OPT_FOREGROUND = 21;
private static final int OPT_HEIGHT = 22;
private static final int OPT_HIGHLIGHTBACKGROUND = 23;
private static final int OPT_HIGHLIGHTCOLOR = 24;
private static final int OPT_HIGHLIGHTTHICKNESS = 25;
private static final int OPT_IGNOREREPAINT = 26;
private static final int OPT_INHERITSPOPUPMENU = 27;
private static final int OPT_INSERTBACKGROUND = 28;
private static final int OPT_INSERTBORDERWIDTH = 29;
private static final int OPT_INSERTOFFTIME = 30;
private static final int OPT_INSERTONTIME = 31;
private static final int OPT_INSERTWIDTH = 32;
private static final int OPT_JHELPTARGET = 33;
private static final int OPT_LOCATION = 34;
private static final int OPT_MAXIMUMSIZE = 35;
private static final int OPT_MINIMUMSIZE = 36;
private static final int OPT_NAME = 37;
private static final int OPT_OPAQUE = 38;
private static final int OPT_PADX = 39;
private static final int OPT_PADY = 40;
private static final int OPT_PREFERREDSIZE = 41;
private static final int OPT_RELIEF = 42;
private static final int OPT_REQUESTFOCUSENABLED = 43;
private static final int OPT_SCROLLREGION = 44;
private static final int OPT_SIZE = 45;
private static final int OPT_TOOLTIPTEXT = 46;
private static final int OPT_VERIFYINPUTWHENFOCUSTARGET = 47;
private static final int OPT_VISIBLE = 48;
private static final int OPT_WIDTH = 49;
private static final int OPT_XSCROLLCOMMAND = 50;
private static final int OPT_YSCROLLCOMMAND = 51;

    static TreeMap validCmdsTM = new TreeMap();
    static {
         for (int i=0;i< validCmds.length;i++) {
               validCmdsTM.put(validCmds[i],new Integer(i));
         }
    }


    public SwkCanvas3D(final Interp interp, String name, String className) {
        this.name = name.intern();
        this.interp = interp;

        if (resourceDB == null) {
            resourceDB = new Hashtable();
            initResources();
        }

        

        addMouseListener(new MouseListener() {
                public void mousePressed(MouseEvent mEvent) {
                    transformMouse(mEvent);
                    currentTags = getTagFromEvent(mEvent);

                    if (currentTags == null) {
                        return;
                    }

                    for (int i = 0; i < currentTags.length; i++) {
                        currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                        processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.PRESS);
                    }
                }

                public void mouseReleased(MouseEvent mEvent) {
                    transformMouse(mEvent);
                    previousTags = currentTags;

                    /*
                     if (previousTags != null) {
                         for (int i=0;i<previousTags.length;i++) {
                             System.out.println("prev "+previousTags[i].toString());
                         }
                     }
                     **/
                    currentTags = getTagFromEvent(mEvent);

                    if (currentTags != null) {
                        // for (int i=0;i<currentTags.length;i++) {
                        //   System.out.println("current "+currentTags[i].toString());
                        //}
                    } else {
                        currentTag = null;
                        setEventCurrentShape(null);
                    }

                    checkForMouseExit(mEvent);

                    if (currentTags == null) {
                        return;
                    }

                    for (int i = 0; i < currentTags.length; i++) {
                        currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                        processMouse(mEvent, SwkBinding.MOUSE,
                            SwkBinding.RELEASE);
                    }
                }

                public void mouseClicked(MouseEvent mEvent) {
                    transformMouse(mEvent);
                    currentTags = getTagFromEvent(mEvent);

                    if (currentTags == null) {
                        return;
                    }

                    for (int i = 0; i < currentTags.length; i++) {
                        currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                        processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.CLICK);
                    }
                }

                public void mouseEntered(MouseEvent mEvent) {
                    transformMouse(mEvent);
                }

                public void mouseExited(MouseEvent mEvent) {
                    transformMouse(mEvent);
                }
            });

        addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent mEvent) {
                    transformMouse(mEvent);
                    processMouseMotion(mEvent);
                }

                public void mouseMoved(MouseEvent mEvent) {
                    transformMouse(mEvent);
                    processMouseMotion(mEvent);
                }
            });

        setTransferHandler(new ImageSelection());


        addFocusListener(new FocusAdapter () {
            public void focusGained(FocusEvent fEvent) {
		FocusCmd.setFocusWindow(getName());
            }
        }
        );
        addMouseListener(new MouseAdapter () {
            public void mouseClicked(MouseEvent mEvent) {
                mEvent.getComponent().requestFocus();
            }
        }
        );
        addMouseMotionListener(new MouseMotionAdapter () {
            public void mouseMoved(MouseEvent mEvent) {
		mouseX = mEvent.getX();
		mouseY = mEvent.getY();
            }
            public void mouseDragged(MouseEvent mEvent) {
		mouseX = mEvent.getX();
		mouseY = mEvent.getY();
            }
        }
        );
    
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


    public static void getWidgetOptions(Interp interp) throws TclException {
           TclObject result = TclList.newInstance();
           for (int i=0, n=validCmds.length;i<n;i++) {
                 TclList.append(interp,result,TclString.newInstance(validCmds[i]));
           }
           interp.setResult(result);
    }
    public static void getWidgetCmds(Interp interp) throws TclException {
           TclObject result = TclList.newInstance();
           String[] validCmds = SwkCanvas3DWidgetCmd.getValidCmds();
           for (int i=0, n=validCmds.length;i<n;i++) {
                 TclList.append(interp,result,TclString.newInstance(validCmds[i]));
           }
           interp.setResult(result);
    }

    public int print(Graphics g, final PageFormat pageFormat, int pageIndex)
        throws PrinterException {
        int result = NO_SUCH_PAGE;
        if (pageIndex == 0) {
            Graphics2D g2 = (Graphics2D) g;
            double pX = pageFormat.getImageableX();
            double pY = pageFormat.getImageableY();
            double pW = pageFormat.getImageableWidth();
            double pH = pageFormat.getImageableHeight();

            Dimension dimSize = getSize();
            double sx = 1.0;
            double sy = 1.0;

            if (dimSize.width > pW) {
                sx = pW / dimSize.width;
                sy = pW / dimSize.width;
            }
            if ((dimSize.height*sx) > pH) {
                sy = sx*pH / (dimSize.height*sx);
                sx = sx*pH / (dimSize.height*sx);
            }

            g2.translate(pX,pY);
            g2.scale(sx, sy);
            boolean wasBuffered = SwankUtil.disableDoubleBuffering(this);
            paint(g2);
            SwankUtil.restoreDoubleBuffering(this, wasBuffered);
            result = PAGE_EXISTS;
        } 
        return result;
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

     
            public void setJHelpTarget(String jhelptarget) {
                this.jhelptarget = jhelptarget;
                SwankUtil.setJHelpTarget(this,jhelptarget);
            }
            public String getJHelpTarget() {
                return(jhelptarget);
            }
        
        public void setInsertBackground(Color insertBackground) {
            this.insertBackground = insertBackground;
        }
        public Color getInsertBackground() {
            return(insertBackground);
        }
        
        public void setInsertWidth(int insertWidth) {
            this.insertWidth = insertWidth;
        }
        public int getInsertWidth() {
            return(insertWidth);
        }
        
        public void setInsertBorderWidth(int insertBorderWidth) {
            this.insertBorderWidth = insertBorderWidth;
        }
        public int getInsertBorderWidth() {
            return(insertBorderWidth);
        }
        
        public void setInsertOffTime(int insertOffTime) {
            this.insertOffTime = insertOffTime;
        }
        public int getInsertOffTime() {
            return(insertOffTime);
        }
        
        public void setInsertOnTime(int insertOnTime) {
            this.insertOnTime = insertOnTime;
        }
        public int getInsertOnTime() {
            return(insertOnTime);
        }
        
        public void setScrollRegion(int scrollRegion[][]) {
        /*     scrollRegion[0][0] = scrollRegion[0][0];
             scrollRegion[0][1] = scrollRegion[0][1];
             scrollRegion[1][0] = scrollRegion[1][0];
             scrollRegion[1][1] = scrollRegion[1][1];
         */

        }
        public int[][] getScrollRegion() {
          //  return(scrollRegion);
            return new int[2][2];
        }
        
            public void setAnchor(float anchor[]) 
            {
                this.anchor = anchor;
            }
            public float[] getAnchor() 
            {
                return(anchor);
            }
        
                public void setBorderWidth(double borderWidth) {
                   this.borderWidth = (int) borderWidth;
                   if (!(getBorder() instanceof SwkBorder)) {
                      setBorder(new SwkBorder());
                   }
                } 
            
            public int getBorderWidth() {
                return(borderWidth);
            }
        
        public void setHighlightBackground(Color highlightBackground) {
            this.highlightBackground = highlightBackground;
        }
        public Color getHighlightBackground() {
            return(highlightBackground);
        }
        
        public void setHighlightColor(Color highlightColor) {
            this.highlightColor = highlightColor;
        }
        public Color getHighlightColor() {
            return(highlightColor);
        }
        
        public void setHighlightThickness(int highlightThickness) {
            this.highlightThickness = highlightThickness;
        }
        public int getHighlightThickness() {
            return(highlightThickness);
        }
        
        public void setPadx(int padx) {
            this.padx = (int) padx;
	    emptyBorderInsets.left = this.padx;
	    emptyBorderInsets.right = this.padx;
	    minimumSize = null;	
        }
        public int getPadx() {
            return(padx);
        }
        
        public void setPady(int pady) {
            this.pady =(int)  pady;
	    emptyBorderInsets.top = this.pady;
	    emptyBorderInsets.bottom = this.pady;
            minimumSize = null;
        }
        public int getPady() {
            return(pady);
        }
        
            public void setRelief(String relief) {
                if (!(getBorder() instanceof SwkBorder)) {
                    setBorder(new SwkBorder());
                }
                this.relief = relief.intern();
            }
            public String getRelief() {
                if (relief == null) {
                    relief = "";
                }
                return(relief);
            }
        
        public void setXScrollCommand(String xScrollCommand) {
            this.xScrollCommand = xScrollCommand.intern();
        }
        public String getXScrollCommand() {
            return(xScrollCommand);
        }
        
        public void setYScrollCommand(String yScrollCommand) {
            this.yScrollCommand = yScrollCommand.intern();
        }
        public String getYScrollCommand() {
            return(yScrollCommand);
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

       
    public void paintComponent(Graphics g) {
    }

    public void setZoom(double newZoom) {
        zoom = newZoom;
    }

    public double getZoom() {
        return zoom;
    }

    public Dimension getPreferredScrollableViewportSize() {
        Dimension dim = new Dimension(swkwidth, swkheight);
        Dimension vdim;

        try {
            JViewport viewport = Widgets.getViewport(this);

            if (viewport != null) {
                vdim = viewport.getSize();

                if (swkwidth < vdim.width) {
                    dim.width = vdim.width;
                }

                if (swkheight < vdim.height) {
                    dim.height = vdim.height;
                }
            } else {
            }
        } catch (TclException tclE) {
        }

        return (dim);
    }

    public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect,
        int direction, int orientation) {
        return (1);
    }

    public boolean getScrollableTracksViewportWidth() {
        return (false);
    }

    public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect,
        int direction, int orientation) {
        return (1);
    }

    public boolean getScrollableTracksViewportHeight() {
        return (false);
    }

    void processMouseMotion(MouseEvent mEvent) {
        previousTags = currentTags;

        /*
         if (previousTags != null) {
             for (int i=0;i<previousTags.length;i++) {
                 System.out.println("prev "+previousTags[i].toString());
             }
         }
         */
        int buttonMask = (InputEvent.BUTTON1_MASK + InputEvent.BUTTON2_MASK +
            InputEvent.BUTTON3_MASK);
        int mods = mEvent.getModifiers();

        if ((mods & buttonMask) == 0) {
            currentTags = getTagFromEvent(mEvent);

            /*
             if (currentTags != null) {
                 for (int i=0;i<currentTags.length;i++) {
                     System.out.println("current "+currentTags[i].toString());
                 }
             }
             **/
            checkForMouseExit(mEvent);
            currentShape = getLastShapeScanned();
            checkForMouseEnter(mEvent);
        }

        if (currentTags != null) {
            for (int i = 0; i < currentTags.length; i++) {
                currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                processMouse(mEvent, SwkBinding.MOUSEMOTION, SwkBinding.MOTION);
            }
        }
    }

    void checkForMouseExit(MouseEvent mEvent) {
        boolean stillPresent = true;

        if ((currentTags == null) && (previousTags != null)) {
            for (int i = 0; i < previousTags.length; i++) {
                previousTag = getTagOrIDFromTagID(previousTags[i].toString());
                processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.EXIT);
            }
        } else if ((currentTags != null) && (previousTags != null)) {
            for (int i = 0; i < previousTags.length; i++) {
                previousTag = previousTags[i].toString();
                stillPresent = false;

                for (int j = 0; j < currentTags.length; j++) {
                    String thisTag = currentTags[j].toString();

                    if (previousTags[i].toString().equals(thisTag)) {
                        stillPresent = true;

                        break;
                    }
                }

                if (!stillPresent) {
                    previousTag = getTagOrIDFromTagID(previousTag);
                    processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.EXIT);
                }
            }
        }
    }

    void checkForMouseEnter(MouseEvent mEvent) {
        boolean wasPresent = true;

        if ((currentTags != null) && (previousTags == null)) {
            for (int i = 0; i < currentTags.length; i++) {
                currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.ENTER);
            }
        } else if ((currentTags != null) && (previousTags != null)) {
            for (int i = 0; i < currentTags.length; i++) {
                String thisTag = currentTags[i].toString();
                wasPresent = false;

                for (int j = 0; j < previousTags.length; j++) {
                    if (previousTags[j].toString().equals(thisTag)) {
                        wasPresent = true;

                        break;
                    }
                }

                if (!wasPresent) {
                    currentTag = getTagOrIDFromTagID(thisTag);
                    processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.ENTER);
                }
            }
        }
    }

    public void processMouse(MouseEvent e, int type, int subtype) {
        BindEvent bEvent = new BindEvent(interp, this, (EventObject) e, type,
                subtype, currentTag, previousTag, currentShape);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, int type, int subtype,
        String currentTag, String previousTag, SwkShape3D eventCurrentShape) {
        MouseEvent e = (MouseEvent) eventObject;

        //   System.out.println("processE "+type+" "+subtype+" C "+currentTag+" P "+previousTag);       
        if (e.isConsumed()) {
            return;
        }

        setEventCurrentShape(eventCurrentShape);

        // System.out.println("processE "+type+" "+subtype+" C "+currentTag+" P "+previousTag);
        Vector bindings = null;

        if (type == SwkBinding.FOCUS) {
            if ((currentTag != null) && (focusHash != null)) {
                bindings = (Vector) focusHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.MOUSE) {
            if (subtype == SwkBinding.EXIT) {
                if ((previousTag != null) && (mouseHash != null)) {
                    bindings = (Vector) mouseHash.get(previousTag);
                }
            } else {
                if ((currentTag != null) && (mouseHash != null)) {
                    bindings = (Vector) mouseHash.get(currentTag);
                }
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.MOUSEMOTION) {
            if ((currentTag != null) && (mouseMotionHash != null)) {
                bindings = (Vector) mouseMotionHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.KEY) {
            if ((currentTag != null) && (keyHash != null)) {
                bindings = (Vector) keyHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        }

        SwkBinding binding;
        int buttons = e.getButton();
        int mods = e.getModifiersEx();

        //    System.out.println("event "+e);
        //    System.out.println("emods "+mods);
        int i;

        for (i = 0; i < bindings.size(); i++) {
            binding = (SwkBinding) bindings.elementAt(i);

            //  System.out.println(type+" "+subtype+" "+" "+binding.type+" "+binding.subtype);
            if (binding.subtype != subtype) {
                continue;
            }

            if ((subtype != SwkBinding.ENTER) && (subtype != SwkBinding.EXIT)) {
                if ((type == SwkBinding.MOUSE) && (e.getClickCount() > 0) &&
                        (binding.count != e.getClickCount())) {
                    continue;
                }

                //System.out.println(binding.detail+" "+binding.mod+" "+mods+" "+(binding.mod  & mods));
                // if ((binding.mod & buttonMask) != (mods & buttonMask)) {continue;}
                if (type == SwkBinding.MOUSEMOTION) {
                    if (!SwkMouseMotionListener.checkButtonState(e,
                                binding.mod, mods)) {
                        continue;
                    }
                } else if (type == SwkBinding.MOUSE) {
                    if (!SwkMouseMotionListener.checkButtons(binding.detail,
                                buttons)) {
                        continue;
                    }
                }

                //  System.out.println("check mods");
                if (!SwkMouseMotionListener.checkMods(binding.mod, mods)) {
                    continue;
                }
            }

            if ((binding.command != null) && (binding.command.length() != 0)) {
                try {
                    BindCmd.doCmd(interp, binding.command, e);
                } catch (TclException tclE) {
                    if (tclE.getCompletionCode() == TCL.BREAK) {
                        e.consume();

                        return;
                    } else {
                        interp.addErrorInfo("\n    (\"binding\" script)");
                        interp.backgroundError();
                    }
                }
            }
        }
    }

    String getTagOrIDFromTagID(String tagID) {
        int spacePos = tagID.indexOf(" ");

        if (spacePos == -1) {
            return tagID;
        } else {
            return tagID.substring(0, spacePos);
        }
    }

    public TclObject[] getTagFromEvent(MouseEvent mEvent) {
        currentTag = null;

        return (scanCanvasForTags((double) mEvent.getX(), (double) mEvent.getY()));
    }
    public void removeBindingsForTag(String tagName) {
        focusHash.remove(tagName);
        mouseHash.remove(tagName);
        mouseMotionHash.remove(tagName);
        keyHash.remove(tagName);
    }

   public void setupBinding(Interp interp, SwkBinding newBinding,
        String tagName) {
        Vector bindVec = null;

        if (newBinding.type == SwkBinding.FOCUS) {
            if (focusHash == null) {
                focusHash = new Hashtable();
                bindVec = new Vector(2);
                focusHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) focusHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    focusHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.MOUSE) {
            if (mouseHash == null) {
                mouseHash = new Hashtable();
                bindVec = new Vector(2);
                mouseHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) mouseHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    mouseHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.MOUSEMOTION) {
            if (mouseMotionHash == null) {
                mouseMotionHash = new Hashtable();
                bindVec = new Vector(2);
                mouseMotionHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) mouseMotionHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    mouseMotionHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.KEY) {
            if (keyHash == null) {
                keyHash = new Hashtable();
                bindVec = new Vector(2);
                keyHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) keyHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    keyHash.put(tagName, bindVec);
                }
            }
        }

        if (bindVec != null) {
            if (!newBinding.add) {
                for (int i = 0; i < bindVec.size(); i++) {
                    SwkBinding binding = (SwkBinding) bindVec.elementAt(i);

                    if (binding.equals(newBinding)) {
                        bindVec.setElementAt(newBinding, i);

                        return;
                    }
                }
            }

            bindVec.addElement(newBinding);
        }
    }


    public void drawBox(int x1, int y1, int width, int height) {
        Graphics g = getGraphics();

        g.setXORMode(getBackground());
        g.drawRect(x1, y1, width, height);
        g.dispose();
    }

    public void copyImageToClipboard(Clipboard clipboard) {
        TransferHandler handler = getTransferHandler();
        handler.exportToClipboard(this, clipboard, TransferHandler.COPY);
    }

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


    FontRenderContext getFontRenderContext() {
        return fRC;
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


            public Dimension getMinimumSize() {
            int scrollRegion[][] = getScrollRegion();
Dimension dSize = new Dimension(scrollRegion[1][0]-scrollRegion[0][0],scrollRegion[1][1]-scrollRegion[0][1]); 
                if (dSize.width < swkwidth) {
                    dSize.width = swkwidth;
                }
                if (dSize.height < swkheight) {
                    dSize.height = swkheight;
                }
                return(dSize);
            }
            public Dimension getPreferredSize() {
                return(getMinimumSize());
            }
            public void setSwkHeight(int height) {
                this.swkheight = height;
            }
            public int getSwkHeight() {
                Dimension size = getSize();
                return(size.height);
            }
            public void setSwkWidth(int width) {
                this.swkwidth = width;
            }
            public int getSwkWidth() {
                Dimension size = getSize();
                return(size.width);
            }
        
        public boolean isFocusTraversable() {
            return true;
        }
        public boolean isCreated() {
            return created;
        }
        public void setCreated(boolean state) {
		created = state;
        }
	public int getMouseX() {
           return mouseX;
        }
	public int getMouseY() {
           return mouseY;
        }
    
            public SwkMouseListener getMouseListener() {
                return (mouseListener);
            }
            public void setMouseListener(SwkMouseListener mouseListener) {
                this.mouseListener = mouseListener;
            }
        
        public SwkFocusListener getFocusListener() {
            return(focusListener);
        }
        public void setFocusListener(SwkFocusListener focusListener) {
            this.focusListener = focusListener;
        }
        public SwkComponentListener getComponentListener() {
            return(componentListener);
        }
        public void setComponentListener(SwkComponentListener componentListener) {
            this.componentListener = componentListener;
        }
        public SwkChangeListener getChangeListener() {
            return(changeListener);
        }
        public void setChangeListener(SwkChangeListener changeListener) {
            this.changeListener = changeListener;
        }
        
        public SwkKeyListener getKeyListener() {
            return(keyListener);
        }
        public void setKeyListener(SwkKeyListener keyListener) {
            this.keyListener = keyListener;
        }
        public SwkKeyCommandListener getKeyCommandListener() {
            return(keyCommandListener);
        }
        public void setKeyCommandListener(SwkKeyCommandListener keyCommandListener) {
            this.keyCommandListener = keyCommandListener;
        }
        
        public SwkMouseMotionListener getMouseMotionListener() {
            return(mouseMotionListener);
        }
        public void setMouseListener(SwkMouseMotionListener mouseMotionListener) {
            this.mouseMotionListener = mouseMotionListener;
        }
        
    
    public void setClassName(String className) {
        this.className = className.intern();
    }
    public String getClassName() {
        return(className);
    }
    
    
        public void close() throws TclException {}
    
    


    void jgetAll(Interp interp) throws TclException {
        if (EventQueue.isDispatchThread()) {
              System.out.println("never  run on event thread");
        }
            JGetAll jgetAll = new JGetAll(this);
	    try {
                SwingUtilities.invokeAndWait(jgetAll);
	    } catch (InterruptedException iE) {
		throw new TclException(interp,iE.toString());
	    } catch (Exception  e) {
		throw new TclException(interp,e.toString());
	    }
            getAllConfigurations(interp,jgetAll.roValues);
    }
   class JGetAll implements Runnable {
        SwkCanvas3D swkcanvas;
        ArrayList roValues = null;
        JGetAll( SwkCanvas3D swkcanvas) {
                this.swkcanvas = swkcanvas;
        }
        public void run() {
		roValues = getAllConfigurations();
        }
    }
    ArrayList getAllConfigurations() {
           int nCmds = validCmds.length;
           ArrayList results = new ArrayList();
           Enumeration e = SwkCanvas3D.resourceDB.keys();
           while (e.hasMoreElements()) {
                 String keyName = (String) e.nextElement();
                 ResourceObject ro = (ResourceObject) resourceDB.get(keyName);
                 if (ro == null) {
                    continue;
                 }
                 String value = jget(ro.optNum);
                 results.add(keyName);
                 results.add(ro);
                 results.add(value);
           }
           return results;
    }
    void getAllConfigurations(Interp interp,ArrayList roValues) throws TclException {
                TclObject list2 = TclList.newInstance();
                for (int i=0,n=roValues.size();i<n;i += 3) {
                    TclObject list1 = TclList.newInstance();
                    String keyName = (String) roValues.get(i);
                    ResourceObject ro = (ResourceObject) roValues.get(i+1);
                    String value = (String) roValues.get(i+2);

                    if (ro == null) {
                        continue;
                    }

                    TclObject tObj = TclString.newInstance(keyName);

                    TclList.append(interp, list1, tObj);
                    TclList.append(interp, list1, TclString.newInstance(ro.resource));
                    TclList.append(interp, list1, TclString.newInstance(ro.className));

                    if (ro.defaultVal == null) {
                        TclList.append(interp, list1, TclString.newInstance(""));
                    } else {
                        TclList.append(interp, list1, TclString.newInstance(ro.defaultVal));
                    }

                    if (value == null) {
                        value = "";
                    }

                    TclList.append(interp, list1, TclString.newInstance(value));
                    TclList.append(interp, list2, list1);
                }

                interp.setResult(list2);
    }

    public void setValues(Setter setter,int opt) {
         
        switch (opt) {
case OPT_ALIGNMENTX: 
			this.setAlignmentX(setter.fValue);
                       break;
                       case OPT_ALIGNMENTY: 
			this.setAlignmentY(setter.fValue);
                       break;
                       case OPT_ANCHOR: 
			this.setAnchor((float[]) setter.oValue);
                       break;
                       case OPT_AUTOSCROLLS: 
			this.setAutoscrolls(setter.bValue);
                       break;
                       case OPT_BACKGROUND: 
			this.setBackground((Color) setter.oValue);
                       break;
                       case OPT_BD: 
			this.setBorderWidth(setter.dValue);
                       break;
                       case OPT_BG: 
			this.setBackground((Color) setter.oValue);
                       break;
                       case OPT_BORDER: 
			this.setBorderWidth(setter.dValue);
                       break;
                       case OPT_BORDERWIDTH: 
			this.setBorderWidth(setter.dValue);
                       break;
                       case OPT_BOUNDS: 
			this.setBounds((Rectangle) setter.oValue);
                       break;
                       case OPT_CLASS: 
			this.setClassName(setter.sValue);
                       break;
                       case OPT_CURSOR: 
			this.setCursor((Cursor) setter.oValue);
                       break;
                       case OPT_DEBUGGRAPHICSOPTIONS: 
			this.setDebugGraphicsOptions(setter.iValue);
                       break;
                       case OPT_DOUBLEBUFFERED: 
			this.setDoubleBuffered(setter.bValue);
                       break;
                       case OPT_ENABLED: 
			this.setEnabled(setter.bValue);
                       break;
                       case OPT_FG: 
			this.setForeground((Color) setter.oValue);
                       break;
                       case OPT_FOCUSABLE: 
			this.setFocusable(setter.bValue);
                       break;
                       case OPT_FOCUSCYCLEROOT: 
			this.setFocusCycleRoot(setter.bValue);
                       break;
                       case OPT_FOCUSTRAVERSALKEYSENABLED: 
			this.setFocusTraversalKeysEnabled(setter.bValue);
                       break;
                       case OPT_FOCUSTRAVERSALPOLICYPROVIDER: 
			this.setFocusTraversalPolicyProvider(setter.bValue);
                       break;
                       case OPT_FONT: 
			this.setFont((Font) setter.oValue);
                       break;
                       case OPT_FOREGROUND: 
			this.setForeground((Color) setter.oValue);
                       break;
                       case OPT_HEIGHT: 
			this.setSwkHeight(setter.iValue);
                       break;
                       case OPT_HIGHLIGHTBACKGROUND: 
			this.setHighlightBackground((Color) setter.oValue);
                       break;
                       case OPT_HIGHLIGHTCOLOR: 
			this.setHighlightColor((Color) setter.oValue);
                       break;
                       case OPT_HIGHLIGHTTHICKNESS: 
			this.setHighlightThickness(setter.iValue);
                       break;
                       case OPT_IGNOREREPAINT: 
			this.setIgnoreRepaint(setter.bValue);
                       break;
                       case OPT_INHERITSPOPUPMENU: 
			this.setInheritsPopupMenu(setter.bValue);
                       break;
                       case OPT_INSERTBACKGROUND: 
			this.setInsertBackground((Color) setter.oValue);
                       break;
                       case OPT_INSERTBORDERWIDTH: 
			this.setInsertBorderWidth(setter.iValue);
                       break;
                       case OPT_INSERTOFFTIME: 
			this.setInsertOffTime(setter.iValue);
                       break;
                       case OPT_INSERTONTIME: 
			this.setInsertOnTime(setter.iValue);
                       break;
                       case OPT_INSERTWIDTH: 
			this.setInsertWidth(setter.iValue);
                       break;
                       case OPT_JHELPTARGET: 
			this.setJHelpTarget(setter.sValue);
                       break;
                       case OPT_LOCATION: 
			this.setLocation((Point) setter.oValue);
                       break;
                       case OPT_MAXIMUMSIZE: 
			this.setMaximumSize((Dimension) setter.oValue);
                       break;
                       case OPT_MINIMUMSIZE: 
			this.setMinimumSize((Dimension) setter.oValue);
                       break;
                       case OPT_NAME: 
			this.setName(setter.sValue);
                       break;
                       case OPT_OPAQUE: 
			this.setOpaque(setter.bValue);
                       break;
                       case OPT_PADX: 
			this.setPadx(setter.iValue);
                       break;
                       case OPT_PADY: 
			this.setPady(setter.iValue);
                       break;
                       case OPT_PREFERREDSIZE: 
			this.setPreferredSize((Dimension) setter.oValue);
                       break;
                       case OPT_RELIEF: 
			this.setRelief(setter.sValue);
                       break;
                       case OPT_REQUESTFOCUSENABLED: 
			this.setRequestFocusEnabled(setter.bValue);
                       break;
                       case OPT_SCROLLREGION: 
			this.setScrollRegion((int[][]) setter.oValue);
                       break;
                       case OPT_SIZE: 
			this.setSize((Dimension) setter.oValue);
                       break;
                       case OPT_TOOLTIPTEXT: 
			this.setToolTipText(setter.sValue);
                       break;
                       case OPT_VERIFYINPUTWHENFOCUSTARGET: 
			this.setVerifyInputWhenFocusTarget(setter.bValue);
                       break;
                       case OPT_VISIBLE: 
			this.setVisible(setter.bValue);
                       break;
                       case OPT_WIDTH: 
			this.setSwkWidth(setter.iValue);
                       break;
                       case OPT_XSCROLLCOMMAND: 
			this.setXScrollCommand(setter.sValue);
                       break;
                       case OPT_YSCROLLCOMMAND: 
			this.setYScrollCommand(setter.sValue);
                       break;
                       }
    }
    public void configure(Interp interp, TclObject[] argv, int start) throws TclException { 
        if (EventQueue.isDispatchThread()) {
             throw new RuntimeException("Configure on eventQueue");
        }
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
           /* 
            if (ro.defaultVal == null) {
                ro.defaultVal = SwkCanvas3DConfigure.jget(interp, swkcanvas,
                        argv[i]);
            }       
          */
                 
        int opt = SwkIndex.get(interp, argv[i], validCmdsTM, "option", 0);
        switch (opt) {
case OPT_ALIGNMENTX: {
			float value = (float) TclDouble.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_ALIGNMENTY: {
			float value = (float) TclDouble.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_ANCHOR: {
			Object value  = SwankUtil.getAnchor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_AUTOSCROLLS: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_BACKGROUND: {
			Color value = SwankUtil.getColor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_BD: {
			double value = SwankUtil.getTkSizeD(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_BG: {
			Color value = SwankUtil.getColor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_BORDER: {
			double value = SwankUtil.getTkSizeD(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_BORDERWIDTH: {
			double value = SwankUtil.getTkSizeD(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_BOUNDS: {
			Rectangle value = SwankUtil.getRectangle(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_CLASS: {
			String value = argv[i+1].toString();
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_CURSOR: {
			Cursor value = SwankUtil.getCursor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_DEBUGGRAPHICSOPTIONS: {
			int value = TclInteger.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_DOUBLEBUFFERED: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_ENABLED: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_FG: {
			Color value = SwankUtil.getColor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_FOCUSABLE: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_FOCUSCYCLEROOT: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_FOCUSTRAVERSALKEYSENABLED: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_FOCUSTRAVERSALPOLICYPROVIDER: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_FONT: {
			Font value = SwankUtil.getFont(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_FOREGROUND: {
			Color value = SwankUtil.getColor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_HEIGHT: {
			int value = SwankUtil.getTkSize(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_HIGHLIGHTBACKGROUND: {
			Color value = SwankUtil.getColor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_HIGHLIGHTCOLOR: {
			Color value = SwankUtil.getColor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_HIGHLIGHTTHICKNESS: {
			int value = SwankUtil.getTkSize(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_IGNOREREPAINT: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_INHERITSPOPUPMENU: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_INSERTBACKGROUND: {
			Color value = SwankUtil.getColor(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_INSERTBORDERWIDTH: {
			int value = SwankUtil.getTkSize(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_INSERTOFFTIME: {
			int value = TclInteger.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_INSERTONTIME: {
			int value = TclInteger.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_INSERTWIDTH: {
			int value = SwankUtil.getTkSize(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_JHELPTARGET: {
			String value = argv[i+1].toString();
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_LOCATION: {
			Point value = SwankUtil.getPoint(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_MAXIMUMSIZE: {
			Dimension value = SwankUtil.getDimension(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_MINIMUMSIZE: {
			Dimension value = SwankUtil.getDimension(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_NAME: {
			String value = argv[i+1].toString();
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_OPAQUE: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_PADX: {
			int value = SwankUtil.getTkSize(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_PADY: {
			int value = SwankUtil.getTkSize(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_PREFERREDSIZE: {
			Dimension value = SwankUtil.getDimension(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_RELIEF: {
			String value = SwankUtil.getTkRelief(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_REQUESTFOCUSENABLED: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_SCROLLREGION: {
			Object value  = SwankUtil.getTkRectangleCorners(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_SIZE: {
			Dimension value = SwankUtil.getDimension(interp,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_TOOLTIPTEXT: {
			String value = argv[i+1].toString();
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_VERIFYINPUTWHENFOCUSTARGET: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_VISIBLE: {
			boolean value = TclBoolean.get(interp, argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_WIDTH: {
			int value = SwankUtil.getTkSize(interp,(Component) this,argv[i+1]);
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_XSCROLLCOMMAND: {
			String value = argv[i+1].toString();
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       case OPT_YSCROLLCOMMAND: {
			String value = argv[i+1].toString();
(new Setter((SwkWidget) this,opt)).exec(value);
                       break;
                       }
                       }

        }
        SwankUtil.doWait();
        this.repaint();
    }
    String jget(final int opt) {
            
        switch (opt) {
case OPT_ALIGNMENTX:
                        return(Float.toString(this.getAlignmentX()));
                       case OPT_ALIGNMENTY:
                        return(Float.toString(this.getAlignmentY()));
                       case OPT_ANCHOR:
                        return(SwankUtil.parseAnchor(this.getAnchor()));
                       case OPT_AUTOSCROLLS:
                        return(String.valueOf(this.getAutoscrolls()));
                       case OPT_BACKGROUND:
                        return(SwankUtil.parseColor(this.getBackground()));
                       case OPT_BD:
                        return(SwankUtil.parseTkSize(this.getBorderWidth()));
                       case OPT_BG:
                        return(SwankUtil.parseColor(this.getBackground()));
                       case OPT_BORDER:
                        return(SwankUtil.parseTkSize(this.getBorderWidth()));
                       case OPT_BORDERWIDTH:
                        return(SwankUtil.parseTkSize(this.getBorderWidth()));
                       case OPT_BOUNDS:
                        return(SwankUtil.parseRectangle(this.getBounds()));
                       case OPT_CLASS:
                        return(this.getClassName());
                       case OPT_CURSOR:
                        return(SwankUtil.parseCursor(this.getCursor()));
                       case OPT_DEBUGGRAPHICSOPTIONS:
                        return(Integer.toString(this.getDebugGraphicsOptions()));
                       case OPT_DOUBLEBUFFERED:
                        return(isDoubleBuffered()?"1":"0");
                       case OPT_ENABLED:
                        return(isEnabled()?"1":"0");
                       case OPT_FG:
                        return(SwankUtil.parseColor(this.getForeground()));
                       case OPT_FOCUSABLE:
                        return(isFocusable()?"1":"0");
                       case OPT_FOCUSCYCLEROOT:
                        return(isFocusCycleRoot()?"1":"0");
                       case OPT_FOCUSTRAVERSALKEYSENABLED:
                        return(String.valueOf(this.getFocusTraversalKeysEnabled()));
                       case OPT_FOCUSTRAVERSALPOLICYPROVIDER:
                        return(isFocusTraversalPolicyProvider()?"1":"0");
                       case OPT_FONT:
                        return(SwankUtil.parseFont(this.getFont()));
                       case OPT_FOREGROUND:
                        return(SwankUtil.parseColor(this.getForeground()));
                       case OPT_HEIGHT:
                        return(SwankUtil.parseTkSize(this.getSwkHeight()));
                       case OPT_HIGHLIGHTBACKGROUND:
                        return(SwankUtil.parseColor(this.getHighlightBackground()));
                       case OPT_HIGHLIGHTCOLOR:
                        return(SwankUtil.parseColor(this.getHighlightColor()));
                       case OPT_HIGHLIGHTTHICKNESS:
                        return(SwankUtil.parseTkSize(this.getHighlightThickness()));
                       case OPT_IGNOREREPAINT:
                        return(String.valueOf(this.getIgnoreRepaint()));
                       case OPT_INHERITSPOPUPMENU:
                        return(String.valueOf(this.getInheritsPopupMenu()));
                       case OPT_INSERTBACKGROUND:
                        return(SwankUtil.parseColor(this.getInsertBackground()));
                       case OPT_INSERTBORDERWIDTH:
                        return(SwankUtil.parseTkSize(this.getInsertBorderWidth()));
                       case OPT_INSERTOFFTIME:
                        return(Integer.toString(this.getInsertOffTime()));
                       case OPT_INSERTONTIME:
                        return(Integer.toString(this.getInsertOnTime()));
                       case OPT_INSERTWIDTH:
                        return(SwankUtil.parseTkSize(this.getInsertWidth()));
                       case OPT_JHELPTARGET:
                        return(this.getJHelpTarget());
                       case OPT_LOCATION:
                        return(SwankUtil.parsePoint(this.getLocation()));
                       case OPT_MAXIMUMSIZE:
                        return(SwankUtil.parseDimension(this.getMaximumSize()));
                       case OPT_MINIMUMSIZE:
                        return(SwankUtil.parseDimension(this.getMinimumSize()));
                       case OPT_NAME:
                        return(this.getName());
                       case OPT_OPAQUE:
                        return(isOpaque()?"1":"0");
                       case OPT_PADX:
                        return(SwankUtil.parseTkSize(this.getPadx()));
                       case OPT_PADY:
                        return(SwankUtil.parseTkSize(this.getPady()));
                       case OPT_PREFERREDSIZE:
                        return(SwankUtil.parseDimension(this.getPreferredSize()));
                       case OPT_RELIEF:
                        return(SwankUtil.parseTkRelief(this.getRelief()));
                       case OPT_REQUESTFOCUSENABLED:
                        return(isRequestFocusEnabled()?"1":"0");
                       case OPT_SCROLLREGION:
                        return(SwankUtil.parseTkRectangleCorners(this.getScrollRegion()));
                       case OPT_SIZE:
                        return(SwankUtil.parseDimension(this.getSize()));
                       case OPT_TOOLTIPTEXT:
                        return(this.getToolTipText());
                       case OPT_VERIFYINPUTWHENFOCUSTARGET:
                        return(String.valueOf(this.getVerifyInputWhenFocusTarget()));
                       case OPT_VISIBLE:
                        return(isVisible()?"1":"0");
                       case OPT_WIDTH:
                        return(SwankUtil.parseTkSize(this.getSwkWidth()));
                       case OPT_XSCROLLCOMMAND:
                        return(this.getXScrollCommand());
                       case OPT_YSCROLLCOMMAND:
                        return(this.getYScrollCommand());
                       }
            return "";
    }

    String jget(final Interp interp, final TclObject arg) throws TclException {
       int opt = 0;
       // XXX SwkIndex doesn't throw correct error for hear 
       try {
           opt = SwkIndex.get(interp, arg, validCmdsTM, "option", 0);
       } catch (TclException tclE) {
           throw new TclException(interp, "unknown option \"" + arg + "\"");
       }
        String result = "";
        if (!EventQueue.isDispatchThread()) {
            JGet jget = new JGet(this, opt);
            try {
                SwingUtilities.invokeAndWait(jget);
            } catch (InterruptedException iE) {
                throw new TclException(interp,iE.toString());
            } catch (Exception  e) {
                throw new TclException(interp,e.toString());
            }
            result =  jget.result;
        } else {
            result =  jget(opt);
        }
        return result;
    }
   class JGet implements Runnable {
        SwkCanvas3D swkcanvas;
        int opt = 0;
	String result = "";
        JGet(SwkCanvas3D swkcanvas, int opt) {
                this.swkcanvas = swkcanvas;
                this.opt = opt;
        }

        public void run() {
                        result = swkcanvas.jget(opt);
        }

    }
   void setResourceDefaults() throws TclException {
        if (!EventQueue.isDispatchThread()) {
            ResourceDefaultsSetter resourceDefaultsSetter = new ResourceDefaultsSetter(interp,this);
            try {
                SwingUtilities.invokeAndWait(resourceDefaultsSetter);
            } catch (InterruptedException iE) {
                throw new TclException(interp,iE.toString());
            } catch (Exception  e) {
                throw new TclException(interp,e.toString());
            }
        } else {
            setResourceDefaultsET();
        }
    }

  class ResourceDefaultsSetter implements Runnable {
        Interp interp;
        SwkCanvas3D swkcanvas;
        ResourceDefaultsSetter(Interp interp, SwkCanvas3D swkcanvas) {
                this.interp = interp;
                this.swkcanvas = swkcanvas;
        }

        public void run() {
			setResourceDefaultsET();
        }

    }

    void setResourceDefaultsET() {
        String keyName;
        TclObject tObj;

        Enumeration e = SwkCanvas3D.resourceDB.keys();

        while (e.hasMoreElements()) {
            keyName = (String) e.nextElement();

            if (keyName == null) {
                continue;
            }

            ResourceObject ro = (ResourceObject) SwkCanvas3D.resourceDB.get(keyName);

            if (ro == null) {
                continue;
            }

                try {
                   ro.defaultVal = jget(ro.optNum);
                } catch (IllegalComponentStateException icsE) {
                    continue;
                }
        }
    }
    private static void initResources() {
	ResourceObject resourceObject=null;

		resourceObject = new ResourceObject("alignmentX","AlignmentX",OPT_ALIGNMENTX);
		resourceDB.put("-alignmentx",resourceObject);
	
		resourceObject = new ResourceObject("alignmentY","AlignmentY",OPT_ALIGNMENTY);
		resourceDB.put("-alignmenty",resourceObject);
	
		resourceObject = new ResourceObject("anchor","Anchor",OPT_ANCHOR);
		resourceDB.put("-anchor",resourceObject);
	
		resourceObject = new ResourceObject("autoscrolls","Autoscrolls",OPT_AUTOSCROLLS);
		resourceDB.put("-autoscrolls",resourceObject);
	
		resourceObject = new ResourceObject("background","Background",OPT_BACKGROUND);
		resourceDB.put("-background",resourceObject);
	
		resourceObject = new ResourceObject("borderWidth","BorderWidth",OPT_BD);
		resourceDB.put("-bd",resourceObject);
	
		resourceObject = new ResourceObject("background","Background",OPT_BG);
		resourceDB.put("-bg",resourceObject);
	
		resourceObject = new ResourceObject("borderWidth","BorderWidth",OPT_BORDER);
		resourceDB.put("-border",resourceObject);
	
		resourceObject = new ResourceObject("borderWidth","BorderWidth",OPT_BORDERWIDTH);
		resourceDB.put("-borderwidth",resourceObject);
	
		resourceObject = new ResourceObject("bounds","Bounds",OPT_BOUNDS);
		resourceDB.put("-bounds",resourceObject);
	
		resourceObject = new ResourceObject("className","ClassName",OPT_CLASS);
		resourceDB.put("-class",resourceObject);
	
		resourceObject = new ResourceObject("cursor","Cursor",OPT_CURSOR);
		resourceDB.put("-cursor",resourceObject);
	
		resourceObject = new ResourceObject("debugGraphicsOptions","DebugGraphicsOptions",OPT_DEBUGGRAPHICSOPTIONS);
		resourceDB.put("-debuggraphicsoptions",resourceObject);
	
		resourceObject = new ResourceObject("doubleBuffered","DoubleBuffered",OPT_DOUBLEBUFFERED);
		resourceDB.put("-doublebuffered",resourceObject);
	
		resourceObject = new ResourceObject("enabled","Enabled",OPT_ENABLED);
		resourceDB.put("-enabled",resourceObject);
	
		resourceObject = new ResourceObject("foreground","Foreground",OPT_FG);
		resourceDB.put("-fg",resourceObject);
	
		resourceObject = new ResourceObject("focusable","Focusable",OPT_FOCUSABLE);
		resourceDB.put("-focusable",resourceObject);
	
		resourceObject = new ResourceObject("focusCycleRoot","FocusCycleRoot",OPT_FOCUSCYCLEROOT);
		resourceDB.put("-focuscycleroot",resourceObject);
	
		resourceObject = new ResourceObject("focusTraversalKeysEnabled","FocusTraversalKeysEnabled",OPT_FOCUSTRAVERSALKEYSENABLED);
		resourceDB.put("-focustraversalkeysenabled",resourceObject);
	
		resourceObject = new ResourceObject("focusTraversalPolicyProvider","FocusTraversalPolicyProvider",OPT_FOCUSTRAVERSALPOLICYPROVIDER);
		resourceDB.put("-focustraversalpolicyprovider",resourceObject);
	
		resourceObject = new ResourceObject("font","Font",OPT_FONT);
		resourceDB.put("-font",resourceObject);
	
		resourceObject = new ResourceObject("foreground","Foreground",OPT_FOREGROUND);
		resourceDB.put("-foreground",resourceObject);
	
		resourceObject = new ResourceObject("height","Height",OPT_HEIGHT);
		resourceDB.put("-height",resourceObject);
	
		resourceObject = new ResourceObject("highlightBackground","HighlightBackground",OPT_HIGHLIGHTBACKGROUND);
		resourceDB.put("-highlightbackground",resourceObject);
	
		resourceObject = new ResourceObject("highlightColor","HighlightColor",OPT_HIGHLIGHTCOLOR);
		resourceDB.put("-highlightcolor",resourceObject);
	
		resourceObject = new ResourceObject("highlightThickness","HighlightThickness",OPT_HIGHLIGHTTHICKNESS);
		resourceDB.put("-highlightthickness",resourceObject);
	
		resourceObject = new ResourceObject("ignoreRepaint","IgnoreRepaint",OPT_IGNOREREPAINT);
		resourceDB.put("-ignorerepaint",resourceObject);
	
		resourceObject = new ResourceObject("inheritsPopupMenu","InheritsPopupMenu",OPT_INHERITSPOPUPMENU);
		resourceDB.put("-inheritspopupmenu",resourceObject);
	
		resourceObject = new ResourceObject("insertBackground","InsertBackground",OPT_INSERTBACKGROUND);
		resourceDB.put("-insertbackground",resourceObject);
	
		resourceObject = new ResourceObject("insertBorderWidth","InsertBorderWidth",OPT_INSERTBORDERWIDTH);
		resourceDB.put("-insertborderwidth",resourceObject);
	
		resourceObject = new ResourceObject("insertOffTime","InsertOffTime",OPT_INSERTOFFTIME);
		resourceDB.put("-insertofftime",resourceObject);
	
		resourceObject = new ResourceObject("insertOnTime","InsertOnTime",OPT_INSERTONTIME);
		resourceDB.put("-insertontime",resourceObject);
	
		resourceObject = new ResourceObject("insertWidth","InsertWidth",OPT_INSERTWIDTH);
		resourceDB.put("-insertwidth",resourceObject);
	
		resourceObject = new ResourceObject("jHelpTarget","JHelpTarget",OPT_JHELPTARGET);
		resourceDB.put("-jhelptarget",resourceObject);
	
		resourceObject = new ResourceObject("location","Location",OPT_LOCATION);
		resourceDB.put("-location",resourceObject);
	
		resourceObject = new ResourceObject("maximumSize","MaximumSize",OPT_MAXIMUMSIZE);
		resourceDB.put("-maximumsize",resourceObject);
	
		resourceObject = new ResourceObject("minimumSize","MinimumSize",OPT_MINIMUMSIZE);
		resourceDB.put("-minimumsize",resourceObject);
	
		resourceObject = new ResourceObject("name","Name",OPT_NAME);
		resourceDB.put("-name",resourceObject);
	
		resourceObject = new ResourceObject("opaque","Opaque",OPT_OPAQUE);
		resourceDB.put("-opaque",resourceObject);
	
		resourceObject = new ResourceObject("padx","Padx",OPT_PADX);
		resourceDB.put("-padx",resourceObject);
	
		resourceObject = new ResourceObject("pady","Pady",OPT_PADY);
		resourceDB.put("-pady",resourceObject);
	
		resourceObject = new ResourceObject("preferredSize","PreferredSize",OPT_PREFERREDSIZE);
		resourceDB.put("-preferredsize",resourceObject);
	
		resourceObject = new ResourceObject("relief","Relief",OPT_RELIEF);
		resourceDB.put("-relief",resourceObject);
	
		resourceObject = new ResourceObject("requestFocusEnabled","RequestFocusEnabled",OPT_REQUESTFOCUSENABLED);
		resourceDB.put("-requestfocusenabled",resourceObject);
	
		resourceObject = new ResourceObject("scrollRegion","ScrollRegion",OPT_SCROLLREGION);
		resourceDB.put("-scrollregion",resourceObject);
	
		resourceObject = new ResourceObject("size","Size",OPT_SIZE);
		resourceDB.put("-size",resourceObject);
	
		resourceObject = new ResourceObject("toolTipText","ToolTipText",OPT_TOOLTIPTEXT);
		resourceDB.put("-tooltiptext",resourceObject);
	
		resourceObject = new ResourceObject("verifyInputWhenFocusTarget","VerifyInputWhenFocusTarget",OPT_VERIFYINPUTWHENFOCUSTARGET);
		resourceDB.put("-verifyinputwhenfocustarget",resourceObject);
	
		resourceObject = new ResourceObject("visible","Visible",OPT_VISIBLE);
		resourceDB.put("-visible",resourceObject);
	
		resourceObject = new ResourceObject("width","Width",OPT_WIDTH);
		resourceDB.put("-width",resourceObject);
	
		resourceObject = new ResourceObject("xScrollCommand","XScrollCommand",OPT_XSCROLLCOMMAND);
		resourceDB.put("-xscrollcommand",resourceObject);
	
		resourceObject = new ResourceObject("yScrollCommand","YScrollCommand",OPT_YSCROLLCOMMAND);
		resourceDB.put("-yscrollcommand",resourceObject);
	}
}


