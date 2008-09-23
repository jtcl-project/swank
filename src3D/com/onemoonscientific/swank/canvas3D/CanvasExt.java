/*
 *
 * $Id: CanvasExt.java,v 1.2 2000/05/05 18:14:00 johnsonb Exp $
 *
 * Copyright (c) 2000 Merck & Co., Inc., Whitehouse Station, N.J., USA
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
package com.onemoonscientific.swank.canvas3D;


/*
 * NvExtension.java --
 *
 */
import tcl.lang.*;


/*
 * This class implements a simple Tcl extension package "NvExtension". This
 * extension contains one Tcl command "nvcmd".  See the API documentation of
 * the tcl.lang.Extension class for details.
 */
public class CanvasExt extends Extension {
    /*
     * Create all the commands in the Simple package.
     */
    public void init(Interp interp) {
        interp.createCommand("canvas3D", new SwkCanvasCmd());
    }
}
