# plot.tcl --
#
# This demonstration script creates a canvas widget showing a 2-D
# plot with data points that can be dragged with the mouse.
#
# RCS: @(#) $Id: swkcanvas.tcl,v 1.1 2004/08/20 00:04:53 bruce_johnson Exp $

if {![info exists widgetDemo]} {
    error "This script should be run from the \"widget\" demo."
}

set w .plot
catch {destroy $w}
toplevel $w
wm title $w "Plot Demonstration"
wm iconname $w "Plot"
positionWindow $w
set c $w.c

label $w.msg -font $font -wraplength 4i -justify left -text "This window displays a canvas widget containing some of the items that the Swank canvas can display ."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

canvas $c -relief raised -width 600 -height 500
pack $w.c -side top -fill x

$c config -bg salmon
set x1 20
set y1 20
set x2 100
set y2 100
set delta 100
set border 30

set arcParams { -fill orange -outline black -style pieslice }
set arcText   { -width 1 -outline yellow -text "Arcs" -font "Courier 16 bold" -anchor s}
set arcCoords {$x1 $y1 $x2 $y2}
set rectParams {-fill orange -outline black}
set rectText   { -anchor s -width 1 -outline yellow -text "Rectangles" -font "Courier 16 bold"}
set rectCoords {$x1 $y1 $x2 $y2}
set ovalParams {-gradient "$x1 $y1 red $x2 $y2 green" -outline black}
set ovalText   { -anchor s -width 1 -outline yellow -text "Ovals" -font "Courier 16 bold"}
set ovalCoords {$x1 $y1 $x2 $y2}
set lineParams { -outline red -width 2 -dash "4 4"}
set lineText {-anchor s -width 1 -outline yellow -text "Lines" -font "Courier 16 bold"}
set lineCoords {$x1 $y1 [expr ($x1+$x2)/2] $y2 $x2 $y1}
set polygonParams {-width 1 -outline black -fill green}
set polygonText {-anchor s -width 1 -outline yellow -text "Polygons" -font "Courier 16 bold"}
set polygonCoords {$x1 $y1 $x2 $y2 $x2 $y1}
set textParams { -width 1 -outline yellow }
set imageParams {-image "starfishB.gif starfishB" }
set imageText  {-anchor s -width 1 -outline yellow -font "Courier 16 bold" -text "Images"}
set imageCoords {$x1 $y1}
set textureParams { -texture "starfish.gif starfish" -outline yellow}
set textureText {-anchor s -width 1 -outline yellow -text "Textures" -font "Courier 20 bold"}

set i 0
foreach shape "arc rect oval line polygon image" {
    puts $shape
    set coords [set ${shape}Coords]
    set coords [subst $coords]
    set item [eval $c create $shape  $coords [set ${shape}Params] ]
    $c itemconfig $item -tags $shape
    set item [eval $c create text [expr ($x1+$x2)/2] $y1 [set ${shape}Text]]
    set x1 [expr $x1+$delta+$border]
    set x2 [expr $x1+$delta]
    incr i
    if {$i==4} {
        set i 0
        set x1 $border
        set x2 [expr $x1+$delta]
        set y1 [expr $y1+$delta+$border]
        set y2 [expr $y1+$delta]
    }
    
}


set yy1 $y1
foreach font "6 7 9 12 15 19 24 30" {
    set text [eval $c create text $x1 $yy1 $textParams -text Text$font -font \"Courier $font\" -tags text]
    set yy1 [expr $yy1+$font+1]
}
set x1 $border
set x2 [expr $x1+3*$delta]
set y1 [expr $y1+$delta+$border]
set y2 [expr $y1+2*$delta]


puts texture
set ellipse2 [eval $c create oval $x1 $y1 $x2 $y2 $textureParams -tags toval]
set text [eval $c create text [expr ($x1+$x2)/2] $y1 $textureText]
set x1 [expr $x1+3*$delta+$border]
set x2 [expr $x1+$delta]

puts angleText

foreach angle "0 -15 -30 -45 -60 -75 -90" {
    set text [$c create text $x1 $y2 -width 1 -outline black  -font "Courier 20" -text "       Text$angle" -rotate $angle -anchor w]
}




