proc incrCoords {coords  amount} {
    set dX [lindex $amount 0]
    set dY [lindex $amount 1]
    set newCoords [list]
    foreach "x y" $coords {
        lappend newCoords [expr {$x+$dX}]
        lappend newCoords [expr {$y+$dY}]
    }
    return $newCoords
}

proc drawItems {type startCoords dX attrs} {
puts "$type $attrs"
    set appAttr [list]
    set x 0
    eval .c create $type $startCoords
    set x0 [lindex $startCoords 0]
    set y0 [lindex $startCoords 1]
    set y0 [expr {$y0-30}]
    set yT [expr {$y0-15}]
    .c create line 0 $yT 1000 $yT -width 2 -fill gray
    .c create text 10 $yT -text $type -anchor nw
    foreach "aName aValue" $attrs {
        if {$aName ne "hselect"} {
            lappend appAttr $aName
            lappend appAttr $aValue
        }
        set x [expr {$x+$dX}]
        set x0 [expr {$x0+$dX}]
        set y1 [expr {$y0+15}]
        .c create text $x0 $y0 -text "$aName"
        .c create text $x0 $y1 -text "$aValue"
        set coords [incrCoords $startCoords "$x 0"] 
        set item [eval .c create $type $coords $appAttr]
        if {$aName eq "hselect"} {
           .c hselect $item 1
        }
    }
}

pack [canvas .c -width 800 -height 1150 -bg white] -fill both -expand y
set lineCoords [list 50 50 80 80 110 50]
set rectCoords [list 50 50 90 90]
set ovalCoords [list 50 50 90 70]
set arcCoords [list 50 50 90 90]
set segmentCoords [list 50 50 60 80 70 80 80 50 40 60 100 60]

set dX 80
set dY 100
set y 0
set attrs [list  -fill red -width 4 -startstyle square -endstyle arrow -arrowshape "10 20 8" -rotate 45 -smooth 5 hselect 1]
set coords [incrCoords $lineCoords "0 $y"] 
drawItems line $coords $dX $attrs

incr y $dY
set attrs [list  -width 1 -dash "." -dash "-" -dash "-." -dash "2 4" -dash "6 4" -dash "6 4 2 4" -dash "6 4 2 4 2 4"]
set coords [incrCoords "50 50 100 50" "0 $y"] 
drawItems line $coords $dX $attrs

incr y $dY
set attrs [list  -width 10 -capstyle butt -capstyle round -capstyle projecting -joinstyle bevel -joinstyle miter -joinstyle round]
set coords [incrCoords $lineCoords "0 $y"] 
drawItems line $coords $dX $attrs

incr y $dY
set attrs [list  -radius 5 -outline blue -symbol circle -fill red -rotate 45 hselect 1]
set coords [incrCoords $lineCoords "0 $y"] 
drawItems symbols $coords $dX $attrs

incr y $dY
set attrs [list  -radius 5 -symbol circle -symbol triangle_up -symbol triangle_down -symbol cross -symbol square -symbol diamond ]
set coords [incrCoords $lineCoords "0 $y"] 
drawItems symbols $coords $dX $attrs

incr y $dY
set attrs [list  -outline blue -width 4 -fill red -gradient "0 0 blue\n 1 0 green" -rotate 45 hselect 1]
set coords [incrCoords $rectCoords "0 $y"] 
drawItems rectangle $coords $dX $attrs

incr y $dY
set attrs [list  -fill red -text Hello -anchor c -textcolor blue -font {Helvetica 16}]
set coords [incrCoords $rectCoords "0 $y"] 
drawItems rectangle $coords $dX $attrs

incr y $dY
set attrs [list  -outline blue -width 4 -fill red -gradient "0 0 blue\n 1 0 green" -rotate 45 hselect 1]
set coords [incrCoords $ovalCoords "0 $y"] 
drawItems oval $coords $dX $attrs

incr y $dY
set attrs [list  -outline blue -width 4 -extent 120  -style chord -style pie -fill red -start 180 -rotate 45 hselect 1]
set coords [incrCoords $arcCoords "0 $y"] 
drawItems arc $coords $dX $attrs

incr y $dY
set attrs [list  -outline blue -width 4 -fill red  -dash "." -rotate 45 hselect 1]
set coords [incrCoords $lineCoords "0 $y"] 
drawItems polygon $coords $dX $attrs

incr y $dY
set attrs [list  -width 4 -fill red  -dash "." -rotate 45 hselect 1]
set coords [incrCoords $segmentCoords "0 $y"] 
drawItems segments $coords $dX $attrs



vwait done
