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


public class SwkEventType {
    static Hashtable eventTable = null;
    static Hashtable modTable = null;
    static Hashtable countTable = null;
    static Hashtable detailTable = null;
    static Hashtable simpleMap = new Hashtable();
    static SwkEventType[] eventTypes = {
        new SwkEventType("Key", SwkBinding.KEY, SwkBinding.PRESS),
        new SwkEventType("KeyPress", SwkBinding.KEY, SwkBinding.PRESS),
        new SwkEventType("KeyRelease", SwkBinding.KEY, SwkBinding.RELEASE),
        new SwkEventType("KeyType", SwkBinding.KEY, SwkBinding.TYPE),
        new SwkEventType("ButtonPress", SwkBinding.MOUSE, SwkBinding.PRESS),
        new SwkEventType("Button", SwkBinding.MOUSE, SwkBinding.PRESS),
        new SwkEventType("ButtonRelease", SwkBinding.MOUSE, SwkBinding.RELEASE),
        new SwkEventType("Motion", SwkBinding.MOUSEMOTION, SwkBinding.MOTION),
        new SwkEventType("Enter", SwkBinding.MOUSE, SwkBinding.ENTER),
        new SwkEventType("Leave", SwkBinding.MOUSE, SwkBinding.EXIT),
        new SwkEventType("FocusIn", SwkBinding.FOCUS, SwkBinding.IN),
        new SwkEventType("FocusOut", SwkBinding.FOCUS, SwkBinding.OUT),
        new SwkEventType("Expose", SwkBinding.COMPONENT, SwkBinding.SHOWN),
        new SwkEventType("Visibility", SwkBinding.COMPONENT, SwkBinding.SHOWN),
        new SwkEventType("Destroy", SwkBinding.WINDOW, SwkBinding.DESTROY),
        new SwkEventType("Unmap", SwkBinding.WINDOW, SwkBinding.EXPOSE),
        new SwkEventType("Configure", SwkBinding.COMPONENT, SwkBinding.COMPONENT),
        new SwkEventType("AppOut", SwkBinding.APP, SwkBinding.OUT),
        new SwkEventType("AppIn", SwkBinding.APP, SwkBinding.IN),
        new SwkEventType("Activate", SwkBinding.ACTIVATION, SwkBinding.ACTIVATED),
        new SwkEventType("Deactivate", SwkBinding.ACTIVATION, SwkBinding.DEACTIVATED),
        new SwkEventType("<<StateChanged>>", SwkBinding.STATECHANGED,
            SwkBinding.STATECHANGED),
        new SwkEventType("<<SelectionChanged>>", SwkBinding.SELECTIONCHANGED,
            SwkBinding.SELECTIONCHANGED),
    };
    static SwkEventType[] modTypes = {
        new SwkEventType("Control", InputEvent.CTRL_DOWN_MASK, 0),
        new SwkEventType("Shift", InputEvent.SHIFT_DOWN_MASK, 0),
        new SwkEventType("Meta", InputEvent.META_DOWN_MASK, 0),
        new SwkEventType("M", InputEvent.META_DOWN_MASK, 0),
        new SwkEventType("Alt", InputEvent.ALT_DOWN_MASK, 0),
        new SwkEventType("B1", InputEvent.BUTTON1_DOWN_MASK, 0),
        new SwkEventType("Button1", InputEvent.BUTTON1_DOWN_MASK, 0),
        new SwkEventType("B2", InputEvent.BUTTON2_DOWN_MASK, 0),
        new SwkEventType("Button2", InputEvent.BUTTON2_DOWN_MASK, 0),
        new SwkEventType("B3", InputEvent.BUTTON3_DOWN_MASK, 0),
        new SwkEventType("Button3", InputEvent.BUTTON3_DOWN_MASK, 0),
        new SwkEventType("Any", 0, 0)
    };
    static SwkEventType[] countTypes = {
        new SwkEventType("Double", 2, 0), new SwkEventType("Triple", 3, 0),
    };
    static SwkEventType[] detailTypes = {
        new SwkEventType("return", SwkBinding.KEY, SwkBinding.PRESS, 10),
        new SwkEventType("enter", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ENTER),
        new SwkEventType("kp_enter", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ENTER),
        new SwkEventType("back_space", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_BACK_SPACE),
        new SwkEventType("backspace", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_BACK_SPACE),
        new SwkEventType("tab", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_TAB),
        new SwkEventType("cancel", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CANCEL),
        new SwkEventType("clear", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CLEAR),
        new SwkEventType("shift_l", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SHIFT),
        new SwkEventType("control_l", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CONTROL),
        new SwkEventType("alt_l", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ALT),
        new SwkEventType("pause", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PAUSE),
        new SwkEventType("caps_lock", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CAPS_LOCK),
        new SwkEventType("escape", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ESCAPE),
        new SwkEventType("space", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SPACE),
        new SwkEventType("page_up", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PAGE_UP),
        new SwkEventType("page_down", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PAGE_DOWN),
        new SwkEventType("prior", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PAGE_UP),
        new SwkEventType("next", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PAGE_DOWN),
        new SwkEventType("end", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_END),
        new SwkEventType("home", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_HOME),
        new SwkEventType("kp_page_up", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PAGE_UP),
        new SwkEventType("kp_page_down", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PAGE_DOWN),
        new SwkEventType("kp_end", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_END),
        new SwkEventType("kp_home", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_HOME),
        new SwkEventType("kp_begin", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_HOME),
        new SwkEventType("left", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_LEFT),
        new SwkEventType("up", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_UP),
        new SwkEventType("right", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_RIGHT),
        new SwkEventType("down", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DOWN),
        new SwkEventType("comma", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_COMMA, ','),
        new SwkEventType("minus", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_MINUS, '-'),
        new SwkEventType("period", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PERIOD, '.'),
        new SwkEventType("slash", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SLASH, '/'),
        new SwkEventType("semicolon", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SEMICOLON, ';'),
        new SwkEventType("equals", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_EQUALS, '='),
        new SwkEventType("open_bracket", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_OPEN_BRACKET, '['),
        new SwkEventType("back_slash", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_BACK_SLASH, '\\'),
        new SwkEventType("close_bracket", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CLOSE_BRACKET, ']'),
        new SwkEventType("bracketright", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CLOSE_BRACKET, ']'),
        new SwkEventType("numpad0", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD0),
        new SwkEventType("numpad1", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD1),
        new SwkEventType("numpad2", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD2),
        new SwkEventType("numpad3", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD3),
        new SwkEventType("numpad4", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD4),
        new SwkEventType("numpad5", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD5),
        new SwkEventType("numpad6", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD6),
        new SwkEventType("numpad7", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD7),
        new SwkEventType("numpad8", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD8),
        new SwkEventType("numpad9", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD9),
        new SwkEventType("kp_0", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD0),
        new SwkEventType("kp_1", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD1),
        new SwkEventType("kp_2", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD2),
        new SwkEventType("kp_3", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD3),
        new SwkEventType("kp_4", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD4),
        new SwkEventType("kp_5", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD5),
        new SwkEventType("kp_6", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD6),
        new SwkEventType("kp_7", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD7),
        new SwkEventType("kp_8", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD8),
        new SwkEventType("kp_9", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMPAD9),
        new SwkEventType("multiply", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_MULTIPLY, '*'),
        new SwkEventType("add", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ADD, '+'),
        new SwkEventType("kp_add", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ADD),
        new SwkEventType("separater", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SEPARATER, '|'),
        new SwkEventType("subtract", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SUBTRACT, '-'),
        new SwkEventType("kp_subtract", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SUBTRACT, '-'),
        new SwkEventType("decimal", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DECIMAL),
        new SwkEventType("divide", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DIVIDE, '/'),
        new SwkEventType("delete", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DELETE),
        new SwkEventType("num_lock", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUM_LOCK),
        new SwkEventType("scroll_lock", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_SCROLL_LOCK),
        new SwkEventType("f1", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F1),
        new SwkEventType("f2", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F2),
        new SwkEventType("f3", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F3),
        new SwkEventType("f4", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F4),
        new SwkEventType("f5", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F5),
        new SwkEventType("f6", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F6),
        new SwkEventType("f7", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F7),
        new SwkEventType("f8", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F8),
        new SwkEventType("f9", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F9),
        new SwkEventType("f10", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F10),
        new SwkEventType("f11", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F11),
        new SwkEventType("f12", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F12),
        new SwkEventType("f13", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F13),
        new SwkEventType("f14", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F14),
        new SwkEventType("f15", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F15),
        new SwkEventType("f16", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F16),
        new SwkEventType("f17", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F17),
        new SwkEventType("f18", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F18),
        new SwkEventType("f19", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F19),
        new SwkEventType("f20", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F20),
        new SwkEventType("f21", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F21),
        new SwkEventType("f22", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F22),
        new SwkEventType("f23", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F23),
        new SwkEventType("f24", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_F24),
        new SwkEventType("printscreen", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PRINTSCREEN),
        new SwkEventType("insert", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_INSERT),
        new SwkEventType("help", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_HELP),
        new SwkEventType("meta_l", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_META),
        new SwkEventType("back_quote", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_BACK_QUOTE, '`'),
        new SwkEventType("quote", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_QUOTE, '\''),
        new SwkEventType("kp_up", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_KP_UP),
        new SwkEventType("kp_down", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_KP_DOWN),
        new SwkEventType("kp_left", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_KP_LEFT),
        new SwkEventType("kp_right", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_KP_RIGHT),
        new SwkEventType("dead_grave", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_GRAVE),
        new SwkEventType("dead_acute", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_ACUTE),
        new SwkEventType("dead_circumflex", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX),
        new SwkEventType("dead_tilde", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_TILDE, '~'),
        new SwkEventType("dead_macron", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_MACRON),
        new SwkEventType("dead_breve", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_BREVE),
        new SwkEventType("dead_abovedot", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_ABOVEDOT),
        new SwkEventType("dead_diaeresis", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_DIAERESIS),
        new SwkEventType("dead_abovering", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_ABOVERING),
        new SwkEventType("dead_doubleacute", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_DOUBLEACUTE),
        new SwkEventType("dead_caron", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_CARON),
        new SwkEventType("dead_cedilla", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_CEDILLA),
        new SwkEventType("dead_ogonek", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_OGONEK),
        new SwkEventType("dead_iota", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_IOTA),
        new SwkEventType("dead_voiced_sound", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DEAD_VOICED_SOUND),
        new SwkEventType("dead_semivoiced_sound", SwkBinding.KEY,
            SwkBinding.PRESS, java.awt.event.KeyEvent.VK_DEAD_SEMIVOICED_SOUND),
        new SwkEventType("ampersand", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_AMPERSAND, '@'),
        new SwkEventType("asterisk", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ASTERISK, '*'),
        new SwkEventType("quotedbl", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_QUOTEDBL, '"'),
        new SwkEventType("less", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_LESS, '<'),
        new SwkEventType("greater", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_GREATER, '>'),
        new SwkEventType("braceleft", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_BRACELEFT, '{'),
        new SwkEventType("braceright", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_BRACERIGHT, '}'),
        new SwkEventType("at", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_AT, '@'),
        new SwkEventType("colon", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_COLON, ':'),
        new SwkEventType("circumflex", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CIRCUMFLEX, '^'),
        new SwkEventType("dollar", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_DOLLAR, '$'),
        new SwkEventType("euro_sign", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_EURO_SIGN),
        new SwkEventType("exclamation_mark", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_EXCLAMATION_MARK, '!'),
        new SwkEventType("inverted_exclamation_mark", SwkBinding.KEY,
            SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_INVERTED_EXCLAMATION_MARK),
        new SwkEventType("left_parenthesis", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS, '('),
        new SwkEventType("number_sign", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NUMBER_SIGN, '#'),
        new SwkEventType("plus", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PLUS),
        new SwkEventType("parenright", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS, ')'),
        new SwkEventType("right_parenthesis", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS),
        new SwkEventType("underscore", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_UNDERSCORE, '_'),
        new SwkEventType("final", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_FINAL),
        new SwkEventType("convert", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CONVERT),
        new SwkEventType("nonconvert", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_NONCONVERT),
        new SwkEventType("accept", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ACCEPT),
        new SwkEventType("modechange", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_MODECHANGE),
        new SwkEventType("kana", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_KANA),
        new SwkEventType("kanji", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_KANJI),
        new SwkEventType("alphanumeric", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ALPHANUMERIC),
        new SwkEventType("katakana", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_KATAKANA),
        new SwkEventType("hiragana", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_HIRAGANA),
        new SwkEventType("full_width", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_FULL_WIDTH),
        new SwkEventType("half_width", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_HALF_WIDTH),
        new SwkEventType("roman_characters", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ROMAN_CHARACTERS),
        new SwkEventType("all_candidates", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ALL_CANDIDATES),
        new SwkEventType("previous_candidate", SwkBinding.KEY,
            SwkBinding.PRESS, java.awt.event.KeyEvent.VK_PREVIOUS_CANDIDATE),
        new SwkEventType("code_input", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CODE_INPUT),
        new SwkEventType("japanese_katakana", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_JAPANESE_KATAKANA),
        new SwkEventType("japanese_hiragana", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_JAPANESE_HIRAGANA),
        new SwkEventType("japanese_roman", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_JAPANESE_ROMAN),
        new SwkEventType("cut", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_CUT),
        new SwkEventType("copy", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_COPY),
        new SwkEventType("paste", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PASTE),
        new SwkEventType("undo", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_UNDO),
        new SwkEventType("again", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_AGAIN),
        new SwkEventType("find", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_FIND),
        new SwkEventType("props", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_PROPS),
        new SwkEventType("stop", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_STOP),
        new SwkEventType("compose", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_COMPOSE),
        new SwkEventType("alt_graph", SwkBinding.KEY, SwkBinding.PRESS,
            java.awt.event.KeyEvent.VK_ALT_GRAPH)
    };
    String name;
    int type;
    int subtype;
    int detail;
    int mods = 0;

