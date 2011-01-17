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
import java.awt.image.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class ImageTestCmd implements Command {

    static Hashtable images = new Hashtable();
    static int iImage = 0;
    private static final String[] validCmds = {"changed"};
    private static final int OPT_CHANGED = 0;

    /**
     *
     * @param interp
     * @param argv
     * @throws TclException
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
            case OPT_CHANGED: {
                if (argv.length != 8) {
                    throw new TclNumArgsException(interp, 1, argv,
                            "option ?arg arg ...?");
                }

                break;
            }

            default:
                throw new TclRuntimeError("TclIndex.get() error");
        }
    }

    /**
     *
     * @param interp
     * @param sourceImage
     * @param destImage
     * @return
     */
    public static BufferedImage edge(Interp interp, BufferedImage sourceImage,
            BufferedImage destImage) {
        float[] edge = {0f, -1f, 0f, -1f, 4f, -1f, 0f, -1f, 0f};
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, edge),
                ConvolveOp.EDGE_NO_OP, null);

        return (op.filter(sourceImage, destImage));
    }

    /**
     *
     * @param interp
     * @param sourceImage
     * @param scaleValue
     * @param offsetValue
     * @return
     */
    public static BufferedImage scale(Interp interp, BufferedImage sourceImage,
            double scaleValue, double offsetValue) {
        RescaleOp op = new RescaleOp((float) scaleValue, (float) offsetValue,
                null);

        return (op.filter(sourceImage, null));
    }

    /**
     *
     * @param interp
     * @param image
     * @param argv
     * @param start
     * @throws TclException
     */
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
