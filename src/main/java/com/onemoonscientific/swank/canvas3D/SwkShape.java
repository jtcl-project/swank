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
package com.onemoonscientific.swank.canvas3D;



import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.util.*;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.geometry.Primitive;
import javax.media.j3d.*;
import javax.vecmath.Point3d;

import com.onemoonscientific.swank.SwkException;




/**
 *
 * @author brucejohnson
 */
public abstract class SwkShape implements SwkShape3DConfig {
    Shape3D shape = null;
    Node objectNode = null;
    NvBranchGroup bG = null;
    SwkShape previous = null;
    SwkShape next = null;
    SwkImageCanvas canvas = null;
    SwkAppearance swkAppearance = null;
    Hashtable tags = new Hashtable();
    String[] tagNames = null;
    Transformer transformer = null;
    double rotate = 0.0;
    float width = 0.0f;
    int id;
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    public SwkShape() {
        bG = new NvBranchGroup(this);
    }

    public SwkShape(SwkImageCanvas canvas) {
        this.canvas = canvas;
        swkAppearance = canvas.defaultAppearance;
        bG = new NvBranchGroup(this);
    }
    public int getId() {
         return id;
    }
    public void setCanvas(SwkImageCanvas canvas) {
        this.canvas = canvas;
        swkAppearance = this.canvas.defaultAppearance;
    }

    public void setShape(Shape3D shape) {
        this.shape = shape;
    }
    public Map getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     */
    public void setTags(String[] tags) {
        tagNames = tags;
    }
    public void setAppearanceByName(final String name) {
        System.out.println("set app " + name);
        this.swkAppearance = SwkImageCanvas.getSwkAppearance(name,true);
    }
    public String  getAppearanceName() {
        return swkAppearance.name;
    }

    // public abstract void config(Interp interp, TclObject argv[],int start);
    public void paintShape(Graphics2D g2) {
    }

    public void config(Interp interp, TclObject[] argv, int start)
        throws TclException {
    }

    public void coords(SwkImageCanvas canvas, double[] coords) throws SwkException {
    }
    public TclObject itemGet(Interp interp, TclObject argv)
        throws TclException {
        return (TclString.newInstance(""));
    }

    public String toString() {
        return (String.valueOf(id));
    }

    public void move(double x, double y) {
    }

    public boolean hitShape(double x, double y) {
        return false;
    }

    public String hit(double x, double y) {
        return String.valueOf(pickShape(x,y));
    }

    /**
     *
     * @param tObj
     */
    public void select(TclObject tObj) {
    }

    public int getIndexAtXY(double x, double y) {
        return 0;
    }

    public int getIndex(Interp interp, TclObject indexArg)
        throws TclException {
        String indexString = indexArg.toString();

        if (indexString.startsWith("@")) {
            if (indexString.length() > 1) {
                int commaPos = indexString.indexOf(',');

                if (commaPos < 0) {
                    throw new TclException(interp,
                        "bad text index \"" + indexString + "\"");
                }

                String xS = indexString.substring(1, commaPos);
                String yS = indexString.substring(commaPos + 1);
                int x = Integer.valueOf(xS).intValue();
                int y = Integer.valueOf(yS).intValue();
                int offset = getIndexAtXY(x, y);

                return offset;
            } else {
                throw new TclException(interp,
                    "bad text index \"" + indexString + "\"");
            }
        }

        return 0;
    }

    /**
     *
     * @param xOrigin
     * @param yOrigin
     * @param xScale
     * @param yScale
     */
    public void scale(double xOrigin, double yOrigin, double xScale,
        double yScale) {
        /* FIXME
            if (this instanceof SwkCanvText) {
                SwkCanvText ctext = (SwkCanvText) this;
                ctext.setX(ctext.getX() - xOrigin);
                ctext.setY(ctext.getY() - yOrigin);
                ctext.setX(ctext.getX() * xScale);
                ctext.setY(ctext.getY() * yScale);
                ctext.setX(ctext.getX() + xOrigin);
                ctext.setY(ctext.getY() + yOrigin);
            } else {
                AffineTransform at = new AffineTransform();
                at.translate(xOrigin, yOrigin);
                at.scale(xScale, yScale);
                at.translate(-xOrigin, -yOrigin);
                shape = at.createTransformedShape(shape);
            }
         */
    }

