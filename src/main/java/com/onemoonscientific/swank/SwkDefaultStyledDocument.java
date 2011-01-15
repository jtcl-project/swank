/*

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
import java.awt.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class SwkDefaultStyledDocument extends DefaultStyledDocument
        implements DocumentListener {

    static Hashtable resourceDB = null;
    String name = null;
    TclObject tclObject = null;
    Hashtable marks = null;
    Vector paragraphs;
    Position endPosition = null;
    int[] indexes = null;
    Vector rangeOffsets = new Vector();
    int swkwidth;
    boolean removeInProgress = false;
    // This Hashtable maps from Style -> Hashtable<Element, null>. That is, each
    // key is a Style. The values are Hashtables, the keys of which are the
    // Elements that use the Style. The values of the inner Hashtables are useless
    // (we should use a "Set" data structure, but in JDK 1.1 there is none).
    private Hashtable styleHash = new Hashtable();

    SwkDefaultStyledDocument() {
        super();
        init();

        marks = new Hashtable();
        paragraphs = new Vector();
        endPosition = getEndPosition();

        if (resourceDB == null) {
            resourceDB = new Hashtable();
            initResources();
        }
    }

    // We listen to ourself. Also, we add the first paragraph to our style
    // hashtable since we won't get notified that it was added.
    protected void init() {
        addDocumentListener(this);

        //    addToStyleHash(getParagraphElement(0));
    }

    // This method indicates that the definition of the given style has changed. It
    // goes through each of the Elements that use the style and fires an event
    // indicating that the attributes for the Element have changed. This causes the
    // View to re-check the attributes and redraw.
    public void styleUpdated(SwkJTextPane swkjtextpane, Style style,
            boolean remove) {
        // Find the set of Elements that use this style . . .
        //    AttributeSet attrs = style.getAttributes();
        //   if (attrs == null) {return;}
        String tagName = (String) style.getAttribute("tagName");

        //System.out.println("styleUpdated "+tagName);
        if (tagName == null) {
            return;
        }

        StyleContext rStyle = new StyleContext();
        endPosition = getEndPosition();

        int endOffset = endPosition.getOffset();

        if (endOffset <= 0) {
            return;
        }

        Hashtable ht = (Hashtable) styleHash.get(tagName);
        String[] styleList = null;
        Style nextStyle = null;

        if (ht != null) {
            // somebody's using it if we get here.
            // Create a Vector of Elements that shouldn't be in this table because they
            // no longer use this Style (we don't remove them when they change Styles,
            // so they will still be here)
            Vector cleanUp = new Vector();

            // Update each Element . . .
            Enumeration e = ht.keys();

            while (e.hasMoreElements()) {
                Element el = (Element) e.nextElement();
                int start = el.getStartOffset();
                int end = el.getEndOffset();
                AttributeSet attrs = el.getAttributes();

                if (attrs == null) {
                    continue;
                }

                String checkName = (String) attrs.getAttribute("tagName");

                if (checkName == null) {
                    continue;
                }

                //System.out.println("checkName "+checkName);
                // Fire an event only if this Element is still using this Style.
                //if (checkName.equals( tagName)) {
                int checkIndex = checkName.indexOf(tagName);
                int lastChar = checkIndex + tagName.length();

                if ((checkIndex != -1)
                        && ((checkIndex == 0)
                        || (checkName.charAt(checkIndex - 1) == ' '))
                        && ((lastChar == checkName.length())
                        || (checkName.charAt(lastChar) == ' '))) {
                    styleList = checkName.split("\\s", -1);

                    if (remove) {
                        //System.out.println("remove style "+start+" "+end);
                        this.setCharacterAttributes(start, end - start,
                                rStyle.getEmptySet(), true);

                        //System.out.println("empty set");
                        //this.setParagraphAttributes(start, end - start,rStyle.getEmptySet() , true);
                        //System.out.println("empty set para");
                    } else {
                        Style groupedStyle = swkjtextpane.getStyle(checkName);

                        for (int j = 0; j < styleList.length; j++) {
                            /*
                            if (!styleList[j].toString ().equals (tagName)) {
                            continue;
                            }
                             */

                            //System.out.println("styleList "+styleList[j].toString()+" "+start+" "+end);
                            if (styleList.length == 1) {
                                nextStyle = style;
                            } else {
                                nextStyle = swkjtextpane.getStyle(styleList[j].toString());
                            }

                            if (j == 0) {
                                // this.setCharacterAttributes (start, end - start, nextStyle, true);
                            } else {
                                //this.setCharacterAttributes (start, end - start, nextStyle, false);
                            }

                            groupedStyle.addAttributes(nextStyle);
                        }

                        groupedStyle.addAttribute("tagName", checkName);

                        // here is problem
                        //nextStyle = swkjtextpane.getStyle (checkName);
                        if (groupedStyle != null) {
                            this.setCharacterAttributes(start, end - start,
                                    groupedStyle, true);
                        }

                        DefaultDocumentEvent ev = new DefaultDocumentEvent(start,
                                end - start, DocumentEvent.EventType.CHANGE);
                        fireChangedUpdate(ev);
                    }
                } else {
                    // If not, remove this Element, since it no longer uses this Style
                    cleanUp.addElement(el);
                }
            }

            // Clean up . . .
            e = cleanUp.elements();

            while (e.hasMoreElements()) {
                Element bad = (Element) e.nextElement();
                ht.remove(bad);
            }
        }
    }

    // Document Listener Methods
    // Call updateStyleHash() whenever text is inserted
    public void insertUpdate(DocumentEvent ev) {
        //System.out.println("insert "+ev.toString());
        updateStyleHash(ev);
    }

    // Call updateStyleHash() whenever text is removed
    public void removeUpdate(DocumentEvent ev) {
        //System.out.println("remove");
        updateStyleHash(ev);
    }

    // Whenever attributes change, add the paragraph that was changed to our hash.
    public void changedUpdate(DocumentEvent ev) {
        if (removeInProgress) {
            return;
        }

        //System.out.println("change "+ev.toString());
        int offset = ev.getOffset();

        //System.out.println("offset "+offset);
        Element elem = ((SwkDefaultStyledDocument) ev.getDocument()).getCharacterElement(offset);
        addToStyleHash(elem);
    }

    // Internal Methods
    // Called to see if there are any added or removed Elements. If there are any,
    // we need to update our hash.
    protected void updateStyleHash(DocumentEvent ev) {
        int offset = ev.getOffset();

        Element elem = ((SwkDefaultStyledDocument) ev.getDocument()).getCharacterElement(offset);
        Element parent = elem.getParentElement();

        DocumentEvent.ElementChange chg = ev.getChange(parent);

        if (chg != null) {
            Element[] removed = chg.getChildrenRemoved();

            for (int i = 0; i < removed.length; i++) {
                removeFromStyleHash(removed[i]);
            }

            Element[] added = chg.getChildrenAdded();

            for (int i = 0; i < added.length; i++) {
                addToStyleHash(added[i]);
            }
        }
    }

    // Called to add an Element to our hash.
    protected void addToStyleHash(Element elem) {
        AttributeSet attrs = elem.getAttributes();
        String[] tagList = null;

        if (attrs != null) {
            String tagNames = (String) attrs.getAttribute("tagName");

            //System.out.println("add to stylehash "+tagNames);
            if (tagNames == null) {
                return;
            }

            tagList = tagNames.split("\\s", -1);

            String tagName = null;

            for (int i = 0; i < tagList.length; i++) {
                tagName = tagList[i];

                // We've got the Style, now see if we've got a set of Elements that
                // use this Style
                Hashtable ht = (Hashtable) styleHash.get(tagName);

                if (ht == null) {
                    // First user of this Style . . .add a new set
                    ht = new Hashtable();
                    styleHash.put(tagName, ht);
                }

                // If this paragraph isn't already in the set, we add it. We really want
                // a Set, not a Hashtable, but to be JDK 1.1 friendly here, we'll use a
                // Hashtable with a throw-away value. We only care about the keys.
                //System.out.println("Add to StyleHash "+tagName+" "+elem.getName()+" "+elem.getElementCount()+" "+elem.getStartOffset()+" "+elem.getEndOffset());
                if (ht.containsKey(elem) == false) {
                    ht.put(elem, new Object());
                }
            }

            /*       Enumeration e = ht.keys();
            while (e.hasMoreElements()) {
            Element el = (Element)e.nextElement();
            }
             */
        }
    }

    // Called to remove an Element from our hash
    protected void removeFromStyleHash(Element elem) {
        //System.out.println("remove from stylehash "+elem.toString());
        AttributeSet attrs = elem.getAttributes();
        String[] tagList = null;

        if (attrs != null) {
            String tagNames = (String) attrs.getAttribute("tagName");

            if (tagNames == null) {
                return;
            }

            //System.out.println("remove "+tagNames);
            tagList = tagNames.split("\\s", -1);

            String tagName = null;

            for (int i = 0; i < tagList.length; i++) {
                tagName = tagList[i];

                Hashtable ht = (Hashtable) styleHash.get(tagName);

                if (ht != null) {
                    ht.remove(elem);
                }
            }
        }
    }

    public void indexParagraphs() {
        ElementIterator elIter = new ElementIterator(this);
        Element elem;
        paragraphs.setSize(0);

        while (true) {
            if ((elem = elIter.next()) != null) {
                if (elem.getName().equals(AbstractDocument.ParagraphElementName)) {
                    paragraphs.addElement(elem);
                }
            } else {
                break;
            }
        }
    }

    void removeStyleFromRange(SwkJTextPane swkjtextpane, int index1,
            int index2, String tagName) {
        Style style;
        Style nextStyle;
        removeInProgress = true;

        StyleContext rStyle = new StyleContext();
        endPosition = getEndPosition();

        int endOffset = endPosition.getOffset();
        int nextPos = index1;
        StringBuffer sbuf = new StringBuffer();

        while (true) {
            Element e1 = getCharacterElement(nextPos);
            int start = e1.getStartOffset();
            int end = e1.getEndOffset();
            nextPos = end;

            //System.out.println(index1+" "+index2+" "+start+" "+end+" "+endOffset);
            int adjustStart = start;
            int adjustEnd = end;

            if (index1 > adjustStart) {
                adjustStart = index1;
            }

            if (index2 < adjustEnd) {
                adjustEnd = index2;
            }

            AttributeSet attrs = e1.getAttributes();

            if (attrs == null) {
                if ((end >= index2) || (end >= endOffset)) {
                    break;
                }

                continue;
            }

            String checkName = (String) attrs.getAttribute("tagName");

            if (checkName == null) {
                if ((end >= index2) || (end >= endOffset)) {
                    break;
                }

                continue;
            }

            int checkIndex = checkName.indexOf(tagName);
            int lastChar = checkIndex + tagName.length();

            if ((checkIndex != -1)
                    && ((checkIndex == 0)
                    || (checkName.charAt(checkIndex - 1) == ' '))
                    && ((lastChar == checkName.length())
                    || (checkName.charAt(lastChar) == ' '))) {
                String[] styleList = checkName.split("\\s", -1);

                if (tagName.equals(checkName)) {
                    //System.out.println("setchar "+adjustStart+" "+adjustEnd);
                    this.setCharacterAttributes(adjustStart,
                            adjustEnd - adjustStart, rStyle.getEmptySet(), true);

                    //this.setParagraphAttributes(adjustStart, adjustEnd - adjustStart,rStyle.getEmptySet() , true);
                } else {
                    sbuf.setLength(0);

                    for (int j = 0; j < styleList.length; j++) {
                        if (styleList[j].equals(tagName)) {
                            continue;
                        }

                        if (sbuf.length() > 0) {
                            sbuf.append(' ');
                        }

                        sbuf.append(styleList[j]);
                    }

                    String newTag = sbuf.toString();
                    Style groupedStyle = swkjtextpane.getStyle(newTag);

                    if (groupedStyle == null) {
                        groupedStyle = swkjtextpane.addStyle(newTag, null);

                        for (int j = 0; j < styleList.length; j++) {
                            if (styleList[j].equals(tagName)) {
                                continue;
                            }

                            //System.out.println("styleListRemove "+styleList[j].toString());
                            style = swkjtextpane.getStyle(styleList[j].toString());

                            if (style != null) {
                                groupedStyle.addAttributes(style);
                            }
                        }
                    }

                    //System.out.println("newTag "+newTag+" "+start+" "+end);
                    groupedStyle.addAttribute("tagName", newTag);
                    this.setCharacterAttributes(start, end - start,
                            groupedStyle, true);
                }
            }

            if ((end >= index2) || (end >= endOffset)) {
                break;
            }
        }

        removeInProgress = false;
    }

    void addStyleToRange(SwkJTextPane swkjtextpane, int index1, int index2,
            String tagName) {
        Style style;
        Style nextStyle;
        StyleContext rStyle = new StyleContext();
        endPosition = getEndPosition();

        int endOffset = endPosition.getOffset();
        int nextPos = index1;

        while (true) {
            Element e1 = getCharacterElement(nextPos);
            int start = e1.getStartOffset();
            int end = e1.getEndOffset();
            nextPos = end + 1;

            //System.out.println(index1+" "+index2+" "+start+" "+end+" "+endOffset);
            int adjustStart = start;
            int adjustEnd = end;

            if (index1 > adjustStart) {
                adjustStart = index1;
            }

            if (index2 < adjustEnd) {
                adjustEnd = index2;
            }

            AttributeSet attrs = e1.getAttributes();

            if (attrs == null) {
                if ((end >= index2) || (end >= endOffset)) {
                    break;
                }

                continue;
            }

            String oldName = (String) attrs.getAttribute("tagName");
            String newName = null;

            if (oldName == null) {
                //System.out.println("null old");
                oldName = "";
            }

            //System.out.println("not null old");
            int checkIndex = oldName.indexOf(tagName);
            int lastChar = checkIndex + tagName.length();

            if ((checkIndex != -1)
                    && ((checkIndex == 0)
                    || (oldName.charAt(checkIndex - 1) == ' '))
                    && ((lastChar == oldName.length())
                    || (oldName.charAt(lastChar) == ' '))) {
                newName = oldName;

                //System.out.println("new eq old");
            } else {
                //System.out.println("new "+tagName+" neq old "+oldName);
                if (oldName != "") {
                    newName = oldName + " " + tagName;
                } else {
                    newName = tagName;
                }

                //System.out.println(newName);
                String[] styleList = newName.split("\\s", -1);
                Style groupedStyle = swkjtextpane.getStyle(newName);

                if (groupedStyle == null) {
                    //System.out.println("group null");
                    groupedStyle = swkjtextpane.addStyle(newName, null);

                    for (int j = 0; j < styleList.length; j++) {
                        style = swkjtextpane.getStyle(styleList[j]);

                        //System.out.println("add style "+styleList[j].toString()+" "+style.toString());
                        groupedStyle.addAttributes(style);
                    }

                    groupedStyle.addAttribute("tagName", newName);
                }

                this.setCharacterAttributes(adjustStart,
                        adjustEnd - adjustStart, groupedStyle, true);
            }

            if ((end >= index2) || (end >= endOffset)) {
                break;
            }
        }
    }

    public void setMarkGravity(SwkJTextPane swkjtextpane, String markName,
            String direction) throws IllegalArgumentException {
        int offset = 0;
        SwkPosition swkPosition = (SwkPosition) marks.get(markName);

        if (swkPosition == null) {
            throw new IllegalArgumentException("there is no mark named \""
                    + markName + "\"");
        }

        if (direction.equals("left")) {
            if (swkPosition.bias == Position.Bias.Backward) {
                return;
            } else {
                offset = swkPosition.position.getOffset();
                offset = offset - 1;

                if (offset < 0) {
                    offset = 0;
                }

                swkPosition = new SwkPosition();

                try {
                    swkPosition.position = createPosition(offset);
                } catch (BadLocationException badLoc) {
                    throw new IllegalArgumentException(
                            "bad location in mark gravity");
                }

                swkPosition.bias = Position.Bias.Backward;
                marks.put(markName, swkPosition);
            }
        } else if (direction.equals("right")) {
            if (swkPosition.bias == Position.Bias.Forward) {
                return;
            } else {
                offset = swkPosition.position.getOffset();
                offset = offset + 1;
                endPosition = getEndPosition();

                int endOffset = endPosition.getOffset();

                if (offset > endOffset) {
                    offset = endOffset;
                }

                swkPosition = new SwkPosition();

                try {
                    swkPosition.position = createPosition(offset);
                } catch (BadLocationException badLoc) {
                    throw new IllegalArgumentException(
                            "bad location in mark gravity");
                }

                swkPosition.bias = Position.Bias.Forward;
                marks.put(markName, swkPosition);
            }
        } else {
            throw new IllegalArgumentException("bad mark gravity \""
                    + direction + "\": must be left or right");
        }
    }

    public void setMark(SwkJTextPane swkjtextpane, String markName, String index)
            throws IllegalArgumentException {
        if (markName.toString().equals("end")) {
            return;
        }

        int offset = getIndexLC(swkjtextpane, index);
        endPosition = getEndPosition();

        int endOffset = endPosition.getOffset();

        if (offset >= endOffset) {
            offset = endOffset - 1;
        }

        if (markName.toString().equals("insert")) {
            swkjtextpane.setCaretPosition(offset);

            return;
        } else {
            try {
                SwkPosition swkPosition = new SwkPosition();
                swkPosition.position = createPosition(offset);
                marks.put(markName, swkPosition);
            } catch (BadLocationException badLoc) {
                System.out.println(badLoc.toString());
            }
        }
    }

    public void unsetMarks(String[] markStrings) {
        for (int i = 0; i < markStrings.length; i++) {
            marks.remove(markStrings[i]);
        }
    }

    public ArrayList getMarks() {
        ArrayList list = new ArrayList();
        Enumeration e = marks.keys();

        while (e.hasMoreElements()) {
            String mark = (String) e.nextElement();

            if (!mark.startsWith(".")) {
                list.add(mark);
            }
        }

        return list;
    }

    public ArrayList getTags() {
        ArrayList list = new ArrayList();
        Enumeration e = styleHash.keys();

        while (e.hasMoreElements()) {
            String tagName = (String) e.nextElement();
            list.add(tagName);
        }

        return list;
    }

    public Result compareIndices(SwkJTextPane swkjtextpane, String indexArg1,
            String op, String indexArg2) {
        Result result = new Result();
        result.setValue(false);
        final int index1;
        final int index2;
        try {
            index1 = getIndexLC(swkjtextpane, indexArg1);
            index2 = getIndexLC(swkjtextpane, indexArg2);
        } catch (IllegalArgumentException iaE) {
            result.setError(iaE.getMessage());
            return result;
        }

        if (op.equals("==")) {
            if (index1 == index2) {
                result.setValue(true);
            }
        } else if (op.equals("!=")) {
            if (index1 != index2) {
                result.setValue(true);
            }
        } else if (op.equals("<")) {
            if (index1 < index2) {
                result.setValue(true);
            }
        } else if (op.equals("<=")) {
            if (index1 <= index2) {
                result.setValue(true);
            }
        } else if (op.equals(">")) {
            if (index1 > index2) {
                result.setValue(true);
            }
        } else if (op.equals(">=")) {
            if (index1 >= index2) {
                result.setValue(true);
            }
        } else {
            result.setError("bad comparison operator \"" + op
                    + "\": must be <, <=, ==, >=, >, or !=");
        }

        return result;
    }

    public int getIndexLC(SwkJTextPane swkjtextpane, String indexArg)
            throws IllegalArgumentException {
        String[] indexVals = indexArg.split("\\s", -1);
        String base = indexVals[0];
        String badTextIndex = "bad text index \"" + indexArg + "\"";
        int endCharPosition = base.toString().length();
        int dotPosition = base.toString().indexOf(".");
        int plusPosition = base.toString().indexOf("+");
        int minusPosition = base.toString().indexOf("-");

        if ((dotPosition == -1)
                && ((plusPosition == 0) || (minusPosition == 0))) {
            throw new IllegalArgumentException(badTextIndex);
        }

        if (minusPosition == (dotPosition + 1)) {
            minusPosition = -1;
        }

        if ((plusPosition > 0) && (minusPosition > 0)) {
            throw new IllegalArgumentException(badTextIndex);
        }

        if ((plusPosition > 0) && (plusPosition < dotPosition)) {
            throw new IllegalArgumentException(badTextIndex);
        }

        if ((minusPosition > 0) && (minusPosition < dotPosition)) {
            throw new IllegalArgumentException(badTextIndex);
        }

        String baseString;

        if (plusPosition > 0) {
            endCharPosition = plusPosition;
            baseString = base.toString().substring(0, plusPosition).trim();
            indexVals[0] = base.substring(plusPosition);
        } else if (minusPosition > 0) {
            endCharPosition = minusPosition;
            baseString = base.substring(0, minusPosition).trim();
            indexVals[0] = base.substring(minusPosition);
        } else {
            baseString = base;
            indexVals[0] = null;
        }

        Element elem;
        int offset = 0;
        int lineIndex = 1;
        int charIndex = 0;
        endPosition = getEndPosition();

        int endOffset = endPosition.getOffset();

        if (baseString.equals("end")) {
            offset = endOffset;
        } else if (baseString.equals("sel.first")) {
            offset = swkjtextpane.getSelectionStart();
        } else if (baseString.equals("sel.last")) {
            offset = swkjtextpane.getSelectionEnd();
        } else if (baseString.equals("insert")) {
            if (swkjtextpane == null) {
                return 0;
            } else {
                offset = swkjtextpane.getCaretPosition();
            }
        } else {
            SwkPosition swkPosition = null;

            if (dotPosition > 0) {
                swkPosition = (SwkPosition) marks.get(baseString.substring(0,
                        dotPosition));

                if (swkPosition != null) {
                    throw new IllegalArgumentException(badTextIndex);
                }
            } else {
                swkPosition = (SwkPosition) marks.get(baseString);
            }

            if (swkPosition != null) {
                offset = swkPosition.getOffset();
            } else {
                if (baseString.startsWith("@")) {
                    if (baseString.length() > 1) {
                        int commaPos = baseString.indexOf(',');

                        if (commaPos < 0) {
                            throw new IllegalArgumentException(badTextIndex);
                        }

                        String xS = baseString.substring(1, commaPos);
                        String yS = baseString.substring(commaPos + 1);
                        int x = Integer.valueOf(xS).intValue();
                        int y = Integer.valueOf(yS).intValue();
                        offset = swkjtextpane.viewToModel(new Point(x, y));
                    } else {
                        throw new IllegalArgumentException(badTextIndex);
                    }
                } else {
                    if ((dotPosition >= 0)
                            && (dotPosition < (baseString.length() - 1))) {
                        lineIndex = -1;
                        charIndex = 0;

                        try {
                            lineIndex = Integer.parseInt(baseString.substring(
                                    0, dotPosition));
                        } catch (NumberFormatException nfE) {
                            if (baseString.substring(dotPosition + 1).equals("first")) {
                                offset = getRangeOffset(baseString.substring(
                                        0, dotPosition), 1);

                                if (offset < 0) {
                                    throw new IllegalArgumentException(
                                            "text doesn't contain any characters tagged with \""
                                            + baseString.substring(0, dotPosition)
                                            + "\"");
                                }
                            } else if (baseString.substring(dotPosition + 1).equals("last")) {
                                offset = getRangeOffset(baseString.substring(
                                        0, dotPosition), 2);

                                if (offset < 0) {
                                    throw new IllegalArgumentException(
                                            "text doesn't contain any characters tagged with \""
                                            + baseString.substring(0, dotPosition)
                                            + "\"");
                                }
                            } else {
                                throw new IllegalArgumentException(badTextIndex);
                            }
                        }

                        if (lineIndex > 0) {
                            try {
                                charIndex = Integer.parseInt(baseString.substring(dotPosition
                                        + 1));
                            } catch (NumberFormatException nfE) {
                                if (baseString.substring(dotPosition + 1).equals("end")) {
                                    charIndex = Integer.MAX_VALUE;
                                } else {
                                    throw new IllegalArgumentException(badTextIndex);
                                }
                            }

                            indexParagraphs();

                            if (paragraphs.size() == 0) {
                                return (0);
                            }

                            if (lineIndex < 1) {
                                return (0);
                            } else if (lineIndex > paragraphs.size()) {
                                //lineIndex = paragraphs.size ();
                                offset = endOffset;
                            } else {
                                elem = (Element) paragraphs.elementAt(lineIndex
                                        - 1);

                                if (elem != null) {
                                    if (charIndex < 0) {
                                        offset = elem.getStartOffset();
                                    } else if (charIndex >= (elem.getEndOffset()
                                            - elem.getStartOffset())) {
                                        offset = elem.getEndOffset() - 1;
                                    } else {
                                        offset = elem.getStartOffset()
                                                + charIndex;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        boolean gotSign = false;
        boolean gotValue = false;
        int delta = 0;
        int cpos = 0;
        int sign = 1;
        boolean gotUnits = false;

        for (int i = 0; i < indexVals.length; i++) {
            if (indexVals[i] == null) {
                continue;
            }

            String modString = indexVals[i].toString();

            if (modString.equals("-")) {
                gotSign = true;
                gotUnits = false;
                sign = -1 * sign;
            } else if (modString.equals("+")) {
                gotUnits = false;
                gotSign = true;
            } else {
                cpos = modString.length();

                int start = 0;

                if (modString.charAt(0) == '-') {
                    start = 1;
                    sign = -1 * sign;
                    gotSign = true;
                } else if (modString.charAt(0) == '+') {
                    start = 1;
                    sign = 1;
                    gotSign = true;
                }

                for (int j = start; j < modString.length(); j++) {
                    if (!Character.isDigit(modString.charAt(j))) {
                        cpos = j;

                        break;
                    }
                }

                if (cpos > start) {
                    delta = Integer.parseInt(modString.substring(start, cpos));
                    gotValue = true;
                    gotUnits = false;
                }

                if (cpos == modString.length()) {
                    continue;
                }

                if ("chars".startsWith(modString.substring(cpos))) {
                    if (gotValue) {
                        gotUnits = true;
                        offset += (sign * delta);
                        gotValue = gotSign = false;

                        if (offset < 0) {
                            offset = 0;
                        }
                    } else {
                        throw new IllegalArgumentException(badTextIndex);
                    }
                } else if (gotValue
                        && (modString.substring(cpos).length() < 6)
                        && "lines".startsWith(modString.substring(cpos))) {
                    if (gotValue) {
                        offset = indexMath(offset, sign * delta, 0);
                        gotValue = gotSign = false;
                        sign = 1;
                    } else {
                        throw new IllegalArgumentException(badTextIndex);
                    }
                } else if ((modString.substring(cpos).length() > 4)
                        && "lineend".startsWith(modString)) {
                    if (gotSign || gotValue) {
                        throw new IllegalArgumentException(badTextIndex);
                    } else {
                        gotUnits = true;
                        offset = indexMath(offset, 0, 1);
                        gotValue = gotSign = false;
                        sign = 1;
                    }
                } else if ((modString.substring(cpos).length() > 4)
                        && "linestart".startsWith(modString)) {
                    if (gotSign || gotValue) {
                        throw new IllegalArgumentException(badTextIndex);
                    } else {
                        gotUnits = true;
                        offset = indexMath(offset, 0, -1);
                        gotValue = gotSign = false;
                        sign = 1;
                    }
                } else if ((modString.substring(cpos).length() > 4)
                        && "wordend".startsWith(modString)) {
                    if (gotSign || gotValue) {
                        throw new IllegalArgumentException(badTextIndex);
                    }

                    offset = getWordEnd(offset);
                    gotUnits = true;
                    gotValue = gotSign = false;
                    sign = 1;
                } else if ((modString.substring(cpos).length() > 4)
                        && "wordstart".startsWith(modString)) {
                    if (gotSign || gotValue) {
                        throw new IllegalArgumentException(badTextIndex);
                    }

                    offset = getWordStart(offset);

                    //getText
                    gotUnits = true;
                    gotValue = gotSign = false;
                    sign = 1;
                } else {
                    throw new IllegalArgumentException(badTextIndex);
                }
            }
        }

        if ((gotValue || gotSign) && !gotUnits) {
            throw new IllegalArgumentException("bad text index \""
                    + indexArg.toString() + "\"");
        }

        return (offset);
    }

    // should replace much of this with call to regexp
    public String getIndex(SwkJTextPane swkjtextpane, String indexArg) {
        int offset = getIndexLC(swkjtextpane, indexArg);

        return getIndexFromOffset(offset, null);
    }

    public int indexMath(int offset, int dLine, int mode) {
        indexParagraphs();

        Element elem = null;
        int charIndex = 0;
        boolean foundOffset = false;
        int j = 0;

        for (j = 0; j < paragraphs.size(); j++) {
            elem = (Element) paragraphs.elementAt(j);

            if ((offset >= elem.getStartOffset())
                    && (offset < elem.getEndOffset())) {
                foundOffset = true;

                break;
            }
        }

        if (!foundOffset) {
            return 0;
        }

        charIndex = offset - elem.getStartOffset();
        j += dLine;

        if (j < 0) {
            j = 0;
        } else if (j >= paragraphs.size()) {
            j = paragraphs.size() - 1;
        }

        elem = (Element) paragraphs.elementAt(j);

        if (mode == -1) {
            offset = elem.getStartOffset();
        } else if (mode == 1) {
            offset = elem.getEndOffset() - 1;
        } else {
            if ((elem.getStartOffset() + charIndex) <= elem.getEndOffset()) {
                offset = elem.getStartOffset() + charIndex;
            } else {
                offset = elem.getEndOffset();
            }
        }

        return offset;
    }

    public String getIndexFromOffset(int offset, TclObject[] indexVals) {
        indexParagraphs();

        Element elem = null;
        int charIndex = 0;
        int endOffset = endPosition.getOffset();

        if (offset >= endOffset) {
            return ((paragraphs.size() + 1) + ".0");
        }

        for (int j = 0; j < paragraphs.size(); j++) {
            elem = (Element) paragraphs.elementAt(j);

            if ((offset >= elem.getStartOffset())
                    && (offset < elem.getEndOffset())) {
                charIndex = offset - elem.getStartOffset();

                if ((indexVals != null) && (indexVals.length == 2)
                        && (indexVals[1].toString().startsWith("lines"))) {
                    charIndex = 0;
                }

                if ((indexVals != null) && (indexVals.length == 2)
                        && (indexVals[1].toString().startsWith("linee"))) {
                    charIndex = elem.getEndOffset() - elem.getStartOffset();
                }

                return ((j + 1) + "." + charIndex);
            }
        }

        return ("1.0");
    }

    public int getParagraph(int offset) {
        int endOffset = endPosition.getOffset();

        if (offset > endOffset) {
            return -1;
        }

        if (offset == endOffset) {
            return paragraphs.size() - 1;
        }

        Element elem = null;

        for (int j = 0; j < paragraphs.size(); j++) {
            elem = (Element) paragraphs.elementAt(j);

            if ((offset >= elem.getStartOffset())
                    && (offset < elem.getEndOffset())) {
                return j;
            }
        }

        return -1;
    }

    public String getName() {
        return (name);
    }

    public void setSwkWidth(int width) {
        this.swkwidth = width;
    }

    public int getSwkWidth() {
        return (swkwidth);
    }

    public void removeStyle(SwkJTextPane swkjtextpane, String tagName) {
        //System.out.println("remove "+tagName);
        Style style = swkjtextpane.getStyle(tagName);

        if (style == null) {
            return;
        }

        int endOffset = endPosition.getOffset();
        removeStyleFromRange(swkjtextpane, 0, endOffset - 1, tagName);

        Hashtable ht = (Hashtable) styleHash.get(tagName);

        if (ht == null) {
            return;
        }

        Enumeration e = ht.keys();

        while (e.hasMoreElements()) {
            Element bad = (Element) e.nextElement();
            ht.remove(bad);
        }

        styleHash.remove(tagName);
    }

    private int compare(int i, int j) {
        if (((Integer) rangeOffsets.elementAt(i)).intValue() > ((Integer) rangeOffsets.elementAt(
                j)).intValue()) {
            return 1;
        } else if (((Integer) rangeOffsets.elementAt(i)).intValue() < ((Integer) rangeOffsets.elementAt(
                j)).intValue()) {
            return -1;
        } else {
            return (0);
        }
    }

    public void shuttlesort(int[] from, int[] to, int low, int high) {
        if ((high - low) < 2) {
            return;
        }
        int middle = (low + high) >>> 1;
        shuttlesort(to, from, low, middle);
        shuttlesort(to, from, middle, high);

        int p = low;
        int q = middle;

        if (((high - low) >= 4)
                && (compare(from[middle - 1], from[middle]) <= 0)) {
            for (int i = low; i < high; i++) {
                to[i] = from[i];
            }

            return;
        }

        // A normal merge.
        for (int i = low; i < high; i++) {
            if ((q >= high)
                    || ((p < middle) && (compare(from[p], from[q]) <= 0))) {
                to[i] = from[p++];
            } else {
                to[i] = from[q++];
            }
        }
    }

    public String getRanges(String tagName, int mode) {
        return getRangeString(tagName, mode);
    }

    public String getRangeString(String tagName, int mode) {
        int[] rangeIndexes = getRangeArray(tagName);

        if (rangeIndexes == null) {
            return null;
        }

        if (mode == 0) {
            return (getRangeString(rangeIndexes));
        } else {
            return (getRangeString(rangeIndexes, mode));
        }
    }

    public int getRangeOffset(String tagName, int mode) {
        int[] rangeIndexes = getRangeArray(tagName);

        if (rangeIndexes == null) {
            return -1;
        }

        return (getRangeOffset(rangeIndexes, mode));
    }

    public int[] getRangeArray(String tagName) {
        Hashtable ht = (Hashtable) styleHash.get(tagName);
        String[] styleList = null;
        Style nextStyle = null;
        rangeOffsets.setSize(0);

        if (ht != null) {
            // somebody's using it if we get here.
            // no longer use this Style (we don't remove them when they change Styles,
            // so they will still be here)
            Enumeration e = ht.keys();

            while (e.hasMoreElements()) {
                Element el = (Element) e.nextElement();
                int start = el.getStartOffset();
                int end = el.getEndOffset();
                AttributeSet attrs = el.getAttributes();

                if (attrs == null) {
                    continue;
                }

                String checkName = (String) attrs.getAttribute("tagName");

                if (checkName == null) {
                    continue;
                }

                int checkIndex = checkName.indexOf(tagName);
                int lastChar = checkIndex + tagName.length();

                if ((checkIndex != -1)
                        && ((checkIndex == 0)
                        || (checkName.charAt(checkIndex - 1) == ' '))
                        && ((lastChar == checkName.length())
                        || (checkName.charAt(lastChar) == ' '))) {
                    styleList = checkName.split("\\s", -1);

                    for (int j = 0; j < styleList.length; j++) {
                        if (!styleList[j].equals(tagName)) {
                            continue;
                        }
                    }

                    rangeOffsets.addElement(Integer.valueOf(start));
                    rangeOffsets.addElement(Integer.valueOf(end));
                }
            }
        }

        if (rangeOffsets.size() == 0) {
            return null;
        }

        indexes = new int[rangeOffsets.size()];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }

        shuttlesort((int[]) indexes.clone(), indexes, 0, indexes.length);

        return indexes;
    }

    public String getRangeString(int[] indexes) {
        int level = 0;
        StringBuffer sbuf = new StringBuffer();

        for (int i = 0; i < indexes.length; i++) {
            int j = indexes[i];

            if ((j % 2) == 0) {
                if (level == 0) {
                    int start = ((Integer) rangeOffsets.elementAt(j)).intValue();

                    if (i != 0) {
                        sbuf.append(" ");
                    }

                    sbuf.append(getIndexFromOffset(start, null));
                }

                level++;
            } else {
                level--;

                if (level == 0) {
                    int end = ((Integer) rangeOffsets.elementAt(j)).intValue();

                    sbuf.append(" " + getIndexFromOffset(end, null));
                }
            }
        }

        return (sbuf.toString());
    }

    public String getRangeString(int[] indexes, int mode) {
        if (mode == 1) {
            if (indexes.length == 0) {
                return ("");
            } else {
                int firstIndex = indexes[0];
                int start = ((Integer) rangeOffsets.elementAt(firstIndex)).intValue();

                return (getIndexFromOffset(start, null));
            }
        } else {
            if (indexes.length == 0) {
                return ("");
            } else {
                int lastIndex = indexes[indexes.length - 1];
                int end = ((Integer) rangeOffsets.elementAt(lastIndex)).intValue();

                return (getIndexFromOffset(end, null));
            }
        }
    }

    public int getRangeOffset(int[] indexes, int mode) {
        if (mode == 1) {
            if (indexes.length == 0) {
                return (0);
            } else {
                int firstIndex = indexes[0];
                int start = ((Integer) rangeOffsets.elementAt(firstIndex)).intValue();

                return (start);
            }
        } else {
            if (indexes.length == 0) {
                return (0);
            } else {
                int lastIndex = indexes[indexes.length - 1];
                int end = ((Integer) rangeOffsets.elementAt(lastIndex)).intValue();

                return (end);
            }
        }
    }

    private static void initResources() {
        ResourceObject resourceObject = null;

        resourceObject = new ResourceObject("asynchronousLoadPriority",
                "AsynchronousLoadPriority");
        resourceDB.put("-asynchronousloadpriority", resourceObject);

        resourceObject = new ResourceObject("documentProperties",
                "DocumentProperties");
        resourceDB.put("-documentproperties", resourceObject);

        resourceObject = new ResourceObject("width", "Width");
        resourceDB.put("-width", resourceObject);
    }

    /**
     * Taken from tcl.lang.Expression since it is not public in that class
     * This procedure decides whether the leading characters of a
     * string look like an integer or something else (such as a
     * floating-point number or string).
     * @return a boolean value indicating if the string looks like an integer.
     */
    static boolean looksLikeInt(String s, int len, int i) {
        while ((i < len) && Character.isWhitespace(s.charAt(i))) {
            i++;
        }

        if (i >= len) {
            return false;
        }

        char c = s.charAt(i);

        if ((c == '+') || (c == '-')) {
            i++;

            if (i >= len) {
                return false;
            }

            c = s.charAt(i);
        }

        if (!Character.isDigit(c)) {
            return false;
        }

        while ((i < len) && Character.isDigit(s.charAt(i))) {
            i++;
        }

        if (i >= len) {
            return true;
        }

        c = s.charAt(i);

        if ((c != '.') && (c != 'e') && (c != 'E')) {
            return true;
        }

        return false;
    }

    int getWordStart(int offset) {
        if (!isWordChar(offset)) {
            return offset;
        }

        while ((offset >= 0) && isWordChar(offset)) {
            offset--;
        }

        return (offset + 1);
    }

    int getWordEnd(int offset) {
        int endOffset = endPosition.getOffset();

        if (!isWordChar(offset)) {
            if (offset < endOffset) {
                return offset + 1;
            } else {
                return endOffset;
            }
        }

        while ((offset <= endOffset) && isWordChar(offset)) {
            offset++;
        }

        return (offset);
    }

    boolean isWordChar(int offset) {
        char ch;
        try {
            ch = getText(offset, 1).charAt(0);
        } catch (BadLocationException badLoc) {
            return false;
        }

        if (Character.isDigit(ch) || Character.isLetter(ch) || (ch == '_')) {
            return true;
        } else {
            return false;
        }
    }

    private static class SwkPosition {

        Position position = null;
        Position.Bias bias = Position.Bias.Forward;

        int getOffset() {
            int offset = position.getOffset();

            if (bias == Position.Bias.Backward) {
                return (offset + 1);
            } else {
                return offset;
            }
        }
    }
}
