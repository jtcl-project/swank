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

import java.lang.*;

import java.util.*;

import javax.media.j3d.*;

import javax.vecmath.*;


public class SwkSphere extends SwkShape implements RadiusInterface {
    int nDivisions = 15;
    float radius = 1.0f;
    Point3d a = new Point3d();
    static CanvasParameter[] parameters = {
        new RadiusParameter(),
    };
   static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }



    SwkSphere(SwkImageCanvas canvas) {
        super(canvas);
    }

    public CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }




    @Override
   public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        a.x = coords[0];
        a.y = coords[1];
        a.z = coords[2];
        genShape();
    }
    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = (float) radius;
    }
    void makeObjectNode() {
        objectNode = new Sphere(radius, Primitive.GENERATE_NORMALS, nDivisions,appearance);
    }
    TransformGroup makeTransform() {
        Transform3D t3D = new Transform3D();
        t3D.setTranslation(new Vector3d(a.x, a.y, a.z));
        TransformGroup tG = new TransformGroup(t3D);
        return tG;
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
            } else if ("-ndivisions".startsWith(argv[i].toString())) {
                nDivisions = TclInteger.get(interp, argv[i + 1]);
                doGen = true;
            } else if (argv[i].toString().startsWith("-tag")) {
              // fixme  canvas.setTags(interp, argv[i + 1], (SwkShape) this);
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
}
