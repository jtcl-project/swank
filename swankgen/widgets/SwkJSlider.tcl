#
#
# Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, NJ, USA
#
#
append specialVars {
    DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance();
}

append specialListeners {
}
append specialImports "\nimport java.text.*;"

append specialInits {
}
# -bigincrement
	set specialGets [concat  $specialGets {
		{setBigIncrement double BigIncrement -bigincrement} 
	}]
        append specialVars {
                double bigIncrement=0.0;
                MySlider jslider = null;
                MyPanel jPanel = null;
                JLabel jLabel = null;
                int currentOrientation = JSlider.VERTICAL+1+JSlider.HORIZONTAL;

        }
        append specialMethods {
                        public void setBigIncrement(double value) {
                                bigIncrement = value;
                        }

                        public double getBigIncrement() {
                                return(bigIncrement);
                        }
                        public JSlider getSlider() {
                                return jslider;
                        }

                        }


# -from
	set specialGets [concat  $specialGets {
		{setFrom double From -from} 
	}]
        append specialVars {
		double from = 0.0;
	}
	append specialMethods {
    class MySlider extends JSlider {
          MySlider() {
              super();
          }
          public void paintComponent(Graphics g) {
               super.paintComponent(g);
               jPanel.repaint();
          }
                      public Dimension getMinimumSize() {
                            Dimension dSize = new Dimension(0,0);
                                FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                                int fontHeight = fontMetrics.getHeight();
                                if (jslider.getOrientation() == JSlider.VERTICAL) {
                                   dSize = super.getMinimumSize();
                                    if (dSize.height < length) {
                                        dSize.height = length;
                                    }
                                    if (dSize.width < swkwidth) {
                                        dSize.width = swkwidth;
                                    }
                                    if (jslider.getPaintLabels()) {
                                        dSize.width += 20;
                                    }
                                    if (jslider.getPaintTicks()) {
                                        dSize.width += 10;
                                    }
                                } else {
                                   dSize = super.getMinimumSize();

                                    if (dSize.width < length) {
                                        dSize.width = length;
                                    }

                                    if (dSize.height < swkwidth) {
                                        dSize.height = swkwidth;
                                    }

                                    /*
                                    if (jslider.getPaintLabels()) {
                                        dSize.height += 20;
                                     }
                                    if (jslider.getPaintTicks()) {
                                        dSize.height += 10;
                                     }
                                     */

                                }
                                  return dSize;
                 } 
                        public Dimension getPreferredSize() {
                                return(getMinimumSize());
                        }
    }


   class MyPanel extends JPanel {
          MyPanel() {
              super();
          }

  synchronized public void paintComponent(Graphics g) {
        double value = jslider.getValue();
        Dimension size = jPanel.getSize();
        FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
        int valueHeight = fontMetrics.getHeight();
        String valString = nf.format(getDValue());
        int valueWidth = fontMetrics.stringWidth(valString);
        if (jslider.getOrientation() == JSlider.VERTICAL) {
                if (showValue) {
                        int y = Math.round((int) ((size.height-20)*value/(jslider.getMaximum()-jslider.getMinimum()))+10);
                        y += (valueHeight/2);
                        //g.setClip(0,0,size.width,size.height);
                        g.setColor(getBackground());
                        //g.setColor(Color.RED);
                        g.fillRect(0,0,size.width,size.height);
                        g.setColor(getForeground());
                        g.drawString(valString, 0, y);
                } 
        } else {
                if (showValue) {
                        int x = Math.round((int) ((size.width-20)*value/(jslider.getMaximum()-jslider.getMinimum()))+10);
                        x = x-(valueWidth/2);
                        if (x < 0) {
                                x = 0;
                        }
                        if (x > (size.width-valueWidth)) {
                            x = (size.width-valueWidth);
                        }
                        int y = size.height-1;
                        g.setColor(getBackground());
                        g.fillRect(0,0,size.width,size.height);
                        g.setColor(getForeground());
                        g.drawString(valString, x, y);
                }
        }
   }
                      public Dimension getMinimumSize() {
                            Dimension dSize = new Dimension(0,0);
                                FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                               if (jslider.getOrientation() == JSlider.VERTICAL) {
                                    if (showValue) {
                                        double dVal = convertValue(jslider.getMaximum());
                                        String valString = nf.format(dVal);
                                        dSize.width += fontMetrics.stringWidth(valString);
                                    }
                                } else {
                                    if (showValue) {
                                        dSize.height += fontMetrics.getHeight();
                                    }
                                }
                            return dSize;
                          }
                        public Dimension getPreferredSize() {
                                return(getMinimumSize());
                        }

   }


                       public void setDValue(double value) {
                                int iValue = 0;
                                Dimension size = getSize();
                                double to = getTo();
                                double from = getFrom();
                                double f = (value-from)/(to-from);
                                jslider.setValue((int) Math.round(f*(jslider.getMaximum()-jslider.getMinimum())+jslider.getMinimum()));
                        }
                        public double getDValue() {
                                return convertValue((double) jslider.getValue());
                        }
                        double convertValue(double iValue) {
                                double dValue = 0.0;
                                double to = getTo();
                                double from = getFrom();
                                double f = (iValue - jslider.getMinimum())/(jslider.getMaximum()-jslider.getMinimum());
                                dValue = f*(to-from)+from;
                                double cRes=1.0;
                                if (resolution > 0.0) {
                                        cRes = resolution;
                                } else {
                                        cRes = Math.abs(to-from)/getLength();
                                        cRes = Math.pow(10.0,Math.floor(Math.log(cRes)/Math.log(10.0)));
                                }
                                dValue = ((long) (Math.round(dValue/cRes)))*cRes;
                                return(dValue);
                        }

                       public void updateRange() {
                                Hashtable labels = new Hashtable();
                                labels.put(new Integer(0),new JLabel("0"));
                                jslider.setLabelTable(labels);
                                double dValue = getDValue();
                                jslider.setMinimum(0);
                                if (resolution > 0.0) {
                                        to = ((long) (Math.round(to/resolution)))*resolution;
                                        from = ((long) (Math.round(from/resolution)))*resolution;
                                        jslider.setMaximum((int) Math.round(Math.abs((to-from))/resolution));
                                } else {
                                        jslider.setMaximum(1000);
                                }
                                setDValue(dValue);
                                if (from < to) {
                                        tickInterval = Math.abs(tickInterval);
                                } else {
                                        tickInterval = -Math.abs(tickInterval);
                                }
                                if (tickInterval == 0.0) {
                                        jslider.setPaintLabels(false);
                                        jslider.setMajorTickSpacing(jslider.getMaximum()/10);
                                        jslider.setMinorTickSpacing(jslider.getMajorTickSpacing()/2);
                                } else {
                                jslider.setMajorTickSpacing((int) Math.round(jslider.getMaximum()*Math.abs(tickInterval/(to-from))));
                                jslider.setMinorTickSpacing(jslider.getMajorTickSpacing()/2);
                                labels = new Hashtable();
                                int incr = jslider.getMajorTickSpacing();
                                for (int i=jslider.getMinimum();i<=jslider.getMaximum();i += incr) {
                                        labels.put(new Integer(i),new JLabel(String.valueOf(convertValue(i))));
                                }
                                jslider.setLabelTable(labels);
                                }

                        }


			public void setTo(double value) {
				to = value;
			}
			
			public double getTo() {
				return(to);
			}
			public void setFrom(double value) {
				from = value;
			}
			
			public double getFrom() {
				return(from);
			}
			}
	append specialVars {
		String orient=null;
	}
	append specialMethods {
			public void setOrient(int orient) {
				if (orient == JSlider.VERTICAL) {
					jslider.setOrientation(JSlider.VERTICAL);
					jslider.setInverted(true);
				} else {
					jslider.setOrientation(JSlider.HORIZONTAL);
					jslider.setInverted(false);
				}
                                componentsValid = false;
			}
			public String getOrient() {
				if (jslider.getOrientation() == JSlider.VERTICAL) {
        			return("vertical");
				} else {
        			return("horizontal");
				}
			}
	}
	set specialGets [concat  $specialGets {
		{setOrient orient Orient} 
	}]
 
