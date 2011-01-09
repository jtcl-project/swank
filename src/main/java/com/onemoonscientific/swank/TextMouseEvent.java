package com.onemoonscientific.swank;

import tcl.lang.*;
import java.awt.event.*;
import java.util.*;

class TextMouseEvent extends TclEvent {

    SwkJTextPane swkJTextPane = null;
    MouseEvent event = null;
    Vector bindings = null;
    int type = 0;
    int subtype = 0;

    TextMouseEvent(Interp interp, SwkJTextPane swkJTextPane, MouseEvent event,
            Vector bindings, int type, int subtype) {
        this.swkJTextPane = swkJTextPane;
        this.event = event;
        this.bindings = bindings;
        this.type = type;
        this.subtype = subtype;
    }

    public int processEvent(int flags) {
        swkJTextPane.processMouseEvent(event, bindings, type, subtype);

        return 1;
    }
}
