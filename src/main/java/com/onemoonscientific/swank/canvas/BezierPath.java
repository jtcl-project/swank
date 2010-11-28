/*
 *--------------------------------------------------------------
 *
 * TkMakeBezierCurve --
 *
 *        Given a set of points, create a new set of points that fit
 *        parabolic splines to the line segments connecting the original
 *        points.  Produces output points in either of two forms.
 *
 *        Note: in spite of this procedure's name, it does *not* generate
 *        Bezier curves.  Since only three control points are used for
 *        each curve segment, not four, the curves are actually just
 *        parabolic.
 *
 * Results:
 *        Either or both of the xPoints or dblPoints arrays are filled
 *        in.  The return value is the number of points placed in the
 *        arrays.  Note:  if the first and last points are the same, then
 *        a closed curve is generated.
 *
 * Side effects:
 *        None.
 *
 *--------------------------------------------------------------
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;

import java.lang.*;

import java.util.*;


//Algorithm from http://www.antigrain.com/research/bezier_interpolation/
class BezierPath {
    public static void makeBezierCurve(double[] coords, int numSteps,
        GeneralPath gPath, double smoothValue) {
        // Assume we need to calculate the control
        // points between (x1,y1) and (x2,y2).
        // Then x0,y0 - the previous vertex,
        //      x3,y3 - the next one.
        boolean closed = false;
        int numCoords = coords.length;

        if ((coords[0] == coords[numCoords - 2]) &&
                (coords[1] == coords[numCoords - 1])) {
            closed = true;
        }

        gPath.moveTo((float) coords[0], (float) coords[1]);

        double x0;
        double y0;
        double x1;
        double y1;
        double x2;
        double y2;
        double x3;
        double y3;

        for (int k = 0; k < (numCoords - 2); k += 2) {
            if (k > 0) {
                x0 = coords[k - 2];
                y0 = coords[k - 1];
            } else if (closed) {
                x0 = coords[numCoords - 4];
                y0 = coords[numCoords - 3];
            } else {
                x0 = coords[k];
                y0 = coords[k + 1];
            }

            x1 = coords[k];
            y1 = coords[k + 1];
            x2 = coords[k + 2];
            y2 = coords[k + 3];

            if (k < (numCoords - 4)) {
                x3 = coords[k + 4];
                y3 = coords[k + 5];
            } else if (closed) {
                x3 = coords[2];
                y3 = coords[3];
            } else {
                x3 = coords[k + 2];
                y3 = coords[k + 3];
            }

            double xc1 = (x0 + x1) / 2.0;
            double yc1 = (y0 + y1) / 2.0;
            double xc2 = (x1 + x2) / 2.0;
            double yc2 = (y1 + y2) / 2.0;
            double xc3 = (x2 + x3) / 2.0;
            double yc3 = (y2 + y3) / 2.0;

            double len1 = Math.sqrt(((x1 - x0) * (x1 - x0)) +
                    ((y1 - y0) * (y1 - y0)));
            double len2 = Math.sqrt(((x2 - x1) * (x2 - x1)) +
                    ((y2 - y1) * (y2 - y1)));
            double len3 = Math.sqrt(((x3 - x2) * (x3 - x2)) +
                    ((y3 - y2) * (y3 - y2)));

            double k1 = len1 / (len1 + len2);
            double k2 = len2 / (len2 + len3);

            double xm1 = xc1 + ((xc2 - xc1) * k1);
            double ym1 = yc1 + ((yc2 - yc1) * k1);

            double xm2 = xc2 + ((xc3 - xc2) * k2);
            double ym2 = yc2 + ((yc3 - yc2) * k2);

            // Resulting control points. Here smoothValue is mentioned
            // above coefficient K whose value should be in range [0...1].
            float ctrl1_x = (float) ((xm1 + ((xc2 - xm1) * smoothValue) + x1) -
                xm1);
            float ctrl1_y = (float) ((ym1 + ((yc2 - ym1) * smoothValue) + y1) -
                ym1);

            float ctrl2_x = (float) ((xm2 + ((xc2 - xm2) * smoothValue) + x2) -
                xm2);
            float ctrl2_y = (float) ((ym2 + ((yc2 - ym2) * smoothValue) + y2) -
                ym2);
            gPath.curveTo(ctrl1_x, ctrl1_y, ctrl2_x, ctrl2_y, (float) x2,
                (float) y2);
        }
    }
}
