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
/*
 * SwkWindow.java
 *
 * Created on February 19, 2000, 3:14 PM
 */
/**
 *
 * @author  JOHNBRUC
 * @version
 */
package com.onemoonscientific.swank.canvas;

import com.onemoonscientific.swank.*;
import tcl.lang.*;
import java.awt.*;

/**
 *
 * @author brucejohnson
 */
public class ItemWindow extends SwkShape {

    static CanvasParameter[] parameters = {
        new WindowParameter(), new AnchorParameter(),
        new WidthParameter(), new HeightParameter(), new TagsParameter(),
        new TransformerParameter(),};

    static {
        initializeParameters(parameters, parameterMap);
    }
    String windowName = "";
    int winWidth = 0;
    int winHeight = 0;
    PlacerLayout placer = null;
    SwkWidget window = null;

    ItemWindow(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        storeCoords = new double[2];
    }

    /**
     *
     * @param canvas
     * @param coords
     * @throws SwkException
     */
    @Override
    public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        if (coords.length != 2) {
            throw new SwkException("wrong # coordinates: expected 2, got "
                    + coords.length);
        }

        storeCoords[0] = coords[0];
        storeCoords[1] = coords[1];
    }

    /**
     *
     * @param interp
     * @param swkCanvas
     * @param argv
     * @param start
     * @throws TclException
     */
    @Override
    protected void configShape(Interp interp, SwkImageCanvas swkCanvas,
            TclObject[] argv, int start) throws TclException {
        for (int i = start; i < argv.length; i += 2) {
            if (((i + 1) < argv.length)
                    && "-window".startsWith(argv[i].toString())) {
                windowName = argv[i + 1].toString();
                setupLayout(interp);

                break;
            }
        }

        super.configShape(interp, swkCanvas, argv, start);
    }

    /**
     *
     */
    @Override
    protected void applyCoordinates() {
        addWindow();
    }

    void setupLayout(Interp interp) throws TclException {
        if ((windowName == null) || windowName.equals("")) {
            return;
        }

        window = (SwkWidget) Widgets.get(interp, windowName);

        LayoutManager layoutManager;

        if (!(canvas.getComponent() instanceof Container)) {
            throw new TclException(interp,
                    "can't add window to this type of canvas");
        }

        Container parent = (Container) canvas.getComponent();
        layoutManager = parent.getLayout();

        if (!(layoutManager instanceof PlacerLayout)) {
            parent.removeAll();
            placer = new PlacerLayout(interp);
            parent.setLayout(placer);
        } else {
            placer = (PlacerLayout) layoutManager;
        }
    }

    void addWindow() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("-x ");
        sbuf.append((int) storeCoords[0]);
        sbuf.append(" -y ");
        sbuf.append((int) storeCoords[1]);

        if (winWidth != 0) {
            sbuf.append(" -width ");
            sbuf.append(winWidth);
        }

        if (winHeight != 0) {
            sbuf.append(" -height ");
            sbuf.append(winHeight);
        }

        Container parent = (Container) canvas.getComponent();
        placeWindow(parent, placer, window, sbuf.toString());
        Component comp = (Component) window;
        shape = comp.getBounds();
    }

    void placeWindow(Container parent, PlacerLayout placer, SwkWidget window,
            String layoutString) {
        try {
            ((Component) window).invalidate();

            if (!placer.updateLayoutComponent(layoutString, (Component) window)) {
                placer.setIgnoreNextRemove(true);
                parent.add(layoutString, (Component) window);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            placer.setIgnoreNextRemove(false);
        }
    }

    /**
     *
     * @return
     */
    public String getType() {
        return "window";
    }
}
