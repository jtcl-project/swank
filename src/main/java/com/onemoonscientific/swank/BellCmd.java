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
import java.awt.*;

/**
 *
 * @author brucejohnson
 */
public class BellCmd implements Command {

    /**
     * 
     * @param interp
     * @param argv
     * @throws TclException
     */
    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;
        TclObject tObj;

        if ((argv.length != 1) && (argv.length != 3)) {
            throw new TclNumArgsException(interp, 1, argv, "?-displayof window?");
        }

        final Object object;

        if (argv.length == 3) {
            if (argv[1].toString().equals("-displayof")) {
                tObj = (TclObject) Widgets.getWidget(interp, argv[2].toString());

                if (tObj == null) {
                    throw new TclException(interp,
                            "bad window path name \"" + argv[2] + "\"");
                }

                object = ReflectObject.get(interp, tObj);
            } else {
                throw new TclException(interp,
                        "bad option \"" + argv[1] + "\": must be -displayof");
            }
        } else {
            object = null;
        }

        (new UpdateOnEventThread() {

            @Override
            public void run() {
                beep(object);
            }
        }).execOnThread();
    }

    /**
     *
     * @param object
     */
    protected static void beep(Object object) {
        if ((object == null) || !(object instanceof Component)) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            ((Component) object).getToolkit().beep();
        }
    }
}
