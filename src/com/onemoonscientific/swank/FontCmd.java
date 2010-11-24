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

import java.io.*;

import java.util.*;


public class FontCmd implements Command {
    static final private String[] validCmds = {
        "actual", "configure", "create", "delete", "families", "measure",
        "metrics", "names",
    };
    static final private int OPT_ACTUAL = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_CREATE = 2;
    static final private int OPT_DELETE = 3;
    static final private int OPT_FAMILIES = 4;
    static final private int OPT_MEASURE = 5;
    static final private int OPT_METRICS = 6;
    static final private int OPT_NAMES = 7;

    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        switch (opt) {
        case OPT_ACTUAL: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            break;
        }

        case OPT_CONFIGURE: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            break;
        }

        case OPT_CREATE: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            break;
        }

        case OPT_DELETE: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            break;
        }

        case OPT_FAMILIES: {
            String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                       .getAvailableFontFamilyNames();
            TclObject list = TclList.newInstance();

            for (int i = 0; i < fontFamilies.length; i++) {
                TclList.append(interp, list,
                    TclString.newInstance(fontFamilies[i]));
            }

            interp.setResult(list);

            break;
        }

        case OPT_MEASURE: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            Font font = SwankUtil.getFont(interp, argv[2]);

            if (font == null) {
                throw new TclException(interp,
                    "unknown font \"" + argv[2].toString() + "\"");
            }

            Component comp = null;

            if (argv.length > 3) {
                if ("-displayof".startsWith(argv[3].toString())) {
                    if (argv.length == 4) {
                        throw new TclException(interp,
                            "-value for \"-displayof\" missing");
                    } else {
                        comp = (Component) Widgets.get(interp,
                                argv[4].toString());
                    }
                }
            }

            if (comp == null) {
                comp = (Component) Widgets.get(interp, "any");
            }

            if (comp == null) {
                throw new TclException(interp,
                    "can't get component for font command");
            }

            FontMetrics fontMetrics = comp.getFontMetrics(font);
            int width = fontMetrics.stringWidth(argv[3].toString());
            interp.setResult(width);

            break;
        }

        case OPT_METRICS: {
            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            int start = 3;
            Component comp = null;
            Font font = SwankUtil.getFont(interp, argv[2]);

            if (font == null) {
                throw new TclException(interp,
                    "unknown font \"" + argv[2].toString() + "\"");
            }

            if (argv.length > 3) {
                if ("-displayof".startsWith(argv[3].toString())) {
                    if (argv.length == 4) {
                        throw new TclException(interp,
                            "-value for \"-displayof\" missing");
                    } else {
                        start = 5;
                        comp = (Component) Widgets.get(interp,
                                argv[4].toString());
                    }
                }
            }

            if (comp == null) {
                comp = (Component) Widgets.get(interp, "any");
            }

            if (comp == null) {
                throw new TclException(interp,
                    "can't get component for font command");
            }

            // FontMetrics fontMetrics = comp.getFontMetrics(font);
            FontMetrics fontMetrics = (new Add()).exec(comp, font);

            boolean getAscent = true;
            boolean getDescent = true;
            boolean getLinespace = true;
            boolean getFixed = true;
            boolean singleMode = false;

            for (int i = start; i < argv.length; i++) {
                if (i == start) {
                    getAscent = getDescent = getLinespace = getFixed = false;
                    singleMode = true;
                }

                if ("-ascent".startsWith(argv[i].toString())) {
                    getAscent = true;
                }

                if ("-descent".startsWith(argv[i].toString())) {
                    getDescent = true;
                }

                if ("-linespace".startsWith(argv[i].toString())) {
                    getLinespace = true;
                }

                if ("-fixed".startsWith(argv[i].toString())) {
                    getFixed = true;
                }
            }

            TclObject list = TclList.newInstance();

            if (getAscent) {
                if (!singleMode) {
                    TclList.append(interp, list,
                        TclString.newInstance("-ascent"));
                }

                TclList.append(interp, list,
                    TclInteger.newInstance(fontMetrics.getAscent()));
            }

            if (getDescent) {
                if (!singleMode) {
                    TclList.append(interp, list,
                        TclString.newInstance("-descent"));
                }

                TclList.append(interp, list,
                    TclInteger.newInstance(fontMetrics.getDescent()));
            }

            if (getLinespace) {
                if (!singleMode) {
                    TclList.append(interp, list,
                        TclString.newInstance("-linespace"));
                }

                TclList.append(interp, list,
                    TclInteger.newInstance(fontMetrics.getHeight()));
            }

            if (getFixed) {
                if (!singleMode) {
                    TclList.append(interp, list, TclString.newInstance("-fixed"));
                }

                TclList.append(interp, list,
                    TclBoolean.newInstance(
                        fontMetrics.charWidth('m') == fontMetrics.charWidth('i')));
            }

            interp.setResult(list);

            break;
        }

        case OPT_NAMES: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            break;
        }
        }
    }

    class Add extends GetValueOnEventThread {
        Component comp = null;
        Font font = null;
        FontMetrics fontMetrics = null;

        FontMetrics exec(final Component comp, final Font font) {
            this.comp = comp;
            this.font = font;
            execOnThread();

            return fontMetrics;
        }

        public void run() {
            fontMetrics = comp.getFontMetrics(font);
        }
    }
}
