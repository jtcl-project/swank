#
# 
# Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, NJ, USA
#
# See the file \"LICENSE\" for information on usage and redistribution
# of this file.
# IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
# ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
# CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
# SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
# EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#
# THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
# PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
# IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
# DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
# SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
#
#

append specialVars {
 JViewport jview=null;
  JScrollPane jscroll=null;
  JComponent packComponent=null;
SwkJEditorPane html;
    String loadScript = "::swank::htmlViewer::load";
    String hyperlinkScript = "::swank::htmlViewer::hyperlink";

}
append specialImports {
import java.io.IOException;
import java.net.URL;
}
append specialListeners { ,HyperlinkListener }


append specialInits {
    if (className.equals("Html")) {
    	jview = new JViewport();
    	packComponent = jview;
    	jview.setView(this);
    } else if (className.equals("Shtml")) {
    	jscroll = new JScrollPane(this);
    	jview = jscroll.getViewport();
    	packComponent = jscroll;
    } else {
    	packComponent = this;
    }
       StyledEditorKit htmlEdKit = new StyledEditorKit();
    this.setEditorKit( htmlEdKit );
    addHyperlinkListener(this);
    html = this;
      
}



append specialMethods {
    public void setPage2(URL url) {
        try {
            super.setPage(url);
         }
        catch(IOException ioE) {
        }
   }

   public void setPage(URL url) {
       if (url != null) {
            Cursor c = html.getCursor();
            Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            html.setCursor(waitCursor);
            SwingUtilities.invokeLater(new PageLoader(url, c));
        }
    }
    
    
    public void hyperlinkUpdate(HyperlinkEvent hEvent) {
        URL url = hEvent.getURL();
        String fileName = url.getFile().toString();
        HyperlinkEvent.EventType eventType = hEvent.getEventType();
        if (hyperlinkScript.length() != 0) {
            String cmd = null;
                if (eventType == HyperlinkEvent.EventType.ENTERED) {
                    cmd = hyperlinkScript+" entered "+html.getName()+" {"+url.toString()+"}";
                } else if (eventType == HyperlinkEvent.EventType.EXITED) {
                    cmd = hyperlinkScript+" exited "+html.getName()+" {"+url.toString()+"}";
                } else if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    cmd = hyperlinkScript+" activated "+html.getName()+" {"+url.toString()+"}";
                } 
                BindEvent bEvent = new BindEvent(interp,cmd);
                interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
        } else {
            if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
            if (url.getRef() == null) {
                  linkActivated(url);
            } else {
            if ((url.getRef().length() != 0) &&
            ((fileName.length() == 0) || (fileName.charAt(fileName.length()-1) == '/') || getPage().sameFile(url))) {
                scrollToReference(url.getRef());
            } else {
                linkActivated(url);
            }
            }
            }
            
        }
    }
   
    protected void linkActivated(URL u) {
        Cursor c = html.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        html.setCursor(waitCursor);
        SwingUtilities.invokeLater(new PageLoader(u, c));
    }
    
    /**
     * temporary class that loads synchronously (although
     * later than the request so that a cursor change
     * can be done).
     */
    class PageLoader implements Runnable {
        
        PageLoader(URL u, Cursor c) {
            url = u;
            cursor = c;
            loading=true;
        }
        
        public void run() {
            if (loading == false) {
                // restore the original cursor
                html.setCursor(cursor);
                
                // PENDING(prinz) remove this hack when
                // automatic validation is activated.
                Container parent = html.getParent();
                parent.repaint();
                    if (loadScript.length() != 0) {
                         BindEvent bEvent = new BindEvent(interp,loadScript+" loaded "+html.getName()+" {"+url.toString()+"}");
                         interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
                    }
           } else {
                Document doc = html.getDocument();
                try {
                        if (loadScript.length() != 0) {
                         BindEvent bEvent = new BindEvent(interp,loadScript+" loading "+html.getName()+" {"+url.toString()+"}");
                         interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
                        }
                     html.setPage2(url);
               } finally {
                    // schedule the cursor to revert after
                    // the paint has happended.
                    loading = false;
                    SwingUtilities.invokeLater(this);
                }
            }
        }
        
        URL url;
        Cursor cursor;
        boolean loading;
    }
    
    }