    public Rectangle2D getBounds2D() {
               Rectangle2D bounds = null;
        return bounds;
    }
    public String getType() {
       return "";
    }
   public void configShape(Interp interp, SwkImageCanvas swkCanvas,
            TclObject[] argv, int start) throws TclException {
        Map parameterMap = getParameterMap();
        CanvasParameter[] setPars = new CanvasParameter[(argv.length - start) / 2];

        boolean gotPar = false;
System.out.println("config");
        for (int i = start, j = 0; i < argv.length; i += 2, j++) {
            CanvasParameter cPar = null;

            cPar = getPar(argv[i].toString());

System.out.println("config "+cPar);
            if (cPar == null) {
                String parName = "com.onemoonscientific.swank.canvas3D." +
                        argv[i].toString().substring(1, 2).toUpperCase() +
                        argv[i].toString().substring(2).toLowerCase() +
                        "Parameter";
                Class newClass = null;

                try {
                    newClass = Class.forName(parName);
                } catch (ClassNotFoundException cnfE) {
                    //  throw new TclException(interp, "class "+parName+" doesn't exist "+cnfE.toString());
                    continue;
                }

                try {
                    cPar = (CanvasParameter) newClass.newInstance();
                } catch (InstantiationException iE) {
                    throw new TclException(interp,
                            "can't instantiate " + parName);
                } catch (IllegalAccessException iaE) {
                    throw new TclException(interp,
                            "can't instantiate " + parName);
                }
            }

            if (cPar != null) {
                setPars[j] = (CanvasParameter) cPar.clone();
                setPars[j].setValue(interp, swkCanvas, argv[i + 1]);
                gotPar = true;
            }
        }

        if (gotPar) {
System.out.println("gotpar");
            (new SwkShapeRunnable(swkCanvas, this, setPars)).exec();
        }
    }

   public static void config(Interp interp, SwkImageCanvas swkCanvas,
        TclObject[] argv, int start) throws TclException {
        // Map parameterMap = getParameterMap();
        CanvasParameter[] setPars = new CanvasParameter[(argv.length - start) / 2];
        boolean gotPar = false;

System.out.println("config2");
        // this is more difficult than it should be because we want to check validity of parameter name
        // before we've figured out which shape were configuring
        for (int i = start, j = 0; i < argv.length; i += 2, j++) {
            CanvasParameter cPar = null;

            cPar = CanvasParameter.getStdPar(argv[i].toString());
           //  Mostly not used as custom pars are added automatically to stdPars

            if (cPar == null) {
                String parName = "com.onemoonscientific.swank.canvas3D." +
                    argv[i].toString().substring(1, 2).toUpperCase() +
                    argv[i].toString().substring(2) + "Parameter";
                Class newClass = null;

                try {
                    newClass = Class.forName(parName);
                } catch (ClassNotFoundException cnfE) {
                    throw new TclException(interp,
                        "class " + parName + " doesn't exist " +
                        cnfE.toString());

                    //continue;
                }

                try {
                    cPar = (CanvasParameter) newClass.newInstance();
                } catch (InstantiationException iE) {
                    throw new TclException(interp,
                        "can't instantiate " + parName);
                } catch (IllegalAccessException iaE) {
                    throw new TclException(interp,
                        "can't instantiate " + parName);
                }
            }

            if (cPar != null) {
                setPars[j] = (CanvasParameter) cPar.clone();
                setPars[j].setValue(interp, swkCanvas, argv[i + 1]);
                gotPar = true;
            }
        }

        if (gotPar) {
            (new SwkShapeRunnable(swkCanvas, argv[start - 1].toString(), setPars)).exec();
        }
    }
   /**
    *
    * @return
    */
   public  CanvasParameter[] getParameters() {
        // fixme 
        return null;
    }
    public Map getParameterMap() {
        return null;
    }

