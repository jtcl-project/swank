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
public class ItemArc extends SwkShape {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parameters = {
        new ExtentParameter(), new AngleStartParameter(),
        new ArcStyleParameter(), new DashParameter(), new DashPhaseParameter(),
        new FillParameter(), new OutlineParameter(), new StateParameter(),
        new RotateParameter(), new ShearParameter(), new TagsParameter(),
        new TransformerParameter(), new WidthParameter(), new NodeParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    Arc2D arc2D = null;

    ItemArc(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        storeCoords = new double[4];
        arc2D = (Arc2D) shape;

        // FIXME  should get from ExtentParameter
        arc2D.setAngleExtent(90);
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
        if (coords.length != 4) {
            throw new SwkException("wrong # coordinates: expected 4, got "
                    + coords.length);
        }

        System.arraycopy(coords, 0, storeCoords, 0, 4);
        applyCoordinates();
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
    public String getType() {
        return "arc";
    }

    /**
     *
     */
    @Override
    protected void applyCoordinates() {
        checkCoordinates(storeCoords);

        AffineTransform aT = new AffineTransform();
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(xShear, yShear);
        aT.translate(-storeCoords[0], -storeCoords[1]);
        aT.rotate(rotate, ((storeCoords[0] + storeCoords[2]) / 2.0),
                ((storeCoords[1] + storeCoords[3]) / 2.0));
        arc2D.setFrame(storeCoords[0], storeCoords[1],
                storeCoords[2] - storeCoords[0], storeCoords[3] - storeCoords[1]);
        shape = aT.createTransformedShape(arc2D);
    }
}
