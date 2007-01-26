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


append specialInits {
    setOpaque(true);
    setHorizontalAlignment(anchor[1]);
    setVerticalAlignment(anchor[0]);
}

append specialConfig "
	$widgetVar.updateState();
"

append specialMethods {
public void updateState() {
                if (!textVariable.equals("")) {
                        try {
                        TclObject varObj = interp.getVar(textVariable,TCL.GLOBAL_ONLY);
                        if (varObj != null) {
                                this.setText(varObj.toString());
                        }
                        }
                        catch (TclException tclE)
                        {
                        }
                }
}

}
