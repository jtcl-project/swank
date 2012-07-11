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

import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class SwkBinding implements Comparable {

    /**
     *
     */
    public static final int FOCUS = 1;
    /**
     *
     */
    public static final int MOUSE = 2;
    /**
     *
     */
    public static final int KEY = 3;
    /**
     *
     */
    public static final int MOUSEMOTION = 4;
    /**
     *
     */
    public static final int COMPONENT = 5;

    /**
     *
     */
    public static final int CONTAINER = 6;
    /**
     *
     */
    public static final int WINDOW = 7;
    /**
     *
     */
    public static final int APP = 8;
    /**
     *
     */
   public static final int MOUSEWHEEL = 9;
    /**
     *
     */
    public static final int PRESS = 0;
    /**
     *
     */
    public static final int RELEASE = 1;
    /**
     *
     */
    public static final int CLICK = 2;
    /**
     *
     */
    public static final int ENTER = 3;
    /**
     *
     */
    public static final int EXIT = 4;
    /**
     *
     */
    public static final int IN = 5;
    /**
     *
     */
    public static final int OUT = 6;
    /**
     *
     */
    public static final int EXPOSE = 7;
    /**
     *
     */
    public static final int DESTROY = 8;
    /**
     *
     */
    public static final int MOTION = 9;
    /**
     *
     */
    public static final int SHOWN = 10;
    /**
     *
     */
    public static final int TYPE = 11;
    /**
     *
     */
    public static final int RESIZE = 12;
    /**
     *
     */
    public static final int HIDDEN = 13;
    /**
     *
     */
    public static final int MOVED = 14;
    /**
     *
     */
    public static final int STATECHANGED = 15;
    /**
     *
     */
    public static final int SELECTIONCHANGED = 16;
    /**
     *
     */
    public static final int ACTIVATION = 17;
    /**
     *
     */
    public static final int ACTIVATED = 18;
    /**
     *
     */
    public static final int DEACTIVATED = 19;
    /**
     *
     */
    public boolean virtual = false;
    /**
     *
     */
    public int type = 0;
    /**
     *
     */
    public int subtype = 0;
    /**
     *
     */
    public int mod = 0;
    /**
     *
     */
    public int count = 1;
    /**
     *
     */
    public int detail = 0;
    /**
     *
     */
    public boolean add = false;
    /**
     *
     */
    public boolean remove = false;
    /**
     *
     */
    public KeyStroke keyStroke = null;
    /**
     *
     */
    public String name = null;
    /**
     *
     */
    public String command = null;
    /**
     *
     */
    public String string = null;

  @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.virtual ? 1 : 0);
        hash = 97 * hash + this.type;
        hash = 97 * hash + this.subtype;
        hash = 97 * hash + this.mod;
        hash = 97 * hash + this.count;
        hash = 97 * hash + this.detail;
        hash = 97 * hash + (this.keyStroke != null ? this.keyStroke.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @return
     */
    public String getEvent() {
        return SwkEventType.getStringRep(type, subtype, count, mod, detail, keyStroke);
    }

    @Override
    public String toString() {
        return SwkEventType.getStringRep(type, subtype, count, mod, detail, keyStroke) + " " + command;
    }

    /**
     *
     * @return
     */
    public String getCommand() {
        String result = "";
        if (command != null) {
            result = command;
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SwkBinding)) {
            return false;
        } 
        SwkBinding testBinding = (SwkBinding) object;
        if (type != testBinding.type) {
            return false;
        } else if (subtype != testBinding.subtype) {
            return false;
        } else if (mod != testBinding.mod) {
            return false;
        } else if (count != testBinding.count) {
            return false;
        } else if (detail != testBinding.detail) {
            return false;
        } else if (keyStroke != testBinding.keyStroke) {
            return false;
        } else {
            return (true);
        }
    }

    /**
     *
     * @param testBinding
     * @return
     */
    public boolean sameButClick(SwkBinding testBinding) {
        if (testBinding == null) {
            return false;
        } else if (type != testBinding.type) {
            return false;
        } else if (subtype != testBinding.subtype) {
            return false;
        } else if (mod != testBinding.mod) {
            return false;
        } else if (detail != testBinding.detail) {
            return false;
        } else if (keyStroke != testBinding.keyStroke) {
            return false;
        } else if (count == testBinding.count) {
            return false;
        } else {
            return (true);
        }
    }

    public int compareTo(Object o) {
        int result = 0;
        if (o == null) {
            result = 1;
        } else {
            if (!(o instanceof SwkBinding)) {
                result = 1;
            } else {
                SwkBinding b2 = (SwkBinding) o;
                if (count > b2.count) {
                    result = -1;
                } else if (count < b2.count) {
                    result = 1;
                }
            }
        }
        return result;
    }
}
