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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author brucejohnson
 */
public class SwkBorder extends AbstractBorder {

    private int width = 6;

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        int i1 = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        if (c instanceof JRootPane) {
            c = c.getParent();
        }
        String relief = ((SwkWidget) c).getRelief();
        width = (int) ((SwkWidget) c).getBorderWidth();

        if (width == 0) {
            return;
        }
        if ((relief == null) || (relief.equals(""))) {
            return;
        } else if (relief.startsWith("f")) {
            return;
        } else if (relief.startsWith("r")) {
            i1 = 0;
            i2 = width;
            i3 = 0;
            i4 = 0;
        } else if (relief.startsWith("s")) {
            i1 = 0;
            i2 = 0;
            i3 = 0;
            i4 = width;
        } else if (relief.startsWith("g")) {
            i1 = 0;
            i2 = width / 2;
            i3 = width / 2;
            i4 = width;
        } else if (relief.startsWith("r")) {
            i1 = width / 2;
            i2 = width;
            i3 = 0;
            i4 = width / 2;
        }

        g.setColor(c.getBackground());
        g.setColor(g.getColor().brighter());

        for (int i = i1; i < i2; i++) {
            //System.out.println(g.getColor());
            g.drawLine(x + i, y + i, (x + w) - i, y + i);
            g.drawLine(x + i, y + i, x + i, (y + h) - i);
        }

        g.setColor(c.getBackground());
        g.setColor(g.getColor().darker());

        for (int i = i3; i < i4; i++) {
            //System.out.println(g.getColor());
            g.drawLine(x + i, y + i, (x + w) - i, y + i);
            g.drawLine(x + i, y + i, x + i, (y + h) - i);
        }

        for (int i = i1; i < i2; i++) {
            //System.out.println(g.getColor());
            g.drawLine(x + i, (y + h) - i, (x + w) - i, (y + h) - i);
            g.drawLine((x + w) - i, y + i, (x + w) - i, (y + h) - i);
        }

        g.setColor(c.getBackground());
        g.setColor(g.getColor().brighter());

        for (int i = i3; i < i4; i++) {
            //System.out.println(g.getColor());
            g.drawLine(x + i, (y + h) - i, (x + w) - i, (y + h) - i);
            g.drawLine((x + w) - i, y + i, (x + w) - i, (y + h) - i);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        if (c instanceof JRootPane) {
            c = c.getParent();
        }
        int bWidth = (int) ((SwkWidget) c).getBorderWidth();
        Insets eBI = ((SwkWidget) c).getEmptyBorderInsets();

        if (eBI == null) {
            return new Insets(bWidth, bWidth, bWidth, bWidth);
        } else {
            return new Insets(bWidth + eBI.top, bWidth + eBI.left,
                    bWidth + eBI.bottom, bWidth + eBI.right);
        }
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
