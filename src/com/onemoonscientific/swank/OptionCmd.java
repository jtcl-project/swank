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

import java.awt.*;

import java.io.*;

import java.util.*;

import javax.swing.*;


public class OptionCmd implements Command {
    static Vector uniqueOptions = new Vector();
    static Hashtable classHash = new Hashtable();

    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        if (argv[1].toString().equals("add")) {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            StreamTokenizer tokenizer = null;
            tokenizer = new StreamTokenizer(new StringReader(argv[2].toString()));
            tokenizer.resetSyntax();
            tokenizer.ordinaryChars('*', '*');
            tokenizer.wordChars('A', 'Z');
            tokenizer.wordChars('a', 'z');
            tokenizer.wordChars('0', '9');

            if (tokenizer != null) {
                int nWild = 0;
                Vector optionVec = new Vector();

                try {
                    while (true) {
                        if (tokenizer.nextToken() == StreamTokenizer.TT_EOF) {
                            break;
                        }

                        if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                            optionVec.addElement(tokenizer.sval);
                        } else if (tokenizer.ttype == '*') {
                            nWild++;
                            optionVec.addElement("*");
                        }
                    }
                } catch (IOException ioE) {
                    throw new TclException(interp, "Couldn't parse option");
                }

                int nElems = optionVec.size();

                if (nElems > 1) {
                    String classElem = (String) optionVec.elementAt(nElems - 2);
                    String fieldElem = (String) optionVec.elementAt(nElems - 1);
                    char firstChar = classElem.charAt(0);

                    if (Character.isUpperCase(firstChar)) {
                        //System.out.println ("Class option " + classElem + " " + argv[3].toString ());
                        Vector classVec = (Vector) classHash.get(classElem);

                        if (classVec == null) {
                            classVec = new Vector();
                            classHash.put(classElem, classVec);

                            //System.out.println ("add classVec " + classElem);
                        }

                        int idx = argv[2].toString().lastIndexOf(classElem);
                        String option = argv[2].toString().substring(0, idx);
                        classVec.addElement(option);
                        classVec.addElement(fieldElem);
                        classVec.addElement(argv[3].toString());
                    } else {
                        int idx = argv[2].toString().lastIndexOf(fieldElem);
                        String option = argv[2].toString().substring(0, idx);
                        uniqueOptions.addElement(option);
                        uniqueOptions.addElement(fieldElem);
                        uniqueOptions.addElement(argv[3].toString());

                        //System.out.println ("add unique " + option + " " + fieldElem + " " + argv[3].toString ());
                    }
                }
            }
        } else if (argv[1].toString().equals("get")) {
            if (argv.length < 3) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            Vector classVec = (Vector) classHash.get(argv[2].toString());

            if (classVec != null) {
                for (i = 0; i < classVec.size(); i += 2) {
                    String option = (String) classVec.elementAt(i);
                    String value = (String) classVec.elementAt(i + 1);

                    //System.out.println (option + " " + value);
                }
            } else {
                //System.out.println ("No options for class " + argv[2].toString ());
            }
        }
    }
}
