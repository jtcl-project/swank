package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;


public class ArcStyleParameter extends CanvasParameter {
    private static String name = "style";
    private static String defValue = "arc";
    private int newValue = Arc2D.OPEN;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-style".startsWith(s)) {
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
