package com.onemoonscientific.swank.canvas3D;

import com.onemoonscientific.swank.*;

import tcl.lang.*;


import java.util.*;


/*
 * This class implements the "sayhello" command in SimplePackage.
 */
class SwkCanvas3DWidgetCmd implements Command {
    /*
     * This procedure is invoked to process the "sayhello" Tcl command -- it
     * takes no arguments and returns "Hello World!" string as its result.
     */
    static final private String[] validCmds = {
        "object", "configure", "jadd", "cget", "child", "view", "lines"
    };
    static final private int OPT_OBJECT = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_JADD = 2;
    static final private int OPT_CGET = 3;
    static final private int OPT_CHILD = 4;
    static final private int OPT_VIEW = 5;
    static final private int OPT_LINES = 6;
    static private SwkCanvas3D copyBuffer = null;

    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        int i;
        boolean gotDefaults = false;

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

        SwkCanvas3D swkcanvas3d = (SwkCanvas3D) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(jget(interp, swkcanvas3d, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkcanvas3d.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                String keyName;
                ResourceObject ro;
                String result;
                TclObject list2 = TclList.newInstance();
                Enumeration e = SwkCanvas3D.resourceDB.keys();

                while (e.hasMoreElements()) {
                    TclObject list1 = TclList.newInstance();
                    keyName = (String) e.nextElement();

                    if (keyName == null) {
                        continue;
                    }

                    ro = (ResourceObject) SwkCanvas3D.resourceDB.get(keyName);

                    if (ro == null) {
                        continue;
                    }

                    tObj = TclString.newInstance(keyName);

                    try {
                        result = jget(interp, swkcanvas3d, tObj);
                    } catch (TclException tclE) {
                        continue;
                    }

                    TclList.append(interp, list1, tObj);
                    TclList.append(interp, list1,
                        TclString.newInstance(ro.resource));
                    TclList.append(interp, list1,
                        TclString.newInstance(ro.className));

                    if (ro.defaultVal == null) {
                        TclList.append(interp, list1, TclString.newInstance(""));
                    } else {
                        TclList.append(interp, list1,
                            TclString.newInstance(ro.defaultVal));
                    }

                    if (result == null) {
                        result = "";
                    }

                    TclList.append(interp, list1, TclString.newInstance(result));
                    TclList.append(interp, list2, list1);
                }

                interp.setResult(list2);
            } else if (argv.length == 3) {
                String result = jget(interp, swkcanvas3d, argv[2]);
                ResourceObject ro = (ResourceObject) SwkCanvas3D.resourceDB.get(argv[2].toString());
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
                configure(interp, swkcanvas3d, argv, 2);
            }

            break;

        case OPT_OBJECT:
            interp.setResult(tObj);

            break;

        case OPT_JADD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            jadd(interp, swkcanvas3d, argv[2]);

            break;

        case OPT_CHILD:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv[2].toString().equals("add")) {
                NvBranchGroup bg = new NvBranchGroup();
                int id = swkcanvas3d.addChild(bg);

                if (id >= 0) {
                    interp.setResult(id);
                } else {
                    throw new TclException(interp, "couldn't add new child");
                }
            } else if (argv[2].toString().equals("count")) {
                NvBranchGroup bg = new NvBranchGroup();
                int result = swkcanvas3d.numChildren();

                if (result >= 0) {
                    interp.setResult(result);
                } else {
                    throw new TclException(interp, "couldn't find root");
                }
            } else if (argv[2].toString().equals("delete")) {
                if (argv.length != 4) {
                    throw new TclNumArgsException(interp, 2, argv, "tagOrID");
                }

                int id = TclInteger.get(interp, argv[3]);
                int result = swkcanvas3d.removeChild(id);

                if (result >= 0) {
                    interp.setResult("");
                } else {
                    throw new TclException(interp,
                        "branchgroup \"" + id + "\"doesn't exit");
                }
            }

            break;

        case OPT_VIEW:

            if (argv.length != 6) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            if (argv[2].toString().equals("eye")) {
                float x = (float) TclDouble.get(interp, argv[3]);
                float y = (float) TclDouble.get(interp, argv[4]);
                float z = (float) TclDouble.get(interp, argv[5]);

                //FIXME swkcanvas3d.setEyePosition(x,y,z);
            } else if (argv[2].toString().equals("center")) {
                float x = (float) TclDouble.get(interp, argv[3]);
                float y = (float) TclDouble.get(interp, argv[4]);
                float z = (float) TclDouble.get(interp, argv[5]);

                //FIXMEswkcanvas3d.setViewCenter(x,y,z);
            } else if (argv[2].toString().equals("up")) {
                float x = (float) TclDouble.get(interp, argv[3]);
                float y = (float) TclDouble.get(interp, argv[4]);
                float z = (float) TclDouble.get(interp, argv[5]);

                //FIXMEswkcanvas3d.setUpDirection(x,y,z);
            }

            break;

        case OPT_LINES:

            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            TclObject[] coordObjects = TclList.getElements(interp, argv[2]);
            int n = coordObjects.length;

            if ((n % 3) != 0) {
                throw new TclException(interp,
                    "number of coords must be a multiple of 3");
            }

            float[] coords = new float[n];
            float[] colors = new float[n];
            TclObject[] colorObjects = TclList.getElements(interp, argv[3]);

            for (int iElem = 0; iElem < coordObjects.length; iElem++) {
                coords[iElem] = (float) TclDouble.get(interp,
                        coordObjects[iElem]);
                colors[iElem] = 1.0f;
            }

            int id = swkcanvas3d.addLines(coords, colors, 0, n);

            if (id >= 0) {
                interp.setResult(id);
            } else {
                throw new TclException(interp,
                    "couldn't add lines, no scene or root");
            }

            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    public static void configure(Interp interp, SwkCanvas3D swkCanvas3D,
        TclObject[] argv, int start) throws TclException {
    }

    public static String jget(Interp interp, SwkCanvas3D swkCanvas3D,
        TclObject tclObject) throws TclException {
        return null;
    }

    public static void jadd(Interp interp, SwkCanvas3D swkcanvas3d,
        TclObject tclObject) throws TclException {
    }
}
