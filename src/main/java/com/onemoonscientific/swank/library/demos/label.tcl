# label.tcl --
#
# This demonstration script creates a toplevel window containing
# several label widgets.
#
# RCS: @(#) $Id: label.tcl,v 1.1 2004/08/20 00:04:51 bruce_johnson Exp $

if {![info exists widgetDemo]} {
    error "This script should be run from the \"widget\" demo."
}

set w .label
catch {destroy $w}
toplevel $w
wm title $w "Label Demonstration"
wm iconname $w "label"
positionWindow $w

label $w.msg -font $font -wraplength 4i -justify left -text "Five labels are displayed below: three textual ones on the left, and a bitmap label and a text label on the right.  Labels are pretty boring because you can't do anything with them."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

set f $w
frame $f.left
frame $f.right
pack $f.left $f.right -side left   -padx 10 -pady 10 -fill both

label $f.left.l1 -text "First label"
label $f.left.l2 -text "Second label, raised" -relief raised -bd 2
label $f.left.l3 -text "Third label, sunken" -relief sunken -bd 2
pack $f.left.l1 $f.left.l2 $f.left.l3 -side top -expand yes -pady 2 -anchor w 
image create photo bruce -file [file join $tk_library demos images bruce2.gif]
label $f.right.bitmap -borderwidth 2 -relief sunken -image bruce
label $f.right.caption -text "Swank Proprietor"
pack  $f.right.bitmap $f.right.caption -side top -expand yes -fill x
