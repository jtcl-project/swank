package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.util.ArrayList;


public class TagsParameter extends CanvasParameter {
    private static String name = "tags";
    private static String defValue = "";
    private String[] newValue = null;

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
     * @param s
     * @return
     */
    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-tags".startsWith(s)) {
            return true;
        } else {
            return false;
        }
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

        ArrayList aList = swkShape.canvas.getTags(swkShape);

        return (SwankUtil.arrayToList(interp, aList));
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        TclObject[] tagList = TclList.getElements(interp, arg);
        newValue = SwankUtil.argvToStrings(tagList, 0);
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if (swkShape != null) {
            swkShape.setTags(newValue);

            try {
                swkCanvas.setTags(swkShape.tagNames, swkShape);
            } catch (SwkException swkE) {
            }
        }
    }
}
