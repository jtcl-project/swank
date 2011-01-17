package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;

/**
 *
 * @author brucejohnson
 */
public class ArcStyleParameter extends CanvasParameter {

    private static String name = "style";
    private static String defValue = "arc";
    private int newValue = Arc2D.OPEN;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getDefault() {
        return defValue;
    }


    /**
     *
     * @param interp
     * @param swkShape
     * @return
     * @throws TclException
     */
    public TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException {
        if (swkShape == null) {
            throw new TclException(interp, "shape doesn't exist");
        }

        if (swkShape instanceof ItemArc) {
            Arc2D arc2D = ((ItemArc) swkShape).arc2D;

            String arcType = null;

            switch (arc2D.getArcType()) {
                case Arc2D.PIE:
                    arcType = "pie";

                    break;

                case Arc2D.CHORD:
                    arcType = "chord";

                    break;

                case Arc2D.OPEN:
                    arcType = "arc";

                    break;
            }

            return TclString.newInstance(arcType);
        } else {
            throw new TclException(interp, "shape not arc");
        }
    }

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param arg
     * @throws TclException
     */
    @Override
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
        if (arg.toString().startsWith("pie")) {
            newValue = Arc2D.PIE;
        } else if (arg.toString().startsWith("cho")) {
            newValue = Arc2D.CHORD;
        } else if (arg.toString().equals("arc")) {
            newValue = Arc2D.OPEN;
        }
    }

    /**
     *
     * @param swkCanvas
     * @param swkShape
     */
    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape == null) {
            return;
        }

        if (swkShape instanceof ItemArc) {
            Arc2D arc2D = ((ItemArc) swkShape).arc2D;
            arc2D.setArcType(newValue);
        }
    }
}
