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

import java.util.regex.*;
import javax.swing.text.*;

/**
 *
 * @author brucejohnson
 */
public class SwkDocumentSearch {

    /**
     *
     */
    public static final int SEARCH_BACKWARDS = 1;
    /**
     *
     */
    public static final int SEARCH_REGEXP = 2;
    /**
     *
     */
    public static final int SEARCH_NOCASE = 4;
    /**
     *
     */
    public static final int SEARCH_ELIDE = 8;
    private SwkDefaultStyledDocument doc = null;
    Pattern pattern = null;
    String searchString = null;
    int flags = 0;
    int iStart = 0;
    int iEnd = 0;
    int nWrap = 1;
    int startLine = -1;
    int endLine = -1;
    Result result = new Result();

    /**
     *
     * @param searchDoc
     * @param string
     * @param flags
     */
    public SwkDocumentSearch(SwkDefaultStyledDocument searchDoc, String string,
            int flags) {
        doc = searchDoc;
        searchString = string;
        this.flags = flags;
    }

    /**
     *
     * @param searchStart
     * @param searchEnd
     * @return
     */
    public Result search(int searchStart, int searchEnd) {
        result.i = -1;
        doc.indexParagraphs();

        startLine = doc.getParagraph(searchStart);

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
            } else {
                if ((flags & SEARCH_NOCASE) == SEARCH_NOCASE) {
                    searchString = searchString.toLowerCase();
                }
            }
        } catch (PatternSyntaxException psE) {
            result.setError("couldn't compile regular expression pattern: " + psE.getMessage());
            return result;
        }

        iStart = startLine;

        if ((flags & SEARCH_BACKWARDS) == SEARCH_BACKWARDS) {
             if (searchEnd < 0) {
                 nWrap = 2;
                 searchEnd = 0;
             }
             endLine = doc.getParagraph(searchEnd);
             iEnd = 0;

            return (searchBackwards(searchStart, searchEnd));
        } else {
             if (searchEnd < 0) {
                 nWrap = 2;
                 searchEnd = doc.getEndPosition().getOffset();
             }
            iEnd = doc.paragraphs.size();

            return (searchForwards(searchStart, searchEnd));
        }
    }

    /**
     *
     * @param searchStart
     * @param searchEnd
     * @return
     */
    public Result searchForwards(int searchStart, int searchEnd) {
        boolean lastLine = false;
        Element elem = null;

//System.out.println("nWrap " + nWrap);
        for (int iWrap = 0; iWrap < nWrap; iWrap++) {
//System.out.println("iWrap " + iWrap);
            for (int j = iStart; j < iEnd; j++) {
                elem = (Element) doc.paragraphs.elementAt(j);

                int start = searchStart;
                int elemStart = elem.getStartOffset();
                int elemEnd = elem.getEndOffset();

                if ((searchEnd >= elemStart) && (searchEnd <= elemEnd)) {
                    lastLine = true;
                }

                int length = 0;
//System.out.println("searchStart " + searchStart + " searchEnd " + searchEnd);
//System.out.println("elemStart " + elemStart + " elemEnd " + elemEnd);
//System.out.println("startLine " + startLine + " endLine " + endLine);

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
                    length = elemEnd - start - 1;
                } else {
                    start = elemStart;
                    length = elemEnd - start - 1;
                }
//System.out.println("start " + start + " length " + length);

                if (length < 0) {
                    return result;
                }

                String testLine = null;

                try {
                    testLine = doc.getText(start, length);
                } catch (BadLocationException badLoc) {
//System.out.println("badloc");
                    return result;
                }
//System.out.println(testLine);
                int searchIndex = -1;
                int nChars = 0;

                if (pattern != null) {
                    Matcher matcher = pattern.matcher(testLine);

                    if (matcher.find()) {
                        searchIndex = matcher.start();
                        nChars = matcher.end() - searchIndex;
                    }
                } else {
                    if ((flags & SEARCH_NOCASE) == SEARCH_NOCASE) {
                        testLine = testLine.toLowerCase();
                    }
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
//System.out.println("lastline");
                    break;
                }
            }

            searchEnd = searchStart;
            searchStart = 0;
            startLine = doc.getParagraph(searchStart);
            endLine = doc.getParagraph(searchEnd);
            iStart = 0;
            iEnd = endLine;
            lastLine = false;
        }

        return result;
    }

    /**
     *
     * @param searchStart
     * @param searchEnd
     * @return
     */
    public Result searchBackwards(int searchStart, int searchEnd) {
        Element elem = null;

        // forward
        boolean lastLine = false;
//System.out.println("nWrap " + nWrap);
        for (int iWrap = 0; iWrap < nWrap; iWrap++) {
//System.out.println("iWrap " + iWrap);
            for (int j = iStart; j >= iEnd; j--) {
                elem = (Element) doc.paragraphs.elementAt(j);

                int start = searchStart;
                int elemStart = elem.getStartOffset();
                int elemEnd = elem.getEndOffset();
                if ((searchEnd >= elemStart) && (searchEnd <= elemEnd)) {
                    lastLine = true;
                }

                int length = 0;

//System.out.println("searchStart " + searchStart + " searchEnd " + searchEnd);
//System.out.println("elemStart " + elemStart + " elemEnd " + elemEnd);
//System.out.println("startLine " + startLine + " endLine " + endLine);
                if (startLine == endLine) {
                    start = searchEnd;
                    length = searchStart - start;
                } else if ((iWrap == 0) && (j == startLine)) {
                    start = elemStart;
                    length = searchStart - start;
                } else if ((iWrap == 1) && (j == startLine)) {
                    start = searchEnd;
                    length = elemEnd - start;
                } else {
                    start = elemStart;
                    length = elemEnd - start;
                }

//System.out.println("start " + start + " length " + length);
                if (length < 0) {
                    return result;
                }

                String testLine = null;

                try {
                    testLine = doc.getText(start, length);
                } catch (BadLocationException badLoc) {
//System.out.println("badloc");
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
                    if ((flags & SEARCH_NOCASE) == SEARCH_NOCASE) {
                        testLine = testLine.toLowerCase();
                    }
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
//System.out.println("lastline");
                    break;
                }
            }

            searchEnd = searchStart;
            searchStart = doc.getEndPosition().getOffset();
            startLine = doc.getParagraph(searchStart);
            endLine = doc.getParagraph(searchEnd);
            iStart = doc.paragraphs.size() - 1;
            iEnd = startLine;
            lastLine = false;
        }

        return result;
    }
}
