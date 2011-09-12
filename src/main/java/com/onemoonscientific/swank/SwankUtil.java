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
import java.awt.geom.*;
import java.awt.image.*;

import java.io.File;
import java.lang.reflect.Method;

import java.net.*;

import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class SwankUtil {

    static Hashtable colorTable = null;
    static Hashtable iColorTable = null;
    static Pattern intPattern = Pattern.compile("^[+-]?[0-9]+$");
    static Boolean hasJHelp = null;
    static Object helpobject = null;
    static Method helpmethod = null;

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Color getColor(Interp interp, TclObject tclObject)
            throws TclException {
        if (colorTable == null) {
            initColorTable();
        }

        try {
            if (ReflectObject.get(interp, tclObject) instanceof Color) {
                return ((Color) ReflectObject.get(interp, tclObject));
            }
        } catch (TclException tclE) {
        }

        interp.resetResult();

        TclObject[] argv = TclList.getElements(interp, tclObject);

        if (argv == null) {
            throw new TclException(interp, "no tclObject in getColor");
        }

        if (argv.length == 1) {
            if (argv[0].toString().charAt(0) == '#') {
                String colorName = argv[0].toString();

                Color color = parseHexColor(colorName);
                if (color == null) {
                    throw new TclException(interp, "invalid color name \"" + colorName + "\"");
                } else {
                    return color;
                }

            } else {
                Color color = (Color) colorTable.get(argv[0].toString().toLowerCase());

                if (color == null) {
                    throw new TclException(interp,
                            "unknown color name \"" + argv[0].toString() + "\"");
                } else {
                    return (color);
                }
            }
        } else if (argv.length > 1) {
            Color color = (Color) colorTable.get(tclObject.toString().toLowerCase());
            if (color != null) {
                return color;
            } else {
                if (argv.length == 2) {
                    color = (Color) colorTable.get(argv[0].toString().toLowerCase());
                    if (color == null) {
                        throw new TclException(interp,
                                "unknown color name \"" + argv[0].toString() + "\"");
                    } else {
                        float alpha = (float) TclDouble.get(interp, argv[1]);
                        if (alpha != 1.0) {
                            float[] rgb = new float[4];
                            color.getComponents(rgb);
                            color = new Color(rgb[0], rgb[1], rgb[2], alpha);
                        }
                        return (color);
                    }

                } else if (argv.length == 3) {
                    int red = TclInteger.get(interp, argv[0]);
                    int green = TclInteger.get(interp, argv[1]);
                    int blue = TclInteger.get(interp, argv[2]);

                    return (new Color(red, green, blue));
                } else if (argv.length == 4) {
                    int red = TclInteger.get(interp, argv[0]);
                    int green = TclInteger.get(interp, argv[1]);
                    int blue = TclInteger.get(interp, argv[2]);
                    int alpha = TclInteger.get(interp, argv[3]);

                    return (new Color(red, green, blue, alpha));
                } else {
                    return (null);
                }
            }
        } else {
            return (null);
        }
    }

    /**
     *
     * @param colorName
     * @return
     */
    public static Color getColor(String colorName) {
        if (colorTable == null) {
            initColorTable();
        }
        Color color = null;
        if (colorName.charAt(0) == '#') {
            color = parseHexColor(colorName);
        } else {
            color = (Color) colorTable.get(colorName.toLowerCase());
        }
        return color;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @param p1
     * @param p2
     * @return
     * @throws TclException
     */
    public static GradientPaint getGradient(Interp interp, TclObject tclObject,
            Point2D p1, Point2D p2) throws TclException {
        if (colorTable == null) {
            initColorTable();
        }

        TclObject[] argv = TclList.getElements(interp, tclObject);

        if (argv == null) {
            throw new TclException(interp, "no tclObject in getColor");
        }

        if ((argv.length == 6) || (argv.length == 7)) {
            double x1 = TclDouble.get(interp, argv[0]);
            double y1 = TclDouble.get(interp, argv[1]);
            Color color1 = SwankUtil.getColor(interp, argv[2]);
            double x2 = TclDouble.get(interp, argv[3]);
            double y2 = TclDouble.get(interp, argv[4]);
            Color color2 = SwankUtil.getColor(interp, argv[5]);
            p1.setLocation(x1, y1);

            //swkShape.setGradPt1(p1);
            p2.setLocation(x2, y2);

            //swkShape.setGradPt2(p2);
            boolean cyclic = false;

            if (argv.length == 7) {
                cyclic = TclBoolean.get(interp, argv[6]);
            }

            return (new GradientPaint(p1, color1, p2, color2, cyclic));
        } else {
            throw new TclNumArgsException(interp, 0, argv,
                    "x1 y1 color1 x2 y2 color2 ?cyclic?");
        }
    }

    /**
     *
     * @param gradientPaint
     * @return
     */
    public static String parseGradient(GradientPaint gradientPaint) {
        String result = "";
        if (gradientPaint != null) {
            StringBuilder sBuild = new StringBuilder();
            Point2D pt1 = gradientPaint.getPoint1();
            Color color1 = gradientPaint.getColor1();
            Point2D pt2 = gradientPaint.getPoint2();
            Color color2 = gradientPaint.getColor2();
            sBuild.append(pt1.getX());
            sBuild.append(" ");
            sBuild.append(pt1.getY());
            sBuild.append(" ");
            String colorName = parseColor(color1);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('{');
            }
            sBuild.append(colorName);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('}');
            }
            sBuild.append(" ");
            sBuild.append(pt2.getX());
            sBuild.append(" ");
            sBuild.append(pt2.getY());
            sBuild.append(" ");
            colorName = parseColor(color2);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('{');
            }
            sBuild.append(colorName);
            if (colorName.indexOf(' ') != -1) {
                sBuild.append('}');
            }
            result = sBuild.toString();
        }
        return result;
    }

    /**
     *
     * @param color
     * @return
     */
    public static String parseColor(Color color) {
        if (colorTable == null) {
            initColorTable();
        }

        if (color == null) {
            return ("");
        } else {
            int alpha = color.getAlpha();
            Color opaqueColor = color;
            if (alpha != 255) {
                opaqueColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
            }
            String colorName = (String) iColorTable.get(opaqueColor);

            if (colorName != null) {
                if (alpha != 255) {
                    colorName = colorName + " " + ((float) alpha / 255.0);
                }
                return (colorName);
            } else {
                if (alpha != 255) {
                    if (alpha < 16) {
                        return ("#0" + Integer.toHexString(color.getRGB()));
                    } else {
                        return ("#" + Integer.toHexString(color.getRGB()));
                    }
                } else {
                    return ("#" + Integer.toHexString(color.getRGB()).substring(2));
                }
            }
        }
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static String getDefault(Interp interp, TclObject tclObject) throws TclException {
        return getDefaultOrState(interp,tclObject,"default");
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static String getState(Interp interp, TclObject tclObject) throws TclException {
        return getDefaultOrState(interp,tclObject,"state");
    }
    /**
     *
     * @param interp
     * @param tclObject
     * @param mode
     * @return
     * @throws TclException
     */
    public static String getDefaultOrState(Interp interp, TclObject tclObject,final String mode)
            throws TclException {
        String value = tclObject.toString();
        String state = "";

        if (SwkWidget.NORMAL.startsWith(value)) {
            state = SwkWidget.NORMAL;
        } else if (SwkWidget.ACTIVE.startsWith(value)) {
            state = SwkWidget.ACTIVE;
        } else if (SwkWidget.DISABLED.startsWith(value)) {
            state = SwkWidget.DISABLED;
        } else {
            throw new TclException(interp,
                    "bad " + mode  + " \"" + value
                    + "\": must be active, disabled, or normal");
        }

        return state;
    }
    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static String getTextState(Interp interp, TclObject tclObject)
            throws TclException {
        String value = tclObject.toString();
        String state = "";

        if (SwkWidget.NORMAL.startsWith(value)) {
            state = SwkWidget.NORMAL;
        } else if (SwkWidget.READONLY.startsWith(value)) {
            state = SwkWidget.READONLY;
        } else if (SwkWidget.DISABLED.startsWith(value)) {
            state = SwkWidget.DISABLED;
        } else {
            throw new TclException(interp,
                    "bad state \"" + value
                    + "\": must be disabled, normal, or readonly");
        }

        return state;
    }

    /**
     *
     * @param interp
     * @param varTrace
     * @param textVariable
     * @param name
     * @return
     * @throws TclException
     */
    public static String setupTrace(Interp interp, VarTrace varTrace,
            String textVariable, String name) throws TclException {
        if ((textVariable != null) && (!textVariable.equals(""))) {
            interp.untraceVar(textVariable, varTrace,
                    TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);
        }

        String s = null;

        if ((name != null) && !name.equals("")) {
            TclObject tObj = null;

            try {
                tObj = interp.getVar(name, TCL.GLOBAL_ONLY);
                s = tObj.toString();
            } catch (TclException tclException) {
                interp.resetResult();
                tObj = TclString.newInstance("");
                interp.setVar(name, tObj, TCL.GLOBAL_ONLY);
            }
        }

        interp.traceVar(name, varTrace, TCL.TRACE_WRITES | TCL.GLOBAL_ONLY);

        return s;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static String getWrap(Interp interp, TclObject tclObject)
            throws TclException {
        String value = tclObject.toString();
        String wrap = "none";

        if ("none".startsWith(value)) {
            wrap = "none";
        } else if ("char".startsWith(value)) {
            wrap = "char";
        } else if ("word".startsWith(value)) {
            wrap = "word";
        } else {
            throw new TclException(interp,
                    "bad wrap mode \"" + value + "\": must be char, none, or word");
        }

        return wrap;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Object[] getOptions(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] tOptions = TclList.getElements(interp, tclObject);
        Object[] options = new Object[tOptions.length];

        for (int i = 0; i < tOptions.length; i++) {
            options[i] = tOptions[i].toString();
        }

        return options;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static String getJustify(Interp interp, TclObject tclObject)
            throws TclException {
        String value = tclObject.toString();
        String justify = "";

        if (SwkWidget.LEFT.startsWith(value)) {
            justify = SwkWidget.LEFT;
        } else if (SwkWidget.RIGHT.startsWith(value)) {
            justify = SwkWidget.RIGHT;
        } else if (SwkWidget.CENTER.startsWith(value)) {
            justify = SwkWidget.CENTER;
        } else {
            throw new TclException(interp,
                    "bad justification \"" + value
                    + "\": must be left, right, or center");
        }

        return justify;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static int getOrient(Interp interp, TclObject tclObject)
            throws TclException {
        String value = tclObject.toString();
        int orient = JSlider.VERTICAL;

        if ("vertical".startsWith(value)) {
            orient = JSlider.VERTICAL;
        } else if ("horizontal".startsWith(value)) {
            orient = JSlider.HORIZONTAL;
        } else {
            throw new TclException(interp,
                    "bad orient \"" + value
                    + "\": must be horizontal or vertical");
        }

        return orient;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static float[] getAnchor(Interp interp, TclObject tclObject)
            throws TclException {
        if (tclObject == null) {
            float[] a = new float[2];
            a[0] = 0.0f;
            a[1] = 0.0f;

            return (a);
        } else {
            return getAnchor(interp, tclObject.toString());
        }
    }

    /**
     *
     * @param interp
     * @param string
     * @return
     * @throws TclException
     */
    public static float[] getAnchor(Interp interp, String string)
            throws TclException {
        float[] a = new float[2];

        if (string == null) {
            a[0] = 0.0f;
            a[1] = 0.0f;

            return (a);
        } else if (string.equals("n")) {
            a[0] = 1.0f;
            a[1] = 0.5f;
        } else if (string.equals("s")) {
            a[0] = 0.0f;
            a[1] = 0.5f;
        } else if (string.equals("e")) {
            a[0] = 0.5f;
            a[1] = 1.0f;
        } else if (string.equals("w")) {
            a[0] = 0.5f;
            a[1] = 0.0f;
        } else if (string.equals("nw")) {
            a[0] = 1.0f;
            a[1] = 0.0f;
        } else if (string.equals("sw")) {
            a[0] = 0.0f;
            a[1] = 0.0f;
        } else if (string.equals("ne")) {
            a[0] = 1.0f;
            a[1] = 1.0f;
        } else if (string.equals("se")) {
            a[0] = 0.0f;
            a[1] = 1.0f;
        } else if (string.startsWith("c")) {
            a[0] = 0.5f;
            a[1] = 0.5f;
        } else {
            throw new TclException(interp,
                    "bad anchor \"" + string
                    + "\": must be n, ne, e, se, s, sw, w, nw, or center");
        }

        return (a);
    }

    /**
     *
     * @param a
     * @return
     */
    public static String parseAnchor(float[] a) {
        char[] anchor = {' ', ' '};

        if (a[1] == 0.0f) {
            anchor[1] = 'w';
        } else if (a[1] == 1.0f) {
            anchor[1] = 'e';
        }

        if (a[0] == 1.0f) {
            anchor[0] = 'n';
        } else if (a[0] == 0.0f) {
            anchor[0] = 's';
        }

        if ((anchor[0] == ' ') && (anchor[1] == ' ')) {
            return ("c");
        } else {
            return (new String(anchor).trim());
        }
    }

    /**
     *
     * @param interp
     * @param string
     * @return
     * @throws TclException
     */
    public static int[] getAnchorConstants(Interp interp, String string)
            throws TclException {
        int[] a = new int[2];

        if (string == null) {
            a[0] = SwingConstants.BOTTOM;
            a[1] = SwingConstants.LEFT;

            return (a);
        } else if (string.equals("n")) {
            a[0] = SwingConstants.TOP;
            a[1] = SwingConstants.CENTER;
        } else if (string.equals("s")) {
            a[0] = SwingConstants.BOTTOM;
            a[1] = SwingConstants.CENTER;
        } else if (string.equals("nw")) {
            a[0] = SwingConstants.TOP;
            a[1] = SwingConstants.LEFT;
        } else if (string.equals("w")) {
            a[0] = SwingConstants.CENTER;
            a[1] = SwingConstants.LEFT;
        } else if (string.equals("sw")) {
            a[0] = SwingConstants.BOTTOM;
            a[1] = SwingConstants.LEFT;
        } else if (string.equals("ne")) {
            a[0] = SwingConstants.TOP;
            a[1] = SwingConstants.RIGHT;
        } else if (string.equals("e")) {
            a[0] = SwingConstants.CENTER;
            a[1] = SwingConstants.RIGHT;
        } else if (string.equals("se")) {
            a[0] = SwingConstants.BOTTOM;
            a[1] = SwingConstants.RIGHT;
        } else if (string.startsWith("c")) {
            a[0] = SwingConstants.CENTER;
            a[1] = SwingConstants.CENTER;
        } else {
            throw new TclException(interp,
                    "bad anchor \"" + string
                    + "\": must be n, ne, e, se, s, sw, w, nw, or center");
        }

        return (a);
    }

    /**
     *
     * @param a
     * @return
     */
    public static String parseAnchorConstants(int[] a) {
        char[] anchor = {' ', ' '};

        if (a[1] == SwingConstants.LEFT) {
            anchor[1] = 'w';
        } else if (a[1] == SwingConstants.RIGHT) {
            anchor[1] = 'e';
        }

        if (a[0] == SwingConstants.TOP) {
            anchor[0] = 'n';
        } else if (a[0] == SwingConstants.BOTTOM) {
            anchor[0] = 's';
        }

        if ((anchor[0] == ' ') && (anchor[1] == ' ')) {
            return ("c");
        } else {
            return (new String(anchor).trim());
        }
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Rectangle getRectangle(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
        int x = TclInteger.get(interp, argv[0]);
        int y = TclInteger.get(interp, argv[1]);
        int width = TclInteger.get(interp, argv[2]);
        int height = TclInteger.get(interp, argv[3]);

        return (new Rectangle(x, y, width, height));
    }

    /**
     *
     * @param rectangle
     * @return
     */
    public static String parseRectangle(Rectangle rectangle) {
        if (rectangle == null) {
            return 0 + " " + 0 + " " + 0 + " " + 0;
        }

        int x = rectangle.x;
        int y = rectangle.y;
        int width = rectangle.width;
        int height = rectangle.height;

        return x + " " + y + " " + width + " " + height;
    }

    /**
     *
     * @param interp
     * @param comp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Rectangle getTkRectangle(Interp interp, Component comp,
            TclObject tclObject) throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
        int x = getTkSize(interp, comp, argv[0]);
        int y = getTkSize(interp, comp, argv[1]);
        int width = getTkSize(interp, comp, argv[2]);
        int height = getTkSize(interp, comp, argv[3]);

        return (new Rectangle(x, y, width, height));
    }

    /**
     *
     * @param rectangle
     * @return
     */
    public static String parseTkRectangle(Rectangle rectangle) {
        int x = rectangle.x;
        int y = rectangle.y;
        int width = rectangle.width;
        int height = rectangle.height;

        return x + " " + y + " " + width + " " + height;
    }

    /**
     *
     * @param interp
     * @param comp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static int[][] getTkRectangleCorners(Interp interp, Component comp,
            TclObject tclObject) throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
        int[][] corners = new int[2][2];
        corners[0][0] = getTkSize(interp, comp, argv[0]);
        corners[0][1] = getTkSize(interp, comp, argv[1]);
        corners[1][0] = getTkSize(interp, comp, argv[2]);
        corners[1][1] = getTkSize(interp, comp, argv[3]);

        return (corners);
    }

    /**
     *
     * @param corners
     * @return
     */
    public static String parseTkRectangleCorners(int[][] corners) {
        return corners[0][0] + " " + corners[0][1] + " " + corners[1][0] + " "
                + corners[1][1];
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static URL getURL(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
        URL url = getURL(interp, argv[0].toString());

        return (url);
    }

    /**
     *
     * @param interp
     * @param urlString
     * @return
     * @throws TclException
     */
    public static URL getURL(Interp interp, String urlString)
            throws TclException {
        try {
            URL url = new URL(urlString);

            return (url);
        } catch (MalformedURLException urlE) {
            throw new TclException(interp, urlE.toString());
        }
    }

    /**
     *
     * @param url
     * @return
     */
    public static String parseURL(URL url) {
        if (url == null) {
            return ("");
        } else {
            return url.toString();
        }
    }

    /**
     *
     * @param interp
     * @param tObj
     * @return
     * @throws TclException
     */
    public static File getFile(Interp interp, TclObject tObj)
            throws TclException {
        if (tObj == null) {
            throw new TclException(interp,
                    "null fileString in SwankUtil.getFile");
        }

        File file = new File(tObj.toString());

        return (file);
    }

    /**
     *
     * @param file
     * @return
     */
    public static String parseFile(File file) {
        if (file == null) {
            return ("");
        } else {
            return file.toString();
        }
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Insets getInsets(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
         if (argv.length != 4) {
             throw new TclException(interp,"Inset arg must have two elements");
         }
        int top = TclInteger.get(interp, argv[0]);
        int left = TclInteger.get(interp, argv[1]);
        int bottom = TclInteger.get(interp, argv[2]);
        int right = TclInteger.get(interp, argv[3]);

        return (new Insets(top, left, bottom, right));
    }

    /**
     *
     * @param inset
     * @return
     */
    public static String parseInsets(Insets inset) {
        int top = 0;
        int left = 0;
        int bottom = 0;
        int right = 0;

        if (inset != null) {
            top = inset.top;
            left = inset.left;
            bottom = inset.bottom;
            right = inset.right;
        }

        return top + " " + left + " " + bottom + " " + right;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Point getPoint(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
         if (argv.length != 2) {
             throw new TclException(interp,"Point arg must have two elements");
         }
        int x = TclInteger.get(interp, argv[0]);
        int y = TclInteger.get(interp, argv[1]);

        return (new Point(x, y));
    }

    /**
     *
     * @param point
     * @return
     */
    public static String parsePoint(Point point) {
        int x = point.x;
        int y = point.y;

        return x + " " + y;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Dimension getDimension(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
         if (argv.length != 2) {
             throw new TclException(interp,"Dimension arg must have two elements");
         }
        int width = TclInteger.get(interp, argv[0]);
        int height = TclInteger.get(interp, argv[1]);

        return (new Dimension(width, height));
    }

    /**
     *
     * @param dimension
     * @return
     */
    public static String parseDimension(Dimension dimension) {
        int width = dimension.width;
        int height = dimension.height;

        return width + " " + height;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Locale getLocale(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);

        if (argv.length == 2) {
            return (new Locale(argv[0].toString(), argv[1].toString()));
        } else if (argv.length == 3) {
            return (new Locale(argv[0].toString(), argv[1].toString(),
                    argv[2].toString()));
        } else {
            return (Locale.US);
        }
    }

    /**
     *
     * @param locale
     * @return
     */
    public static String parseLocale(Locale locale) {
        return locale.getLanguage() + " " + locale.getCountry() + " "
                + locale.getVariant();
    }

    /**
     *
     * @param imageIcon
     * @return
     */
    public static String parseImageIcon(ImageIcon imageIcon) {
        if (imageIcon == null) {
            return "";
        } else {
            String description = imageIcon.getDescription();

            if (description == null) {
                return ("");
            } else {
                return description;
            }
        }
    }

    /**
     *
     * @param imageObject
     * @return
     */
    public static String parseImageIcon(Object imageObject) {
        if (imageObject == null) {
            return "";
        } else if (imageObject instanceof ImageIcon) {
            return parseImageIcon((ImageIcon) imageObject);
        } else {
            return "";
        }
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static ImageIcon getBitmapImage(Interp interp, TclObject tclObject)
            throws TclException {
          return getImageIcon(interp,tclObject,true);
    }
    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static ImageIcon getImageIcon(Interp interp, TclObject tclObject)
            throws TclException {
          return getImageIcon(interp,tclObject,false);
    }
  
    /**
     *
     * @param interp
     * @param tclObject
     * @param bitmapMode
     * @return
     * @throws TclException
     */
    public static ImageIcon getImageIcon(Interp interp, TclObject tclObject,final boolean bitmapMode)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);

        if (argv.length == 0) {
            return null;
        }

        ImageIcon image = null;
        Object imageObject = null;

        if ((argv.length == 1) && !argv[0].toString().startsWith("@")) {
            imageObject = ImageCmd.getImage(argv[0].toString());

            if (imageObject == null) {
                if (bitmapMode) {
                    throw new TclException(interp,
                            "bitmap \"" + argv[0].toString() + "\" not defined");
                } else {
                    throw new TclException(interp,
                            "image \"" + argv[0].toString() + "\" doesn't exist");
                }
            } else {
                if (imageObject instanceof ImageIcon) {
                    return ((ImageIcon) imageObject);
                } else if (imageObject instanceof BufferedImage) {
                    return (new ImageIcon((BufferedImage) imageObject));
                } else {
                    throw new TclException(interp,
                            "image \"" + argv[0].toString() + "\" wrong type");
                }
            }
        } else {
            String imageFile;
            String imageName;

            if (argv[0].toString().startsWith("@")) {
                imageFile = argv[0].toString().substring(1);
            } else {
                imageFile = argv[0].toString();
            }

            if (argv.length == 2) {
                imageName = argv[1].toString();
            } else {
                imageName = imageFile;
            }

            image = (ImageIcon) ImageCmd.getImage(imageName);

            if (image == null) {
                URL url = null;

                if (imageFile.startsWith("resource:")) {
                    url = Thread.currentThread().getContextClassLoader().getResource(imageFile.substring(10));
                } else {
                    url = Thread.currentThread().getContextClassLoader().getResource(imageFile);
                }

                if (url != null) {
                    image = new ImageIcon(url, imageName);
                } else {
                    image = new ImageIcon(imageFile, imageName);
                }

                if (image == null) {
                    throw new TclException(interp,
                            "Couldn't load image " + imageFile);
                }

                ImageCmd.addImage(interp,imageName,image);
                image.setDescription(imageName);
            }

            return (image);
        }
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static BufferedImage getBufferedImage(Interp interp,
            TclObject tclObject) throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);

        if (argv.length == 0) {
            return null;
        }

        ImageIcon imageIcon = null;
        Object imageObject = null;

        if ((argv.length == 1) && !argv[0].toString().startsWith("@")) {
            imageObject = ImageCmd.getImage(argv[0].toString());

            if (imageObject == null) {
                throw new TclException(interp,
                        "image \"" + argv[0].toString() + "\" doesn't exist");
            } else {
                if (imageObject instanceof BufferedImage) {
                    return ((BufferedImage) imageObject);
                } else {
                    if (imageObject instanceof ImageIcon) {
                        imageIcon = (ImageIcon) imageObject;

                        if ((imageIcon.getIconWidth() <= 0)
                                || (imageIcon.getIconHeight() <= 0)) {
                            throw new TclException(interp,
                                    "image \"" + argv[0].toString()
                                    + "\" has invalid size");
                        }

                        BufferedImage bufferedImage = makeBufferedImage(imageIcon);

                        return (bufferedImage);
                    } else {
                        throw new TclException(interp,
                                "image \"" + argv[0].toString()
                                + "\" not BufferedImage");
                    }
                }
            }
        } else {
            String imageFile;
            String imageName;

            if (argv[0].toString().startsWith("@")) {
                imageFile = argv[0].toString().substring(1);
            } else {
                imageFile = argv[0].toString();
            }

            if (argv.length == 2) {
                imageName = argv[1].toString();
            } else {
                imageName = imageFile;
            }

            imageObject = ImageCmd.getImage(imageName);

            if (imageObject == null) {
                imageIcon = new ImageIcon(imageFile, imageName);

                if (imageIcon == null) {
                    throw new TclException(interp,
                            "Couldn't load image " + imageFile);
                }

                if ((imageIcon.getIconWidth() <= 0)
                        || (imageIcon.getIconHeight() <= 0)) {
                    throw new TclException(interp,
                            "image \"" + argv[0].toString()
                            + "\" has invalid size");
                }

                BufferedImage bufferedImage = makeBufferedImage(imageIcon);
                ImageCmd.addImage(interp,imageName, bufferedImage);

                return (bufferedImage);
            } else {
                if (imageObject instanceof BufferedImage) {
                    return ((BufferedImage) imageObject);
                } else {
                    throw new TclException(interp,
                            "Image " + argv[0].toString() + " not BufferedImage");
                }
            }
        }
    }

    /**
     *
     * @param imageIcon
     * @return
     */
    public static BufferedImage makeBufferedImage(ImageIcon imageIcon) {
        BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(),
                imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(imageIcon.getImage(), 0, 0, null);

        return bufferedImage;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Font getFont(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);

        String name;
        if (argv.length == 0) {
            throw new TclException(interp,"font \"\" doesn't exist");
        } else {
            name = argv[0].toString();
        }
        int pointSize = 12;

        if (argv.length > 1) {
            pointSize = TclInteger.get(interp, argv[1]);
        }

        int style = 0;
        int i;

        for (i = 2; i < argv.length; i++) {
            if ("italic".startsWith(argv[i].toString())) {
                style |= 1;
            } else if ("bold".startsWith(argv[i].toString())) {
                style |= 2;
            }
        }

        if (style == 3) {
            return (Font.decode(name + "-bolditalic-" + pointSize));
        } else if (style == 2) {
            return (Font.decode(name + "-bold-" + pointSize));
        } else if (style == 1) {
            return (Font.decode(name + "-italic-" + pointSize));
        } else {
            return (Font.decode(name + "-plain-" + pointSize));
        }
    }

    /**
     *
     * @param font
     * @return
     */
    public static String parseFont(Font font) {
        if (font == null) {
            return ("");
        }

        StringBuffer sBuf = new StringBuffer();
        String family = font.getFamily();

        boolean hasSpaces = (family.indexOf(' ') != -1);

        if (hasSpaces) {
            sBuf.append('{');
        }

        sBuf.append(family);

        if (hasSpaces) {
            sBuf.append('}');
        }

        sBuf.append(" ");
        sBuf.append(font.getSize());

        int style = font.getStyle();

        if (style != Font.PLAIN) {
            if (style == Font.ITALIC) {
                sBuf.append(" italic");
            } else if (style == Font.BOLD) {
                sBuf.append(" bold");
            } else if (style == (Font.BOLD + Font.ITALIC)) {
                sBuf.append(" bold italic");
            }
        }

        return (sBuf.toString());
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static java.util.List getSpinlist(Interp interp, TclObject tclObject)
            throws TclException {
        TclObject[] argv = TclList.getElements(interp, tclObject);
        java.util.List spinValues = new ArrayList(argv.length);
        for (TclObject arg : argv) {
            spinValues.add(arg.toString());
        }
        return spinValues;
    }

    /**
     *
     * @param object
     * @return
     */
    public static String parseSpinlist(Object object) {
        java.util.List list = (java.util.List) object;
// fixme  need to build proper Tcl list result
        boolean first = true;
        StringBuffer sbuf = new StringBuffer();
        for (Object o : list) {
            if (!first) {
                sbuf.append(' ');
            } else {
                first = false;
            }
            sbuf.append(o.toString());
        }
        return sbuf.toString();
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static String getTkRelief(Interp interp, TclObject tclObject)
            throws TclException {
        String relief = tclObject.toString();
        String validReliefs = "flat, groove, raised, ridge, solid, or sunken";

        if (validReliefs.indexOf(relief) < 0) {
            throw new TclException(interp,
                    "bad relief \"" + relief
                    + "\": must be flat, groove, raised, ridge, solid, or sunken");
        }

        return (relief);
    }

    /**
     *
     * @param relief
     * @return
     */
    public static String parseTkRelief(String relief) {
        return (relief);
    }

    /**
     *
     * @param interp
     * @param comp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static int getTkSize(Interp interp, Component comp,
            TclObject tclObject) throws TclException {
        String value = tclObject.toString();
        int size = getTkSize(interp, comp, value);

        return size;
    }

    /**
     *
     * @param interp
     * @param comp
     * @param value
     * @return
     * @throws TclException
     */
    public static int getTkSize(Interp interp, Component comp, String value)
            throws TclException {
        Double dValue = null;
        int screenResolution = 0;

        if (comp == null) {
            screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
        } else {
            screenResolution = comp.getToolkit().getScreenResolution();
        }

        int sizeMode = 0;
        double scale = 1.0;

        if (value.endsWith("c")) {
            scale = screenResolution / 2.54;
            sizeMode = 1;
        } else if (value.endsWith("m")) {
            scale = screenResolution / 25.4;
            sizeMode = 2;
        } else if (value.endsWith("i")) {
            scale = screenResolution;
            sizeMode = 3;
        } else if (value.endsWith("p")) {
            scale = screenResolution / 72.0;
            sizeMode = 3;
        }

        if (sizeMode > 0) {
            int length = value.length();

            if (length < 2) {
                throw new TclException(interp,
                        "Couldn't parse tkSize value " + value);
            }

            try {
                dValue = new Double(value.substring(0, length - 1));
            } catch (NumberFormatException nE) {
                throw new TclException(interp,
                        "bad screen distance \"" + value + "\"");
            }

            if (dValue != null) {
                int size = (int) Math.round(dValue.doubleValue() * scale);

                return (size);
            } else {
                throw new TclException(interp,
                        "Couldn't parse tkSize value " + value);
            }
        } else {
            try {
                dValue = new Double(value);

                int size = (int) Math.round(dValue.doubleValue());

                return (size);
            } catch (NumberFormatException nE) {
                throw new TclException(interp,
                        "bad screen distance \"" + value + "\"");
            }
        }
    }

    /**
     *
     * @param size
     * @return
     */
    public static String parseTkSize(int size) {
        return (Integer.toString(size));
    }

    /**
     *
     * @param size
     * @return
     */
    public static String parseTkSizeD(double size) {
        return (Double.toString(size));
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static boolean isDashIntPattern(Interp interp, TclObject tclObject)
            throws TclException {
        if (tclObject == null) {
            throw new TclException(interp, "no tclObject in getDash");
        }

        String dashStringPattern = tclObject.toString();

        for (int i = 0; i < dashStringPattern.length(); i++) {
            if (Character.isDigit(dashStringPattern.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static float[] getDash(Interp interp, TclObject tclObject)
            throws TclException {
        if (tclObject == null) {
            throw new TclException(interp, "no tclObject in getDash");
        }

        if (isDashIntPattern(interp, tclObject)) {
            TclObject[] argv = TclList.getElements(interp, tclObject);

            if (argv == null) {
                throw new TclException(interp, "no tclObject in getDash");
            }

            float[] dash = new float[argv.length];

            for (int i = 0; i < argv.length; i++) {
                dash[i] = (float) TclDouble.get(interp, argv[i]);
            }

            for (int i = 0; i < argv.length; i++) {
                dash[i] = (float) TclDouble.get(interp, argv[i]);
            }

            return dash;
        } else {
            String dashStringPattern = tclObject.toString();
            int n = 0;

            for (int i = 0; i < dashStringPattern.length(); i++) {
                if (dashStringPattern.charAt(i) != ' ') {
                    n += 2;
                }
            }

            float[] dash = new float[n];
            int j = 0;

            for (int i = 0; i < dashStringPattern.length(); i++) {
                switch (dashStringPattern.charAt(i)) {
                    case '.':
                        dash[j++] = 2;
                        dash[j++] = 4;

                        break;

                    case ',':
                        dash[j++] = 4;
                        dash[j++] = 4;

                        break;

                    case '-':
                        dash[j++] = 6;
                        dash[j++] = 4;

                        break;

                    case '_':
                        dash[j++] = 8;
                        dash[j++] = 4;

                        break;

                    case ' ':
                        dash[j - 1] *= 2;

                        break;

                    default:
                        throw new TclException(interp,
                                "bad dash list \"" + dashStringPattern
                                + "\": must be a list of integers or a format like \"-..\"");
                }
            }

            return dash;
        }
    }

    /**
     *
     * @param interp
     * @param comp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static double getTkSizeD(Interp interp, Component comp,
            TclObject tclObject) throws TclException {
        String value = tclObject.toString();
        int screenResolution = 0;
        Toolkit toolKit = null;

        if (comp != null) {
            toolKit = comp.getToolkit();
        }

        if (toolKit == null) {
            toolKit = Toolkit.getDefaultToolkit();
        }

        screenResolution = toolKit.getScreenResolution();

        int sizeMode = 0;
        double scale = 1.0;

        if (value.endsWith("c")) {
            scale = screenResolution / 2.54;
            sizeMode = 1;
        } else if (value.endsWith("m")) {
            scale = screenResolution / 25.4;
            sizeMode = 2;
        } else if (value.endsWith("i")) {
            scale = screenResolution;
            sizeMode = 3;
        } else if (value.endsWith("p")) {
            scale = screenResolution / 72.0;
            sizeMode = 3;
        }

        if (sizeMode > 0) {
            int length = value.length();

            if (length < 2) {
                return -1;
            }

            Double dValue = new Double(value.substring(0, length - 1));

            if (dValue != null) {
                double size = dValue.doubleValue() * scale;

                return (size);
            } else {
                throw new TclException(interp,
                        "Couldn't parse tkSize value " + value);
            }
        } else {
            try {
                Double dValue = new Double(value);
                double size = dValue.doubleValue();

                return (size);
            } catch (NumberFormatException nE) {
                throw new TclException(interp,
                        "bad screen distance \"" + value + "\"");
            }
        }
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static Cursor getCursor(Interp interp, TclObject tclObject)
            throws TclException {
        if (tclObject.toString().startsWith("defa")) {
            return (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (tclObject.toString().startsWith("arrow")) {
            return (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (tclObject.toString().startsWith("left_ptr")) {
            return (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (tclObject.toString().startsWith("top_lef")) {
            return (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (tclObject.toString().startsWith("cross")) {
            return (Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else if (tclObject.toString().startsWith("hand")) {
            return (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (tclObject.toString().startsWith("move")) {
            return (Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else if (tclObject.toString().startsWith("text")) {
            return (Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else if (tclObject.toString().startsWith("wait")) {
            return (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else if (tclObject.toString().startsWith("n_res")) {
            return (Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        } else if (tclObject.toString().startsWith("ne_res")) {
            return (Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
        } else if (tclObject.toString().startsWith("e_res")) {
            return (Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        } else if (tclObject.toString().startsWith("se_res")) {
            return (Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        } else if (tclObject.toString().startsWith("s_res")) {
            return (Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        } else if (tclObject.toString().startsWith("sw_res")) {
            return (Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
        } else if (tclObject.toString().startsWith("w_res")) {
            return (Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        } else if (tclObject.toString().startsWith("nw_res")) {
            return (Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
        } else {
            Cursor cursor = CursorCmd.getCursor(tclObject.toString());

            if (cursor == null) {
                throw new TclException(interp,
                        "bad cursor spec \"" + tclObject.toString() + "\"");
            }

            return cursor;
        }
    }

    /**
     *
     * @param cursor
     * @return
     */
    public static String parseCursor(Cursor cursor) {
        int type = cursor.getType();

        switch (type) {
            case Cursor.DEFAULT_CURSOR:
                return "left_ptr";

            case Cursor.CROSSHAIR_CURSOR:
                return "crosshair";

            case Cursor.HAND_CURSOR:
                return "hand2";

            case Cursor.MOVE_CURSOR:
                return "move";

            case Cursor.TEXT_CURSOR:
                return "text";

            case Cursor.WAIT_CURSOR:
                return "wait";

            case Cursor.N_RESIZE_CURSOR:
                return "n_resize";

            case Cursor.NE_RESIZE_CURSOR:
                return "ne_resize";

            case Cursor.E_RESIZE_CURSOR:
                return "e_resize";

            case Cursor.SE_RESIZE_CURSOR:
                return "se_resize";

            case Cursor.S_RESIZE_CURSOR:
                return "s_resize";

            case Cursor.SW_RESIZE_CURSOR:
                return "sw_resize";

            case Cursor.W_RESIZE_CURSOR:
                return "w_resize";

            case Cursor.NW_RESIZE_CURSOR:
                return "nw_resize";

            case Cursor.CUSTOM_CURSOR:
                return cursor.getName();

            default:
                return "left_ptr";
        }
    }

    /**
     *
     * @param interp
     * @param tclObject
     * @return
     * @throws TclException
     */
    public static int getTkSelectMode(Interp interp, TclObject tclObject)
            throws TclException {
        if (tclObject.toString().startsWith("single")) {
            return (ListSelectionModel.SINGLE_SELECTION);
        } else if (tclObject.toString().startsWith("browse")) {
            return (ListSelectionModel.SINGLE_SELECTION);
        } else if (tclObject.toString().startsWith("multiple")) {
            return (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        } else if (tclObject.toString().startsWith("extended")) {
            return (ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            throw new TclException(interp,
                    "bad selection mode \"" + tclObject.toString() + "\"");
        }
    }

    /**
     *
     * @param mode
     * @return
     */
    public static String parseTkSelectMode(int mode) {
        switch (mode) {
            case ListSelectionModel.SINGLE_SELECTION:
                return "single";

            case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
                return "multiple";

            case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
                return "extended";

            default:
                return "single";
        }
    }

    /**
     *
     * @param interp
     * @param arg
     * @return
     */
    public static int getIndex(Interp interp, TclObject arg) {
        return (0);
    }

    /**
     *
     * @param interp
     * @param menuName
     * @return
     * @throws TclException
     */
    public static Object getMenuBar(Interp interp, String menuName)
            throws TclException {
        TclObject tObj = (TclObject) Widgets.getWidget(interp, menuName);
        SwkJMenuBar swkjmenubar = null;

        if (tObj == null) {
            swkjmenubar = new SwkJMenuBar(interp, menuName);
            interp.createCommand(menuName, new SwkJMenuBarWidgetCmd());
            tObj = ReflectObject.newInstance(interp, SwkJMenuBar.class, swkjmenubar);
            tObj.preserve();
            Widgets.addNewWidget(interp, menuName, tObj);
        }

        Object object = ReflectObject.get(interp, tObj);

        if (!(object instanceof SwkJMenuBar)) {
            throw new TclException(interp, "Invalid menu object");
        }

        return object;
    }

    /**
     *
     * @param interp
     * @param menuName
     * @return
     * @throws TclException
     */
    public static Object getMenu(Interp interp, String menuName)
            throws TclException {
        TclObject tObj = (TclObject) Widgets.getWidget(interp, menuName);
        SwkJMenu swkjmenu = null;

        if (tObj == null) {
            swkjmenu = new SwkJMenu(interp, menuName);
            interp.createCommand(menuName, new SwkJMenuWidgetCmd());
            tObj = ReflectObject.newInstance(interp, SwkJMenu.class, swkjmenu);
            tObj.preserve();
            Widgets.addNewWidget(interp, menuName, tObj);
        }

        Object object = ReflectObject.get(interp, tObj);

        if (!(object instanceof SwkJMenuBar) && !(object instanceof SwkJMenu)) {
            throw new TclException(interp, "Invalid menu object");
        }

        return object;
    }

    /**
     *
     * @param interp
     * @param jcomp
     * @param argv
     * @throws TclException
     */
    public static void addmenu(Interp interp, JComponent jcomp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        if (argv[2].toString().equals("command")) {
            SwkJMenuItem jmenuItem = new SwkJMenuItem(interp, "");
            jmenuItem.configure(interp, argv, 3);

            if (jcomp instanceof JPopupMenu) {
                ((JPopupMenu) jcomp).add(jmenuItem);
            } else {
                ((JMenu) jcomp).add(jmenuItem);
            }
        } else if (argv[2].toString().startsWith("check")) {
            SwkJCheckBoxMenuItem jmenuItem = new SwkJCheckBoxMenuItem(interp,
                    "");
            jmenuItem.configure(interp, argv, 3);

            if (jcomp instanceof JPopupMenu) {
                ((JPopupMenu) jcomp).add(jmenuItem);
            } else {
                ((JMenu) jcomp).add(jmenuItem);
            }
        } else if (argv[2].toString().startsWith("radio")) {
            SwkJRadioButtonMenuItem jmenuItem = new SwkJRadioButtonMenuItem(interp,
                    "");
            jmenuItem.configure(interp, argv, 3);

            if (jcomp instanceof JPopupMenu) {
                ((JPopupMenu) jcomp).add(jmenuItem);
            } else {
                ((JMenu) jcomp).add(jmenuItem);
            }
        } else if (argv[2].toString().startsWith("sepa")) {
            if (jcomp instanceof JPopupMenu) {
                ((JPopupMenu) jcomp).add(new JSeparator());
            } else {
                ((JMenu) jcomp).add(new JSeparator());
            }
        } else if (argv[2].toString().equals("cascade")) {
            String menuName = null;
            int j = 0;

            if (((argv.length - 3) % 2) != 0) {
                throw new TclNumArgsException(interp, 1, argv,
                        "arguments not multiple of 2");
            }

            TclObject[] argNew = new TclObject[argv.length - 5];

            for (i = 3; i < argv.length; i += 2) {
                if (argv[i].toString().equals("-menu")) {
                    menuName = argv[i + 1].toString();
                } else {
                    argNew[j] = TclString.newInstance(argv[i].toString());
                    argNew[j + 1] = TclString.newInstance(argv[i + 1].toString());
                    j += 2;
                }
            }

            SwkJMenu swkjmenu = null;
            TclObject tObj = (TclObject) Widgets.getWidget(interp, menuName);

            if (tObj == null) {
                swkjmenu = new SwkJMenu(interp, menuName);
                interp.createCommand(menuName, new SwkJMenuWidgetCmd());
                tObj = ReflectObject.newInstance(interp, SwkJMenu.class,
                        swkjmenu);
                tObj.preserve();
                swkjmenu.children = null;
                swkjmenu = (SwkJMenu) ReflectObject.get(interp, tObj);
                swkjmenu.configure(interp, argNew, 0);
                Widgets.addNewWidget(interp, menuName, tObj);
                swkjmenu.setCreated(false);
            } else {
                swkjmenu = (SwkJMenu) ReflectObject.get(interp, tObj);
                swkjmenu.configure(interp, argNew, 0);
            }

            if (jcomp instanceof JPopupMenu) {
                ((JPopupMenu) jcomp).add(swkjmenu);
            } else {
                ((JMenu) jcomp).add(swkjmenu);
            }
        }

        return;
    }

    /**
     *
     */
    public static void initColorTable() {
        colorTable = new Hashtable();
        iColorTable = new Hashtable();
        colorTable.put("alice blue", new Color(240, 248, 255));
        colorTable.put("aliceblue", new Color(240, 248, 255));
        colorTable.put("antique white", new Color(250, 235, 215));
        colorTable.put("antiquewhite", new Color(250, 235, 215));
        colorTable.put("antiquewhite1", new Color(255, 239, 219));
        colorTable.put("antiquewhite2", new Color(238, 223, 204));
        colorTable.put("antiquewhite3", new Color(205, 192, 176));
        colorTable.put("antiquewhite4", new Color(139, 131, 120));
        colorTable.put("aquamarine", new Color(127, 255, 212));
        colorTable.put("aquamarine2", new Color(118, 238, 198));
        colorTable.put("aquamarine3", new Color(102, 205, 170));
        colorTable.put("aquamarine4", new Color(69, 139, 116));
        colorTable.put("azure2", new Color(224, 238, 238));
        colorTable.put("azure3", new Color(193, 205, 205));
        colorTable.put("azure4", new Color(131, 139, 139));
        colorTable.put("azure", new Color(240, 255, 255));
        colorTable.put("beige", new Color(245, 245, 220));
        colorTable.put("bisque2", new Color(238, 213, 183));
        colorTable.put("bisque3", new Color(205, 183, 158));
        colorTable.put("bisque4", new Color(139, 125, 107));
        colorTable.put("bisque", new Color(255, 228, 196));
        colorTable.put("black", new Color(0, 0, 0));
        colorTable.put("blanched almond", new Color(255, 235, 205));
        colorTable.put("blanchedalmond", new Color(255, 235, 205));
        colorTable.put("blue violet", new Color(138, 43, 226));
        colorTable.put("blue2", new Color(0, 0, 238));
        colorTable.put("blue3", new Color(0, 0, 205));
        colorTable.put("blue4", new Color(0, 0, 139));
        colorTable.put("blue", new Color(0, 0, 255));
        colorTable.put("blueviolet", new Color(138, 43, 226));
        colorTable.put("brown1", new Color(255, 64, 64));
        colorTable.put("brown2", new Color(238, 59, 59));
        colorTable.put("brown3", new Color(205, 51, 51));
        colorTable.put("brown4", new Color(139, 35, 35));
        colorTable.put("brown", new Color(165, 42, 42));
        colorTable.put("burlywood1", new Color(255, 211, 155));
        colorTable.put("burlywood2", new Color(238, 197, 145));
        colorTable.put("burlywood3", new Color(205, 170, 125));
        colorTable.put("burlywood4", new Color(139, 115, 85));
        colorTable.put("burlywood", new Color(222, 184, 135));
        colorTable.put("cadet blue", new Color(95, 158, 160));
        colorTable.put("cadetblue", new Color(95, 158, 160));
        colorTable.put("cadetblue1", new Color(152, 245, 255));
        colorTable.put("cadetblue2", new Color(142, 229, 238));
        colorTable.put("cadetblue3", new Color(122, 197, 205));
        colorTable.put("cadetblue4", new Color(83, 134, 139));
        colorTable.put("chartreuse", new Color(127, 255, 0));
        colorTable.put("chartreuse2", new Color(118, 238, 0));
        colorTable.put("chartreuse3", new Color(102, 205, 0));
        colorTable.put("chartreuse4", new Color(69, 139, 0));
        colorTable.put("chocolate", new Color(210, 105, 30));
        colorTable.put("chocolate1", new Color(255, 127, 36));
        colorTable.put("chocolate2", new Color(238, 118, 33));
        colorTable.put("chocolate3", new Color(205, 102, 29));
        colorTable.put("chocolate4", new Color(139, 69, 19));
        colorTable.put("coral", new Color(255, 127, 80));
        colorTable.put("coral1", new Color(255, 114, 86));
        colorTable.put("coral2", new Color(238, 106, 80));
        colorTable.put("coral3", new Color(205, 91, 69));
        colorTable.put("coral4", new Color(139, 62, 47));
        colorTable.put("cornflower blue", new Color(100, 149, 237));
        colorTable.put("cornflowerblue", new Color(100, 149, 237));
        colorTable.put("cornsilk", new Color(255, 248, 220));
        colorTable.put("cornsilk2", new Color(238, 232, 205));
        colorTable.put("cornsilk3", new Color(205, 200, 177));
        colorTable.put("cornsilk4", new Color(139, 136, 120));
        colorTable.put("cyan", new Color(0, 255, 255));
        colorTable.put("cyan2", new Color(0, 238, 238));
        colorTable.put("cyan3", new Color(0, 205, 205));
        colorTable.put("cyan4", new Color(0, 139, 139));
        colorTable.put("dark blue", new Color(0, 0, 139));
        colorTable.put("dark cyan", new Color(0, 139, 139));
        colorTable.put("dark goldenrod", new Color(184, 134, 11));
        colorTable.put("dark gray", new Color(169, 169, 169));
        colorTable.put("dark green", new Color(0, 100, 0));
        colorTable.put("dark grey", new Color(169, 169, 169));
        colorTable.put("dark khaki", new Color(189, 183, 107));
        colorTable.put("dark magenta", new Color(139, 0, 139));
        colorTable.put("dark olive green", new Color(85, 107, 47));
        colorTable.put("dark orange", new Color(255, 140, 0));
        colorTable.put("dark orchid", new Color(153, 50, 204));
        colorTable.put("dark red", new Color(139, 0, 0));
        colorTable.put("dark salmon", new Color(233, 150, 122));
        colorTable.put("dark sea green", new Color(143, 188, 143));
        colorTable.put("dark slate blue", new Color(72, 61, 139));
        colorTable.put("dark slate gray", new Color(47, 79, 79));
        colorTable.put("dark slate grey", new Color(47, 79, 79));
        colorTable.put("dark turquoise", new Color(0, 206, 209));
        colorTable.put("dark violet", new Color(148, 0, 211));
        colorTable.put("darkblue", new Color(0, 0, 139));
        colorTable.put("darkcyan", new Color(0, 139, 139));
        colorTable.put("darkgoldenrod", new Color(184, 134, 11));
        colorTable.put("darkgoldenrod1", new Color(255, 185, 15));
        colorTable.put("darkgoldenrod2", new Color(238, 173, 14));
        colorTable.put("darkgoldenrod3", new Color(205, 149, 12));
        colorTable.put("darkgoldenrod4", new Color(139, 101, 8));
        colorTable.put("darkgray", new Color(169, 169, 169));
        colorTable.put("darkgreen", new Color(0, 100, 0));
        colorTable.put("darkgrey", new Color(169, 169, 169));
        colorTable.put("darkkhaki", new Color(189, 183, 107));
        colorTable.put("darkmagenta", new Color(139, 0, 139));
        colorTable.put("darkolivegreen", new Color(85, 107, 47));
        colorTable.put("darkolivegreen1", new Color(202, 255, 112));
        colorTable.put("darkolivegreen2", new Color(188, 238, 104));
        colorTable.put("darkolivegreen3", new Color(162, 205, 90));
        colorTable.put("darkolivegreen4", new Color(110, 139, 61));
        colorTable.put("darkorange", new Color(255, 140, 0));
        colorTable.put("darkorange1", new Color(255, 127, 0));
        colorTable.put("darkorange2", new Color(238, 118, 0));
        colorTable.put("darkorange3", new Color(205, 102, 0));
        colorTable.put("darkorange4", new Color(139, 69, 0));
        colorTable.put("darkorchid", new Color(153, 50, 204));
        colorTable.put("darkorchid1", new Color(191, 62, 255));
        colorTable.put("darkorchid2", new Color(178, 58, 238));
        colorTable.put("darkorchid3", new Color(154, 50, 205));
        colorTable.put("darkorchid4", new Color(104, 34, 139));
        colorTable.put("darkred", new Color(139, 0, 0));
        colorTable.put("darksalmon", new Color(233, 150, 122));
        colorTable.put("darkseagreen", new Color(143, 188, 143));
        colorTable.put("darkseagreen1", new Color(193, 255, 193));
        colorTable.put("darkseagreen2", new Color(180, 238, 180));
        colorTable.put("darkseagreen3", new Color(155, 205, 155));
        colorTable.put("darkseagreen4", new Color(105, 139, 105));
        colorTable.put("darkslateblue", new Color(72, 61, 139));
        colorTable.put("darkslategray", new Color(47, 79, 79));
        colorTable.put("darkslategray1", new Color(151, 255, 255));
        colorTable.put("darkslategray2", new Color(141, 238, 238));
        colorTable.put("darkslategray3", new Color(121, 205, 205));
        colorTable.put("darkslategray4", new Color(82, 139, 139));
        colorTable.put("darkslategrey", new Color(47, 79, 79));
        colorTable.put("darkturquoise", new Color(0, 206, 209));
        colorTable.put("darkviolet", new Color(148, 0, 211));
        colorTable.put("deep pink", new Color(255, 20, 147));
        colorTable.put("deep sky blue", new Color(0, 191, 255));
        colorTable.put("deeppink", new Color(255, 20, 147));
        colorTable.put("deeppink2", new Color(238, 18, 137));
        colorTable.put("deeppink3", new Color(205, 16, 118));
        colorTable.put("deeppink4", new Color(139, 10, 80));
        colorTable.put("deepskyblue", new Color(0, 191, 255));
        colorTable.put("deepskyblue2", new Color(0, 178, 238));
        colorTable.put("deepskyblue3", new Color(0, 154, 205));
        colorTable.put("deepskyblue4", new Color(0, 104, 139));
        colorTable.put("dim gray", new Color(105, 105, 105));
        colorTable.put("dim grey", new Color(105, 105, 105));
        colorTable.put("dimgray", new Color(105, 105, 105));
        colorTable.put("dimgrey", new Color(105, 105, 105));
        colorTable.put("dodger blue", new Color(30, 144, 255));
        colorTable.put("dodgerblue", new Color(30, 144, 255));
        colorTable.put("dodgerblue2", new Color(28, 134, 238));
        colorTable.put("dodgerblue3", new Color(24, 116, 205));
        colorTable.put("dodgerblue4", new Color(16, 78, 139));
        colorTable.put("firebrick", new Color(178, 34, 34));
        colorTable.put("firebrick1", new Color(255, 48, 48));
        colorTable.put("firebrick2", new Color(238, 44, 44));
        colorTable.put("firebrick3", new Color(205, 38, 38));
        colorTable.put("firebrick4", new Color(139, 26, 26));
        colorTable.put("floral white", new Color(255, 250, 240));
        colorTable.put("floralwhite", new Color(255, 250, 240));
        colorTable.put("forest green", new Color(34, 139, 34));
        colorTable.put("forestgreen", new Color(34, 139, 34));
        colorTable.put("gainsboro", new Color(220, 220, 220));
        colorTable.put("ghost white", new Color(248, 248, 255));
        colorTable.put("ghostwhite", new Color(248, 248, 255));
        colorTable.put("gold", new Color(255, 215, 0));
        colorTable.put("gold2", new Color(238, 201, 0));
        colorTable.put("gold3", new Color(205, 173, 0));
        colorTable.put("gold4", new Color(139, 117, 0));
        colorTable.put("goldenrod", new Color(218, 165, 32));
        colorTable.put("goldenrod1", new Color(255, 193, 37));
        colorTable.put("goldenrod2", new Color(238, 180, 34));
        colorTable.put("goldenrod3", new Color(205, 155, 29));
        colorTable.put("goldenrod4", new Color(139, 105, 20));
        colorTable.put("gray", new Color(190, 190, 190));
        colorTable.put("gray0", new Color(0, 0, 0));
        colorTable.put("gray1", new Color(3, 3, 3));
        colorTable.put("gray10", new Color(26, 26, 26));
        colorTable.put("gray100", new Color(255, 255, 255));
        colorTable.put("gray11", new Color(28, 28, 28));
        colorTable.put("gray12", new Color(31, 31, 31));
        colorTable.put("gray13", new Color(33, 33, 33));
        colorTable.put("gray14", new Color(36, 36, 36));
        colorTable.put("gray15", new Color(38, 38, 38));
        colorTable.put("gray16", new Color(41, 41, 41));
        colorTable.put("gray17", new Color(43, 43, 43));
        colorTable.put("gray18", new Color(46, 46, 46));
        colorTable.put("gray19", new Color(48, 48, 48));
        colorTable.put("gray2", new Color(5, 5, 5));
        colorTable.put("gray20", new Color(51, 51, 51));
        colorTable.put("gray21", new Color(54, 54, 54));
        colorTable.put("gray22", new Color(56, 56, 56));
        colorTable.put("gray23", new Color(59, 59, 59));
        colorTable.put("gray24", new Color(61, 61, 61));
        colorTable.put("gray25", new Color(64, 64, 64));
        colorTable.put("gray26", new Color(66, 66, 66));
        colorTable.put("gray27", new Color(69, 69, 69));
        colorTable.put("gray28", new Color(71, 71, 71));
        colorTable.put("gray29", new Color(74, 74, 74));
        colorTable.put("gray3", new Color(8, 8, 8));
        colorTable.put("gray30", new Color(77, 77, 77));
        colorTable.put("gray31", new Color(79, 79, 79));
        colorTable.put("gray32", new Color(82, 82, 82));
        colorTable.put("gray33", new Color(84, 84, 84));
        colorTable.put("gray34", new Color(87, 87, 87));
        colorTable.put("gray35", new Color(89, 89, 89));
        colorTable.put("gray36", new Color(92, 92, 92));
        colorTable.put("gray37", new Color(94, 94, 94));
        colorTable.put("gray38", new Color(97, 97, 97));
        colorTable.put("gray39", new Color(99, 99, 99));
        colorTable.put("gray4", new Color(10, 10, 10));
        colorTable.put("gray40", new Color(102, 102, 102));
        colorTable.put("gray41", new Color(105, 105, 105));
        colorTable.put("gray42", new Color(107, 107, 107));
        colorTable.put("gray43", new Color(110, 110, 110));
        colorTable.put("gray44", new Color(112, 112, 112));
        colorTable.put("gray45", new Color(115, 115, 115));
        colorTable.put("gray46", new Color(117, 117, 117));
        colorTable.put("gray47", new Color(120, 120, 120));
        colorTable.put("gray48", new Color(122, 122, 122));
        colorTable.put("gray49", new Color(125, 125, 125));
        colorTable.put("gray5", new Color(13, 13, 13));
        colorTable.put("gray50", new Color(127, 127, 127));
        colorTable.put("gray51", new Color(130, 130, 130));
        colorTable.put("gray52", new Color(133, 133, 133));
        colorTable.put("gray53", new Color(135, 135, 135));
        colorTable.put("gray54", new Color(138, 138, 138));
        colorTable.put("gray55", new Color(140, 140, 140));
        colorTable.put("gray56", new Color(143, 143, 143));
        colorTable.put("gray57", new Color(145, 145, 145));
        colorTable.put("gray58", new Color(148, 148, 148));
        colorTable.put("gray59", new Color(150, 150, 150));
        colorTable.put("gray6", new Color(15, 15, 15));
        colorTable.put("gray60", new Color(153, 153, 153));
        colorTable.put("gray61", new Color(156, 156, 156));
        colorTable.put("gray62", new Color(158, 158, 158));
        colorTable.put("gray63", new Color(161, 161, 161));
        colorTable.put("gray64", new Color(163, 163, 163));
        colorTable.put("gray65", new Color(166, 166, 166));
        colorTable.put("gray66", new Color(168, 168, 168));
        colorTable.put("gray67", new Color(171, 171, 171));
        colorTable.put("gray68", new Color(173, 173, 173));
        colorTable.put("gray69", new Color(176, 176, 176));
        colorTable.put("gray7", new Color(18, 18, 18));
        colorTable.put("gray70", new Color(179, 179, 179));
        colorTable.put("gray71", new Color(181, 181, 181));
        colorTable.put("gray72", new Color(184, 184, 184));
        colorTable.put("gray73", new Color(186, 186, 186));
        colorTable.put("gray74", new Color(189, 189, 189));
        colorTable.put("gray75", new Color(191, 191, 191));
        colorTable.put("gray76", new Color(194, 194, 194));
        colorTable.put("gray77", new Color(196, 196, 196));
        colorTable.put("gray78", new Color(199, 199, 199));
        colorTable.put("gray79", new Color(201, 201, 201));
        colorTable.put("gray8", new Color(20, 20, 20));
        colorTable.put("gray80", new Color(204, 204, 204));
        colorTable.put("gray81", new Color(207, 207, 207));
        colorTable.put("gray82", new Color(209, 209, 209));
        colorTable.put("gray83", new Color(212, 212, 212));
        colorTable.put("gray84", new Color(214, 214, 214));
        colorTable.put("gray85", new Color(217, 217, 217));
        colorTable.put("gray86", new Color(219, 219, 219));
        colorTable.put("gray87", new Color(222, 222, 222));
        colorTable.put("gray88", new Color(224, 224, 224));
        colorTable.put("gray89", new Color(227, 227, 227));
        colorTable.put("gray9", new Color(23, 23, 23));
        colorTable.put("gray90", new Color(229, 229, 229));
        colorTable.put("gray91", new Color(232, 232, 232));
        colorTable.put("gray92", new Color(235, 235, 235));
        colorTable.put("gray93", new Color(237, 237, 237));
        colorTable.put("gray94", new Color(240, 240, 240));
        colorTable.put("gray95", new Color(242, 242, 242));
        colorTable.put("gray96", new Color(245, 245, 245));
        colorTable.put("gray97", new Color(247, 247, 247));
        colorTable.put("gray98", new Color(250, 250, 250));
        colorTable.put("gray99", new Color(252, 252, 252));
        colorTable.put("green yellow", new Color(173, 255, 47));
        colorTable.put("green2", new Color(0, 238, 0));
        colorTable.put("green3", new Color(0, 205, 0));
        colorTable.put("green4", new Color(0, 139, 0));
        colorTable.put("green", new Color(0, 255, 0));
        colorTable.put("greenyellow", new Color(173, 255, 47));
        colorTable.put("grey", new Color(190, 190, 190));
        colorTable.put("grey0", new Color(0, 0, 0));
        colorTable.put("grey1", new Color(3, 3, 3));
        colorTable.put("grey10", new Color(26, 26, 26));
        colorTable.put("grey100", new Color(255, 255, 255));
        colorTable.put("grey11", new Color(28, 28, 28));
        colorTable.put("grey12", new Color(31, 31, 31));
        colorTable.put("grey13", new Color(33, 33, 33));
        colorTable.put("grey14", new Color(36, 36, 36));
        colorTable.put("grey15", new Color(38, 38, 38));
        colorTable.put("grey16", new Color(41, 41, 41));
        colorTable.put("grey17", new Color(43, 43, 43));
        colorTable.put("grey18", new Color(46, 46, 46));
        colorTable.put("grey19", new Color(48, 48, 48));
        colorTable.put("grey2", new Color(5, 5, 5));
        colorTable.put("grey20", new Color(51, 51, 51));
        colorTable.put("grey21", new Color(54, 54, 54));
        colorTable.put("grey22", new Color(56, 56, 56));
        colorTable.put("grey23", new Color(59, 59, 59));
        colorTable.put("grey24", new Color(61, 61, 61));
        colorTable.put("grey25", new Color(64, 64, 64));
        colorTable.put("grey26", new Color(66, 66, 66));
        colorTable.put("grey27", new Color(69, 69, 69));
        colorTable.put("grey28", new Color(71, 71, 71));
        colorTable.put("grey29", new Color(74, 74, 74));
        colorTable.put("grey3", new Color(8, 8, 8));
        colorTable.put("grey30", new Color(77, 77, 77));
        colorTable.put("grey31", new Color(79, 79, 79));
        colorTable.put("grey32", new Color(82, 82, 82));
        colorTable.put("grey33", new Color(84, 84, 84));
        colorTable.put("grey34", new Color(87, 87, 87));
        colorTable.put("grey35", new Color(89, 89, 89));
        colorTable.put("grey36", new Color(92, 92, 92));
        colorTable.put("grey37", new Color(94, 94, 94));
        colorTable.put("grey38", new Color(97, 97, 97));
        colorTable.put("grey39", new Color(99, 99, 99));
        colorTable.put("grey4", new Color(10, 10, 10));
        colorTable.put("grey40", new Color(102, 102, 102));
        colorTable.put("grey41", new Color(105, 105, 105));
        colorTable.put("grey42", new Color(107, 107, 107));
        colorTable.put("grey43", new Color(110, 110, 110));
        colorTable.put("grey44", new Color(112, 112, 112));
        colorTable.put("grey45", new Color(115, 115, 115));
        colorTable.put("grey46", new Color(117, 117, 117));
        colorTable.put("grey47", new Color(120, 120, 120));
        colorTable.put("grey48", new Color(122, 122, 122));
        colorTable.put("grey49", new Color(125, 125, 125));
        colorTable.put("grey5", new Color(13, 13, 13));
        colorTable.put("grey50", new Color(127, 127, 127));
        colorTable.put("grey51", new Color(130, 130, 130));
        colorTable.put("grey52", new Color(133, 133, 133));
        colorTable.put("grey53", new Color(135, 135, 135));
        colorTable.put("grey54", new Color(138, 138, 138));
        colorTable.put("grey55", new Color(140, 140, 140));
        colorTable.put("grey56", new Color(143, 143, 143));
        colorTable.put("grey57", new Color(145, 145, 145));
        colorTable.put("grey58", new Color(148, 148, 148));
        colorTable.put("grey59", new Color(150, 150, 150));
        colorTable.put("grey6", new Color(15, 15, 15));
        colorTable.put("grey60", new Color(153, 153, 153));
        colorTable.put("grey61", new Color(156, 156, 156));
        colorTable.put("grey62", new Color(158, 158, 158));
        colorTable.put("grey63", new Color(161, 161, 161));
        colorTable.put("grey64", new Color(163, 163, 163));
        colorTable.put("grey65", new Color(166, 166, 166));
        colorTable.put("grey66", new Color(168, 168, 168));
        colorTable.put("grey67", new Color(171, 171, 171));
        colorTable.put("grey68", new Color(173, 173, 173));
        colorTable.put("grey69", new Color(176, 176, 176));
        colorTable.put("grey7", new Color(18, 18, 18));
        colorTable.put("grey70", new Color(179, 179, 179));
        colorTable.put("grey71", new Color(181, 181, 181));
        colorTable.put("grey72", new Color(184, 184, 184));
        colorTable.put("grey73", new Color(186, 186, 186));
        colorTable.put("grey74", new Color(189, 189, 189));
        colorTable.put("grey75", new Color(191, 191, 191));
        colorTable.put("grey76", new Color(194, 194, 194));
        colorTable.put("grey77", new Color(196, 196, 196));
        colorTable.put("grey78", new Color(199, 199, 199));
        colorTable.put("grey79", new Color(201, 201, 201));
        colorTable.put("grey8", new Color(20, 20, 20));
        colorTable.put("grey80", new Color(204, 204, 204));
        colorTable.put("grey81", new Color(207, 207, 207));
        colorTable.put("grey82", new Color(209, 209, 209));
        colorTable.put("grey83", new Color(212, 212, 212));
        colorTable.put("grey84", new Color(214, 214, 214));
        colorTable.put("grey85", new Color(217, 217, 217));
        colorTable.put("grey86", new Color(219, 219, 219));
        colorTable.put("grey87", new Color(222, 222, 222));
        colorTable.put("grey88", new Color(224, 224, 224));
        colorTable.put("grey89", new Color(227, 227, 227));
        colorTable.put("grey9", new Color(23, 23, 23));
        colorTable.put("grey90", new Color(229, 229, 229));
        colorTable.put("grey91", new Color(232, 232, 232));
        colorTable.put("grey92", new Color(235, 235, 235));
        colorTable.put("grey93", new Color(237, 237, 237));
        colorTable.put("grey94", new Color(240, 240, 240));
        colorTable.put("grey95", new Color(242, 242, 242));
        colorTable.put("grey96", new Color(245, 245, 245));
        colorTable.put("grey97", new Color(247, 247, 247));
        colorTable.put("grey98", new Color(250, 250, 250));
        colorTable.put("grey99", new Color(252, 252, 252));
        colorTable.put("honeydew", new Color(240, 255, 240));
        colorTable.put("honeydew2", new Color(224, 238, 224));
        colorTable.put("honeydew3", new Color(193, 205, 193));
        colorTable.put("honeydew4", new Color(131, 139, 131));
        colorTable.put("hot pink", new Color(255, 105, 180));
        colorTable.put("hotpink", new Color(255, 105, 180));
        colorTable.put("hotpink1", new Color(255, 110, 180));
        colorTable.put("hotpink2", new Color(238, 106, 167));
        colorTable.put("hotpink3", new Color(205, 96, 144));
        colorTable.put("hotpink4", new Color(139, 58, 98));
        colorTable.put("indian red", new Color(205, 92, 92));
        colorTable.put("indianred", new Color(205, 92, 92));
        colorTable.put("indianred1", new Color(255, 106, 106));
        colorTable.put("indianred2", new Color(238, 99, 99));
        colorTable.put("indianred3", new Color(205, 85, 85));
        colorTable.put("indianred4", new Color(139, 58, 58));
        colorTable.put("ivory", new Color(255, 255, 240));
        colorTable.put("ivory2", new Color(238, 238, 224));
        colorTable.put("ivory3", new Color(205, 205, 193));
        colorTable.put("ivory4", new Color(139, 139, 131));
        colorTable.put("khaki", new Color(240, 230, 140));
        colorTable.put("khaki1", new Color(255, 246, 143));
        colorTable.put("khaki2", new Color(238, 230, 133));
        colorTable.put("khaki3", new Color(205, 198, 115));
        colorTable.put("khaki4", new Color(139, 134, 78));
        colorTable.put("lavender", new Color(230, 230, 250));
        colorTable.put("lavender blush", new Color(255, 240, 245));
        colorTable.put("lavenderblush", new Color(255, 240, 245));
        colorTable.put("lavenderblush2", new Color(238, 224, 229));
        colorTable.put("lavenderblush3", new Color(205, 193, 197));
        colorTable.put("lavenderblush4", new Color(139, 131, 134));
        colorTable.put("lawn green", new Color(124, 252, 0));
        colorTable.put("lawngreen", new Color(124, 252, 0));
        colorTable.put("lemon chiffon", new Color(255, 250, 205));
        colorTable.put("lemonchiffon", new Color(255, 250, 205));
        colorTable.put("lemonchiffon2", new Color(238, 233, 191));
        colorTable.put("lemonchiffon3", new Color(205, 201, 165));
        colorTable.put("lemonchiffon4", new Color(139, 137, 112));
        colorTable.put("light blue", new Color(173, 216, 230));
        colorTable.put("light coral", new Color(240, 128, 128));
        colorTable.put("light cyan", new Color(224, 255, 255));
        colorTable.put("light goldenrod", new Color(238, 221, 130));
        colorTable.put("light goldenrod yellow", new Color(250, 250, 210));
        colorTable.put("light gray", new Color(211, 211, 211));
        colorTable.put("light green", new Color(144, 238, 144));
        colorTable.put("light grey", new Color(211, 211, 211));
        colorTable.put("light pink", new Color(255, 182, 193));
        colorTable.put("light salmon", new Color(255, 160, 122));
        colorTable.put("light sea green", new Color(32, 178, 170));
        colorTable.put("light sky blue", new Color(135, 206, 250));
        colorTable.put("light slate blue", new Color(132, 112, 255));
        colorTable.put("light slate gray", new Color(119, 136, 153));
        colorTable.put("light slate grey", new Color(119, 136, 153));
        colorTable.put("light steel blue", new Color(176, 196, 222));
        colorTable.put("light yellow", new Color(255, 255, 224));
        colorTable.put("lightblue", new Color(173, 216, 230));
        colorTable.put("lightblue1", new Color(191, 239, 255));
        colorTable.put("lightblue2", new Color(178, 223, 238));
        colorTable.put("lightblue3", new Color(154, 192, 205));
        colorTable.put("lightblue4", new Color(104, 131, 139));
        colorTable.put("lightcoral", new Color(240, 128, 128));
        colorTable.put("lightcyan", new Color(224, 255, 255));
        colorTable.put("lightcyan2", new Color(209, 238, 238));
        colorTable.put("lightcyan3", new Color(180, 205, 205));
        colorTable.put("lightcyan4", new Color(122, 139, 139));
        colorTable.put("lightgoldenrod", new Color(238, 221, 130));
        colorTable.put("lightgoldenrod1", new Color(255, 236, 139));
        colorTable.put("lightgoldenrod2", new Color(238, 220, 130));
        colorTable.put("lightgoldenrod3", new Color(205, 190, 112));
        colorTable.put("lightgoldenrod4", new Color(139, 129, 76));
        colorTable.put("lightgoldenrodyellow", new Color(250, 250, 210));
        colorTable.put("lightgray", new Color(211, 211, 211));
        colorTable.put("lightgreen", new Color(144, 238, 144));
        colorTable.put("lightgrey", new Color(211, 211, 211));
        colorTable.put("lightpink", new Color(255, 182, 193));
        colorTable.put("lightpink1", new Color(255, 174, 185));
        colorTable.put("lightpink2", new Color(238, 162, 173));
        colorTable.put("lightpink3", new Color(205, 140, 149));
        colorTable.put("lightpink4", new Color(139, 95, 101));
        colorTable.put("lightsalmon", new Color(255, 160, 122));
        colorTable.put("lightsalmon2", new Color(238, 149, 114));
        colorTable.put("lightsalmon3", new Color(205, 129, 98));
        colorTable.put("lightsalmon4", new Color(139, 87, 66));
        colorTable.put("lightseagreen", new Color(32, 178, 170));
        colorTable.put("lightskyblue", new Color(135, 206, 250));
        colorTable.put("lightskyblue1", new Color(176, 226, 255));
        colorTable.put("lightskyblue2", new Color(164, 211, 238));
        colorTable.put("lightskyblue3", new Color(141, 182, 205));
        colorTable.put("lightskyblue4", new Color(96, 123, 139));
        colorTable.put("lightslateblue", new Color(132, 112, 255));
        colorTable.put("lightslategray", new Color(119, 136, 153));
        colorTable.put("lightslategrey", new Color(119, 136, 153));
        colorTable.put("lightsteelblue", new Color(176, 196, 222));
        colorTable.put("lightsteelblue1", new Color(202, 225, 255));
        colorTable.put("lightsteelblue2", new Color(188, 210, 238));
        colorTable.put("lightsteelblue3", new Color(162, 181, 205));
        colorTable.put("lightsteelblue4", new Color(110, 123, 139));
        colorTable.put("lightyellow", new Color(255, 255, 224));
        colorTable.put("lightyellow2", new Color(238, 238, 209));
        colorTable.put("lightyellow3", new Color(205, 205, 180));
        colorTable.put("lightyellow4", new Color(139, 139, 122));
        colorTable.put("lime green", new Color(50, 205, 50));
        colorTable.put("limegreen", new Color(50, 205, 50));
        colorTable.put("linen", new Color(250, 240, 230));
        colorTable.put("magenta", new Color(255, 0, 255));
        colorTable.put("magenta2", new Color(238, 0, 238));
        colorTable.put("magenta3", new Color(205, 0, 205));
        colorTable.put("magenta4", new Color(139, 0, 139));
        colorTable.put("maroon", new Color(176, 48, 96));
        colorTable.put("maroon1", new Color(255, 52, 179));
        colorTable.put("maroon2", new Color(238, 48, 167));
        colorTable.put("maroon3", new Color(205, 41, 144));
        colorTable.put("maroon4", new Color(139, 28, 98));
        colorTable.put("medium aquamarine", new Color(102, 205, 170));
        colorTable.put("medium blue", new Color(0, 0, 205));
        colorTable.put("medium orchid", new Color(186, 85, 211));
        colorTable.put("medium purple", new Color(147, 112, 219));
        colorTable.put("medium sea green", new Color(60, 179, 113));
        colorTable.put("medium slate blue", new Color(123, 104, 238));
        colorTable.put("medium spring green", new Color(0, 250, 154));
        colorTable.put("medium turquoise", new Color(72, 209, 204));
        colorTable.put("medium violet red", new Color(199, 21, 133));
        colorTable.put("mediumaquamarine", new Color(102, 205, 170));
        colorTable.put("mediumblue", new Color(0, 0, 205));
        colorTable.put("mediumorchid", new Color(186, 85, 211));
        colorTable.put("mediumorchid1", new Color(224, 102, 255));
        colorTable.put("mediumorchid2", new Color(209, 95, 238));
        colorTable.put("mediumorchid3", new Color(180, 82, 205));
        colorTable.put("mediumorchid4", new Color(122, 55, 139));
        colorTable.put("mediumpurple", new Color(147, 112, 219));
        colorTable.put("mediumpurple1", new Color(171, 130, 255));
        colorTable.put("mediumpurple2", new Color(159, 121, 238));
        colorTable.put("mediumpurple3", new Color(137, 104, 205));
        colorTable.put("mediumpurple4", new Color(93, 71, 139));
        colorTable.put("mediumseagreen", new Color(60, 179, 113));
        colorTable.put("mediumslateblue", new Color(123, 104, 238));
        colorTable.put("mediumspringgreen", new Color(0, 250, 154));
        colorTable.put("mediumturquoise", new Color(72, 209, 204));
        colorTable.put("mediumvioletred", new Color(199, 21, 133));
        colorTable.put("midnight blue", new Color(25, 25, 112));
        colorTable.put("midnightblue", new Color(25, 25, 112));
        colorTable.put("mint cream", new Color(245, 255, 250));
        colorTable.put("mintcream", new Color(245, 255, 250));
        colorTable.put("misty rose", new Color(255, 228, 225));
        colorTable.put("mistyrose", new Color(255, 228, 225));
        colorTable.put("mistyrose2", new Color(238, 213, 210));
        colorTable.put("mistyrose3", new Color(205, 183, 181));
        colorTable.put("mistyrose4", new Color(139, 125, 123));
        colorTable.put("moccasin", new Color(255, 228, 181));
        colorTable.put("navajo white", new Color(255, 222, 173));
        colorTable.put("navajowhite", new Color(255, 222, 173));
        colorTable.put("navajowhite2", new Color(238, 207, 161));
        colorTable.put("navajowhite3", new Color(205, 179, 139));
        colorTable.put("navajowhite4", new Color(139, 121, 94));
        colorTable.put("navy", new Color(0, 0, 128));
        colorTable.put("navy blue", new Color(0, 0, 128));
        colorTable.put("navyblue", new Color(0, 0, 128));
        colorTable.put("old lace", new Color(253, 245, 230));
        colorTable.put("oldlace", new Color(253, 245, 230));
        colorTable.put("olive drab", new Color(107, 142, 35));
        colorTable.put("olivedrab", new Color(107, 142, 35));
        colorTable.put("olivedrab1", new Color(192, 255, 62));
        colorTable.put("olivedrab2", new Color(179, 238, 58));
        colorTable.put("olivedrab3", new Color(154, 205, 50));
        colorTable.put("olivedrab4", new Color(105, 139, 34));
        colorTable.put("orange", new Color(255, 165, 0));
        colorTable.put("orange red", new Color(255, 69, 0));
        colorTable.put("orange2", new Color(238, 154, 0));
        colorTable.put("orange3", new Color(205, 133, 0));
        colorTable.put("orange4", new Color(139, 90, 0));
        colorTable.put("orangered", new Color(255, 69, 0));
        colorTable.put("orangered2", new Color(238, 64, 0));
        colorTable.put("orangered3", new Color(205, 55, 0));
        colorTable.put("orangered4", new Color(139, 37, 0));
        colorTable.put("orchid", new Color(218, 112, 214));
        colorTable.put("orchid1", new Color(255, 131, 250));
        colorTable.put("orchid2", new Color(238, 122, 233));
        colorTable.put("orchid3", new Color(205, 105, 201));
        colorTable.put("orchid4", new Color(139, 71, 137));
        colorTable.put("pale goldenrod", new Color(238, 232, 170));
        colorTable.put("pale green", new Color(152, 251, 152));
        colorTable.put("pale turquoise", new Color(175, 238, 238));
        colorTable.put("pale violet red", new Color(219, 112, 147));
        colorTable.put("palegoldenrod", new Color(238, 232, 170));
        colorTable.put("palegreen", new Color(152, 251, 152));
        colorTable.put("palegreen1", new Color(154, 255, 154));
        colorTable.put("palegreen2", new Color(144, 238, 144));
        colorTable.put("palegreen3", new Color(124, 205, 124));
        colorTable.put("palegreen4", new Color(84, 139, 84));
        colorTable.put("paleturquoise", new Color(175, 238, 238));
        colorTable.put("paleturquoise1", new Color(187, 255, 255));
        colorTable.put("paleturquoise2", new Color(174, 238, 238));
        colorTable.put("paleturquoise3", new Color(150, 205, 205));
        colorTable.put("paleturquoise4", new Color(102, 139, 139));
        colorTable.put("palevioletred", new Color(219, 112, 147));
        colorTable.put("palevioletred1", new Color(255, 130, 171));
        colorTable.put("palevioletred2", new Color(238, 121, 159));
        colorTable.put("palevioletred3", new Color(205, 104, 137));
        colorTable.put("palevioletred4", new Color(139, 71, 93));
        colorTable.put("papaya whip", new Color(255, 239, 213));
        colorTable.put("papayawhip", new Color(255, 239, 213));
        colorTable.put("peach puff", new Color(255, 218, 185));
        colorTable.put("peachpuff", new Color(255, 218, 185));
        colorTable.put("peachpuff2", new Color(238, 203, 173));
        colorTable.put("peachpuff3", new Color(205, 175, 149));
        colorTable.put("peachpuff4", new Color(139, 119, 101));
        colorTable.put("peru", new Color(205, 133, 63));
        colorTable.put("pink", new Color(255, 192, 203));
        colorTable.put("pink1", new Color(255, 181, 197));
        colorTable.put("pink2", new Color(238, 169, 184));
        colorTable.put("pink3", new Color(205, 145, 158));
        colorTable.put("pink4", new Color(139, 99, 108));
        colorTable.put("plum", new Color(221, 160, 221));
        colorTable.put("plum1", new Color(255, 187, 255));
        colorTable.put("plum2", new Color(238, 174, 238));
        colorTable.put("plum3", new Color(205, 150, 205));
        colorTable.put("plum4", new Color(139, 102, 139));
        colorTable.put("powder blue", new Color(176, 224, 230));
        colorTable.put("powderblue", new Color(176, 224, 230));
        colorTable.put("purple", new Color(160, 32, 240));
        colorTable.put("purple1", new Color(155, 48, 255));
        colorTable.put("purple2", new Color(145, 44, 238));
        colorTable.put("purple3", new Color(125, 38, 205));
        colorTable.put("purple4", new Color(85, 26, 139));
        colorTable.put("red", new Color(255, 0, 0));
        colorTable.put("red2", new Color(238, 0, 0));
        colorTable.put("red3", new Color(205, 0, 0));
        colorTable.put("red4", new Color(139, 0, 0));
        colorTable.put("rosy brown", new Color(188, 143, 143));
        colorTable.put("rosybrown", new Color(188, 143, 143));
        colorTable.put("rosybrown1", new Color(255, 193, 193));
        colorTable.put("rosybrown2", new Color(238, 180, 180));
        colorTable.put("rosybrown3", new Color(205, 155, 155));
        colorTable.put("rosybrown4", new Color(139, 105, 105));
        colorTable.put("royal blue", new Color(65, 105, 225));
        colorTable.put("royalblue", new Color(65, 105, 225));
        colorTable.put("royalblue1", new Color(72, 118, 255));
        colorTable.put("royalblue2", new Color(67, 110, 238));
        colorTable.put("royalblue3", new Color(58, 95, 205));
        colorTable.put("royalblue4", new Color(39, 64, 139));
        colorTable.put("saddle brown", new Color(139, 69, 19));
        colorTable.put("saddlebrown", new Color(139, 69, 19));
        colorTable.put("salmon", new Color(250, 128, 114));
        colorTable.put("salmon1", new Color(255, 140, 105));
        colorTable.put("salmon2", new Color(238, 130, 98));
        colorTable.put("salmon3", new Color(205, 112, 84));
        colorTable.put("salmon4", new Color(139, 76, 57));
        colorTable.put("sandy brown", new Color(244, 164, 96));
        colorTable.put("sandybrown", new Color(244, 164, 96));
        colorTable.put("sea green", new Color(46, 139, 87));
        colorTable.put("seagreen", new Color(46, 139, 87));
        colorTable.put("seagreen1", new Color(84, 255, 159));
        colorTable.put("seagreen2", new Color(78, 238, 148));
        colorTable.put("seagreen3", new Color(67, 205, 128));
        colorTable.put("seagreen4", new Color(46, 139, 87));
        colorTable.put("seashell", new Color(255, 245, 238));
        colorTable.put("seashell2", new Color(238, 229, 222));
        colorTable.put("seashell3", new Color(205, 197, 191));
        colorTable.put("seashell4", new Color(139, 134, 130));
        colorTable.put("sienna", new Color(160, 82, 45));
        colorTable.put("sienna1", new Color(255, 130, 71));
        colorTable.put("sienna2", new Color(238, 121, 66));
        colorTable.put("sienna3", new Color(205, 104, 57));
        colorTable.put("sienna4", new Color(139, 71, 38));
        colorTable.put("sky blue", new Color(135, 206, 235));
        colorTable.put("skyblue", new Color(135, 206, 235));
        colorTable.put("skyblue1", new Color(135, 206, 255));
        colorTable.put("skyblue2", new Color(126, 192, 238));
        colorTable.put("skyblue3", new Color(108, 166, 205));
        colorTable.put("skyblue4", new Color(74, 112, 139));
        colorTable.put("slate blue", new Color(106, 90, 205));
        colorTable.put("slate gray", new Color(112, 128, 144));
        colorTable.put("slate grey", new Color(112, 128, 144));
        colorTable.put("slateblue", new Color(106, 90, 205));
        colorTable.put("slateblue1", new Color(131, 111, 255));
        colorTable.put("slateblue2", new Color(122, 103, 238));
        colorTable.put("slateblue3", new Color(105, 89, 205));
        colorTable.put("slateblue4", new Color(71, 60, 139));
        colorTable.put("slategray", new Color(112, 128, 144));
        colorTable.put("slategray1", new Color(198, 226, 255));
        colorTable.put("slategray2", new Color(185, 211, 238));
        colorTable.put("slategray3", new Color(159, 182, 205));
        colorTable.put("slategray4", new Color(108, 123, 139));
        colorTable.put("slategrey", new Color(112, 128, 144));
        colorTable.put("snow", new Color(255, 250, 250));
        colorTable.put("snow2", new Color(238, 233, 233));
        colorTable.put("snow3", new Color(205, 201, 201));
        colorTable.put("snow4", new Color(139, 137, 137));
        colorTable.put("spring green", new Color(0, 255, 127));
        colorTable.put("springgreen", new Color(0, 255, 127));
        colorTable.put("springgreen2", new Color(0, 238, 118));
        colorTable.put("springgreen3", new Color(0, 205, 102));
        colorTable.put("springgreen4", new Color(0, 139, 69));
        colorTable.put("steel blue", new Color(70, 130, 180));
        colorTable.put("steelblue", new Color(70, 130, 180));
        colorTable.put("steelblue1", new Color(99, 184, 255));
        colorTable.put("steelblue2", new Color(92, 172, 238));
        colorTable.put("steelblue3", new Color(79, 148, 205));
        colorTable.put("steelblue4", new Color(54, 100, 139));
        colorTable.put("tan", new Color(210, 180, 140));
        colorTable.put("tan1", new Color(255, 165, 79));
        colorTable.put("tan2", new Color(238, 154, 73));
        colorTable.put("tan3", new Color(205, 133, 63));
        colorTable.put("tan4", new Color(139, 90, 43));
        colorTable.put("thistle", new Color(216, 191, 216));
        colorTable.put("thistle1", new Color(255, 225, 255));
        colorTable.put("thistle2", new Color(238, 210, 238));
        colorTable.put("thistle3", new Color(205, 181, 205));
        colorTable.put("thistle4", new Color(139, 123, 139));
        colorTable.put("tomato", new Color(255, 99, 71));
        colorTable.put("tomato2", new Color(238, 92, 66));
        colorTable.put("tomato3", new Color(205, 79, 57));
        colorTable.put("tomato4", new Color(139, 54, 38));
        colorTable.put("turquoise", new Color(64, 224, 208));
        colorTable.put("turquoise1", new Color(0, 245, 255));
        colorTable.put("turquoise2", new Color(0, 229, 238));
        colorTable.put("turquoise3", new Color(0, 197, 205));
        colorTable.put("turquoise4", new Color(0, 134, 139));
        colorTable.put("violet", new Color(238, 130, 238));
        colorTable.put("violet red", new Color(208, 32, 144));
        colorTable.put("violetred", new Color(208, 32, 144));
        colorTable.put("violetred1", new Color(255, 62, 150));
        colorTable.put("violetred2", new Color(238, 58, 140));
        colorTable.put("violetred3", new Color(205, 50, 120));
        colorTable.put("violetred4", new Color(139, 34, 82));
        colorTable.put("wheat", new Color(245, 222, 179));
        colorTable.put("wheat1", new Color(255, 231, 186));
        colorTable.put("wheat2", new Color(238, 216, 174));
        colorTable.put("wheat3", new Color(205, 186, 150));
        colorTable.put("wheat4", new Color(139, 126, 102));
        colorTable.put("white", new Color(255, 255, 255));
        colorTable.put("white smoke", new Color(245, 245, 245));
        colorTable.put("whitesmoke", new Color(245, 245, 245));
        colorTable.put("yellow", new Color(255, 255, 0));
        colorTable.put("yellow green", new Color(154, 205, 50));
        colorTable.put("yellow2", new Color(238, 238, 0));
        colorTable.put("yellow3", new Color(205, 205, 0));
        colorTable.put("yellow4", new Color(139, 139, 0));
        colorTable.put("yellowgreen", new Color(154, 205, 50));

        Enumeration e = colorTable.keys();
        Color color = null;
        String keyName = null;

        while (e.hasMoreElements()) {
            keyName = (String) e.nextElement();

            if (!keyName.equals("grey0") && !keyName.equals("gray0")) {
                color = (Color) colorTable.get(keyName);
                iColorTable.put(color, keyName);
            }
        }
    }

    /**
     *
     * @param comp
     * @param tkRelief
     */
    public static void updateRelief(JComponent comp, String tkRelief) {
        int width = 3;
        comp.setBorder(new SwkBorder());

        /*      if (tkRelief.startsWith("rai")) {
        comp.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createCompoundBorder(
        BorderFactory.createBevelBorder(BevelBorder.RAISED),
        BorderFactory.createEmptyBorder(width,width,width,width)),
        BorderFactory.createLineBorder(Color.red,1)));
        }
         */
    }

    /**
     *
     * @param c
     * @return
     */
    public static boolean disableDoubleBuffering(Component c) {
        if (c instanceof JComponent == false) {
            return false;
        }

        JComponent jc = (JComponent) c;
        boolean wasBuffered = jc.isDoubleBuffered();
        jc.setDoubleBuffered(false);

        return wasBuffered;
    }

    /**
     *
     * @param c
     * @param wasBuffered
     */
    public static void restoreDoubleBuffering(Component c, boolean wasBuffered) {
        if (c instanceof JComponent) {
            ((JComponent) c).setDoubleBuffered(wasBuffered);
        }
    }

    /**
     *
     * @param s
     * @return
     */
    static public boolean looksLikeInt(String s) {
        return intPattern.matcher(s).matches();
    }

    /**
     *
     * @param argv
     * @param start
     * @return
     */
    static public String[] argvToStrings(TclObject[] argv, int start) {
        String[] strings = new String[argv.length - start];

        for (int i = start; i < argv.length; i++) {
            strings[i - start] = argv[i].toString();
        }

        return strings;
    }

    /**
     *
     * @param interp
     * @param aList
     * @return
     * @throws TclException
     */
    static public TclObject arrayToList(Interp interp, ArrayList aList)
            throws TclException {
        TclObject list = TclList.newInstance();

        if (aList != null) {
            for (int j = 0; j < aList.size(); j++) {
                String value = (String) aList.get(j);
                TclList.append(interp, list, TclString.newInstance(value));
            }
        }

        return list;
    }

    /**
     *
     */
    public static void doWait() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                }
            });
        } catch (Exception e) {
        }
    }

    /**
     *
     * @param comp
     * @param target
     */
    public static void setJHelpTarget(Component comp, String target) {
        if (hasJHelp == null) {
            try {
                Class helpClass = Class.forName("com.onemoonscientific.swank.jhelp.SwkJHelpCmd");
                helpobject = helpClass.newInstance();
                helpmethod = helpClass.getMethod("setTarget", new Class[]{Component.class, String.class});

                hasJHelp = Boolean.valueOf(true);
                System.out.println("class does exist");
            } catch (Exception e) {
                hasJHelp =  Boolean.valueOf(false);
                System.out.println("class doesn't exist");
            }
        }
        if (hasJHelp) {
            try {
                helpmethod.invoke(helpobject, comp, target);
            } catch (Exception e) {
            }
        }

    }

    private static Color parseHexColor(String colorName) {
        int len = colorName.length() - 1;
        int r;
        int g;
        int b;
        int a = 255;
        try {
            if (len == 3) {
                r = Integer.parseInt(colorName.substring(1, 2), 16) * 16;
                g = Integer.parseInt(colorName.substring(2, 3), 16) * 16;
                b = Integer.parseInt(colorName.substring(3, 4), 16) * 16;
            } else if (len == 4) {
                a = Integer.parseInt(colorName.substring(1, 2), 16) * 16;
                r = Integer.parseInt(colorName.substring(2, 3), 16) * 16;
                g = Integer.parseInt(colorName.substring(3, 4), 16) * 16;
                b = Integer.parseInt(colorName.substring(4, 5), 16) * 16;
            } else if (len == 6) {
                r = Integer.parseInt(colorName.substring(1, 3), 16);
                g = Integer.parseInt(colorName.substring(3, 5), 16);
                b = Integer.parseInt(colorName.substring(5, 7), 16);
            } else if (len == 8) {
                a = Integer.parseInt(colorName.substring(1, 3), 16);
                r = Integer.parseInt(colorName.substring(3, 5), 16);
                g = Integer.parseInt(colorName.substring(5, 7), 16);
                b = Integer.parseInt(colorName.substring(7, 9), 16);
            } else if (len == 9) {
                r = Integer.parseInt(colorName.substring(1, 4), 16) / 16;
                g = Integer.parseInt(colorName.substring(4, 7), 16) / 16;
                b = Integer.parseInt(colorName.substring(7, 10), 16) / 16;
            } else if (len == 12) {
                a = Integer.parseInt(colorName.substring(1, 4), 16) / 16;
                r = Integer.parseInt(colorName.substring(4, 7), 16) / 16;
                g = Integer.parseInt(colorName.substring(7, 10), 16) / 16;
                b = Integer.parseInt(colorName.substring(10, 13), 16) / 16;
            } else {
                return null;
            }
        } catch (NumberFormatException nFE) {
            return null;
        }

        return new Color(r, g, b, a);
    }
}

