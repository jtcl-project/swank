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

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.*;

import org.jfree.ui.RectangleEdge;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.util.*;
import java.text.DecimalFormat;

public class XYLineAndShapeComplete extends XYPlotShape {

    static XYToolTipGenerator generator = new DCXYToolTipGenerator("{0} {1} {2} {3}", new DecimalFormat("0.000"), new DecimalFormat("0.000"));
    static CanvasParameter[] parameters = {
        new TagsParameter(), new DatasetParameter(), new ShapesvisibleParameter(), new LegendStateParameter(), new LegendLocParameter(),
        new PaintParameter(), new LinesvisibleParameter(), new SplineParameter(),
        new DLabelParameter(), new DMinParameter(), new DMaxParameter(), new DAutoParameter(),
        new RLabelParameter(), new RMinParameter(), new RMaxParameter(), new RAutoParameter(),
        new TransformerParameter()};
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }
    String plotType = "lineandshape";

    public XYLineAndShapeComplete() {
        rect2D = new Rectangle2D.Double();
        setRenderer();
        plot.setRangeAxis(new NumberAxis());
        plot.setDomainAxis(new NumberAxis());
        setShape(rect2D);
    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public void setRenderer() {
        renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        renderer.setToolTipGenerator(generator);
    }

    public void setRenderer(XYItemRenderer newRenderer) {
        renderer = newRenderer;
        plot.setRenderer(renderer);
        renderer.setToolTipGenerator(generator);
    }
    public String getType() {
        return "xyplot";
    }
}


