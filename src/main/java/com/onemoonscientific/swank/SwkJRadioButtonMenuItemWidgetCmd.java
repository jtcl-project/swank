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


class SwkJRadioButtonMenuItemWidgetCmd implements Command {

    static final private String[] validCmds = {
        "cget", "configure", "deselect", "flash", "invoke",
        "select"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_DESELECT = 2;
    static final private int OPT_FLASH = 3;
    static final private int OPT_INVOKE = 4;
    static final private int OPT_SELECT = 5;
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

        final SwkJRadioButtonMenuItem swkjradiobutton = (SwkJRadioButtonMenuItem) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjradiobutton.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjradiobutton.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjradiobutton.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjradiobutton.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJRadioButtonMenuItem.resourceDB.get(argv[2].toString());

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
                    swkjradiobutton.configure(interp, argv, 2);
                }

                break;

            case OPT_DESELECT:
                deselect(interp, swkjradiobutton, argv);

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
                invoke(interp, swkjradiobutton, argv);
                break;

            case OPT_SELECT:
                select(interp, swkjradiobutton, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }
    void invoke(final Interp interp, final SwkJRadioButtonMenuItem swkjradiobutton,
            final TclObject[] argv) throws TclException {
        CommandVarListenerSettings buttonSettings = (new Toggle()).exec(swkjradiobutton);
        if (!buttonSettings.isEnabled()) {
            return;
        }
        swkjradiobutton.commandListener.tclAction(buttonSettings);
    }

    void deselect(final Interp interp, final SwkJRadioButtonMenuItem swkjradiobutton,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }

        CommandVarListenerSettings buttonSettings = (new Select()).exec(swkjradiobutton,false);
        if (!buttonSettings.isEnabled()) {
            return;
        }
        swkjradiobutton.commandListener.tclAction(buttonSettings);

    }

    void select(final Interp interp, final SwkJRadioButtonMenuItem swkjradiobutton,
            final TclObject[] argv) throws TclException {
        if (argv.length != 2) {
            throw new TclNumArgsException(interp, 2, argv, "");
        }
        CommandVarListenerSettings buttonSettings = (new Select()).exec(swkjradiobutton,true);
        if (!buttonSettings.isEnabled()) {
            return;
        }
        swkjradiobutton.commandListener.tclAction(buttonSettings);

    }

    private static class Select extends GetValueOnEventThread {

        boolean mode = false;
        SwkJRadioButtonMenuItem swkjradiobutton;
        CommandVarListenerSettings buttonSettings;

        CommandVarListenerSettings  exec(SwkJRadioButtonMenuItem swkjradiobutton, final boolean mode) {
            this.mode = mode;
            this.swkjradiobutton = swkjradiobutton;
            execOnThread();
            return buttonSettings;
        }

        @Override
        public void run() {
            if (!swkjradiobutton.isEnabled()) {
                return;
            }

            swkjradiobutton.setSelected(mode);
            buttonSettings = swkjradiobutton.commandListener.getButtonSettings();
        }
    }
   static class ButtonState {
        final private boolean enabled;
        final private String varName;
        final private String varValue;
        final private boolean selected;
        ButtonState (final boolean enabled, final String varName, final String varValue, final boolean selected) {
            this.enabled = enabled;
            this.varName = varName;;
            this.varValue = varValue;
            this.selected = selected;
        }
   }
   static class Toggle extends GetValueOnEventThread {

        SwkJRadioButtonMenuItem swkjradiobutton;
        CommandVarListenerSettings buttonSettings;

        CommandVarListenerSettings  exec(SwkJRadioButtonMenuItem swkjradiobutton) {
            this.swkjradiobutton = swkjradiobutton;
            execOnThread();
            return buttonSettings;
        }

        @Override
        public void run() {
            if (swkjradiobutton.isEnabled()) {
                swkjradiobutton.setSelected(!swkjradiobutton.isSelected());
                buttonSettings = swkjradiobutton.commandListener.getButtonSettings();
            }
        }
    }

}
