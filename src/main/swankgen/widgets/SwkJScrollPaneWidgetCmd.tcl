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
   enum SCROLLPOLICY {
        ALWAYS(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS) {
        },
        ASNEEDED(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED) {
        },
        NEVER(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER) {
        },
        ;
        int hConstant=0;
        int vConstant=0;
        static String validOptions = "always, asneeded or never";
        SCROLLPOLICY(final int hConstant, final int vConstant) {
            this.hConstant=hConstant;
            this.vConstant = vConstant;
        }
        public static int findPolicy(final Interp interp,final String name, final boolean vertical) throws TclException {
            String upName = name.toUpperCase();
            SCROLLPOLICY foundPolicy = SCROLLPOLICY.valueOf(upName);
            if (foundPolicy == null) {
                int nFound = 0;
                for (SCROLLPOLICY policy:SCROLLPOLICY.values()) {
                    if (policy.toString().startsWith(upName)) {
                        foundPolicy = policy;
                        nFound++;
                    }
                }
                if (nFound == 0) {
                    throw new TclException(interp,"bad policy \"" + name + "\": must be " + validOptions);
                } else if (nFound > 1) {
                    throw new TclException(interp,"ambiguous policy \"" + name + "\": must be " + validOptions);
               }
            }
            if (vertical) {
                return foundPolicy.vConstant;
            } else {
                return foundPolicy.hConstant;
            }
        }
    }
}

append specialListeners {
}


append specialInits {
    
}



append specialMethods {
    public Dimension getPreferredSize() {
	final Dimension size = getMinimumSize();
	return size;
    }
   public Dimension getMinimumSize() {
        final Dimension size; 
	if (getViewport().getView() instanceof Scrollable) {
	    Scrollable scrollable = (Scrollable) getViewport().getView();
	    if (scrollable != null) {
		size = scrollable.getPreferredScrollableViewportSize();
                size.height += getHorizontalScrollBar().getSize().height;
                size.width += getVerticalScrollBar().getSize().width;
	    } else {
		size = new Dimension(swkwidth,swkheight);
	    }
	} else {
	    size = new Dimension(swkwidth,swkheight);
	}
	if (size.height < swkheight) {
            size.height = swkheight;
        }
	if (size.width < swkwidth) {
            size.width = swkwidth;
        }
       return size;
    }
    
}

