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

import com.onemoonscientific.swank.canvas.*;

import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.*;

import org.jfree.chart.LegendItemSource;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;


    class PlotLegend  {
    LegendItemSource legendSource = null;
    LegendTitle legend = null;
    Rectangle2D.Double plotArea = null;
    RectangleEdge edge = RectangleEdge.BOTTOM;
    
    PlotLegend(LegendItemSource legendSource) {
        initLegend(legendSource);
    }
    void initLegend(LegendItemSource legendSource) {
        if (legendSource != null) {
            legend = new LegendTitle(legendSource);
            if (legend != null) {
                legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
                legend.setBorder(new BlockBorder());
                legend.setBackgroundPaint(Color.white);
                legend.setPosition(RectangleEdge.BOTTOM);
            }
        }
    }
    Rectangle2D  arrangeLegend(Graphics2D g2, Rectangle2D area) {
        if (legend == null) {
            throw new IllegalArgumentException("Null 't' argument.");
        }
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");
        }
        Rectangle2D legendArea = new Rectangle2D.Double();
        double areaWidth = area.getWidth();
        if (areaWidth <= 0.0) {
            return null;
        }
        double areaHeight = area.getHeight();
        if (areaHeight <= 0.0) {
            return null;
        }
        RectangleConstraint constraint = new RectangleConstraint(
                areaWidth, new Range(0.0, areaWidth), LengthConstraintType.RANGE,
                areaHeight, new Range(0.0, areaHeight), LengthConstraintType.RANGE
                );
        Object retValue = null;
        Size2D size = legend.arrange(g2, constraint);
        legendArea = createAlignedRectangle2D(size, area, legend.getHorizontalAlignment(), VerticalAlignment.BOTTOM);
        area.setRect(area.getX(), area.getY(), area.getWidth(), area.getHeight() - size.height);
        return legendArea;
    }
    void drawLegend(Graphics2D g2, Rectangle2D legendArea) {
        legend.draw(g2, legendArea, null);
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
                x, y, dimensions.width, dimensions.height
                );
    }
}
