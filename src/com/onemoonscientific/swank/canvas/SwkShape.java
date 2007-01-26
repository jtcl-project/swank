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
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;


public abstract class SwkShape implements SwkShapeConfig {
    static public final byte ACTIVE = 0;
    static public final byte DISABLED = 1;
    static public final byte HIDDEN = 2;
    Shape shape = null;
    int id;
    public double[] storeCoords = null;
    SwkShape previous = null;
    SwkShape next = null;
    Color fill = null;
    GradientPaint fillGradient = null;
    Point2D gradPt1 = null;
    Point2D gradPt2 = null;
    TexturePaint texturePaint = null;
    String imageName = "";
    Color outline = Color.black;
    BasicStroke stroke = null;
    boolean newStroke = false;
    boolean newTransform = false;
    float width = (float) 1.0;
    float height = (float) 1.0;
    int cap = BasicStroke.CAP_SQUARE;
    int join = BasicStroke.JOIN_MITER;
    float miterLimit = 10.0f;
    String dashString = "";
    float[] dash = {  };
    float[] dashTemp = null;
    float dashPhase = 0.0f;
    boolean dashIntPattern = false;
    Transformer transformer = null;
    float rotate = 0.0f;
    float xShear = 0.0f;
    float yShear = 0.0f;
    float alpha = 1.0f;
    byte state = ACTIVE;
    Hashtable tags = new Hashtable();
    String[] tagNames = null;
    SwkImageCanvas canvas = null;
    boolean xorMode = false;
    Composite composite = null;

    public SwkShape() {
    }

    public SwkShape(Shape shape, SwkImageCanvas canvas) {
        this.shape = shape;
        this.canvas = canvas;
    }

    public int getState() {
        return state;
    }

    public float getXShear() {
        return xShear;
    }

    public float getYShear() {
        return yShear;
    }

    public String getStateString() {
        if (state == ACTIVE) {
            return "normal";
        } else if (state == DISABLED) {
            return "disabled";
        } else if (state == HIDDEN) {
            return "hidden";
        } else {
            throw new RuntimeException("invalid state value");
        }
    }

    public void setState(byte newState) {
        state = newState;
    }

    public double[] getStoreCoords() {
        return storeCoords;
    }

    public AffineTransform getTransform() {
        if (transformer != null) {
            if (transformer.isValid()) {
                return transformer.getTransform();
            } else {
                transformer = null;
            }
        }

        return null;
    }

    public Shape getShape() {
        return shape;
    }

    public SwkImageCanvas getCanvas() {
        return canvas;
    }

    public SwkShape getPrevious() {
        return previous;
    }

    public SwkShape getNext() {
        return next;
    }

    public int getId() {
        return id;
    }

    public Color getFill() {
        return fill;
    }

    public void setFill(Color color) {
        fill = color;
    }

    public GradientPaint getFillGradient() {
        return fillGradient;
    }

    public TexturePaint getTexturePaint() {
        return texturePaint;
    }

    public Color getOutline() {
        return outline;
    }

    public void setOutline(Color color) {
        outline = color;
    }

    public BasicStroke getStroke() {
        return stroke;
    }

    public float getWidth() {
        return width;
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int newValue) {
        cap = newValue;
    }

    public void setXOrMode(boolean value) {
        xorMode = value;
    }

    public int getJoin() {
        return join;
    }

    public void setJoin(int newValue) {
        join = newValue;
    }

    public float getMiterLimit() {
        return miterLimit;
    }

    public float[] getDash() {
        return dash;
    }

    public float getDashPhase() {
        return dashPhase;
    }

    public float getRotate() {
        return rotate;
    }

    public float getAlpha() {
        return alpha;
    }

