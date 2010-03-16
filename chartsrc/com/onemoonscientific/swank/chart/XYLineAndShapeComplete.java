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
    static XYToolTipGenerator generator = new DCXYToolTipGenerator("{0} {1} {2} {3}", new DecimalFormat("0.000"), new DecimalFormat("0.000") ); 
    static CanvasParameter[] parameters = {
        new TagsParameter(),  new DatasetParameter(), new ShapesvisibleParameter(),
         new PaintParameter(), new LinesvisibleParameter(), new SplineParameter(),
         new DLabelParameter(), new DMinParameter(), new DMaxParameter(), new DAutoParameter(),
         new RLabelParameter(), new RMinParameter(), new RMaxParameter(), new RAutoParameter(),
 };
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

    public  void setRenderer() {
        renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        renderer.setToolTipGenerator(generator); 
    }
    public  void setRenderer(XYItemRenderer newRenderer) {
        renderer = newRenderer;
        plot.setRenderer(renderer);
        renderer.setToolTipGenerator(generator); 
    }
   static class DatasetParameter extends CanvasParameter {
        private static String name = "dataset";
        String[] datasetNames = new String[0];

        DatasetParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }
        public TclObject getValue(Interp interp, SwkShape swkShape)
             throws TclException {
             if ((swkShape == null) || !(swkShape instanceof XYPlotShape)) {
                 throw new TclException(interp, "xyplot shape doesn't exist");
            }
            int nDatasets = ((XYPlotShape) swkShape).plot.getDatasetCount();
            TclObject list = TclList.newInstance();
            for (int i=0;i<nDatasets;i++) {
                 XYData xyData = (XYData) ((XYPlotShape) swkShape).plot.getDataset(i);
                 TclList.append(interp,list,TclString.newInstance(xyData.getName()));
            }
            return list;
        }

        public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
             throws TclException {
             TclObject[] datasetNameList = TclList.getElements(interp, arg);

             if (datasetNameList.length == 0) {
                 throw new TclException(interp,
                     "bad dataset value, must be \"dataset1 dataset2 ...\"");
             }
             String datasetNamesTmp[] = new String[datasetNameList.length];
             for (int i=0;i<datasetNameList.length;i++) {
                 datasetNamesTmp[i] =  datasetNameList[i].toString();
             }
             datasetNames = datasetNamesTmp;
         }

         public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
              int nDatasets = ((XYPlotShape) swkShape).plot.getDatasetCount();
              for (int i=0;i<datasetNames.length;i++) {
               ((XYPlotShape) swkShape).setDataset(i,datasetNames[i]);
               if (i >= nDatasets) {
                     XYLineAndShapeRenderer newRenderer = new XYLineAndShapeRenderer();
                     newRenderer.setToolTipGenerator(generator); 
                    ((XYPlotShape) swkShape).plot.setRenderer(i,newRenderer);
               }
              }
         }
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
            XYPlot plot = ((XYPlotShape) swkShape).plot;
            StringBuffer sBuf = new StringBuffer();
            int nDatasets = plot.getDatasetCount();
            int j = 0;
            for (int iData=0;iData<nDatasets;iData++) {
                XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
                if (renderer == null) {
                     renderer = ((XYPlotShape) swkShape).plot.getRenderer();
                }
                if (renderer instanceof XYLineAndShapeRenderer) {
                    XYData data = (XYData) plot.getDataset(iData);
                    int nSeries = data.getSeriesCount();

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
                          j++;
                  }
                } else {
                    sBuf.append('0');
                }
            }
            return sBuf.toString();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            XYPlot plot = ((XYPlotShape) swkShape).plot;
            String newValue = getNewValue();
            String[] values = newValue.split(" ");
            int nDatasets = plot.getDatasetCount();
            int j = 0;
            for (int iData=0;iData<nDatasets;iData++) {
                XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
                if (renderer == null) {
                     renderer = ((XYPlotShape) swkShape).plot.getRenderer();
                }
                if (renderer instanceof XYLineAndShapeRenderer) {
                    XYData data = (XYData) plot.getDataset(iData);
                    int nSeries = data.getSeriesCount();
                    for (int i=0;i<nSeries;i++) {
                        boolean bValue = true;
                        if (values.length > j) {
                             bValue = values[j].charAt(0) == '1';
                        }
                        ((XYLineAndShapeRenderer) renderer).setSeriesLinesVisible(i,bValue);
                           j++;
                 }
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
            XYPlot plot = ((XYPlotShape) swkShape).plot;
            StringBuffer sBuf = new StringBuffer();
            int nDatasets = plot.getDatasetCount();
            int j = 0;
            for (int iData=0;iData<nDatasets;iData++) {
                XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
                if (renderer == null) {
                     renderer = ((XYPlotShape) swkShape).plot.getRenderer();
                }
                if (renderer instanceof XYLineAndShapeRenderer) {
                    XYData data = (XYData) plot.getDataset(iData);
                    int nSeries = data.getSeriesCount();

                    for (int i=0;i<nSeries;i++) {
                        boolean bValue = (((XYLineAndShapeRenderer) renderer).getSeriesShapesVisible(i)).booleanValue();
                        if (i > 0) {
                           sBuf.append(' ');
                        }
                        if (bValue) {
                           sBuf.append('1');
                        } else {
                           sBuf.append('0');
                        }
                          j++;
                  }
                } else {
                    sBuf.append('0');
                }
            }
            return sBuf.toString();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            XYPlot plot = ((XYPlotShape) swkShape).plot;
            String newValue = getNewValue();
            String[] values = newValue.split(" ");
            int nDatasets = plot.getDatasetCount();
            int j = 0;
            for (int iData=0;iData<nDatasets;iData++) {
                XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
                if (renderer == null) {
                     renderer = ((XYPlotShape) swkShape).plot.getRenderer();
                }
                if (renderer instanceof XYLineAndShapeRenderer) {
                    XYData data = (XYData) plot.getDataset(iData);
                    int nSeries = data.getSeriesCount();
                    for (int i=0;i<nSeries;i++) {
                        boolean bValue = true;
                        if (values.length > j) {
                             bValue = values[j].charAt(0) == '1';
                        }
                        ((XYLineAndShapeRenderer) renderer).setSeriesShapesVisible(i,bValue);
                        j++;

                    }
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
            XYPlot plot = ((XYPlotShape) swkShape).plot;
            StringBuffer sBuf = new StringBuffer();
            int nDatasets = plot.getDatasetCount();
            int j = 0;
            for (int iData=0;iData<nDatasets;iData++) {
                XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
                if (renderer == null) {
                     renderer = ((XYPlotShape) swkShape).plot.getRenderer();
                }
                if (renderer instanceof XYLineAndShapeRenderer) {
                    XYData data = (XYData) plot.getDataset(iData);
                    int nSeries = data.getSeriesCount();

                    for (int i=0;i<nSeries;i++) {
                       Color color = (Color) (((XYLineAndShapeRenderer) renderer).getSeriesPaint(i));
                        if (j > 0) {
                           sBuf.append(' ');
                        }
                        sBuf.append(SwankUtil.parseColor(color));
                        j++;
                  }
                } else {
                    sBuf.append('0');
                }
            }
            return sBuf.toString();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            XYPlot plot = ((XYPlotShape) swkShape).plot;
            String newValue = getNewValue();
            String[] values = newValue.split(" ");
            Color color = Color.BLACK;
            int nDatasets = plot.getDatasetCount();
            int j = 0;
            for (int iData=0;iData<nDatasets;iData++) {
                XYItemRenderer renderer = ((XYPlotShape) swkShape).plot.getRenderer(iData);
                if (renderer == null) {
                     renderer = ((XYPlotShape) swkShape).plot.getRenderer();
                }
                if (renderer instanceof XYLineAndShapeRenderer) {
                    XYData data = (XYData) plot.getDataset(iData);
                    int nSeries = data.getSeriesCount();
                    for (int i=0;i<nSeries;i++) {
                       color = Color.BLACK;
                      if (values.length > j) {
                         try {
                            color = SwankUtil.getColor(values[j]);
                         } catch (Exception e) {
                               System.out.println("ex "+e.getMessage());
                         }
                         }
                         ((XYLineAndShapeRenderer) renderer).setSeriesPaint(i,color);
                         ((XYLineAndShapeRenderer) renderer).setSeriesFillPaint(i,color); 
                         ((XYLineAndShapeRenderer) renderer).setSeriesOutlinePaint(i,color);
                      
                      j++;

                    }
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
                     XYLineAndShapeRenderer newRenderer = new XYLineAndShapeRenderer();
                     newRenderer.setToolTipGenerator(generator); 
                     plotShape.setRenderer(newRenderer);
                }
            } else
                if (renderer instanceof XYSplineRenderer) {
                     XYSplineRenderer splineRenderer = (XYSplineRenderer) renderer;
                     splineRenderer.setPrecision(newValue);
                } else {
                    XYPlotShape plotShape = (XYPlotShape) swkShape;
                    XYLineAndShapeRenderer newRenderer = new XYLineAndShapeRenderer();
                    newRenderer.setToolTipGenerator(generator); 
                    plotShape.setRenderer(newRenderer);
                }
            }
        }

    static class DLabelParameter extends StringParameter {
        private static String name = "dlabel";

        DLabelParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
            return axis.getLabel();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
             axis.setLabel(getNewValue());
        }
    }
    static class RLabelParameter extends StringParameter {
        private static String name = "rlabel";

        RLabelParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public String getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getRangeAxis();
            return axis.getLabel();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getRangeAxis();
             axis.setLabel(getNewValue());
        }
    }

    static class RMinParameter extends DoubleParameter {
        private static String name = "rmin";

        RMinParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getRangeAxis();
            return axis.getLowerBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getRangeAxis();
             axis.setLowerBound(getNewValue());
        }
    }
     static class DMinParameter extends DoubleParameter {
        private static String name = "dmin";

        DMinParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
            return axis.getLowerBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
             axis.setLowerBound(getNewValue());
        }
    }
   static class DMaxParameter extends DoubleParameter {
        private static String name = "dmax";

        DMaxParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
            return axis.getUpperBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
             axis.setUpperBound(getNewValue());
        }
    }
   static class RMaxParameter extends DoubleParameter {
        private static String name = "rmax";

        RMaxParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public double getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getRangeAxis();
            return axis.getUpperBound();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
             NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getRangeAxis();
             axis.setUpperBound(getNewValue());
        }
    }
   static class DAutoParameter extends BooleanParameter {
        private static String name = "dauto";

        DAutoParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public boolean getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
            return axis.isAutoRange();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getDomainAxis();
            axis.setAutoRange(getNewValue());
        }
    }
   static class RAutoParameter extends BooleanParameter {
        private static String name = "rauto";

        RAutoParameter() {
            CanvasParameter.addParameter(this);
        }

        public String getName() {
            return name;
        }

        public boolean getValue(SwkShape swkShape) {
            NumberAxis axis =(NumberAxis)  ((XYPlotShape) swkShape).plot.getRangeAxis();
            return axis.isAutoRange();
        }

        public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
            NumberAxis axis = (NumberAxis) ((XYPlotShape) swkShape).plot.getRangeAxis();
            axis.setAutoRange(getNewValue());
        }
    }
}


