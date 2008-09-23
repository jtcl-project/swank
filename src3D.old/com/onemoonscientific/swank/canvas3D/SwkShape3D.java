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

import com.onemoonscientific.swank.*;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.lang.*;

import java.util.*;

import javax.media.j3d.*;

import javax.swing.*;

import javax.vecmath.*;


public abstract class SwkShape3D implements SwkShape3DConfig {
    Shape3D shape = null;
    Primitive primitive = null;
    NvBranchGroup bG = null;
    SwkShape3D previous = null;
    SwkShape3D next = null;
    SwkCanvas3D canvas = null;
    Appearance appearance = null;
    Hashtable tags = new Hashtable();
    int id;

    public SwkShape3D() {
        bG = new NvBranchGroup();
    }

    public SwkShape3D(Primitive primitive, SwkCanvas3D canvas) {
        this.primitive = primitive;
        this.canvas = canvas;
        appearance = canvas.defaultAppearance;
        bG = new NvBranchGroup();
    }

    public SwkShape3D(SwkCanvas3D canvas) {
        this.canvas = canvas;
        appearance = canvas.defaultAppearance;
        bG = new NvBranchGroup();
    }

    public void setCanvas(SwkCanvas3D canvas) {
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

    public void coords(Interp interp, SwkCanvas3D canvas, TclObject[] argv,
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
}
