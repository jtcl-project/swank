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
import java.net.*;
import java.util.*;
import javax.swing.*;

public class ImageCmd implements Command {

    private final static Hashtable images = new Hashtable();
    private final static Hashtable builtinImages = new Hashtable();
    private static int iImage = 0;
    static final private String[] builtinImageNames = {
        "error", "gray12", "gray25", "gray50", "gray75", "hourglass", "info",
        "question", "questhead", "warning",};
    static final private String[] validCmds = {
        "create", "delete", "configure", "types", "names", "object", "edge",
        "scale"
    };
    static final private int OPT_CREATE = 0;
    static final private int OPT_DELETE = 1;
    static final private int OPT_CONFIGURE = 2;
    static final private int OPT_TYPES = 3;
    static final private int OPT_NAMES = 4;
    static final private int OPT_OBJECT = 5;
    static final private int OPT_EDGE = 6;
    static final private int OPT_SCALE = 7;

    static {
        for (int i = 0; i < builtinImageNames.length; i++) {
            String fileName = "com/onemoonscientific/swank/library/images/"
                    + builtinImageNames[i] + ".bmp";
            URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);

            if (url != null) {
                ImageIcon image = new ImageIcon(url, builtinImageNames[i]);

                if (image != null) {
                    builtinImages.put(builtinImageNames[i], image);
                }
            }
        }
    }

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

                //need generic imagetype support
                if (!argv[2].toString().startsWith("bitmap")
                        && !argv[2].toString().startsWith("photo")
                        && !argv[2].toString().startsWith("obj")
                        && !argv[2].toString().startsWith("test")) {
                    throw new TclException(interp,
                            "image type \"" + argv[2].toString() + "\" doesn't exist");
                }

                String imageName;
                int firstOption = 3;

                if (argv[3].toString().startsWith("-")) {
                    imageName = "image" + iImage;
                    iImage++;
                    firstOption = 3;
                } else {
                    imageName = argv[3].toString();
                    firstOption = 4;
                }

                ImageIcon image = null;

                if (argv[2].toString().equals("test")) {
                    ImageIcon quest = (ImageIcon) builtinImages.get("questhead");
                    image = new ImageIcon(quest.getImage());
                } else {
                    image = new ImageIcon();
                }

                image.setDescription(imageName);
                images.put(imageName, image);

                configure(interp, image, argv, firstOption);

                if (argv[2].toString().equals("test")) {
                    interp.createCommand(imageName, new ImageTestCmd());
                } else if (!argv[2].toString().equals("bitmapb")) {
                    interp.createCommand(imageName, new ImageCmd());
                }

                interp.setResult(imageName);

                break;
            }

            case OPT_DELETE:

                for (i = 2; i < argv.length; i++) {
                    images.remove(argv[i].toString());
                    interp.deleteCommand(argv[i].toString());
                }

                break;

            case OPT_CONFIGURE:

                ImageIcon image = null;

                if (argv[0].toString().equals("image")) {
                    if (argv.length < 4) {
                        throw new TclNumArgsException(interp, 1, argv,
                                "option ?arg arg ...?");
                    }

                    image = (ImageIcon) images.get(argv[2].toString());

                    if (image != null) {
                        configure(interp, image, argv, 3);
                    }
                } else {
                    if (argv.length < 4) {
                        throw new TclNumArgsException(interp, 1, argv,
                                "option ?arg arg ...?");
                    }

                    image = (ImageIcon) images.get(argv[0].toString());

                    if (image != null) {
                        configure(interp, image, argv, 2);
                    }
                }

                break;

            case OPT_EDGE: {
                if (argv.length < 4) {
                    throw new TclNumArgsException(interp, 1, argv,
                            "option ?arg arg ...?");
                }

                Object sourceObject = images.get(argv[2].toString());
                Object destObject = images.get(argv[3].toString());
                BufferedImage destImage = null;

                if (destObject != null) {
                    if (destObject instanceof BufferedImage) {
                        destImage = (BufferedImage) destObject;
                    }
                }

                //throw exceptions
                if ((sourceObject != null)
                        && (sourceObject instanceof BufferedImage)) {
                    destImage = edge(interp, (BufferedImage) sourceObject, destImage);

                    if (destObject == null) {
                        images.put(argv[3].toString(), destImage);
                    }
                }

                break;
            }

            case OPT_SCALE: {
                if ((argv.length < 4) || (argv.length > 5)) {
                    throw new TclNumArgsException(interp, 1, argv,
                            "image scale ? offset ?");
                }

                Object sourceObject = images.get(argv[2].toString());

                if (sourceObject == null) {
                    throw new TclException(interp,
                            "image " + argv[2].toString() + " doesn't exist");
                }

                if (!(sourceObject instanceof BufferedImage)) {
                    throw new TclException(interp,
                            "image " + argv[2].toString() + " not BufferedImage");
                }

                double scaleValue = TclDouble.get(interp, argv[3]);
                double offset = 0.0;

                if (argv.length == 5) {
                    offset = TclDouble.get(interp, argv[4]);
                }

                BufferedImage sourceImage = (BufferedImage) sourceObject;
                BufferedImage destImage = scale(interp, sourceImage, scaleValue,
                        offset, sourceImage);

                //images.put(argv[2].toString(),destImage);
                break;
            }

            case OPT_OBJECT: {
                if (argv.length != 3) {
                    throw new TclNumArgsException(interp, 1, argv,
                            "object imageName");
                }

                Object imageObject = images.get(argv[2].toString());

                if (imageObject == null) {
                    throw new TclException(interp,
                            "image " + argv[2].toString() + " doesn't exist");
                } else {
                    if (imageObject instanceof BufferedImage) {
                        TclObject tObj = ReflectObject.newInstance(interp,
                                BufferedImage.class, (BufferedImage) imageObject);
                        interp.setResult(tObj);
                    } else {
                        BufferedImage bufferedImage = makeBufferedImage((ImageIcon) imageObject);
                        ImageCmd.images.put(argv[2].toString(), bufferedImage);

                        TclObject tObj = ReflectObject.newInstance(interp,
                                BufferedImage.class, bufferedImage);
                        interp.setResult(tObj);
                    }
                }

                break;
            }

            case OPT_TYPES: {
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                TclObject list = TclList.newInstance();
                TclList.append(interp, list, TclString.newInstance("photo"));
                TclList.append(interp, list, TclString.newInstance("bitmap"));
                TclList.append(interp, list, TclString.newInstance("test"));
                interp.setResult(list);

                break;
            }

            case OPT_NAMES: {
                if (argv.length != 2) {
                    throw new TclNumArgsException(interp, 1, argv, "option");
                }

                TclObject list = TclList.newInstance();
                Enumeration e = images.keys();

                while (e.hasMoreElements()) {
                    TclList.append(interp, list,
                            TclString.newInstance((String) e.nextElement()));
                }

                interp.setResult(list);

                break;
            }
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
            double scaleValue, double offsetValue, BufferedImage destImage) {
        RescaleOp op = new RescaleOp((float) scaleValue, (float) offsetValue,
                null);

        return (op.filter(sourceImage, destImage));
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
                URL url = null;

                if (fileName.startsWith("resource:")) {
                    url = Thread.currentThread().getContextClassLoader().getResource(fileName.substring(10));
                } else {
                    url = Thread.currentThread().getContextClassLoader().getResource(fileName);
                }

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
    public static void addImage(final Interp interp, final String imageName,final BufferedImage image) {
        images.put(imageName, image);
    }
    public static void addImage(final Interp interp, final String imageName,final ImageIcon image) {
        images.put(imageName, image);
    }
    public static Object getImage(String imageName) {
        Object imageObject = builtinImages.get(imageName);

        if (imageObject == null) {
            imageObject = images.get(imageName);
        }

        return imageObject;
    }

    public static BufferedImage makeBufferedImage(ImageIcon imageIcon) {
        BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(),
                imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(imageIcon.getImage(), 0, 0, null);

        return bufferedImage;
    }
}
