# items.tcl --
#
# This demonstration script creates a canvas that displays the
# canvas item types.
#
# RCS: @(#) $Id: swkitems.tcl,v 1.1 2004/08/20 00:04:53 bruce_johnson Exp $

if {![info exists widgetDemo]} {
    error "This script should be run from the \"widget\" demo."
}

set w .swkitems
catch {destroy $w}
toplevel $w
wm title $w "Canvas Item Demonstration"
wm iconname $w "Items"
positionWindow $w
set c $w.frame.c

label $w.msg -font $font -wraplength 5i -justify left -text "This window contains a canvas widget with effects supported by the Swank canvas.  The following operations are supported:\n  Button-1 drag:\tmoves item under pointer.\n  Button-2 drag:\trepositions view.\n  Button-3 drag:\tstrokes out area.\n  Ctrl+f:\t\tprints items under area."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

frame $w.frame
pack $w.frame -side top -fill both -expand yes

frame $w.frame.bbar
pack $w.frame.bbar -fill x -side top
#scale $w.frame.bbar.scale -sliderlength 200 -orient horizontal -from 0.20 -to 3.00 -value 1.0 -resolution 0.05 -showvalue 1  -command "$c zoom"
set zoom 1.0
scale $w.frame.bbar.scale -sliderlength 200 -orient horizontal -from 0.20 -to 3.00 -value 1.0 -resolution 0.05 -showvalue 1 -variable zoom -label Zoom
pack $w.frame.bbar.scale -side left

jscrollpane $w.frame.scroll
canvas $c -scrollregion {0c 0c 30c 16c} -width 15c -height 10c -relief sunken -borderwidth 2
$w.frame.scroll add $c
# FIXME following revalidate and repaint are kludgy and shouldn't be necessary
# they're used at present to get the scrollpane to refresh after zooming canvas
#bind $w.frame.bbar.scale <ButtonRelease-1> "$c zoom \$zoom;\[$c object\] revalidate;\[$w.frame.scroll object\] repaint"
#bind $w.frame.bbar.scale <ButtonRelease-1> "$c zoom \$zoom;\[$w.frame.scroll object\] repaint"
bind $w.frame.bbar.scale <ButtonRelease-1> "$c zoom \$zoom"
#scrollbar $w.frame.vscroll -command "$c yview"
#scrollbar $w.frame.hscroll -orient horiz -command "$c xview"

pack $w.frame.scroll -fill both -expand yes
#grid $w.frame.scroll -in $w.frame -row 0 -column 0 -rowspan 1 -columnspan 1 -sticky news
#grid $w.frame.vscroll -row 0 -column 1 -rowspan 1 -columnspan 1 -sticky news
#grid $w.frame.hscroll -row 1 -column 0 -rowspan 1 -columnspan 1 -sticky news
#grid rowconfig    $w.frame 0 -weight 1 -minsize 0
#grid columnconfig $w.frame 0 -weight 1 -minsize 0

# Display a 3x3 rectangular grid.

$c create rect 0c 0c 30c 16c -width 2
$c create line 0c 8c 30c 8c -width 2
$c create line 10c 0c 10c 16c -width 2
$c create line 20c 0c 20c 16c -width 2

set font1 {Helvetica 12}
set font2 {Helvetica 24 bold}
if {[winfo depth $c] > 1} {
    set blue DeepSkyBlue3
    set red red
    set bisque bisque3
    set green SeaGreen3
} else {
    set blue black
    set red black
    set bisque black
    set green black
}

# Set up demos within each of the areas of the grid.

$c create text 5c .2c -text Textures -anchor n
set texture @[file join $tk_library demos starfish.gif]
$c create rectangle 1c 1c 9c 7.0c -outline black -width 2 -tags item  -texture $texture

$c create text 15c .2c -text "Transparency" -anchor n
$c create rect 12c 1c 18c 7c -fill "0 0 0" -outline {red} 
$c create oval 13.5c 1c 16.5c 7c -fill "255 0 0" -outline {red}
$c create oval 12c 2.5c 18c 5.5c -fill "0 255 0 130" -outline {red} 

$c create text 25c .2c -text Shear -anchor n
$c create rectangle 21c 1c 23.5c 3.5c -fill red -outline black -width 2 -tags item -shear "0.0 0.0"
$c create rectangle 21c 4.5c 23.5c 7c -fill green -outline black -width 2 -tags item -shear "0.2 0.0"
$c create rectangle 26.5c 1c 29c 3.5c -fill blue -outline black -width 2 -tags item -shear "0.0 0.2"
$c create rectangle 26.5c 4.5c 29c 7c -fill yellow -outline black -width 2 -tags item -shear "0.2 0.2"

