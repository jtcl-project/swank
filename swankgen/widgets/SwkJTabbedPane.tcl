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
    public String tabCGet(String argv, int index) {
        String result=null;

        if (argv.equals("-foreground")) {
            result = SwankUtil.parseColor(getForegroundAt(index));
        } else if (argv.equals("-background")) {
            result = SwankUtil.parseColor(getBackgroundAt(index));
        } else if (argv.equals("-title")) {
            result = getTitleAt(index);
        } else if (argv.equals("-tooltiptext")) {
            result = getToolTipTextAt(index);
        } else if (argv.equals("-widget")) {
            result = getComponentAt(index).getName();
        }

        return result;
    }

}
