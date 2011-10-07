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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.BreakIterator;

/**
 *
 * @author brucejohnson
 */
public class TextParameters {

    static private final TextParameters defaultPar = new TextParameters();
    static private final Font defaultFont = new Font(Font.MONOSPACED,Font.PLAIN,12);
    private String text = "";
    private Font font = defaultFont;
    private Color textColor = null;
    private float[] anchor = {0.0f, 0.0f};
    static BreakIterator wordIterator = BreakIterator.getWordInstance();
    int[] ends = null;

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
     * @param strokePar
     * @param newValue
     * @return
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

    /**
     *
     * @param strokePar
     * @param newValue
     * @return
     */
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
     * @param strokePar
     * @param newValue
     * @return
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
        return anchor.clone();
    }

    /**
     * @param strokePar
     * @param newValue
     * @return
     */
    public static TextParameters setAnchor(TextParameters strokePar, float[] newValue) {
        boolean change = false;
        if (newValue.length != strokePar.anchor.length) {
            change = true;
        } else {
            for (int i = 0; i < newValue.length; i++) {
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

    /**
     *
     * @param g2
     * @param fRC
     * @param swkShape
     * @param x
     * @param y
     * @return
     */
    public Rectangle2D paint(Graphics2D g2, FontRenderContext fRC, SwkShape swkShape, double x, double y) {
        Font paintFont = g2.getFont();
        if (getFont() != null) {
            paintFont = getFont();
            g2.setFont(paintFont);
        }

        g2.setPaint(getTextColor());

        String paintText = this.getText();
        Rectangle2D rf1d = null;
        if ((paintText == null) || (paintText.equals(""))) {
            return new Rectangle2D.Float((float) x, (float) y,
                    1, 1);
        }
        int nLines = getLineBreaks(fRC, paintFont, paintText, swkShape.width);
        String textLine = paintText;
        float width2 = 0;
        Point2D point2D = new Point2D.Double(x, y);
        Rectangle2D unionRect = new Rectangle2D.Double();
        AffineTransform shapeTransform = swkShape.getTransform();

        if (shapeTransform != null) {
            point2D = shapeTransform.transform(point2D, point2D);
        }
        x = (int) point2D.getX();
        y = (int) point2D.getY();
        for (int i = 0; i < nLines; i++) {
            if (nLines > 1) {
                textLine = paintText.substring(ends[i], ends[i + 1]).trim();
            }

            float width1 = (float) (paintFont.getStringBounds(textLine, fRC).getWidth());
            float height1 = (float) (paintFont.getStringBounds(textLine, fRC).getHeight());
            if (i == 0) {
                width2 = (float) (width1 * this.getAnchor()[1]);
            }

            float height2 = (float) (height1 * this.getAnchor()[0]);
            AffineTransform aT = new AffineTransform();

            aT.rotate(swkShape.rotate, x, y);

            Rectangle2D.Double rf2 = new Rectangle2D.Double((x
                    - width2), (y + height2), 0.0, 0.0);
            Rectangle2D rf2D = aT.createTransformedShape(rf2).getBounds2D();

            Rectangle2D.Double rf1 = new Rectangle2D.Double((float) (x
                    - width2), (float) (y - height1 + height2), width1, height1);
            rf1d = aT.createTransformedShape(rf1).getBounds2D();

            if (i == 0) {
                unionRect.setRect(rf1d);
            } else {
                Rectangle2D.union(rf1d, unionRect, unionRect);
            }

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
        return unionRect;
    }
}
