# states.tcl --
#
# This demonstration script creates a listbox widget that displays
# the names of the 50 states in the United States of America.
#
# RCS: @(#) $Id: states.tcl,v 1.1 2004/08/20 00:04:53 bruce_johnson Exp $

if {![info exists widgetDemo]} {
    #error "This script should be run from the \"widget\" demo."
}

set w .states
catch {destroy $w}
toplevel $w
wm title $w "Listbox Demonstration (50 states)"
wm iconname $w "states"
positionWindow $w

label $w.msg -font $font -wraplength 4i  -text "A listbox containing the 50 states is displayed below, along with a scrollbar.  You can scan the list either using the scrollbar or by scanning.  To scan, press button 2 in the widget and drag up or down."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

frame $w.frame -borderwidth .5c
pack $w.frame -side top -expand yes -fill both

#scrollbar $w.frame.scroll -command "$w.frame.list yview"
jscrollpane $w.frame.scroll
listbox $w.frame.list -height 6 -width 30
$w.frame.scroll add $w.frame.list
#pack $w.frame.scroll -side right -fill y
pack $w.frame.scroll -side left -expand 1 -fill both

$w.frame.list insert 0 Alabama Alaska Arizona Arkansas California \
    Colorado Connecticut Delaware Florida Georgia Hawaii Idaho Illinois \
    Indiana Iowa Kansas Kentucky Louisiana Maine Maryland \
    Massachusetts Michigan Minnesota Mississippi Missouri \
 Montana Nebraska Nevada "New Hampshire" "New Jersey" "New Mexico" \
    "New York" "North Carolina" "North Dakota" \
    Ohio Oklahoma Oregon Pennsylvania "Rhode Island" \
    "South Carolina" "South Dakota" \
    Tennessee Texas Utah Vermont Virginia Washington \
    "West Virginia" Wisconsin Wyoming
