pack [canvas .c -width 200 -height 200 -bg white] -fill both -expand y

set node0 [.c create node 0 0 -outline blue -width 2]
set node1 [.c create node 0 0 -outline green -width 2]

set red [.c create rectangle 50 50 100 100 -fill red]
set yellow [.c create rectangle 60 60 110 110 -fill yellow]
set green [.c create rectangle 70 70 120 120 -fill green]
set purple [.c create rectangle 80 80 130 130 -fill purple]


.c itemconfigure $red -node $node0
.c itemconfigure $yellow -node $node1
.c itemconfigure $green -node $node1
.c itemconfigure $node1 -node $node0
.c itemconfigure $purple -node $node0
.c raise $red $purple
vwait done
