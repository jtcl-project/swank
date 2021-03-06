package com.onemoonscientific.swank.canvas;

import tcl.lang.*;
import java.util.*;

/**
 *
 * @author brucejohnson
 */
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
    static TreeMap stdMap = null;

    @Override
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
    @Override
    public String toString() {
        return "CanvasParameter: " + getName();
    }

    /**
     *
     * @return
     */
    abstract public String getName();

    /**
     *
     * @return
     */
    abstract public String getDefault();

    /**
     *
     * @param s
     * @return
     */
    public boolean isParameterLabel(String s) {
        boolean value = false;
        if (s.length() > 3) {
            if (s.charAt(0) == '-') {
                if (getName().startsWith(s.substring(1))) {
                    value = true;
                }
            } else {
                if (getName().startsWith(s)) {
                    value = true;
                }
            }
        }
        return value;
    }

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param arg
     * @throws TclException
     */
    public void setValue(Interp interp, SwkImageCanvas swkCanvas, TclObject arg)
            throws TclException {
    }

    /**
     *
     * @param interp
     * @param swkShape
     * @return
     * @throws TclException
     */
    public abstract TclObject getValue(Interp interp, SwkShape swkShape)
            throws TclException;

    /**
     *
     * @param par
     */
    public static void addParameter(CanvasParameter par) {
        if (stdMap == null) {
            stdMap = new TreeMap();
            initStdPar();
        }

        stdMap.put(par.getName(), par);
    }

    /**
     * 
     * @param arg
     * @return
     */
    public static CanvasParameter getStdPar(String arg) {
        if (stdMap == null) {
            stdMap = new TreeMap();
            initStdPar();
        }

        return getPar(stdMap, arg);
    }

    /**
     *
     * @param map
     * @param arg
     * @return
     */
    public static CanvasParameter getPar(TreeMap map, String arg) {
        String searchArg = null;

        if (arg.charAt(0) == '-') {
            searchArg = arg.substring(1);
        } else {
            searchArg = arg;
        }

        SortedMap tailMap = null;
        tailMap = map.tailMap(searchArg.substring(0, 1));

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
