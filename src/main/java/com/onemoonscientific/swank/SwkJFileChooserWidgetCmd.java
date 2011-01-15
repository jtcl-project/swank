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
import java.io.File;


import javax.swing.*;

class SwkJFileChooserWidgetCmd implements Command {

    static final private String[] validCmds = {
        "configure", "cget", "open", "save", "filter"
    };
    static final private int OPT_CONFIGURE = 0;
    static final private int OPT_CGET = 1;
    static final private int OPT_OPEN = 2;
    static final private int OPT_SAVE = 3;
    static final private int OPT_FILTER = 4;
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

        final SwkJFileChooser swkjfilechooser = (SwkJFileChooser) ReflectObject.get(interp,
                tObj);

        switch (opt) {

            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjfilechooser.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjfilechooser.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjfilechooser.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjfilechooser.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJFileChooser.resourceDB.get(argv[2].toString());

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
                    swkjfilechooser.configure(interp, argv, 2);
                }

                break;

            case OPT_OPEN:
                open(interp, swkjfilechooser, argv);

                break;

            case OPT_SAVE:
                save(interp, swkjfilechooser, argv);

                break;

            case OPT_FILTER:
                filter(interp, swkjfilechooser, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void open(Interp interp, SwkJFileChooser swkjfilechooser, TclObject[] argv)
            throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        String dialogParent = swkjfilechooser.getDialogParent();
        Component dParent = null;
        if ((dialogParent != null) && (dialogParent.length() != 0)) {
            TclObject tObj = (TclObject) Widgets.getWidget(interp, dialogParent);
            if (tObj == null) {
                throw new TclException(interp, "bad window path name \"" + dialogParent + "\"");
            }
            dParent = (Component) ReflectObject.get(interp, tObj);
        }
        File[] files = (new Open()).exec(swkjfilechooser, dParent);

        if (files != null) {
            if (swkjfilechooser.isMultiSelectionEnabled()) {
                TclObject list = TclList.newInstance();

                try {
                    for (int iFile = 0; iFile < files.length; iFile++) {
                        TclList.append(interp, list,
                                TclString.newInstance(
                                files[iFile].getAbsolutePath()));
                    }
                } catch (TclException tclE) {
                }

                interp.setResult(list);
            } else {
                interp.setResult(files[0].getAbsolutePath());
            }
        } else {
            interp.resetResult();
        }
    }

    void save(Interp interp, SwkJFileChooser swkjfilechooser, TclObject[] argv)
            throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }
        String dialogParent = swkjfilechooser.getDialogParent();
        Component dParent = null;
        if ((dialogParent != null) && (dialogParent.length() != 0)) {
            TclObject tObj = (TclObject) Widgets.getWidget(interp, dialogParent);
            if (tObj == null) {
                throw new TclException(interp, "bad window path name \"" + dialogParent + "\"");
            }
            dParent = (Component) ReflectObject.get(interp, tObj);
        }

        File file = (new Save()).exec(swkjfilechooser, dParent);

        if (file != null) {
            interp.setResult(file.getAbsolutePath());
        }
    }

    void filter(Interp interp, SwkJFileChooser swkjfilechooser, TclObject[] argv)
            throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        final SwkFileFilter filter = new SwkFileFilter(interp, argv[2],
                argv[3].toString());
        (new Filter()).exec(swkjfilechooser, filter);
    }

    private static class Open extends GetValueOnEventThread {

        SwkJFileChooser swkjfilechooser;
        Component dParent = null;
        File[] files = null;

        File[] exec(SwkJFileChooser swkjfilechooser, Component dParent) {
            this.swkjfilechooser = swkjfilechooser;
            this.dParent = dParent;
            execOnThread();

            return files;
        }

        @Override
        public void run() {
            swkjfilechooser.setVisible(true);

            int option = swkjfilechooser.showOpenDialog(dParent);

            if (option == JFileChooser.APPROVE_OPTION) {
                if (swkjfilechooser.isMultiSelectionEnabled()) {
                    files = swkjfilechooser.getSelectedFiles();
                } else {
                    if (swkjfilechooser.getSelectedFile() != null) {
                        files = new File[1];
                        files[0] = swkjfilechooser.getSelectedFile();
                    }
                }
            }
        }
    }

    private static class Save extends GetValueOnEventThread {

        SwkJFileChooser swkjfilechooser;
        Component dParent = null;
        File file = null;

        File exec(SwkJFileChooser swkjfilechooser, Component dParent) {
            this.swkjfilechooser = swkjfilechooser;
            this.dParent = dParent;
            execOnThread();

            return file;
        }

        @Override
        public void run() {
            swkjfilechooser.setVisible(true);

            int soption = swkjfilechooser.showSaveDialog(dParent);

            if (soption == JFileChooser.APPROVE_OPTION) {
                file = swkjfilechooser.getSelectedFile();
            }
        }
    }

    private static class Filter extends UpdateOnEventThread {

        SwkFileFilter filter = null;
        SwkJFileChooser swkjfilechooser = null;

        void exec(final SwkJFileChooser swkjfilechooser,
                final SwkFileFilter filter) {
            this.swkjfilechooser = swkjfilechooser;
            this.filter = filter;
            execOnThread();
        }

        @Override
        public void run() {
            swkjfilechooser.addChoosableFileFilter(filter);
        }
    }
}
