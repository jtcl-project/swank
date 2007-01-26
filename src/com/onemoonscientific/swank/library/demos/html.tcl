# html.tcl --
#
# This demonstration script creates a html widget with some controls to browse web
#
# RCS: @(#) $Id: html.tcl,v 1.1 2004/08/20 00:04:50 bruce_johnson Exp $

if {![info exists widgetDemo]} {
    error "This script should be run from the \"widget\" demo."
}

set w .html
catch {destroy $w}
toplevel $w
wm title $w "HTML Viewer Demonstration"
wm iconname $w "HTML"
positionWindow $w
wm geometry $w 500x500
set hWin $w.hWin

label $w.msg -font $font -wraplength 4i -justify left -text "This window displays an HTML widget."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

::swank::htmlViewer::makeWin $hWin
