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
import tcl.pkg.java.ReflectObject;

import java.awt.*;

import java.io.*;

import java.util.*;

import javax.swing.*;

public class PasswordCmd implements Command {

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if ((argv.length < 3) || (argv.length > 3)) {
            throw new TclNumArgsException(interp, 1, argv, "?frame title?");
        }

        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[1].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[1].toString() + "\"");
        }

        SwkWidget swkwidget = (SwkWidget) ReflectObject.get(interp, tObj);
        Component component = (Component) swkwidget;
        String title = argv[2].toString();
        PasswordValue pwValue = (new Password()).exec(null, title);
        TclObject pwObj = ReflectObject.newInstance(interp, PasswordValue.class, pwValue);
        interp.setResult(pwObj);
    }

    public class PasswordValue {

        String name = "";
        char[] password = null;

        PasswordValue(String name, char[] password) {
            this.name = name;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public char[] getPassword() {
            return password;
        }

        void clear() {
            if (password != null) {
                for (int i = 0; i < password.length; i++) {
                    password[i] = 0;
                }
            }
        }
    }

    class Password extends GetValueOnEventThread {

        Frame frame = null;
        String title = null;
        String name = null;
        char[] password = null;

        public PasswordValue exec(final Frame frame, final String title) {
            this.frame = frame;
            this.title = title;
            execOnThread();
            return new PasswordValue(name, password);
        }

        public void run() {
            PasswordDialog p = new PasswordDialog(frame, title);
            if (p.showDialog()) {
                name = p.getName();
                password = p.getPassword();
            }
        }
    }
}
