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
 * SwkEllipse.java
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

import java.util.*;


public class ItemEllipse extends SwkShape implements TextInterface {

    static CanvasParameter[] parameters = {
        new FillParameter(), new OutlineParameter(), new TextureParameter(),
        new GradientParameter(), new RotateParameter(), new ShearParameter(),
        new StateParameter(), new TagsParameter(), new WidthParameter(),
        new TransformerParameter(),};
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }
    Ellipse2D ellipse2D = null;
    TextParameters textPar = TextParameters.getDefault();

    ItemEllipse(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        storeCoords = new double[4];
        ellipse2D = (Ellipse2D) shape;
    }

    public String getText() {
        return textPar.getText();
    }

    public void setText(String newValue) {
        textPar = TextParameters.setText(textPar, newValue);
    }

    public float[] getAnchor() {
        return textPar.getAnchor();
    }

    public void setAnchor(float[] newValue) {
        textPar = TextParameters.setAnchor(textPar, newValue);
    }

    public Font getFont() {
        return textPar.getFont();
    }

    public void setFont(Font newValue) {
        textPar = TextParameters.setFont(textPar, newValue);
    }

    public Color getTextColor() {
        return textPar.getTextColor();
    }

    public void setTextColor(Color newValue) {
        textPar = TextParameters.setTextColor(textPar, newValue);
    }

    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 4) {
            throw new SwkException("wrong # coordinates: expected 4, got "
                    + coords.length);
        }

        System.arraycopy(coords, 0, storeCoords, 0, 4);
        applyCoordinates();
    }

    public String getType() {
        return "oval";
    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public void applyCoordinates() {
        checkCoordinates(storeCoords);

        AffineTransform aT = new AffineTransform();
        aT.shear(xShear, yShear);
        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
                ((storeCoords[1] + storeCoords[3]) / 2.0));
        ellipse2D.setFrame(storeCoords[0], storeCoords[1],
                storeCoords[2] - storeCoords[0], storeCoords[3] - storeCoords[1]);
        genGradient(aT);
        shape = aT.createTransformedShape(ellipse2D);
    }
}
