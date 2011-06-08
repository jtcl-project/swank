/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */
/*
 * HitShape.java
 *
 * Created on February 19, 2000, 3:14 PM
 */
/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.canvas.HittableShape;

/**
 *
 * @author brucejohnson
 */
public class HitShape implements HittableShape {

    final int handle;
    final SwkShape swkShape;

    HitShape(SwkShape shape, int handle) {
        this.swkShape = shape;
        this.handle = handle;
    }

    HitShape(SwkShape shape) {
        this.swkShape = shape;
        this.handle = -1;
    }

    /**
     *
     * @return
     */
    public SwkShape getShape() {
        return swkShape;
    }

    /**
     *
     * @return
     */
    public int getHandle() {
        return handle;
    }
    public boolean hasShape() {
        return swkShape != null;
    }

    public int getId() {
        if (swkShape == null) {
            return -1;
        } else {
            return swkShape.getId();
        }
    }
}
