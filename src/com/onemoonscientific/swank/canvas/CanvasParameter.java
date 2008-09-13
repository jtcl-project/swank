package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;

import tcl.lang.*;

import java.util.*;

import javax.swing.SwingUtilities;


public abstract class CanvasParameter implements CanvasParameterConfigure,
    Cloneable {
    static CanvasParameter[] stdParameters = {
        new AnchorParameter(), new AngleStartParameter(),
        new ArcStyleParameter(), new BitmapParameter(), new CapstyleParameter(),
        new DashParameter(), new DashPhaseParameter(), new ExtentParameter(),
        new FillParameter(), new FontParameter(), new GradientParameter(),
        new ImageParameter(), new JoinstyleParameter(), new JustifyParameter(),
        new OutlineParameter(), new RadiusParameter(), new RotateParameter(),
        new ShearParameter(), new SmoothParameter(), new StateParameter(),
        new SymbolParameter(), new TagsParameter(), new TextParameter(),
        new TextWidthParameter(), new TextureParameter(),
        new TransformerParameter(), new WidthParameter()
    };
    static Map stdMap = null;
    String name = "";
    String defValue = "";
    SwkShape swkShape = null;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    static void initStdPar() {
        for (int i = 0; i < stdParameters.length; i++) {
            if ((stdParameters[i] != null)) {
                if ((stdParameters[i].getName() == null)) {
                    System.out.println("null name");
                } else {
                    stdMap.put(stdParameters[i].getName(), stdParameters[i]);
                }
            }
        }
    }

    /*
    public int compare(Object o1, Object o2) {
        if (o1 == null) {
            throw new NullPointerException();
        }
        if (o2 == null) {
            throw new NullPointerException();
        }
        if (!(o1 instanceof CanvasParameter)) {
            throw new ClassCastException();
        }
        if (!(o2 instanceof CanvasParameter)) {
            throw new ClassCastException();
        }
        CanvasParameter cp1 = (CanvasParameter) o1;
        CanvasParameter cp2 = (CanvasParameter) o2;
        String name1 = cp1.getName();
        String name2 = cp1.getName();
        if ((name1 == null) || (name2 == null)) {
            throw new NullPointerException();
        }
        return name1.compareTo(name2);
    }
    public boolean  equals(Object obj) {
        if (this == obj) {
            return true;
         }
         if (!(obj instanceof CanvasParameter)) {
                return false;
         }
         CanvasParameter cp = (CanvasParameter) obj;
         return (name == null ? cp.name == null : name.equals(cp.name));
    }
    public int hashCode() {
            return  name.hashCode();
    }
     */
    public String toString() {
        return "CanvasParameter: " + name;
    }

    public String getName() {
        return name;
    }

    public String getDefault() {
        return defValue;
    }

    public boolean isParameterLabel(String s) {
        return s.equals(name);
    }

    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
        throws TclException {
    }

    public abstract TclObject getValue(Interp interp, SwkShape swkShape)
        throws TclException;

    public static void addParameter(CanvasParameter par) {
        if (stdMap == null) {
            stdMap = new TreeMap();
            initStdPar();
        }

        stdMap.put(par.getName(), par);
    }

    public static CanvasParameter getStdPar(String arg) {
        if (stdMap == null) {
            stdMap = new TreeMap();
            initStdPar();
        }

        return getPar(stdMap, arg);
    }

    public static CanvasParameter getPar(Map map, String arg) {
        String searchArg = null;

        if (arg.charAt(0) == '-') {
            searchArg = arg.substring(1);
        } else {
            searchArg = arg;
        }

        TreeMap parMap = (TreeMap) map;
        SortedMap tailMap = null;
        tailMap = parMap.tailMap(searchArg.substring(0, 1));

        Iterator iter = tailMap.values().iterator();
        CanvasParameter par = null;

        // FIXME following doensn't test for ambiguity yet
        while (iter.hasNext()) {
            par = (CanvasParameter) iter.next();

            if (par.getName().equals(searchArg)) {
                return par;
            } else if (par.getName().startsWith(searchArg)) {
                return par;
            }
        }

        return null;
    }
}
