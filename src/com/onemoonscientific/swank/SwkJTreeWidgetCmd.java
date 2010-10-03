/*
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file "LICENSE" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 */
package com.onemoonscientific.swank;

import tcl.lang.*;

import javax.swing.SwingUtilities;
import javax.swing.tree.*;


class SwkJTreeWidgetCmd implements Command {
    static final private String[] validCmds = {
        "cget", "configure", "path", "node", "update"
    };
    static final private int OPT_CGET = 0;
    static final private int OPT_CONFIGURE = 1;
    static final private int OPT_PATH = 2;
    static final private int OPT_NODE = 3;
    static final private int OPT_UPDATE = 4;
    static boolean gotDefaults = false;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        final int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);
        final TclObject tObj = (TclObject) Widgets.getWidget(interp,argv[0].toString());

        if (tObj == null) {
            throw new TclException(interp,
                "bad window path name \"" + argv[0].toString() + "\"");
        }

        final SwkJTree swkjtree = (SwkJTree) ReflectObject.get(interp, tObj);

        switch (opt) {
        case OPT_CGET:

            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.setResult(swkjtree.jget(interp, argv[2]));

            break;

        case OPT_CONFIGURE:

            if (!gotDefaults) {
                swkjtree.setResourceDefaults();
                gotDefaults = true;
            }

            if (argv.length == 2) {
                swkjtree.jgetAll(interp);
            } else if (argv.length == 3) {
                String result = swkjtree.jget(interp, argv[2]);
                ResourceObject ro = (ResourceObject) SwkJTree.resourceDB.get(argv[2].toString());

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
                swkjtree.configure(interp, argv, 2);
            }

            break;

        case OPT_PATH:
            getPath(interp, swkjtree, argv);

            break;

        case OPT_NODE:
            node(interp, swkjtree, argv);

            break;

        case OPT_UPDATE:

            if (argv.length != 2) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        DefaultTreeModel model = (DefaultTreeModel) swkjtree.getModel();
                        model.reload();
                    }
                });


            break;

        default:
            throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    void node(final Interp interp, final SwkJTree swkjtree,
        final TclObject[] argv) throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        if (argv[2].toString().equals("add")) {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 2, argv, "option");
            }

            interp.resetResult();

            int[] iNodes = null;

            if (!argv[3].toString().equals("root")) {
                TclObject[] nodeList = TclList.getElements(interp, argv[3]);
                iNodes = new int[nodeList.length];

                for (int i = 0; i < iNodes.length; i++) {
                    iNodes[i] = TclInteger.get(interp, nodeList[i]);
                }
            }

            String value = argv[4].toString();
            (new NodeAdd()).add(swkjtree, iNodes, value);
        } else if (argv[2].toString().equals("count")) {
            TreePath treePath = (new Path()).exec(swkjtree);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            if (node == null) {
                 interp.resetResult();
            } else {
                interp.setResult(node.getChildCount());
            }
        }
    }

    void getPath(final Interp interp, final SwkJTree swkjtree,
        final TclObject[] argv) throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "option");
        }

        if (argv[2].toString().equals("get")) {
            interp.resetResult();

            TreePath treePath = (new Path()).exec(swkjtree);
            TclObject list = TclList.newInstance();

            if (treePath != null) {
                for (int iPath = 0; iPath < treePath.getPathCount(); iPath++) {
                    TclList.append(interp, list,
                        TclString.newInstance(treePath.getPathComponent(iPath)
                                                      .toString()));
                }
            }

            interp.setResult(list);
        }
    }

    class NodeAdd extends UpdateOnEventThread {
        SwkJTree swkjtree;
        int[] iNodes = null;
        DefaultMutableTreeNode node = null;

        void add(final SwkJTree swkjtree, final int[] iNodes,
            final String nodeVal) {
            this.swkjtree = swkjtree;
            this.iNodes = iNodes;
            node = new DefaultMutableTreeNode(nodeVal);
            execOnThread();
        }

        public void run() {
            DefaultTreeModel model = (DefaultTreeModel) swkjtree.getModel();

            if (iNodes == null) {
                model.setRoot(node);
            } else {
                DefaultMutableTreeNode refNode = (DefaultMutableTreeNode) model.getRoot();

 	    if (iNodes[0] >= 0) {
                for (int i = 0; i < iNodes.length; i++) {
                   refNode = (DefaultMutableTreeNode) refNode.getChildAt(iNodes[i]);
                }
             }

             refNode.add(node);
          }
        }
    }

    class Path extends GetValueOnEventThread {
        SwkJTree swkjtree;
        TreePath treePath = null;

        TreePath exec(final SwkJTree swkjtree) {
            this.swkjtree = swkjtree;
            execOnThread();

            return treePath;
        }

        public void run() {
            treePath = swkjtree.getSelectionPath();
        }
    }
}
