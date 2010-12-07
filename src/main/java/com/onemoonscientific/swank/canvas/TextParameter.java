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
        if ((swkShape != null) && ((swkShape instanceof TextInterface))) {
            if (((TextInterface) swkShape).getText() == null) {
                ((TextInterface) swkShape).setText("");
            }

            return TclString.newInstance(((TextInterface) swkShape).getText());
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
        if ((swkShape != null) && ((swkShape instanceof TextInterface))) {
            ((TextInterface) swkShape).setText(newValue);
        }
    }
}
