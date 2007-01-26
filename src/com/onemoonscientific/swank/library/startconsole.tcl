console show
jmenu .console.menubar.demos -tearoff 0 -label Demos
  .console.menubar.demos add command -label "Widgets" -underline 2 \
  -command "source [file join $tk_library demos widget]"
.console.menubar jadd .console.menubar.demos
#[.console object] pack
vwait __done
