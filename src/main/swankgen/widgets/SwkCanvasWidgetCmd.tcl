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
	Graphics g1=null;
    BasicStroke stroke = new BasicStroke();
   double zoom = 1.0;
    AffineTransform canvasTransform = new AffineTransform();
    Hashtable transformerHash = new Hashtable();
    Point2D transMouse = new Point2D.Double();
    Point2D origMouse = new Point2D.Double();
    static ScheduledThreadPoolExecutor schedExecutor = new ScheduledThreadPoolExecutor(10);
    ScheduledFuture futureUpdate = null;
    class UpdateTask implements Runnable {

         public void run() {
                   repaint();
         }
    }

 synchronized void startTimer(final int delay) {
        if ((futureUpdate == null) || futureUpdate.isDone()) {
            UpdateTask updateTask = new UpdateTask();
            futureUpdate = schedExecutor.schedule(updateTask, delay, TimeUnit.MILLISECONDS);
        }

    }

}

append specialListeners {,Scrollable}
set specialPrints {
             swkImageCanvas.setSize(getSize());
             swkImageCanvas.paintComponent(g,null);

}

append specialInits {
        swkImageCanvas = new SwkImageCanvas(interp,name);
        swkImageCanvas.setComponent((Component) this);
        addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {

           }

            public void keyPressed(KeyEvent e) {
                Point point = MouseInfo.getPointerInfo().getLocation();
                point = swkImageCanvas.transformPosition(point.x, point.y);
                currentTags = getTagFromKeyEvent(point.x, point.y);
                if (currentTags == null) {
                    return;
                }

                for (int i = 0; i < currentTags.length; i++) {

                    currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                    processKey(e, SwkBinding.PRESS);
                }
            }

            public void keyReleased(KeyEvent e) {
                Point point = MouseInfo.getPointerInfo().getLocation();
                point = swkImageCanvas.transformPosition(point.x, point.y);
                currentTags = getTagFromKeyEvent(point.x, point.y);
                if (currentTags == null) {
                    return;
                }

                for (int i = 0; i < currentTags.length; i++) {
                    currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                    processKey(e, SwkBinding.RELEASE);
                }
            }
        });
        addMouseListener(new MouseListener() {
                public void mousePressed(MouseEvent mEvent) {
                    swkImageCanvas.transformMouse(mEvent);
                    currentTags = getTagFromEvent(mEvent);

                    if (currentTags == null) {
                        return;
                    }

                    for (int i = 0; i < currentTags.length; i++) {
                        currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                        processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.PRESS);
                    }
                }

                public void mouseReleased(MouseEvent mEvent) {
                    swkImageCanvas.transformMouse(mEvent);
                    previousTags = currentTags;

                    /*
                     if (previousTags != null) {
                         for (int i=0;i<previousTags.length;i++) {
                             System.out.println("prev "+previousTags[i].toString());
                         }
                     }
                     **/
                    currentTags = getTagFromEvent(mEvent);

                    if (currentTags != null) {
                        // for (int i=0;i<currentTags.length;i++) {
                        //   System.out.println("current "+currentTags[i].toString());
                        //}
                    } else {
                        currentTag = null;
                        swkImageCanvas.setEventCurrentShape(null);
                    }

                    checkForMouseExit(mEvent);

                    if (currentTags == null) {
                        return;
                    }

                    for (int i = 0; i < currentTags.length; i++) {
                        currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                        processMouse(mEvent, SwkBinding.MOUSE,
                            SwkBinding.RELEASE);
                    }
                }

                public void mouseClicked(MouseEvent mEvent) {
                    swkImageCanvas.transformMouse(mEvent);
                    currentTags = getTagFromEvent(mEvent);

                    if (currentTags == null) {
                        return;
                    }

                    for (int i = 0; i < currentTags.length; i++) {
                        currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                        processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.CLICK);
                    }
                }

                public void mouseEntered(MouseEvent mEvent) {
                    swkImageCanvas.transformMouse(mEvent);
                }

                public void mouseExited(MouseEvent mEvent) {
                    swkImageCanvas.transformMouse(mEvent);
                }
            });

        addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent mEvent) {
                    swkImageCanvas.transformMouse(mEvent);
                    processMouseMotion(mEvent);
                }

                public void mouseMoved(MouseEvent mEvent) {
                    swkImageCanvas.transformMouse(mEvent);
                    processMouseMotion(mEvent);
                }
            });

        setTransferHandler(new ImageSelection());

}


