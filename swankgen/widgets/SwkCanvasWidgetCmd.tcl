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

}

append specialListeners {,Scrollable}


append specialInits {
        swkImageCanvas = new SwkImageCanvas(interp,name,className);
        swkImageCanvas.setComponent((Component) this);
        addMouseListener(new MouseListener() {
                public void mousePressed(MouseEvent mEvent) {
                    transformMouse(mEvent);
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
                    transformMouse(mEvent);
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
                    transformMouse(mEvent);
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
                    transformMouse(mEvent);
                }

                public void mouseExited(MouseEvent mEvent) {
                    transformMouse(mEvent);
                }
            });

        addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent mEvent) {
                    transformMouse(mEvent);
                    processMouseMotion(mEvent);
                }

                public void mouseMoved(MouseEvent mEvent) {
                    transformMouse(mEvent);
                    processMouseMotion(mEvent);
                }
            });

        setTransferHandler(new ImageSelection());

}


append specialVars {
FontRenderContext fRC = null;
int lastShapeId=0;
SwkShape currentShape = null;
Point currentPt = new Point(0,0);
String currentTag = null;
String previousTag = null;
TclObject currentTags[]=null;
TclObject previousTags[]=null;

Hashtable focusHash=null;
Hashtable mouseHash=null;
Hashtable mouseMotionHash = null;
Hashtable keyHash=null;
Hashtable tagHash= new Hashtable();
Vector tagVec = new Vector();

SwkImageCanvas swkImageCanvas = null;
}


append specialImports "\nimport java.awt.geom.*;
import java.awt.font.*;
import java.awt.datatransfer.*;
import com.onemoonscientific.swank.*;"
	
