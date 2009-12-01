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


public class XYLineAndShape extends XYPlotShape {
    static CanvasParameter[] parameters = {
        new TagsParameter(), new RangeaxisParameter(), new DomainaxisParameter(), new DatasetParameter(), new ShapesvisibleParameter(),
         new PaintParameter(), new LinesvisibleParameter(), new SplineParameter() };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }

    String plotType = "lineandshape";

    public XYLineAndShape() {
        setRenderer();
        setShape(null);
    }
    public CanvasParameter[] getParameters() {
        return parameters;
    }
 
    public Map getParameterMap() {
        return parameterMap;
    }

    public  void setRenderer() {
        renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        XYToolTipGenerator generator = new DCXYToolTipGenerator("{0} {1} {2} {3}", new DecimalFormat("0.000"), new DecimalFormat("0.000") ); 
        renderer.setToolTipGenerator(generator); 
    }
    public  void setRenderer(XYItemRenderer newRenderer) {
        renderer = newRenderer;
        plot.setRenderer(renderer);
        XYToolTipGenerator generator = new DCXYToolTipGenerator("{0} {1} {2} {3}", new DecimalFormat("0.000"), new DecimalFormat("0.000") ); 
        renderer.setToolTipGenerator(generator); 
    }
    static class LinesvisibleParameter extends StringParameter {
        private static String name = "linesvisible";

        LinesvisibleParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYPlot plot = ((XYPlotShape) swkShape).plot;
                XYData data = (XYData) plot.getDataset();
                int nSeries = data.getSeriesCount();
                StringBuffer sBuf = new StringBuffer();
                for (int i=0;i<nSeries;i++) {
                    boolean bValue = (((XYLineAndShapeRenderer) renderer).getSeriesLinesVisible(i)).booleanValue();
                    if (i > 0) {
                       sBuf.append(' ');
                    }
                    if (bValue) {
                       sBuf.append('1');
                    } else {
                       sBuf.append('0');
                    }
                }
                return sBuf.toString();
            } else {
                return "0";
            }
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYPlot plot = ((XYPlotShape) swkShape).plot;
                String newValue = getNewValue();
                String[] values = newValue.split(" ");
                XYData data = (XYData) plot.getDataset();
                int nSeries = data.getSeriesCount();
                for (int i=0;i<nSeries;i++) {
                    boolean bValue = true;
                    if (values.length > i) {
                         bValue = values[i].charAt(0) == '1';
                    }
                    ((XYLineAndShapeRenderer) renderer).setSeriesLinesVisible(i,bValue);
                }
            }
        }
    }

    static class ShapesvisibleParameter extends StringParameter {
        private static String name = "shapesvisible";

        ShapesvisibleParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYPlot plot = ((XYPlotShape) swkShape).plot;
                XYData data = (XYData) plot.getDataset();
                int nSeries = data.getSeriesCount();
                StringBuffer sBuf = new StringBuffer();
                for (int i=0;i<nSeries;i++) {
                    Boolean visible = (((XYLineAndShapeRenderer) renderer).getSeriesShapesVisible(i));
                    boolean bValue = false;
                    if (visible != null) {
                        bValue = visible.booleanValue();
                    }
                    if (i > 0) {
                       sBuf.append(' ');
                    }
                    if (bValue) {
                       sBuf.append('1');
                    } else {
                       sBuf.append('0');
                    }
                }
                return sBuf.toString();
            } else {
                return "0";
            }
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYPlot plot = ((XYPlotShape) swkShape).plot;
                String newValue = getNewValue();
                String[] values = newValue.split(" ");
                XYData data = (XYData) plot.getDataset();
                int nSeries = data.getSeriesCount();
                for (int i=0;i<nSeries;i++) {
                    boolean bValue = true;
                    if (values.length > i) {
                         bValue = values[i].charAt(0) == '1';
                    }
                    ((XYLineAndShapeRenderer) renderer).setSeriesShapesVisible(i,bValue);
                }
            }
        }
    }
    static class PaintParameter extends StringParameter {
        private static String name = "paint";

        PaintParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYPlot plot = ((XYPlotShape) swkShape).plot;
                XYData data = (XYData) plot.getDataset();
                int nSeries = data.getSeriesCount();
                StringBuffer sBuf = new StringBuffer();
                for (int i=0;i<nSeries;i++) {
                    Color color = (Color) (((XYLineAndShapeRenderer) renderer).getSeriesPaint(i));
                    if (i > 0) {
                       sBuf.append(' ');
                    }
                    sBuf.append(SwankUtil.parseColor(color));
                }
                return sBuf.toString();
            } else {
                return "0";
            }
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            if (renderer instanceof XYLineAndShapeRenderer) {
                XYPlot plot = ((XYPlotShape) swkShape).plot;
                String newValue = getNewValue();
                String[] values = newValue.split(" ");
                XYData data = (XYData) plot.getDataset();
                int nSeries = data.getSeriesCount();
                Color color = Color.ORANGE;
                //((XYLineAndShapeRenderer) renderer).setPaint(color);
                for (int i=0;i<nSeries;i++) {
                    if (values.length > i) {
                         try {
                            color = SwankUtil.getColor(values[i]);
                         } catch (Exception e) {
                               System.out.println("ex "+e.getMessage());
                         }
                    }
                    ((XYLineAndShapeRenderer) renderer).setSeriesPaint(i,color);
                    ((XYLineAndShapeRenderer) renderer).setSeriesFillPaint(i,color);
                    ((XYLineAndShapeRenderer) renderer).setSeriesOutlinePaint(i,color);
                }
            }
        }
    }
   static class SplineParameter extends IntegerParameter {
        private static String name = "spline";

        SplineParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public int getValue(SwkShape swkShape) {
            int value = 0;
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            if (renderer instanceof XYSplineRenderer) {
                   XYSplineRenderer splineRenderer = (XYSplineRenderer) renderer;
                   value = splineRenderer.getPrecision();
            } 
            return value;
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            XYItemRenderer renderer = ((XYPlotShape) swkShape).renderer;
            int newValue = getNewValue();
            if (newValue <= 0) {
                if (renderer instanceof XYSplineRenderer) {
                    XYPlotShape plotShape = (XYPlotShape) swkShape;
                    plotShape.setRenderer(new XYLineAndShapeRenderer());
                }
            } else
                if (renderer instanceof XYSplineRenderer) {
                     XYSplineRenderer splineRenderer = (XYSplineRenderer) renderer;
                     splineRenderer.setPrecision(newValue);
                } else {
                    XYPlotShape plotShape = (XYPlotShape) swkShape;
                    plotShape.setRenderer(new XYSplineRenderer(newValue));
                }
            }
        }
}
