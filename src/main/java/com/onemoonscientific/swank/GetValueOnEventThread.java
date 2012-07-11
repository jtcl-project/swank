package com.onemoonscientific.swank;
import tcl.lang.Interp;
import tcl.lang.TclException;
import javax.swing.SwingUtilities;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author brucejohnson
 */
public class GetValueOnEventThread implements Runnable {

    /**
     *
     */
   public static void runAndBlockSilently(final Runnable r) {
       final FutureTask ft = new FutureTask(r, null);
       boolean wasInterrupted = false;
       SwingUtilities.invokeLater(ft);
       while (! ft.isDone() ) {
           try {
               ft.get();
           }
           catch ( InterruptedException e ) {
               wasInterrupted = true;
            // Continue ...
           }
           catch(ExecutionException exEx) {
               Throwable cause = exEx.getCause();
// fixme should log
// fixme should throw error on interp
               System.err.println("Exception while blocking " + cause.getMessage());
               exEx.printStackTrace();
//               if (cause instanceof RuntimeException) {
 //                     throw (RuntimeException) cause;
  //             }
           }
        }

       if(wasInterrupted) {
           Thread.currentThread().interrupt();
       }
    }
   public static void runAndBlockSilently(final Interp interp, final Runnable r) throws TclException {
       final FutureTask ft = new FutureTask(r, null);
       boolean wasInterrupted = false;
       SwingUtilities.invokeLater(ft);
       while (! ft.isDone() ) {
           try {
               ft.get();
           }
           catch ( InterruptedException e ) {
               wasInterrupted = true;
            // Continue ...
           }
           catch(ExecutionException exEx) {
               Throwable cause = exEx.getCause();
               if (interp != null) {
                   throw new TclException(interp,cause.getMessage());
               }
// should log
               System.err.println("Exception while blocking " + cause.getMessage());
               if (cause instanceof RuntimeException) {
                      throw (RuntimeException) cause;
               }
           }
        }

       if(wasInterrupted) {
           Thread.currentThread().interrupt();
       }
    }

    public void execOnThread() {
        runAndBlockSilently(this);
    }
    public void execOnThread(final Interp interp) throws TclException {
        runAndBlockSilently(interp,this);
    }

    public void run() {
    }
}
