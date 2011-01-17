/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;
import tcl.pkg.java.ReflectObject;

class SwkJCheckBoxWidgetCmd implements Command {

    private static final String[] validCmds = {
        "cget", "configure", "deselect", "flash", "invoke",
        "select", "toggle"
    };
    private static final int OPT_CGET = 0;
    private static final int OPT_CONFIGURE = 1;
    private static final int OPT_DESELECT = 2;
    private static final int OPT_FLASH = 3;
    private static final int OPT_INVOKE = 4;
    private static final int OPT_SELECT = 5;
    private static final int OPT_TOGGLE = 6;
    static boolean gotDefaults = false;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
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

        final SwkJCheckBox swkjcheckbox = (SwkJCheckBox) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjcheckbox.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjcheckbox.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjcheckbox.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjcheckbox.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJCheckBox.resourceDB.get(argv[2].toString());

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
                    swkjcheckbox.configure(interp, argv, 2);
                }

                break;

            case OPT_DESELECT:
                deselect(interp, swkjcheckbox, argv);

                break;

            case OPT_FLASH:

                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }

                break;

            case OPT_INVOKE:

                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }
                invoke(interp, swkjcheckbox, argv);
                break;

            case OPT_SELECT:
                select(interp, swkjcheckbox, argv);

                break;

            case OPT_TOGGLE:

                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 2, argv, "");
                }
                toggle(interp, swkjcheckbox, argv);
                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }
    void invoke(final Interp interp, final SwkJCheckBox swkjcheckbox,
            final TclObject[] argv) throws TclException {
        SwkCheckButtonListener.ButtonSettings buttonSettings = (new Toggle()).exec(swkjcheckbox);
        if (!buttonSettings.isEnabled()) {
            return;
        }
        swkjcheckbox.commandListener.tclAction(buttonSettings);
    }
    void toggle(final Interp interp, final SwkJCheckBox swkjcheckbox,
            final TclObject[] argv) throws TclException {
        SwkCheckButtonListener.ButtonSettings buttonSettings = (new Toggle()).exec(swkjcheckbox);
        if (!buttonSettings.isEnabled()) {
            return;
        }
        swkjcheckbox.commandListener.tclActionVar(buttonSettings);
    }

    void deselect(final Interp interp, final SwkJCheckBox swkjcheckbox,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        SwkCheckButtonListener.ButtonSettings buttonSettings = (new Select()).exec(swkjcheckbox,false);
        if (!buttonSettings.isEnabled()) {
            return;
        }
        swkjcheckbox.commandListener.tclAction(buttonSettings);

    }

    void select(final Interp interp, final SwkJCheckBox swkjcheckbox,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }
        SwkCheckButtonListener.ButtonSettings buttonSettings = (new Select()).exec(swkjcheckbox,true);
        if (!buttonSettings.isEnabled()) {
            return;
        }
        swkjcheckbox.commandListener.tclAction(buttonSettings);

    }

    private static class Select extends GetValueOnEventThread {

        boolean mode = false;
        SwkJCheckBox swkjcheckbox;
        SwkCheckButtonListener.ButtonSettings buttonSettings;

        SwkCheckButtonListener.ButtonSettings  exec(SwkJCheckBox swkjcheckbox, final boolean mode) {
            this.mode = mode;
            this.swkjcheckbox = swkjcheckbox;
            execOnThread();
            return buttonSettings;
        }

        @Override
        public void run() {
            if (!swkjcheckbox.isEnabled()) {
                return;
            }

            swkjcheckbox.setSelected(mode);
            buttonSettings = swkjcheckbox.commandListener.getButtonSettings();
        }
    }
   static class ButtonState {
        final private boolean enabled;
        final private String varName;
        final private String varValue;
        final private boolean selected;
        @SuppressWarnings("empty-statement")
        ButtonState (final boolean enabled, final String varName, final String varValue, final boolean selected) {
            this.enabled = enabled;
            this.varName = varName;;
            this.varValue = varValue;
            this.selected = selected;
        }
   }
   static class Toggle extends GetValueOnEventThread {

        SwkJCheckBox swkjcheckbox;
        SwkCheckButtonListener.ButtonSettings buttonSettings;

        SwkCheckButtonListener.ButtonSettings  exec(SwkJCheckBox swkjcheckbox) {
            this.swkjcheckbox = swkjcheckbox;
            execOnThread();
            return buttonSettings;
        }

        @Override
        public void run() {
            if (swkjcheckbox.isEnabled()) {
                swkjcheckbox.setSelected(!swkjcheckbox.isSelected());
                buttonSettings = swkjcheckbox.commandListener.getButtonSettings();
            }
        }
    }

}