$c create text 5c 8.2c -text Rotate -anchor n
foreach rotate {0 30 60 90 120 150} {
    $c create oval 4.5c 10c 5.5c 15c -outline {red} -rotate $rotate
}

$c create text 15c 8.2c -text Gradients -anchor n
$c create rectangle 11c 9.5c 19c 12.0c -outline black -width 2 -tags item  -gradient "0.3 0.5  red 0.7 0.5  green 0"
$c create rectangle 11c 12.5c 19c 15.0c -outline black -width 2 -tags item  -gradient "0.3 0.5  red 0.7 0.5  green 1"

$c create text 25c 8.2c -text RotateText -anchor n
set x1 22c
set y1 15c
foreach angle "0 -15 -30 -45 -60 -75 -90" {
    set text [$c create text $x1 $y1 -width 1 -outline black  -font "Courier 20" -text "       Text$angle" -rotate $angle -anchor w]
}


# Set up event bindings for canvas:

$c bind item <Any-Enter> "itemEnter $c"
$c bind item <Any-Leave> "itemLeave $c"
bind $c <2> "$c scan mark %x %y"
bind $c <B2-Motion> "$c scan dragto %x %y"
bind $c <3> "itemMark $c %x %y"
bind $c <B3-Motion> "itemStroke $c %x %y"
bind $c <Control-f> "itemsUnderArea $c"
bind $c <1> "itemStartDrag $c %x %y"
bind $c <B1-Motion> "itemDrag $c %x %y"

# Utility procedures for highlighting the item under the pointer:

proc itemEnter {c} {
    global restoreCmd

    if {[winfo depth $c] == 1} {
	set restoreCmd {}
	return
    }
    set type [$c type current]
    if {$type == "window"} {
	set restoreCmd {}
	return
    }
    if {$type == "bitmap"} {
	set bg [lindex [$c itemconf current -background] 4]
	set restoreCmd [list $c itemconfig current -background $bg]
	$c itemconfig current -background SteelBlue2
	return
    }
    set fill [lindex [$c itemconfig current -fill] 4]
    if {(($type == "rectangle") || ($type == "oval") || ($type == "arc"))
	    && ($fill == "")} {
	set outline [lindex [$c itemconfig current -outline] 4]
	set restoreCmd "$c itemconfig current -outline $outline"
	$c itemconfig current -outline SteelBlue2
    } else {
	set restoreCmd "$c itemconfig current -fill $fill"
	$c itemconfig current -fill SteelBlue2
    }
}

proc itemLeave {c} {
    global restoreCmd

    eval $restoreCmd
}

# Utility procedures for stroking out a rectangle and printing what's
# underneath the rectangle's area.

proc itemMark {c x y} {
    global areaX1 areaY1
    set areaX1 [$c canvasx $x]
    set areaY1 [$c canvasy $y]
    $c delete area
}

proc itemStroke {c x y} {
    global areaX1 areaY1 areaX2 areaY2
    set x [$c canvasx $x]
    set y [$c canvasy $y]
    if {($areaX1 != $x) && ($areaY1 != $y)} {
	$c delete area
	$c addtag area withtag [$c create rect $areaX1 $areaY1 $x $y \
		-outline black]
	set areaX2 $x
	set areaY2 $y
    }
}

proc itemsUnderArea {c} {
    global areaX1 areaY1 areaX2 areaY2
    set area [$c find withtag area]
    set items ""
    foreach i [$c find enclosed $areaX1 $areaY1 $areaX2 $areaY2] {
	if {[lsearch [$c gettags $i] item] != -1} {
	    lappend items $i
	}
    }
    puts stdout "Items enclosed by area: $items"
    set items ""
    foreach i [$c find overlapping $areaX1 $areaY1 $areaX2 $areaY2] {
	if {[lsearch [$c gettags $i] item] != -1} {
	    lappend items $i
	}
    }
    puts stdout "Items overlapping area: $items"
}

set areaX1 0
set areaY1 0
set areaX2 0
set areaY2 0

# Utility procedures to support dragging of items.

proc itemStartDrag {c x y} {
    global lastX lastY
    set lastX [$c canvasx $x]
    set lastY [$c canvasy $y]
}

proc itemDrag {c x y} {
    global lastX lastY
    set x [$c canvasx $x]
    set y [$c canvasy $y]
    $c move current [expr $x-$lastX] [expr $y-$lastY]
    set lastX $x
    set lastY $y
}

# Procedure that's invoked when the button embedded in the canvas
# is invoked.

proc butPress {w color} {
    set i [$w create text 25c 18.1c -text "Ouch!!" -fill $color -anchor n]
    after 500 "$w delete $i"
}
