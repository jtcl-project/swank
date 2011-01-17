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

import java.util.*;

/**
 *
 * @author brucejohnson
 */
public abstract class SwkShape implements SwkShapeConfig {

    /**
     *
     */
    static public final byte ACTIVE = 0;
    /**
     *
     */
    static public final byte DISABLED = 1;
    /**
     *
     */
    static public final byte HIDDEN = 2;
    static BasicStroke bstroke = new BasicStroke();
    /**
     *
     */
    public static final int handleSize = 6;
    StrokeParameters strokePar = StrokeParameters.getDefault();
    Shape shape = null;
    ItemTreeNode node = null;
    int id;
    /**
     *
     */
    public double[] storeCoords = null;
    //SwkShape previous = null;
    //SwkShape next = null;
    Color fill = null;
    GradientPaint fillGradient = null;
    Point2D gradPt1 = null;
    Point2D gradPt2 = null;
    TexturePaint texturePaint = null;
    Color outline = Color.black;
    BasicStroke stroke = null;
    boolean newStroke = false;
    float width = (float) 1.0;
    float height = (float) 1.0;
    Transformer transformer = null;
    float rotate = 0.0f;
    float xShear = 0.0f;
    float yShear = 0.0f;
    byte state = ACTIVE;
    Map tags = new LinkedHashMap();
    String[] tagNames = null;
    SwkImageCanvas canvas = null;
    boolean selected = false;
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();
    String imageName="";
    boolean newTransform = false;

    /**
     *
     */
    public SwkShape() {
    }

    /**
     *
     * @param shape
     * @param canvas
     */
    public SwkShape(Shape shape, SwkImageCanvas canvas) {
        this.shape = shape;
        this.canvas = canvas;
    }

    /**
     *
     * @return
     */
    public TreeMap<String,CanvasParameter> getParameterMap() {
        return parameterMap;
    }

    /**
     *
     * @return
     */
    protected int getState() {
        return state;
    }

    /**
     *
     * @param selected
     */
    protected void setSelected(final boolean selected) {
        this.selected = selected;
    }

    /**
     *
     * @return
     */
    protected boolean isSelected() {
        return selected;
    }

    /**
     *
     * @return
     */
    protected float getXShear() {
        return xShear;
    }

    /**
     *
     * @return
     */
    protected float getYShear() {
        return yShear;
    }

    /**
     *
     * @param nodeName
     */
    protected void setNode(String nodeName) {
        try {
            SwkShape shapeNode = (SwkShape) canvas.getShape(nodeName);
            ItemTreeNode newParent = shapeNode.node;
            System.out.println("set node for " + getId() + " to " + nodeName);
            newParent.add(node);

        } catch (SwkException swkE) {
        }
    }

    /**
     *
     * @return
     */
    protected String getNode() {
        return "";
    }

