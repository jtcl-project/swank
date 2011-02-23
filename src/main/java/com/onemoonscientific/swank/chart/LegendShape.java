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
package com.onemoonscientific.swank.chart;

import com.onemoonscientific.swank.*;
import com.onemoonscientific.swank.canvas.*;

import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.*;

import java.util.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;

/**
 *
 * @author brucejohnson
 */
public class LegendShape extends SwkShape {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parameters = {
        new TagsParameter(), new EdgeParameter(), new PlotParameter()
    };

    static {
        initializeParameters(parameters, parameterMap);
    }
    XYPlotShape xyPlotShape = null;
    BoxPlotShapeComplete boxPlotShape = null;
    String plotTag = "";
    LegendTitle legend = null;
    Rectangle2D.Double plotArea = null;
    RectangleEdge edge = RectangleEdge.BOTTOM;
    String edgeString = "bottom";

    public LegendShape() {
        setShape(null);
        initLegend();
    }
   /**
     *
     * @return
     */
    public TreeMap<String,CanvasParameter> getParameterMap() {
        return parameterMap;
    }

    void initLegend() {
        SwkImageCanvas canvas = getCanvas();
        if (canvas != null) {
            try {
                SwkShape plotShape = canvas.getShape(plotTag);
                if (plotShape instanceof XYPlotShape) {
                    xyPlotShape = (XYPlotShape) plotShape;
                } else if (plotShape instanceof BoxPlotShapeComplete) {
                    boxPlotShape = (BoxPlotShapeComplete) plotShape;
                }
            } catch (SwkException swkE) {
            }
            if (xyPlotShape != null) {
                legend = new LegendTitle(xyPlotShape.getPlot());
            } else if (boxPlotShape != null) {
                legend = new LegendTitle(boxPlotShape.getPlot());
            }
            if (legend != null) {
                legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
                legend.setBorder(new BlockBorder());
                legend.setBackgroundPaint(Color.white);
                legend.setPosition(RectangleEdge.BOTTOM);
            }
        }
    }

    @Override
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {

        if (coords.length != 4) {
            throw new SwkException("wrong # coordinates: expected 4, got "
                    + coords.length);
        }


        if ((storeCoords == null) || (storeCoords.length != coords.length)) {
            storeCoords = new double[coords.length];
        }

        System.arraycopy(coords, 0, storeCoords, 0, coords.length);
        applyCoordinates();
    }

    public void applyCoordinates() {
        AffineTransform aT = new AffineTransform();
        aT.translate(storeCoords[0], storeCoords[1]);
        aT.shear(getXShear(), getYShear());
        aT.translate(-storeCoords[0], -storeCoords[1]);
        aT.rotate(getRotate(), ((storeCoords[0] + storeCoords[2]) / 2.0),
                ((storeCoords[1] + storeCoords[3]) / 2.0));
        plotArea = new Rectangle2D.Double();
        plotArea.setFrameFromDiagonal(storeCoords[0], storeCoords[1],
                storeCoords[2], storeCoords[3]);

    }

    public String getType() {
        return "vector";
    }

    void drawLegend(Graphics2D g2, Rectangle2D area) {
        if (legend == null) {
            throw new IllegalArgumentException("Null 't' argument.");
        }
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");
        }
        double areaWidth = area.getWidth();
        if (areaWidth <= 0.0) {
            return;
        }
        double areaHeight = area.getHeight();
        if (areaHeight <= 0.0) {
            return;
        }
        RectangleConstraint constraint = new RectangleConstraint(
                areaWidth, new Range(0.0, areaWidth), LengthConstraintType.RANGE,
                areaHeight, new Range(0.0, areaHeight), LengthConstraintType.RANGE);
        Size2D size = legend.arrange(g2, constraint);
        Rectangle2D legendArea = createAlignedRectangle2D(size, area, legend.getHorizontalAlignment(), VerticalAlignment.BOTTOM);
        legend.draw(g2, legendArea, null);
        area.setRect(area.getX(), area.getY(), area.getWidth(), area.getHeight() - size.height);

    }
    // Copied from JFreeChart, need to build own implementation

    /**
     * Creates a rectangle that is aligned to the frame.
     *
     * @param dimensions
     * @param frame
     * @param hAlign
     * @param vAlign
     *
     * @return A rectangle.
     */
    private Rectangle2D createAlignedRectangle2D(Size2D dimensions,
            Rectangle2D frame, HorizontalAlignment hAlign,
            VerticalAlignment vAlign) {
        double x = Double.NaN;
        double y = Double.NaN;
        if (hAlign == HorizontalAlignment.LEFT) {
            x = frame.getX();
        } else if (hAlign == HorizontalAlignment.CENTER) {
            x = frame.getCenterX() - (dimensions.width / 2.0);
        } else if (hAlign == HorizontalAlignment.RIGHT) {
            x = frame.getMaxX() - dimensions.width;
        }
        if (vAlign == VerticalAlignment.TOP) {
            y = frame.getY();
        } else if (vAlign == VerticalAlignment.CENTER) {
            y = frame.getCenterY() - (dimensions.height / 2.0);
        } else if (vAlign == VerticalAlignment.BOTTOM) {
            y = frame.getMaxY() - dimensions.height;
        }

        return new Rectangle2D.Double(
                x, y, dimensions.width, dimensions.height);
    }

    @Override
    public void paintShape(Graphics2D g2) {
        initLegend();
        if (legend != null) {
            Rectangle2D rect = (Rectangle2D) plotArea.clone();
            drawLegend(g2, rect);
        }
    }

    private static class EdgeParameter extends StringParameter {

        private static String name = "edge";

        EdgeParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getDefault() {
            return "";
        }

        public String getValue(SwkShape swkShape) {
            return ((LegendShape) swkShape).edgeString;
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            String edgeString = getNewValue();
            RectangleEdge edge = RectangleEdge.BOTTOM;

            if ("bottom".equals(edgeString)) {
                edge = RectangleEdge.BOTTOM;
            } else if ("top".equals(edgeString)) {
                edge = RectangleEdge.TOP;
            } else if ("left".equals(edgeString)) {
                edge = RectangleEdge.LEFT;
            } else if ("right".equals(edgeString)) {
                edge = RectangleEdge.RIGHT;
            }

            ((LegendShape) swkShape).edgeString = edgeString;
            ((LegendShape) swkShape).edge = edge;
        }
    }

    private static class PlotParameter extends StringParameter {

        private static String name = "plot";

        PlotParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getDefault() {
            return "";
        }

        public String getValue(SwkShape swkShape) {
            return ((LegendShape) swkShape).plotTag;
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            String tag = getNewValue();
            ((LegendShape) swkShape).plotTag = tag;
        }
    }
}
