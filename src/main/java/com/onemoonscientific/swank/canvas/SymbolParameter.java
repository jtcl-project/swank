package com.onemoonscientific.swank.canvas;

import tcl.lang.*;

import java.awt.geom.*;


public class SymbolParameter extends CanvasParameter {
    private static String name = "symbol";
    private static int defValue = 3;
    private int newValue = defValue;

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue + "";
    }

    public boolean isParameterLabel(String s) {
        if ((s.length() > 3) && "-symbol".startsWith(s)) {
            return true;
        } else {
            return false;
        }
    }

    public int parseSymbolType(String symbolName) {
        int symbolType = -1;

        if ("none".startsWith(symbolName)) {
            symbolType = -1;
        } else if ("circle".startsWith(symbolName)) {
            symbolType = 0;
        } else if ("triangle_up".startsWith(symbolName)) {
            symbolType = 1;
        } else if ("triangle_down".startsWith(symbolName)) {
            symbolType = 2;
        } else if ("cross".startsWith(symbolName)) {
            symbolType = 3;
        } else if ("square".startsWith(symbolName)) {
            symbolType = 4;
        } else if ("diamond".startsWith(symbolName)) {
            symbolType = 5;
        }

        return symbolType;
    }

    public static String getSymbolType(int symbolType) {
        switch (symbolType) {
        case -1:
            return "none";

        case 0:
            return "circle";

        case 1:
            return "uptriangle";

        case 2:
            return "downtriangle";

        case 3:
            return "cross";

        case 4:
            return "square";

        case 5:
            return "diamond";

        default:
            return "";
        }
    }

    public TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException {
        String symbolType = "";

        if ((swkShape != null) && (swkShape instanceof SymbolInterface)) {
            symbolType = ((SymbolInterface) swkShape).getSymbolType();
        }

        return TclString.newInstance(symbolType);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
        newValue = -1;

        if ((arg != null) && (arg.toString().length() > 0)) {
            if (Character.isDigit(arg.toString().charAt(0))) {
                newValue = TclInteger.get(interp, arg);
            } else {
                newValue = parseSymbolType(arg.toString());
            }
        }
    }

    public void exec(SwkImageCanvas swkCanvas, SwkShape swkShape) {
        if ((swkShape != null) && (swkShape instanceof SymbolInterface)) {
            ((SymbolInterface) swkShape).setSymbolType(newValue);
        }
    }
}
