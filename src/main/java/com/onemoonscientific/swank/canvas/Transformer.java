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

    public Transformer(String name) {
        this.name = name.intern();
        valid = true;
    }

    public AffineTransform getTransform() {
        return aT;
    }

    public void setTransform(AffineTransform newTrans) {
        aT.setTransform(newTrans);
    }

    public void setToTransform(AffineTransform newTrans) {
        aT = newTrans;
    }

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }
}