    SwkEventType(String name, int type, int flags) {
        this.name = name;
        this.type = type;
        this.subtype = flags;
    }

    SwkEventType(String name, int type, int flags, int detail) {
        this.name = name;
        this.type = type;
        this.subtype = flags;
        this.detail = detail;

        if (detail == java.awt.event.KeyEvent.VK_CONTROL) {
            this.mods = InputEvent.CTRL_DOWN_MASK;
        }

        if (detail == java.awt.event.KeyEvent.VK_SHIFT) {
            this.mods = InputEvent.SHIFT_DOWN_MASK;
        }

        if (detail == java.awt.event.KeyEvent.VK_META) {
            this.mods = InputEvent.META_DOWN_MASK;
        }

        if (detail == java.awt.event.KeyEvent.VK_ALT) {
            this.mods = InputEvent.ALT_DOWN_MASK;
        }
    }

    SwkEventType(String name, int type, int flags, int detail, char simpleChar) {
        this.name = name;
        this.type = type;
        this.subtype = flags;
        this.detail = detail;

        Character SimpleChar = new Character(simpleChar);
        simpleMap.put(SimpleChar, new Integer(detail));
    }

    static int getSimpleCode(Character sChar) {
        Integer keyInteger = (Integer) simpleMap.get(sChar);

        if (keyInteger == null) {
            return -1;
        } else {
            return keyInteger.intValue();
        }
    }