append specialMethods {
    public void paintComponent(Graphics g) {
        swkImageCanvas.setSize(getSize());
        swkImageCanvas.paintComponent(g);
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
        Dimension vdim;

        try {
            JViewport viewport = Widgets.getViewport(this);

            if (viewport != null) {
                vdim = viewport.getSize();

                if (swkwidth < vdim.width) {
                    dim.width = vdim.width;
                }

                if (swkheight < vdim.height) {
                    dim.height = vdim.height;
                }
            } else {
            }
        } catch (TclException tclE) {
        }

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
            currentShape = swkImageCanvas.getLastShapeScanned();
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

    public void processMouse(MouseEvent e, int type, int subtype) {
        BindEvent bEvent = new BindEvent(interp, this, (EventObject) e, type,
                subtype, currentTag, previousTag, currentShape);
        interp.getNotifier().queueEvent(bEvent, TCL.QUEUE_TAIL);
    }

    public void processEvent(EventObject eventObject, int type, int subtype,
        String currentTag, String previousTag, SwkShape eventCurrentShape) {
        MouseEvent e = (MouseEvent) eventObject;

        //   System.out.println("processE "+type+" "+subtype+" C "+currentTag+" P "+previousTag);       
        if (e.isConsumed()) {
            return;
        }

        swkImageCanvas.setEventCurrentShape(eventCurrentShape);

        // System.out.println("processE "+type+" "+subtype+" C "+currentTag+" P "+previousTag);
        Vector bindings = null;

        if (type == SwkBinding.FOCUS) {
            if ((currentTag != null) && (focusHash != null)) {
                bindings = (Vector) focusHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.MOUSE) {
            if (subtype == SwkBinding.EXIT) {
                if ((previousTag != null) && (mouseHash != null)) {
                    bindings = (Vector) mouseHash.get(previousTag);
                }
            } else {
                if ((currentTag != null) && (mouseHash != null)) {
                    bindings = (Vector) mouseHash.get(currentTag);
                }
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.MOUSEMOTION) {
            if ((currentTag != null) && (mouseMotionHash != null)) {
                bindings = (Vector) mouseMotionHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        } else if (type == SwkBinding.KEY) {
            if ((currentTag != null) && (keyHash != null)) {
                bindings = (Vector) keyHash.get(currentTag);
            }

            if (bindings == null) {
                return;
            }
        }

        SwkBinding binding;
        int buttons = e.getButton();
        int mods = e.getModifiersEx();

        //    System.out.println("event "+e);
        //    System.out.println("emods "+mods);
        int i;

        for (i = 0; i < bindings.size(); i++) {
            binding = (SwkBinding) bindings.elementAt(i);

            //  System.out.println(type+" "+subtype+" "+" "+binding.type+" "+binding.subtype);
            if (binding.subtype != subtype) {
                continue;
            }

            if ((subtype != SwkBinding.ENTER) && (subtype != SwkBinding.EXIT)) {
                if ((type == SwkBinding.MOUSE) && (e.getClickCount() > 0) &&
                        (binding.count != e.getClickCount())) {
                    continue;
                }

                //System.out.println(binding.detail+" "+binding.mod+" "+mods+" "+(binding.mod  & mods));
                // if ((binding.mod & buttonMask) != (mods & buttonMask)) {continue;}
                if (type == SwkBinding.MOUSEMOTION) {
                    if (!SwkMouseMotionListener.checkButtonState(e,
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

            if ((binding.command != null) && (binding.command.length() != 0)) {
                try {
                    BindCmd.doCmd(interp, binding.command, e);
                } catch (TclException tclE) {
                    if (tclE.getCompletionCode() == TCL.BREAK) {
                        e.consume();

                        return;
                    } else {
                        interp.addErrorInfo("\n    (\"binding\" script)");
                        interp.backgroundError();
                    }
                }
            }
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
    public void removeBindingsForTag(String tagName) {
        focusHash.remove(tagName);
        mouseHash.remove(tagName);
        mouseMotionHash.remove(tagName);
        keyHash.remove(tagName);
    }

   public void setupBinding(Interp interp, SwkBinding newBinding,
        String tagName) {
        Vector bindVec = null;

        if (newBinding.type == SwkBinding.FOCUS) {
            if (focusHash == null) {
                focusHash = new Hashtable();
                bindVec = new Vector(2);
                focusHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) focusHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    focusHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.MOUSE) {
            if (mouseHash == null) {
                mouseHash = new Hashtable();
                bindVec = new Vector(2);
                mouseHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) mouseHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    mouseHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.MOUSEMOTION) {
            if (mouseMotionHash == null) {
                mouseMotionHash = new Hashtable();
                bindVec = new Vector(2);
                mouseMotionHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) mouseMotionHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    mouseMotionHash.put(tagName, bindVec);
                }
            }
        } else if (newBinding.type == SwkBinding.KEY) {
            if (keyHash == null) {
                keyHash = new Hashtable();
                bindVec = new Vector(2);
                keyHash.put(tagName, bindVec);
            } else {
                bindVec = (Vector) keyHash.get(tagName);

                if (bindVec == null) {
                    bindVec = new Vector(2);
                    keyHash.put(tagName, bindVec);
                }
            }
        }

        if (bindVec != null) {
            if (!newBinding.add) {
                for (int i = 0; i < bindVec.size(); i++) {
                    SwkBinding binding = (SwkBinding) bindVec.elementAt(i);

                    if (binding.equals(newBinding)) {
                        bindVec.setElementAt(newBinding, i);

                        return;
                    }
                }
            }

            bindVec.addElement(newBinding);
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

    public void transformMouse(MouseEvent mEvent) {
        double x = mEvent.getX();
        double y = mEvent.getY();
        origMouse.setLocation(x, y);
        transMouse.setLocation(x, y);

        try {
            transMouse = canvasTransform.inverseTransform(origMouse, transMouse);
        } catch (java.awt.geom.NoninvertibleTransformException ntE) {
        }

        mEvent.translatePoint((int) (transMouse.getX() - x),
            (int) (transMouse.getY() - y));
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

