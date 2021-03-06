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

import java.awt.*;
import java.util.*;
import javax.swing.text.*;

class SwkJTextPaneWidgetCmd implements Command {

    private static final String[] validCmds = {
        "bbox","cget", "compare","configure", "debug", "delete", "dlineinfo", "dump", "edit", "get", 
        "image", "index", "insert", "mark", "scan", "search", "see", "tag", "window"
    };
    private static final int OPT_BBOX = 0;
    private static final int OPT_CGET = 1;
    private static final int OPT_COMPARE = 2;
    private static final int OPT_CONFIGURE = 3;
    private static final int OPT_DEBUG = 4;
    private static final int OPT_DELETE = 5;
    private static final int OPT_DLINEINFO = 6;
    private static final int OPT_DUMP = 7;
    private static final int OPT_EDIT = 8;
    private static final int OPT_GET = 9;
    private static final int OPT_IMAGE = 10;
    private static final int OPT_INDEX = 11;
    private static final int OPT_INSERT = 12;
    private static final int OPT_MARK = 13;
    private static final int OPT_SCAN = 14;
    private static final int OPT_SEARCH = 15;
    private static final int OPT_SEE = 16;
    private static final int OPT_TAG = 17;
    private static final int OPT_WINDOW = 18;
    static boolean gotDefaults = false;
    Interp interp = null;

    public static String[] getValidCmds() {
        return validCmds;
    }

    public void cmdProc(final Interp interp, final TclObject[] argv)
            throws TclException {
        int i = 0;
        this.interp = interp;

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

        SwkJTextPane swkjtextpane = (SwkJTextPane) ReflectObject.get(interp,
                tObj);

        switch (opt) {
            case OPT_CGET:

                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 2, argv, "option");
                }

                interp.setResult(swkjtextpane.jget(interp, argv[2]));

                break;

            case OPT_CONFIGURE:

                if (!gotDefaults) {
                    swkjtextpane.setResourceDefaults();
                    gotDefaults = true;
                }

                if (argv.length == 2) {
                    swkjtextpane.jgetAll(interp);
                } else if (argv.length == 3) {
                    String result = swkjtextpane.jget(interp, argv[2]);
                    ResourceObject ro = (ResourceObject) SwkJTextPane.resourceDB.get(argv[2].toString());

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
                    swkjtextpane.configure(interp, argv, 2);
                }

                break;

            case OPT_DUMP:
                dump(interp, swkjtextpane, argv);
                break;

            case OPT_EDIT:
                edit(interp, swkjtextpane, argv);
                break;


            case OPT_INDEX:
                index(interp, swkjtextpane, argv);

                break;

            case OPT_INSERT:
                insert(interp, swkjtextpane, argv);

                break;

            case OPT_DELETE:
                delete(interp, swkjtextpane, argv);

                break;

            case OPT_TAG:
                tag(interp, swkjtextpane, argv);

                break;

            case OPT_MARK:
                mark(interp, swkjtextpane, argv);

                break;

            case OPT_COMPARE:
                compare(interp, swkjtextpane, argv);

                break;

            case OPT_DEBUG:
                debug(interp, swkjtextpane, argv);

                break;

            case OPT_SEARCH:
                search(interp, swkjtextpane, argv);

                break;

            case OPT_SEE:
                see(interp, swkjtextpane, argv);

                break;

            case OPT_BBOX:
                bbox(interp, swkjtextpane, argv);

                break;

            case OPT_WINDOW:
                window(interp, swkjtextpane, argv);

                break;

            case OPT_GET:
                get(interp, swkjtextpane, argv);