# -to

        append specialVars {
		double to = 100.0;
	}
	set specialGets [concat  $specialGets {
		{setTo double To -to} 
	}]

# -value

	set specialGets [concat  $specialGets {
		{setDValue double DValue -value} 
	}]

# -label
        set specialGets [concat  $specialGets {
                {setLabel java.lang.String Label -label}
        }]
        append specialVars {
                String label="";
        }
        append specialMethods {
                        public void setLabel(String value) {
                                if (value == null) {
                                      value = "";
                                }
                                label = value;
                                jLabel.setText(label);
                                
                                componentsValid = false;
                                revalidate();
                        }

                        public String getLabel() {
                                return(label);
                        }
                        }
# -troughcolor
        set specialGets [concat  $specialGets {
                {setTroughColor java.awt.Color TroughColor -troughcolor}

        }]
        append specialVars {
                Color troughColor=Color.white;
        }
        append specialMethods {
                        public void setTroughColor(Color value) {
                                troughColor = value;
                        }

                        public Color getTroughColor() {
                                return(troughColor);
                        }
                        }


# -state
        set specialGets [concat  $specialGets {
                {setState state State -state}
        }]
        append specialVars {
                String state=NORMAL;
        }
        append specialMethods {
                        public void setState(String value) {
				if (NORMAL.startsWith(value)) {
                                	state = NORMAL;
					setEnabled(true);
				} else if (ACTIVE.startsWith(value)) {
                                	state = ACTIVE;
					setEnabled(true);
				} else if (DISABLED.startsWith(value)) {
                                	state = DISABLED;
					setEnabled(false);
				}
                        }

                        public String getState() {
				if (isEnabled())  {
					if (!state.equals(ACTIVE) && !state.equals(NORMAL)) {
                                		state = NORMAL;
					} 
				} else {
                                	state = DISABLED;
				}
                                return state;
                        }
                        }

