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
import java.awt.event.*;

import java.io.*;

import java.lang.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


public class SwkBind {
    /** Processes a list of arguments to set up a binding.
     * @param interp Current interpreter for event
     * @param argv Array of arguments
     * @param firstArg Index of first argument in array to be scanned
     * @return Returns SwkBinding object containing information about specified binding.
     */
    public static SwkBinding getBinding(Interp interp, TclObject[] argv,
        int firstArg) {
        String eventString = argv[firstArg].toString();

        char eventChar;
        StreamTokenizer tokenizer = null;
        SwkBinding swkBinding = new SwkBinding();
        int subtype = -1;

        if (EventCmd.checkVirtual(eventString)) {
            if (eventString.equals("<<StateChanged>>")) {
                swkBinding.type = SwkBinding.STATECHANGED;
            } else if (eventString.equals("<<SelectionChanged>>")) {
                swkBinding.type = SwkBinding.SELECTIONCHANGED;
            } else {
                swkBinding.virtual = true;
                swkBinding.string = eventString;
            }

            return swkBinding;
        }

        SwkEventType eventType = null;

        if (SwkEventType.eventTable == null) {
            SwkEventType.initEventTable();
        }

        int end = eventString.length();
        int start = 0;

        if (eventString.charAt(0) == '<') {
            start = 1;
        }

        if (eventString.charAt(end - 1) == '>') {
            end--;
        }

        //System.out.println(eventString+" "+start+" "+end);
        eventString = eventString.substring(start, end);

        //System.out.println(eventString+" "+start+" "+end);
        if (eventString.length() == 1) {
            eventChar = eventString.charAt(0);

            if ((start > 0) &&
                    ((eventChar == '1') || (eventChar == '2') ||
                    (eventChar == '3'))) {
                swkBinding.type = SwkBinding.MOUSE;
                swkBinding.subtype = SwkBinding.PRESS;

                if (eventChar == '1') {
                    ;
                    swkBinding.detail = InputEvent.BUTTON1_MASK;
                    swkBinding.mod |= InputEvent.BUTTON1_DOWN_MASK;
                } else if (eventChar == '2') {
                    ;
                    swkBinding.detail = InputEvent.BUTTON2_MASK;
                    swkBinding.mod |= InputEvent.BUTTON2_DOWN_MASK;
                } else if (eventChar == '3') {
                    ;
                    swkBinding.detail = InputEvent.BUTTON3_MASK;
                    swkBinding.mod |= InputEvent.BUTTON3_DOWN_MASK;
                }
            } else {
                swkBinding.type = SwkBinding.KEY;
                swkBinding.subtype = SwkBinding.PRESS;
                swkBinding.detail = eventChar;

                //  System.out.println("detail is "+swkBinding.detail+" for "+eventChar);
                swkBinding.keyStroke = null;
            }
        } else {
            tokenizer = new StreamTokenizer(new StringReader(eventString));
            tokenizer.resetSyntax();
            tokenizer.wordChars('a', 'z');
            tokenizer.wordChars('A', 'Z');
            tokenizer.wordChars('_', '_');
            tokenizer.whitespaceChars(0000, 32);

            tokenizer.wordChars('0', '9');
            tokenizer.whitespaceChars('-', '-');

            boolean gotEvent = false;
            boolean gotMod = false;

            while (true) {
                try {
                    if (tokenizer.nextToken() == StreamTokenizer.TT_EOF) {
                        break;
                    }
                } catch (IOException ioE) {
                    return (null);
                }

                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    eventType = (SwkEventType) SwkEventType.countTable.get(tokenizer.sval.toLowerCase());

                    if (eventType != null) {
                        //System.out.println(eventType.name);
                        if (swkBinding.count > 1) {
                            System.out.println("Two count fields");

                            return null;
                        }

                        swkBinding.count = eventType.type;
                    } else {
                        eventType = (SwkEventType) SwkEventType.modTable.get(tokenizer.sval.toLowerCase());

                        if (eventType != null) {
                            //System.out.println(eventType.name);
                            gotMod = true;

                            if (gotEvent) {
                                System.out.println(
                                    "Mod field after event field: " +
                                    tokenizer.sval);

                                return null;
                            }

                            swkBinding.mod |= eventType.type;
                        } else {
                            eventType = (SwkEventType) SwkEventType.eventTable.get(tokenizer.sval.toLowerCase());

                            if (eventType != null) {
                                //System.out.println(eventType.name);
                                if (gotEvent) {
                                    System.out.println(
                                        "Event field after event field");

                                    return (null);
                                }

                                swkBinding.type = eventType.type;
                                subtype = swkBinding.subtype = eventType.subtype;

                                //		if (swkBinding.subtype == SwkBinding.RELEASE) {
                                //			swkBinding.count=0;
                                //		}
                                gotEvent = true;
                            } else if (tokenizer.sval.length() == 1) {
                                char eCh = tokenizer.sval.charAt(0);

                                if (!gotEvent) {
                                    if ((eCh == '1') || (eCh == '2') ||
                                            (eCh == '3')) {
                                        swkBinding.type = SwkBinding.MOUSE;
                                    } else {
                                        swkBinding.type = SwkBinding.KEY;
                                    }
                                }

                                if (swkBinding.type == SwkBinding.KEY) {
                                    swkBinding.detail = eCh;
                                    swkBinding.keyStroke = null;
                                } else if ((swkBinding.type == SwkBinding.MOUSE) ||
                                        (swkBinding.type == SwkBinding.MOUSEMOTION)) {
                                    if (eCh == '1') {
                                        ;
                                        swkBinding.detail = InputEvent.BUTTON1_MASK;
                                        swkBinding.mod |= InputEvent.BUTTON1_DOWN_MASK;
                                    } else if (eCh == '2') {
                                        ;
                                        swkBinding.mod |= InputEvent.BUTTON2_DOWN_MASK;
                                        swkBinding.detail = InputEvent.BUTTON2_MASK;
                                    } else if (eCh == '3') {
                                        ;
                                        swkBinding.mod |= InputEvent.BUTTON3_DOWN_MASK;
                                        swkBinding.detail = InputEvent.BUTTON3_MASK;
                                    }
                                }

                                gotEvent = true;

                                //System.out.println("Char is "+tokenizer.sval);
                            } else if (gotEvent == false) {
                                eventType = (SwkEventType) SwkEventType.detailTable.get(tokenizer.sval.toLowerCase());

                                if (eventType != null) {
                                    swkBinding.type = eventType.type;
                                    swkBinding.subtype = eventType.subtype;
                                    swkBinding.detail = eventType.detail;
                                    swkBinding.name = eventType.name;
                                    swkBinding.keyStroke = KeyStroke.getKeyStroke((char) eventType.detail);

                                    if (eventType.subtype == SwkBinding.PRESS) {
                                        swkBinding.mod |= eventType.mods;
                                    }

                                    //  System.out.println(eventType.name+" "+swkBinding.toString()+" "+eventType.detail);
                                    gotEvent = true;
                                } else {
                                    System.out.println("Unknown detail " +
                                        tokenizer.sval.toLowerCase());

                                    return (null);
                                }
                            } else {
                                eventType = (SwkEventType) SwkEventType.detailTable.get(tokenizer.sval.toLowerCase());

                                if (eventType != null) {
                                    swkBinding.detail = eventType.detail;
                                    swkBinding.name = eventType.name;
                                    swkBinding.keyStroke = KeyStroke.getKeyStroke((char) eventType.detail);

                                    if (subtype == SwkBinding.PRESS) {
                                        swkBinding.mod |= eventType.mods;
                                    }

                                    //System.out.println(eventType.name+" "+swkBinding.toString());
                                    gotEvent = true;
                                } else {
                                    System.out.println("Unknown detail " +
                                        tokenizer.sval.toLowerCase());

                                    return (null);
                                }

                                //System.out.println("Detail without event: " + tokenizer.sval);
                                //return (null);
                            }
                        }
                    }
                }
            }

            if (!gotEvent) {
                if (!gotMod) {
                    System.out.println("no event");

                    return null;
                } else {
                    System.out.println("event is mod");

                    return null;
                }
            }
        }

        return swkBinding;
    }

    public static void updateBindingCommand(Interp interp,
        SwkBinding swkBinding, TclObject[] argv, int firstArg) {
        if (argv.length == (firstArg + 1)) {
            // should return binding
        } else if (argv[firstArg + 1].toString().length() == 0) {
            // should remove binding
            swkBinding.remove = true;
        } else if (argv[firstArg + 1].toString().charAt(0) == '+') {
            swkBinding.add = true;
            swkBinding.command = argv[firstArg + 1].toString().substring(1);
        } else {
            swkBinding.add = false;
            swkBinding.command = argv[firstArg + 1].toString();
        }
    }
}
