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


class SwkJColorChooserWidgetCmd implements Command {

    static final private String[] validCmds = {
        "configure", "cget", "choose"
    };
    static final private int OPT_CONFIGURE = 0;
    static final private int OPT_CGET = 1;
    static final private int OPT_CHOOSE = 2;
    static boolean gotDefaults = false;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJColorChooser swkjcolorchooser = (SwkJColorChooser) ReflectObject.get(interp,
                tObj);

        switch (opt) {

            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjcolorchooser.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjcolorchooser.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjcolorchooser.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjcolorchooser.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJColorChooser.resourceDB.get(argv[2].toString());

                    if (ro == null) {
                        throw new TclException(interp,
                                "unknown option \"" + argv[2].toString() + "\"");
                    }

                    TclObject list = TclList.newInstance();
                    TclList.append(interp, list,
                            TclString.newInstance(argv[2].toString()));
                    TclList.append(interp, list, TclString.newInstance(ro.resource));
                    TclList.append(interp, list, TclString.newInstance(ro.className));
                    TclList.append(interp, list,
                            TclString.newInstance(ro.defaultVal));
                    TclList.append(interp, list, TclString.newInstance(result));
                    interp.setResult(list);
                } else {
                    swkjcolorchooser.configure(interp, argv, 2);
                }

                break;

            case OPT_CHOOSE:
                choose(interp, swkjcolorchooser, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void choose(Interp interp, SwkJColorChooser swkjcolorchooser,
            TclObject[] argv) throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "title color");
        }

        Color initialColor = SwankUtil.getColor(interp, argv[3]);
        Color color = (new Choose()).exec(swkjcolorchooser, argv[2].toString(),
                initialColor);

        if (color != null) {
            interp.setResult(SwankUtil.parseColor(color));
        }
    }

    private static class Choose extends GetValueOnEventThread {

        SwkJColorChooser swkjcolorchooser;
        Color color = null;
        Color initialColor = null;
        String title = "";

        Color exec(final SwkJColorChooser swkjcolorchooser, final String title,
                final Color initialColor) {
            this.swkjcolorchooser = swkjcolorchooser;
            this.title = title;
            this.initialColor = initialColor;
            execOnThread();

            return color;
        }

        @Override
        public void run() {
            color = SwkJColorChooser.showDialog(swkjcolorchooser.getParent(),
                    title, initialColor);
        }
    }
}
