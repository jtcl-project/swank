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
import org.jfree.data.general.*;
import org.jfree.data.xy.*;
import org.jfree.chart.renderer.xy.*;

import org.jfree.ui.RectangleEdge;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.util.*;
import java.text.DecimalFormat;

public class XYBarStatShapeComplete extends XYPlotShape {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static CanvasParameter[] parameters = {
        new TagsParameter(), new DatasetParameter(), new LegendStateParameter(), new LegendLocParameter(),
        new PaintParameter(), new SplineParameter(),
        new DLabelParameter(), new DMinParameter(), new DMaxParameter(), new DAutoParameter(),
        new RLabelParameter(), new RMinParameter(), new RMaxParameter(), new RAutoParameter(),
        new TransformerParameter(), new WidthParameter(), new FillParameter(), new GradientParameter()
    };

    static {
        initializeParameters(parameters, parameterMap);
    }
    String plotType = "xystatplot";

    public XYBarStatShapeComplete() {
        rect2D = new Rectangle2D.Double();
        setRenderer();
        plot.setRangeAxis(new NumberAxis());
        plot.setDomainAxis(new NumberAxis());
        setShape(rect2D);
        setFill(Color.gray);
        setWidth(0.95);
    }
   /**
     *
     * @return
     */
    public TreeMap<String,CanvasParameter> getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return plotType;
    }

    public void paintShape(Graphics2D g2) {
        if (getFillGradient() != null) {
            renderer.setSeriesPaint(0, getFillGradient());
        } else {
            renderer.setSeriesPaint(0, getFill());
        }
        XYData data = (XYData) plot.getDataset();
        data.setDeltaX(getWidth());
        super.paintShape(g2);
    }

    /**
     *
     */
    public void setRenderer() {
        renderer = new StatisticalXYBarRenderer();
        plot.setRenderer(renderer);
        XYToolTipGenerator generator = new DCXYToolTipGenerator("{0} {1} {2} {3}", new DecimalFormat("0.000"), new DecimalFormat("0.000"));
        renderer.setToolTipGenerator(generator);
    }

    public void setDataset(Dataset dataset) {
        if (dataset instanceof XYTableStatsData) {
            plot.setDataset((XYTableStatsData) dataset);
        }
    }

}

