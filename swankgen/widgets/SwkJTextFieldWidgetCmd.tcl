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
			SwkDocumentListener docListener=null;
}

append specialListeners {
}


append specialInits {
		docListener = new SwkDocumentListener(interp,this);
        getDocument ().addDocumentListener (docListener);
}
  


append specialMethods {
		public void getIndex(String sIndex,int endVal,Result result) {
     int index=0;
     boolean isInt = false;

       if (SwankUtil.looksLikeInt(sIndex)) {
            try {
                index = Integer.parseInt(sIndex);
                isInt = true; 
            } catch (NumberFormatException nfE) {
                isInt = false; 
            }
        }
        
        if (!isInt) {
                boolean validIndex = false;
                if (sIndex.startsWith("e")) {
                        index = getText().length()+endVal;
                        validIndex = true;
                } else if (sIndex.equals("active")) {
			index = active;
			if (index >= getText().length()) {
				index = getText().length()-1;
			}
                        validIndex = true;
                } else if (sIndex.startsWith("sel.f")) {
                        validIndex = true;
                	index = getSelectionStart();
                } else if (sIndex.startsWith("sel.l")) {
                        validIndex = true;
                 	index = getSelectionEnd();
               } else if (sIndex.equals("anchor")) {
		  	/*	int selected[] = getSelectedIndices();
		  		if (selected.length > 0) {
		  			index = selected[0];
                                        validIndex = true;
                    } else {
                    	index = 0;
                    	validIndex = true;
                    }
                    */
                } else if (sIndex.startsWith("@") ){
                	if (sIndex.length() > 1) {
                        	String xS = sIndex.substring(1);
		                      try {
		                   /*     	int x = Integer.valueOf(xS).intValue();
		                        	p.y = Integer.valueOf(yS).intValue();
			  						p1 = jview.getViewPosition ();
									p.y += p1.y;
		                        	index = locationToIndex(p);
		                        	*/
/*			 						Dimension listSize = getSize ();
			  						index  += (int) (p.y / (listSize.height / model.getSize ()));
*/
		                        	validIndex = true;
		                        } catch (Exception e) 
		                        {
		                        }
                }}
                
                if (!validIndex) {
					result.setError("bad listbox index \""+sIndex+"\": must be active, anchor, end, @x,y, or a number");
				}
				
				
        }
        result.i = index;
		
    }

		}

