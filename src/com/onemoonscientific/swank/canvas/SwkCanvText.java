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
 * SwkEllipse.java
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
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.lang.*;

import java.text.*;

import java.util.*;

import javax.swing.*;


public class SwkCanvText extends SwkShape {
    static BreakIterator wordIterator = BreakIterator.getWordInstance();
    static CanvasParameter[] parameters = {
        new TextParameter(), new AnchorParameter(), new FontParameter(),
        new WidthParameter(), new FillParameter(), new TagsParameter(),
        new TransformerParameter(), new RotateParameter(),
    };
    static Map parameterMap = new TreeMap();

    static {
        initializeParameters(parameters, parameterMap);
    }

    String text = null;
    Font font = null;
    double x = 0.0;
    double y = 0.0;
    float[] anchor = { 0.0f, 0.0f };
    int[] ends = null;
    Rectangle2D.Float rf2 = new Rectangle2D.Float();

    SwkCanvText(Shape shape, SwkImageCanvas canvas) {
        super(shape, canvas);
        width = 0;
        storeCoords = new double[2];
        setFont(new Font("Courier", Font.PLAIN, 12));
        fill = Color.BLACK;
    }

    public void coords(SwkImageCanvas canvas, double[] coords)
        throws SwkException {
        if (coords.length != 2) {
            throw new SwkException("wrong # coordinates: expected 2, got " +
                coords.length);
        }

        setX(coords[0]);
        setY(coords[1]);
    }

    CanvasParameter[] getParameters() {
        return parameters;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public String getType() {
        return "text";
    }

    public double getX() {
        return storeCoords[0];
    }

    public void setX(final double x) {
        storeCoords[0] = x;
    }

    public double getY() {
        return storeCoords[1];
    }

    public void setY(final double y) {
        storeCoords[1] = y;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public float[] getAnchor() {
        return this.anchor;
    }

    public void setAnchor(final float[] anchor) {
        this.anchor = anchor;
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(final Font font) {
        this.font = font;
    }

    int getLineBreaks(FontRenderContext fRC, Font font, String text) {
        float width1 = (float) (font.getStringBounds(text, fRC).getWidth());
        int iEnd = 1;

        if (text.indexOf('\n') != -1) {
            int nNewLines = 0;

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    nNewLines++;
                }
            }

            ends = new int[nNewLines + 2];
            ends[0] = 0;

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    ends[iEnd++] = i;
                }
            }

            ends[iEnd] = text.length();

            return iEnd;
        }

        if ((width != 0) && (width1 > width)) {
            int start = 0;

            // FIXME this guess could be to small, need to check when using ends if its ok, or switch to Vector
            int guessSize = (int) ((width1 * 2) / width) + 1;

            if ((ends == null) || (ends.length < guessSize)) {
                ends = new int[guessSize];
            }

            ends[0] = 0;
            wordIterator.setText(text);
            start = wordIterator.first();

            int okEnd = text.length();
            boolean gotAnEnd = false;

            for (int end = wordIterator.next(); end != BreakIterator.DONE;
                    end = wordIterator.next()) {
                width1 = (float) (font.getStringBounds(text.substring(start, end),
                        fRC).getWidth());

                if (width1 < width) {
                    okEnd = end;
                    ends[iEnd] = okEnd;
                    gotAnEnd = true;
                } else {
                    if (gotAnEnd) {
                        iEnd++;
                        start = okEnd;
                        okEnd = end;
                        ends[iEnd] = okEnd;
                        gotAnEnd = true;
                    }
                }
            }
        }

        return iEnd;
    }

    // FIXME getting bounds of multiline text not correct
    public Rectangle2D getBounds() {
        String text = getText();

        if (text == null) {
            return new Rectangle2D.Float((float) getX(), (float) getY(), 1, 1);
        }

        FontRenderContext fRC = canvas.getFontRenderContext();

        if (fRC == null) {
            return new Rectangle2D.Float((float) getX(), (float) getY(), 1, 1);
        }

        AffineTransform aT = new AffineTransform();
        Font font = getFont();
        float width1 = (float) (font.getStringBounds(text, fRC).getWidth());
        float height1 = (float) (font.getStringBounds(text, fRC).getHeight());
        float width2 = (float) (width1 * getAnchor()[1]);
        float height2 = (float) (height1 * getAnchor()[0]);
        Rectangle2D.Float rf1 = new Rectangle2D.Float((float) (getX() - width2),
                (float) (getY() - height1 + height2), width1, height1);
        rf1 = (Rectangle2D.Float) aT.createTransformedShape(rf1).getBounds2D();

        return rf1;
    }

    public void paint(Graphics2D g2, FontRenderContext fRC) {
        if (this.getFont() != null) {
            g2.setFont(this.getFont());
        }

        if (this.fill == null) {
            this.fill = Color.BLACK;
        }

        g2.setPaint(this.fill);

        String text = this.getText();

        if ((text == null) || (text.equals(""))) {
            this.shape = new Rectangle2D.Float((float) getX(), (float) getY(),
                    1, 1);

            return;
        }

        int nLines = getLineBreaks(fRC, getFont(), text);
        String textLine = text;
        double y = getY();
        float width2 = 0;

        for (int i = 0; i < nLines; i++) {
            if (nLines > 1) {
                textLine = text.substring(ends[i], ends[i + 1]).trim();
            }

            float width1 = (float) (this.getFont().getStringBounds(textLine, fRC)
                                        .getWidth());
            float height1 = (float) (this.getFont()
                                         .getStringBounds(textLine, fRC)
                                         .getHeight());

            if (i == 0) {
                width2 = (float) (width1 * this.getAnchor()[1]);
            }

            float height2 = (float) (height1 * this.getAnchor()[0]);
            AffineTransform aT = new AffineTransform();
            AffineTransform shapeTransform = this.getTransform();

            if (shapeTransform != null) {
                aT.setTransform(shapeTransform);
            }

            aT.rotate(this.rotate, this.getX(), y);

            Rectangle2D.Double rf2 = new Rectangle2D.Double((this.getX() -
                    width2), (y + height2), 0.0, 0.0);
            Rectangle2D rf2D = aT.createTransformedShape(rf2).getBounds2D();

            Rectangle2D.Double rf1 = new Rectangle2D.Double((float) (this.getX() -
                    width2), (float) (y - height1 + height2), width1, height1);

            Rectangle2D rf1d = aT.createTransformedShape(rf1).getBounds2D();
            this.shape = rf1d;

            if (this.rotate != 0.0) {
                g2.rotate(this.rotate, rf2D.getX(), rf2D.getY());
            }

            if (textLine != null) {
                g2.drawString(textLine, (int) (rf2D.getX()), (int) (rf2D.getY()));
            }

            if (this.rotate != 0.0) {
                g2.rotate(-this.rotate, rf2D.getX(), rf2D.getY());
            }

            y += height1;
        }
    }
}
