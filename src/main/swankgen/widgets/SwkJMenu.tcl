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

append specialListeners {,MenuListener
}


append specialInits {
addMenuListener(this);
}
  


append specialMethods {
			public void menuCanceled(MenuEvent e) {
			}
			public void menuDeselected(MenuEvent e) {
			}
   public void menuSelected(MenuEvent e)  {
        if ((postCommand != null) && (postCommand.length() > 0)) {
            BindEvent bEvent = new BindEvent(interp,postCommand);
            interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
        }
   }
        public int getIndex( String sIndex, int endVal) {
        int index = -1;
        int nComp = getMenuComponentCount();

            if ((sIndex.equals("end")) || (sIndex.equals("last"))) {
                index = nComp + endVal;
            } else if (sIndex.equals("active")) {
                index = getPopupMenu().getSelectionModel().getSelectedIndex();
            } else if (sIndex.equals("none")) {
                index = -1;
            } else {
                for (int i=0;i<nComp;i++) {
                        Component comp = getPopupMenu().getComponent(i);
                        if (comp instanceof JMenuItem) {
                                String itemLabel = ((JMenuItem) comp).getText();
                                if (Util.stringMatch(itemLabel, sIndex)) {
                                        index = i;
                                        break;
                                }
                        }
               }
        }
        return (index);
    }

}
