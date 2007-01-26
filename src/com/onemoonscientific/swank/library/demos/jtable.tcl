# jtable.tcl --
#
# This demonstration script creates a jtable widget 
#

if {![info exists widgetDemo]} {
    error "This script should be run from the \"widget\" demo."
}

set w .jtable
catch {destroy $w}
toplevel $w
wm title $w "JTable Demo"
wm iconname $w "JTable"
positionWindow $w
wm geometry $w 500x500
set jt $w.jt
set js $w.js

label $w.msg -font $font -wraplength 4i -justify left -text "This window displays a JTable."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

set widgets [::swank::help widgets]
set nCols [expr {[llength $widgets]+1}]
jscrollpane $js
set iCol 1
set widgetHelp(-1,0) "Commands/Options"
foreach widget $widgets {
    set widgetHelp(-1,$iCol) $widget
    set widgetCol($widget) $iCol
    incr iCol
    foreach cmd [$widget help commands] {
           lappend widgetCmds($cmd) $widget
    }
    foreach opt [$widget help options] {
           lappend widgetOpts($opt) $widget
    }
}

set cmdList  [lsort -dictionary [array names widgetCmds]]
set iRow 0
foreach cmd $cmdList {
     set widgetHelp($iRow,0) $cmd
     foreach widget $widgetCmds($cmd) {
          set iCol $widgetCol($widget)
          set widgetHelp($iRow,$iCol) $cmd
     }
     incr iRow
}
set optList  [lsort -dictionary [array names widgetOpts]]
foreach opt $optList {
     set widgetHelp($iRow,0) $opt
     foreach widget $widgetOpts($opt) {
          set iCol $widgetCol($widget)
          set widgetHelp($iRow,$iCol) $opt
     }
     incr iRow
}
jtable $jt -variable widgetHelp -cols $nCols -rows $iRow -autoresizemode 0
pack $js -fill both -expand y
$js add $jt
