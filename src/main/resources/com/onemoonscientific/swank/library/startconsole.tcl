console show
menu .console.menubar.demos -tearoff 0
.console.menubar.demos add command -label "Widgets" -underline 2 -command "source [file join $tk_library demos widget]"
.console.menubar add cascade  -label Demos -menu .console.menubar.demos
vwait __done