    static void initEventTable() {
        int i;
        SwkEventType eventType;
        eventTable = new Hashtable();
        modTable = new Hashtable();
        countTable = new Hashtable();
        detailTable = new Hashtable();

        for (i = 0; i < eventTypes.length; i++) {
            eventType = (SwkEventType) eventTypes[i];
            eventTable.put(eventType.name.toLowerCase(), eventType);
        }

        for (i = 0; i < modTypes.length; i++) {
            eventType = (SwkEventType) modTypes[i];
            modTable.put(eventType.name.toLowerCase(), eventType);
        }

        for (i = 0; i < countTypes.length; i++) {
            eventType = (SwkEventType) countTypes[i];
            countTable.put(eventType.name.toLowerCase(), eventType);
        }

        for (i = 0; i < detailTypes.length; i++) {
            eventType = (SwkEventType) detailTypes[i];
            detailTable.put(eventType.name.toLowerCase(), eventType);
        }
    }

    static public String getStringRep(int type, int subtype, int count, int mod,int detail,KeyStroke keyStroke) {
        String detailString = "";
        SwkEventType eventType = null;
        StringBuilder sBuild = new StringBuilder();
        for (int i = 0; i < eventTypes.length; i++) {
            eventType = eventTypes[i];

            if ((eventType.type == type) && (eventType.subtype == subtype)) {
                break;
            }
        }

        if (type == SwkBinding.MOUSE) {
            switch (detail) {
            case InputEvent.BUTTON1_MASK:
                detailString = "1";

                break;

            case InputEvent.BUTTON2_MASK:
                detailString = "2";

                break;

            case InputEvent.BUTTON3_MASK:
                detailString = "3";

                break;
            }
        } else if (type == SwkBinding.KEY) {
            if (keyStroke == null) {
                detailString = Character.toString((char) detail);
            } else {
            detailString = "";

            for (int j = 0; j < detailTypes.length; j++) {
                SwkEventType detailType = detailTypes[j];

                if (detail == detailType.detail) {
                    detailString = detailType.name;
                }
            }
            }
        }
        for (SwkEventType eventModType:modTypes) {
            if ((mod & eventModType.type) != 0)  {
                if ((type == SwkBinding.MOUSE) && (eventModType.name.startsWith("B"))) {
                    continue;
                }
                sBuild.append(eventModType.name+"-");
            }
        }
        if (count == 2) {
           sBuild.append("Double-");
        } else if (count == 3) {
           sBuild.append("Triple-");
        }
        sBuild.append(eventType.name);
        sBuild.append('-');
        sBuild.append(detailString);
        return sBuild.toString();
    }
}
