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

import java.awt.*;
import java.awt.event.*;

import java.lang.*;

import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.text.*;

public class SwkDocumentSearch {

    public static final int SEARCH_BACKWARDS = 1;
    public static final int SEARCH_REGEXP = 2;
    public static final int SEARCH_NOCASE = 4;
    public static final int SEARCH_ELIDE = 8;
    private SwkDefaultStyledDocument doc = null;
    Pattern pattern = null;
    String searchString = null;
    int flags = 0;
    int iStart = 0;
    int iEnd = 0;
    int nWrap = 2;
    int startLine = -1;
    int endLine = -1;
    Result result = new Result();

    public SwkDocumentSearch(SwkDefaultStyledDocument searchDoc, String string,
            int flags) {
        doc = searchDoc;
        searchString = string;
        this.flags = flags;
    }

    public Result search(int searchStart, int searchEnd) {
        result.i = -1;
        doc.indexParagraphs();

        startLine = doc.getParagraph(searchStart);
        endLine = doc.getParagraph(searchEnd);

        if (startLine < 0) {
            return result;
        }

        try {
            if ((flags & SEARCH_REGEXP) == SEARCH_REGEXP) {
                if ((flags & SEARCH_NOCASE) == SEARCH_NOCASE) {
                    pattern = Pattern.compile(searchString,
                            Pattern.CASE_INSENSITIVE);
                } else {
                    pattern = Pattern.compile(searchString);
                }
            }
        } catch (PatternSyntaxException psE) {
            result.setError("pattern syntax error");

            return result;
        }

        iStart = startLine;

        if ((flags & SEARCH_BACKWARDS) == SEARCH_BACKWARDS) {
            iEnd = 0;

            return (searchBackwards(searchStart, searchEnd));
        } else {
            iEnd = doc.paragraphs.size();

            return (searchForwards(searchStart, searchEnd));
        }
    }

    public Result searchForwards(int searchStart, int searchEnd) {
        boolean lastLine = false;
        Element elem = null;

        for (int iWrap = 0; iWrap < nWrap; iWrap++) {
            for (int j = iStart; j < iEnd; j++) {
                elem = (Element) doc.paragraphs.elementAt(j);

                int start = searchStart;
                int elemStart = elem.getStartOffset();
                int elemEnd = elem.getEndOffset();

                if ((searchEnd >= elemStart) && (searchEnd <= elemEnd)) {
                    lastLine = true;
                }

                int length = 0;

                if (lastLine) {
                    elemEnd = searchEnd;
                }

                if (startLine == endLine) {
                    start = searchStart;
                    length = searchEnd - start;
                } else if ((iWrap == 0) && (j == startLine)) {
                    start = searchStart;
                    length = elemEnd - start;
                } else if ((iWrap == 1) && (j == startLine)) {
                    start = elemStart;
                    length = searchStart - start;
                } else {
                    start = elemStart;
                    length = elemEnd - start - 1;
                }

                if (length < 0) {
                    return result;
                }

                String testLine = null;

                try {
                    testLine = doc.getText(start, length);
                } catch (BadLocationException badLoc) {
                    return result;
                }

                int searchIndex = -1;
                int nChars = 0;

                if (pattern != null) {
                    Matcher matcher = pattern.matcher(testLine);

                    if (matcher.find()) {
                        searchIndex = matcher.start();
                        nChars = matcher.end() - searchIndex;
                    }
                } else {
                    searchIndex = testLine.indexOf(searchString);
                    nChars = searchString.length();
                }

                if (searchIndex >= 0) {
                    result.i = nChars;
                    result.s = (j + 1) + "."
                            + ((start - elemStart) + searchIndex);

                    return result;
                }

                if (lastLine) {
                    return result;
                }
            }

            iStart = 0;
            iEnd = startLine;
        }

        return result;
    }

    public Result searchBackwards(int searchStart, int searchEnd) {
        Element elem = null;

        // forward
        boolean lastLine = false;

        for (int iWrap = 0; iWrap < nWrap; iWrap++) {
            for (int j = iStart; j >= iEnd; j--) {
                elem = (Element) doc.paragraphs.elementAt(j);

                int start = searchStart;
                int elemStart = elem.getStartOffset();
                int elemEnd = elem.getEndOffset();

                if ((searchEnd >= elemStart) && (searchEnd <= elemEnd)) {
                    lastLine = true;
                }

                int length = 0;

                if (lastLine) {
                    elemEnd = searchEnd;
                }

                if (startLine == endLine) {
                    start = searchEnd;
                    length = searchStart - start;
                } else if ((iWrap == 0) && (j == startLine)) {
                    start = elemStart;
                    length = searchStart - start;
                } else if ((iWrap == 1) && (j == startLine)) {
                    start = searchStart + 1;
                    length = elemEnd - start;
                } else {
                    start = elemStart;
                    length = elemEnd - start;
                }

                if (length < 0) {
                    return result;
                }

                String testLine = null;

                try {
                    testLine = doc.getText(start, length);
                } catch (BadLocationException badLoc) {
                    return result;
                }

                int searchIndex = -1;
                int nChars = 0;

                if (pattern != null) {
                    Matcher matcher = pattern.matcher(testLine);

                    while (matcher.find()) {
                        searchIndex = matcher.start();
                        nChars = matcher.end() - searchIndex;
                    }
                } else {
                    searchIndex = testLine.lastIndexOf(searchString);
                    nChars = searchString.length();
                }

                if (searchIndex >= 0) {
                    result.i = nChars;
                    result.s = (j + 1) + "."
                            + ((start - elemStart) + searchIndex);

                    return result;
                }

                if (lastLine) {
                    return result;
                }
            }

            iStart = doc.paragraphs.size() - 1;
            iEnd = startLine;
        }

        return result;
    }
}