# -sliderrelief
        set specialGets [concat  $specialGets {
                {setSliderRelief tkRelief SliderRelief -sliderrelief}
        }]
        append specialVars {
                String sliderRelief="sunken";
        }
        append specialMethods {
                        public void setSliderRelief(String value) {
                                sliderRelief = value;
                        }

                        public String getSliderRelief() {
                                return(sliderRelief);
                        }
                        }


# -resolution
	set specialGets [concat  $specialGets {
		{setResolution double Resolution -resolution} 
	}]
	append specialVars {
		double resolution=1.0;
	}
	append specialMethods {
			public void setResolution(double value) {
				resolution = value;
			}
			
			public double getResolution() {
				return(resolution);
			}
			}
# -digits
	set specialGets [concat  $specialGets {
		{setDigits int Digits -digits} 
	}]
	append specialVars {
		int digits=0;
	}
	append specialMethods {
			public void setDigits(int value) {
				digits = value;
        			nf.setMaximumFractionDigits(digits);
        			nf.setMinimumFractionDigits(digits);

			}
			
			public int getDigits() {
				return(digits);
			}
			}
    		append specialVars "
			SwkSliderChangeListener sliderChangeListener=null;
			boolean showValue=true;
			int length = 100;
			int swkwidth = 15;
			double tickInterval=0.0;
                        boolean componentsValid = false;
		"
		append specialInits {
                        jslider = new MySlider();
                        jPanel = new MyPanel();
                        jLabel = new JLabel(label);
                         setLayout(new GridBagLayout());
			sliderChangeListener = new SwkSliderChangeListener(interp,this);
        		jslider.addChangeListener (sliderChangeListener);
                        setOpaque(true);
                        jslider.setOpaque(true);

		}
    		append specialMethods {
    void updateComponents() {
                   GridBagConstraints gbConstr = new GridBagConstraints();
                   removeAll();
                   if (jslider.getOrientation() == JSlider.VERTICAL) {
                         gbConstr.gridx = GridBagConstraints.RELATIVE;
                         gbConstr.gridy = 0;
                         gbConstr.weighty = 0.0;
                         gbConstr.anchor = GridBagConstraints.NORTH;
                         gbConstr.fill = GridBagConstraints.VERTICAL;
                         if (showValue) {
                             add(jPanel,gbConstr);
                         }
                         gbConstr.weighty = 1.0;
                         add(jslider,gbConstr);
                         gbConstr.weighty = 0.0;
                         if (!label.equals("")) {
                             gbConstr.fill = GridBagConstraints.NONE;
                             add(jLabel,gbConstr);
                         }
                   } else {
                         gbConstr.gridx = 0;
                         gbConstr.weightx = 0.0;
                         gbConstr.gridy = GridBagConstraints.RELATIVE;
                         gbConstr.anchor = GridBagConstraints.WEST;
                         if (!label.equals("")) {
                             gbConstr.fill = GridBagConstraints.NONE;
                             add(jLabel,gbConstr);
                         }
                         gbConstr.fill = GridBagConstraints.HORIZONTAL;
                         gbConstr.weightx = 1.0;
                         add(jslider,gbConstr);
                         if (showValue) {
                             gbConstr.weightx = 0.0;
                             add(jPanel,gbConstr);
                         }
                   }
                   componentsValid = true;
    }
    public Dimension getMinimumSize() {
           if (jslider.getOrientation()  != currentOrientation) {
                   currentOrientation = jslider.getOrientation();
           }
           if (!componentsValid) {
                   updateComponents();
           }
           Dimension dSize = jslider.getMinimumSize();
           if (jslider.getOrientation() == JSlider.VERTICAL) {
               if (showValue) {
                    Dimension dSizeP = jPanel.getMinimumSize();
                    dSize.width += dSizeP.width;
               }
               if (!label.equals("")) {
                    Dimension dSizeL = jLabel.getMinimumSize();
                    dSize.width += dSizeL.width;
               }
           } else {
               if (showValue) {
                    Dimension dSizeP = jPanel.getMinimumSize();
                    dSize.height += dSizeP.height;
               }
               if (!label.equals("")) {
                    Dimension dSizeL = jLabel.getMinimumSize();
                    dSize.height += dSizeL.height;
               }
           }
           return dSize;
   }
          public void paintComponent(Graphics g) {
               super.paintComponent(g);
          }


                        public Dimension getPreferredSize() {
                                return(getMinimumSize());
                        }
			public void setCommand(String name) {
        			sliderChangeListener.setCommand(name); }
			public String getCommand() {
        			return(sliderChangeListener.getCommand());
			}
			public void setTickInterval(double tickInterval) {
				if (resolution > 0.0) {
                                         this.tickInterval = resolution*((int) Math.round(tickInterval/resolution));
                                } else {
                                        this.tickInterval = Math.round(tickInterval);
                                }
 
			}
			public double getTickInterval() {
        			return(tickInterval);
			}
			public String getVarName() {
        			return(sliderChangeListener.getVarName());
			}
			public void setShowValue(boolean showValue) {
        			this.showValue = showValue;
                                componentsValid = false;
                                revalidate();
			}
			public String getShowValue() {
				if (showValue) {
        			return("1");
				} else {
					return("0");
				}
			}
                       public void setLength(int length) {
                                this.length = length;
                        }
                        public int getLength() {
                                return(length);
                        }
                        public void setSwkWidth(int width) {
                                this.swkwidth = width;
                        }
                        public int getSwkWidth() {
                                return(swkwidth);
                        }
		    }
		    
		
		lappend specialGets "setCommand java.lang.String Command"
		lappend specialGets "setVarName variable VarName -variable"
		lappend specialGets "setValue int Value -value"
		lappend specialGets "setShowValue boolean ShowValue -showvalue"
		lappend specialGets "setTickInterval double TickInterval -tickinterval"
		lappend specialGets "setLength tkSize Length -length"
		lappend specialGets "setLength tkSize Length -sliderlength"
		lappend specialGets "setSwkWidth tkSize Width -width"
		append specialConfig "
			$widgetVar.sliderChangeListener.setFromVar(interp,true);
			$widgetVar.updateRange();
		"
        set closeMethod {
                   public void close() {
                        if ((getVarName() != null) && (getVarName().length() != 0)) {
                        interp.untraceVar(getVarName(),sliderChangeListener,TCL.TRACE_WRITES| TCL.GLOBAL_ONLY);
                    }
                    }
        }



append specialMethods {
     public void setVarName(Interp interp, String name) throws TclException {
        sliderChangeListener.setVarName(interp,name);
     }

}
