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
/*
 * SwkGPath.java
 *
 * Created on February 19, 2000, 3:14 PM
 */
/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.TreeMap;


/**
 *
 * @author brucejohnson
 */
public class ItemSymbols extends SwkShape implements SymbolInterface {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parameters = {
        new SymbolParameter(), new RadiusParameter(), new RotateParameter(),
        new ShearParameter(), new TagsParameter(), new StateParameter(), new NodeParameter(),
        new TransformerParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    float radius = 2.0f;
    int symbolType = 3;
    GeneralPath gPath = null;

    /**
     *
     * @param shape
     * @param canvas
     */
    public ItemSymbols(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        gPath = (GeneralPath) shape;
        fill = null;
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
     * @param newSymbolType
     */
    public void setSymbolType(int newSymbolType) {
        symbolType = newSymbolType;
    }

    /**
     *
     * @return
     */
    public String getSymbolType() {
        return SymbolParameter.getSymbolType(symbolType);
    }

    /**
     *
     * @param newRadius
     */
    public void setRadius(double newRadius) {
        radius = (float) newRadius;
    }

    /**
     *
     * @return
     */
    public double getRadius() {
        return radius;
    }

    /**
     *
     * @param canvas
     * @param coords
     * @throws SwkException
     */
    @Override
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length < 2) {
            throw new SwkException(
                    "wrong # coordinates: expected at least 2, got "
                    + coords.length);
        }

        if ((coords.length % 2) != 0) {
            throw new SwkException(
                    "wrong # coordinates: expected even number, got "
                    + coords.length);
        }

        gPath.reset();

        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }

    /**
     *
     */
    @Override
    protected void applyCoordinates() {
        for (int i = 0; i < storeCoords.length; i += 2) {
            addSymbol((float) storeCoords[i], (float) storeCoords[i + 1], radius);
        }

        AffineTransform aT = new AffineTransform();
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(xShear, yShear);
        aT.translate(-storeCoords[0], -storeCoords[1]);
        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
                ((storeCoords[1] + storeCoords[3]) / 2.0));
        shape = aT.createTransformedShape(gPath);
    }

    /**
     *
     * @param x1
     * @param y1
     * @param radius
     */
    protected void addSymbol(float x1, float y1, float radius) {
        float x2;
        float y2;

        /*
        gPath.moveTo(x1-radius,y1);
        gPath.lineTo(x1+radius,y1);
        gPath.moveTo(x1,y1-radius);
        gPath.lineTo(x1,y1+radius);
         */

        //System.out.println(symbolType+" "+radius);
        switch (symbolType) {
            case 0: { //circle

                Ellipse2D ellipse = new Ellipse2D.Float(x1 - radius, y1 - radius,
                        2 * radius, 2 * radius);
                gPath.append(ellipse, false);

                break;
            }

            case 1: { //triangle up
                gPath.moveTo(x1, y1 - radius);
                gPath.lineTo(x1 - (radius * 0.67f), y1 + (radius * 0.66f));
                gPath.lineTo(x1 + (radius * 0.67f), y1 + (radius * 0.66f));
                gPath.closePath();

                break;
            }

            case 2: { //triangle down
                gPath.moveTo(x1, y1 + radius);
                gPath.lineTo(x1 - (radius * 0.67f), y1 - (radius * 0.66f));
                gPath.lineTo(x1 + (radius * 0.67f), y1 - (radius * 0.66f));
                gPath.closePath();

                break;
            }

            case 3: { //cross
                gPath.moveTo(x1, y1 + radius);
                gPath.lineTo(x1, y1 - radius);
                gPath.moveTo(x1 - radius, y1);
                gPath.lineTo(x1 + radius, y1);

                break;
            }

            case 4: { //square
                gPath.moveTo(x1 - radius, y1 - radius);
                gPath.lineTo(x1 + radius, y1 - radius);
                gPath.lineTo(x1 + radius, y1 + radius);
                gPath.lineTo(x1 - radius, y1 + radius);
                gPath.closePath();

                break;
            }

            case 5: { //diamond
                gPath.moveTo(x1, y1 - radius);
                gPath.lineTo(x1 + radius, y1);
                gPath.lineTo(x1, y1 + radius);
                gPath.lineTo(x1 - radius, y1);
                gPath.closePath();

                break;
            }
        }
    }

    /**
     *
     * @return
     */
    public String getType() {
        return "symbols";
    }
}
