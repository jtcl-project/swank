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

import java.io.File;

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;


class SwkFileDialogWidgetCmd implements Command {
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
        TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkFileDialog swkfiledialog = (SwkFileDialog) ReflectObject.get(interp,
                tObj);

        switch (opt) {

        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkfiledialog.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkfiledialog.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkfiledialog.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkfiledialog.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkFileDialog.resourceDB.get(argv[2].toString());

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
                swkfiledialog.configure(interp, argv, 2);
            }

            break;

        case OPT_OPEN:
            open(interp, swkfiledialog, argv);

            break;

        case OPT_SAVE:
            save(interp, swkfiledialog, argv);

            break;

        case OPT_FILTER:
            filter(interp, swkfiledialog, argv);

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void open(Interp interp, SwkFileDialog swkfiledialog, TclObject[] argv)
        throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        File[] files = (new Open()).exec(swkfiledialog);

        if (files != null) {
            if (swkfiledialog.isMultiSelectionEnabled()) {
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

    void save(Interp interp, SwkFileDialog swkfiledialog, TclObject[] argv)
        throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        File file = (new Save()).exec(swkfiledialog);

        if (file != null) {
            interp.setResult(file.getAbsolutePath());
        }
    }

    void filter(Interp interp, SwkFileDialog swkfiledialog, TclObject[] argv)
        throws TclException {
        if (argv.length != 4) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        final SwkFilenameFilter filter = new SwkFilenameFilter(interp, argv[2],
                argv[3].toString());
        (new Filter()).exec(swkfiledialog, filter);
    }

    class Open extends GetValueOnEventThread {
        int index = -1;
        String item = null;
        SwkFileDialog swkfiledialog;
        File[] files = null;

        File[] exec(SwkFileDialog swkfiledialog) {
            this.swkfiledialog = swkfiledialog;
            execOnThread();

            return files;
        }

        public void run() {
            swkfiledialog.setMode(java.awt.FileDialog.LOAD);
            swkfiledialog.setVisible(true);
            if (swkfiledialog.getFile() != null) {
                if (swkfiledialog.isMultiSelectionEnabled()) {
                    files = swkfiledialog.getSelectedFiles();
                } else {
                    if (swkfiledialog.getSelectedFile() != null) {
                        files = new File[1];
                        files[0] = swkfiledialog.getSelectedFile();
                    }
                }
            }
            ((Window) swkfiledialog.getParent()).hide();
        }
    }

    class Save extends GetValueOnEventThread {
        int index = -1;
        String item = null;
        SwkFileDialog swkfiledialog;
        File file = null;

        File exec(SwkFileDialog swkfiledialog) {
            this.swkfiledialog = swkfiledialog;
            execOnThread();

            return file;
        }

        public void run() {
            swkfiledialog.setMode(java.awt.FileDialog.SAVE);
            swkfiledialog.setVisible(true);
            if (swkfiledialog.getFile() != null) {
                file = swkfiledialog.getSelectedFile();
            }
        }
    }

    class Filter extends UpdateOnEventThread {
        SwkFilenameFilter filter = null;
        SwkFileDialog swkfiledialog = null;

        void exec(final SwkFileDialog swkfiledialog,
            final SwkFilenameFilter filter) {
            this.swkfiledialog = swkfiledialog;
            this.filter = filter;
            execOnThread();
        }

        public void run() {
           swkfiledialog.setFilenameFilter(filter);
        }
    }
}
