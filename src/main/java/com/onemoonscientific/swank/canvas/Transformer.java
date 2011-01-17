/*
 * Transformer.java
 *
 * Created on December 23, 2004, 3:35 PM
 */
package com.onemoonscientific.swank.canvas;

import java.awt.geom.AffineTransform;

/**
 *
 * @author brucejohnson
 */
public class Transformer {

    /** Creates a new instance of Transformer */
    private AffineTransform aT = new AffineTransform();
    private boolean valid = false;
    private String name = null;

    /**
     *
     * @param name
     */
    public Transformer(String name) {
        this.name = name.intern();
        valid = true;
    }

    /**
     *
     * @return
     */
    public AffineTransform getTransform() {
        return aT;
    }

    /**
     *
     * @param newTrans
     */
    public void setTransform(AffineTransform newTrans) {
        aT.setTransform(newTrans);
    }

    /**
     *
     * @param newTrans
     */
    public void setToTransform(AffineTransform newTrans) {
        aT = newTrans;
    }

    /**
     *
     * @return
     */
    public boolean isValid() {
        return valid;
    }

    /**
     *
     * @return
     */
    public String getName() {
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }
}
