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

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;


public class ItemSegments extends SwkShape {
    static CanvasParameter[] parameters = {
        new DashParameter(), new DashPhaseParameter(), new WidthParameter(),
        new FillParameter(), new OutlineParameter(), new RotateParameter(),
        new ShearParameter(), new TagsParameter(), new StateParameter(),
        new TransformerParameter(),
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }

    boolean closePath = false;
    GeneralPath gPath = null;

    ItemSegments(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        gPath = (GeneralPath) shape;
        fill = null;
    }

    public void coords(SwkImageCanvas canvas, double[] coords)
        throws SwkException {
        if (coords.length < 4) {
            throw new SwkException(
                "wrong # coordinates: expected at least 4, got " +
                coords.length);
        }

        if ((coords.length % 4) != 0) {
            throw new SwkException(
                "wrong # coordinates: expected multiple of 4, got " +
                coords.length);
        }

        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }

    public void applyCoordinates() {
        gPath.reset();

        for (int i = 0; i < storeCoords.length; i += 4) {
            gPath.moveTo((float) storeCoords[i], (float) storeCoords[i + 1]);
            gPath.lineTo((float) storeCoords[i + 2], (float) storeCoords[i + 3]);
        }

        AffineTransform aT = new AffineTransform();
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(xShear, yShear);
        aT.translate(-storeCoords[0], -storeCoords[1]);

        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
            ((storeCoords[1] + storeCoords[3]) / 2.0));
        shape = aT.createTransformedShape(gPath);
    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "segments";
    }
}