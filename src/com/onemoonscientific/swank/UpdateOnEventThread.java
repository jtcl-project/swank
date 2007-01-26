package com.onemoonscientific.swank;

import javax.swing.SwingUtilities;


public class UpdateOnEventThread implements Runnable {
    public void execOnThread() {
        try {
            SwingUtilities.invokeLater(this);
        } catch (Exception iE) {
        }
    }

    public void run() {
    }
}