    public Hashtable getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        tagNames = tags;
    }

    public void setCanvas(SwkImageCanvas canvas) {
        this.canvas = canvas;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void paintShape(Graphics2D g2) {
    }

    CanvasParameter[] getParameters() {
        return null;
    }

    public void configOld(Interp interp, TclObject[] argv, int start)
        throws TclException {
        if (((argv.length - start) % 2) != 0) {
            throw new TclNumArgsException(interp, 0, argv,
                "-option value ? -option value? ...");
        }
    }

    public void configShape(Interp interp, SwkImageCanvas swkCanvas,
        TclObject[] argv, int start) throws TclException {
        Map parameterMap = getParameterMap();
        CanvasParameter[] setPars = new CanvasParameter[(argv.length - start) / 2];

        boolean gotPar = false;

        for (int i = start, j = 0; i < argv.length; i += 2, j++) {
            CanvasParameter cPar = null;

            cPar = getPar(argv[i].toString());

            if (cPar == null) {
                String parName = "com.onemoonscientific.swank.canvas." +
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
            (new SwkShapeRunnable(swkCanvas, this, setPars)).exec();
        }
    }

    public static void config(Interp interp, SwkImageCanvas swkCanvas,
        TclObject[] argv, int start) throws TclException {
        // Map parameterMap = getParameterMap();
        CanvasParameter[] setPars = new CanvasParameter[(argv.length - start) / 2];
        boolean gotPar = false;

        // this is more difficult than it should be because we want to check validity of parameter name
        // before we've figured out which shape were configuring
        for (int i = start, j = 0; i < argv.length; i += 2, j++) {
            CanvasParameter cPar = null;

            cPar = CanvasParameter.getStdPar(argv[i].toString());

            if (cPar == null) {
                String parName = "com.onemoonscientific.swank.canvas." +
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

    public void itemConfigure(Interp interp, TclObject[] argv, int start)
        throws TclException {
        TclObject list = TclList.newInstance();
        interp.resetResult();

        if (argv.length == start) {
            CanvasParameter[] pars = getParameters();

            for (int i = 0; i < pars.length; i++) {
                TclObject list2 = itemGet(interp, pars[i].getName(), true);
                TclList.append(interp, list, list2);
            }

            interp.setResult(list);

            return;
        } else {
            interp.setResult(itemGet(interp, argv[start].toString(), true));

            return;
        }
    }

    public void coords(SwkImageCanvas canvas, double[] coordArray)
        throws SwkException {
    }

    public double[] coords() {
        return storeCoords;
    }

    public TclObject itemGet(Interp interp, String argString,
        boolean configStyle) throws TclException {
        CanvasParameter par = getPar(argString);

        if (par != null) {
            TclObject value = par.getValue(interp, this);

            if (!configStyle) {
                return value;
            } else {
                TclObject list = TclList.newInstance();
                TclList.append(interp, list,
                    TclString.newInstance("-" + par.getName()));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclString.newInstance(par.getDefault()));
                TclList.append(interp, list, value);

                return (list);
            }
        } else {
            return TclString.newInstance("");
        }
    }

    public String toString() {
        return (String.valueOf(id));
    }

    public void move(double x, double y) {
        for (int i = 0; i < storeCoords.length; i += 2) {
            storeCoords[i] += x;
            storeCoords[i + 1] += y;
        }

        applyCoordinates();
    }

    void checkCoordinates(double[] coords) {
        double hold;

        if ((coords == null) || (coords.length != 4)) {
            return;
        }

        if (coords[0] > coords[2]) {
            hold = coords[0];
            coords[0] = coords[2];
            coords[2] = hold;
        }

        if (coords[1] > coords[3]) {
            hold = coords[1];
            coords[1] = coords[3];
            coords[3] = hold;
        }
    }

    void applyCoordinates() {
    }

    public Point2D getGradPt1() {
        return gradPt1;
    }

    public Point2D getGradPt2() {
        return gradPt2;
    }

    public void setGradPt1(Point2D p1) {
        gradPt1 = p1;
    }

    public void setGradPt2(Point2D p2) {
        gradPt2 = p2;
    }

    public boolean hitShape(double x, double y) {
        return false;
    }

    public String hit(double x, double y) {
        return "";
    }

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

    public void scale(double xOrigin, double yOrigin, double xScale,
        double yScale) {
        for (int i = 0; i < storeCoords.length; i += 2) {
            storeCoords[i] = ((storeCoords[i] - xOrigin) * xScale) + xOrigin;
            storeCoords[i + 1] = ((storeCoords[i + 1] - yOrigin) * yScale) +
                yOrigin;
        }

        applyCoordinates();
    }

    void updateStroke() {
        if (newStroke) {
            if (dashString.equals("")) {
                stroke = new BasicStroke(width, cap, join, miterLimit);
            } else {
                if (dashIntPattern || (width == 1.0f) || (width == 0.0f)) {
                    stroke = new BasicStroke(width, cap, join, miterLimit,
                            dash, dashPhase);
                } else {
                    if ((dashTemp == null) || (dashTemp.length != dash.length)) {
                        dashTemp = new float[dash.length];
                    }

                    for (int i = 0; i < dash.length; i++) {
                        dashTemp[i] = dash[i] * width;
                    }

                    stroke = new BasicStroke(width, cap, join, miterLimit,
                            dashTemp, dashPhase);
                }
            }
        }
    }

    void genGradient(AffineTransform aT) {
        if ((gradPt1 != null) && (gradPt2 != null)) {
            Color c1 = fillGradient.getColor1();
            Color c2 = fillGradient.getColor2();
            boolean cyclic = fillGradient.isCyclic();
            Point2D p1 = new Point2D.Double();
            Point2D p2 = new Point2D.Double();
            double x = ((storeCoords[2] - storeCoords[0]) * gradPt1.getX()) +
                storeCoords[0];
            double y = ((storeCoords[3] - storeCoords[1]) * gradPt1.getY()) +
                storeCoords[1];
            p1.setLocation(x, y);
            x = ((storeCoords[2] - storeCoords[0]) * gradPt2.getX()) +
                storeCoords[0];
            y = ((storeCoords[3] - storeCoords[1]) * gradPt2.getY()) +
                storeCoords[1];
            p2.setLocation(x, y);

            p1 = aT.transform(p1, p1);
            p2 = aT.transform(p2, p2);
            fillGradient = new GradientPaint(p1, c1, p2, c2, cyclic);
        }
    }

    public Map getParameterMap() {
        return null;
    }

    public static void initializeParameters(CanvasParameter[] params, Map map) {
        for (int i = 0; i < params.length; i++) {
            map.put(((CanvasParameter) params[i]).getName(), params[i]);
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
}
