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
import java.awt.image.*;

import java.io.*;

import java.lang.*;

import java.net.*;

import java.util.*;

import javax.swing.*;


public class CursorCmd implements Command {
    static HashMap cursors = new HashMap();
    static final private String[] validCmds = { "create", "object", };
    static final private int OPT_CREATE = 0;
    static final private int OPT_OBJECT = 1;

    /*
        static {
            for (int i = 0; i < builtinImageNames.length; i++) {
                String fileName = "com/onemoonscientific/swank/library/images/" +
                    builtinImageNames[i] + ".bmp";
                URL url = Thread.currentThread().getContextClassLoader()
                                .getResource(fileName);

                if (url != null) {
                    ImageIcon image = new ImageIcon(url, builtinImageNames[i]);

                    if (image != null) {
                        builtinImages.put(builtinImageNames[i], image);
                    }
                }
            }
        }
     */
    public void cmdProc(Interp interp, TclObject[] argv)
        throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        switch (opt) {
        case OPT_CREATE: {
            if (argv.length < 4) {
                throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
            }

            String cursorName = argv[2].toString();
            int firstOption = 3;

            create(interp, cursorName, argv, firstOption);
            interp.setResult(cursorName);

            break;
        }

        case OPT_OBJECT: {
            if (argv.length != 3) {
                throw new TclNumArgsException(interp, 1, argv,
                    "object imageName");
            }

            Object cursorObject = cursors.get(argv[2].toString());

            if (cursorObject == null) {
                throw new TclException(interp,
                    "image " + argv[2].toString() + " doesn't exist");
            } else {
                if (cursorObject instanceof Cursor) {
                    TclObject tObj = ReflectObject.newInstance(interp,
                            Cursor.class, (Cursor) cursorObject);
                    interp.setResult(tObj);
                }
            }

            break;
        }
        }
    }

    public static void create(Interp interp, String cursorName,
        TclObject[] argv, int start) throws TclException {
        int i;

        if (argv.length <= start) {
            return;
        }

        ResourceObject ro = null;
        String fileName = null;
        int x = 0;
        int y = 0;

        for (i = start; i < argv.length; i += 2) {
            if ((i + 1) >= argv.length) {
                throw new TclException(interp,
                    "no value for \"" + argv[i].toString() + "\"");
            }

            if (argv[i].toString().equals("-file")) {
                fileName = argv[i + 1].toString();
            } else if (argv[i].toString().equals("-x")) {
                x = TclInteger.get(interp, argv[i + 1]);
            } else if (argv[i].toString().equals("-y")) {
                y = TclInteger.get(interp, argv[i + 1]);
            }
        }

        if (fileName == null) {
            throw new TclException(interp, "No filename specified");
        }

        ImageIcon imageIcon = null;
        URL url = null;

        if (fileName.startsWith("resource:")) {
            url = Thread.currentThread().getContextClassLoader().getResource(fileName.substring(10));
        } else {
            url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        }

        if (url != null) {
            imageIcon = new ImageIcon(url, cursorName);
        } else {
            imageIcon = new ImageIcon(fileName, cursorName);
        }


        if (imageIcon == null) {
            throw new TclException(interp,
                "Couldn't create image from file " + fileName);
        }

        Image image = imageIcon.getImage();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor cursor = toolkit.createCustomCursor(image, new Point(x, y),
                cursorName);

        if (cursor == null) {
            throw new TclException(interp, "Couldn't create cursor from image");
        }

        cursors.put(cursorName, cursor);
    }

    public static Cursor getCursor(String name) {
        return (Cursor) cursors.get(name);
    }
}
