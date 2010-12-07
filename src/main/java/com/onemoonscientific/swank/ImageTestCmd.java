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
import tcl.pkg.java.ReflectObject;

import java.awt.*;
import java.awt.image.*;

import java.io.*;

import java.net.*;

import java.util.*;

import javax.swing.*;

public class ImageTestCmd implements Command {

    static Hashtable images = new Hashtable();
    static int iImage = 0;
    static final private String[] validCmds = {"changed"};
    static final private int OPT_CHANGED = 0;

    public void cmdProc(Interp interp, TclObject[] argv)
            throws TclException {
        int i;

        if (argv.length < 2) {
            throw new TclNumArgsException(interp, 1, argv,
                    "option ?arg arg ...?");
        }

        int opt = TclIndex.get(interp, argv[1], validCmds, "option", 0);

        switch (opt) {
            case OPT_CHANGED: {
                if (argv.length != 8) {
                    throw new TclNumArgsException(interp, 1, argv,
                            "option ?arg arg ...?");
                }

                int x = TclInteger.get(interp, argv[3]);
                int y = TclInteger.get(interp, argv[3]);
                int width = TclInteger.get(interp, argv[3]);
                int height = TclInteger.get(interp, argv[3]);
                int timWidth = TclInteger.get(interp, argv[3]);
                int timHeight = TclInteger.get(interp, argv[3]);

                break;
            }

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    public static BufferedImage edge(Interp interp, BufferedImage sourceImage,
            BufferedImage destImage) {
        float[] edge = {0f, -1f, 0f, -1f, 4f, -1f, 0f, -1f, 0f};
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, edge),
                ConvolveOp.EDGE_NO_OP, null);

        return (op.filter(sourceImage, destImage));
    }

    public static BufferedImage scale(Interp interp, BufferedImage sourceImage,
            double scaleValue, double offsetValue) {
        RescaleOp op = new RescaleOp((float) scaleValue, (float) offsetValue,
                null);

        return (op.filter(sourceImage, null));
    }

    public static void configure(Interp interp, ImageIcon image,
            TclObject[] argv, int start) throws TclException {
        int i;

        if (argv.length <= start) {
            return;
        }

        ResourceObject ro = null;

        for (i = start; i < argv.length; i += 2) {
            if (argv[i].toString().equals("-file")) {
                String fileName = argv[i + 1].toString();
                String imageName = image.getDescription();

                ImageIcon image2 = null;
                URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);

                if (url != null) {
                    image2 = new ImageIcon(url, imageName);
                } else {
                    image2 = new ImageIcon(fileName, imageName);
                }

                if (image2 == null) {
                    throw new TclException(interp,
                            "Couldn't create image from file " + fileName);
                }

                image.setImage(image2.getImage());
            } else if (argv[i].toString().equals("-object")) {
                Object iObj = ReflectObject.get(interp, argv[i + 1]);

                if (iObj instanceof BufferedImage) {
                    String imageName = image.getDescription();
                    images.put(imageName, (BufferedImage) iObj);
                }
            }
        }
    }
}
