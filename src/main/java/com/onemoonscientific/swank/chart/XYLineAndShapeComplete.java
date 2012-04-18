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
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.renderer.xy.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.text.DecimalFormat;

public class XYLineAndShapeComplete extends XYPlotShape {
    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();

    static XYToolTipGenerator generator = new DCXYToolTipGenerator("{0} {1} {2} {3}", new DecimalFormat("0.000"), new DecimalFormat("0.000"));
    static CanvasParameter[] parameters = {
        new TagsParameter(), new StateParameter(), new DatasetParameter(), new ShapesvisibleParameter(), new LegendStateParameter(), new LegendLocParameter(),
        new PaintParameter(), new LinesvisibleParameter(), new SplineParameter(),
        new DLabelParameter(), new DMinParameter(), new DMaxParameter(), new DAutoParameter(),
        new RLabelParameter(), new RMinParameter(), new RMaxParameter(), new RAutoParameter(),
        new TransformerParameter(), new RInvertedParameter(), new DInvertedParameter(),
        new ShapeParameter()};

    static {
        initializeParameters(parameters, parameterMap);
    }
    String plotType = "xyplot";
    boolean[] linesVisible = new boolean[0];
    boolean[] shapesVisible = new boolean[0];
    public XYLineAndShapeComplete() {
        rect2D = new Rectangle2D.Double();
        setRenderer();
        plot.setRangeAxis(new NumberAxis());
        plot.setDomainAxis(new NumberAxis());
        setShape(rect2D);
    }
   /**
     *
     * @return
     */
    public TreeMap<String,CanvasParameter> getParameterMap() {
        return parameterMap;
    }

    @Override
        public String getType() {
            return plotType;
        }

    @Override
    public void setRenderer() {
        renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        plot.setDrawingSupplier(new DefaultDrawingSupplier());
        renderer.setToolTipGenerator(generator);
    }
    public void setSplineRenderer(final int precision) {
        XYSplineRenderer splineRenderer = new XYSplineRenderer();
        splineRenderer.setPrecision(precision);
        renderer = splineRenderer;
        plot.setRenderer(renderer);
        plot.setDrawingSupplier(new DefaultDrawingSupplier());
        renderer.setToolTipGenerator(generator);
    }

    public void setRenderer(XYItemRenderer newRenderer) {
        renderer = newRenderer;
        plot.setRenderer(renderer);
        renderer.setToolTipGenerator(generator);
    }
    public void paintShape(Graphics2D g2) {
         updateLinesAndShapes();     
         super.paintShape(g2);
    }

    void setLinesVisible(boolean[] value) {
        linesVisible = value;
    }
    public boolean[] getLinesVisible() {
         return linesVisible.clone();
    }
    void setShapesVisible(boolean[] value) {
        shapesVisible = value;
    }
    public boolean[] getShapesVisible() {
         return shapesVisible.clone();
    }
    void updateLinesAndShapes() {
            int nDatasets = plot.getDatasetCount();
            int j = 0;
            for (int iData = 0; iData < nDatasets; iData++) {
                    XYItemRenderer renderer = plot.getRenderer(iData);
                    if (renderer == null) {
                            renderer = plot.getRenderer();
                    }
                    if (renderer instanceof XYLineAndShapeRenderer) {
                            XYData data = (XYData) plot.getDataset(iData);
                            if (data != null) {
                                int nSeries = data.getSeriesCount();
                                for (int i = 0; i < nSeries; i++) {
                                        boolean bValue = true;
                                        if (linesVisible.length > j) {
                                                bValue = linesVisible[j];
                                        }
                                        ((XYLineAndShapeRenderer) renderer).setSeriesLinesVisible(i, bValue);
                                        bValue = true;
                                        if (shapesVisible.length > j) {
                                                bValue = shapesVisible[j];
                                        }
                                        ((XYLineAndShapeRenderer) renderer).setSeriesShapesVisible(i, bValue);
                                        j++;
                                }
                           }
                    }
            }
            if (linesVisible.length != j) {
                 boolean[] visibleTemp = new boolean[j];
                 for (int i=0;((i<linesVisible.length) && (i < j));i++) {
                      visibleTemp[i] = linesVisible[i];
                 }
                 for (int i=linesVisible.length;i<j;i++) {
                      visibleTemp[i] = true;
                 }
                 linesVisible = visibleTemp;
            }
            if (shapesVisible.length != j) {
                 boolean[] visibleTemp = new boolean[j];
                 for (int i=0;((i<shapesVisible.length) && (i < j));i++) {
                      visibleTemp[i] = shapesVisible[i];
                 }
                 for (int i=shapesVisible.length;i<j;i++) {
                      visibleTemp[i] = true;
                 }
                 shapesVisible = visibleTemp;
            }
    }
}