                break;

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }
    void edit(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "option ?arg arg ...?");
        }
        if (argv[2].toString().equals("undo")) {
            if (swkjtextpane.undoManager != null) {
                if (!swkjtextpane.undoManager.canUndo()) {
                     throw new TclException(interp,"nothing to undo");
                }
                swkjtextpane.undoManager.undo();
            }
        } else if (argv[2].toString().equals("redo")) {
            if (swkjtextpane.undoManager != null) {
                if (!swkjtextpane.undoManager.canRedo()) {
                     throw new TclException(interp,"nothing to redo");
                }
                swkjtextpane.undoManager.redo();
            }
        } else if (argv[2].toString().equals("reset")) {
            if (swkjtextpane.undoManager != null) {
                swkjtextpane.undoManager.discardAllEdits();
            }
        } else if (argv[2].toString().equals("separator")) {
        } else if (argv[2].toString().equals("modified")) {
            if (argv.length == 4) {
            }
        } else {
           throw new TclException(interp,"bad edit option \"" + argv[2].toString() + "\": must be modified, redo, reset, separator or undo");
        }

    }

    void dump(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        ElementIterator elIter = new ElementIterator(swkjtextpane.doc);
        Element elem;
        Element elem2;
        String tagName;

        while (true) {
            if ((elem = elIter.next()) != null) {
                //System.out.println("elem " + elem.getStartOffset() + " " + elem.getEndOffset() + " " + elem.toString());
                elem2 = swkjtextpane.doc.getCharacterElement(elem.getStartOffset());

                if (elem2 != null) {
                    AttributeSet attrs = elem2.getAttributes();

                    if (attrs != null) {
                        tagName = (String) attrs.getAttribute("tagName");

                        //System.out.println(elem2.getStartOffset() + " " + elem2.getEndOffset() + " " + tagName);
                    }
                }

                elem2 = swkjtextpane.doc.getParagraphElement(elem.getStartOffset());

                if (elem2 != null) {
                    AttributeSet attrs = elem2.getAttributes();

                    if (attrs != null) {
                        tagName = (String) attrs.getAttribute("tagName");

                        //                            System.out.println(elem2.getStartOffset() + " " + elem2.getEndOffset() + " " + tagName);
                    }
                }
            } else {
                break;
            }
        }
    }

    void celem(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        Element elem = swkjtextpane.doc.getCharacterElement(TclInteger.get(
                interp, argv[2]));

        if (elem != null) {
            interp.setResult(elem.getName() + " " + elem.getElementCount()
                    + " " + elem.getStartOffset() + " " + elem.getEndOffset());
        }
    }

    void index(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length != 3) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        String index = (new Index()).exec(swkjtextpane, argv[2].toString());
        if (index == null) {
             throw new TclException(interp,"bad text index \"" + argv[2].toString() + "\"");
        }
        interp.setResult(index);
    }

    @SuppressWarnings("static-access")
    void tag(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        if (argv[2].toString().equals("configure")) {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ...?");
            }

            if (argv.length < 6) {
                Style style = (new StyleGet()).exec(swkjtextpane,
                        argv[3].toString(), false);

                if (style == null) {
                    throw new TclException(interp,
                            "coudn't find style " + argv[3].toString());
                }

                if (argv.length > 4) {
                    swkjtextpane.getStyleStuff(interp, style, argv[4], true);
                } else {
                    swkjtextpane.getStyleStuff(interp, style, null, true);
                }
            } else {
                Style style = (new StyleGet()).exec(swkjtextpane,
                        argv[3].toString(), true);

                SwkJTextPane.setStyleStuff(interp, style, argv, 4);
                (new StyleSet()).exec(swkjtextpane, style);
            }
        } else if (argv[2].toString().equals("cget")) {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ...?");
            }

            Style style = swkjtextpane.getStyle(argv[3].toString());

            if (style != null) {
                swkjtextpane.getStyleStuff(interp, style, argv[4], false);
            } else {
                throw new TclException(interp,
                        "coudn't find style " + argv[3].toString());
            }
        } else if (argv[2].toString().equals("add")) {
           if (argv.length < 5) {
                throw new TclNumArgsException(interp, 2, argv, "tagName index1 ?index2 index1 index2...?");
            }
            String[] tagStrings = new String[argv.length-4];
    
            for (int j = 0; j < tagStrings.length; j++) {
                tagStrings[j] = argv[j+4].toString();
            }

            (new Add()).exec(swkjtextpane, argv[0].toString(), argv[3].toString(), tagStrings);
        } else if (argv[2].toString().equals("bind")) {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ...?");
            }

            Style style = (new StyleGet()).exec(swkjtextpane,
                    argv[3].toString(), false);

            if (style == null) {
                throw new TclException(interp,
                        "style " + argv[3].toString() + " doesn't exist");
            }

            if (argv.length > 4) {
                SwkBinding binding = SwkBind.getBinding(interp, argv, 4);

                if (binding != null) {
                    swkjtextpane.setupBinding(interp, binding,
                            argv[3].toString());
                    SwkBind.updateBindingCommand(interp, binding, argv, 4);
                }
            }
        } else if (argv[2].toString().equals("names")) {
            String arg1 = null;

            if (argv.length > 3) {
                arg1 = argv[3].toString();
            }

            ArrayList arrayResult = (new TagGet()).names(swkjtextpane, arg1);

            if (arrayResult != null) {
                if (arg1 != null) {
                    interp.setResult((String) arrayResult.get(0));
                } else {
                    TclObject list = TclList.newInstance();

                    for (int i = 0; i < arrayResult.size(); i++) {
                        TclList.append(interp, list,
                                TclString.newInstance((String) arrayResult.get(i)));
                    }

                    interp.setResult(list);
                }
            }
        } else if (argv[2].toString().equals("ranges")) {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ... ?");
            }

            String ranges = (new TagGet()).ranges(swkjtextpane,
                    argv[3].toString());
            interp.setResult(ranges);
        } else if (argv[2].toString().equals("delete")) {
            if (argv.length != 4) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ...?");
            }

            (new StyleRemove()).removeStyle(swkjtextpane, argv[3].toString());
        } else if (argv[2].toString().equals("remove")) {
            if (argv.length != 6) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ...?");
            }

            (new StyleRemove()).removeStyleFromRange(swkjtextpane,
                    argv[3].toString(), argv[4].toString(), argv[5].toString());
        }
    }

    void mark(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if ((argv.length < 3)) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        if (argv[2].toString().equals("set")) {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ...?");
            }

            (new MarkSet()).exec(swkjtextpane, argv[3].toString(),
                    argv[4].toString());
        } else if (argv[2].toString().equals("gravity")) {
            if (argv.length != 5) {
                throw new TclNumArgsException(interp, 1, argv,
                        "option ?arg arg ...?");
            }

            (new MarkSet()).execGravity(swkjtextpane, argv[3].toString(),
                    argv[4].toString());
        } else if (argv[2].toString().equals("unset")) {
            String[] marks = new String[argv.length - 3];

            for (int i = 3; i < argv.length; i++) {
                marks[i - 3] = argv[i].toString();
            }

            (new MarkSet()).execUnMark(swkjtextpane, marks);
        } else if (argv[2].toString().equals("names")) {
            ArrayList arrayResult = (new MarkGet()).exec(swkjtextpane);
            TclObject list = TclList.newInstance();

            if (arrayResult != null) {
                for (int i = 0; i < arrayResult.size(); i++) {
                    TclList.append(interp, list,
                            TclString.newInstance((String) arrayResult.get(i)));
                }
            }

            interp.setResult(list);
        }
    }

    void debug(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if ((argv.length != 2) && (argv.length != 3)) {
            throw new TclNumArgsException(interp, 2, argv, "boolean");
        }

        if (argv.length == 3) {
            swkjtextpane.debug = TclBoolean.get(interp, argv[2]);
        }

        interp.setResult(swkjtextpane.debug);
    }

    void compare(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length != 5) {
            throw new TclNumArgsException(interp, 2, argv, "index1 op index2");
        }

        Result result = (new Compare()).exec(swkjtextpane, argv[2].toString(),
                argv[3].toString(), argv[4].toString());
        if (result == null) { 
           throw new TclException(interp,"null result");
        }
        result.checkError(interp);
        interp.setResult(result.b);
    }

    void search(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length < 4) {
            throw new TclNumArgsException(interp, 2, argv,
                    "?switches? pattern index ?stopIndex?");
        }

        int searchFlags = 0;
        boolean forwardsMode = true;
        boolean exactMode = true;
        boolean regexpMode = true;
        boolean flagSearch = true;
        String countVar = null;
        int patternIndex = -1;
        int startIndex = -1;
        int endIndex = -1;
        int index2 = -1;

        for (int i = 2; i < argv.length; i++) {
            if (argv[i].toString().length() == 0) {
                throw new TclException(interp,"bad search arg");
            }
            if ((argv[i].toString().charAt(0) != '-') || !flagSearch) {
                if (patternIndex == -1) {
                    patternIndex = i;
                } else if (startIndex == -1) {
                    startIndex = i;
                } else if (endIndex == -1) {
                    endIndex = i;
                } else {
                    throw new TclNumArgsException(interp, 2, argv,
                            "?switches? pattern index ?stopIndex?");
                }
            } else {
                if (patternIndex != -1) {
                    throw new TclNumArgsException(interp, 2, argv,
                            "?switches? pattern index ?stopIndex?");
                }

                if ("--".equals(argv[i].toString())) {
                    flagSearch = false;
                } else if ("-forwards".startsWith(argv[i].toString())) {
                    // FIXME forwards not implemented (however it is default so not necessary)
                } else if ("-exact".startsWith(argv[i].toString())) {
                    // FIXME exact not implemented (however it is default so not necessary)
                } else if ("-backwards".startsWith(argv[i].toString())) {
                    searchFlags |= SwkDocumentSearch.SEARCH_BACKWARDS;
                } else if ("-regexp".startsWith(argv[i].toString())) {
                    searchFlags |= SwkDocumentSearch.SEARCH_REGEXP;
                } else if ("-nocase".startsWith(argv[i].toString())) {
                    searchFlags |= SwkDocumentSearch.SEARCH_NOCASE;
                } else if ("-elide".startsWith(argv[i].toString())) {
                    searchFlags |= SwkDocumentSearch.SEARCH_ELIDE;
                } else if ("-count".startsWith(argv[i].toString())) {
                    i++;

                    if (i >= argv.length) {
                        throw new TclNumArgsException(interp, 2, argv,
                                "?switches? pattern index ?stopIndex?");
                    }

                    countVar = argv[i].toString();
                } else {
                    throw new TclException(interp,
                            "bad switch \"" + argv[i].toString()
                            + "\": must be --, -backward, -count, -elide, -exact, -forward, -nocase, or -regexp");
                }
            }
        }

        if (patternIndex == -1) {
            throw new TclNumArgsException(interp, 2, argv,
                    "?switches? pattern index ?stopIndex?");
        }

        if (startIndex == -1) {
            throw new TclNumArgsException(interp, 2, argv,
                    "?switches? pattern index ?stopIndex?");
        }

        String index1Arg = argv[startIndex].toString();
        String index2Arg = null;

        if (endIndex != -1) {
            index2Arg = argv[endIndex].toString();
        }

        String patternString = argv[patternIndex].toString();
        Result result = (new Search()).exec(swkjtextpane, patternString,
                index1Arg, index2Arg, searchFlags);
        result.checkError(interp);

        if ((countVar != null) && (result.i >= 0)) {
            TclObject tObj = TclInteger.newInstance(result.i);
            interp.setVar(countVar, tObj, 0);
        }

        interp.setResult(result.s);
    }

    void get(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "index1 ?index2 ...?");
        }
        String[] tagStrings = new String[argv.length-2];

        for (int j = 0; j < tagStrings.length; j++) {
            tagStrings[j] = argv[j+2].toString();
        }

        Result result = (new Get()).exec(swkjtextpane, tagStrings);
        result.checkError(interp);
        if (result.o == null) {
            interp.setResult(result.s);
        } else {
            String[] resultStrings = (String[]) result.o;
            TclObject resultList = TclList.newInstance();
            for (String s: resultStrings) {
                TclList.append(interp,resultList,TclString.newInstance(s));
            }
            interp.setResult(resultList);
        }
    }

    void insert(final Interp interp, final SwkJTextPane swkjtextpane,
            final TclObject[] argv) throws TclException {
        if (argv.length < 4) {
            throw new TclNumArgsException(interp, 2, argv,
                    "index chars ?tagList chars tagList ...?");
        }

        int nPairs = (argv.length - 2) / 2;
        int k = 0;
        String[] groupedStyleStrings = new String[nPairs];
        String[][] tagStrings = new String[nPairs][];
        String[] insertStrings = new String[nPairs];

        for (int i = 3; i < argv.length; i += 2) {
            if ((i + 1) < argv.length) {
                TclObject[] tags = TclList.getElements(interp, argv[i + 1]);
                groupedStyleStrings[k] = argv[i + 1].toString();
                tagStrings[k] = new String[tags.length];

                for (int j = 0; j < tags.length; j++) {
                    tagStrings[k][j] = tags[j].toString();
                }
            }

            insertStrings[k] = argv[i].toString();
            k++;
        }

        (new Insert()).exec(swkjtextpane, argv[2].toString(),
                groupedStyleStrings, tagStrings, insertStrings);
    }

    void delete(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if (argv.length < 3) {
            throw new TclNumArgsException(interp, 2, argv, "index1 ?index2 ...?");
        }
        String[] tagStrings = new String[argv.length-2];

        for (int j = 0; j < tagStrings.length; j++) {
            tagStrings[j] = argv[j+2].toString();
        }
        Result result = (new Delete()).exec(swkjtextpane, tagStrings);
        result.checkError(interp);
    }

    void see(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if ((argv.length != 3)) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        String indexArg = argv[2].toString();
        (new See()).exec(swkjtextpane, indexArg);
    }

    void bbox(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if ((argv.length != 3)) {
            throw new TclNumArgsException(interp, 2, argv, "index");
        }

        String indexArg = argv[2].toString();
        Rectangle rect = (new BBox()).exec(swkjtextpane, indexArg);

        if (rect == null) {
            throw new TclException(interp, "bad location in bbox");
        }

        interp.setResult(SwankUtil.parseRectangle(rect));
    }

    void window(Interp interp, SwkJTextPane swkjtextpane, TclObject[] argv)
            throws TclException {
        if ((argv.length < 6)) {
            throw new TclNumArgsException(interp, 1, argv,
                    "create ?arg arg ...?");
        }

        if ("create".startsWith(argv[2].toString())) {
            //set insertion
            //System.out.println("create window " + argv[3].toString() + " " + index1);
            Object windowObject = null;
            String windowName = null;

            for (int i = 4; i < argv.length; i++) {
                if ("-window".startsWith(argv[i].toString())) {
                    i++;

                    if (i == argv.length) {
                        throw new TclException(interp,
                                "value for \"-window\" missing");
                    } else {
                        windowName = argv[i].toString();
                        windowObject = Widgets.get(interp, windowName);

                        if ((windowObject == null)
                                || (!(windowObject instanceof Component))) {
                            throw new TclException(interp,
                                    "bad window path name \"" + windowName + "\"");
                        }
                    }
                }
            }

            String indexArg = argv[3].toString();
            (new Window()).exec(swkjtextpane, indexArg, windowName, windowObject);
        }
    }

    private static class Index extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String indexArg = null;
        String index = "";

        String exec(final SwkJTextPane swkjtextpane, final String indexArg) {
            this.swkjtextpane = swkjtextpane;
            this.indexArg = indexArg;
            execOnThread();

            return index;
        }

        @Override
        public void run() {
            try {
                index = swkjtextpane.doc.getIndex(swkjtextpane, indexArg);
            } catch (IllegalArgumentException iaE) {
                index = null;
            }
        }
    }

    private static class Add extends UpdateOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String windowName = null;
        String tagName = null;
        String[] indexNames = null;
        Result result = new Result();

        Result exec(final SwkJTextPane swkjtextpane, final String windowName,
                final String tagName, final String[] indexNames) {
            this.swkjtextpane = swkjtextpane;
            this.windowName = windowName;
            this.tagName = tagName;
            this.indexNames = indexNames;
            execOnThread();
            return result;
        }

        @Override
        public void run() {
            Style style = swkjtextpane.getStyle(tagName);
            int nEntries = indexNames.length;
            if ((indexNames.length % 2) == 1) {
                 nEntries++;
            }
            int[] indices = new int[nEntries];
            int endOffset = swkjtextpane.doc.getEndPosition().getOffset();
            for (int i=0;i<indexNames.length;i++) {
                try {
                    indices[i] = swkjtextpane.doc.getIndexLC(swkjtextpane, indexNames[i]);
                } catch (Exception e) {
                    result.setError(e.getMessage());
                    return;
                }
            }
            if ((indexNames.length % 2) == 1) {
                 indices[nEntries-1] = indices[nEntries-2]+1;
            }

            if (tagName.equals("sel")) {
                swkjtextpane.setCaretPosition(indices[0]);
                swkjtextpane.moveCaretPosition(indices[1]-1);
                if (!swkjtextpane.selectionWindowAdded) {
                    SelectionCmd.addSelectionWindow(windowName);
                    swkjtextpane.selectionWindowAdded = true;
                }
            }

            if (style == null) {
                style = swkjtextpane.addStyle(tagName, null);
                style.addAttribute("tagName", tagName);
            }

            for (int i=0;i<nEntries;i += 2) {
               if ((indices[i+1] > indices[i]) && (indices[i] < endOffset)) {
                       swkjtextpane.doc.addStyleToRange(swkjtextpane, indices[i],indices[i+1], tagName);
               }
           }
        }
    }

    private static class StyleRemove extends UpdateOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String arg1 = null;
        String arg2 = null;
        String arg3 = null;
        int mode = 0;

        void removeStyle(final SwkJTextPane swkjtextpane, final String arg1) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            mode = 0;
            execOnThread();
        }

        void removeStyleFromRange(final SwkJTextPane swkjtextpane,
                final String arg1, final String arg2, final String arg3) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            mode = 1;
            execOnThread();
        }

        @Override
        public void run() {
            if (mode == 0) {
                swkjtextpane.doc.removeStyle(swkjtextpane, arg1);
            } else {
                int index1 = swkjtextpane.doc.getIndexLC(swkjtextpane, arg2);
                int index2 = swkjtextpane.doc.getIndexLC(swkjtextpane, arg3);
                swkjtextpane.doc.removeStyleFromRange(swkjtextpane, index1,
                        index2, arg1);
            }
        }
    }

    private static class StyleSet extends UpdateOnEventThread {

        SwkJTextPane swkjtextpane = null;
        Style style = null;

        void exec(final SwkJTextPane swkjtextpane, final Style style) {
            this.swkjtextpane = swkjtextpane;
            this.style = style;
            execOnThread();
        }

        @Override
        public void run() {
            swkjtextpane.doc.styleUpdated(swkjtextpane, style, false);
        }
    }

    private static class StyleGet extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String arg1 = null;
        Style style = null;
        boolean addIf = false;

        Style exec(final SwkJTextPane swkjtextpane, final String arg1,
                final boolean addIf) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            this.addIf = addIf;
            execOnThread();

            return style;
        }

        @Override
        public void run() {
            style = swkjtextpane.getStyle(arg1);

            if ((style == null) && addIf) {
                style = swkjtextpane.addStyle(arg1, null);
                style.addAttribute("tagName", arg1);
            }
        }
    }

    private static class TagGet extends GetValueOnEventThread {

        static final int RANGES = 0;
        static final int NAMES = 1;
        SwkJTextPane swkjtextpane = null;
        String arg1 = null;
        String result = null;
        ArrayList resultList = null;
        int mode = 0;

        String ranges(final SwkJTextPane swkjtextpane, final String arg1) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            mode = RANGES;
            execOnThread();

            return result;
        }

        ArrayList names(final SwkJTextPane swkjtextpane, final String arg1) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            mode = NAMES;
            execOnThread();

            return resultList;
        }

        @Override
        public void run() {
            switch (mode) {
                case RANGES:
                    runRanges();

                    break;

                case NAMES:
                    runNames();

                    break;

                default:
            }
        }

        public void runRanges() {
            result = swkjtextpane.doc.getRanges(arg1, 0);
        }

        public void runNames() {
            if (arg1 == null) {
                resultList = swkjtextpane.doc.getTags();
            } else {
                int index = swkjtextpane.doc.getIndexLC(swkjtextpane, arg1);
                Element elem = swkjtextpane.doc.getCharacterElement(index);

                if (elem != null) {
                    AttributeSet attrs = elem.getAttributes();

                    if (attrs != null) {
                        String tag = (String) attrs.getAttribute("tagName");
                        resultList = new ArrayList();
                        resultList.add(tag);
                    }
                }
            }
        }
    }

    private static class MarkSet extends UpdateOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String arg1 = null;
        String arg2 = null;
        String[] marks = null;
        int mode = 0;

        void exec(final SwkJTextPane swkjtextpane, final String arg1,
                final String arg2) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            this.arg2 = arg2;
            execOnThread();
        }

        void execGravity(final SwkJTextPane swkjtextpane, final String arg1,
                final String arg2) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            this.arg2 = arg2;
            mode = 1;
            execOnThread();
        }

        void execUnMark(final SwkJTextPane swkjtextpane, final String[] marks) {
            this.swkjtextpane = swkjtextpane;
            this.marks = marks;
            mode = 2;
            execOnThread();
        }

        @Override
        public void run() {
            if (mode == 0) {
                swkjtextpane.doc.setMark(swkjtextpane, arg1, arg2);
            } else if (mode == 1) {
                swkjtextpane.doc.setMarkGravity(swkjtextpane, arg1, arg2);
            } else {
                swkjtextpane.doc.unsetMarks(marks);
            }
        }
    }

    private static class MarkGet extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        ArrayList result = null;

        ArrayList exec(final SwkJTextPane swkjtextpane) {
            this.swkjtextpane = swkjtextpane;
            execOnThread();

            return result;
        }

        @Override
        public void run() {
            result = swkjtextpane.doc.getMarks();
        }
    }

    private static class Compare extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String arg1 = "";
        String op = "";
        String arg2 = "";
        Result result = null;

        Result exec(final SwkJTextPane swkjtextpane, final String arg1,
                final String op, final String arg2) {
            this.swkjtextpane = swkjtextpane;
            this.arg1 = arg1;
            this.op = op;
            this.arg2 = arg2;
            execOnThread();

            return result;
        }

        @Override
        public void run() {
            result = swkjtextpane.doc.compareIndices(swkjtextpane, arg1, op,
                    arg2);
        }
    }

    private static class Search extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String patternString = null;
        String index1Arg = null;
        String index2Arg = null;
        int searchFlags = 0;
        Result result = null;

        Result exec(final SwkJTextPane swkjtextpane,
                final String patternString, final String index1Arg,
                final String index2Arg, final int searchFlags) {
            this.swkjtextpane = swkjtextpane;
            this.patternString = patternString;
            this.index1Arg = index1Arg;
            this.index2Arg = index2Arg;
            this.searchFlags = searchFlags;
            execOnThread();

            return result;
        }

        @Override
        public void run() {
            int index1 = -1;
            int index2 = -1;
            try {
                index1 = swkjtextpane.doc.getIndexLC(swkjtextpane, index1Arg);
                if (index2Arg != null) {
                    if (index2Arg.equals("end")) {
                        if (index1 == -1) {
                            return;
                        }
                    }
                    index2 = swkjtextpane.doc.getIndexLC(swkjtextpane, index2Arg);
                }
            } catch (IllegalArgumentException iaE) {
                result = new Result();
                result.setError(iaE.getMessage());
                return;
            }

            SwkDocumentSearch docSearch = new SwkDocumentSearch(swkjtextpane.doc,
                    patternString, searchFlags);
            result = docSearch.search(index1, index2);
        }
    }

    private static class Get extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String[] tagStrings = null;
        Result result = new Result();

        Result exec(final SwkJTextPane swkjtextpane, final String[] tagStrings) {
            this.swkjtextpane = swkjtextpane;
            this.tagStrings = tagStrings;
            execOnThread();
            return result;
        }

        @Override
        public void run() {
            int nEntries = tagStrings.length;
            if ((nEntries % 2) == 1) {
                 nEntries++;
            }
            int[] indices = new int[nEntries];
            int endOffset = swkjtextpane.doc.getEndPosition().getOffset();
            for (int i=0;i<tagStrings.length;i++) {
                try {
                    indices[i] = swkjtextpane.doc.getIndexLC(swkjtextpane, tagStrings[i]);
                } catch (Exception e) {
                    result.setError(e.getMessage());
                    return;
                }
            }
            if ((tagStrings.length % 2) == 1) {
                indices[nEntries-1] = indices[nEntries-2]+1;
            }
            if (nEntries == 2) {
               if (indices[1] > indices[0]) {
                   try {
                       result.s = swkjtextpane.doc.getText(indices[0], indices[1] - indices[0]);
                   } catch (BadLocationException badLoc) {
                       result.setError(badLoc.toString());
                       return;
                   }
               }
           } else {
                String[] resultStrings = new String[nEntries/2];
                for (int i=0;i<nEntries;i += 2) {
                   if (indices[i+1] > indices[i]) {
                       try {
                           resultStrings[i/2] = swkjtextpane.doc.getText(indices[i], indices[i+1] - indices[i]);
                       } catch (BadLocationException badLoc) {
                           result.setError(badLoc.toString());
                           return;
                       }
                   }
               }
               result.o = resultStrings;
           }
        }
    }

    private static class Insert extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String indexArg = null;
        String[] groupedStyleStrings = null;
        String[][] tagStrings = null;
        String[] insertStrings = null;

        void exec(final SwkJTextPane swkjtextpane, final String indexArg,
                final String[] groupedStyleStrings, final String[][] tagStrings,
                final String[] insertStrings) {
            this.swkjtextpane = swkjtextpane;
            this.indexArg = indexArg;
            this.groupedStyleStrings = groupedStyleStrings;
            this.tagStrings = tagStrings;
            this.insertStrings = insertStrings;
            execOnThread();
        }

        @Override
        public void run() {
            int index = 0;

            try {
                index = swkjtextpane.doc.getIndexLC(swkjtextpane, indexArg);
            } catch (Exception e) {
                // FIXME
                System.out.println("caught " + e);

                return;
            }

            int endOffset = swkjtextpane.doc.getEndPosition().getOffset();

            if (index >= endOffset) {
                index = endOffset - 1;
            }

            Style style = null;
            Style firstStyle = null;
            Style groupedStyle = null;

            TclObject[] tags = null;

            if (!swkjtextpane.isEditable()) {
                return;
            }

            for (int i = 0; i < groupedStyleStrings.length; i++) {
                if (groupedStyleStrings[i] != null) {
                    groupedStyle = swkjtextpane.getStyle(groupedStyleStrings[i]);

                    if (groupedStyle == null) {
                        groupedStyle = swkjtextpane.addStyle(groupedStyleStrings[i],
                                null);
                        swkjtextpane.initStyle(groupedStyle);

                        for (int j = 0; j < tagStrings[i].length; j++) {
                            style = swkjtextpane.getStyle(tagStrings[i][j]);

                            if (style == null) {
                                style = swkjtextpane.addStyle(tagStrings[i][j],
                                        null);
                                style.addAttribute("tagName", tagStrings[i][j]);
                            } else {
                                groupedStyle.addAttributes(style);
                            }

                            if (j == 0) {
                                firstStyle = style;
                            }
                        }

                        groupedStyle.addAttribute("tagName",
                                groupedStyleStrings[i]);
                    }
                }

                try {
                    if (groupedStyle == null) {
                        swkjtextpane.doc.insertString(index, insertStrings[i],
                                swkjtextpane.defaultStyle);
                    } else {
                        swkjtextpane.doc.insertString(index, insertStrings[i],
                                groupedStyle);
                    }

                    int index2 = insertStrings[i].length() + index;

                    if (tagStrings[i] != null) {
                        for (int j = 1; j < tagStrings[i].length; j++) {
                            style = swkjtextpane.getStyle(tagStrings[i][j]);

                            if (style != null) {
                                //swkjtextpane.doc.setCharacterAttributes(index, index2 - index, style, false);
                            }
                        }
                    }

                    if (groupedStyle == null) {
                        String tagNames = (String) swkjtextpane.defaultStyle.getAttribute(
                                "tagName");

                        if (tagNames != null) {
                            //System.out.println("insert with de "+tagNames);
                        }

                        //swkjtextpane.doc.setParagraphAttributes(index, index2 - index, swkjtextpane.defaultStyle, true);
                    } else {
                        String tagNames = (String) groupedStyle.getAttribute(
                                "tagName");

                        if (tagNames != null) {
                            //System.out.println("insert with gr "+tagNames);
                        }

                        //swkjtextpane.doc.setParagraphAttributes(index, index2 - index, groupedStyle, true);
                    }
                } catch (BadLocationException badLoc) {
                } catch (NullPointerException nullE) {
                }

                index += insertStrings[i].length();
            }
        }
    }

    static class OffsetPair implements Comparable {
        final int offset;
        final int delta;
        OffsetPair(final int offset, final int delta) {
           this.offset = offset;
           this.delta = delta;
        } 
        public int compareTo(Object objectPair) {
           OffsetPair offsetPair = (OffsetPair) objectPair;
           if ((this.offset + this.delta)  < (offsetPair.offset + offsetPair.delta)) {
               return 1;
           } else if (this.offset == offsetPair.offset) {
               if (this.delta < offsetPair.delta) {
                   return 1;
               } else if (this.delta == offsetPair.delta) {
                   return 0;
               } else {
                   return -1;
               }
           } else {
               return -1;
           }
        }
    }

    private static class Delete extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String[] tagStrings = null;
        Result result = new Result();

        Result exec(final SwkJTextPane swkjtextpane, final String[] tagStrings) {
            this.swkjtextpane = swkjtextpane;
            this.tagStrings = tagStrings;
            execOnThread();
            return result;
        }

        @Override
        public void run() {
            int nEntries = tagStrings.length;
            if ((nEntries % 2) == 1) {
                 nEntries++;
            }
            int[] indices = new int[nEntries];
            int endOffset = swkjtextpane.doc.getEndPosition().getOffset();
            for (int i=0;i<tagStrings.length;i++) {
                try {
                    indices[i] = swkjtextpane.doc.getIndexLC(swkjtextpane, tagStrings[i]);
                } catch (Exception e) {
                    result.setError(e.getMessage());
                    return;
                }
                if (indices[i] >= (endOffset - 1)) {
                    indices[i] = endOffset - 1;
                }
            }
            if (!swkjtextpane.isEditable()) {
                return;
            }
            if ((tagStrings.length % 2) == 1) {
                indices[nEntries-1] = indices[nEntries-2]+1;
            }
            ArrayList<OffsetPair> offsetPairs = new ArrayList<OffsetPair>();
            int nStart=nEntries-1;
            for (int i=0;i<nStart;i += 2) {
               if (indices[i+1] > indices[i]) {
                   OffsetPair offsetPair = new OffsetPair(indices[i],indices[i+1]-indices[i]);
                   offsetPairs.add(offsetPair);
               }
            }
            Collections.sort(offsetPairs);
            int lastOffset = -1;
            try {
                for (int i=0;i<offsetPairs.size();i++) {
                    OffsetPair offsetPair = offsetPairs.get(i);
                    int offset = offsetPair.offset;
                    int delta = offsetPair.delta;
                    if (offset == lastOffset) {
                        continue;
                    }
                    if ((lastOffset != -1) && ((offset+delta) > lastOffset)) {
                        delta = lastOffset-offset;
                    }
                    swkjtextpane.doc.remove(offset, delta);
                    lastOffset = offset;
                }
            } catch (BadLocationException badLoc) {
                //result.setError(badLoc.getMessage());
            }
        }
    }

    private class See extends UpdateOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String indexArg = null;

        void exec(final SwkJTextPane swkjtextpane, final String indexArg) {
            this.swkjtextpane = swkjtextpane;
            this.indexArg = indexArg;
            execOnThread();
        }

        @Override
        public void run() {
            int index1 = 0;

            try {
                index1 = swkjtextpane.doc.getIndexLC(swkjtextpane, indexArg);
            } catch (Exception e) {
                // FIXME
                System.out.println("caught " + e.getMessage());

                return;
            }

            int endOffset = swkjtextpane.doc.getEndPosition().getOffset();

            if (index1 >= endOffset) {
                index1 = endOffset - 1;
            }

            try {
                Rectangle view = swkjtextpane.modelToView(index1);
                if (view != null) {
                    swkjtextpane.scrollRectToVisible(view);
                }
            } catch (BadLocationException badLoc) {
                interp.backgroundError();

                return;
            }
        }
    }

    private class BBox extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String indexArg = null;
        Rectangle rect = null;

        Rectangle exec(final SwkJTextPane swkjtextpane, final String indexArg) {
            this.swkjtextpane = swkjtextpane;
            this.indexArg = indexArg;
            execOnThread();

            return rect;
        }

        @Override
        public void run() {
            int index1 = 0;

            try {
                index1 = swkjtextpane.doc.getIndexLC(swkjtextpane, indexArg);
            } catch (Exception e) {
                // FIXME
                System.out.println("caught " + e.getMessage());

                return;
            }

            int endOffset = swkjtextpane.doc.getEndPosition().getOffset();

            if (index1 >= endOffset) {
                index1 = endOffset - 1;
            }

            try {
                rect = swkjtextpane.modelToView(index1);
            } catch (BadLocationException badLoc) {
                interp.backgroundError();

                return;
            }
        }
    }

    private static class Window extends GetValueOnEventThread {

        SwkJTextPane swkjtextpane = null;
        String indexArg = null;
        String windowName = null;
        Object windowObject = null;

        void exec(final SwkJTextPane swkjtextpane, final String indexArg,
                final String windowName, final Object windowObject) {
            this.swkjtextpane = swkjtextpane;
            this.indexArg = indexArg;
            this.windowObject = windowObject;
            this.windowName = windowName;
            execOnThread();
        }

        @Override
        public void run() {
            int index1 = 0;

            try {
                index1 = swkjtextpane.doc.getIndexLC(swkjtextpane, indexArg);
                swkjtextpane.doc.setMark(swkjtextpane, windowName, indexArg);
            } catch (Exception e) {
                // FIXME
                System.out.println("caught " + e.getMessage());

                return;
            }

            int endOffset = swkjtextpane.doc.getEndPosition().getOffset();

            if (index1 >= endOffset) {
                index1 = endOffset - 1;
            }

            swkjtextpane.setCaretPosition(index1);

            //System.out.println("insert mark");
            //System.out.println("insert comp "+argv[i].toString());
            swkjtextpane.insertComponent((Component) windowObject);
        }
    }
}
