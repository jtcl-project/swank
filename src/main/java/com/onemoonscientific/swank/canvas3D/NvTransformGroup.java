package com.onemoonscientific.swank.canvas3D;

import java.util.*;
import javax.media.j3d.*;



/**
 *
 * @author brucejohnson
 */
public class NvTransformGroup extends TransformGroup {
    SwkShape swkShape;
    int index = -1;
    public NvTransformGroup(javax.media.j3d.Transform3D t3D) {
         super(t3D);
    }
    public SwkShape getShape() {
        return swkShape;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public void setShape(SwkShape swkShape) {
        this.swkShape = swkShape;
    }
}
