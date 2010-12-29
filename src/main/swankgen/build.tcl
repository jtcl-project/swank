package require java
cd [file join src main]
source [file join swankgen swkSpecial.tcl]
source [file join swankgen swkMakeSetMethods.tcl]
source [file join swankgen swkMakeGetMethods.tcl]
source [file join swankgen swkMakeAddMethods.tcl]
source [file join swankgen swkResources.tcl]
source [file join swankgen setter.tcl]


set tkcomps {
}
set swkRoot [file join java com onemoonscientific swank]
set specialImports ""
set specialVars ""
set widgets { JButton {SMenuButton JButton} JCheckBox JCheckBoxMenuItem 
		    JComboBox JDialog JFrame JLabel JList
		    JMenu JMenuBar JMenuItem JOptionPane JPanel {LabelFrame JPanel} JPopupMenu {SMenu JPopupMenu} JProgressBar JRadioButton
		    JRadioButtonMenuItem JScrollBar JScrollPane {JSlider JPanel JSlider} JSplitPane JSpinner
		    JTabbedPane JTable JTextArea JTextField JPasswordField JTextPane JToggleButton JToolBar
		    JEditorPane JTree JWindow {Canvas JPanel} JInternalFrame JDesktopPane JFileChooser JColorChooser FileDialog}
array set widgetMap {
JButton Button
SMenuButton Menubutton
JLabel Label
JCheckBox Checkbutton
JCheckBoxMenuItem Checkbutton
JComboBox JCombobox
JDialog JDialog
JFrame Toplevel
JLabel Label
JList List
JMenu Menu
JMenuBar Menubar
JMenuItem Menuitem
JOptionPane JOptionpane
JPanel Frame
LabelFrame Labelframe
JPopupMenu Popupmenu
SMenu  Menu
JProgressBar JProgressbar
JRadioButton Radiobutton
JRadioButtonMenuItem Radiobutton
JScrollBar Scrollbar
JScrollPane JScrollpane
JSlider Scale
JSplitPane Panedwindow
JSpinner Spinbox
JTabbedPane JTabbedpane
JTable JTable
JTextArea  Message
JTextField Entry
JPasswordField JPasswordfield
JTextPane Text
JToggleButton JTogglebutton
JToolBar JToolbar
JEditorPane JEditorpane
JTree JTree
JWindow JWindow
Canvas  Canvas
JInternalFrame JInternalframe
JDesktopPane JDesktoppane
JFileChooser JFilechooser
JColorChooser JColorchooser
FileDialog Filedialog
}
proc checkStatus {swkHome widgets} {
       set files [glob [file join swankgen *.tcl]]
       set files [concat $files [glob [file join swankgen *.java]]]
       set files [concat $files [glob [file join swankgen widgets *.tcl]]]
       set scriptTime 0
       foreach file $files {
            set mtime [file mtime $file]
            if {$mtime > $scriptTime} {
                set scriptTime $mtime
            }
       }
	foreach compWidget $widgets {
		set widget [lindex $compWidget 0]
		set widgetType Swk$widget
                if {[string match *Canvas* $widgetType]} {
                    set subdir canvas
                } else {
                    set subdir ""
                }
                foreach type {"" Cmd} {
		    set file [file join $swkHome $subdir ${widgetType}$type.java]
                    if {![file exists $file]} {
puts "Regenerate files as Java file $file missing"
                        return 0
                    }
                    set mtime [file mtime $file]
                    if {$mtime < $scriptTime} {
puts "Regenerate files as swankgen scripts updated"
                        return 0
                    }
                }
	}
        return 1
}
proc makeWidget {f1 type widgetType widget} {
    global specialImports
    global specialInits
    global specialSuper
    global specialVars
    global specialMethods
    global specialListeners
    global specialVisible
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
    global specialPrints
    global widgetClass
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
set status [checkStatus $swkRoot $widgets]
if {$status} {
    exit 0
}
foreach widgetArg $widgets {
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
        set specialPrints {paint(g2);}
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
            set widgetClass $widgetMap($simpleWidget)
	    makeWidget $f1 $type $widgetType $mainWidget
	    close $f1
        }
}
exit 0
