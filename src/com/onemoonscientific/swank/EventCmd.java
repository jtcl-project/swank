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

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/** This class implements the Jacl event command.
 * @author Bruce A. Johnson
 * @version %I%, %G%
 */
public class EventCmd implements Command {
    public static Map virtualEvents = new LinkedHashMap();

    /** Method called to process the bind command.
     * @param interp The interpreter in which this command is active.
     * @param argv Array of TclObjects containing arguments to the bind command.
     * @throws TclException .
     */
    static final private String[] validCmds = {
        "add", "delete", "generate", "info",
    };
    static final private int OPT_ADD = 0;
    static final private int OPT_DELETE = 1;
    static final private int OPT_GENERATE = 2;
    static final private int OPT_INFO = 3;

    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        switch (opt) {
        case OPT_ADD: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 2, argv,
                    "<<virtual>> sequence ?sequence ...?");
            }

            String virtual = argv[2].toString();

            if (!checkVirtual(virtual)) {
                throw new TclException(interp,
                    "virtual event \"" + virtual + "\" badly formed");
            }

            Vector sequenceVec = (Vector) virtualEvents.get(virtual);

            if (sequenceVec == null) {
                sequenceVec = new Vector(2);
                virtualEvents.put(virtual, sequenceVec);
            }

            for (int i = 3; i < argv.length; i++) {
                SwkBinding binding = SwkBind.getBinding(interp, argv, i);
                sequenceVec.add(binding);
            }

            break;
        }

        case OPT_DELETE:
            System.out.println("event delete not implemented yet");

            break;

        case OPT_GENERATE:
            System.out.println("event generate not implemented yet");

            break;

        case OPT_INFO: {
            if ((argv.length != 2) && (argv.length != 3)) {
                throw new TclNumArgsException(interp, 2, argv, "?<<virtual>>?");
            }

            if (argv.length == 2) {
                Iterator iter = virtualEvents.keySet().iterator();
                TclObject list = TclList.newInstance();

                while (iter.hasNext()) {
                    TclList.append(interp, list,
                        TclString.newInstance((String) iter.next()));
                }

                interp.setResult(list);
            } else {
                String virtual = argv[2].toString();

                if (!checkVirtual(virtual)) {
                    throw new TclException(interp,
                        "virtual event \"" + virtual + "\" badly formed");
                }

                Vector sequenceVec = (Vector) virtualEvents.get(virtual);

                if (sequenceVec != null) {
                    TclObject list = TclList.newInstance();

                    for (int i = 0; i < sequenceVec.size(); i++) {
                        SwkBinding binding = (SwkBinding) sequenceVec.elementAt(i);
                        TclList.append(interp, list,
                            TclString.newInstance(binding.toString()));
                    }

                    interp.setResult(list);
                }
            }
        }

        break;

        default:}
    }

    public static boolean checkVirtual(String virtual) {
        return virtual.matches("^<<[^<>]+>>$");
    }

    public static Vector getVirtualEvents(String name) {
        Vector sequenceVec = (Vector) virtualEvents.get(name);

        return sequenceVec;
    }
}
