/*

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
 * SwkRectangle.java
 *
 * Created on February 19, 2000, 3:02 PM
 */

/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;


public class SwkRectangle extends SwkShape {
    static CanvasParameter[] parameters = {
        new WidthParameter(), new TextureParameter(), new GradientParameter(),
        new FillParameter(), new OutlineParameter(), new TagsParameter(),
        new TransformerParameter(), new RotateParameter(), new ShearParameter(),
        new StateParameter(),
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }

    String imageName = "";
    Rectangle2D rect2D = null;

    SwkRectangle(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        rect2D = (Rectangle2D) shape;
    }

    public void coords(SwkImageCanvas canvas, double[] coords)
        throws SwkException {
        if (coords.length != 4) {
            throw new SwkException("wrong # coordinates: expected 4, got " +
                coords.length);
        }

        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        storeCoords[0] = coords[0];
        storeCoords[1] = coords[1];
        storeCoords[2] = coords[2];
        storeCoords[3] = coords[3];
        applyCoordinates();
    }

    void applyCoordinates() {
        checkCoordinates(storeCoords);
        rect2D.setFrame(storeCoords[0], storeCoords[1],
            storeCoords[2] - storeCoords[0], storeCoords[3] - storeCoords[1]);

        AffineTransform aT = new AffineTransform();
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(xShear, yShear);
        aT.translate(-storeCoords[0], -storeCoords[1]);
        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
            ((storeCoords[1] + storeCoords[3]) / 2.0));
        genGradient(aT);
        shape = aT.createTransformedShape(rect2D);
    }

    CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "rectangle";
    }
}
