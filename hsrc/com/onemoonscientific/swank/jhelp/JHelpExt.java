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
package com.onemoonscientific.swank.jhelp;


/*
 * JHelpExt.java --
 *
 */
import com.onemoonscientific.swank.*;
import tcl.lang.*;


/*
 * This class implements a simple Tcl extension package "JHelpExt". This
 * extension contains one Tcl command "jhelp".  See the API documentation of
 * the tcl.lang.Extension class for details.
 */
/*
 * This class implements a simple Tcl extension package "JHelpExt". This
 * extension contains one Tcl command "jhelp".  See the API documentation of
 * the tcl.lang.Extension class for details.
 */
public class JHelpExt extends Extension {
    /*
     * Create all the commands in the Simple package.
     */

    public void init(Interp interp) {
        Extension.loadOnDemand(interp, "jhelp",
            "com.onemoonscientific.swank.jhelp.SwkJHelpCmd");
    }
}
