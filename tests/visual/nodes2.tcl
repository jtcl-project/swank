pack [canvas .c -width 200 -height 200 -bg white] -fill both -expand y

set node0 [.c create node 0 0 -outline blue -width 2]
set node1 [.c create node 0 0 -outline green -width 2]
set node2 [.c create node 0 0 -outline blue -width 2]
set node3 [.c create node 0 0 -outline yellow -width 2]
set node4 [.c create node 0 0 -outline purple -width 2]

#.c itemconfigure $node2 -state hidden
set red [.c create rectangle 50 50 100 100 -fill red]
set yellow [.c create rectangle 60 60 110 110 -fill yellow]
set green [.c create rectangle 70 70 120 120 -fill green]
set purple [.c create rectangle 80 80 130 130 -fill purple]
set blue [.c create rectangle 90 90 140 140 -fill blue]
set violet [.c create rectangle 100 100 150 150 -fill violet]

.c itemconfigure $node1 -node $node0
.c itemconfigure $node2 -node $node0
.c itemconfigure $node3 -node $node1
.c itemconfigure $node4 -node $node1

.c itemconfigure $red -node $node3
.c itemconfigure $yellow -node $node3
.c itemconfigure $green -node $node4
.c itemconfigure $purple -node $node2
.c itemconfigure $blue -node $node2
.c itemconfigure $violet -node $node0

#.c raise $node1
update
for {set i 0} {$i < 7} {incr i} {
    puts [.c bbox $i]
}
vwait done
