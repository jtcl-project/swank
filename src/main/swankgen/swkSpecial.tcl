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

# listbox specific
# -height selectmode -width
#

global swkdefs

set swkdefs(BLACK)		"Black"
set swkdefs(WHITE)		"White"

set swkdefs(NORMAL_BG)	"#d9d9d9"
set swkdefs(ACTIVE_BG)	"#ececec"
set swkdefs(SELECT_BG)	"#c3c3c3"
set swkdefs(TROUGH)		"#c3c3c3"
set swkdefs(INDICATOR)	"#b03060"
set swkdefs(DISABLED)	"#a3a3a3"


set swkdefs(JList,BG_COLOR)		$swkdefs(NORMAL_BG)
set swkdefs(JList,BG_MONO)		$swkdefs(WHITE)
set swkdefs(JList,BORDER_WIDTH)	"2"
set swkdefs(JList,CURSOR)		""
set swkdefs(JList,EXPORT_SELECTION)	"1"
set swkdefs(JList,FONT)		"Helvetica -12 bold"
set swkdefs(JList,FG)			$swkdefs(BLACK)
set swkdefs(JList,HEIGHT)		"10"
set swkdefs(JList,HIGHLIGHT_BG)	$swkdefs(NORMAL_BG)
set swkdefs(JList,HIGHLIGHT)		$swkdefs(BLACK)
set swkdefs(JList,HIGHLIGHT_WIDTH)	"1"
set swkdefs(JList,RELIEF)		{"sunken"}
set swkdefs(JList,SCROLL_COMMAND)	""
set swkdefs(JList,SELECT_COLOR)	$swkdefs(SELECT_BG)
set swkdefs(JList,SELECT_MONO)		$swkdefs(BLACK)
set swkdefs(JList,SELECT_BD)		"1"
set swkdefs(JList,SELECT_FG_COLOR)	$swkdefs(BLACK)
set swkdefs(JList,SELECT_FG_MONO)	$swkdefs(WHITE)
set swkdefs(JList,SELECT_MODE)		"browse"
set swkdefs(JList,SET_GRID)		"0"
set swkdefs(JList,TAKE_FOCUS)		null
set swkdefs(JList,WIDTH)		"20"

set swkdefs(JFrame,WIDTH)		"200"
set swkdefs(JFrame,HEIGHT)		"200"


set swkdefs(JButton,RELIEF)		{"raised"}
set swkdefs(JButton,BORDERWIDTH)	"2"
set swkdefs(SMenuButton,RELIEF)		{"raised"}
set swkdefs(SMenuButton,BORDERWIDTH)	"2"
set swkdefs(JTextField,RELIEF)		{"sunken"}
set swkdefs(JTextField,BORDERWIDTH)	"2"

set swkdefs(JLabel,BORDERWIDTH)	"2"

proc defpar {par widget default} {
    global swkdefs
    set par [string toupper $par]
    if {[info exists swkdefs($widget,$par)]} {
        return $swkdefs($widget,$par)
        } elseif {[info exists swkdefs($par)]} {
        return $swkdefs($par)
        } else {
        return $default
    }
}

