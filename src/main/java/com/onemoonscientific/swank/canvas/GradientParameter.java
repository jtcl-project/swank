package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;

public class GradientParameter extends CanvasParameter {

    private static String name = "gradient";
    private static TexturePaint defValue = null;
    private GradientPaint newValue = null;
    private Point2D p1 = new Point2D.Double(0, 0);
    private Point2D p2 = new Point2D.Double(0, 0);

    public String getName() {
        return name;
    }

    public String getDefault() {
        return "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-gradient".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }
        GradientPaint gradientPaint = swkShape.fillGradient;
        String result = "";
        if (gradientPaint != null) {
            StringBuilder sBuild = new StringBuilder();
            Point2D pt1 = swkShape.getGradPt1();
            Color color1 = gradientPaint.getColor1();
            Point2D pt2 = swkShape.getGradPt2();
            Color color2 = gradientPaint.getColor2();
            sBuild.append(pt1.getX());
            sBuild.append(" ");
            sBuild.append(pt1.getY());
            sBuild.append(" ");
            String colorName = SwankUtil.parseColor(color1);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('{');
            }
            sBuild.append(colorName);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('}');
            }
            sBuild.append(" ");
            sBuild.append(pt2.getX());
            sBuild.append(" ");
            sBuild.append(pt2.getY());
            sBuild.append(" ");
            colorName = SwankUtil.parseColor(color2);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('{');
            }
            sBuild.append(colorName);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('}');
            }
            result = sBuild.toString();
        }


        return (TclString.newInstance(result));
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        if (arg.toString().equals("")) {
            newValue = null;
        } else {
            newValue = SwankUtil.getGradient(interp, arg, p1, p2);
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape.shape == null) {
            return;
        }

        swkShape.fillGradient = newValue;
        swkShape.setGradPt1(p1);
        swkShape.setGradPt2(p2);
    }
}
