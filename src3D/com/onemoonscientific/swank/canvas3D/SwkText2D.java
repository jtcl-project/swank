package com.onemoonscientific.swank.canvas3D;


import com.sun.j3d.utils.geometry.*;

import tcl.lang.*;


import java.lang.*;

import javax.media.j3d.*;

import javax.vecmath.*;
import com.onemoonscientific.swank.*;


public class SwkText2D extends SwkShape {
    String text = "duck";
    Point3d a = new Point3d();

    SwkText2D(SwkImageCanvas canvas) {
        super(canvas);
    }

   public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        a.x = coords[0];
        a.y = coords[1];
        a.z = coords[2];
        genShape();
    }
    void genShape() {
        Transform3D t3D = new Transform3D();
        t3D.setTranslation(new Vector3d(a.x, a.y, a.z));
        TransformGroup tG = new TransformGroup(t3D);
        System.out.println("create text2d "+a.x+" "+a.y+" "+a.z);
        Shape3D shape = new Text2D(text, new Color3f(0.1f, 0.95f, 0.1f), "SansSerif",
                540, 0);
        System.out.println("created text2d " + shape.toString());
        tG.addChild(shape);
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
            if ("-text".startsWith(argv[i].toString())) {
                text = new String(argv[i + 1].toString());
                doGen = true;
            } else if (argv[i].toString().startsWith("-tag")) {
             // fixme   canvas.setTags(interp, argv[i + 1], (SwkShape) this);
            }
            doGen = true;
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