proc swkMakeSpecial {widget widgetVar} {
    global specialCmds specialInits specialVars specialMethods specialGets specialSuper specialVisible
    global specialVCmds specialOpts specialListeners specialConfig specialImports specialWidgetTypeCmds specialPrints
    
    set specialCmds ""
    set specialVCmds ""
    set specialOpts ""
    set specialInits ""
    set specialSuper ""
    set specialVisible ""
    set specialVars ""
    set specialMethods " "
    set closeMethod {
        public void close() throws TclException {}
    }
    set specialGets ""
    set specialIss ""
    set specialListeners ""
    set specialConfig ""
    set specialImports ""
    set specialWidgetTypeCmds ""
    
    #non-standard options
    # -accelerator
    set vWidgets "SMenuButton JButton SMenu SMenuButton JMenu JMenuItem"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            String swkaccelerator="";
        }
        append specialMethods {
            public void setSwkAccelerator(String accelerator) {
                this.swkaccelerator = accelerator;
            }
            public String getSwkAccelerator() {
                return(swkaccelerator);
            }
        }
        set specialGets [concat  $specialGets {
            {setSwkAccelerator java.lang.String Accelerator -accelerator}
        }]
    }
    
    # -container
    if {[lsearch "JPanel LabelFrame" $widget ] >= 0} {
        append specialVars {
            boolean container;
        }
        
        append specialMethods {
            public void setContainer(boolean container) {
                this.container=container;
            }
            public boolean getContainer() {
                return container;
            }
        }
        
        set specialGets [concat  $specialGets {
            {setContainer boolean Container}
        }]
    }
    # -visual
    if {[lsearch "JPanel LabelFrame" $widget ] >= 0} {
        append specialVars {
            String visual=null;
        }
        
        append specialMethods {
            public void setVisual(String visual) {
                this.visual = visual.intern();
            }
            public String getVisual() {
                return(visual);
            }
        }
        set specialGets [concat  $specialGets {
            {setVisual java.lang.String Visual}
        }]
    }
    
    
    # -default
    if {[lsearch "JButton" $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setDefault default Default -default}
        }]
        append specialVars {
            String SwkDefault=null;
        }
        
        append specialMethods "
        public void setDefault(String SwkDefault) \{
            this.SwkDefault = SwkDefault.intern();
        \}
        public String getDefault() \{
            return(SwkDefault);
        \}
        "
    }
    
    # -debug
    if {[lsearch "JTextPane" $widget ] >= 0} {
        append specialVars {
            boolean debug=false;
        }
    }
    
    # -height
    if {[lsearch "JList" $widget ] >= 0} {
        append specialVars {
            int swkheight;
        }
        
        set specialGets [concat  $specialGets {
            {setSwkHeight int VisibleRowCount -height}
        }]
    }
    
    # -jhelptarget
    if {1} {
        append specialVars {
            String jhelptarget="";
        }
        append specialMethods {
            public void setJHelpTarget(String jhelptarget) {
                this.jhelptarget = jhelptarget;
                SwankUtil.setJHelpTarget(this,jhelptarget);
            }
            public String getJHelpTarget() {
                return(jhelptarget);
            }
        }
        set specialGets [concat  $specialGets {
            {setJHelpTarget java.lang.String JHelpTarget -jhelptarget}
        }]
    } 
    
    # -hidemargin
    if {[lsearch "SMenuButton JButton SMenuButton JMenu JMenuItem" $widget ] >= 0} {
        append specialVars {
            boolean hideMargin;
        }
        
        append specialMethods {
            public void setHideMargin(boolean hideMargin) {
                this.hideMargin=hideMargin;
            }
            public boolean getHideMargin() {
                return hideMargin;
            }
        }
        
        set specialGets [concat  $specialGets {
            {setHideMargin boolean HideMargin}
        }]
    }
    
    # -insertBackground
    set vWidgets "JTextPane JTextField Canvas"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            Color insertBackground;
        }
        append specialMethods "
        public void setInsertBackground(Color insertBackground) \{
            this.insertBackground = insertBackground;
        \}
        public Color getInsertBackground() \{
            return(insertBackground);
        \}
        "
        set specialGets [concat  $specialGets {
            {setInsertBackground java.awt.Color InsertBackground}
        }]
    }
    # -insertWidth
    set vWidgets "JTextPane JTextField Canvas"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars "
        int insertWidth = [defpar insertWidth $widget 0];
        "
        
        append specialMethods "
        public void setInsertWidth(int insertWidth) \{
            this.insertWidth = insertWidth;
        \}
        public int getInsertWidth() \{
            return(insertWidth);
        \}
        "
        set specialGets [concat  $specialGets {
            {setInsertWidth tkSize InsertWidth -insertwidth}
        }]
    }
    
    # -insertBorderWidth
    set vWidgets "JTextPane JTextField Canvas"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars "
        int insertBorderWidth = [defpar insertBorderWidth $widget 0];
        "
        
        append specialMethods "
        public void setInsertBorderWidth(int insertBorderWidth) \{
            this.insertBorderWidth = insertBorderWidth;
        \}
        public int getInsertBorderWidth() \{
            return(insertBorderWidth);
        \}
        "
        set specialGets [concat  $specialGets {
            {setInsertBorderWidth tkSize InsertBorderWidth -insertborderwidth}
        }]
    }
    # -insertInsertOffTime
    set vWidgets "JTextPane JTextField Canvas"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars "
        int insertOffTime = [defpar insertOffTime $widget 0];
        "
        
        append specialMethods "
        public void setInsertOffTime(int insertOffTime) \{
            this.insertOffTime = insertOffTime;
        \}
        public int getInsertOffTime() \{
            return(insertOffTime);
        \}
        "
        set specialGets [concat  $specialGets {
            {setInsertOffTime int InsertOffTime -insertofftime}
        }]
    }
    # -insertInsertOnTime
    set vWidgets "JTextPane JTextField Canvas"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars "
        int insertOnTime = [defpar insertOnTime $widget 0];
        "
        
        append specialMethods "
        public void setInsertOnTime(int insertOnTime) \{
            this.insertOnTime = insertOnTime;
        \}
        public int getInsertOnTime() \{
            return(insertOnTime);
        \}
        "
        set specialGets [concat  $specialGets {
            {setInsertOnTime int InsertOnTime -insertontime}
        }]
    }
    # -nativekeys
    set vWidgets "JTextPane JTextField"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars "
        boolean processNativeKeyBindings = true;
        "
        append specialMethods "
        public void setProcessNativeKeyBindings(boolean processNativeKeyBindings) \{
            this.processNativeKeyBindings = processNativeKeyBindings;
        \}
        public boolean getProcessNativeKeyBindings() \{
            return(processNativeKeyBindings);
        \}
        "
        set specialGets [concat  $specialGets {
            {setProcessNativeKeyBindings boolean NativeKeys -nativekeys}
        }]
    }

    # -show
    set vWidgets "JTextField"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            String show="";
        }
        append specialMethods {
            public void setShow(String show) {
                this.show = show;
            }
            public String getShow() {
                return(show);
            }
        }
        set specialGets [concat  $specialGets {
            {setShow java.lang.String Show -show}
        }]
    }
    # -bd
    
    # -spacing
    set vWidgets "JTextPane"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            int spacing1=1;
            int spacing2=1;
            int spacing3=1;
        }
        append specialMethods {
            public void setSpacing1(int spacing1) {
                if (spacing1 < 0) {
                    spacing1 = 0;
                }
                this.spacing1 = spacing1;
            }
            public int getSpacing1() {
                return(spacing1);
            }
            public void setSpacing2(int spacing2) {
                if (spacing2 < 0) {
                    spacing2 = 0;
                }
                this.spacing2 = spacing2;
            }
            public int getSpacing2() {
                return(spacing2);
            }
            public void setSpacing3(int spacing3) {
                if (spacing3 < 0) {
                    spacing3 = 0;
                }
                this.spacing3 = spacing3;
            }
            public int getSpacing3() {
                return(spacing3);
            }
        }
        set specialGets [concat  $specialGets {
            {setSpacing1 int Spacing1 -spacing1}
        }]
        set specialGets [concat  $specialGets {
            {setSpacing2 int Spacing2 -spacing2}
        }]
        set specialGets [concat  $specialGets {
            {setSpacing3 int Spacing3 -spacing3}
        }]
    }
    # -bd
    
    #-selectimage
    
    if {[lsearch "SMenuButton JCheckBox JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem" $widget] >= 0} {
        set specialGets [concat  $specialGets {
            {setSelectedIcon javax.swing.Icon SelectImage -selectimage}
        }]
    }
    # -scrollregion
    set vWidgets "Canvas"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialMethods {
        public void setScrollRegion(int scrollRegion[][]) {
             swkImageCanvas.scrollRegion[0][0] = scrollRegion[0][0];
             swkImageCanvas.scrollRegion[0][1] = scrollRegion[0][1];
             swkImageCanvas.scrollRegion[1][0] = scrollRegion[1][0];
             swkImageCanvas.scrollRegion[1][1] = scrollRegion[1][1];

        }
        public int[][] getScrollRegion() {
            return(swkImageCanvas.scrollRegion);
        }
        public void setClassName(String className) {
            this.className = className.intern();
        }
        }
        set specialGets [concat  $specialGets {
            {setScrollRegion tkRectangleCorners ScrollRegion}
        }]
        set specialGets [concat  $specialGets {{setClassName java.lang.String ClassName -class}}]
    }
    
    # -state
    set vWidgets "JMenu JMenuItem SMenuButton JButton JRadioButton JRadioButtonMenuItem JCheckBox SMenuButton JLabel JComboBox JSpinner"
    if {[lsearch $vWidgets $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setState state State -state}
        }]
        append specialVars {
            String state=NORMAL;
        }
        append specialMethods {
            public void setState(String value) {
                state = NORMAL;
                if (NORMAL.startsWith(value)) {
                    state = NORMAL;
                    setEnabled(true);
                } else if (ACTIVE.startsWith(value)) {
                    state = ACTIVE;
                    setEnabled(true);
                } else if (DISABLED.startsWith(value)) {
                    state = DISABLED;
                    setEnabled(false);
                } else {
                    state = NORMAL;
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
    }
    
    # -indicatoron
    
    if {[lsearch "JCheckBox JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem" $widget] >= 0} {
        append specialVars {
            boolean indicatorOn=true;
        }
        set specialGets [concat  $specialGets {
            {setIndicatorOn boolean IndicatorOn}
        }]
        append specialMethods {
            public void setIndicatorOn(boolean indicatorOn) {
                this.indicatorOn = indicatorOn;
            }
            public boolean getIndicatorOn() {
                return(indicatorOn);
            }
        }
        
    }
    
    # -label
    if {[lsearch "SMenuButton JButton JCheckBox JRadioButton JToggleButton JMenuItem JCheckBoxMenuItem JRadioButtonMenuItem JMenu" $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setText java.lang.String Text -label}
        }]
    }
    
    # -label
    if {[lsearch "JOptionPane" $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setSwkMessage java.lang.String Text -message}
            {setSwkOptions options Text -options}
        }
        ]
        append specialMethods {
            public void setSwkMessage(String message) {
                setMessage((Object) message);
            }
            public String getSwkMessage() {
                return(getMessage().toString());
            }
            
            public void setSwkOptions(Object[] options) {
                setOptions(options);
                
            }
            public String getSwkOptions() {
                Object options[] = getOptions();
                String sOptions = "";
                if (options != null) {
                    for (int i=0;i<options.length;i++) {
                        sOptions = sOptions+" "+options[i].toString();
                    }
                }
                
                return sOptions;
            }
            
        }
    }
    
    # -postcommand
    if {[lsearch "JPopupMenu SMenu JMenu" $widget ] >= 0} {
        append specialVars {
            String postCommand;
        }
        
        append specialMethods "
        public void setPostCommand(String postCommand) \{
            this.postCommand = postCommand.intern();
        \}
        public String getPostCommand() \{
            return(postCommand);
        \}
        "
        set specialGets [concat  $specialGets {
            {setPostCommand java.lang.String PostCommand}
        }]
    }
    
    # -selectColor
    if {[lsearch "JCheckBox JMenu JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem" $widget] >= 0} {
        append specialVars {
            Color selectColor=Color.red;
        }
        append specialMethods {
            public void setSelectColor(Color selectColor) {
                this.selectColor = selectColor;
            }
            public Color getSelectColor() {
                return(selectColor);
            }
        }
        
        
        set specialGets [concat  $specialGets {
            {setSelectColor java.awt.Color SelectColor}
        }]
    }
    
    # -selectMode
    set vWidgets "JList"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setSelectionMode tkSelectMode SelectionMode -selectmode}
        }]
    }
    
    # -state
    set vWidgets "JTextArea JTextField JTextPane"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialMethods {
            public void setState(String state) {
                if (NORMAL.startsWith(state)) {
                    this.setEnabled(true);
                    this.setEditable(true);
                    } else if (READONLY.startsWith(state)) {
                    this.setEnabled(true);
                    this.setEditable(false);
                    } else if (DISABLED.startsWith(state)) {
                    this.setEditable(false);
                    this.setEnabled(false);
                    } else {
                }
            }
            public String getState() {
                String state = NORMAL;
                if (!this.isEditable()) {
                    state = DISABLED;
                }
                return state;
            }
        }
        
        set specialGets [concat  $specialGets {
            {setState tstate State}
        }]
    }
    # -tabs
    set vWidgets "JTextPane"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            String tabs="";
        }
        append specialMethods {
            public void setTabs(String tabs) {
                this.tabs = tabs.intern();
            }
            public String getTabs() {
                return(tabs);
            }
        }
        set specialGets [concat  $specialGets {
            {setTabs java.lang.String Tabs}
        }]
    }
    
    # -wrap
    set vWidgets "JTextPane"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            String wrap="word";
        }
        append specialMethods {
            public void setWrap(String value) {
                if ("none".startsWith(value)) {
                    wrap = "none";
                    } else if ("char".startsWith(value)) {
                    wrap = "char";
                    } else if ("word".startsWith(value)) {
                    wrap = "word";
                    } else {
                    wrap = "word";
                }
            }
            public String getWrap() {
                return(wrap);
            }
        }
        set specialGets [concat  $specialGets {
            {setWrap java.lang.String Wrap}
        }]
    }
    
    # standard options
    
    # -activeBackground
    set vWidgets "SMenuButton JScrollBar JButton JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem SMenuButton JSlider JScrollBar SMenu JMenu"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            Color activeBackground;
        }
        append specialMethods "
        public void setActiveBackground(Color activeBackground) \{
            this.activeBackground = activeBackground;
        \}
        public Color getActiveBackground() \{
            return(activeBackground);
        \}
        "
        set specialGets [concat  $specialGets {
            {setActiveBackground java.awt.Color ActiveBackground}
        }]
    }
    
    
    # -activeBorderWidth
    if {[lsearch "JMenuItem JMenu" $widget ] >= 0} {
        append specialVars {
            int activeBorderWidth;
        }
        append specialMethods "
        public void setActiveBorderWidth(int activeBorderWidth) \{
            this.activeBorderWidth = activeBorderWidth;
        \}
        public int getActiveBorderWidth() \{
            return(activeBorderWidth);
        \}
        "
        set specialGets [concat  $specialGets {
            {setActiveBorderWidth tkSize ActiveBorderWidth}
        }]
    }
    
    
    # -activeForeground
    set vWidgets "SMenuButton JButton  JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SMenu SMenuButton JMenu"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            Color activeForeground;
        }
        append specialMethods "
        public void setActiveForeground(Color activeForeground) \{
            this.activeForeground = activeForeground;
        \}
        public Color getActiveForeground() \{
            return(activeForeground);
        \}
        "
        set specialGets [concat  $specialGets {
            {setActiveForeground java.awt.Color ActiveForeground}
        }]
    }
    # -anchor
    set vWidgets "SMenuButton JButton JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SMenuButton JLabel SLabel"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            int anchor[] = {SwingConstants.CENTER,SwingConstants.CENTER};
        }
        append specialMethods {
            public void setAnchor(int[] anchor) 
            {
                this.setVerticalAlignment(anchor[0]);
                this.setHorizontalAlignment(anchor[1]);
            }
            public String getAnchor() 
            {
               int anchor[] = {getVerticalAlignment(),getHorizontalAlignment()};
                return(SwankUtil.parseAnchorConstants(anchor));
            }
        }
        set specialGets [concat  $specialGets {
            {setAnchor anchor2 Anchor}
        }]
    }
    # -anchor
    set vWidgets "Canvas JTextField JTextArea"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            float anchor[] = {0.0f,0.0f};
        }
        append specialMethods {
            public void setAnchor(float anchor[]) 
            {
                this.anchor = anchor;
            }
            public float[] getAnchor() 
            {
                return(anchor);
            }
        }
        set specialGets [concat  $specialGets {
            {setAnchor anchor Anchor}
        }]
    }
    # -bg
    if {1} {
        set specialGets [concat  $specialGets {
            {setBackground java.awt.Color Background -bg}
        }]
    }
    # -borderWidth  
    if {1} {
        append specialVars "
            int borderWidth = [defpar borderWidth $widget 0];
        "
    if {[lsearch "SMenuButton JButton SMenuButton JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem" $widget ] >= 0} {
            append specialMethods {

	        public void setMargin(Insets i) {
        		super.setMargin(i);
		        minimumSize = null;
       		 }

                public void setBorderWidth(double borderWidth) {
                   this.borderWidth = (int) borderWidth;
                   if (!(getBorder() instanceof SwkBorder)) {
                       int iBorder = (int) borderWidth;
                       setMargin(new Insets(iBorder,iBorder,iBorder,iBorder)); 
                   }
                } 
            }
        } elseif {[lsearch "JWindow JDialog JFrame" $widget] >= 0} {
            append specialMethods {
                public void setBorderWidth(double borderWidth) {
                   if (this.borderWidth != (int) borderWidth) {
                       this.borderWidth = (int) borderWidth;
                       Widgets.relayoutContainer(getContentPane());
                   }
                } 
            }
        } elseif {[lsearch "JPanel" $widget] >= 0} {
            append specialMethods {
                public void setBorderWidth(double borderWidth) {
                   if (!(getBorder() instanceof SwkBorder)) {
                      setBorder(new SwkBorder());
                   }
                   if (this.borderWidth != (int) borderWidth) {
                       this.borderWidth = (int) borderWidth;
                       Widgets.relayoutContainer(this);
                   }
               }
            }
        } else {
            append specialMethods {
                public void setBorderWidth(double borderWidth) {
                   if (this.borderWidth != (int) borderWidth) {
                       this.borderWidth = (int) borderWidth;
                   }
               }
            }
        }
        append specialMethods {
            public int getBorderWidth() {
                return(borderWidth);
            }
        }
        set specialGets [concat  $specialGets {
            {setBorderWidth tkSizeDI BorderWidth -borderwidth}
        }]
        set specialGets [concat  $specialGets {
            {setBorderWidth tkSizeDI BorderWidth -border}
        }]
    }
    # -bd
        set specialGets [concat  $specialGets {
            {setBorderWidth tkSizeDI BorderWidth -bd}
        }]
    
    
    
    
    # -bitmap
    if {[lsearch "SMenuButton JButton SMenuButton JMenu JMenuItem JLabel JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem" $widget ] >= 0} {
        append specialVars {
            String bitmap;
            String bitmapName = "";
        }
        
        append specialMethods {
            public void setBitmap(ImageIcon icon, String name) {
                setIcon(icon);
                bitmapName = name;
            }
            public String getBitmap() {
                return bitmapName;
            }
        }
        
        set specialGets [concat  $specialGets {
            {setBitmap bitmap Bitmap -bitmap}
        }]
    }
    
    # -cursor
    
    # -disabledForeground
    if {[lsearch "SMenuButton JButton JLabel JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SMenu SMenuButton JMenu" $widget ] >= 0} {
        append specialVars {
            Color disabledForeground;
        }
        
        append specialMethods "
        public void setDisabledForeground(Color disabledForeground) \{
            this.disabledForeground = disabledForeground;
        \}
        public Color getDisabledForeground() \{
            return(disabledForeground);
        \}
        "
        set specialGets [concat  $specialGets {
            {setDisabledForeground java.awt.Color DisabledForeground}
        }
        ]
    }
    # -exportselection
    if {[lsearch "JTextPane JTextField JList" $widget ] >= 0} {
        append specialVars {
            boolean exportSelection;
        }
        append specialMethods {
        public void setExportSelection(boolean exportSelection) {
            this.exportSelection = exportSelection;
        }
        public boolean getExportSelection() {
            return exportSelection;
            }
        }
        set specialGets [concat  $specialGets {
            {setExportSelection boolean ExportSelection}
        }
        ]
    }
    # -fg
    set vWidgets "JSlider JList JTextArea JTextField JLabel SLabel"
    if {1} {
        set specialGets [concat  $specialGets {
            {setForeground java.awt.Color Foreground -fg}
        }]
    }
    
    
    
    # -highlightBackground
    set vWidgets "JTextPane JSlider JList JPanel LabelFrame JFrame JScrollBar JTextArea JTextField JLabel SMenuButton JButton  JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SMenu JMenu SLabel SMenuButton Canvas"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            Color highlightBackground=Color.white;
        }
        append specialMethods "
        public void setHighlightBackground(Color highlightBackground) \{
            this.highlightBackground = highlightBackground;
        \}
        public Color getHighlightBackground() \{
            return(highlightBackground);
        \}
        "
        set specialGets [concat  $specialGets {
            {setHighlightBackground java.awt.Color HighlightBackground}
        }]
    }
    
    # -highlightColor
    set vWidgets "JTextPane JSlider JList JPanel LabelFrame JFrame JScrollBar JTextArea JTextField JLabel SMenuButton JButton  JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SLabel JMenu SMenu SMenuButton Canvas"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            Color highlightColor=Color.red;
        }
        append specialMethods "
        public void setHighlightColor(Color highlightColor) \{
            this.highlightColor = highlightColor;
        \}
        public Color getHighlightColor() \{
            return(highlightColor);
        \}
        "
        set specialGets [concat  $specialGets {
            {setHighlightColor java.awt.Color HighlightColor}
        }]
    }
    
    # -highlightThickness
    if {[lsearch "JSlider JPanel LabelFrame JFrame JList JScrollBar JTextArea JTextField JTextPane JLabel SMenuButton JButton  JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SLabel JMenu SMenu SMenuButton Canvas" $widget ] >= 0} {
        append specialVars {
            int highlightThickness;
        }
        
        append specialMethods "
        public void setHighlightThickness(int highlightThickness) \{
            this.highlightThickness = highlightThickness;
        \}
        public int getHighlightThickness() \{
            return(highlightThickness);
        \}
        "
        set specialGets [concat  $specialGets {
            {setHighlightThickness tkSize HighlightThickness}
        }]
    }
   
 
    
    if {[lsearch "JLabel JMenu SMenuButton JButton JCheckBox JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem" $widget] >= 0} {
        set specialGets [concat  $specialGets {
            {setIcon javax.swing.Icon Image -image}
        }]
    }
    # -jump
    
    if {[lsearch "JScrollBar" $widget ] >= 0} {
        append specialVars {
            boolean jump=false;
        }
        append specialMethods "
        public void setJump(boolean jump) \{
            this.jump = jump;
        \}
        public boolean getJump() \{
            return(jump);
        \}
        "
        set specialGets [concat  $specialGets {
            {setJump boolean Jump}
        }]
    }
    
    # -justify
    if {[lsearch "JTextField JLabel JButton JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem" $widget ] >= 0} {
        append specialVars {
            String justify="";
        }
        append specialMethods {
            public void setJustify(String justify) {
                this.justify = justify;
                if (SwkWidget.LEFT.startsWith(justify)) {
                    this.justify = SwkWidget.LEFT;
                } else if (SwkWidget.RIGHT.startsWith(justify)) {
                    this.justify = SwkWidget.RIGHT;
                } else if (SwkWidget.CENTER.startsWith(justify)) {
                    this.justify = SwkWidget.CENTER;
                } else {
                    this.justify = "";
                }
            }
            public String getJustify() {
                return(justify);
            }
        }
        set specialGets [concat  $specialGets {
            {setJustify justify Justify}
        }]
    }
    
    
    
    # -orient
    if {[lsearch "JScrollBar" $widget ] >= 0} {
        append specialMethods "
        public void setOrient(String orient) \{
            if (orient.startsWith(\"v\")) \{
                setOrientation(JScrollBar.VERTICAL);
                \} else \{
                setOrientation(JScrollBar.HORIZONTAL);
            \}
            
        \}
        public String getOrient() \{
            if (getOrientation() == JScrollBar.VERTICAL) \{
                return(\"vertical\");
                \} else \{
                return(\"horizontal\");
            \}
        \}
        "
        set specialGets [concat  $specialGets {
            {setOrient java.lang.String Orient}
        }]
    }

    # -orient
    if {[lsearch "JSplitPane" $widget ] >= 0} {
        append specialMethods "
        public void setOrient(String orient) \{
            if (orient.startsWith(\"v\")) \{
                setOrientation(JSplitPane.VERTICAL_SPLIT);
                \} else \{
                setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            \}

        \}
        public String getOrient() \{
            if (getOrientation() == JSplitPane.VERTICAL_SPLIT) \{
                return(\"vertical\");
                \} else \{
                return(\"horizontal\");
            \}
        \}
        "
        set specialGets [concat  $specialGets {
            {setOrient java.lang.String Orient}
        }]
    }


    # -padx
    if {[lsearch "JPanel LabelFrame JList JScrollBar JTextArea JTextField JLabel JButton  JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SLabel JMenu SMenu SMenuButton Canvas JTextPane" $widget ] >= 0} {
        append specialVars {
            int padx;
        }
        
        append specialMethods "
        public void setPadX(int padx) \{
            this.padx = (int) padx;
	    emptyBorderInsets.left = this.padx;
	    emptyBorderInsets.right = this.padx;
	    minimumSize = null;	
        \}
        public int getPadX() \{
            return(padx);
        \}
        "
        set specialGets [concat  $specialGets {
            {setPadX tkSize PadX}
        }]
    }
    
    # -pady
    if {[lsearch "JPanel LabelFrame JTextPane JList JScrollBar JTextArea JTextField JLabel JButton  JRadioButton JRadioButtonMenuItem JCheckBox JCheckBoxMenuItem  SLabel JMenu SMenu SMenuButton Canvas" $widget ] >= 0} {
        append specialVars {
            int pady;
        }
        
        append specialMethods "
        public void setPadY(int pady) \{
            this.pady =(int)  pady;
	    emptyBorderInsets.top = this.pady;
	    emptyBorderInsets.bottom = this.pady;
            minimumSize = null;
        \}
        public int getPadY() \{
            return(pady);
        \}
        "
        set specialGets [concat  $specialGets {
            {setPadY tkSize PadY}
        }]
    }
    
    # -relief
    set vWidgets "JInternalFrame LabelFrame JPanel LabelFrame JButton JMenu SMenuButton SMenu JCheckBox JCheckBoxMenuItem  JRadioButton JRadioButtonMenuItem
    JList JScrollBar JSlider JTextArea JTextField JTextPane JLabel SLabel Canvas"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars "
        String relief=[defpar relief $widget null];
        "
        append specialMethods {
            public void setRelief(String relief) {
                if (!(getBorder() instanceof SwkBorder)) {
                    setBorder(new SwkBorder());
                }
                this.relief = relief.intern();
            }
            public String getRelief() {
                if (relief == null) {
                    relief = "";
                }
                return(relief);
            }
        }
        set specialGets [concat  $specialGets {
            {setRelief tkRelief Relief}
        }]
        } elseif {$widget != "JFrame"} {
        append specialMethods "
        public String getRelief() \{
            return(\"\");
        \}
        "
    }
   # -relief
    set vWidgets "JFrame"
   
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars "
        String relief=[defpar relief $widget null];
        "
        append specialMethods {
            public void setRelief(String relief) {
                if (!(getRootPane().getBorder() instanceof SwkBorder)) {
                    getRootPane().setBorder(new SwkBorder());
                }
                this.relief = relief.intern();
            }
            public String getRelief() {
                if (relief == null) {
                    relief = "";
                }
                return(relief);
            }
        }
        set specialGets [concat  $specialGets {
            {setRelief tkRelief Relief}
        }]
    }

    # -repeatdelay
    if {[lsearch "JSlider" $widget ] >= 0} {
        append specialVars {
            int repeatDelay;
        }
        
        append specialMethods "
        public void setRepeatDelay(int repeatDelay) \{
            this.repeatDelay = repeatDelay;
        \}
        public int getRepeatDelay() \{
            return(repeatDelay);
        \}
        "
        set specialGets [concat  $specialGets {
            {setRepeatDelay int RepeatDelay}
        }]
    }
    
    # -repeatinterval
    if {[lsearch "JSlider" $widget ] >= 0} {
        append specialVars {
            int repeatInterval;
        }
        
        append specialMethods "
        public void setRepeatInterval(int repeatInterval) \{
            this.repeatInterval = repeatInterval;
        \}
        public int getRepeatInterval() \{
            return(repeatInterval);
        \}
        "
        set specialGets [concat  $specialGets {
            {setRepeatInterval int RepeatInterval}
        }]
    }
    
    
    
    
    # -selectBackground
    set vWidgets " JList"
    if {[lsearch $vWidgets $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setSelectionBackground java.awt.Color SelectionBackground -selectbackground}
        }]
    }
    # -selectBackground
    set vWidgets "JTextPane JTextField"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            Color selectBackground;
        }
        append specialMethods "
        public void setSelectBackground(Color selectBackground) \{
            this.selectBackground = selectBackground;
        \}
        public Color getSelectBackground() \{
            return(selectBackground);
        \}
        "
        set specialGets [concat  $specialGets {
            {setSelectBackground java.awt.Color SelectBackground}
        }]
    }
    # -selectBorderWidth
    if {[lsearch "JTextPane JTextField JList" $widget ] >= 0} {
        append specialVars {
            int selectBorderWidth;
        }
        
        append specialMethods "
        public void setSelectBorderWidth(int selectBorderWidth) \{
            this.selectBorderWidth = selectBorderWidth;
        \}
        public int getSelectBorderWidth() \{
            return(selectBorderWidth);
        \}
        "
        set specialGets [concat  $specialGets {
            {setSelectBorderWidth tkSize SelectBorderWidth}
        }]
    }
    # -selectForeground
    set vWidgets "JList"
    if {[lsearch $vWidgets $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setSelectionForeground java.awt.Color SelectionForeground -selectforeground}
        }]
    }
    # -selectForeground
    set vWidgets "JTextPane JTextField"
    if {[lsearch $vWidgets $widget ] >= 0} {
        set specialGets [concat  $specialGets {
            {setSelectionColor java.awt.Color SelectionForeground -selectforeground}
        }]
    }
    
    
    # -setgrid
    if {[lsearch "JList JTextPane" $widget ] >= 0} {
        append specialVars {
            boolean setGrid;
        }
        append specialMethods "
        public void setSetGrid(boolean setGrid) \{
            this.setGrid = setGrid;
        \}
        public boolean getSetGrid() \{
            return setGrid;
        \}
        "
        
        set specialGets [concat  $specialGets {
            {setSetGrid boolean SetGrid}
        }
        ]
    }
    
    # -takefocus
    set vWidgets "JTextPane JLabel JMenu SMenuButton JButton JCheckBox JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem JList JPanel LabelFrame JScrollBar JSlider JTextArea JTextField JLabel"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialMethods {
        public void setTakeFocus(final boolean takeFocus) {
               setFocusable(takeFocus);
        }
        public boolean getTakeFocus() {
            return isFocusable();
        }
        }
        set specialGets [concat  $specialGets {
            {setTakeFocus boolean TakeFocus}
        }]
    }
    
    # -tearoff
    set vWidgets "JMenu"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            boolean tearoff=false;
        }
        append specialMethods "
        public void setTearoff(boolean tearoff) \{
            this.tearoff = tearoff;
        \}
        public boolean getTearoff() \{
            return(tearoff);
        \}
        "
        set specialGets [concat  $specialGets {
            {setTearoff boolean Tearoff}
        }]
    }
    
    # -tearoffcommand
    set vWidgets "JMenu"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            String tearOffCommand=null;
        }
        append specialMethods "
        public void setTearOffCommand(String tearOffCommand) \{
            this.tearOffCommand = tearOffCommand.intern();
        \}
        public String getTearOffCommand() \{
            return(tearOffCommand);
        \}
        "
        set specialGets [concat  $specialGets {
            {setTearOffCommand java.lang.String TearOffCommand}
        }]
    }
    
    # -text
    # -textvariable
    
    # -troughcolor
    if {$widget == "JSlider JScrollBar"} {
        set specialGets [concat  $specialGets {
            {setBackground java.awt.Color Background -troughcolor}
        }]
    }
    
    
    
    # -underline
    set vWidgets "JLabel JMenu SMenuButton JButton JCheckBox JRadioButton JMenuItem JCheckBoxMenuItem JRadioButtonMenuItem"
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            int underline;
        }
        append specialMethods {
            public void setUnderline(int underline) {
                this.underline = underline;
            }
            public int getUnderline() {
                return(underline);
            }
        }
        set specialGets [concat  $specialGets {
            {setUnderline int Underline}
        }]
    }
    
    # -wraplength
    if {[lsearch "JLabel JMenu SMenuButton JButton JCheckBox JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem" $widget] >= 0} {
        append specialVars {
            int wrapLength;
        }
        
        append specialMethods "
        public void setWrapLength(int wrapLength) \{
            this.wrapLength = wrapLength;
	    minimumSize = null;
        \}
        public int getWrapLength() \{
            return(wrapLength);
        \}
        "
        set specialGets [concat  $specialGets {
            {setWrapLength tkSize WrapLength}
        }]
    }
    
    
    # -xscrollcommand
    set vWidgets "JList JTextArea JTextField JTextPane JLabel Canvas"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            String xScrollCommand=null;
        }
        append specialMethods "
        public void setXScrollCommand(String xScrollCommand) \{
            this.xScrollCommand = xScrollCommand.intern();
        \}
        public String getXScrollCommand() \{
            return(xScrollCommand);
        \}
        "
        set specialGets [concat  $specialGets {
            {setXScrollCommand java.lang.String XScrollCommand}
        }]
    }
    # -yscrollcommand
    set vWidgets "JList JTextArea JTextField JTextPane JLabel Canvas"
    
    if {[lsearch $vWidgets $widget ] >= 0} {
        append specialVars {
            String yScrollCommand=null;
        }
        append specialMethods "
        public void setYScrollCommand(String yScrollCommand) \{
            this.yScrollCommand = yScrollCommand.intern();
        \}
        public String getYScrollCommand() \{
            return(yScrollCommand);
        \}
        "
        set specialGets [concat  $specialGets {
            {setYScrollCommand java.lang.String YScrollCommand}
        }]
    }
    
    
    
    
    
    if {[lsearch "JTextField SLabel" $widget ] >= 0} {
        append specialVars {
            String textVariable="";
        }
        set specialGets [concat  $specialGets { {setTextVariable textvariable TextVariable -textvariable}}]
        append specialMethods "
     public void setTextVariable( String name) {
     }
     public void setTextVariable(Interp interp, String name) throws TclException {
            docListener.setVarName(name);
     }

        public String getTextVariable() \{
            return(docListener.getVarName());
        \}
        "
        append specialConfig "
        $widgetVar.docListener.setFromVar(interp);
        "
        set closeMethod {
            public void close() throws TclException {
                if ((docListener.getVarName() != null) && (docListener.getVarName().length() != 0)) {
                    interp.untraceVar(docListener.getVarName(),docListener,TCL.TRACE_WRITES| TCL.GLOBAL_ONLY);
                }
            }
        }
        
        
    }
    if {[lsearch "SMenuButton JLabel JButton JCheckBox JRadioButton" $widget ] >= 0} {
        append specialVars {
            String textVariable="";
        }
        append specialListeners { ,VarTrace,SwkTextVariable }
        
        set specialGets [concat  $specialGets { {setTextVariable textvariable TextVariable -textvariable}}]
        set specialGets [concat  $specialGets { {setSwkText java.lang.String Text -text}}]
        append specialMethods {
           public void setTextVariable(String name) {
           }
           public void setTextVariable(Interp interp,String name) throws TclException {
                 String text =  SwankUtil.setupTrace(interp,this, textVariable, name);
                 textVariable = name;
                 if (text != null) {
                    (new Setter((SwkWidget) this,OPT_TEXT)).exec(text);
                 }
           }

            public void setSwkText(String value)  {
                if ((value != null) && (textVariable != null) && !textVariable.equals ("")) {
                     BindEvent bEvent = new BindEvent(interp,textVariable,null,value);
                     interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
                }
                super.setText(value);
            }
            public String getSwkText()  {
                return(super.getText());
            }
            
            public String getTextVariable() {
                return(textVariable);
            }
            
            public void traceProc(Interp interp, String string1, String string2, int flags) throws TclException
            {
                TclObject tObj = interp.getVar(textVariable,TCL.GLOBAL_ONLY);
                final String s = tObj.toString();
                SwingUtilities.invokeLater(new Runnable() {
                      public void run()  {
                           setText(s);
                      }
                });
            }
        }
        set closeMethod {
            public void close() throws TclException {
                if ((textVariable != null) && (textVariable.length() != 0)) {
                    interp.untraceVar(textVariable,this,TCL.TRACE_WRITES| TCL.GLOBAL_ONLY);
                }
            }
        }
    }
    
    
    
    
    
    if {$widget == "SMenuButton"} {
        append specialVars "
        SwkActionListener actionListener=null;
        "
        append specialInits "
        actionListener = new SwkActionListener(interp,this);
        addActionListener (actionListener);
        "
        append specialMethods "
        public void setMenu(String name) \{
        actionListener.setMenu(name); \}
        public String getMenu() \{
            return(actionListener.getMenu());
        \}
        "
        lappend specialGets "setMenu java.lang.String Menu"
    }
    set widgets "JMenuItem JButton SMenuButton"
    if {[lsearch $widgets $widget] >= 0} {
        append specialVars "
        SwkCommandListener commandListener=null;
        "
        append specialInits "
        commandListener = new SwkCommandListener(interp,this);
        addActionListener (commandListener);
        "
        append specialMethods {
            public void setCommand(String name) {
                commandListener.setCommand(name); 
            }
            public String getCommand() {
                return(commandListener.getCommand());
            }
        }
        lappend specialGets "setCommand java.lang.String Command"
    }
    set widgets "JFrame JInternalFrame"
    if {[lsearch $widgets $widget] >= 0} {
        append specialInits {
            this.setSize(swkwidth,swkheight);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setTitle(name);
            SwkComponentListener compList = new SwkComponentListener (interp, (Component) this);
            ((Component) this).addComponentListener (compList);
            this.setComponentListener (compList);
            
            
        }
    }
    set widgets "JFrame"
    if {[lsearch $widgets $widget] >= 0} {
        append specialVars {
            String closeCommand="";
            SwkWindowListener swkWindowListener;
        }
        append specialInits {
            this.setVisible(false);
        addWindowListener(new WindowAdapter () {
            public void windowClosing(WindowEvent wEvent) {
                if (closeCommand.equals("")) {
                    if (getName().equals(".")) {
                        System.exit(0);
                    }
                    BindEvent bEvent = new BindEvent(interp,"destroy "+getName());
                    interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
                } else {
                     BindEvent bEvent = new BindEvent(interp,closeCommand);
                     interp.getNotifier().queueEvent(bEvent,TCL.QUEUE_TAIL);
                }
    

            }
        }
        );
        }
        append specialMethods {
             public void setCloseCommand(String command) {
                 closeCommand = command;
             }
             public void setWindowListener(final SwkWindowListener windowListener) {
                 this.swkWindowListener = windowListener;
             }
             public SwkWindowListener getWindowListener() {
                 return swkWindowListener;
             }
        }
        set specialVisible {
       boolean visible = true;
        for (int iArg=2;iArg<argv.length;iArg += 2) {
            if (argv[iArg].toString().equals("-visible") && (iArg < (argv.length-1))) {
               visible = TclBoolean.get(interp,argv[iArg+1]);
            }
        }
        if (visible) {
                final SwkJFrame swkjframe2 = swkjframe;
                try {
                      SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                  swkjframe2.setVisible(true);
                            }
                      });
                 } catch (InterruptedException iE) {
                 } catch (Exception  e) {
                 }
        }
    }
    }
    
    set widgets "JFileChooser"
    if {[lsearch $widgets $widget] >= 0} {
        append specialVars {
        SwkFileChooserListener fileChooserListener=null;
        String dialogParent="";
        }
        append specialInits {
        putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
        fileChooserListener = new SwkFileChooserListener(interp,this);
        addActionListener (fileChooserListener);
        }
        append specialMethods "
        public void setCommand(String name) \{
            fileChooserListener.setCommand(name);
        \}
        public String getCommand() \{
            return(fileChooserListener.getCommand());
        \}
        public void setDialogParent(String name) \{
            dialogParent = name;
        \}
        public String getDialogParent() \{
            return dialogParent;
        \}
        
        "
        lappend specialGets "setCommand java.lang.String Command"
        lappend specialGets "setDialogParent java.lang.String DialogParent"
    }
    set widgets "FileDialog"
    if {[lsearch $widgets $widget] >= 0} {
       append specialImports "import java.io.File;"
       append specialSuper {
        super(new Frame());
       }

       append specialMethods {
       public void setBorder(SwkBorder border) {
      }
      public SwkBorder getBorder() {
          return null;
      }
      public boolean isMultiSelectionEnabled() {
          return false;
      }
      public File getSelectedFile() {
          return new File(getFile());
      }
      public File[] getSelectedFiles() {
             File[] files = new File[1];
             files[0] = getSelectedFile();
          return files;
      }
      }
   } 
    
    set widgets "JRadioButton JRadioButtonMenuItem"
    if {[lsearch $widgets $widget] >= 0} {
            append specialVars "
            static Hashtable bgroupTable = new Hashtable();
            SwkRadioButtonListener commandListener=null;
            "
            append specialInits "
            commandListener = new SwkRadioButtonListener(interp,this);
            addActionListener (commandListener);
            "
        
        append specialMethods "
          public boolean setVarName(Interp interp,String name) throws TclException {
               boolean state = commandListener.setVarName(interp,name);
               return state;
           }
           public void setVarName(final boolean state) {
               setSelected(state);
           }
           public void setVarName(final String name) {
           }
        public void setCommand(String name) \{
        commandListener.setCommand(name); \}
        public String getCommand() \{
            return(commandListener.getCommand());
        \}
        public String getVarName() \{
            return(commandListener.getVarName());
        \}
        public void setValue(String name) \{
            commandListener.setValue(name);
        \}
        public String getValue() \{
            return(commandListener.getValue());
        \}
        "
        lappend specialGets "setCommand java.lang.String Command"
        lappend specialGets "setVarName bvariable VarName -variable"
        lappend specialGets "setValue java.lang.String Value -value"
        append specialConfig "
        $widgetVar.commandListener.setFromVar(interp);
        "
        set closeMethod {
            public void close() throws TclException {
                if ((getVarName() != null) && (getVarName().length() != 0)) {
                    interp.untraceVar(getVarName(),commandListener,TCL.TRACE_WRITES| TCL.GLOBAL_ONLY);
                }
            }
        }
    }
    set widgets "JCheckBox JCheckBoxMenuItem JComboBox JRadioButton JRadioButtonMenuItem JSlider"
    if {[lsearch $widgets $widget] >= 0} {
        append specialListeners { ,SwkVariable }
    }
    
    
    set widgets "JCheckBox JCheckBoxMenuItem "
    if {[lsearch $widgets $widget] >= 0} {
            append specialVars "
            SwkCheckButtonListener commandListener=null;
            "
            append specialInits "
            commandListener = new SwkCheckButtonListener(interp,this);
            addActionListener (commandListener);
            "
        
        
        append specialMethods "
           public void setVarName(final boolean state) {
               setSelected(state);
           }
           public void setVarName(final String name) {
           }
          public boolean setVarName(Interp interp,String name) throws TclException {
               boolean state = commandListener.setVarName(interp,name);
               return state;
           }
        public void setCommand(String name) \{
        commandListener.setCommand(name); \}
        public String getCommand() \{
            return(commandListener.getCommand());
        \}
        public String getVarName() \{
            return(commandListener.getVarName());
        \}
        public void setOnValue(String name) \{
            commandListener.setOnValue(name);
        \}
        public String getOnValue() \{
            return(commandListener.getOnValue());
        \}
        public void setOffValue(String name) \{
            commandListener.setOffValue(name);
        \}
        public String getOffValue() \{
            return(commandListener.getOffValue());
        \}
        "
        lappend specialGets "setCommand java.lang.String Command"
        lappend specialGets "setVarName bvariable VarName -variable"
        lappend specialGets "setOnValue java.lang.String Value -onvalue"
        lappend specialGets "setOffValue java.lang.String Value -offvalue"
        append specialConfig "
        $widgetVar.commandListener.setFromVar(interp);
        "
        set closeMethod {
            public void close() throws TclException {
                if ((getVarName() != null) && (getVarName().length() != 0)) {
                    interp.untraceVar(getVarName(),commandListener,TCL.TRACE_WRITES| TCL.GLOBAL_ONLY);
                }
            }
        }
    }
    set widgets "JComboBox "
    if {[lsearch $widgets $widget] >= 0} {
        append specialVars "
        SwkJComboBoxListener commandListener=null;
        "
        append specialInits {
            commandListener = new SwkJComboBoxListener(interp,this);
            addActionListener (commandListener);
            final JTextComponent editor = (JTextComponent) getEditor().getEditorComponent();
            editor.addKeyListener(new KeyAdapter () {
                public void keyReleased(KeyEvent kEvent) {
                    commandListener.keyReleased(editor,kEvent);
                } 
            }
            );
        }
        
        append specialMethods "
     public void setVarName( String name) {
     }
     public void setVarName(Interp interp, String name) throws TclException {
            commandListener.setVarName(interp,name);
     }

        public void setCommand(String name) \{
        commandListener.setCommand(name); \}
        public String getCommand() \{
            return(commandListener.getCommand());
        \}
        public String getVarName() \{
            return(commandListener.getVarName());
        \}
        public void setValue(String name) \{
            commandListener.setValue(name);
        \}
        public String getValue() \{
            return(commandListener.getValue());
        \}
        "
        lappend specialGets "setCommand java.lang.String Command"
        lappend specialGets "setVarName variable Variable -variable"
        lappend specialGets "setValue java.lang.String Value -value"
        #		append specialConfig "
        #			$widgetVar.commandListener.setFromVar(interp);
        #		"
        set closeMethod {
            public void close() throws TclException {
                if ((getVarName() != null) && (getVarName().length() != 0)) {
                    interp.untraceVar(getVarName(),commandListener,TCL.TRACE_WRITES| TCL.GLOBAL_ONLY);
                }
            }
        }
    }
    
    
    
    set widgets "JScrollBar"
    if {[lsearch $widgets $widget] >= 0} {
        append specialVars "
        SwkScrollAdjustmentListener adjustmentListener=null;
        "
        append specialInits "
        adjustmentListener = new SwkScrollAdjustmentListener(interp,this);
        addAdjustmentListener (adjustmentListener);
        "
        append specialMethods "
        public void setCommand(String name) \{
        adjustmentListener.setCommand(name); \}
        public String getCommand() \{
            return(adjustmentListener.getCommand());
        \}
        "
        lappend specialGets "setCommand java.lang.String Command"
    }
    
    
    if {$widget == "SMenu"} {
        append specialCmds "
        case OPT_ADD:
        SwankUtil.addmenu (interp, swksmenu, argv);
        break;
        "
        append specialVCmds {
            ,"add"
        }
        append specialOpts "
        static final private int OPT_ADD = 4;
        "
    }
    set widgets "JScrollPane"
    if {[lsearch $widgets $widget] >= 0} {
        append specialMethods {
            void setVScrollbar(final int policy) {
                setVerticalScrollBarPolicy(policy);
            }
            void setHScrollbar(final int policy ) {
                setHorizontalScrollBarPolicy(policy);
            }
            String getHScrollbar() {
                return "always";
            }
            String getVScrollbar() {
                return "always";
            }
        }
        lappend specialGets "setVScrollbar vscrollbar VScrollbar -vscrollbar"
        lappend specialGets "setHScrollbar hscrollbar HScrollbar -hscrollbar"
    }
    set iOpt 3
    if {$widget == "JList"} {
        source [file join swankgen widgets/SwkJListWidgetCmd.tcl]
    }
    if {$widget == "JScrollBar"} {
        source [file join swankgen widgets/SwkJScrollWidgetCmd.tcl]
    }
    if {$widget == "JPopupMenu"} {
        source [file join swankgen widgets/SwkJPopupMenuWidgetCmd.tcl]
    }
    if {$widget == "SMenu"} {
        source [file join swankgen widgets/SwkSMenuWidgetCmd.tcl]
    }
    if {$widget == "JButton"} {
        source [file join swankgen widgets/SwkJButtonWidgetCmd.tcl]
    }
    if {$widget == "JRadioButton"} {
        source [file join swankgen widgets/SwkJRadioWidgetCmd.tcl]
    }
    if {$widget == "JRadioButtonMenuItem"} {
        source [file join swankgen widgets/SwkJRadioMenuWidgetCmd.tcl]
    }
    if {$widget == "JCheckBox"} {
        source [file join swankgen widgets/SwkJCheckBoxWidgetCmd.tcl]
    }
    if {$widget == "JCheckBoxMenuItem"} {
        source [file join swankgen widgets/SwkJCheckMenuWidgetCmd.tcl]
        
    }
    if {$widget == "JTextArea"} {
        source [file join swankgen widgets/SwkJTextAreaWidgetCmd.tcl]
    }
    if {$widget == "JTextField"} {
        source [file join swankgen widgets/SwkJTextFieldWidgetCmd.tcl]
    }
    if {$widget == "JMenu"} {
        source [file join swankgen widgets/SwkJMenu.tcl]
    }
    if {$widget == "JLabel"} {
        source [file join swankgen widgets/SwkJLabelWidgetCmd.tcl]
    }
    if {$widget == "JTabbedPane"} {
        source [file join swankgen widgets/SwkJTabbedPane.tcl]
    }
    if {$widget == "JSplitPane"} {
        source [file join swankgen widgets/SwkJSplitPane.tcl]
    }
    if {$widget == "JTextPane"} {
        source [file join swankgen widgets/SwkJTextPaneWidgetCmd.tcl]
    }
    if {$widget == "JEditorPane"} {
        source [file join swankgen widgets/SwkJEditorPaneWidgetCmd.tcl]
    }
    if {$widget == "JScrollPane"} {
        source [file join swankgen widgets/SwkJScrollPaneWidgetCmd.tcl]
    }
    if {$widget == "JDesktopPane"} {
        source [file join swankgen widgets/SwkJDesktopPaneWidgetCmd.tcl]
    }
    
    if {$widget == "JTable"} {
        source [file join swankgen widgets/SwkJTableWidgetCmd.tcl]
    }
    if {$widget == "JTree"} {
        source [file join swankgen widgets/SwkJTreeWidgetCmd.tcl]
    }
    if {$widget == "Canvas"} {
        source [file join swankgen widgets/SwkCanvasWidgetCmd.tcl]
    }
    if {$widget == "JSlider"} {
        source [file join swankgen widgets/SwkJSlider.tcl]
    }
    if {$widget == "JInternalFrame"} { source [file join swankgen widgets/SwkJInternalFrame.tcl]
    }
    if {$widget == "JSpinner"} {
        source [file join swankgen widgets/SwkJSpinner.tcl]
    }
    
    
    if {[lsearch "JScrollBar" $widget] >= 0} {
        append specialVars {
            int swkwidth;
        }
        set specialGets [concat  $specialGets { {setSwkWidth int Width} } ]
        append specialMethods "
        public void setSwkWidth(int width) \{
            this.swkwidth = width;
        \}
        public int getSwkWidth() \{
            return(swkwidth);
        \}
        "
    }
    if {[lsearch "JList" $widget] >= 0} {
        append specialVars {
            int swkwidth;
        }
        set specialGets [concat  $specialGets { {setSwkWidth int Width} } ]
    }
    if {[lsearch "JScrollPane" $widget] >= 0} {
        append specialVars {
            int swkwidth=1;
            int swkheight=1;
        }
        set specialGets [concat  $specialGets { {setSwkWidth tkSize Width -width} } ]
        set specialGets [concat  $specialGets { {setSwkHeight tkSize Height -height} } ]
        append specialMethods "
        public void setSwkHeight(int height) {
            swkheight = height;
        }
        public int getSwkHeight() {
            return(swkheight);
        }
        public void setSwkWidth(int width) {
            swkwidth = width;
        }
        public int getSwkWidth() {
            return(swkwidth);
        }
        "
    }
    
    if {[lsearch "Canvas" $widget] >= 0} {
        append specialVars {
            int swkwidth=1;
            int swkheight=1;
        }
        set specialGets [concat  $specialGets { {setSwkWidth tkSize Width -width} } ]
        set specialGets [concat  $specialGets { {setSwkHeight tkSize Height -height} } ]
        append specialMethods {
            public Dimension getMinimumSize() {
            int scrollRegion[][] = getScrollRegion();
Dimension dSize = new Dimension(scrollRegion[1][0]-scrollRegion[0][0],scrollRegion[1][1]-scrollRegion[0][1]); 
                if (dSize.width < swkwidth) {
                    dSize.width = swkwidth;
                }
                if (dSize.height < swkheight) {
                    dSize.height = swkheight;
                }
                return(dSize);
            }
            public Dimension getPreferredSize() {
                return(getMinimumSize());
            }
            public void setSwkHeight(int height) {
                this.swkheight = height;
            }
            public int getSwkHeight() {
                Dimension size = getSize();
                return(size.height);
            }
            public void setSwkWidth(int width) {
                this.swkwidth = width;
            }
            public int getSwkWidth() {
                Dimension size = getSize();
                return(size.width);
            }
        }
        set closeMethod {
            public void close() throws TclException {
                 swkImageCanvas.close();
            }
        }
    }
    if {[lsearch "JFrame" $widget] >= 0} {
        append specialVars {
            String menu = "";
            boolean sizeConfigured=false;
            boolean isPacking = false;
        }
        
        set specialGets [concat  $specialGets { {setMenu menu Menu -menu} } ]
        append specialMethods {
            public void setMenu(Object menuObject)  {
                if (menuObject instanceof SwkJMenuBar) {
                    setJMenuBar((SwkJMenuBar) menuObject);
                }
            } 
            public String getMenu() {
                return(menu);
            }
            
        }
    }
    if {[lsearch "JDesktopPane JInternalFrame" $widget] >= 0} {
        append specialVars "
        int swkwidth = [defpar Width $widget 1];
        int swkheight = [defpar Height $widget 1];
        "
    }

    if {[lsearch "LabelFrame" $widget] >= 0} {
        append specialVars {
            String text="";
        }
        append specialMethods {
            public void setText(String text) {
                this.text = text.intern();
		if (!text.equals("")) {
                	setBorder(new javax.swing.border.TitledBorder(text));
                } else {
                   setBorder(new SwkBorder());
               }
            }
            public String getText() {
		return this.text;
            }
        }
        set specialGets [concat  $specialGets {{setText java.lang.String Text -text}}]
    }
    if {[lsearch "JPanel LabelFrame JFrame" $widget] >= 0} {
        append specialVars "
        int swkwidth = 0;
        int swkheight = 0;
        "
        set specialGets [concat  $specialGets {{setClassName java.lang.String ClassName -class}}]
        set specialGets [concat  $specialGets { {setSwkWidth tkSize Width -width} } ]
        set specialGets [concat  $specialGets { {setSwkHeight tkSize Height -height} } ]
        append specialMethods {
            public void setClassName(String className) {
                this.className = className.intern();
            }
        }

        if {[lsearch "JPanel LabelFrame" $widget] >= 0} {
            append specialMethods {
               public Dimension getMinimumSize() {
                    LayoutManager layout = getLayout();
                    Dimension dSize = layout.minimumLayoutSize(this);
                    boolean propagate = false;
                    
                    if (layout instanceof com.onemoonscientific.swank.PackerLayout) {
                        propagate = ((com.onemoonscientific.swank.PackerLayout) layout).propagate;
                        } else if (layout instanceof SwkGridBagLayout) {
                        propagate = ((SwkGridBagLayout) layout).propagate;
                    }
       if (!propagate) {
            if (swkwidth > 0) {
                int minWidth = swkwidth;
                if (dSize.width < minWidth) {
                    dSize.width = minWidth;
                }
            }

            if (swkheight > 0) {
                int minHeight = swkheight;
                if (dSize.height < minHeight) {
                    dSize.height = minHeight;
                }
            }
        }
                 return (dSize);
                }
   public Dimension getMaximumSize() {
       boolean propagate=true;
        LayoutManager layout = getLayout();
        boolean packed = layout instanceof com.onemoonscientific.swank.PackerLayout;
        boolean gridded = layout instanceof SwkGridBagLayout;

        if (gridded) {
            propagate = ((SwkGridBagLayout) layout).propagate;
       } else if (packed) {
            propagate = ((com.onemoonscientific.swank.PackerLayout) layout).propagate;
        } else {
        }
        int width = 4096;
        int height = 4096;
        if (!propagate) {
            if (swkwidth > 0) {
                width = swkwidth;
            }

            if (swkheight > 0) {
                height = swkheight;
            }
        }
        return new Dimension(width,height);
    }

             public Dimension getPreferredSize() {
                    Dimension minSize = getMinimumSize();
                    Dimension maxSize = getMaximumSize();
                    int width = minSize.width;
                    if (width > maxSize.width) {
                        width = maxSize.width;
                    }
                    int height = minSize.height;
                    if (height > maxSize.height) {
                        height = maxSize.height;
                    }
                    return new Dimension(width,height);
                }

                public void setSwkHeight(int height) {
                    if (swkheight != height) {
                        swkheight = height;
                        Widgets.relayoutContainer(this);
                    }
                }
                public int getSwkHeight() {
                    return swkheight;
                }
                public void setSwkWidth(int width) {
                    if (swkwidth != width) {
                        swkwidth = width;
                        Widgets.relayoutContainer(this);
                    }
                }
                public int getSwkWidth() {
                    return swkwidth;
                }
            }
            } else {
            append specialVars "
            Dimension geometry = new Dimension(0,0);
	    boolean geometryActive = false;
            "
            append specialMethods {
    public Dimension getMinimumSize() {
        LayoutManager layout = getLayout();
	Container c1 = getContentPane();
        LayoutManager layoutC = c1.getLayout();
        Dimension dSize = null;
        boolean packed = layoutC instanceof com.onemoonscientific.swank.PackerLayout;
        boolean gridded = layoutC instanceof SwkGridBagLayout;
        boolean propagate=true;
        Insets insets = getInsets();
        Dimension gSize  = null;
        if (geometryActive) {
            gSize = new Dimension(geometry);
            gSize.width += insets.left + insets.right;
            gSize.height += insets.top + insets.bottom;
        }
    
        if (gridded) {
            dSize = layoutC.minimumLayoutSize(c1);
            propagate = ((SwkGridBagLayout) layoutC).propagate;
            dSize.width += insets.left + insets.right;
            dSize.height += insets.top + insets.bottom;
            //dSize = layout.minimumLayoutSize(this);
       } else if (packed) {
            propagate = ((com.onemoonscientific.swank.PackerLayout) layoutC).propagate;
            dSize = layoutC.minimumLayoutSize(c1);
    
            dSize.width += insets.left + insets.right;
            dSize.height += insets.top + insets.bottom;
        } else {
            dSize = layout.minimumLayoutSize(this);
        }
        if (gSize != null) {
            if (gSize.width > dSize.width) {
                  dSize.width = gSize.width;
            }
    
            if (gSize.height > dSize.height) {
                dSize.height = gSize.height;
            }
    
        }
        sizeConfigured = true;
        if (!propagate) {
            if (swkwidth > 0) {
                int minWidth = swkwidth+insets.left+insets.right;
                if (dSize.width < minWidth) {
                    dSize.width = minWidth;
                }
            }

            if (swkheight > 0) {
                int minHeight = swkheight+insets.top+insets.bottom;
                if (dSize.height < minHeight) {
                    dSize.height = minHeight;
                }
            }
        }
       return (dSize);
    }
    public Dimension getMaximumSize() {
       boolean propagate=true;
        Insets insets = getInsets();
        LayoutManager layout = getLayout();
	Container c1 = getContentPane();
        LayoutManager layoutC = c1.getLayout();
        boolean packed = layoutC instanceof com.onemoonscientific.swank.PackerLayout;
        boolean gridded = layoutC instanceof SwkGridBagLayout;

        if (gridded) {
            propagate = ((SwkGridBagLayout) layoutC).propagate;
       } else if (packed) {
            propagate = ((com.onemoonscientific.swank.PackerLayout) layoutC).propagate;
        } else {
        }
        int width = 4096;
        int height = 4096;
        if (!propagate) {
            if (swkwidth > 0) {
                width = swkwidth+insets.left+insets.right;
            }

            if (swkheight > 0) {
                height = swkheight+insets.top+insets.bottom;
            }
        }
        return new Dimension(width,height);
    }

            
                public void setGeometry(int width, int height) {
                    geometry = new Dimension(width,height);
                    geometryActive = true;
                }
                public void setGeometryInactive() {
                    geometryActive = false;
                }
                
            
                public Dimension getPreferredSize() {
                    Dimension minSize = getMinimumSize();
                    Dimension maxSize = getMaximumSize();
                    int width = minSize.width;
                    if (width > maxSize.width) {
                        width = maxSize.width;
                    }
                    int height = minSize.height;
                    if (height > maxSize.height) {
                        height = maxSize.height;
                    }
                    return new Dimension(width,height);
                }
                
                public void setSwkHeight(int height) {
                    if (swkheight != height) {
                        swkheight = height;
                        Widgets.relayoutContainer(getContentPane());
                    }
                }
                public int getSwkHeight() {
                    return swkheight;
                }
                public void setSwkWidth(int width) {
                    if (swkwidth != width) {
                        swkwidth = width;
                        Widgets.relayoutContainer(getContentPane());
                    }
                }
                public int getSwkWidth() {
                    return swkwidth;
                }
 
            }
            
        }
    }
    if {[lsearch "JTextArea" $widget] >= 0} {
        append specialVars {
            int swkwidth;
            int swkheight;
        }
        set specialGets [concat  $specialGets { {setSwkWidth int Width -width} } ]
        set specialGets [concat  $specialGets { {setSwkHeight int Height -height} } ]
        append specialMethods "
        public void setSwkHeight(int height) \{
            this.swkheight = height;
            Dimension size = getSize();
            size.height = height;
            setSize(size);
            setPreferredSize(size);
        \}
        public int getSwkHeight() \{
            Dimension size = getSize();
            return(size.height);
        \}
        public void setSwkWidth(int width) \{
            this.swkwidth = width;
            Dimension size = getSize();
            size.width = width;
            setSize(size);
            setPreferredSize(size);
        \}
        public int getSwkWidth() \{
            Dimension size = getSize();
            return(size.width);
        \}
        "
    }
    if {[lsearch "JLabel JMenu SMenuButton JButton JCheckBox JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem" $widget] >= 0} {
        append specialVars {
            int swkwidth=1;
            int swkheight=1;
            private Insets insets  = new Insets(0,0,0,0);
        }
        if {[lsearch "JCheckBox JRadioButton JCheckBoxMenuItem JRadioButtonMenuItem" $widget] >= 0} {
            append specialVars {
                private int symbolSize = 24;
            }
            
            } else {
            append specialVars {
                private int symbolSize = 0;
            }
        }
        
        set specialGets [concat  $specialGets { {setSwkWidth int Width -width} } ]
        set specialGets [concat  $specialGets { {setSwkHeight int Height -height} } ]
        
        append specialMethods {
           

       public void setFont(Font font) {
            super.setFont(font);
           minimumSize = null;
        }
        public void setText(String s) {
            super.setText(s);
            minimumSize = null;
        }
        public void setIcon(Icon i) {
            super.setIcon(i);
           minimumSize = null;
        }
        
        public void setBorder(SwkBorder sb) {
            super.setBorder(sb);
            minimumSize = null;
        }

            public Dimension getPreferredSize() {
                Dimension size = getMinimumSize();
                return size;
            }
            
            public Dimension getMinimumSize() {
             if(minimumSize == null) {  
		 FontMetrics fontMetrics =  this.getFontMetrics(this.getFont());
		int charW = fontMetrics.charWidth('O');
                String s1 = getText();
		Dimension size = new Dimension(0,0);
                ImageIcon icon = (ImageIcon) getIcon();
                if ( (s1.length() > 0) || (icon == null)) {
                    size.height = (int) (swkheight*fontMetrics.getHeight()*1.2);
                    if ((s1.length() > swkwidth)) {
                        size.width = fontMetrics.stringWidth(s1)+charW;
                        if (wrapLength > (swkwidth*charW)) {
                            size.width = wrapLength+charW;
                            size.height = (int) (((fontMetrics.stringWidth(s1)/wrapLength)+1)*fontMetrics.getHeight()*1.1);
                            if (!s1.startsWith("<html>")) {
                                setText("<html>"+s1+"</html>");
                            }
                        }
                        } else {
                        size.width = (swkwidth)*charW;
                    }
                }
                if (icon != null) {
                    if (icon.getIconWidth() > swkwidth) {
                        size.width += icon.getIconWidth();
                        } else {
                        size.width += swkwidth;
                    }
                    if ((icon.getIconHeight() > swkheight) && (icon.getIconHeight() > size.height)) {
                        size.height = icon.getIconHeight();
                        } else if (icon.getIconHeight() > swkheight) {
                        size.height = icon.getIconHeight();
                        } else {
                        size.height = swkheight;
                    }
                }
                insets = getInsets(insets);
                size.height += insets.top+insets.bottom;
                size.width += insets.left+insets.right;
                size.width += symbolSize;
		minimumSize =size;
	   }	
                return new Dimension(minimumSize);
                
            }
            public void setSwkHeight(int height) {
                swkheight = height;
            }
            public int getSwkHeight() {
                return(swkheight);
            }
            public void setSwkWidth(int width) {
                swkwidth = width;
            }
            public int getSwkWidth() {
                return(swkwidth);
            }
            
            
        }
    }
    
    if {[lsearch "JTextField" $widget] >= 0} {
        set specialGets [concat  $specialGets { {setSwkWidth int Width} } ]
        append specialVars {
            private Insets insets  = new Insets(0,0,0,0);
        }
        append specialListeners { ,SwkTextVariable }
        append specialMethods {
            public void setText(String s) {
                super.setText(s);
                minimumSize = null;
            }
            public Dimension getPreferredScrollableViewportSize() {
                Dimension size = new Dimension();
                FontMetrics fontMetrics =  this.getFontMetrics(this.getFont());
		if(minimumSize == null) {
                    minimumSize = getMinimumSize();
                }
                size.height = minimumSize.height+2;
                size.width = getColumns()*fontMetrics.charWidth('O');
                return size;
            }
            public Dimension getPreferredSize() {
                Dimension size = getMinimumSize();
                return size;
            }
            public Dimension getMinimumSize() {
		if(minimumSize == null) {
                FontMetrics fontMetrics =  this.getFontMetrics(this.getFont());
                Dimension size = new Dimension();
                size.height = fontMetrics.getHeight()+2;
                size.width = getColumns()*fontMetrics.charWidth('O');
                int aWidth = fontMetrics.stringWidth(getText());
                if (aWidth > size.width) {
                   size.width = aWidth;
                }
                insets = getInsets(insets);
                size.height += insets.top + insets.bottom;
                size.width += insets.left+insets.right;
		minimumSize = size;
		}
                return new Dimension(minimumSize);
            }
            
            public void setSwkWidth(int width) {
                minimumSize = null;
                setColumns(width);
            }
            public int getSwkWidth() {
                return(getColumns());
            }
            
        }
    }
    if {[lsearch "JComboBox" $widget] >= 0} {
        append specialVars {
            int swkwidth=10;
        }
        set specialGets [concat  $specialGets { {setSwkWidth int Width} } ]
        append specialMethods {
            public Dimension getPreferredSize() {
                Dimension size = getMinimumSize();
                return size;
            }
            public Dimension getMinimumSize() {
                FontMetrics fontMetrics =  this.getFontMetrics(this.getFont());
                Dimension size = new Dimension();
                size.height = fontMetrics.getHeight()+4;
                size.width = swkwidth*fontMetrics.charWidth('O');
                return(size);
            }
            
            public void setSwkWidth(int width) {
                swkwidth=width;
            }
            public int getSwkWidth() {
                return(swkwidth);
            }
            
        }
    }
    
    if {[lsearch "JTextPane JEditorPane" $widget] >= 0} {
        append specialImports "import javax.swing.undo.*;"
        append specialVars {
            int swkwidth=80;
            int swkheight=24;
            UndoManager undoManager = null;
        }
        set specialGets [concat  $specialGets { {setSwkWidth int Width} } ]
        set specialGets [concat  $specialGets { {setSwkHeight int Height} } ]
        set specialGets [concat  $specialGets { {setUndo boolean Undo} } ]
        set specialGets [concat  $specialGets { {setMaxUndo int MaxUndo} } ]
        set specialGets [concat  $specialGets { {setAutoSeparators boolean AutoSeparators} } ]
        append specialMethods {
            public void setUndo(final boolean value) {
                if (value) {
                      undoManager = new UndoManager();
                      getDocument().addUndoableEditListener(undoManager);
                } else {
                      if (undoManager != null) {
                          getDocument().removeUndoableEditListener(undoManager);
                          undoManager = null;
                      }
                }
            }
            public boolean getUndo() {
                return undoManager != null;
            }
            public boolean getAutoSeparators() {
                 return false;
            }
            public void setAutoSeparators(final boolean value) {
            }
            public int getMaxUndo() {
                 int value = -1;
                 if (undoManager != null) {
                     value = undoManager.getLimit();
                 }
                 return value;
            }
            public void setMaxUndo(final int value) {
                 if (undoManager != null) {
                      undoManager.setLimit(value);
                 }
            }
            public Dimension getPreferredScrollableViewportSize() {
                FontMetrics fontMetrics =  this.getFontMetrics(this.getFont());
                Dimension size = new Dimension();
                size.height = swkheight*fontMetrics.getHeight();
                size.width = swkwidth*fontMetrics.charWidth('O');
                return(size);
            }
            public Dimension getPreferredSize() {
                Dimension size = getMinimumSize();
                return size;
            }
            public Dimension getMinimumSize() {
                FontMetrics fontMetrics =  this.getFontMetrics(this.getFont());
                Dimension size = new Dimension();
                size.height = swkheight*fontMetrics.getHeight();
                size.width = swkwidth*fontMetrics.charWidth('O');
                Dimension superSize = super.getMinimumSize();
                if (size.height < superSize.height) {
                    size.height = superSize.height;
                }
                if (size.width < superSize.width) {
                    size.width = superSize.width;
                }
                
                return(size);
            }
            
            public void setSwkWidth(int width) {
                swkwidth=width;
            }
            public int getSwkWidth() {
                return(swkwidth);
            }
            public void setSwkHeight(int height) {
                swkheight = height;
            }
            public int getSwkHeight() {
                return(swkheight);
            }
            
        }
    }
    if {[lsearch "" $widget] >= 0} {
        append specialMethods {
            public Dimension getPreferredSize() {
                Dimension size = getMinimumSize();
                return size;
            }
            public Dimension getMinimumSize() {
                FontMetrics fontMetrics =  this.getFontMetrics(this.getFont());
                Dimension size = new Dimension();
                size.height = swkheight*fontMetrics.getHeight();
                size.width = swkwidth*fontMetrics.charWidth('O');
                return(size);
            }
            
            
        }
    }
    if {[lsearch "JMenuBar" $widget] >= 0} {
        append specialVars {
            int swkwidth=1;
        }
        set specialGets [concat  $specialGets { {setSwkWidth int Width} } ]
        append specialMethods {
            public void setSwkWidth(int width) {
                swkwidth = width;
            }
            public int getSwkWidth() {
                return(swkwidth);
            }
            public Dimension getPreferredSize() {
                JMenu menu;
                Dimension size = new Dimension(0,0);
                Dimension mDim;
                for (int i=0;i<getMenuCount();i++) {
                    menu = getMenu(i);
                    mDim = menu.getPreferredSize();
                    size.width += mDim.width;
                    if (mDim.height > size.height) {
                        size.height = mDim.height;
                    }
                }
                if (size.width < swkwidth) {
                    size.width = swkwidth;
                }
                return(size);
            }
            public Dimension getMinimumSize() {
                JMenu menu;
                Dimension size = new Dimension(0,0);
                Dimension mDim;
                for (int i=0;i<getMenuCount();i++) {
                    menu = getMenu(i);
                    mDim = menu.getMinimumSize();
                    size.width += mDim.width;
                    if (mDim.height > size.height) {
                        size.height = mDim.height;
                    }
                }
                if (size.width < swkwidth) {
                    size.width = swkwidth;
                }
                return(size);
            }
            
        }
    }
    
    
    if {[lsearch "SLabel" $widget] >= 0} {
        set specialGets [concat  $specialGets { {setSwkWidth int Width -wraplength} } ]
        set specialGets [concat  $specialGets { {setSwkWidth int Width -width} } ]
        set specialGets [concat  $specialGets { {setSwkHeight int Height -height} } ]
        append specialMethods "
        public void setSwkWidth(int width) \{
            setColumns(width);
        \}
        public int getSwkWidth() \{
            return(getColumns());
        \}
        public void setSwkHeight(int height) \{
            setRows(height);
        \}
        public int getSwkHeight() \{
            return(getRows());
        \}
        "
    }
    if {[lsearch "JWindow JDialog JFrame" $widget] < 0} {
        
#        append specialInits {
#            setBorder(new SwkBorder());
#        }
        
    }
    
    append specialMethods {
        public boolean isCreated() {
            return created;
        }
        public void setCreated(boolean state) {
		created = state;
        }
	public int getMouseX() {
           return mouseX;
        }
	public int getMouseY() {
           return mouseY;
        }
    }
    append specialVars {
        SwkMouseListener mouseListener = null;
        SwkKeyListener keyListener = null;
        SwkKeyCommandListener keyCommandListener = null;
        SwkFocusListener focusListener = null;
        SwkComponentListener componentListener = null;
        SwkChangeListener swkChangeListener = null;
        SwkMouseMotionListener mouseMotionListener = null;
	int mouseX = 0;
	int mouseY = 0;
    }
    append specialInits {
        addFocusListener(new FocusAdapter () {
            public void focusGained(FocusEvent fEvent) {
		FocusCmd.setFocusWindow(getName());
            }
        }
        );
        addMouseListener(new MouseAdapter () {
            public void mouseClicked(MouseEvent mEvent) {
                mEvent.getComponent().requestFocus();
            }
        }
        );
        addMouseMotionListener(new MouseMotionAdapter () {
            public void mouseMoved(MouseEvent mEvent) {
		mouseX = mEvent.getX();
		mouseY = mEvent.getY();
            }
            public void mouseDragged(MouseEvent mEvent) {
		mouseX = mEvent.getX();
		mouseY = mEvent.getY();
            }
        }
        );
    }
    
    if {[lsearch "JTable" $widget] != -1} {
        append specialMethods {
            public SwkMouseListener getMouseListener() {
                return (mouseListener);
            }
            public void setMouseListener(SwkMouseListener mouseListener) {
                this.mouseListener = mouseListener;
                this.getTableHeader().setName(this.getName()+".Header");
                this.getTableHeader().addMouseListener(mouseListener);
            }
        }
        
        
        } else {
        append specialMethods {
            public SwkMouseListener getMouseListener() {
                return (mouseListener);
            }
            public void setMouseListener(SwkMouseListener mouseListener) {
                this.mouseListener = mouseListener;
            }
        }
    }
    
    append specialMethods {
        public SwkFocusListener getFocusListener() {
            return(focusListener);
        }
        public void setFocusListener(SwkFocusListener focusListener) {
            this.focusListener = focusListener;
        }
        public SwkComponentListener getComponentListener() {
            return(componentListener);
        }
        public void setComponentListener(SwkComponentListener componentListener) {
            this.componentListener = componentListener;
        }
        public SwkChangeListener getChangeListener() {
            return(swkChangeListener);
        }
        public void setChangeListener(SwkChangeListener swkChangeListener) {
            this.swkChangeListener = swkChangeListener;
        }
        
        public SwkKeyListener getKeyListener() {
            return(keyListener);
        }
        public void setKeyListener(SwkKeyListener keyListener) {
            this.keyListener = keyListener;
        }
        public SwkKeyCommandListener getKeyCommandListener() {
            return(keyCommandListener);
        }
        public void setKeyCommandListener(SwkKeyCommandListener keyCommandListener) {
            this.keyCommandListener = keyCommandListener;
        }
        
        public SwkMouseMotionListener getMouseMotionListener() {
            return(mouseMotionListener);
        }
        public void setMouseListener(SwkMouseMotionListener mouseMotionListener) {
            this.mouseMotionListener = mouseMotionListener;
        }
        
    }
    
    append specialMethods "
    $closeMethod
    "
       set newGets {}
        foreach gets $specialGets {
            if {[llength $gets] == 3} {
                   lappend gets {}
            }
            lappend gets {}
            lappend newGets $gets
        }
        set specialGets $newGets
}
