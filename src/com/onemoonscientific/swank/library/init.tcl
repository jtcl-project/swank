package require java
java::load com.onemoonscientific.swank.WidgetExt
java::load com.onemoonscientific.swank.canvas.CanvasExt
java::load com.onemoonscientific.swank.print.PrintExt
source -url resource:/com/onemoonscientific/swank/library/swank.tcl
source -url resource:/com/onemoonscientific/swank/library/dialog.tcl
source -url resource:/com/onemoonscientific/swank/library/bgerror.tcl
source -url resource:/com/onemoonscientific/swank/library/tkerror.tcl
source -url resource:/com/onemoonscientific/swank/library/html.tcl
source -url resource:/com/onemoonscientific/swank/library/console.tcl
set dir resource:/tcl/lang/library
source -url resource:/tcl/lang/library/tclIndex
rename source jacl_source
rename swank_source source
