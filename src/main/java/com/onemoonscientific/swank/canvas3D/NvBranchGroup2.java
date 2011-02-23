package com.onemoonscientific.swank.canvas3D;

import java.util.*;
import javax.media.j3d.*;



/**
 *
 * @author brucejohnson
 */
public class NvBranchGroup2 extends BranchGroup {
    final NvBranchGroup parent;
    NvBranchGroup2(NvBranchGroup parent) {
         this.parent = parent;
         setCapability(ALLOW_CHILDREN_READ);
         setCapability(ALLOW_CHILDREN_WRITE);
    }
    public NvBranchGroup getNvParent() {
         return parent;
    }
}