    /**
     *
     * @return
     */
    protected String getStateString() {
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

    /**
     *
     * @param newState
     */
    protected void setState(byte newState) {
        state = newState;
    }

    /**
     *
     * @return
     */
    protected double[] getStoreCoords() {
        return storeCoords.clone();
    }

    /**
     *
     * @return
     */
    protected AffineTransform getTransform() {
        if (transformer != null) {
            if (transformer.isValid()) {
                return transformer.getTransform();
            } else {
                transformer = null;
            }
        }

        return null;
    }

    /**
     *
     * @return
     */
    protected Shape getShape() {
        return shape;
    }

    /**
     *
     * @return
     */
    protected SwkImageCanvas getCanvas() {
        return canvas;
    }

    /*    public SwkShape getPrevious() {
    return previous;
    }

    protected SwkShape getNext() {
    return next;
    }
     */
    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    protected Color getFill() {
        return fill;
    }

    /**
     *
     * @param color
     */
    protected void setFill(Color color) {
        fill = color;
    }

    /**
     *
     * @return
     */
    protected GradientPaint getFillGradient() {
        return fillGradient;
    }

    /**
     *
     * @return
     */
    protected TexturePaint getTexturePaint() {
        return texturePaint;
    }

    /**
     *
     * @return
     */
    protected Color getOutline() {
        return outline;
    }

    /**
     *
     * @param color
     */
    protected void setOutline(Color color) {
        outline = color;
    }

    /**
     *
     * @return
     */
    protected BasicStroke getStroke() {
        return stroke;
    }

    /**
     *
     * @return
     */
    protected float getWidth() {
        return width;
    }

    /**
     *
     * @param value
     */
    protected void setWidth(double value) {
        width = (float) value;
    }

    /**
     *
     * @return
     */
    protected int getCap() {
        return strokePar.getCap();
    }

    /**
     *
     * @param newValue
     */
    protected void setCap(int newValue) {
        strokePar = StrokeParameters.setCap(strokePar, newValue);
    }

    /**
     *
     * @return
     */
    protected int getJoin() {
        return strokePar.getJoin();
    }

    /**
     *
     * @param newValue
     */
    protected void setJoin(int newValue) {
        strokePar = StrokeParameters.setJoin(strokePar, newValue);
    }

    /**
     *
     * @return
     */
    protected float getMiterLimit() {
        return strokePar.getMiterLimit();
    }

    /**
     *
     * @return
     */
    protected float[] getDash() {
        return strokePar.getDash();
    }

    /**
     *
     * @param newDash
     */
    protected void setDash(float[] newDash) {
        strokePar = StrokeParameters.setDash(strokePar, newDash);
    }

    /**
     *
     * @return
     */
    protected float getDashPhase() {
        return strokePar.getDashPhase();
    }

    /**
     *
     * @param newPhase
     */
    protected void setDashPhase(float newPhase) {
        strokePar = StrokeParameters.setDashPhase(strokePar, newPhase);
    }

    /**
     *
     * @return
     */
    protected boolean getDashIntPattern() {
        return strokePar.isDashIntPattern();
    }

    /**
     *
     * @param newPar
     */
    protected void setDashIntPattern(boolean newPar) {
        strokePar = StrokeParameters.setDashIntPattern(strokePar, newPar);
    }

    /**
     *
     * @return
     */
    protected String getDashString() {
        return strokePar.getDashString();
    }

    /**
     *
     * @param newString
     */
    protected void setDashString(String newString) {
        strokePar = StrokeParameters.setDashString(strokePar, newString);
    }

    /**
     *
     * @return
     */
    protected float getRotate() {
        return rotate;
    }

    /**
     *
     * @return
     */
    protected Map getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     */
    protected void setTags(String[] tags) {
        tagNames = tags;
    }

    /**
     *
     * @param canvas
     */
    protected void setCanvas(SwkImageCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     *
     * @param shape
     */
    protected void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     *
     * @param handle
     * @return
     */
    protected Cursor getHandleCursor(int handle) {
        final Cursor cursor;
        switch (handle) {
            case 0:
                cursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
                break;
            case 1:
                cursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                break;
            case 2:
                cursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
                break;
            case 3:
                cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                break;
            case 4:
                cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
                break;
            case 5:
                cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                break;
            case 6:
                cursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
                break;
            case 7:
                cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
                break;
            default:
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }
        return cursor;
    }

    /**
     *
     * @param x
     * @param y
     * @param xTest
     * @param yTest
     * @return
     */
    protected boolean hitHandle(int x, int y, double xTest, double yTest) {
        int fuzz = 2;
        return (new Rectangle(x - fuzz, y - fuzz, handleSize + 2 * fuzz, handleSize + 2 * fuzz)).contains(xTest, yTest);
    }

    void drawHandle(Graphics2D g2, int x, int y) {
        g2.setPaint(Color.WHITE);
        g2.fillRect(x - handleSize / 2, y - handleSize / 2, handleSize, handleSize);
        g2.setColor(Color.BLACK);
        g2.setStroke(bstroke);
        g2.drawRect(x - handleSize / 2, y - handleSize / 2, handleSize, handleSize);
    }

    /**
     *
     * @param g2
     */
    protected void drawHandles(Graphics2D g2) {
        if (shape != null) {
            Rectangle2D bounds = shape.getBounds2D();
            double x1 = bounds.getMinX();
            double y1 = bounds.getMinY();
            double x2 = bounds.getMaxX();
            double y2 = bounds.getMaxY();
            double xm = (x1 + x2) / 2;
            double ym = (y1 + y2) / 2;
            double[] xy = {x1, y1, xm, y1, x2, y1, x2, ym, x2, y2, xm, y2, x1, y2, x1, ym};
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                shapeTransform.transform(xy, 0, xy, 0, xy.length / 2);
            }
            for (int i = 0; i < xy.length; i += 2) {
                drawHandle(g2, (int) xy[i], (int) xy[i + 1]);
            }
        }
    }

    /**
     *
     * @param testX
     * @param testY
     * @return
     */
    protected int hitHandles(double testX, double testY) {
        int hitIndex = -1;
        if (shape != null) {
            Rectangle2D bounds = shape.getBounds2D();
            double x1 = bounds.getMinX();
            double y1 = bounds.getMinY();
            double x2 = bounds.getMaxX();
            double y2 = bounds.getMaxY();
            double xm = (x1 + x2) / 2;
            double ym = (y1 + y2) / 2;
            double[] xy = {x1, y1, xm, y1, x2, y1, x2, ym, x2, y2, xm, y2, x1, y2, x1, ym};
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                shapeTransform.transform(xy, 0, xy, 0, xy.length / 2);
            }

            for (int i = 0; i < xy.length; i += 2) {
                if (hitHandle((int) xy[i], (int) xy[i + 1], testX, testY)) {
                    hitIndex = i / 2;
                    break;
                }
            }
        }
        return hitIndex;
    }

