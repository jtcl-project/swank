package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;


public class TextParameter extends CanvasParameter {
    private static String name = "text";
    private static String defValue = "";
    private String newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-text".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException {
// fixme switch to using a TextInterface
// fixme shouldn't read shape on Tcl thread
        if ((swkShape != null) && ((swkShape instanceof SwkCanvText))) {
            if (((SwkCanvText) swkShape).getText() == null) {
                ((SwkCanvText) swkShape).setText("");
            }

            return TclString.newInstance(((SwkCanvText) swkShape).getText());
        } else if ((swkShape != null) && ((swkShape instanceof SwkCanvasHText))) {
            if (((SwkCanvasHText) swkShape).getText() == null) {
                ((SwkCanvasHText) swkShape).setText("");
            }
            return TclString.newInstance(((SwkCanvasHText) swkShape).getText());
        } else {
            return TclString.newInstance("");
        }
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        if (arg == null) {
            newValue = "";
        } else {
            newValue = new String(arg.toString());
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && ((swkShape instanceof SwkCanvText))) {
            ((SwkCanvText) swkShape).setText(newValue);
        } else if ((swkShape != null) && ((swkShape instanceof SwkCanvasHText))) {
            ((SwkCanvasHText) swkShape).setText(newValue);
        }
    }
}
