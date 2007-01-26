# jdesktop.tcl --
#
# This demonstration script creates a jdesktop widget with an internal frame
#

if {![info exists widgetDemo]} {
    error "This script should be run from the \"widget\" demo."
}

set w .jdesktop
catch {destroy $w}
toplevel $w
wm title $w "JDesktopPane Demo"
wm iconname $w "JDesktopPane"
positionWindow $w
wm geometry $w 500x500
set jd $w.jd

label $w.msg -font $font -wraplength 4i -justify left -text "This window displays a JDesktopPane with internal frame."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

jdesktoppane $jd
pack $jd -fill both -expand y
set iframe $jd.iframe
jinternalframe $iframe
$jd add $iframe
$iframe configure -maximizable 1
$iframe configure -iconifiable 1
$iframe configure -resizable 1
$iframe configure -size "300 300"
canvas $iframe.c
pack $iframe.c -fill both -expand y
$iframe.c create text 50 50 -text Hello\nWorld

