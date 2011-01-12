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
import java.awt.*;
import java.awt.geom.*;


public class ItemNode extends SwkShape {

    static CanvasParameter[] parameters = {
        new WidthParameter(), new TextureParameter(), new GradientParameter(),
        new FillParameter(), new OutlineParameter(), new TagsParameter(),
        new TransformerParameter(), new RotateParameter(), new ShearParameter(),
        new StateParameter(), new NodeParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    TextParameters textPar = TextParameters.getDefault();
    Rectangle2D rect2D = null;

    ItemNode(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        rect2D = (Rectangle2D) shape;
    }

    @Override
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 2) {
            throw new SwkException("wrong # coordinates: expected 2, got "
                    + coords.length);
        }

        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        storeCoords[0] = coords[0];
        storeCoords[1] = coords[1];
        applyCoordinates();
    }

    @Override
    public void applyCoordinates() {
        AffineTransform aT = new AffineTransform();
        genGradient(aT);
        //shape = aT.createTransformedShape(rect2D);
    }

    @Override
    public void paintShape(Graphics2D g2) {
        super.paintShape(g2);
    }

    public String getType() {
        return "node";
    }
}
