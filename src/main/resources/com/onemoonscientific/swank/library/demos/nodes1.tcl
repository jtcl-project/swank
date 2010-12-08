set w .nodes1
catch {destroy $w}
toplevel $w
wm title $w "Canvas Nodes Demonstration"
wm iconname $w "Items"
positionWindow $w
set c $w.frame.c

label $w.msg -font $font -wraplength 5i -justify left -text "This window contains a canvas widget that uses nodes."
pack $w.msg -side top

frame $w.buttons
pack $w.buttons -side bottom -fill x -pady 2m
button $w.buttons.dismiss -text Dismiss -command "destroy $w"
button $w.buttons.code -text "See Code" -command "showCode $w"
pack $w.buttons.dismiss $w.buttons.code -side left -expand 1

frame $w.frame
pack $w.frame -side top -fill both -expand yes

pack [canvas $c -width 200 -height 200 -bg white] -fill both -expand y

set node0 [$c create node 0 0 -outline blue -width 2]
set node1 [$c create node 0 0 -outline green -width 2]

set red [$c create rectangle 50 50 100 100 -fill red]
set yellow [$c create rectangle 60 60 110 110 -fill yellow]
set green [$c create rectangle 70 70 120 120 -fill green]
set purple [$c create rectangle 80 80 130 130 -fill purple]


$c itemconfigure $red -node $node0
$c itemconfigure $yellow -node $node1
$c itemconfigure $green -node $node1
$c itemconfigure $node1 -node $node0
$c itemconfigure $purple -node $node0
$c raise $red $purple
