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

/**
 *
 * @author brucejohnson
 */
public class PasswordCmd implements Command {

    /**
     * 
     * @param interp
     * @param argv
     * @throws TclException
     */
    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if ((argv.length < 3) || (argv.length > 7)) {
            throw new TclNumArgsException(interp, 1, argv, "frame title ?x y userName msg?");
        }

        TclObject tObj = (TclObject) Widgets.getWidget(interp, argv[1].toString());

        if (tObj == null) {
            throw new TclException(interp,
                    "bad window path name \"" + argv[1].toString() + "\"");
        }

        // fixme, what are we using argv[1] for
        // SwkWidget swkwidget = (SwkWidget) ReflectObject.get(interp, tObj);
        String title = argv[2].toString();
        int x = -1;
        int y = -1;
        if (argv.length >= 4) {
            x = TclInteger.getInt(interp,argv[3]);
            y = TclInteger.getInt(interp,argv[4]);
        }
        String userName = "";
        String message = "";
        if (argv.length >= 6) {
            userName = argv[5].toString();
            message = argv[6].toString();
        }
        PasswordValue pwValue = (new Password()).exec(null, title,x,y,userName,message);
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

    private class Password extends GetValueOnEventThread {

        Frame frame = null;
        String title = null;
        String userName = null;
        String msg = null;
        String name = null;
        char[] password = null;
        int x = -1;
        int y = -1;

        public PasswordValue exec(final Frame frame, final String title) {
            this.frame = frame;
            this.title = title;
            execOnThread();
            return new PasswordValue(name, password);
        }
        public PasswordValue exec(final Frame frame, final String title, final int x, final int y, final String userName, final String msg) {
            this.frame = frame;
            this.title = title;
            this.userName = userName;
            this.msg = msg;
            this.x = x;
            this.y = y;
            execOnThread();
            return new PasswordValue(name, password);
        }

        @Override
        public void run() {
            PasswordDialog p = new PasswordDialog(frame, title);
            p.setName(userName);
            p.setMsg(msg);
            if (y >= 0) {
                if (p.showDialog(x,y)) {
                    name = p.getName();
                    password = p.getPassword();
                }
            } else {
                if (p.showDialog()) {
                    name = p.getName();
                    password = p.getPassword();
                }
            }
        }
    }
}
