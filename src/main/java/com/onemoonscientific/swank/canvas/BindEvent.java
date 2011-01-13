package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.util.*;

class BindEvent extends TclEvent {

    Interp interp = null;
    SwkCanvas swkCanvas = null;
    EventObject event = null;
    int type = 0;
    int subtype = 0;
    String currentTag = null;
    String previousTag = null;
    HitShape currentShape = null;

    BindEvent(Interp interp, SwkCanvas swkCanvas, EventObject event, int type,
            int subtype, String currentTag, String previousTag,
            HitShape currentShape) {
        this.interp = interp;
        this.swkCanvas = swkCanvas;
        this.event = event;
        this.type = type;
        this.subtype = subtype;

        if (currentTag != null) {
            this.currentTag = currentTag;
        }

        if (previousTag != null) {
            this.previousTag = new String(previousTag);
        }

        if (currentShape != null) {
            this.currentShape = currentShape;
        }
    }

    public int processEvent(int flags) {
        if ((interp != null) && (swkCanvas != null)) {
            swkCanvas.processEvent(event, type, subtype, currentTag,
                    previousTag, currentShape);
        }

        return 1;
    }
}
