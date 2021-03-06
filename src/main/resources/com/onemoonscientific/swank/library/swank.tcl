package provide Tk 8.4
set tk_version 8.4
set tk_patchLevel 8.4.0
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


proc tk_popup {menu x y {entry {}}} {
    $menu post $x $y
}
proc tk_getOpenFile {args} {
    global env
    if { [expr [llength $args] % 2] != 0} {
        error "args not multiple of two"
    }
    set currentDir [::swank::getLastDir]
    destroy .sk_filebox
    set dialogMode 0
    set multipleSelection 0
    set title "Choose File to Open"
    set parent ""
    if {![catch {focus} fWin] && ($fWin ne "") && [winfo exists $fWin] && [winfo viewable $fWin]} {
        set fTWin [winfo toplevel $fWin]
        if {[winfo exists $fTWin] && [winfo viewable $fTWin]} {
            set parent $fTWin
        }
    }
    
    set dialogMode $::swank::defaultFileMode
    foreach "argType argVal" $args {
        switch -- $argType {
            -filedialog {
                set dialogMode $argVal
            }
            -filetypes {
                set filters [lindex $argVal 0]
            }
            -initialdir {
                set currentDir $argVal
            }
            -title {
                set title $argVal
            }
            -multiple {
                set multipleSelection $argVal
            }
            -parent {
                set parent $argVal
            }
            default {
                error "Invalid option type \"$argType\""
            }
        }
    }
    if {$multipleSelection} {
        set dialogMode 0
    }
    
    if {!$dialogMode} {
        jfilechooser .sk_filebox
        .sk_filebox configure -currentdirectory $currentDir
        .sk_filebox configure -multiselectionenabled $multipleSelection
        .sk_filebox configure -dialogtitle $title
        .sk_filebox configure -dialogparent $parent
        if {[info exists filters]} {
            .sk_filebox filter [lindex $filters 1] [lindex $filters 0]
        }
        .sk_filebox configure -visible true
    } else {
        filedialog .sk_filebox
        .sk_filebox configure -directory $currentDir
        .sk_filebox configure -title $title
        if {[info exists filters]} {
            .sk_filebox filter [lindex $filters 1] [lindex $filters 0]
        }
    }
    set result [.sk_filebox open]
    destroy .sk_filebox
    if {$result != ""} {
        ::swank::setLastDir [file dirname $result]
    }
    return $result
}


proc tk_getSaveFile {args} {
    global env
    if { [expr [llength $args] % 2] != 0} {
        error "args not multiple of two"
    }
    set currentDir [::swank::getLastDir]
    destroy .sk_filebox
    set dialogMode $::swank::defaultFileMode
    set defaultExtension ""
    set title "Choose File to Save"
    set parent ""
    if {![catch {focus} fWin] && ($fWin ne "") && [winfo exists $fWin] && [winfo viewable $fWin]} {
        set fTWin [winfo toplevel $fWin]
        if {[winfo exists $fTWin] && [winfo viewable $fTWin]} {
            set parent $fTWin
        }
    }
    foreach "argType argVal" $args {
        switch -- $argType {
            -filedialog {
                set dialogMode $argVal
            }
            -filetypes {
                set filters [lindex $argVal 0]
            }
            -initialdir {
                set currentDir $argVal
            }
            -defaultextension {
                set defaultExtension $argVal
            }
            -title {
                set title $argVal
            }
            -parent {
                set parent $argVal
            }
            default {
                error "Invalid option type \"$argType\""
            }
        }
    }
    
    
    if {!$dialogMode} {
        jfilechooser .sk_filebox
        .sk_filebox configure -currentdirectory $currentDir
        .sk_filebox configure -dialogtitle $title
        .sk_filebox configure -dialogparent $parent
        if {[info exists filters]} {
            .sk_filebox filter [lindex $filters 1] [lindex $filters 0]
        }
        .sk_filebox configure -visible true
    } else {
        filedialog .sk_filebox
        .sk_filebox configure -directory $currentDir
        .sk_filebox configure -title $title
        if {[info exists filters]} {
            .sk_filebox filter [lindex $filters 1] [lindex $filters 0]
        }
    }
    set result [.sk_filebox save]
    if {($result ne "") && ([file extension $result] eq "") && ($defaultExtension ne "")} {
        set result ${result}$defaultExtension
    }
    destroy .sk_filebox
    if {$result != ""} {
        ::swank::setLastDir [file dirname $result]
    }
    return $result
}
proc tk_chooseDirectory {args} {
    global env
    if { [expr [llength $args] % 2] != 0} {
        error "args not multiple of two"
    }
    set currentDir [::swank::getLastDir]
    destroy .sk_filebox
    set dialogMode $::swank::defaultFileMode
    set title "Choose File to Open"
    set parent ""
    if {![catch {focus} fWin] && ($fWin ne "") && [winfo exists $fWin] && [winfo viewable $fWin]} {
        set fTWin [winfo toplevel $fWin]
        if {[winfo exists $fTWin] && [winfo viewable $fTWin]} {
            set parent $fTWin
        }
    }
    foreach "argType argVal" $args {
        switch -- $argType {
            -filedialog {
                set dialogMode $argVal
            }
            -initialdir {
                set currentDir $argVal
            }
            -title {
                set title $argVal
            }
            -parent {
                set parent $argVal
            }
            default {
                error "Invalid option type \"$argType\""
            }
        }
    }
    # fixme  should allow dialogMode on Mac OS by setting Property
    set dialogMode 0
    
    
    if {!$dialogMode} {
        jfilechooser .sk_filebox
        .sk_filebox configure -currentdirectory $currentDir
        .sk_filebox configure -dialogparent $parent
        .sk_filebox configure -fileselectionmode [java::field javax.swing.JFileChooser DIRECTORIES_ONLY]
        .sk_filebox configure -dialogtitle $title
        .sk_filebox configure -visible true
    } else {
        filedialog .sk_filebox
        .sk_filebox configure -directory $currentDir
        .sk_filebox configure -title $title
    }
    set result [.sk_filebox open]
    destroy .sk_filebox
    if {$result != ""} {
        ::swank::setLastDir [file dirname $result]
    }
    return $result
}

proc tk_optionMenu {nm var args} {
    jcombobox $nm -variable $var
    eval $nm item append $args
}




proc tk_chooseColor {args} {
    global tk_colorChooser
    global env
    set initialColor black
    set title "Choose Color"
    set mode colorpicker
    if {![info exists ::swank::useColorPicker]} {
        set ::swank::useColorPicker 0
        catch {
            java::try {
                java::info fields com.bric.swing.ColorPicker
                set ::swank::useColorPicker 1
            } catch {Exception e} {
            } finally {}
        }
    }
    if {$::swank::useColorPicker} {
        return [eval [linsert $args 0 colorpicker]]
    } else {
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
    variable defaultFileMode 0
    proc setLastDir {dir} {
        set ::swank::lastDir $dir
    }
    proc cmdLineArgs {} {
        global argv
        if {[llength $argv] > 0} {
            set firstArg [lindex $argv 0]
            switch -- $firstArg {
                -demo {
                    uplevel #0 {
                        source  resource:/com/onemoonscientific/swank/library/demos/widget
                    }
                }
                -swkcon {
                    uplevel #0 {
                        source  resource:/com/onemoonscientific/swank/library/swkcon.tcl
                        ::tkcon::Init -rows 6
                    }
                }
                -console {
                    console show
                }
            }
        }
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

