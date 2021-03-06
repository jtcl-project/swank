package com.onemoonscientific.swank.canvas3D;

import java.awt.Color;
import java.awt.Font;

interface TextInterface {

    void setText(String text);

    String getText();

    void setFont(Font font);

    Font getFont();

    void setTextColor(Color color);

    Color getTextColor();

    float[] getAnchor();

    void setAnchor(final float[] anchor);
}


