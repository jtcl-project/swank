package com.onemoonscientific.swank;

import javax.swing.SwingUtilities;

/**
 *
 * @author brucejohnson
 */
public class GetValueOnEventThread implements Runnable {

    /**
     *
     */
    public void execOnThread() {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception iE) {
        }
    }

    public void run() {
    }
}
