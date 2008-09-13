package provide Tk 8.3
set tk_version 8.3
set tk_patchLevel 8.3.0
set tk_strictMotif 0
proc tkwait {args} {
   if {[llength $args] != 2} {
	error {wrong # args: should be "tkwait variable|visibility|window name"}
   }
   set mode [lindex $args 0]
    switch $mode {
	variable {
	        update
		vwait [lindex $args 1]
	}
	visibility {
           puts "tkwait $args"
        }
	window  {
           puts "tkwait $args"
        }
	default {
          error "bad option \"$mode\": must be variable, visibility, or window"
	}
   }
}

proc tk {args } {
	if {$args == "appname"} {
		return Wisk
        }
}


proc tk_getOpenFile {args} {
    global env 
    if { [expr [llength $args] % 2] != 0} {
        error "args not multiple of two"
    }
    set currentDir [::swank::getLastDir]
    destroy .sk_filebox
    jfilechooser .sk_filebox
    foreach "argType argVal" $args {
        switch -- $argType {
            -filetypes {
                set filters [lindex $argVal 0]
                .sk_filebox filter [lindex $filters 1] [lindex $filters 0]
            }
            
            -initialdir {
                 set currentDir $argVal
            }
            -title {
                .sk_filebox configure -dialogtitle $argVal
            }
            -multiple {
                .sk_filebox configure -multiselectionenabled $argVal
            }
        }
    }
    
    .sk_filebox configure -currentdirectory $currentDir
    .sk_filebox configure -visible true
    set result [.sk_filebox open]
    destroy .sk_filebox
    if {$result != ""} {
        ::swank::setLastDir [file dirname $result]
    }
    return $result
}

proc tk_getSaveFile {args} {
    destroy .sk_filebox
    set currentDir [::swank::getLastDir]
    jfilechooser .sk_filebox
     foreach "argType argVal" $args {
        switch -- $argType {
            -filetypes {
                set filters $argVal
                foreach filter $filters {
                        set description [lindex $filter 0]
                        set extensions [lindex $filter 1]
                        .sk_filebox filter $extensions $description
                    }
            }
            
            -initialdir {
                 set currentDir $argVal
            }
            -title {
                .sk_filebox configure -dialogtitle $argVal
            }
        }
    }
    .sk_filebox configure -currentdirectory $currentDir
    .sk_filebox configure -visible true
    set result [.sk_filebox save]
    destroy .sk_filebox
    if {$result != ""} {
        ::swank::setLastDir [file dirname $result]
    }
    return $result
}

proc tk_chooseDirectory {args} {
    destroy .sk_filebox
    set currentDir [::swank::getLastDir]
    jfilechooser .sk_filebox -currentdirectory $currentDir
     foreach "argType argVal" $args {
        switch -- $argType {
             -initialdir {
                 set currentDir $argVal
            }
            -title {
                .sk_filebox configure -dialogtitle $argVal
            }
        }
    }
    .sk_filebox configure -currentdirectory $currentDir
    .sk_filebox configure -fileselectionmode [java::field javax.swing.JFileChooser DIRECTORIES_ONLY]
    .sk_filebox configure -visible true
    set result [.sk_filebox open]
    destroy .sk_filebox
    if {$result != ""} {
        ::swank::setLastDir $result
    }
    return $result
}

proc tk_optionMenu {nm var args} {
    jcombobox $nm -variable $var
    eval $nm item append $args
}




proc tk_chooseColor {args} {
    global tk_colorChooser
    set initialColor black
    set title "Choose Color"
     foreach "argType argVal" $args {
        switch -- $argType {
             -initialcolor {
    		set initialColor $argVal
            }
            -title {
    		set title $argVal
            }
        }
    }

    if {![winfo exists .sk_color]} {
        jcolorchooser .sk_color
    }
    set color [.sk_color choose $title $initialColor]
    return $color
}


proc grab {args} {
    puts "grab $args"
}


proc getColumnCount {} {
    return 0
}

proc swank_source {args} {
	if {[llength $args] == 2} {
		uplevel #0 jacl_source [lindex $args 0] [lindex $args 1]
	} else {
		if {[string match resource:* $args]} {
			uplevel #0 jacl_source -url $args
		} else {
			uplevel #0 jacl_source $args
		}
	}
}


proc console {mode args} {
    if {$mode == "show"} {
        global tcl_prompt1
        global tcl_prompt2
        if {![winfo exists .console]} {
            tkConsoleInit
            set tcl_prompt1 >
            set tcl_prompt2 >
            tkConsolePrompt
            rename puts swkcon_tcl_puts
            rename swkcon_puts puts
        }
        wm deiconify .console
    } else {
        if {[winfo exists .console]} {
            wm withdraw .console
        }
    }
}

namespace eval ::swank {
    global env 
    variable lastDir $env(HOME)
    proc setLastDir {dir} {
         set ::swank::lastDir $dir
    }
    proc getLastDir {} {
        set currentDir ""
        if {[info exists ::swank::lastDir]} {
             set currentDir $::swank::lastDir
        }
        if {$currentDir == ""} {
            set currentDir $env(HOME)
        }
        return $currentDir
    }
    proc help {args} {
        if {[llength $args] == 0} {
        } else {
            set mode [lindex $args 0]
            switch $mode {
                widgets {
                    set widgets [java::call com.onemoonscientific.swank.WidgetExt getWidgets]
                    return [$widgets getrange]
                }
            }
        }
    }
    namespace eval ::swank::events {
    variable robot ""
    proc save {fileName} {
         eventrecorder stop
         set f1 [open $fileName w]
         set events [eventrecorder list]
         foreach event $events {
            puts $f1 $event
         }
         close $f1
      }

      proc execFile {fileName} {
         variable robot
         if {$robot == ""} {
             set robot [java::new java.awt.Robot]
         }
         set f1 [open $fileName r]
         set first 1
         set lastTime 0
         set x [winfo x .]
         set y [winfo y .]
         set width [winfo width .]
         set height [winfo height .]
         set rect [java::new java.awt.Rectangle $x $y $width $height]
         set i 0
         set eventTime 20
         set deltaTime 0
         while {[gets $f1 s] != -1} {
              set waitTime [expr {$eventTime-$deltaTime+5}]
              set etime [lindex $s end]
              set eventTime [expr {round($etime-$lastTime)}]
              set lastTime $etime
            
	      if {$waitTime <=  0} {
                   set waitTime 20
              }
              after $waitTime

#             set image [$robot createScreenCapture $rect]
              update
#              set jfile [java::new java.io.File screencapture$i.png]
#              java::call javax.imageio.ImageIO write $image png $jfile
              set startTime [clock clicks]
              if {[catch "[list execEvent $s]" result]} {
                    puts "error in $s"
                    puts $result
                    break
              }
              update
              set endTime [clock clicks]
              if {$endTime <= $startTime} {
                 set deltaTime $eventTime
              } else {
                  set deltaTime [expr {round($endTime-$startTime)}]
              }
              incr i
         }
         close $f1
      }

      proc execEvent {eventString} {
         variable robot
         if {$robot == ""} {
             set robot [java::new java.awt.Robot]
         }
         set event [lindex $eventString 0]
         if {$event == "mouse"} {
             foreach "event type mods win x y time" $eventString {}
             set rootx [winfo rootx $win]
             set rooty [winfo rooty $win]
             set x [expr {$x+$rootx}]
             set y [expr {$y+$rooty}]
             switch $type {
                 motion {
                     $robot mouseMove $x $y 
                 }
                 drag {
                     $robot mouseMove $x $y 
                 }
                 press {
                     $robot mousePress $mods
                 }
                 release {
                     $robot mouseRelease $mods
                 }
             }
         } elseif {$event == "key"} {
             foreach "event type keyCode time" $eventString {}
             switch $type {
                 press {
                     $robot keyPress $keyCode
                 }
                 release {
                     $robot keyRelease $keyCode
                 }
              }
         }
      }
    }
}

set tk_library resource:/com/onemoonscientific/swank/library
catch "toplevel ."
raise .