    public static void initializeParameters(CanvasParameter[] params, Map map) {
        for (int i = 0; i < params.length; i++) {
            map.put(((CanvasParameter) params[i]).getName(), params[i]);
            CanvasParameter cPar = CanvasParameter.getStdPar(params[i].getName());
            if (cPar == null) {
                CanvasParameter.addParameter(params[i]);
            }
        }
    }

    public CanvasParameter getPar(String argString) {
        Map map = getParameterMap();

        if (map == null) {
            return null;
        }

        CanvasParameter par = CanvasParameter.getPar(map, argString);

        return par;
    }
    abstract void makeObjectNode() ;
    abstract NvTransformGroup  makeTransform();
    void genShape() {
System.out.println("genShape");
        NvTransformGroup tG = makeTransform();
        tG.setShape(this);
        makeObjectNode();
        tG.addChild(objectNode);
        try {
            bG.removeAllChildren();
        } catch (Exception bgE) {
        }
        NvBranchGroup2 bG2 = new NvBranchGroup2(bG);
        bG.addChild(bG2);
        bG2.addChild(tG);
        bG.setCapability(NvBranchGroup.ALLOW_DETACH);
        bG2.setCapability(NvBranchGroup.ALLOW_DETACH);
        bG.setCapability(NvBranchGroup.ALLOW_CHILDREN_EXTEND);
        bG2.setCapability(NvBranchGroup.ALLOW_CHILDREN_EXTEND);
        SwkImageCanvas.enablePicking(bG);
        bG.compile();
System.out.println("genShaped");
    }
    void updateShape() {
        try {
System.out.println("updateShape");
            bG.removeAllChildren();
            NvTransformGroup tG = makeTransform();
            tG.setShape(this);
            makeObjectNode();
            tG.addChild(objectNode);
            NvBranchGroup2 bG2 = new NvBranchGroup2(bG);
            bG2.addChild(tG);
            bG2.setCapability(NvBranchGroup.ALLOW_DETACH);
            bG2.setCapability(NvBranchGroup.ALLOW_CHILDREN_EXTEND);
            bG.addChild(bG2);
System.out.println("enablePick");
            SwkImageCanvas.enablePicking(bG2);
System.out.println("updateShaped");
        } catch (Exception bgE) {
            System.out.println("update shape error " +bgE.getMessage());
        }
    }
    int pickShape(final double x, final double y) {
        String pickResult = "";
        PickCanvas pickCanvas = new PickCanvas(canvas.c3D,bG);
        pickCanvas.setTolerance(10.0f);
        pickCanvas.setMode(PickCanvas.GEOMETRY);
        pickCanvas.setShapeLocation((int) x,(int) y);
        PickResult result = pickCanvas.pickClosest();
        SwkShape swkShape = null;
        int pickIndex = -1;
        int index = -1;
        if (result != null) {
            TransformGroup tg = (TransformGroup) result.getNode(PickResult.TRANSFORM_GROUP);
            if (tg instanceof NvTransformGroup) {
                swkShape = ((NvTransformGroup) tg).getShape();
                pickIndex = ((NvTransformGroup) tg).getIndex();
                System.out.println("swksh " + swkShape.getType() + " " + swkShape.getId() + " " + pickIndex);
            }
            if (pickIndex == -1) {
                Primitive p = (Primitive) result.getNode(PickResult.PRIMITIVE);
                Shape3D s = (Shape3D) result.getNode(PickResult.SHAPE3D);
                GeometryArray geomArray = result.getGeometryArray();
                if (geomArray != null) {
                    // System.out.println(" geom count " + geomArray.getVertexCount());
                }
                Point3d eyePos = pickCanvas.getStartPosition();
                PickIntersection pickIntersection = result.getClosestIntersection(eyePos);
                if (pickIntersection != null) {
                    System.out.println("closest vertex index " + pickIntersection.getClosestVertexIndex());
                    System.out.println("closest vertext coord  " + pickIntersection.getClosestVertexCoordinates());
                    System.out.println("closest vertext vword  " + pickIntersection.getClosestVertexCoordinatesVW());
                    int closestVertex = pickIntersection.getClosestVertexIndex();
                    int[] pickIndices = pickIntersection.getPrimitiveVertexIndices();
                    pickIndex = pickIndices[closestVertex];
                }
            }
        }
        return pickIndex;
    }
}
