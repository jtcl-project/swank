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
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;
import java.util.*;


/** This class implements the Jacl bind command.
 * @author Bruce A. Johnson
 * @version %I%, %G%
 */
public class BindTagsCmd implements Command {
    // This Hashtable stores class level focus bindings.

    public static Hashtable focusTable = new Hashtable();
    // This Hashtable stores class level configure bindings.
    public static Hashtable configureTable = new Hashtable();
    // This Hashtable stores class level key bindings.
    public static Hashtable keyTable = new Hashtable();
    // This Hashtable stores class level mouse bindings.
    public static Hashtable mouseTable = new Hashtable();
    // This Hashtable stores class level mousemotion bindings.
    public static Hashtable mouseMotionTable = new Hashtable();

    /** Method called to process the bind command.
     * @param interp The interpreter in which this command is active.
     * @param argv Array of TclObjects containing arguments to the bind command.
     * @throws TclException .
     */
    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;
        SwkWidget swkWidget = null;
        Vector bindingVector = null;

        if ((argv.length != 2) && (argv.length != 3)) {
            throw new TclNumArgsException(interp, 1, argv, "widget ?tagList?");
        }

        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[1].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[1].toString() + "\"");
        }

        swkWidget = (SwkWidget) ReflectObject.get(interp, tObj);

        if (swkWidget == null) {
            throw new TclException(interp,
                    "Can't find widget " + tObj.toString());
        }

        if (argv.length == 2) {
            TclObject list = TclList.newInstance();
            Vector tagList = swkWidget.getTagList();

            for (i = 0; i < tagList.size(); i++) {
                TclList.append(interp, list,
                        TclString.newInstance((String) tagList.elementAt(i)));
            }

            interp.setResult(list);
        } else {
            swkWidget.setTagList(interp, argv[2]);
        }
    }
}