    /**
     *
     * @param g2
     */
    protected void paintShape(Graphics2D g2) {
        if (shape != null) {
            if (stroke != null) {
                g2.setStroke(stroke);
            } else {
                g2.setStroke(bstroke);
            }
            AffineTransform shapeTransform = getTransform();
            Shape shape2 = shape;
            if (shapeTransform != null) {
                shape2 = shapeTransform.createTransformedShape(shape);
            }

            if (texturePaint != null) {
                g2.setPaint(texturePaint);
                g2.fill(shape2);

                //g.drawImage(swkShape.textureImage.getImage(),0,0,null);
            } else if (fillGradient != null) {
                g2.setPaint(fillGradient);
                g2.fill(shape2);
            } else if (fill != null) {
                g2.setPaint(fill);
                g2.fill(shape2);
            }

            if (outline != null) {
                g2.setPaint(outline);
                g2.draw(shape2);
            }
        }
    }


    /**
     *
     * @param interp
     * @param argv
     * @param start
     * @throws TclException
     */
    protected void configOld(Interp interp, TclObject[] argv, int start)
            throws TclException {
        if (((argv.length - start) % 2) != 0) {
            throw new TclNumArgsException(interp, 0, argv,
                    "-option value ? -option value? ...");
        }
    }

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param argv
     * @param start
     * @throws TclException
     */
    protected void configShape(Interp interp, SwkImageCanvas swkCanvas,
            TclObject[] argv, int start) throws TclException {
        CanvasParameter[] setPars = new CanvasParameter[(argv.length - start) / 2];

        boolean gotPar = false;

        for (int i = start, j = 0; i < argv.length; i += 2, j++) {
            CanvasParameter cPar = null;

            cPar = getPar(argv[i].toString());

            if (cPar == null) {
                String parName = "com.onemoonscientific.swank.canvas."
                        + argv[i].toString().substring(1, 2).toUpperCase()
                        + argv[i].toString().substring(2).toLowerCase()
                        + "Parameter";
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

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param argv
     * @param start
     * @throws TclException
     */
    protected static void config(Interp interp, SwkImageCanvas swkCanvas,
            TclObject[] argv, int start) throws TclException {
        // Map parameterMap = getParameterMap();
        CanvasParameter[] setPars = new CanvasParameter[(argv.length - start) / 2];
        boolean gotPar = false;

        // this is more difficult than it should be because we want to check validity of parameter name
        // before we've figured out which shape were configuring
        for (int i = start, j = 0; i < argv.length; i += 2, j++) {
            CanvasParameter cPar = null;

            cPar = CanvasParameter.getStdPar(argv[i].toString());
            //  Mostly not used as custom pars are added automatically to stdPars

            if (cPar == null) {
                String parName = "com.onemoonscientific.swank.canvas."
                        + argv[i].toString().substring(1, 2).toUpperCase()
                        + argv[i].toString().substring(2) + "Parameter";
                Class newClass = null;

                try {
                    newClass = Class.forName(parName);
                } catch (ClassNotFoundException cnfE) {
                    throw new TclException(interp,
                            "class " + parName + " doesn't exist "
                            + cnfE.toString());

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
     * @param interp
     * @param argv
     * @param start
     * @throws TclException
     */
    protected void itemConfigure(Interp interp, TclObject[] argv, int start)
            throws TclException {
        TclObject list = TclList.newInstance();
        interp.resetResult();

        if (argv.length == start) {
            for (CanvasParameter canvasPar:parameterMap.values()) {
                TclObject list2 = itemGet(interp, canvasPar.getName(), true);
                TclList.append(interp, list, list2);
            }

            interp.setResult(list);

            return;
        } else {
            interp.setResult(itemGet(interp, argv[start].toString(), true));

            return;
        }
    }

    /**
     *
     */
    protected void dispose() {
    }

    /**
     *
     * @param canvas
     * @param coordArray
     * @throws SwkException
     */
    public void coords(SwkImageCanvas canvas, double[] coordArray)
            throws SwkException {
    }

    /**
     *
     * @return
     */
    protected double[] coords() {
        return storeCoords.clone();
    }

    /**
     *
     * @param interp
     * @param argString
     * @param configStyle
     * @return
     * @throws TclException
     */
    protected TclObject itemGet(Interp interp, String argString,
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

    @Override
    public String toString() {
        return (String.valueOf(id));
    }

    /**
     *
     * @param x
     * @param y
     */
    protected void move(double x, double y) {
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

    /**
     *
     */
    protected void applyCoordinates() {
    }

    /**
     *
     * @return
     */
    protected Point2D getGradPt1() {
        return gradPt1;
    }

    /**
     *
     * @return
     */
    protected Point2D getGradPt2() {
        return gradPt2;
    }

    /**
     *
     * @param p1
     */
    protected void setGradPt1(Point2D p1) {
        gradPt1 = p1;
    }

    /**
     *
     * @param p2
     */
    protected void setGradPt2(Point2D p2) {
        gradPt2 = p2;
    }

    /**
     *
     * @param x1
     * @param y1
     * @return
     */
    protected boolean hitShape(double x1, double y1) {
        boolean hit = false;
        if (shape != null) {
            Shape checkShape = shape;
            AffineTransform shapeTransform = getTransform();
            if (shapeTransform != null) {
                checkShape = shapeTransform.createTransformedShape(shape);
            }
            if ((fill != null) || (fillGradient != null) || (texturePaint != null)) {
                Rectangle bounds = checkShape.getBounds();
                if (bounds.contains(x1, y1)) {
                    hit = true;
                }
            }

            if (!hit) {
                PathIterator pI = checkShape.getPathIterator(null);
                double[] tcoords = new double[6];
                double tx1 = 0.0;
                double ty1 = 0.0;
                double tx2 = 0.0;
                double ty2 = 0.0;
                int closeEnough = 2;
                int closeEnough2 = closeEnough * closeEnough;

                while (!pI.isDone()) {
                    int type = pI.currentSegment(tcoords);

                    if (type == PathIterator.SEG_LINETO) {
                        tx2 = tcoords[0];
                        ty2 = tcoords[1];

                        double dis = Line2D.ptSegDistSq(tx1, ty1, tx2, ty2,
                                x1, y1);
                        tx1 = tx2;
                        ty1 = ty2;

                        if (dis <= closeEnough2) {
                            hit = true;

                            break;
                        }
                    } else if (type == PathIterator.SEG_MOVETO) {
                        tx1 = tcoords[0];
                        ty1 = tcoords[1];
                    }

                    pI.next();
                }
            }
        }
        return hit;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    protected String hit(double x, double y) {
        return "";
    }

    /**
     *
     * @param tObj
     */
    protected void select(TclObject tObj) {
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    protected int getIndexAtXY(double x, double y) {
        return 0;
    }

    /**
     *
     * @param interp
     * @param indexArg
     * @return
     * @throws TclException
     */
    protected int getIndex(Interp interp, TclObject indexArg)
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
    protected void scale(double xOrigin, double yOrigin, double xScale,
            double yScale) {
        for (int i = 0; i < storeCoords.length; i += 2) {
            storeCoords[i] = ((storeCoords[i] - xOrigin) * xScale) + xOrigin;
            storeCoords[i + 1] = ((storeCoords[i + 1] - yOrigin) * yScale)
                    + yOrigin;
        }

        applyCoordinates();
    }

    void updateStroke() {
        if (newStroke) {
            stroke = strokePar.getStroke(width);
        }
    }

    /**
     *
     * @param aT
     */
    protected void genGradient(AffineTransform aT) {
        if ((fillGradient != null) && (gradPt1 != null) && (gradPt2 != null)) {
            Color c1 = fillGradient.getColor1();
            Color c2 = fillGradient.getColor2();
            boolean cyclic = fillGradient.isCyclic();
            Point2D p1 = new Point2D.Double();
            Point2D p2 = new Point2D.Double();
            double x = ((storeCoords[2] - storeCoords[0]) * gradPt1.getX())
                    + storeCoords[0];
            double y = ((storeCoords[3] - storeCoords[1]) * gradPt1.getY())
                    + storeCoords[1];
            p1.setLocation(x, y);
            x = ((storeCoords[2] - storeCoords[0]) * gradPt2.getX())
                    + storeCoords[0];
            y = ((storeCoords[3] - storeCoords[1]) * gradPt2.getY())
                    + storeCoords[1];
            p2.setLocation(x, y);

            p1 = aT.transform(p1, p1);
            p2 = aT.transform(p2, p2);
            fillGradient = new GradientPaint(p1, c1, p2, c2, cyclic);
        }
    }

    /**
     *
     * @param params
     * @param map
     */
    protected static void initializeParameters(CanvasParameter[] params, Map map) {
        for (int i = 0; i < params.length; i++) {
            map.put(((CanvasParameter) params[i]).getName(), params[i]);
            CanvasParameter cPar = CanvasParameter.getStdPar(params[i].getName());
            if (cPar == null) {
                CanvasParameter.addParameter(params[i]);
            }
        }
    }

    /**
     *
     * @param argString
     * @return
     */
    protected CanvasParameter getPar(String argString) {
        TreeMap<String,CanvasParameter> map = getParameterMap();

        if (map == null) {
            return null;
        }

        CanvasParameter par = CanvasParameter.getPar(map, argString);

        return par;
    }
}
