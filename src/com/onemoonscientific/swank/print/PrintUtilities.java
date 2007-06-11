package com.onemoonscientific.swank.print;
import java.awt.*;
import javax.swing.*;
import java.awt.print.*;
/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */

public class PrintUtilities implements Printable {
  private Component componentToBePrinted;
  private Paper paper = null;
  private int orientation = 0;
  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }
  public static void printComponent(Component c, Paper paper, int orientation) {
    new PrintUtilities(c,paper,orientation).print();
  }
  
  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }
  public PrintUtilities(Component componentToBePrinted, Paper paper, int orientation) {
    this.componentToBePrinted = componentToBePrinted;
    this.paper = paper;
    this.orientation = orientation;
  }
  
  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    if (paper != null) {
        PageFormat pf = printJob.defaultPage();
        pf.setPaper(paper);
        pf.setOrientation(orientation);
        printJob.setPrintable(this,pf);
    } else {
        printJob.setPrintable(this);
    }
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
   int response = NO_SUCH_PAGE;
    Graphics2D g2 = (Graphics2D) g;

    //  for faster printing, turn off double buffering
    disableDoubleBuffering(componentToBePrinted);

    Dimension d = componentToBePrinted.getSize(); //get size of document
    double panelWidth = d.width; //width in pixels
    double panelHeight = d.height; //height in pixels

    double pageHeight = pageFormat.getImageableHeight(); //height of printer page
    double pageWidth = pageFormat.getImageableWidth(); //width of printer page

    double scale = pageWidth / panelWidth;

    if (panelWidth > pageWidth) {
        scale = pageWidth/panelWidth;
    }
    if ((panelHeight*scale) > pageHeight) {
        scale = scale*pageHeight/panelHeight;
    }
 
    int totalNumPages = (int) Math.ceil(scale * panelHeight / pageHeight);

    //  make sure not print empty pages
    if (pageIndex >= totalNumPages) {
      response = NO_SUCH_PAGE;
    }
    else {
      //  shift Graphic to line up with beginning of print-imageable region
      g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      //  shift Graphic to line up with beginning of next page to print
      g2.translate(0f, -pageIndex * pageHeight);

      //  scale the page so the width fits...
      g2.scale(scale, scale);
      componentToBePrinted.paint(g2); //repaint the page for printing
      enableDoubleBuffering(componentToBePrinted);
      response = Printable.PAGE_EXISTS;
    }
    return response;
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}

