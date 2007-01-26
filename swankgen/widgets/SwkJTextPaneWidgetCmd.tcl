#
# 
# Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, NJ, USA
#
# See the file \"LICENSE\" for information on usage and redistribution
# of this file.
# IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
# ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
# CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
# SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
# EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#
# THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
# PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
# IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
# DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
# SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
#
#

append specialVars {
	Point currentPt = new Point(0,0);
	String currentTag = null;
	String previousTag = null;
	TclObject currentTags[]=null;
	TclObject previousTags[]=null;
	
	Hashtable focusHash=null;
	Hashtable mouseHash=null;
	Hashtable mouseMotionHash = null;
	Hashtable keyHash=null;
	boolean selectionWindowAdded = false;
    Style defaultStyle = null;
	
	
 JViewport jview=null;
  JScrollPane jscroll=null;
  JComponent packComponent=null;
SwkDefaultStyledDocument	doc = null;
			SwkDocumentListener docListener=null;
}
append specialImports {
import java.io.IOException;
import java.net.URL;
}
append specialListeners {
}


append specialInits {
         
        if (className.equals("Text")) {
            jview = new JViewport();
            packComponent = jview;
            jview.setView(this);
        } else if (className.equals("Stext")) {
            jscroll = new JScrollPane(this);
            jview = jscroll.getViewport();
            packComponent = jscroll;
        } else {
            packComponent = this;
        }
        doc = new SwkDefaultStyledDocument();
        setStyledDocument(doc);
        if (defaultStyle == null) {
            defaultStyle = this.addStyle("__DEFAULT__", null);
        }
        initStyle(defaultStyle);
        addMouseListener( new MouseListener() {
            
            public void mousePressed(MouseEvent mEvent) {
                currentTags = getTagFromEvent(mEvent);
                if (currentTags == null) {
                    return;
                }
                for (int i=0;i<currentTags.length;i++) {
                    currentTag = currentTags[i].toString();
                    processMouse(mEvent,SwkBinding.MOUSE,SwkBinding.PRESS);
                }
                
            }
            public void mouseReleased(MouseEvent mEvent) {
                currentTags = getTagFromEvent(mEvent);
                if (currentTags == null) {
                    return;
                }
                for (int i=0;i<currentTags.length;i++) {
                    currentTag = currentTags[i].toString();
                    processMouse(mEvent,SwkBinding.MOUSE,SwkBinding.RELEASE);
                }
            }
            public void mouseClicked(MouseEvent mEvent) {
                currentTags = getTagFromEvent(mEvent);
                if (currentTags == null) {
                    return;
                }
                for (int i=0;i<currentTags.length;i++) {
                    currentTag = currentTags[i].toString();
                    processMouse(mEvent,SwkBinding.MOUSE,SwkBinding.CLICK);
                }
            }
            public void mouseEntered(MouseEvent mEvent) {
            }
            public void mouseExited(MouseEvent mEvent) {
            }
        }
        );
        
        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged(MouseEvent mEvent) {
                processMouseMotion(mEvent);
                
            }
            public void mouseMoved(MouseEvent mEvent) {
                processMouseMotion(mEvent);
            }
        }
        );
        
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                FocusCmd.focusWindow = e.getComponent().getName();
            }
            public void focusLost(FocusEvent e) {
            }
        }
        );
 
}
  


