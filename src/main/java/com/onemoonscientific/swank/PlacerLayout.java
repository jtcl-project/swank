/*
 * PlacerLayout.java
 *
 * Copyright (c) 2004 Bruce Johnson, One Moon Scientific.
 * Copyright (c) 2000 Mo DeJong, Red Hat, Inc.
 * Copyright (c) 1998 Mo DeJong, U of MN
 * Copyright (c) 1997 Daeron Meyer, U of MN
 * Copyright (c) 1996 Sun Microsystems, Inc.
 *
 * Modified by BAJ from PackerLayout
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: PlacerLayout.java,v 1.2 2005/11/07 03:20:21 bruce_johnson Exp $
 *
 */
/**
 * PlacerLayout is used to lay out widget components.
 *
 * This layout manager is easy to use, as well as memory and speed tuned.
 * The layout manager parses up command options like -fill x -expand true
 * and does the correct layout management based on Tk's place layout.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import java.awt.*;

import java.lang.*;

import java.util.*;

public class PlacerLayout implements LayoutManager {
    //this hashtable will do the mapping between
    //options and the actual option object

    private static Hashtable option_table;
    //this hashtable will do the mapping between
    //option arguments and the option that they
    //are valid for
    private static Hashtable value_table;
    //if the option_table hash returns a ref to the
    //INT_MAP object then we know that this option
    //just takes an integer
    //if the option and the value args for two strings
    //both refer to the same object then the option
    //pair is valid and can be added to the packtable
    //finally a lookup is done on the value_object_table
    //when we know that both the option and the value
    //are correct. This lookup gives us our Object ref
    //for the value in question (not used for int options)
    private static Hashtable value_object_table;
    private static final String OPT_ANCHOR = "-anchor";
    private static final String OPT_BORDERMODE = "-bordermode";
    private static final String OPT_X = "-x";
    private static final String OPT_Y = "-y";
    private static final String OPT_WIDTH = "-width";
    private static final String OPT_HEIGHT = "-height";
    private static final String OPT_RELX = "-relx";
    private static final String OPT_RELY = "-rely";
    private static final String OPT_RELWIDTH = "-relwidth";
    private static final String OPT_RELHEIGHT = "-relheight";
    private static final String BORDERMODE_OPT_INSIDE = "inside";
    private static final String BORDERMODE_OPT_OUTSIDE = "outside";
    private static final String BORDERMODE_OPT_IGNORE = "ignore";
    private static final String ANCHOR_OPT_N = "n";
    private static final String ANCHOR_OPT_NE = "ne";
    private static final String ANCHOR_OPT_E = "e";
    private static final String ANCHOR_OPT_SE = "se";
    private static final String ANCHOR_OPT_S = "s";
    private static final String ANCHOR_OPT_SW = "sw";
    private static final String ANCHOR_OPT_W = "w";
    private static final String ANCHOR_OPT_NW = "nw"; // default value
    private static final String ANCHOR_OPT_C = "c";
    private static final Object BORDERMODE_OBJ_INSIDE = new Object();
    private static final Object BORDERMODE_OBJ_OUTSIDE = new Object();
    private static final Object BORDERMODE_OBJ_IGNORE = new Object();
    private static final Object ANCHOR_OBJ_N = new Object();
    private static final Object ANCHOR_OBJ_NE = new Object();
    private static final Object ANCHOR_OBJ_E = new Object();
    private static final Object ANCHOR_OBJ_SE = new Object();
    private static final Object ANCHOR_OBJ_S = new Object();
    private static final Object ANCHOR_OBJ_SW = new Object();
    private static final Object ANCHOR_OBJ_W = new Object();
    private static final Object ANCHOR_OBJ_NW = new Object(); //default value
    private static final Object ANCHOR_OBJ_C = new Object();
    private static final Object DEFAULT_ANCHOR = ANCHOR_OBJ_NW;
    private static final Object DEFAULT_BORDERMODE = BORDERMODE_OBJ_INSIDE;
    private static final int DEFAULT_X = 0;
    private static final int DEFAULT_Y = 0;
    private static final int DEFAULT_WIDTH = 0;
    private static final int DEFAULT_HEIGHT = 0;
    private static final float DEFAULT_RELX = 0;
    private static final float DEFAULT_RELY = 0;
    private static final float DEFAULT_RELWIDTH = 0;
    private static final float DEFAULT_RELHEIGHT = 0;
    //place holder for mapping int values to options
    private static final Object INT_MAP = new Object();
    //place holder for mapping double values to options
    private static final Object DOUBLE_MAP = new Object();
    //this exception is throws if the number that is passes in on
    //number options is less than zero
    private static final NumberFormatException NFE = new NumberFormatException();
    //place holder for mapping boolean values to options
    private static final Object BOOLEAN_MAP = new Object();
    //place holder for mapping Tk size values to options
    private static final Object SIZE_MAP = new Object();
    //this dimension will be returned from our class in calls to
    //minimumLayoutSize. It is allocated once here so that it does
    //not have to get allocated every time minimumLayoutSize is called
    private static final Dimension RetDimension = new Dimension(0, 0);

    //in the init block we create the entries for the
    //mapping of option to valid args array, we use the
    //static hash table
    static {
        option_table = new Hashtable(17);
        value_table = new Hashtable(17);
        value_object_table = new Hashtable(17);

        //-anchor option
        //map option string to option object
        option_table.put(OPT_ANCHOR, OPT_ANCHOR);
        option_table.put(OPT_BORDERMODE, OPT_BORDERMODE);

        //map option args to the option object
        value_table.put(BORDERMODE_OPT_INSIDE, OPT_BORDERMODE);
        value_table.put(BORDERMODE_OPT_OUTSIDE, OPT_BORDERMODE);
        value_table.put(BORDERMODE_OPT_IGNORE, OPT_BORDERMODE);

        //map option args to the option object
        value_table.put(ANCHOR_OPT_N, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_NE, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_E, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_SE, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_S, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_SW, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_W, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_NW, OPT_ANCHOR);
        value_table.put(ANCHOR_OPT_C, OPT_ANCHOR);

        //map option value to option value object
        value_object_table.put(ANCHOR_OPT_N, ANCHOR_OBJ_N);
        value_object_table.put(ANCHOR_OPT_NE, ANCHOR_OBJ_NE);
        value_object_table.put(ANCHOR_OPT_E, ANCHOR_OBJ_NE);
        value_object_table.put(ANCHOR_OPT_SE, ANCHOR_OBJ_SE);
        value_object_table.put(ANCHOR_OPT_S, ANCHOR_OBJ_S);
        value_object_table.put(ANCHOR_OPT_SW, ANCHOR_OBJ_SW);
        value_object_table.put(ANCHOR_OPT_W, ANCHOR_OBJ_W);
        value_object_table.put(ANCHOR_OPT_NW, ANCHOR_OBJ_NW);
        value_object_table.put(ANCHOR_OPT_C, ANCHOR_OBJ_C);

        //map option value to option value object
        value_object_table.put(BORDERMODE_OPT_INSIDE, BORDERMODE_OBJ_INSIDE);
        value_object_table.put(BORDERMODE_OPT_OUTSIDE, BORDERMODE_OBJ_OUTSIDE);
        value_object_table.put(BORDERMODE_OPT_IGNORE, BORDERMODE_OBJ_IGNORE);

        //-x, -y, -width, and -height options
        //map from option string to the
        //SIZE_MAP place holder for options that
        //take size arguments
        option_table.put(OPT_X, SIZE_MAP);
        option_table.put(OPT_Y, SIZE_MAP);
        option_table.put(OPT_WIDTH, SIZE_MAP);
        option_table.put(OPT_HEIGHT, SIZE_MAP);

        //map option string to the option object
        value_table.put(OPT_X, OPT_X);
        value_table.put(OPT_Y, OPT_Y);
        value_table.put(OPT_WIDTH, OPT_WIDTH);
        value_table.put(OPT_HEIGHT, OPT_HEIGHT);

        //-relx, -rely, -relwidth, and -relheight options
        //map from option string to the
        //DOUBLE_MAP place holder for options that
        //take double arguments
        option_table.put(OPT_RELX, DOUBLE_MAP);
        option_table.put(OPT_RELY, DOUBLE_MAP);
        option_table.put(OPT_RELWIDTH, DOUBLE_MAP);
        option_table.put(OPT_RELHEIGHT, DOUBLE_MAP);

        //map option string to the option object
        value_table.put(OPT_RELX, OPT_RELX);
        value_table.put(OPT_RELY, OPT_RELY);
        value_table.put(OPT_RELWIDTH, OPT_RELWIDTH);
        value_table.put(OPT_RELHEIGHT, OPT_RELHEIGHT);
    }
    private Hashtable component_table;
    private Component firstcomp;
    private Component lastcomp;
    // This is kind of wacky, but there does not seem to
    // be any way to "update" the options of a component
    // in a Java layout manager. To implement "update",
    // we just call add() again except that we set
    // the special ignore_next_remove flag. Then on
    // the next call to add(), removeLayoutComponent()
    // will be invoked but we will not actually remove
    // the component. When addLayoutComponent() is called
    // we can then update the component's pack record.
    private boolean ignore_next_remove = false;
    Interp interp = null;

    /**
     * Constructs a new Placer Layout.
     */
    public PlacerLayout(Interp interp) {
        this.interp = interp;
        component_table = new Hashtable();
        firstcomp = null;
        lastcomp = null;
    }

    //this helper method is used to split up an input string
    //based on a single splitchar. It returns an array of strings.
    // split("one two  three", ' ') -> {"one", "two", "three"}
    private static String[] split(String in, char splitchar) {
        // first we copy the contents of the string into
        // an array for quick processing
        int i;

        // create an array that is as big as the input
        // str plus one for an extra split char
        int len = in.length();
        char[] str = new char[len + 1];
        in.getChars(0, len, str, 0);
        str[len++] = splitchar;

        int wordstart = 0;

        // make a vector that will hold our elements
        Vector words = new Vector(3);

        //for (i=0; i < len; i++) {
        //  System.out.println(str[i] + " : " + i );
        //}
        for (i = 0; i < len; i++) {
            // compare this char to the split char
            // if they are the same the we need to
            // add the last word to the array
            if (str[i] == splitchar) {
                //System.out.println("split char found at " + i);
                if (wordstart <= (i - 1)) {
                    words.addElement(new String(str, wordstart, i - wordstart));
                }

                wordstart = i + 1;
            }
        }

        // create an array that is as big as the number
        // of elements in the vector and copy over and return
        String[] ret = new String[words.size()];
        words.copyInto(ret);

        return ret;
    }

    public boolean updateLayoutComponent(String spec, Component comp) {
        PlaceRecord pr = (PlaceRecord) component_table.get(comp);

        if (pr == null) {
            return false;
        } else {
            addLayoutComponent(spec, comp);

            return true;
        }
    }

    /**
     * Adds the specified component to the layout.
     * @param name information about attachments
     * @param comp the the component to be added
     */
    public void addLayoutComponent(String spec, Component comp) {
        int i;
        int max;

        if (comp == null) {
            return;
        }

        boolean new_record = false;

        PlaceRecord pr = (PlaceRecord) component_table.get(comp);

        if (pr == null) {
            pr = new PlaceRecord();
            new_record = true;

            //System.out.println("adding new record");
        }

        //System.out.println("input str is \"" + spec + "\"");
        TclObject[] argv = null;

        try {
            argv = TclList.getElements(interp, TclString.newInstance(spec));
        } catch (TclException tclE) {
            return;
        }

        //String[] args = split (spec, ' ');
        //in the default case we will not add an element
        //to the hash table, only if an argument is given will
        //an element be added to the hash table
        if ((argv.length % 2) != 0) {
            throw new PlaceingException(
                    "Fatal error in PlacerLayout string spec, must have even number of arguments");
        }

        max = argv.length;

        Object option;
        Object value;

        for (i = 0; i < max; i += 2) {
            /*
            System.out.println("trying to match the pair");
            System.out.println(argv[i].toString());
            System.out.println(argv[i+1].toString());
             */

            //first match the input string option
            option = option_table.get(argv[i].toString());

            //see if the option returned is the INT_MAP
            //placeholder for options that take an int
            if (option == INT_MAP) {
                //if true then we know that the value must be
                //an integer
                if (argv[i + 1].toString().trim().length() > 0) {
                    int num;

                    try {
                        num = Integer.parseInt(argv[i + 1].toString());

                        if (num < 0) {
                            throw NFE;
                        }
                    } catch (NumberFormatException e) {
                        throw new PlaceingException("error : value of the "
                                + argv[i].toString() + " option must be an integer");
                    }
                }
            } else if (option == DOUBLE_MAP) {
                //if true then we know that the value must be
                //a double
                float fVal = 0.0f;
                boolean gotValue = false;

                if (argv[i + 1].toString().trim().length() > 0) {
                    try {
                        fVal = (float) Double.parseDouble(argv[i + 1].toString());
                        gotValue = true;
                    } catch (NumberFormatException e) {
                        throw new PlaceingException("error : value of the "
                                + argv[i].toString() + " option must be a double");
                    }
                }

                //this is an double option so we need to find out which
                //record member we will assign the double to
                value = value_table.get(argv[i].toString());
                ;

                //temp check for fatal case
                if (value == null) {
                    String str =
                            "null value object for double parser on option \""
                            + argv[i].toString() + "\" : \""
                            + argv[i + 1].toString() + "\"";

                    throw new RuntimeException(str);

                    /*
                    System.out.println( str );
                    System.exit(-1);
                     */
                }

                if (value == OPT_RELX) {
                    pr.relx = fVal;
                } else if (value == OPT_RELY) {
                    pr.rely = fVal;
                } else if (value == OPT_RELWIDTH) {
                    if (gotValue) {
                        pr.relwidth = fVal;
                        pr.relWidthActive = true;
                    } else {
                        pr.relwidth = 0.0f;
                        pr.relWidthActive = false;
                    }
                } else if (value == OPT_RELHEIGHT) {
                    if (gotValue) {
                        pr.relheight = fVal;
                        pr.relHeightActive = true;
                    } else {
                        pr.relheight = 0.0f;
                        pr.relHeightActive = false;
                    }
                } else {
                    throw new RuntimeException("fatal : bad branch");
                }
            } else if (option == SIZE_MAP) {
                int num = 0;
                boolean gotValue = false;

                if (argv[i + 1].toString().trim().length() > 0) {
                    try {
                        num = SwankUtil.getTkSize(interp, comp, argv[i + 1]);
                        gotValue = true;
                    } catch (TclException tclE) {
                        return;
                    }
                }

                //this is an int option so we need to find out which
                //record member we will assign the int to
                value = value_table.get(argv[i].toString());
                ;

                //temp check for fatal case
                if (value == null) {
                    String str = "null value object for int parser on option \""
                            + argv[i].toString() + "\" : \""
                            + argv[i + 1].toString() + "\"";

                    throw new RuntimeException(str);

                    /*
                    System.out.println( str );
                    System.exit(-1);
                     */
                }

                if (value == OPT_X) {
                    pr.x = num;
                } else if (value == OPT_Y) {
                    pr.y = num;
                } else if (value == OPT_WIDTH) {
                    if (gotValue) {
                        pr.width = num;
                        pr.widthActive = true;
                    } else {
                        pr.width = 0;
                        pr.widthActive = false;
                    }
                } else if (value == OPT_HEIGHT) {
                    if (gotValue) {
                        pr.height = num;
                        pr.heightActive = true;
                    } else {
                        pr.height = 0;
                        pr.heightActive = false;
                    }
                } else {
                    throw new RuntimeException("fatal : bad branch");
                }
            } else if (option == BOOLEAN_MAP) {
                //if true then we know that the value must be
                //a boolean value
                boolean bool;
                String s = argv[i + 1].toString();

                if (s.length() > 0) {
                    if ("yes".startsWith(s)) {
                        bool = true;
                    } else if ("no".startsWith(s)) {
                        bool = false;
                    } else if ("true".startsWith(s)) {
                        bool = true;
                    } else if ("false".startsWith(s)) {
                        bool = false;
                    } else if ("on".startsWith(s) && (s.length() > 1)) {
                        bool = true;
                    } else if ("off".startsWith(s) && (s.length() > 1)) {
                        bool = false;
                    } else if (s.equals("0")) {
                        bool = false;
                    } else if (s.equals("1")) {
                        bool = true;
                    } else {
                        throw new PlaceingException("error : value of the "
                                + argv[i].toString() + " option must be a boolean");
                    }
                } else {
                    throw new PlaceingException("error : value of the "
                            + argv[i].toString() + " option must be a boolean");
                }

                //this is an int option so we need to find out which
                //record member we will assign the int to
                value = value_table.get(argv[i].toString());
                ;

                //temp check for fatal case
                if (value == null) {
                    String str = "null value object for int parser on option \""
                            + argv[i].toString() + "\" : \""
                            + argv[i + 1].toString() + "\"";

                    throw new RuntimeException(str);

                    /*
                    System.out.println( str );
                    System.exit(-1);
                     */
                }

                throw new RuntimeException("fatal : bad branch");
            } else if (option == null) {
                //this is an error becuase the option
                //that was just given does not match
                //any of the predefined option types
                // unknown or ambiguous option "-foll": must be -after, -anchor, -before, -expand, -fill, -in, -ipadx, -ipady, -padx, -pady, or -side
                throw new PlaceingException("error : option \""
                        + argv[i].toString() + "\" does not match any of "
                        + OPT_ANCHOR + " " + OPT_BORDERMODE + " " + OPT_X + " "
                        + OPT_Y + " " + OPT_WIDTH + " " + OPT_HEIGHT + " "
                        + OPT_RELX + " " + OPT_RELY + " " + OPT_RELWIDTH + " "
                        + OPT_RELHEIGHT);
            } else if (option != value_table.get(argv[i + 1].toString())) {
                //in this case the given value for the option
                //did not match one of the possible option values
                throw new PlaceingException("error : option \""
                        + argv[i].toString() + "\" can not take the value \""
                        + argv[i + 1].toString() + "\"");
            } else {
                //if no other conditions are true then we
                //must have matched both the option and the value
                //so we can add the option string and map it to
                //the value object in the value_object_table
                value = value_object_table.get(argv[i + 1].toString());

                //temp check
                if (value == null) {
                    String str =
                            "null value object for option parser on option \""
                            + argv[i].toString() + "\" : \""
                            + argv[i + 1].toString() + "\"";

                    throw new RuntimeException(str);
                }

                //now we need to find out which field in our pack record
                //we need to assign this object to
                if (option == OPT_ANCHOR) {
                    pr.anchor = value;
                } else if (option == OPT_BORDERMODE) {
                    pr.bordermode = value;
                } else {
                    throw new RuntimeException("fatal : bad branch");
                }
            }
        }

        // If we created a new record, add a mapping from the component
        // to the pack record to our component_table
        if (new_record) {
            component_table.put(comp, pr);

            //System.out.println("put pack record in table");
            // now add the component to the display list
            if (firstcomp == null) {
                firstcomp = comp;
                lastcomp = comp;
            } else {
                PlaceRecord lpr = (PlaceRecord) component_table.get(lastcomp);
                lpr.next = comp;
                pr.prev = lastcomp;
                lastcomp = comp;
            }
        }
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component to remove
     */
    public void removeLayoutComponent(Component comp) {
        // Wacky "update" workaround, see above for comments
        if (ignore_next_remove) {
            ignore_next_remove = false;

            return;
        }

        PlaceRecord pr = (PlaceRecord) component_table.get(comp);
        Component prev = pr.prev;
        Component next = pr.next;

        if (prev == null) { // we are removing the first component
            firstcomp = next;
        } else { //remove next pointer from prev component
            pr = (PlaceRecord) component_table.get(prev);
            pr.next = next;
        }

        if (next == null) { // we are removing the last component
            lastcomp = prev;
        } else { //remove prev pointer from next component
            pr = (PlaceRecord) component_table.get(next);
            pr.prev = prev;
        }

        //remove this component from the component table
        // System.out.println ("removing pack record from table");
        component_table.remove(comp);

        return;
    }

    /**
     * Returns the preferred dimensions for this layout given the
     * components in the specified target container.
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumSize
     */
    public Dimension preferredLayoutSize(Container target) {
        Dimension dim = minimumLayoutSize(target);
        Dimension cdim = target.getSize();

        //  System.out.println("min "+target.getName()+" "+dim.toString());
        // System.out.println("target "+target.getName()+" "+cdim.toString());
        if (cdim.width < dim.width) {
            cdim.width = dim.width;
        }

        if (cdim.height < dim.height) {
            cdim.height = dim.height;
        }

        // System.out.println("target "+target.getName()+" "+cdim.toString());
        return cdim;
    }

    /**
     * Returns the minimum dimensions needed to layout the
     * components contained in the specified target container.
     * @param target the component which needs to be laid out
     * @see #preferredSize
     */
    public Dimension minimumLayoutSize(Container target) {
        int i;
        Insets insets = target.getInsets();
        Dimension cdim = target.getSize();
        int dim_width = 0;
        int dim_height = 0;
        Dimension d;

        //System.out.println("minimumLayout for "+Widgets.getContainer(target).getName());
        // Don't use RetDimension, nested calls are screwed up otherwise
        //Dimension dmax = RetDimension;
        Dimension dmax = new Dimension(0, 0);
        dmax.width = 0;
        dmax.height = 0;

        int nmembers = target.getComponentCount();
        PlaceRecord pr;

        //System.out.println("target "+target.getName());
        for (i = 0; i < nmembers; i++) {
            Component m = target.getComponent(i);

            //System.out.println("comp "+m.getName());
            if (m.getParent() != Widgets.getContainer(target)) {
                continue;
            }

            //d = m.getMinimumSize ();
            d = target.getSize();

            //System.out.println("minSize for comp is "+d.toString());
            //System.out.println("cur Max for comp is "+dmax.toString());
            pr = (PlaceRecord) component_table.get(m);

            if (pr == null) {
                throw new RuntimeException("null PlaceRecord");

                //break;
            }

            int borderWidth;
            int borderHeight;
            int borderX;
            int borderY;

            if (pr.bordermode == BORDERMODE_OBJ_INSIDE) {
                borderWidth = insets.left + insets.right;
                borderHeight = insets.top + insets.bottom;
                borderX = insets.left;
                borderY = insets.top;
            } else if (pr.bordermode == BORDERMODE_OBJ_OUTSIDE) {
                borderWidth = -(insets.left + insets.right);
                borderHeight = -(insets.top + insets.bottom);
                borderX = -insets.left;
                borderY = -insets.top;
            } else {
                borderWidth = 0;
                borderHeight = 0;
                borderX = 0;
                borderY = 0;
            }

            int width = (int) (pr.width
                    + (pr.relwidth * (d.width - borderWidth)));
            int height = (int) (pr.height
                    + (pr.relheight * (d.height - borderHeight)));

            int x = (int) (pr.x + (pr.relx * (d.width - borderWidth)));
            int y = (int) (pr.y + (pr.rely * (d.height - borderHeight)));
            x = x + getDelta(pr.anchor, true, width) + borderX;
            y = y + getDelta(pr.anchor, false, height) + borderY;

            if (d.width < pr.width) {
                d.width = pr.width;
            }

            if (d.height < pr.height) {
                d.height = pr.height;
            }

            int xmax = 0;

            if (width == 0) {
                xmax = x + d.width;
            } else {
                xmax = x + width;
            }

            int ymax = 0;

            if (height == 0) {
                ymax = y + d.height;
            } else {
                ymax = y + height;
            }

            if (xmax > d.width) {
                d.width = xmax;
            }

            if (ymax > d.height) {
                d.height = ymax;
            }

            //System.out.println("new Max for comp is "+cdim.toString());
        }

        if (dim_width > dmax.width) {
            dmax.width = dim_width;
        }

        if (dim_height > dmax.height) {
            dmax.height = dim_height;
        }

        //System.out.println("Insets: " + insets.left + " " + insets.right + " " + insets.top + " " + insets.bottom);
        //System.out.println("Container minimum size: " + dmax.width + "x" + dmax.height);
        return dmax;
    }

    /**
     * Lays out the container. This method will actually reshape the
     * components in target in order to satisfy the constraints.
     * @param target the specified component being laid out.
     * @see Container
     */
    public void layoutContainer(Container target) {
        Insets insets = target.getInsets();
        Dimension dim = target.getSize();
        int width;
        int height;
        int x;
        int y;
        Component current = firstcomp;

        /*
        System.out.println("Laying out container at size: " +
        cavityWidth + "x" +
        cavityHeight);
         */
        PlaceRecord pr;
        Object anchor;

        while (current != null) {
            pr = (PlaceRecord) component_table.get(current);

            anchor = pr.anchor;

            //figure out the fill x and fill y
            //if the object is fill both or fill x then
            //fillx is true else fillx is false
            current.doLayout();

            Dimension minD = current.getMinimumSize();

            // Now that we have the frame size find out the actual component size
            int borderWidth;

            // Now that we have the frame size find out the actual component size
            int borderHeight;

            // Now that we have the frame size find out the actual component size
            int borderX;

            // Now that we have the frame size find out the actual component size
            int borderY;

            if (pr.bordermode == BORDERMODE_OBJ_INSIDE) {
                borderWidth = insets.left + insets.right;
                borderHeight = insets.top + insets.bottom;
                borderX = insets.left;
                borderY = insets.top;
            } else if (pr.bordermode == BORDERMODE_OBJ_OUTSIDE) {
                borderWidth = -(insets.left + insets.right);
                borderHeight = -(insets.top + insets.bottom);
                borderX = -insets.left;
                borderY = -insets.top;
            } else {
                borderWidth = 0;
                borderHeight = 0;
                borderX = 0;
                borderY = 0;
            }

            width = (int) (pr.width
                    + (pr.relwidth * (dim.width - borderWidth)));
            height = (int) (pr.height
                    + (pr.relheight * (dim.height - borderHeight)));

            if (width == 0) {
                width = minD.width;
            }

            if (height == 0) {
                height = minD.height;
            }

            x = (int) (pr.x + (pr.relx * (dim.width - borderWidth)));
            y = (int) (pr.y + (pr.rely * (dim.height - borderHeight)));
            x = x + getDelta(pr.anchor, true, width) + borderX;
            y = y + getDelta(pr.anchor, false, height) + borderY;

            //System.out.println("Component size: " + width + "x" + height);
            current.setBounds(x, y, width, height);
            current = pr.next;
        }
    }

    int getDelta(Object anchor, boolean horizontal, int size) {
        if (anchor == ANCHOR_OBJ_N) {
            if (horizontal) {
                return (-size / 2);
            }
        } else if (anchor == ANCHOR_OBJ_NE) {
            if (horizontal) {
                return (-size);
            }
        } else if (anchor == ANCHOR_OBJ_E) {
            if (horizontal) {
                return (-size);
            } else {
                return (-size / 2);
            }
        } else if (anchor == ANCHOR_OBJ_SE) {
            if (horizontal) {
                return (-size);
            } else {
                return (-size);
            }
        } else if (anchor == ANCHOR_OBJ_S) {
            if (horizontal) {
                return (-size / 2);
            } else {
                return (-size);
            }
        } else if (anchor == ANCHOR_OBJ_SW) {
            if (!horizontal) {
                return (-size);
            }
        } else if (anchor == ANCHOR_OBJ_W) {
            if (!horizontal) {
                return (-size / 2);
            }
        } else if (anchor == ANCHOR_OBJ_NW) {
        } else if (anchor == ANCHOR_OBJ_C) {
            if (horizontal) {
                return (-size / 2);
            } else {
                return (-size / 2);
            }
        }

        return (0);
    }

    // This method will return the current packing properties
    // of a specific widget like the following format
    // -anchor center -expand 0 -fill none -ipadx 0 -ipady 0 -padx 0 -pady 0 -side top
    public void getComponentSettings(final Component comp, final ArrayList<String> sb) {

        PlaceRecord pr = (PlaceRecord) component_table.get(comp);

        if (pr == null) {
            return; // The widget is not managed by this PlacerLayout
        }

        // -x -y -width -height
        sb.add(OPT_X);
        sb.add(Integer.toString(pr.x));


        sb.add(OPT_RELX);
        sb.add(Float.toString(pr.relx));


        sb.add(OPT_Y);
        sb.add(Integer.toString(pr.y));


        // -x -y -width -height
        sb.add(OPT_RELY);
        sb.add(Float.toString(pr.rely));


        sb.add(OPT_WIDTH);

        if (pr.widthActive) {
            sb.add(Integer.toString(pr.width));
        } else {
            sb.add("");
        }


        sb.add(OPT_RELWIDTH);

        if (pr.relWidthActive) {
            sb.add(Float.toString(pr.relwidth));
        } else {
            sb.add("");
        }


        sb.add(OPT_HEIGHT);

        if (pr.heightActive) {
            sb.add(Integer.toString(pr.height));
        } else {
            sb.add("");
        }


        sb.add(OPT_RELHEIGHT);

        if (pr.relHeightActive) {
            sb.add(Float.toString(pr.relheight));
        } else {
            sb.add("");
        }


        // -anchor
        sb.add(OPT_ANCHOR);

        Object anchor = pr.anchor;

        if (anchor == ANCHOR_OBJ_N) {
            sb.add(ANCHOR_OPT_N);
        } else if (anchor == ANCHOR_OBJ_NE) {
            sb.add(ANCHOR_OPT_NE);
        } else if (anchor == ANCHOR_OBJ_E) {
            sb.add(ANCHOR_OPT_E);
        } else if (anchor == ANCHOR_OBJ_SE) {
            sb.add(ANCHOR_OPT_SE);
        } else if (anchor == ANCHOR_OBJ_S) {
            sb.add(ANCHOR_OPT_S);
        } else if (anchor == ANCHOR_OBJ_SW) {
            sb.add(ANCHOR_OPT_SW);
        } else if (anchor == ANCHOR_OBJ_W) {
            sb.add(ANCHOR_OPT_W);
        } else if (anchor == ANCHOR_OBJ_NW) {
            sb.add(ANCHOR_OPT_NW);
        } else if (anchor == ANCHOR_OBJ_C) {
            sb.add(ANCHOR_OPT_C);
        } else {
            throw new RuntimeException("no match for ANCHOR type");
        }

        sb.add(OPT_BORDERMODE);

        Object bordermode = pr.bordermode;

        if (bordermode == BORDERMODE_OBJ_INSIDE) {
            sb.add(BORDERMODE_OPT_INSIDE);
        } else if (bordermode == BORDERMODE_OBJ_OUTSIDE) {
            sb.add(BORDERMODE_OPT_OUTSIDE);
        } else if (bordermode == BORDERMODE_OBJ_IGNORE) {
            sb.add(BORDERMODE_OPT_IGNORE);
        } else {
            throw new RuntimeException("no match for BORDERMODE type");
        }
    }

    // "update" functionality workaround
    public void setIgnoreNextRemove(boolean bool) {
        ignore_next_remove = bool;
    }

    /**
     * Returns the String representation of this class...
     */
    public String toString() {
        return getClass().getName();
    }

    /**
     * Adds the specified component to the layout.
     * @param name information about attachments
     * @param comp the the component to be added
     */
    public static void checkPlaceArgs(Interp interp, String spec, Component comp)
            throws TclException {
        int i;
        int max;

        boolean new_record = false;

        TclObject[] argv = TclList.getElements(interp,
                TclString.newInstance(spec));

        //in the default case we will not add an element
        //to the hash table, only if an argument is given will
        //an element be added to the hash table
        if ((argv.length % 2) != 0) {
            throw new TclException(interp,
                    "Error in PlacerLayout string spec, must have even number of arguments");
        }

        max = argv.length;

        Object option;
        Object value;

        for (i = 0; i < max; i += 2) {
            //first match the input string option
            option = option_table.get(argv[i].toString());

            //see if the option returned is the INT_MAP
            //placeholder for options that take an int
            if (option == INT_MAP) {
                //if true then we know that the value must be
                //a non negative integer
                int num;

                if (argv[i + 1].toString().trim().length() > 0) {
                    try {
                        num = Integer.parseInt(argv[i + 1].toString());

                        if (num < 0) {
                            throw new TclException(interp, "negative value");
                        }
                    } catch (NumberFormatException e) {
                        throw new TclException(interp,
                                "error : value of the " + argv[i].toString()
                                + " option must be a non negative integer");
                    }
                }
            } else if (option == DOUBLE_MAP) {
                //if true then we know that the value must be
                //a non negative integer
                double num;

                if (argv[i + 1].toString().trim().length() > 0) {
                    try {
                        num = Double.parseDouble(argv[i + 1].toString());
                    } catch (NumberFormatException e) {
                        throw new TclException(interp,
                                "expected floating-point number but got \""
                                + argv[i + 1].toString() + "\"");
                    }
                }
            } else if (option == SIZE_MAP) {
                if (argv[i + 1].toString().trim().length() > 0) {
                    int num = SwankUtil.getTkSize(interp, comp, argv[i + 1]);

                    //this is an int option so we need to find out which
                    //record member we will assign the int to
                }

                value = value_table.get(argv[i].toString());
                ;

                //temp check for fatal case
                if (value == null) {
                    String str = "null value object for int parser on option \""
                            + argv[i].toString() + "\" : \""
                            + argv[i + 1].toString() + "\"";

                    throw new TclException(interp, str);
                }

                if (value == OPT_X) {
                } else if (value == OPT_Y) {
                } else if (value == OPT_WIDTH) {
                } else if (value == OPT_HEIGHT) {
                } else {
                    throw new TclException(interp, "fatal : bad branch");
                }
            } else if (option == BOOLEAN_MAP) {
                //if true then we know that the value must be
                //a boolean value
                boolean bool;
                String s = argv[i + 1].toString();

                if (s.length() > 0) {
                    if ("yes".startsWith(s)) {
                        bool = true;
                    } else if ("no".startsWith(s)) {
                        bool = false;
                    } else if ("true".startsWith(s)) {
                        bool = true;
                    } else if ("false".startsWith(s)) {
                        bool = false;
                    } else if ("on".startsWith(s) && (s.length() > 1)) {
                        bool = true;
                    } else if ("off".startsWith(s) && (s.length() > 1)) {
                        bool = false;
                    } else if (s.equals("0")) {
                        bool = false;
                    } else if (s.equals("1")) {
                        bool = true;
                    } else {
                        throw new TclException(interp,
                                "error : value of the " + argv[i].toString()
                                + " option must be a boolean");
                    }
                } else {
                    throw new TclException(interp,
                            "error : value of the " + argv[i].toString()
                            + " option must be a boolean");
                }

                //this is an int option so we need to find out which
                //record member we will assign the int to
                value = value_table.get(argv[i].toString());
                ;

                //temp check for fatal case
                if (value == null) {
                    String str = "null value object for int parser on option \""
                            + argv[i].toString() + "\" : \""
                            + argv[i + 1].toString() + "\"";

                    throw new TclException(interp, str);
                }
            } else if (option == null) {
                //this is an error becuase the option
                //that was just given does not match
                //any of the predefined option types
                // unknown or ambiguous option "-foll": must be -after, -anchor, -before, -expand, -fill, -in, -ipadx, -ipady, -padx, -pady, or -side
                throw new TclException(interp,
                        "error : option \"" + argv[i].toString()
                        + "\" does not match any of " + OPT_X + " " + OPT_Y + " "
                        + OPT_RELX + " " + OPT_RELY + " " + OPT_WIDTH + " "
                        + OPT_HEIGHT);
            } else if (option != value_table.get(argv[i + 1].toString())) {
                //in this case the given value for the option
                //did not match one of the possible option values
                if (option == OPT_BORDERMODE) {
                    throw new TclException(interp,"bad bordermode \"" + argv[i+1].toString() + "\": must be inside, outside, or ignore");
                }
                throw new TclException(interp,
                        "error : option \"" + argv[i].toString()
                        + "\" can not take the value \"" + argv[i + 1].toString()
                        + "\"");
            } else {
                //if no other conditions are true then we
                //must have matched both the option and the value
                //so we can add the option string and map it to
                //the value object in the value_object_table
                value = value_object_table.get(argv[i + 1].toString());

                //temp check
                if (value == null) {
                    String str =
                            "null value object for option parser on option \""
                            + argv[i].toString() + "\" : \""
                            + argv[i + 1].toString() + "\"";

                    throw new TclException(interp, str);
                }

                //now we need to find out which field in our place record
                //we need to assign this object to
                if (option == OPT_ANCHOR) {
                } else if (option == OPT_BORDERMODE) {
                } else {
                    throw new TclException(interp, "fatal : bad branch");
                }
            }
        }
    }

    //there is one of these records for each widget packed
    //into a window, it stores the pack info for that widget
    class PlaceRecord {

        Component prev = null;
        Component next = null;
        public Object anchor = DEFAULT_ANCHOR;
        public Object bordermode = DEFAULT_BORDERMODE;
        public int x = DEFAULT_X;
        public int y = DEFAULT_Y;
        public int width = DEFAULT_WIDTH;
        public int height = DEFAULT_HEIGHT;
        public float relx = DEFAULT_RELX;
        public float rely = DEFAULT_RELY;
        public float relwidth = DEFAULT_RELWIDTH;
        public float relheight = DEFAULT_RELHEIGHT;
        public boolean widthActive = false;
        public boolean relWidthActive = false;
        public boolean heightActive = false;
        public boolean relHeightActive = false;
    }
}

//this exception is thrown if invalid arguments are passed
//to the placer layout
class PlaceingException extends RuntimeException {

    public PlaceingException(String desc) {
        super(desc);
    }
}
