package com.onemoonscientific.swank.canvas;

import java.awt.Color;
import java.awt.Font;

interface TextInterface {

    void setText(String text);

    String getText();

    void setFont(Font font);

    Font getFont();

    void setTextColor(Color color);

    Color getTextColor();

    public float[] getAnchor();

    public void setAnchor(final float[] anchor);
}


