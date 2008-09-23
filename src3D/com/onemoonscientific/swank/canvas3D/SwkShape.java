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
package com.onemoonscientific.swank.canvas3D;


import com.sun.j3d.utils.geometry.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;

import javax.media.j3d.*;




public abstract class SwkShape implements SwkShape3DConfig {
    Shape3D shape = null;
    Primitive primitive = null;
    NvBranchGroup bG = null;
    SwkShape previous = null;
    SwkShape next = null;
    SwkCanvas canvas = null;
    Appearance appearance = null;
    Hashtable tags = new Hashtable();
    String[] tagNames = null;
    Transformer transformer = null;
    int id;

    public SwkShape() {
        bG = new NvBranchGroup();
    }

    public SwkShape(Primitive primitive, SwkCanvas canvas) {
        this.primitive = primitive;
        this.canvas = canvas;
        appearance = canvas.defaultAppearance;
        bG = new NvBranchGroup();
    }

    public SwkShape(SwkCanvas canvas) {
        this.canvas = canvas;
        appearance = canvas.defaultAppearance;
        bG = new NvBranchGroup();
    }

    public void setCanvas(SwkCanvas canvas) {
        this.canvas = canvas;
        appearance = this.canvas.defaultAppearance;
    }

    public void setShape(Shape3D shape) {
        this.shape = shape;
    }

    // public abstract void config(Interp interp, TclObject argv[],int start);
    public void paintShape(Graphics2D g2) {
    }

    public void config(Interp interp, TclObject[] argv, int start)
        throws TclException {
    }

    public void coords(Interp interp, SwkCanvas canvas, TclObject[] argv,
        int start) throws TclException {
    }

    public void coords(Interp interp) throws TclException {
    }

    public TclObject itemGet(Interp interp, TclObject argv)
        throws TclException {
        return (TclString.newInstance(""));
    }

    public String toString() {
        return (String.valueOf(id));
    }

    public void move(double x, double y) {
    }

    public boolean hitShape(double x, double y) {
        return false;
    }

    public TclObject hit(double x, double y) {
        return TclString.newInstance("");
    }

    public void select(TclObject tObj) {
    }

    public int getIndexAtXY(double x, double y) {
        return 0;
    }

    public int getIndex(Interp interp, TclObject indexArg)
        throws TclException {
        String indexString = indexArg.toString();

        if (indexString.startsWith("@")) {
            if (indexString.length() > 1) {
                int commaPos = indexString.indexOf(',');

                if (commaPos < 0) {
                    throw new TclException(interp,
                        "bad text index \"" + indexString + "\"");
                }

                String xS = indexString.substring(1, commaPos);
                String yS = indexString.substring(commaPos + 1);
                int x = Integer.valueOf(xS).intValue();
                int y = Integer.valueOf(yS).intValue();
                int offset = getIndexAtXY(x, y);

                return offset;
            } else {
                throw new TclException(interp,
                    "bad text index \"" + indexString + "\"");
            }
        }

        return 0;
    }

    public void scale(double xOrigin, double yOrigin, double xScale,
        double yScale) {
        /* FIXME
            if (this instanceof SwkCanvText) {
                SwkCanvText ctext = (SwkCanvText) this;
                ctext.setX(ctext.getX() - xOrigin);
                ctext.setY(ctext.getY() - yOrigin);
                ctext.setX(ctext.getX() * xScale);
                ctext.setY(ctext.getY() * yScale);
                ctext.setX(ctext.getX() + xOrigin);
                ctext.setY(ctext.getY() + yOrigin);
            } else {
                AffineTransform at = new AffineTransform();
                at.translate(xOrigin, yOrigin);
                at.scale(xScale, yScale);
                at.translate(-xOrigin, -yOrigin);
                shape = at.createTransformedShape(shape);
            }
         */
    }

    public Rectangle2D getBounds2D() {
               Rectangle2D bounds = null;
        return bounds;
    }
    public String getType() {
       return "";
    }
   public static void config(Interp interp, SwkImageCanvas swkCanvas,
        TclObject[] argv, int start) throws TclException {
        // Map parameterMap = getParameterMap();
        CanvasParameter[] setPars = new CanvasParameter[(argv.length - start) / 2];
        boolean gotPar = false;

        // this is more difficult than it should be because we want to check validity of parameter name
        // before we've figured out which shape were configuring
        for (int i = start, j = 0; i < argv.length; i += 2, j++) {
            CanvasParameter cPar = null;

            cPar = CanvasParameter.getStdPar(argv[i].toString());
           //  Mostly not used as custom pars are added automatically to stdPars

            if (cPar == null) {
                String parName = "com.onemoonscientific.swank.canvas." +
                    argv[i].toString().substring(1, 2).toUpperCase() +
                    argv[i].toString().substring(2) + "Parameter";
                Class newClass = null;

                try {
                    newClass = Class.forName(parName);
                } catch (ClassNotFoundException cnfE) {
                    throw new TclException(interp,
                        "class " + parName + " doesn't exist " +
                        cnfE.toString());

                    //continue;
                }

                try {
                    cPar = (CanvasParameter) newClass.newInstance();
                } catch (InstantiationException iE) {
                    throw new TclException(interp,
                        "can't instantiate " + parName);
                } catch (IllegalAccessException iaE) {
                    throw new TclException(interp,
                        "can't instantiate " + parName);
                }
            }

            if (cPar != null) {
                setPars[j] = (CanvasParameter) cPar.clone();
                setPars[j].setValue(interp, swkCanvas, argv[i + 1]);
                gotPar = true;
            }
        }

        if (gotPar) {
         // fixme   (new SwkShapeRunnable(swkCanvas, argv[start - 1].toString(), setPars)).exec();
        }
    }
    public  CanvasParameter[] getParameters() {
        // fixme 
        return null;
    }
   public Map getParameterMap() {
        return null;
    }
    public CanvasParameter getPar(String argString) {
        Map map = getParameterMap();

        if (map == null) {
            return null;
        }

        CanvasParameter par = CanvasParameter.getPar(map, argString);

        return par;
    }


}