append specialMethods {

    void processMouseClick(MouseEvent e) {
    }

    void processMouseMotion(MouseEvent mEvent) {
        previousTags = currentTags;

        boolean stillPresent = false;
        boolean wasPresent = false;

        currentTags = getTagFromEvent(mEvent);

        /*
        if (previousTags != null) {
            for (int i=0;i<previousTags.length;i++) {
                System.out.println("prev "+previousTags[i].toString());
            }
        }
        if (currentTags != null) {
            for (int i=0;i<currentTags.length;i++) {
                System.out.println("current "+currentTags[i].toString());
            }
        }
        */
        if ((currentTags == null) && (previousTags != null)) {
            for (int i = 0; i < previousTags.length; i++) {
                previousTag = previousTags[i].toString();
                processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.EXIT);
            }
        } else if ((currentTags != null) && (previousTags != null)) {
            for (int i = 0; i < previousTags.length; i++) {
                previousTag = previousTags[i].toString();
                stillPresent = false;

                for (int j = 0; j < currentTags.length; j++) {
                    currentTag = currentTags[j].toString();

                    if (previousTags[i].toString().equals(currentTag)) {
                        stillPresent = true;

                        break;
                    }
                }

                if (!stillPresent) {
                    processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.EXIT);
                }
            }
        }

        if (currentTags != null) {
            for (int i = 0; i < currentTags.length; i++) {
                currentTag = currentTags[i].toString();

                //System.out.println("processMotion "+currentTag);
                processMouse(mEvent, SwkBinding.MOUSEMOTION, SwkBinding.MOTION);
            }
        }

        if ((currentTags != null) && (previousTags == null)) {
            for (int i = 0; i < currentTags.length; i++) {
                currentTag = currentTags[i].toString();
                processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.ENTER);
            }
        } else if ((currentTags != null) && (previousTags != null)) {
            for (int i = 0; i < currentTags.length; i++) {
                currentTag = currentTags[i].toString();
                wasPresent = false;

                for (int j = 0; j < previousTags.length; j++) {
                    if (previousTags[j].toString().equals(currentTags[i].toString())) {
                        wasPresent = true;

                        break;
                    }
                }

                if (!wasPresent) {
                    processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.ENTER);
                }
            }
        }
    }

    public void processMouse(MouseEvent e, int type, int subtype) {
        Vector bindings = null;

        if (type == SwkBinding.FOCUS) {
            if ((currentTag != null) && (focusHash != null)) {
                bindings = (Vector) focusHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.MOUSE) {
            if (subtype == SwkBinding.EXIT) {
                if ((previousTag != null) && (mouseHash != null)) {
                    bindings = (Vector) mouseHash.get(previousTag);
                }
            } else {
                if ((currentTag != null) && (mouseHash != null)) {
                    bindings = (Vector) mouseHash.get(currentTag);
                }
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.MOUSEMOTION) {
            //System.out.println("processing motion");
            if ((currentTag != null) && (mouseMotionHash != null)) {
                bindings = (Vector) mouseMotionHash.get(currentTag);

                //System.out.println("gotbinding");
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.KEY) {
            if ((currentTag != null) && (keyHash != null)) {
                bindings = (Vector) keyHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        }

        TextMouseEvent bEvent = new TextMouseEvent(interp, this, e, bindings,
                type, subtype);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processMouseEvent(MouseEvent e, Vector bindings, int type,
        int subtype) {
        SwkBinding binding;
        int buttonMask;
        int mods = e.getModifiersEx();
        int buttons = e.getButton();

        for (int i = 0; i < bindings.size(); i++) {
            binding = (SwkBinding) bindings.elementAt(i);

            // System.out.println(type+" event "+subtype+" "+" "+binding.type+" binding "+binding.subtype);
            //   System.out.println(e.getClickCount()+" count "+binding.count);
            if (binding.subtype != subtype) {
                continue;
            }

            if ((subtype != SwkBinding.ENTER) && (subtype != SwkBinding.EXIT)) {
                if ((type != SwkBinding.MOUSEMOTION) &&
                        (e.getClickCount() > 0) &&
                        (binding.count != e.getClickCount())) {
                    continue;
                }

                if (type == SwkBinding.MOUSEMOTION) {
                    if (!SwkMouseMotionListener.checkButtonState(e,
                                binding.mod, mods)) {
                        continue;
                    }
                } else if (type == SwkBinding.MOUSE) {
                    if (!SwkMouseMotionListener.checkButtons(binding.detail,
                                buttons)) {
                        continue;
                    }
                }

                //  System.out.println("check mods");
                if (!SwkMouseMotionListener.checkMods(binding.mod, mods)) {
                    continue;
                }
            }

            //  System.out.println("gotit");
            if ((binding.command != null) && (binding.command.length() != 0)) {
                try {
                    BindCmd.doCmd(interp, binding.command, e);
                } catch (TclException tclE) {
                    if (tclE.getCompletionCode() == TCL.BREAK) {
                        break;
                    } else {
                        interp.addErrorInfo("\n    (\"binding\" script)");
                        interp.backgroundError();
                    }
                }
            }
        }
    }

    public TclObject[] getTagFromEvent(MouseEvent mEvent) {
        String tagName = null;
        currentPt.setLocation(mEvent.getX(), mEvent.getY());

        int offset = viewToModel(currentPt);
        boolean onTag = false;

        try {
            //System.out.println(modelToView(offset));
            Rectangle view = modelToView(offset);
            int dy = mEvent.getY() - view.y;
            int dx = Math.abs(mEvent.getX() - view.x);

            if ((dy >= 2) && (dy < (view.height - 2)) && (dx >= 0) &&
                    (dx < ((view.height * 3) / 4))) {
                onTag = true;
            }
        } catch (BadLocationException blE) {
        }

        if (onTag) {
            Element elem = doc.getCharacterElement(offset);

            if (elem != null) {
                AttributeSet attrs = elem.getAttributes();

                if (attrs != null) {
                    tagName = (String) attrs.getAttribute("tagName");

                    //System.out.println("tagName "+tagName);
                }
            }
        }

        if (tagName != null) {
            try {
                TclObject[] tags = TclList.getElements(interp,
                        TclString.newInstance(tagName));

                return (tags);
            } catch (TclException tclE) {
                return (null);
            }
        } else {
            return (null);
        }
    }

    public void setupBinding(Interp interp, SwkBinding binding, String tagName) {
        Vector bindVec = null;

        if (binding.type == SwkBinding.FOCUS) {
            if (focusHash == null) {
                focusHash = new Hashtable();
                bindVec = new Vector(2);
                focusHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) focusHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    focusHash.put(tagName, bindVec);
                }
            }

            bindVec.addElement(binding);
        } else if (binding.type == SwkBinding.MOUSE) {
            if (mouseHash == null) {
                mouseHash = new Hashtable();
                bindVec = new Vector(2);
                mouseHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) mouseHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    mouseHash.put(tagName, bindVec);
                }
            }

            bindVec.addElement(binding);
        } else if (binding.type == SwkBinding.MOUSEMOTION) {
            if (mouseMotionHash == null) {
                mouseMotionHash = new Hashtable();
                bindVec = new Vector(2);
                mouseMotionHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) mouseMotionHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    mouseMotionHash.put(tagName, bindVec);
                }
            }

            bindVec.addElement(binding);
        } else if (binding.type == SwkBinding.KEY) {
            if (keyHash == null) {
                keyHash = new Hashtable();
                bindVec = new Vector(2);
                keyHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) keyHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    keyHash.put(tagName, bindVec);
                }
            }

            bindVec.addElement(binding);
        }
    }

    protected boolean processKeyBinding(KeyStroke keyStroke, KeyEvent e,
        int condition, boolean pressed) {
        if (processNativeKeyBindings) {
            return super.processKeyBinding(keyStroke, e, condition, pressed);
        } else {
            return true;
        }
    }

    static void setStyleStuff(Interp interp, Style style, TclObject[] argv,
        int start) throws TclException {
        int i;
        String argType;

        for (i = start; i < argv.length; i += 2) {
            argType = argv[i].toString();

            if (((i + 1) == argv.length)) {
                throw new TclException(interp,
                    "value for \"" + argv[i].toString() + "\" missing");
            }

            if (argType.equals("-font")) {
                if (argv[i + 1].toString().length() > 0) {
                    setFontStuff(interp, style, argv[i + 1]);
                } else {
                    //setFontStuff(interp,style,defaultFont);
                }
            } else if (argType.equals("-background")) {
                if (argv[i + 1].toString().length() > 0) {
                    Color background = SwankUtil.getColor(interp, argv[i + 1]);
                    StyleConstants.setBackground(style, background);
                } else {
                    StyleConstants.setBackground(style, Color.white);
                }
            } else if (argType.equals("-foreground")) {
                if (argv[i + 1].toString().length() > 0) {
                    Color foreground = SwankUtil.getColor(interp, argv[i + 1]);

                    if (foreground == null) {
                        throw new TclException(interp, "invalid color");
                    }

                    StyleConstants.setForeground(style, foreground);
                } else {
                    StyleConstants.setForeground(style, Color.black);
                }
            } else if (argType.equals("-justify")) {
                if (argv[i + 1].toString().length() > 0) {
                    if ("left".startsWith(argv[i + 1].toString())) {
                        StyleConstants.setAlignment(style,
                            StyleConstants.ALIGN_LEFT);
                    } else if ("right".startsWith(argv[i + 1].toString())) {
                        StyleConstants.setAlignment(style,
                            StyleConstants.ALIGN_RIGHT);
                    } else if ("center".startsWith(argv[i + 1].toString())) {
                        StyleConstants.setAlignment(style,
                            StyleConstants.ALIGN_CENTER);
                    } else if ("justify".startsWith(argv[i + 1].toString())) {
                        StyleConstants.setAlignment(style,
                            StyleConstants.ALIGN_JUSTIFIED);
                    } else {
                        throw new TclException(interp, "invalid justification");
                    }
                } else {
                    StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
                }
            } else if (argType.equals("-underline")) {
                if (argv[i + 1].toString().length() > 0) {
                    boolean underline = TclBoolean.get(interp, argv[i + 1]);
                    StyleConstants.setUnderline(style, underline);
                } else {
                    StyleConstants.setUnderline(style, false);
                }
            } else if (argType.equals("-overstrike")) {
                if (argv[i + 1].toString().length() > 0) {
                    boolean strikethrough = TclBoolean.get(interp, argv[i + 1]);
                    StyleConstants.setStrikeThrough(style, strikethrough);
                } else {
                    StyleConstants.setStrikeThrough(style, false);
                }
            } else if (argType.equals("-strikethrough")) {
                if (argv[i + 1].toString().length() > 0) {
                    boolean strikethrough = TclBoolean.get(interp, argv[i + 1]);
                    StyleConstants.setStrikeThrough(style, strikethrough);
                } else {
                    StyleConstants.setStrikeThrough(style, false);
                }
            } else if (argType.equals("-superscript")) {
                if (argv[i + 1].toString().length() > 0) {
                    boolean superscript = TclBoolean.get(interp, argv[i + 1]);
                    StyleConstants.setSuperscript(style, superscript);
                } else {
                    StyleConstants.setSuperscript(style, false);
                }
            } else if (argType.equals("-subscript")) {
                if (argv[i + 1].toString().length() > 0) {
                    boolean subscript = TclBoolean.get(interp, argv[i + 1]);
                    StyleConstants.setSubscript(style, subscript);
                } else {
                    StyleConstants.setSubscript(style, false);
                }
            } else if (argType.equals("-lmargin1")) {
                if (argv[i + 1].toString().length() > 0) {
                    float leftMargin1 = (float) SwankUtil.getTkSizeD(interp,
                            null, argv[i + 1]);
                    StyleConstants.setFirstLineIndent(style, leftMargin1);
                } else {
                    StyleConstants.setFirstLineIndent(style, 0.0f);
                }
            } else if (argType.equals("-lmargin2")) {
                if (argv[i + 1].toString().length() > 0) {
                    float leftMargin2 = (float) SwankUtil.getTkSizeD(interp,
                            null, argv[i + 1]);
                    StyleConstants.setLeftIndent(style, leftMargin2);
                } else {
                    StyleConstants.setLeftIndent(style, 0.0f);
                }
            } else if (argType.equals("-rmargin")) {
                if (argv[i + 1].toString().length() > 0) {
                    float rightMargin = (float) SwankUtil.getTkSizeD(interp,
                            null, argv[i + 1]);
                    StyleConstants.setRightIndent(style, rightMargin);
                } else {
                    StyleConstants.setRightIndent(style, 0.0f);
                }
            } else if (argType.equals("-spacing1")) {
                if (argv[i + 1].toString().length() > 0) {
                    float spaceAbove = (float) SwankUtil.getTkSizeD(interp,
                            null, argv[i + 1]);
                    StyleConstants.setSpaceAbove(style, spaceAbove);
                } else {
                    StyleConstants.setSpaceAbove(style, 1.0f);
                }
            } else if (argType.equals("-spacing2")) {
                if (argv[i + 1].toString().length() > 0) {
                    float lineSpacing = (float) SwankUtil.getTkSizeD(interp,
                            null, argv[i + 1]);
                    StyleConstants.setLineSpacing(style, lineSpacing);
                } else {
                    StyleConstants.setLineSpacing(style, 1.0f);
                }
            } else if (argType.equals("-spacing3")) {
                if (argv[i + 1].toString().length() > 0) {
                    float spaceBelow = (float) SwankUtil.getTkSizeD(interp,
                            null, argv[i + 1]);
                    StyleConstants.setSpaceBelow(style, spaceBelow);
                } else {
                    StyleConstants.setSpaceBelow(style, 1.0f);
                }
            }
        }
    }

    void getStyleStuff(Interp interp, Style style, TclObject argv,
        boolean configMode) throws TclException {
        int i;
        String argType = null;

        if (argv != null) {
            argType = argv.toString();
        }

        TclObject list = null;
        TclObject listAll = TclList.newInstance();

        if ((argType == null) || argType.equals("-font")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list, TclString.newInstance("-font"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclString.newInstance(SwankUtil.parseFont(doc.getFont(style))));
            } else {
                interp.setResult(SwankUtil.parseFont(doc.getFont(style)));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        if ((argType == null) || argType.equals("-background")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list,
                    TclString.newInstance("-background"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclString.newInstance(SwankUtil.parseColor(StyleConstants.getBackground(style))));
            } else {
                interp.setResult(SwankUtil.parseColor(StyleConstants.getBackground(style)));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        if ((argType == null) || argType.equals("-foreground")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list,
                    TclString.newInstance("-foreground"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclString.newInstance(SwankUtil.parseColor(StyleConstants.getForeground(style))));
            } else {
                interp.setResult(SwankUtil.parseColor(StyleConstants.getForeground(style)));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        if ((argType == null) || argType.equals("-underline")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list, TclString.newInstance("-underline"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclBoolean.newInstance(StyleConstants.isUnderline(style)));
            } else {
                interp.setResult(StyleConstants.isUnderline(style));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        if ((argType == null) || argType.equals("-overstrike")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list,
                    TclString.newInstance("-overstrike"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclBoolean.newInstance(StyleConstants.isStrikeThrough(style)));
            } else {
                interp.setResult(StyleConstants.isStrikeThrough(style));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        if ((argType == null) || argType.equals("-strikethrough")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list,
                    TclString.newInstance("-strikethrough"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclBoolean.newInstance(StyleConstants.isStrikeThrough(style)));
            } else {
                interp.setResult(StyleConstants.isStrikeThrough(style));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        if ((argType == null) || argType.equals("-superscript")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list,
                    TclString.newInstance("-superscript"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclBoolean.newInstance(StyleConstants.isSuperscript(style)));
            } else {
                interp.setResult(StyleConstants.isSuperscript(style));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        if ((argType == null) || argType.equals("-subscript")) {
            if (configMode) {
                list = TclList.newInstance();
                TclList.append(interp, list, TclString.newInstance("-subscript"));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list, TclString.newInstance(""));
                TclList.append(interp, list,
                    TclBoolean.newInstance(StyleConstants.isSubscript(style)));
            } else {
                interp.setResult(StyleConstants.isSubscript(style));

                return;
            }

            if (argType != null) {
                interp.setResult(list);

                return;
            } else {
                TclList.append(interp, listAll, list);
            }
        }

        interp.setResult(listAll);
    }

    void initStyle(Style style) {
        StyleConstants.setFontFamily(style, this.getFont().getFamily());
        StyleConstants.setFontSize(style, this.getFont().getSize());
        StyleConstants.setItalic(style, false);
        StyleConstants.setBold(style, false);
        StyleConstants.setBackground(style, this.getBackground());
        StyleConstants.setForeground(style, this.getForeground());
        StyleConstants.setUnderline(style, false);
        StyleConstants.setStrikeThrough(style, false);
        StyleConstants.setStrikeThrough(style, false);
        StyleConstants.setSuperscript(style, false);
        StyleConstants.setSubscript(style, false);
        StyleConstants.setAlignment(style,
            StyleConstants.ParagraphConstants.ALIGN_LEFT);
        StyleConstants.setFirstLineIndent(style, 0.0f);
        StyleConstants.setLeftIndent(style, 0.0f);
        StyleConstants.setRightIndent(style, 0.0f);

        //StyleConstants.setSpaceAbove(defaultStyle,1.0f);
        //StyleConstants.setLineSpacing(defaultStyle,1.0f);
        //StyleConstants.setSpaceBelow(defaultStyle,1.0f);
    }

    static void setFontStuff(Interp interp, Style style, TclObject fontArg)
        throws TclException {
        TclObject[] fontAttr = TclList.getElements(interp, fontArg);
        String name = fontAttr[0].toString();
        StyleConstants.setFontFamily(style, name);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);

        if (fontAttr.length > 1) {
            int pointSize = TclInteger.get(interp, fontAttr[1]);
            StyleConstants.setFontSize(style, pointSize);
        }

        int i;

        for (i = 2; i < fontAttr.length; i++) {
            if (fontAttr[i].toString().startsWith("i")) {
                StyleConstants.setItalic(style, true);
            } else if (fontAttr[i].toString().startsWith("b")) {
                StyleConstants.setBold(style, true);
            }
        }
    }

    public void setPage(String urlPage) {
        try {
            URL url = SwankUtil.getURL(interp, urlPage);
            super.setPage(url);
        } catch (IOException ioE) {
        } catch (TclException tclE) {
        }
    }

    public void setPage(URL url) {
        try {
            super.setPage(url);
        } catch (IOException ioE) {
        }
    }

  
}
