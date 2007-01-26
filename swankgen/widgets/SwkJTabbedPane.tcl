#
# Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, NJ, USA
#
#
append specialVars {
}

append specialListeners {
}


append specialInits {

}
  


append specialMethods {
   public void tabConfigure(Interp interp, int index, TclObject[] argv,
        int start) throws TclException {
        if (((argv.length - start) % 2) != 0) {
            throw new TclNumArgsException(interp, 0, argv,
                "-option value ? -option value? ...");
        }

        for (int i = start; i < argv.length; i += 2) {
            if (argv[i].toString().equals("-foreground")) {
                setForegroundAt(index, SwankUtil.getColor(interp, argv[i + 1]));
            } else if (argv[i].toString().equals("-background")) {
                setBackgroundAt(index, SwankUtil.getColor(interp, argv[i + 1]));
            } else if (argv[i].toString().startsWith("-title")) {
                setTitleAt(index, argv[i + 1].toString());
            } else if (argv[i].toString().startsWith("-tooltiptext")) {
                setToolTipTextAt(index, argv[i + 1].toString());
            } else if (argv[i].toString().startsWith("-icon")) {
                ImageIcon icon = SwankUtil.getImageIcon(interp, argv[i + 1]);
                setIconAt(index, icon);
            }
        }
    }
    public void tabCGet(Interp interp, TclObject argv, int index)
        throws TclException {
        if (argv.toString().equals("-foreground")) {
            interp.setResult(SwankUtil.parseColor(getForegroundAt(index)));
        } else if (argv.toString().equals("-background")) {
            interp.setResult(SwankUtil.parseColor(getBackgroundAt(index)));
        } else if (argv.toString().equals("-title")) {
            interp.setResult(getTitleAt(index));
        } else if (argv.toString().equals("-tooltiptext")) {
            interp.setResult(getToolTipTextAt(index));
        } else if (argv.toString().equals("-widget")) {
            interp.setResult(getComponentAt(index).getName());
        }

        return;
    }
}
