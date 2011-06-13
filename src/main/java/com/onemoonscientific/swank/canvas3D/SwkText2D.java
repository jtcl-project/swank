package com.onemoonscientific.swank.canvas3D;


import com.sun.j3d.utils.geometry.*;

import tcl.lang.*;



import javax.media.j3d.*;

import javax.vecmath.*;
import com.onemoonscientific.swank.*;
import java.util.Map;
import java.util.TreeMap;
import java.awt.Font;
import java.awt.Color;

public class SwkText2D extends SwkShape implements TextInterface {
    Point3d a = new Point3d();
    TextParameters textPar = new TextParameters();


    static TreeMap<String,CanvasParameter> parameterMap = new TreeMap<String,CanvasParameter>();
    static CanvasParameter[] parameters = {
        new TextParameter(), new AppearanceParameter(), new FontParameter(),
    };
    static {
        initializeParameters(parameters, parameterMap);
    }

    SwkText2D(SwkImageCanvas canvas) {
        super(canvas);
    }

   public void coords(SwkImageCanvas canvas, double[] coords)
            throws SwkException {
        a.x = coords[0];
        a.y = coords[1];
        a.z = coords[2];
        genShape();
    }
    void makeObjectNode() {
        //objectNode = new Text2D(textPar.getText(), textPar.getTextColor(), "SansSerif", 540, 0);

                Point3f rotationPoint = new Point3f(0.0f, 0.0f, 0.2f);
                Font font = textPar.getFont();
                Color3f color3f = new Color3f();
                swkAppearance.appearance.getMaterial().getDiffuseColor(color3f);
                Text2D text2D = new Text2D(textPar.getText(), color3f,textPar.getFont().getFamily(), textPar.getFont().getSize(), 0);
                OrientedShape3D orientedShape = new OrientedShape3D(
                        text2D.getGeometry(), text2D.getAppearance(),
                        OrientedShape3D.ROTATE_ABOUT_POINT, rotationPoint,true,8.0);
                objectNode = orientedShape;
    }
    NvTransformGroup makeTransform() {
        Transform3D t3D = new Transform3D();
        t3D.setTranslation(new Vector3d(a.x, a.y, a.z));
        NvTransformGroup tG = new NvTransformGroup(t3D);
        return tG;
    }
  /**
     *
     * @return
     */
    public String getText() {
        return textPar.getText();
    }

    /**
     *
     * @param newValue
     */
    public void setText(String newValue) {
        textPar = TextParameters.setText(textPar, newValue);
    }

    /**
     *
     * @return
     */
    public float[] getAnchor() {
        return textPar.getAnchor();
    }

    /**
     *
     * @param newValue
     */
    public void setAnchor(float[] newValue) {
        textPar = TextParameters.setAnchor(textPar, newValue);
    }

    /**
     *
     * @return
     */
    public Font getFont() {
        return textPar.getFont();
    }

    /**
     *
     * @param newValue
     */
    public void setFont(Font newValue) {
        textPar = TextParameters.setFont(textPar, newValue);
    }

    /**
     *
     * @return
     */
    public Color getTextColor() {
        return textPar.getTextColor();
    }

    /**
     *
     * @param newValue
     */
    public void setTextColor(Color newValue) {
        textPar = TextParameters.setTextColor(textPar, newValue);
    }
}
