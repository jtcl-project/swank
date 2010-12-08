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
}

append specialListeners {,PopupMenuListener
}


append specialInits {
addPopupMenuListener(this);
}
  

append specialMethods {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)  {
                            if ((postCommand != null) && (postCommand.length() > 0)) {
                               BindEvent bEvent = new BindEvent(interp,postCommand);
                               interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
			    }
                        }
	
public int getIndex(String sIndex,int endVal) throws TclException
    {
     int index=0;
     int nComp = getComponentCount();
     boolean validIndex = false;

     if (sIndex.equals("end")) {
                        index = nComp+endVal;
                        validIndex = true;
     }
     if (!validIndex) {
                throw new TclException(interp,"bad listbox index \""+sIndex+"\": must be active, anchor, end, @x,y, or a number");
     }

        return(index);
    }





		}
