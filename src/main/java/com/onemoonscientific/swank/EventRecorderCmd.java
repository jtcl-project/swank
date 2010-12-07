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
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

import java.awt.*;

import java.util.*;

import javax.swing.*;

public class EventRecorderCmd implements Command {

    static final private int OPT_COUNT = 0;
    static final private int OPT_GET = 1;
    static final private int OPT_LIST = 2;
    static final private int OPT_START = 3;
    static final private int OPT_STOP = 4;
    static final private String[] validCmds = {
        "count", "get", "list", "start", "stop"
    };

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        switch (opt) {
            case OPT_COUNT: {
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 1, argv, "");
                }

                interp.setResult(EventRecorder.eventCount());

                break;
            }

            case OPT_GET: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 1, argv, "");
                }

                int index = TclInteger.get(interp, argv[2]);

                if (index >= EventRecorder.eventCount()) {
                    throw new TclException(interp,
                            "event \"" + index + "\" doesn't exist");
                }

                interp.setResult(EventRecorder.get(index));

                break;
            }

            case OPT_LIST: {
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 1, argv, "");
                }

                EventRecorder.get(interp);

                break;
            }

            case OPT_START: {
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 1, argv, "");
                }

                EventRecorder.start();

                break;
            }

            case OPT_STOP: {
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 1, argv, "");
                }

                EventRecorder.stop();

                break;
            }
        }
    }
}
