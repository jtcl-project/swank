package require java
source [file join swankgen swkSpecial.tcl]
source [file join swankgen swkMakeSetMethods.tcl]
source [file join swankgen swkMakeGetMethods.tcl]
source [file join swankgen swkMakeAddMethods.tcl]
source [file join swankgen swkResources.tcl]
source [file join swankgen setter.tcl]


set tkcomps {
}
set swkRoot [file join gensrc com onemoonscientific swank]
set specialImports ""
set specialVars ""
set widgets { JButton {SMenuButton JButton} JLabel JCheckBox JCheckBoxMenuItem 
		    JComboBox JDialog JFrame JLabel JList
		    JMenu JMenuBar JMenuItem JOptionPane JPanel {LabelFrame JPanel} JPopupMenu {SMenu JPopupMenu} JProgressBar JRadioButton
		    JRadioButtonMenuItem JScrollBar JScrollPane {JSlider JPanel JSlider} JSplitPane
		    JTabbedPane JTable JTextArea JTextField JPasswordField JTextPane JToggleButton JToolBar
		    JEditorPane JTree JWindow {Canvas JPanel} JInternalFrame JDesktopPane JFileChooser JColorChooser FileDialog}

proc makeWidget {f1 type widgetType widget} {
    global specialImports
    global specialInits
    global specialVars
    global specialMethods
    global specialListeners
    global configureBody
    global getBody
    global addBody
    global subdir
    global resources
    global configOptions
    global configOPTs
    global configCASEs
    global setterCASEs
    global getOptions
    global getOPTs
    global getCASEs
    source [file join swankgen ${type}.java]
    set widgetVar [string tolower $widgetType]
    puts $f1 [subst -nobackslashes -nocommand $body]
}



proc swkClean {swkHome widgets} {
	foreach file [glob -nocomplain [file join $swkHome *.class]] {
		file delete $file
	}
	foreach file [glob -nocomplain [file join $swkHome print *.class]] {
		file delete $file
	}
	foreach file [glob -nocomplain [file join $swkHome canvas *.class]] {
		file delete $file
	}
	foreach compWidget $widgets {
		set widget [lindex $compWidget 0]
		set widgetType Swk$widget
		catch "file delete [file join $swkHome ${widgetType}.java]"
		catch "file delete [file join $swkHome ${widgetType}Cmd.java]"
	}
}

#set widgets.disabled {JFileChooser}
if {$argc==1} {
	if {$argv == "clean"} {
		swkClean $swkRoot $widgets
		exit 0
	} else {
		set widgets $argv
	}
}
if {[info exists widgetList]} {
	set widgets $widgetList
}
foreach widgetArg $widgets {
         puts $widgetArg
	if {[string match *Canvas* $widgetArg]} {
	    set swkHome [file join ${swkRoot} canvas]
	} else {
	    set swkHome $swkRoot
	}
	
	set widget [lindex $widgetArg 0]
	if {[llength $widgetArg] > 1} {
		set widgetClasses [lrange $widgetArg 1 end]
	} else {
		set widgetClasses [lindex $widgetArg 0]
	}
	set widgetType Swk$widget
	set  widgetVar [string tolower $widgetType]
	set simpleWidget $widget
        set methods ""
        set i 0
        foreach widgetClass $widgetClasses {
	    if {[string match *Document* $widgetClass]} {
		set widget javax.swing.text.$widgetClass
	    } elseif {[string match FileDialog $widgetClass]} {
		set widget java.awt.$widgetClass
	    } else {
		set widget javax.swing.$widgetClass
	    }
            if {[string match *Canvas* $widgetType]} {
                set subdir .canvas
            } else {
                set subdir ""
            }
            set components {
                java.awt.Color
                java.awt.Rectangle
                java.awt.Dimension
                java.awt.Point
                java.awt.Insets
                java.awt.Font
                java.util.Locale
                java.awt.Cursor
                javax.swing.text.StyledDocument
            }
            if {$i == 0} {
                 set mainWidget $widget
            }
            if {$i == 0} {
                lappend methods {}
            } else {
                lappend methods $widgetClass 
            }
            lappend methods [java::info methods $widget]
            incr i
        }
        swkMakeSpecial $simpleWidget $widgetVar
        set configureBody [swkMakeSetMethods $methods]
        set getBody [swkMakeGetMethods $methods]
        set addBody [swkMakeAddMethods $methods]
        
        set resources [swkMakeResources]
        
        set types {SwkWidgetConfigure}
        set types {SwkWidget SwkWidgetCmd}
        foreach type $types {
	    set file $widgetType[string range $type 9 end].java
	    set fullPath [file join $swkHome $file]
	    set f1 [open $fullPath w]
	    makeWidget $f1 $type $widgetType $mainWidget
	    close $f1
        }
}
