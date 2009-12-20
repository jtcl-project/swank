/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.onemoonscientific.swank.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.BreakIterator;

/**
 *
 * @author brucejohnson
 */
public class TextParameters {

    static private final TextParameters defaultPar = new TextParameters();
    private String text = "";
    private Font font = null;
    private Color textColor = null;
    private float[] anchor = {0.0f, 0.0f};
    static BreakIterator wordIterator = BreakIterator.getWordInstance();
    int[] ends = null;
    Rectangle2D.Float rf2 = new Rectangle2D.Float();

    static TextParameters getDefault() {
        return defaultPar;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param cap the cap to set
     */
    public static TextParameters setText(TextParameters strokePar, String newValue) {
        if (!newValue.equals(strokePar.text)) {
            if (strokePar == defaultPar) {
                strokePar = new TextParameters();
            }
            strokePar.text = newValue;

        }
        return strokePar;
    }

    /**
     * @return the join
     */
    public Font getFont() {
        return font;
    }

    public static TextParameters setFont(TextParameters strokePar, Font newValue) {
        if (!newValue.equals(strokePar.font)) {
            if (strokePar == defaultPar) {
                strokePar = new TextParameters();
            }
            strokePar.font = newValue;

        }
        return strokePar;
    }

    /**
     * @return the miterLimit
     */
    public Color getTextColor() {
        if (textColor == null) {
            return Color.BLACK;
        } else {
            return textColor;
        }
    }

    /**
     * @param miterLimit the miterLimit to set
     */
    public static TextParameters setTextColor(TextParameters strokePar, Color newValue) {
        if (!newValue.equals(strokePar.textColor)) {
            if (strokePar == defaultPar) {
                strokePar = new TextParameters();
            }
            strokePar.textColor = newValue;

        }
        return strokePar;
    }

    /**
     * @return the dash
     */
    public float[] getAnchor() {
        return anchor;
    }

    /**
     * @param dash the dash to set
     */
    public static TextParameters setAnchor(TextParameters strokePar, float[] newValue) {
        boolean change = false;
        if (newValue.length != strokePar.anchor.length) {
            change = true;
        } else {
            for (int i = 0; i > newValue.length; i++) {
                if (newValue[i] != strokePar.anchor[i]) {
                    change = true;
                    break;
                }
            }
        }
        if (change) {
            if (strokePar == defaultPar) {
                strokePar = new TextParameters();
            }
            strokePar.anchor[0] = newValue[0];
            strokePar.anchor[1] = newValue[1];

        }
        return strokePar;
    }

    int getLineBreaks(FontRenderContext fRC, Font font, String text, float width) {
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

    public void paint(Graphics2D g2, FontRenderContext fRC, SwkShape swkShape, double x, double y) {
        if (this.getFont() != null) {
            g2.setFont(this.getFont());
        }



        g2.setPaint(getTextColor());

        String text = this.getText();

        if ((text == null) || (text.equals(""))) {
            swkShape.shape = new Rectangle2D.Float((float) x, (float) y,
                    1, 1);

            return;
        }

        int nLines = getLineBreaks(fRC, getFont(), text, swkShape.width);
        String textLine = text;
        float width2 = 0;

        for (int i = 0; i < nLines; i++) {
            if (nLines > 1) {
                textLine = text.substring(ends[i], ends[i + 1]).trim();
            }

            float width1 = (float) (this.getFont().getStringBounds(textLine, fRC).getWidth());
            float height1 = (float) (this.getFont().getStringBounds(textLine, fRC).getHeight());

            if (i == 0) {
                width2 = (float) (width1 * this.getAnchor()[1]);
            }

            float height2 = (float) (height1 * this.getAnchor()[0]);
            AffineTransform aT = new AffineTransform();
            AffineTransform shapeTransform = swkShape.getTransform();

            if (shapeTransform != null) {
                aT.setTransform(shapeTransform);
            }

            aT.rotate(swkShape.rotate, x, y);

            Rectangle2D.Double rf2 = new Rectangle2D.Double((x
                    - width2), (y + height2), 0.0, 0.0);
            Rectangle2D rf2D = aT.createTransformedShape(rf2).getBounds2D();

            Rectangle2D.Double rf1 = new Rectangle2D.Double((float) (x
                    - width2), (float) (y - height1 + height2), width1, height1);

            Rectangle2D rf1d = aT.createTransformedShape(rf1).getBounds2D();
            swkShape.shape = rf1d;

            if (swkShape.rotate != 0.0) {
                g2.rotate(swkShape.rotate, rf2D.getX(), rf2D.getY());
            }

            if (textLine != null) {
                g2.drawString(textLine, (int) (rf2D.getX()), (int) (rf2D.getY()));
            }

            if (swkShape.rotate != 0.0) {
                g2.rotate(-swkShape.rotate, rf2D.getX(), rf2D.getY());
            }

            y += height1;
        }
    }
}