append specialVars {
FontRenderContext fRC = null;
int lastShapeId=0;
HitShape hitShape = null;
int handle = -1;
SwkShape currentShape = null;
Point currentPt = new Point(0,0);
String currentTag = null;
String previousTag = null;
TclObject currentTags[]=null;
TclObject previousTags[]=null;

LinkedHashMap focusHash=null;
LinkedHashMap mouseHash=null;
LinkedHashMap mouseMotionHash = null;
LinkedHashMap keyHash=null;
Hashtable tagHash= new Hashtable();
Vector tagVec = new Vector();

SwkImageCanvas swkImageCanvas = null;
       BufferedImage bufOffscreen=null;
        Graphics2D g2Offscreen = null;
boolean changed = false;
Cursor previousCursor = null;

}


append specialImports "\nimport java.awt.geom.*;
import java.awt.font.*;
import java.awt.datatransfer.*;
import com.onemoonscientific.swank.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
"
	
append specialMethods {
    public void paintComponent(Graphics g){
               Graphics2D g2 = (Graphics2D) g;     

        fRC = g2.getFontRenderContext();
        Dimension dimSize = getSize();
        int w = dimSize.width;
        int h = dimSize.height;

        if ((w <= 0) || (h <= 0)) {
            return;
        }
        swkImageCanvas.setSize(getSize());
        swkImageCanvas.paintComponent(g,bufOffscreen);
        changed=false;
    }
    public BufferStrategy getBufferStrategy() {
        BufferStrategy bufferStrategy = null;
        Component comp = Widgets.getFrameOrWindow(getParent());
        if (comp instanceof Window) {
            bufferStrategy = ((Window) comp).getBufferStrategy();   
        }
        return bufferStrategy;
    }

    public SwkImageCanvas getSwkImageCanvas() {
        return swkImageCanvas;
    }
    public void setZoom(double newZoom) {
        zoom = newZoom;
    }

    public double getZoom() {
        return zoom;
    }

    public Dimension getPreferredScrollableViewportSize() {
        Dimension dim = new Dimension(swkwidth, swkheight);
        return (dim);
    }

    public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect,
        int direction, int orientation) {
        return (1);
    }

    public boolean getScrollableTracksViewportWidth() {
        return (false);
    }

    public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect,
        int direction, int orientation) {
        return (1);
    }

    public boolean getScrollableTracksViewportHeight() {
        return (false);
    }
    public void setHandleCursor(Cursor cursor) {
         if (previousCursor == null) {
             previousCursor = getCursor();
         }
         super.setCursor(cursor);
    }
    public void setCursor(Cursor cursor) {
       previousCursor = cursor;
       super.setCursor(cursor);
    }
    void processMouseMotion(MouseEvent mEvent) {
        previousTags = currentTags;

        /*
         if (previousTags != null) {
             for (int i=0;i<previousTags.length;i++) {
                 System.out.println("prev "+previousTags[i].toString());
             }
         }
         */
        int buttonMask = (InputEvent.BUTTON1_MASK + InputEvent.BUTTON2_MASK +
            InputEvent.BUTTON3_MASK);
        int mods = mEvent.getModifiers();

        if ((mods & buttonMask) == 0) {
            currentTags = getTagFromEvent(mEvent);

            /*
             if (currentTags != null) {
                 for (int i=0;i<currentTags.length;i++) {
                     System.out.println("current "+currentTags[i].toString());
                 }
             }
             **/
            checkForMouseExit(mEvent);
            hitShape = new HitShape(swkImageCanvas.getLastShapeScanned(), swkImageCanvas.getHandle());
            if ((hitShape != null) && (hitShape.handle >= 0) && (hitShape.swkShape != null)) {
                      setHandleCursor(hitShape.swkShape.getHandleCursor(hitShape.handle));
            } else {
                 if (previousCursor != null) {
                     setHandleCursor(previousCursor);
                 }
            }
            checkForMouseEnter(mEvent);
        }

        if (currentTags != null) {
            for (int i = 0; i < currentTags.length; i++) {
                currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                processMouse(mEvent, SwkBinding.MOUSEMOTION, SwkBinding.MOTION);
            }
        }
    }

    void checkForMouseExit(MouseEvent mEvent) {
        boolean stillPresent = true;

        if ((currentTags == null) && (previousTags != null)) {
            for (int i = 0; i < previousTags.length; i++) {
                previousTag = getTagOrIDFromTagID(previousTags[i].toString());
                processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.EXIT);
            }
        } else if ((currentTags != null) && (previousTags != null)) {
            for (int i = 0; i < previousTags.length; i++) {
                previousTag = previousTags[i].toString();
                stillPresent = false;

                for (int j = 0; j < currentTags.length; j++) {
                    String thisTag = currentTags[j].toString();

                    if (previousTags[i].toString().equals(thisTag)) {
                        stillPresent = true;

                        break;
                    }
                }

                if (!stillPresent) {
                    previousTag = getTagOrIDFromTagID(previousTag);
                    processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.EXIT);
                }
            }
        }
    }

    void checkForMouseEnter(MouseEvent mEvent) {
        boolean wasPresent = true;

        if ((currentTags != null) && (previousTags == null)) {
            for (int i = 0; i < currentTags.length; i++) {
                currentTag = getTagOrIDFromTagID(currentTags[i].toString());
                processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.ENTER);
            }
        } else if ((currentTags != null) && (previousTags != null)) {
            for (int i = 0; i < currentTags.length; i++) {
                String thisTag = currentTags[i].toString();
                wasPresent = false;

                for (int j = 0; j < previousTags.length; j++) {
                    if (previousTags[j].toString().equals(thisTag)) {
                        wasPresent = true;

                        break;
                    }
                }

                if (!wasPresent) {
                    currentTag = getTagOrIDFromTagID(thisTag);
                    processMouse(mEvent, SwkBinding.MOUSE, SwkBinding.ENTER);
                }
            }
        }
    }

  public void processKey(KeyEvent e, int subtype) {
        BindEvent bEvent = new BindEvent(interp, this, (EventObject) e,SwkBinding.KEY,
                subtype, currentTag, previousTag, hitShape);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);

        //bEvent.sync();
        //processEvent(e,subtype);
    }

  
    public void processMouse(MouseEvent e, int type, int subtype) {
        BindEvent bEvent = new BindEvent(interp, this, (EventObject) e, type,
                subtype, currentTag, previousTag, hitShape);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, int type, int subtype,
        String currentTag, String previousTag, HitShape eventCurrentShape) {
        if (eventObject instanceof KeyEvent) {
            processKeyEvent((KeyEvent) eventObject,type,subtype,currentTag,previousTag,eventCurrentShape);
        } else {
             processMouseEvent(eventObject,type,subtype,currentTag,previousTag,eventCurrentShape);

        }
    }

    public void processKeyEvent(KeyEvent e, int type, int subtype,
            String currentTag, String previousTag, HitShape eventCurrentShape) {

        //System.out.println("key event "+e.toString()); 
        SwkBinding binding;
        int buttonMask;
        boolean debug = false;
        int mods = e.getModifiersEx();
        char keyChar = e.getKeyChar();

        if (Character.isISOControl(keyChar)) {
            keyChar = (char) (keyChar + 96);
        }

        int keyCode = e.getKeyCode();

        if (debug) {
            if (keyChar == KeyEvent.CHAR_UNDEFINED) {
                //System.out.println(keyCode+" undef "+subtype);
            } else {
                //System.out.println(keyCode+" "+keyChar+" "+subtype);
            }
        }

        if (Character.isISOControl(keyChar)) {
            keyChar = (char) (keyChar + 64);
        }

        KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
        boolean nativeProcessEvent = true;
        boolean breakOut = false;

       if (e.isConsumed()) {
            return;
        }

        swkImageCanvas.setEventCurrentShape(eventCurrentShape);

        // System.out.println("processE "+type+" "+subtype+" C "+currentTag+" P "+previousTag);

        ArrayList<SwkBinding> bindings = getBindings(currentTag,type, subtype);

        if (bindings == null) {
            return;
        }

        boolean consumeNextType = true;
        if (subtype == SwkBinding.PRESS) {
            consumeNextType = false;
        }


        //    System.out.println("event "+e);
        //    System.out.println("emods "+mods);
        int i;
        SwkBinding lastBinding = null;
        for (i = 0; i < bindings.size(); i++) {
            binding = (SwkBinding) bindings.get(i);

            //System.out.println("binding is "+binding.toString()+" "+binding.subtype+" "+subtype);
            if (binding.subtype != subtype) {
                continue;
            }

                if (!((binding.subtype == SwkBinding.PRESS) &&
                        (binding.detail == 0))) {
                    if (!((binding.subtype == SwkBinding.RELEASE) &&
                            (binding.detail == 0))) {
                        if (!((binding.subtype == SwkBinding.TYPE) &&
                                (binding.detail == 0))) {
                            //System.out.println("event mods "+mods+" binding mods "+binding.mod);
                            if (binding.keyStroke == null) {
                                //System.out.println("chars "+(keyChar+0)+" "+binding.detail);
                                if (binding.detail != keyChar) {
                                    continue;
                                }

                                if (binding.mod != mods) {
                                    if ((binding.mod |
                                            InputEvent.SHIFT_DOWN_MASK) != mods) {
                                        continue;
                                    }
                                }
                            } else {
                                //System.out.println(binding.detail+" <<>> "+keyCode);
                                if (binding.detail != keyCode) {
                                    //System.out.println("keyCodes not equal");
                                    continue;
                                }

                                if (binding.mod != mods) {
                                    continue;
                                }
                            }
                        }

                        // second accounts for possibility of Caps-lock on
                        // if matched above at detail == keyChar then the case was
                        // right
                    }
                }

                if ((binding.command != null) &&
                        (binding.command.length() != 0)) {
                    try {
                        BindCmd.doCmd(interp, binding.command, e,eventCurrentShape);
                    } catch (TclException tclE) {
                        if (tclE.getCompletionCode() == TCL.BREAK) {
                            nativeProcessEvent = false;

                            //System.out.println("break");
                            e.consume();

                            if (subtype == SwkBinding.PRESS) {
                                //System.out.println("consume next");
                                consumeNextType = true;
                            }

                            breakOut = true;

                            break;
                        } else {
                            interp.addErrorInfo("\n    (\"binding\" script)");
                            interp.backgroundError();
                        }
                    }
                }

            if (breakOut) {
                break;
            }
            }
 }
