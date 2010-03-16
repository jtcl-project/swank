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


public class SwkSphere extends SwkShape {
    int nDivisions = 15;
    float radius = 1.0f;
    Point3d a = new Point3d();

    SwkSphere(SwkImageCanvas canvas) {
        super(canvas);
    }

    public void coords(Interp interp) throws TclException {
        TclObject list = TclList.newInstance();

        /*
        double x1 = ((Arc2D) shape).getX();
        double y1 = ((Arc2D) shape).getY();
        double width = ((Arc2D) shape).getWidth();
        double height = ((Arc2D) shape).getHeight();
        TclList.append(interp, list, TclDouble.newInstance(x1));
        TclList.append(interp, list, TclDouble.newInstance(y1));
        TclList.append(interp, list, TclDouble.newInstance(x1 + width));
        TclList.append(interp, list, TclDouble.newInstance(y1 + height));
         */
        interp.setResult(list);
    }

    public void coords(Interp interp, SwkImageCanvas canvas, TclObject[] argv,
        int start) throws TclException {
        a.x = TclDouble.get(interp, argv[start]);
        a.y = TclDouble.get(interp, argv[start + 1]);
        a.z = TclDouble.get(interp, argv[start + 2]);
    }
    void makePrimitive() {
        primitive = new Sphere(radius, Primitive.GENERATE_NORMALS, nDivisions,appearance);
    }
    void genShape() {
        Transform3D t3D = new Transform3D();
        t3D.setTranslation(new Vector3d(a.x, a.y, a.z));

        TransformGroup tG = new TransformGroup(t3D);
        makePrimitive();
        tG.addChild(primitive);
        bG.removeAllChildren();
        bG.addChild(tG);
        bG.setCapability(NvBranchGroup.ALLOW_DETACH);
        bG.compile();
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
                makePrimitive();
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
