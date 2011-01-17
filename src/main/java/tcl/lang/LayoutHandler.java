package tcl.lang;

import com.onemoonscientific.swank.Widgets;

import java.awt.Component;
import java.awt.Container;

import java.util.HashSet;
import java.util.Iterator;

import javax.swing.*;

public class LayoutHandler extends IdleHandler {

    private static HashSet repaintComponents = new HashSet();
    private static HashSet layoutContainers = new HashSet();

    public LayoutHandler(Notifier n) {
        super(n);
    }

    public void processIdleEvent() {
        doRepaints();
    }

    synchronized public static void addRepaintRequest(Interp interp,
            JComponent jcomp) {
        if (jcomp != null) {
            repaintComponents.add(jcomp);

            Notifier notifier = interp.getNotifier();
            LayoutHandler layoutHandler = new LayoutHandler(notifier);
        }
    }

    /**
     *
     * @param interp
     * @param container
     */
    public static void addLayoutRequest(Interp interp, Container container) {
        if (container != null) {
            layoutContainers.add(container);

            Notifier notifier = interp.getNotifier();
            LayoutHandler layoutHandler = new LayoutHandler(notifier);
        }
    }

    public static synchronized void doRepaints() {
        try {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    doRepaintsNow();
                }
            });
        } catch (Exception e) {
        }
    }

    public static synchronized void doRepaintsNow() {
        Iterator iter = repaintComponents.iterator();

        while (iter.hasNext()) {
            ((JComponent) iter.next()).repaint();
        }

        repaintComponents.clear();

        HashSet layoutContainers1 = new HashSet();

        iter = layoutContainers.iterator();

        while (iter.hasNext()) {
            Container container = (Container) iter.next();
            Component comp = Widgets.getFrameOrWindow(container);
            layoutContainers1.add((Container) comp);
        }

        iter = layoutContainers1.iterator();

        while (iter.hasNext()) {
            Widgets.relayoutContainer(((Container) iter.next()));
        }

        layoutContainers.clear();
        layoutContainers1.clear();
    }
}