ArrayList<SwkBinding> getBindings(String checkTag, int type, int subtype) {
           ArrayList<SwkBinding> bindings = null;
        if (type == SwkBinding.FOCUS) {
            if ((checkTag != null) && (focusHash != null)) {
                bindings = (ArrayList<SwkBinding>) focusHash.get(checkTag);
            }
        } else if (type == SwkBinding.MOUSE) {
            if (subtype == SwkBinding.EXIT) {
                if ((checkTag != null) && (mouseHash != null)) {
                    bindings = (ArrayList<SwkBinding>) mouseHash.get(checkTag);
                }
            } else {
                if ((checkTag != null) && (mouseHash != null)) {
                    bindings = (ArrayList<SwkBinding>) mouseHash.get(checkTag);
                }
            }

         } else if (type == SwkBinding.MOUSEMOTION) {
            if ((checkTag != null) && (mouseMotionHash != null)) {
                bindings = (ArrayList<SwkBinding>) mouseMotionHash.get(checkTag);
            }

         } else if (type == SwkBinding.KEY) {
            if ((checkTag != null) && (keyHash != null)) {
                bindings = (ArrayList<SwkBinding>) keyHash.get(checkTag);
            }
       }
        return bindings;

}
    public void processMouseEvent(EventObject eventObject, int type, int subtype,
        String currentTag, String previousTag, HitShape eventCurrentShape) {

        //   System.out.println("processE "+type+" "+subtype+" C "+currentTag+" P "+previousTag);       
        InputEvent iE = (InputEvent) eventObject;
        if (iE.isConsumed()) {
            return;
        }

        swkImageCanvas.setEventCurrentShape(eventCurrentShape);

        // System.out.println("processE "+type+" "+subtype+" C "+currentTag+" P "+previousTag);

        ArrayList<SwkBinding> bindings = null;
        if (subtype == SwkBinding.EXIT) {
               bindings = getBindings(previousTag,type,subtype);
        } else {
               bindings = getBindings(currentTag,type,subtype);
        }

            if (bindings == null) {
                return;
            }

        SwkBinding binding;
        MouseEvent mE = null;
        int mods = iE.getModifiersEx();
        int buttons = 0;
        if (iE instanceof MouseEvent) {
            mE = (MouseEvent) iE;
            buttons = mE.getButton();
        }


        //    System.out.println("event "+e);
        //    System.out.println("emods "+mods);
        int i;
        SwkBinding lastBinding = null;
        for (i = 0; i < bindings.size(); i++) {
            binding =  bindings.get(i);

            //  System.out.println(type+" "+subtype+" "+" "+binding.type+" "+binding.subtype);
            if (binding.subtype != subtype) {
                continue;
            }

            if ((subtype != SwkBinding.ENTER) && (subtype != SwkBinding.EXIT)) {
                if ((type == SwkBinding.MOUSE) && (mE.getClickCount() > 0) &&
                        (binding.count > mE.getClickCount())) {
                    continue;
                }

                //System.out.println(binding.detail+" "+binding.mod+" "+mods+" "+(binding.mod  & mods));
                // if ((binding.mod & buttonMask) != (mods & buttonMask)) {continue;}
                if (type == SwkBinding.MOUSEMOTION) {
                    if (!SwkMouseMotionListener.checkButtonState(mE,
                                binding.mod, mods)) {
                        continue;
                    }
                } else if (type == SwkBinding.MOUSE) {
                    if (!SwkMouseMotionListener.checkButtons(binding.detail,
                                buttons)) {
                        continue;
                    }
                   

                }

                //  System.out.println("check mods");
                if (!SwkMouseMotionListener.checkMods(binding.mod, mods)) {
                    continue;
                }
            }
            if (binding.sameButClick(lastBinding)) {
                continue;
            }


            if ((binding.command != null) && (binding.command.length() != 0)) {
                try {
                    BindCmd.doCmd(interp, binding.command, iE,eventCurrentShape);
                } catch (TclException tclE) {
                    if (tclE.getCompletionCode() == TCL.BREAK) {
                        iE.consume();

                        return;
                    } else {
                        interp.addErrorInfo("\n    (\"binding\" script)");
                        interp.backgroundError();
                    }
                }
            }
            lastBinding = binding;
        }
    }
    String getTagOrIDFromTagID(String tagID) {
        int spacePos = tagID.indexOf(" ");

        if (spacePos == -1) {
            return tagID;
        } else {
            return tagID.substring(0, spacePos);
        }
    }

    public TclObject[] getTagFromEvent(MouseEvent mEvent) {
        currentTag = null;

        return (swkImageCanvas.scanCanvasForTags((double) mEvent.getX(), (double) mEvent.getY()));
    }
    public TclObject[] getTagFromKeyEvent(int x, int y) {
        currentTag = null;
        return (swkImageCanvas.scanCanvasForTags((double) x, (double) y));
    }
    public void removeBindingsForTag(String tagName) {
        focusHash.remove(tagName);
        mouseHash.remove(tagName);
        mouseMotionHash.remove(tagName);
        keyHash.remove(tagName);
    }

   public void setupBinding(Interp interp, SwkBinding newBinding,
        String tagName) {
        ArrayList<SwkBinding> bindVec = null;

        if (newBinding.type == SwkBinding.FOCUS) {
            if (focusHash == null) {
                focusHash = new LinkedHashMap();
                bindVec = new ArrayList<SwkBinding>(2);
                focusHash.put(tagName, bindVec);
            } else {
                bindVec = (ArrayList<SwkBinding>) focusHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new ArrayList<SwkBinding>(2);
                    focusHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.MOUSE) {
            if (mouseHash == null) {
                mouseHash = new LinkedHashMap();
                bindVec = new ArrayList<SwkBinding>(2);
                mouseHash.put(tagName, bindVec);
            } else {
                bindVec = (ArrayList<SwkBinding>) mouseHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new ArrayList<SwkBinding>(2);
                    mouseHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.MOUSEMOTION) {
            if (mouseMotionHash == null) {
                mouseMotionHash = new LinkedHashMap();
                bindVec = new ArrayList<SwkBinding>(2);
                mouseMotionHash.put(tagName, bindVec);
            } else {
                bindVec = (ArrayList<SwkBinding>) mouseMotionHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new ArrayList<SwkBinding>(2);
                    mouseMotionHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.KEY) {
            if (keyHash == null) {
                keyHash = new LinkedHashMap();
                bindVec = new ArrayList<SwkBinding>(2);
                keyHash.put(tagName, bindVec);
            } else {
                bindVec = (ArrayList<SwkBinding>) keyHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new ArrayList<SwkBinding>(2);
                    keyHash.put(tagName, bindVec);
                }
            }
        }

        if (bindVec != null) {
            if (!newBinding.add) {
                for (int i = 0; i < bindVec.size(); i++) {
                    SwkBinding binding =  bindVec.get(i);

                    if (binding.equals(newBinding)) {
                        bindVec.add(i,newBinding);

                        return;
                    }
                }
            }

            bindVec.add(newBinding);
        }
    }



    public void drawBox(int x1, int y1, int width, int height) {
        Graphics g = getGraphics();

        g.setXORMode(getBackground());
        g.drawRect(x1, y1, width, height);
        g.dispose();
    }

    public void copyImageToClipboard(Clipboard clipboard) {
        TransferHandler handler = getTransferHandler();
        handler.exportToClipboard(this, clipboard, TransferHandler.COPY);
    }

    public Image paintImage() {
        Dimension d = getSize();
        Image offscreen = createImage(d.width, d.height);
        Graphics2D offgraphics = (Graphics2D) offscreen.getGraphics();
        boolean wasBuffered = SwankUtil.disableDoubleBuffering(this);
        paint(offgraphics);
        offgraphics.dispose();
        SwankUtil.restoreDoubleBuffering(this, wasBuffered);

        return (offscreen);
    }


    FontRenderContext getFontRenderContext() {
        return fRC;
    }

    class Tag {
        int id = -1;
        String name = null;
        Hashtable tagShapes = new Hashtable();

        Tag(String name) {
            this.name = name.intern();
            tagVec.addElement(this);
            id = tagVec.size() - 1;
        }
    }

}

