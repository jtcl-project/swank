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
  DefaultListModel model=null;
  DefaultListSelectionModel selectionModel=null;
  JViewport jview=null;
  JScrollPane jscroll=null;
  JComponent packComponent=null;
  private static final String longItem = "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm";
}

append specialListeners {,ChangeListener}


append specialInits {
     if (className.equals("Listbox")) {
    	/*jview = new JViewport();
    	packComponent = jview;
    	jview.setView(this);
		jview.addChangeListener(this);
		*/
    } else if (className.equals("Slistbox")) {
    	jscroll = new JScrollPane(this);
    	jview = jscroll.getViewport();
    	packComponent = jscroll;
    }
    model = new DefaultListModel();
    selectionModel = new DefaultListSelectionModel();
    setModel(model);
    selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    setSelectionModel(selectionModel);
}
  
# -listvar
        append specialVars {
                String listVar;
        }
 
        append specialMethods "
                        public void setListVar(String listVar) \{
                                        this.listVar = listVar.intern();
                        \}
                        public String getListVar() \{
                                return(listVar);
                        \}
        "
        set specialGets [concat  $specialGets {
                {setListVar java.lang.String ListVar}
        }]


append specialMethods {
public void stateChanged(ChangeEvent event) {
    			 if (jview == null) {
    			     jview = (JViewport) getParent();
    			 }
    			 if (jview == null) {
    			     return;
    			 }
    			 
			  Point pt = jview.getViewPosition ();
			  Dimension viewSize = jview.getViewSize ();
			  Dimension listSize = getSize ();
			  Dimension extentSize = jview.getExtentSize ();
			  double fy1 = 1.0 * pt.y / listSize.height;
			  double fy2 = 1.0 * (pt.y + extentSize.height) / listSize.height;
			  double fx1 = 1.0 * pt.x / listSize.width;
			  double fx2 = 1.0 * (pt.x + extentSize.width) / listSize.width;
                          if ((yScrollCommand != null) && (yScrollCommand.length() > 0)) {
                              BindEvent bEvent = new BindEvent(interp,yScrollCommand+" "+fy1+" "+fy2);
                              interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
                          }
                          if ((xScrollCommand != null) && (xScrollCommand.length() > 0)) {
                              BindEvent bEvent = new BindEvent(interp,xScrollCommand+" "+fx1+" "+fx2);
                              interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
                          }
}


    public void getIndex(String sIndex, int endVal, Result result) {
        int index = 0;
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

            if (sIndex.equals("end")) {
                index = model.getSize() + endVal;
                validIndex = true;
            } else if (sIndex.equals("active")) {
                index = active;

                if (index >= model.getSize()) {
                    index = model.getSize() - 1;
                }

                validIndex = true;
            } else if (sIndex.equals("anchor")) {
                int[] selected = getSelectedIndices();

                if (selected.length > 0) {
                    index = selected[0];
                    validIndex = true;
                } else {
                    index = 0;
                    validIndex = true;
                }
            } else if (sIndex.startsWith("@")) {
                int visIndex = this.getFirstVisibleIndex();

                if (sIndex.length() > 3) {
                    int comma = sIndex.indexOf(",");

                    if (comma > 1) {
                        if ((sIndex.length() - comma) > 1) {
                            String xS = sIndex.substring(1, comma);
                            String yS = sIndex.substring(comma + 1);

                            Point p = new Point();
                            Point p1 = new Point();

                            try {
                                p.x = Integer.valueOf(xS).intValue();
                                p.y = Integer.valueOf(yS).intValue();
                                index = locationToIndex(p) + visIndex;
                                validIndex = true;
                            } catch (Exception e) {
                                System.out.println(e.toString());
                            }
                        }
                    }
                }
            }

            if (!validIndex) {
                result.setError("bad listbox index \"" + sIndex +
                    "\": must be active, anchor, end, @x,y, or a number");
            }
        }

        result.i = index;
    }


			public void setSwkWidth(int width) {
        			this.swkwidth = width;
        			String prefItem = longItem.substring(0,width);
        			setPrototypeCellValue(prefItem);
        			Window window = (Window) getTopLevelAncestor();
        			if (window != null) {
        				window.pack();
        			}
			}
			public int getSwkWidth() {
        			return(swkwidth);
			}
			public void setSwkHeight(int height) {
			    this.swkheight=height;
				setVisibleRowCount(height);
				Window window = (Window) getTopLevelAncestor();
        			if (window != null) {
        				window.pack();
        			}
				
			}
			
			public int getSwkHeight() {
        			return(getVisibleRowCount());
			}
		
		
}
