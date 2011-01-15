/*

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
package com.onemoonscientific.swank.canvas;


import tcl.lang.*;
import java.awt.*;


import javax.swing.*;

public class SwkCmd implements Command {

    String errorString = null;

    /**
     * @param interp
     * @param argv
     * @throws TclException  */
    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        errorString = null;

        if (!EventQueue.isDispatchThread()) {
            WidgetCmd widgetCmd = new WidgetCmd(interp, argv);

            try {
                SwingUtilities.invokeAndWait(widgetCmd);
            } catch (InterruptedException iE) {
                throw new TclException(interp, iE.getMessage());
            } catch (Exception e) {
                throw new TclException(interp, e.getMessage());
            }

            if (errorString != null) {
                throw new TclException(interp, errorString);
            }
        } else {
            cmdProcET(interp, argv);
        }
    }

    void cmdProcET(Interp interp, TclObject[] argv) throws TclException {
    }

    private class WidgetCmd implements Runnable {

        Interp interp;
        TclObject[] argv = null;

        WidgetCmd(Interp interp, TclObject[] argv) {
            this.interp = interp;
            this.argv = new TclObject[argv.length];

            for (int i = 0; i < argv.length; i++) {
                argv[i].preserve();
                this.argv[i] = argv[i];
            }
        }

        public void run() {
            try {
                cmdProcET(interp, argv);
            } catch (Exception e) {
                errorString = e.getMessage();
            } finally {
                for (int i = 0; i < argv.length; i++) {
                    argv[i].release();
                }
            }
        }
    }
}
