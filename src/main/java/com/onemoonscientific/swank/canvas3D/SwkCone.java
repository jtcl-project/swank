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
package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;

import com.sun.j3d.utils.geometry.*;

import tcl.lang.*;

import java.awt.*;
import java.awt.geom.*;


import java.util.*;

import javax.media.j3d.*;

import javax.vecmath.*;


public class SwkCone extends SwkShape implements RadiusInterface {
    int xDivisions = 15;
    float radius = 1.0f;
    Point3d a = new Point3d(0.0, 0.0, 0.0);
    Point3d b = new Point3d(0.0, 1.0, 0.0);
    static CanvasParameter[] parameters = {
        new RadiusParameter(),
    };
   static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }


    SwkCone(SwkImageCanvas canvas) {
        super(canvas);
    }

    /**
     *
     * @param canvas
     * @param coords
     * @throws SwkException
     */
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        a.x = coords[0];
        a.y = coords[1];
        a.z = coords[2];
        b.x = coords[3];
        b.y = coords[4];
        b.z = coords[5];
        genShape();
    }


    void makeObjectNode() {
        float length = (float) a.distance(b);
        objectNode = new Cone(radius, length, Primitive.GENERATE_NORMALS, xDivisions, 1, swkAppearance.appearance);
   }
    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = (float) radius;
    }


    public void config(Interp interp, TclObject[] argv, int start)
        throws TclException {
        if (((argv.length - start) % 2) != 0) {
            throw new TclNumArgsException(interp, 0, argv,
                "-option value ? -option value? ...");
        }

        boolean doGen = false;

        for (int i = start; i < argv.length; i += 2) {
            if ("-radius".startsWith(argv[i].toString())) {
                radius = (float) TclDouble.get(interp, argv[i + 1]);
                doGen = true;
            } else if ("-xdivisions".startsWith(argv[i].toString())) {
                xDivisions = TclInteger.get(interp, argv[i + 1]);
                doGen = true;
            } else if (argv[i].toString().startsWith("-tag")) {
               // fixme canvas.setTags(interp, argv[i + 1], (SwkShape) this);
            }
            if (doGen) {
                 makeObjectNode();
            }

        }
    }

    public TclObject itemGet(Interp interp, TclObject argv)
        throws TclException {
        /* FIXME
         Arc2D arc2D = (Arc2D) shape;

        if (argv.toString().equals("-extent")) {
            return (TclDouble.newInstance(arc2D.getAngleExtent()));
        } else if (argv.toString().equals("-start")) {
            return (TclDouble.newInstance(arc2D.getAngleStart()));
        } else if (argv.toString().equals("-style")) {
            switch (arc2D.getArcType()) {
            case Arc2D.PIE:
                return (TclString.newInstance("pie"));

            case Arc2D.CHORD:
                return (TclString.newInstance("chord"));

            case Arc2D.OPEN:
                return (TclString.newInstance("arc"));
            }
        } else if (argv.toString().equals("-fill")) {
            return (TclString.newInstance(SwankUtil.parseColor(interp, fill)));
        } else if (argv.toString().equals("-outline")) {
            // return(SwankUtil.parseColor(interp,outline));
        } else if (argv.toString().equals("-texture")) {
            //return(SwankUtil.parseImageIcon(interp,image));
        } else if (argv.toString().startsWith("-tag")) {
            return (canvas.getTags(interp, (SwkShape) this));
        }
         */
        return (TclString.newInstance(""));
    }
//

// Code for following transform from ConeCreateor.java @ http://teresi.us/html/main/programming.html
//        Scott Teresi, www.teresi.us


    NvTransformGroup makeTransform() {
        Vector3d base = new Vector3d();
        base.x = b.x;
        base.y = b.y;
        base.z = b.z;

        Vector3d apex = new Vector3d();
        apex.x = a.x;
        apex.y = a.y;
        apex.z = a.z;

        // calculate center of object
        Vector3d center = new Vector3d();
        center.x = ((apex.x - base.x) / 2.0) + base.x;
        center.y = ((apex.y - base.y) / 2.0) + base.y;
        center.z = ((apex.z - base.z) / 2.0) + base.z;

        // calculate height of object and unit vector along cylinder axis
        Vector3d unit = new Vector3d();
        unit.sub(apex, base); // unit = apex - base;

        double height = unit.length();
        unit.normalize();

        /* A Java3D cylinder is created lying on the Y axis by default.
           The idea here is to take the desired cylinder's orientation
           and perform a tranformation on it to get it ONTO the Y axis.
           Then this transformation matrix is inverted and used on a
           newly-instantiated Java 3D cylinder. */

        // calculate vectors for rotation matrix
        // rotate object in any orientation, onto Y axis (exception handled below)
        // (see page 418 of _Computer Graphics_ by Hearn and Baker)
        Vector3d uX = new Vector3d();
        Vector3d uY = new Vector3d();
        Vector3d uZ = new Vector3d();
        double magX;
        Transform3D rotateFix = new Transform3D();

        uY = new Vector3d(unit);
        uX.cross(unit, new Vector3d(0, 0, 1));
        magX = uX.length();

        // magX == 0 if object's axis is parallel to Z axis
        if (magX != 0) {
            uX.z = uX.z / magX;
            uX.x = uX.x / magX;
            uX.y = uX.y / magX;
            uZ.cross(uX, uY);
        } else {
            // formula doesn't work if object's axis is parallel to Z axis
            // so rotate object onto X axis first, then back to Y at end
            double magZ;

            // (switched z -> y, y -> x, x -> z from code above)
            uX = new Vector3d(unit);
            uZ.cross(unit, new Vector3d(0, 1, 0));
            magZ = uZ.length();
            uZ.x = uZ.x / magZ;
            uZ.y = uZ.y / magZ;
            uZ.z = uZ.z / magZ;
            uY.cross(uZ, uX);

            // rotate object 90 degrees CCW around Z axis--from X onto Y
            rotateFix.rotZ(Math.PI / 2.0);
        }

        // create the rotation matrix
        Transform3D transMatrix = new Transform3D();
        Transform3D rotateMatrix = new Transform3D(new Matrix4d(uX.x, uX.y,
                    uX.z, 0, uY.x, uY.y, uY.z, 0, uZ.x, uZ.y, uZ.z, 0, 0, 0, 0,
                    1));

        // invert the matrix; need to rotate it off of the Z axis
        rotateMatrix.invert();

        // rotate the cylinder into correct orientation
        transMatrix.mul(rotateMatrix);
        transMatrix.mul(rotateFix);

        // translate the cylinder away
        transMatrix.setTranslation(center);

        // create the transform group
        NvTransformGroup tg = new NvTransformGroup(transMatrix);

        return tg;
    }
}
