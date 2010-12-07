package com.onemoonscientific.swank;

import javax.swing.SwingUtilities;

public class GetValueOnEventThread implements Runnable {

    public void execOnThread() {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception iE) {
        }
    }

    public void run() {
    }
}
