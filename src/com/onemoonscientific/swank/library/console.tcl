# console.tcl --
#
# This code constructs the console window for an application.  It
# can be used by non-unix systems that do not have built-in support
# for shells.
#
# RCS: @(#) $Id: console.tcl,v 1.3 2005/11/07 03:21:11 bruce_johnson Exp $
#
# Copyright (c) 1998-1999 Scriptics Corp.
# Copyright (c) 1995-1997 Sun Microsystems, Inc.
#
# See the file "license.terms" for information on usage and redistribution
# of this file, and for a DISCLAIMER OF ALL WARRANTIES.
#

# TODO: history - remember partially written command

# tkConsoleInit --
# This procedure constructs and configures the console windows.
#
# Arguments:
# 	None.

proc tkTextSetCursor {w pos} {
    global tkPriv

    if {[$w compare $pos == end]} {
        set pos {end - 1 chars}
    }
    $w mark set insert $pos
    $w tag remove sel 1.0 end
#    $w see insert
}

proc tkConsoleInit {{nm ""}} {
    global tcl_platform

#    if {![eval {set tcl_interactive}]} {
#	wm withdraw .
#    }

    if {[string compare $tcl_platform(platform) "macintosh"]} {
	set mod "Ctrl"
    } else {
	set mod "Cmd"
    }
    global console
    if {$nm == ""} {
        set tl .console
        toplevel .console
    } else {
        set tl $nm
    }
    jmenubar $tl.menubar

    menu $tl.menubar.file
    $tl.menubar.file add command -label "Source..." -underline 0 -command tkConsoleSource
    if {$nm == ""} {
        $tl.menubar.file add command -label "Hide Console" -underline 0 -command {wm withdraw .console}
    }
    $tl.menubar.file add command -label "Save Console" -underline 0 -command {tkConsoleSave}
    if {[string compare $tcl_platform(platform) "macintosh"]} {
	$tl.menubar.file add command -label "Exit" -underline 1 -command exit
    } else {
	$tl.menubar.file add command -label "Quit" -command exit 
    }

    menu $tl.menubar.edit
    $tl.menubar.edit add command -label "Cut" -underline 2 -command { event generate $console <<Cut>> } 
    $tl.menubar.edit add command -label "Copy" -underline 0 -command { event generate $console <<Copy>> } 
    $tl.menubar.edit add command -label "Paste" -underline 1 -command { event generate $console <<Paste>> } 

    if {[string compare $tcl_platform(platform) "windows"]} {
	$tl.menubar.edit add command -label "Clear" -underline 2 -command {$console delete 1.0 end}
    } else {
	$tl.menubar.edit add command -label "Clear" -underline 0 -command {$console delete 1.0 end} 
	$tl.menubar add cascade -label Help -menu $tl.menubar.help -underline 0
	menu $tl.menubar.help -tearoff 0
	$tl.menubar.help add command -label "About..." -underline 0 -command tkConsoleAbout
    }
    $tl.menubar add cascade -label File -menu $tl.menubar.file 
    $tl.menubar add cascade -label Edit -menu $tl.menubar.edit

    $tl configure -menu $tl.menubar

    jscrollpane $tl.s
    pack $tl.s -fill both -expand y -side top
    text $tl.console
    $tl.console configure -nativekeys 0
    $tl.s add $tl.console
    if {$nm == ""} {
        wm geometry $tl 600x400
    }
    set console $tl.console
    update



#  -yscrollcommand ".sb set" -setgrid true 
#    scrollbar .sb -command ".console yview"
#    pack .sb -side right -fill both
#    pack .console -fill both -expand 1 -side left
    switch -exact $tcl_platform(platform) {
	"macintosh" {
	    $console configure -font {Monaco 9 normal} -highlightthickness 0
	}
	"windows" {
	    $console configure -font systemfixed
	}
    }

    tkConsoleBind $console

    $console tag configure stderr -foreground red
    $console tag configure stdin -foreground blue
    $console tag configure input -foreground blue

    focus $console
    if {$nm == ""} {
        wm protocol . WM_DELETE_WINDOW { wm withdraw . }
        wm title $tl "Console"
    }
    flush stdout
#    $console mark set output [$console index "end - 1 char"]
    $console mark set output end
    tkTextSetCursor $console end
    $console mark set promptEnd insert
    $console mark gravity promptEnd left
}

# tkConsoleSource --
#
# Prompts the user for a file to source in the main interpreter.
#
# Arguments:
# None.

proc tkConsoleSource {} {
global console
    set filename [tk_getOpenFile -defaultextension .tcl -parent . \
		      -title "Select a file to source" \
		      -filetypes {{"Tcl Scripts" .tcl} {"All Files" *}}]
    if {[string compare $filename ""]} {
    	set cmd [list source $filename]
	if {[catch {eval $cmd} result]} {
	    tkConsoleOutput stderr "$result\n"
	}
    }
}

# tkConsoleSave --
#
# Prompts the user for a file to save the console output to.
#
# Arguments:
# None.

proc tkConsoleSave {} {
global console
    set filename [tk_getSaveFile -defaultextension .tcl -parent . \
		      -title "Select a file to save to" \
		      -filetypes {{"Tcl Scripts" .txt} {"All Files" *}}]
    if {[string compare $filename ""]} {
	set f1 [open $filename w]
	swkcon_tcl_puts $f1 [$console get 1.0 end]
	close $f1
    }
}


# tkConsoleInvoke --
# Processes the command line input.  If the command is complete it
# is evaled in the main interpreter.  Otherwise, the continuation
# prompt is added and more input may be added.
#
# Arguments:
# None.

proc tkConsoleInvoke {args} {
    global console
    set ranges [$console tag ranges input]
    set cmd ""
    if {[llength $ranges]} {
	set pos 0
	while {[string compare [lindex $ranges $pos] ""]} {
	    set start [lindex $ranges $pos]
	    set end [lindex $ranges [incr pos]]
	    append cmd [$console get $start $end]
	    incr pos
	}
    }
    if {[string equal $cmd ""]} {
	tkConsolePrompt
    } elseif {[info complete $cmd]} {
	$console mark set output end
	$console tag delete input
#	set result [consoleinterp record $cmd]
	if {[string trim $cmd] != ""} {
        if {![catch {uplevel #0 $cmd} result]} {
	}
	history add $cmd
#       swkcon_tcl_puts [$console tag ranges input]
	if {[string compare $result ""]} {
#	    puts $result
		$console insert end $result\n
 	}
	}
	tkConsoleHistory reset
	tkConsolePrompt
    } else {
	tkConsolePrompt partial
    }
#    $console yview -pickplace insert
}

# tkConsoleHistory --
# This procedure implements command line history for the
# console.  In general is evals the history command in the
# main interpreter to obtain the history.  The global variable
# histNum is used to store the current location in the history.
#
# Arguments:
# cmd -	Which action to take: prev, next, reset.

set histNum 1
proc tkConsoleHistory {cmd} {
	global console
    global histNum
    
    switch $cmd {
    	prev {
	    incr histNum -1
	    if {$histNum == 0} {
		set cmd {history event [expr {[history nextid] -1}]}
	    } else {
		set cmd "history event $histNum"
	    }
    	    if {[catch {eval $cmd} cmd]} {
    	    	incr histNum
    	    	return
    	    }
	    $console delete promptEnd end
    	    $console insert promptEnd $cmd {input}
    	}
    	next {
	    incr histNum
	    if {$histNum == 0} {
		set cmd {history event [expr {[history nextid] -1}]}
	    } elseif {$histNum > 0} {
		set cmd ""
		set histNum 1
	    } else {
		set cmd "history event $histNum"
	    }
	    if {[string compare $cmd ""]} {
		catch {eval $cmd} cmd
	    }
	    $console delete promptEnd end
	    $console insert promptEnd $cmd {input}
    	}
    	reset {
    	    set histNum 1
    	}
    }
}

# tkConsolePrompt --
# This procedure draws the prompt.  If tcl_prompt1 or tcl_prompt2
# exists in the main interpreter it will be called to generate the 
# prompt.  Otherwise, a hard coded default prompt is printed.
#
# Arguments:
# partial -	Flag to specify which prompt to print.

proc tkConsolePrompt {{partial normal}} {
     global console
     global tcl_prompt1 tcl_prompt2
    if {[string equal $partial "normal"]} {
	set temp [$console index "end - 1 char"]
	$console mark set output end
    	if {[eval "info exists tcl_prompt1"]} {
            eval swkcon console insert end "% "
    	} else {
            eval swkcon console insert end "% "
    	}
    } else {
	set temp [$console index output]
	$console mark set output end
    	if {[eval "info exists tcl_prompt2"]} {
            eval swkcon console insert end "> "
    	} else {
            eval swkcon console insert end "> "
    	}
    }
    flush stdout
    $console mark set output $temp
    tkTextSetCursor $console end
    $console mark set promptEnd insert
    $console mark gravity promptEnd left
}

# tkConsoleBind --
# This procedure first ensures that the default bindings for the Text
# class have been defined.  Then certain bindings are overridden for
# the class.
#
# Arguments:
# None.

proc tkConsoleBind {win} {
global console
#    bindtags $win "$win Text . all"

    # Ignore all Alt, Meta, and Control keypresses unless explicitly bound.
    # Otherwise, if a widget binding for one of these is defined, the
    # <KeyPress> class binding will also fire and insert the character,
    # which is wrong.  Ditto for <Escape>.

#    bind $win <Alt-KeyPress> {# nothing }
#    bind $win <Meta-KeyPress> {# nothing}
#    bind $win <Control-KeyPress> {# nothing}
#    bind $win <Escape> {# nothing}
#    bind $win <KP_Enter> {# nothing}

#    bind $win <KeyType> { if {[string is control %K]} { } else { } }

    bind $win <Tab> {
	tkConsoleInsert %W \t
	focus %W
	break
    }
    bind $win <Return> {
	%W mark set insert {end - 1c}
	tkConsoleInsert %W "\n"
	tkConsoleInvoke
	break
    }
    bind $win <Delete> {
	if {[%W compare insert < promptEnd]} {
	    break
	}
    }
    bind $win <BackSpace> {
	if {[string compare [%W tag nextrange sel 1.0 end] ""]} {
	    %W tag remove sel sel.first promptEnd
	} elseif {[%W compare insert <= promptEnd]} {
	    break
	}
        %W delete insert-1c
    }
    foreach left {Control-a Home} {
	bind $win <$left> {
	    if {[%W compare insert < promptEnd]} {
		tkTextSetCursor %W {insert linestart}
	    } else {
		tkTextSetCursor %W promptEnd
            }
	    break
	}
    }
    foreach right {Control-e End} {
	bind $win <$right> {
	    tkTextSetCursor %W {insert lineend}
	    break
	}
    }
    bind $win <Control-d> {
	if {[%W compare insert < promptEnd]} {
	    break
	}
    }
    bind $win <Control-k> {
	if {[%W compare insert < promptEnd]} {
	    %W mark set insert promptEnd
	}
    }
    bind $win <Control-t> {
	if {[%W compare insert < promptEnd]} {
	    break
	}
    }
    bind $win <Meta-d> {
	if {[%W compare insert < promptEnd]} {
	    break
	}
    }
    bind $win <Meta-BackSpace> {
	if {[%W compare insert <= promptEnd]} {
	    break
	}
    }
    bind $win <Control-h> {
	if {[%W compare insert <= promptEnd]} {
	    break
	}
    }
    foreach prev {Control-p Up} {
	bind $win <$prev> {
	    tkConsoleHistory prev
	    break
	}
    }
    foreach prev {Control-n Down} {
	bind $win <$prev> {
	    tkConsoleHistory next
	    break
	}
    }
    bind $win <Insert> {
	catch {tkConsoleInsert %W [selection get -displayof %W]}
	break
    }
    bind $win <KeyPress> {
	if {[string is print %A]} {
		tkConsoleInsert %W %A
		break
	}
    }
    foreach left {Control-b Left} {
	bind $win <$left> {
	    if {[%W compare insert == promptEnd]} {
		break
	    }
	    tkTextSetCursor %W insert-1c
	    break
	}
    }
    foreach right {Control-f Right} {
	bind $win <$right> {
	    tkTextSetCursor %W insert+1c
	    break
	}
    }
    bind $win <F9> {
	eval destroy [winfo child .]
	if {[string equal $tcl_platform(platform) "macintosh"]} {
	    source -rsrc Console
	} else {
	    source [file join $tk_library console.tcl]
	}
    }
    bind $win <<Cut>> {
        # Same as the copy event
 	if {![catch {set data [%W get sel.first sel.last]}]} {
	    clipboard clear -displayof %W
	    clipboard append -displayof %W $data
	}
	break
    }
    bind $win <<Copy>> {
 	if {![catch {set data [%W get sel.first sel.last]}]} {
	    clipboard clear -displayof %W
	    clipboard append -displayof %W $data
	}
	break
    }
    bind $win <<Paste>> {
	catch {
	    set clip [selection get -displayof %W -selection CLIPBOARD]
	    set list [split $clip \n\r]
	    tkConsoleInsert %W [lindex $list 0]
	    foreach x [lrange $list 1 end] {
		%W mark set insert {end - 1c}
		tkConsoleInsert %W "\n"
		tkConsoleInvoke
		tkConsoleInsert %W $x
	    }
	}
	break
    }
}

# tkConsoleInsert --
# Insert a string into a text at the point of the insertion cursor.
# If there is a selection in the text, and it covers the point of the
# insertion cursor, then delete the selection before inserting.  Insertion
# is restricted to the prompt area.
#
# Arguments:
# w -		The text window in which to insert the string
# s -		The string to insert (usually just a single character)

proc tkConsoleInsert {w s} {
global console
    if {[string equal $s ""]} {
	return
    }
    catch {
	if {[$w compare sel.first <= insert]
		&& [$w compare sel.last >= insert]} {
	    $w tag remove sel sel.first promptEnd
	    $w delete sel.first sel.last
	}
    }
    if {[$w compare insert < promptEnd]} {
	$w mark set insert end	
    }
    $w insert insert $s {input}
    #$w see insert
}

# tkConsoleOutput --
#
# This routine is called directly by ConsolePutsCmd to cause a string
# to be displayed in the console.
#
# Arguments:
# dest -	The output tag to be used: either "stderr" or "stdout".
# string -	The string to be displayed.

proc tkConsoleOutput {dest string} {
global console
    $console insert output $string $dest
    #$console see insert
}

# tkConsoleExit --
#
# This routine is called by ConsoleEventProc when the main window of
# the application is destroyed.  Don't call exit - that probably already
# happened.  Just delete our window.
#
# Arguments:
# None.

proc tkConsoleExit {} {
global console
    destroy .
}

# tkConsoleAbout --
#
# This routine displays an About box to show Tcl/Tk version info.
#
# Arguments:
# None.

proc tkConsoleAbout {} {
global console
    global tk_patchLevel
    tk_messageBox -type ok -message "Tcl for Windows
Copyright \251 2000 Scriptics Corporation

Tcl [info patchlevel]
Tk $tk_patchLevel"
}

##
## Some procedures to make up for lack of built-in shell commands
##
# following from Jeff Hobbs tkcon
## tkcon_puts -
## This allows me to capture all stdout/stderr to the console window
## This will be renamed to 'puts' at the appropriate time during init
##
# ARGS: same as usual
# Outputs:      the string with a color-coded text tag
##
proc swkcon {where args} {
global console
eval $console $args
}

proc swkcon_puts args {
    set len [llength $args]
    if {$len==1} {
        eval swkcon console insert output $args stdout {\n} stdout
        swkcon console see output
    } elseif {$len==2 && \
            [regexp {^(stdout|stderr|-nonewline)} [lindex $args 0] junk tmp]} {
        if {[string compare $tmp -nonewline]} {
            eval swkcon console insert output \
                    [lreplace $args 0 0] $tmp {\n} $tmp
        } else {
            eval swkcon console insert output [lreplace $args 0 0] stdout
        }
        swkcon console see output
    } elseif {$len==3 && \
            [regexp {^(stdout|stderr)$} [lreplace $args 2 2] junk tmp]} {
        if {[string compare [lreplace $args 1 2] -nonewline]} {
            eval swkcon console insert output [lrange $args 1 1] $tmp
        } else {
            eval swkcon console insert output [lreplace $args 0 1] $tmp
        }
        swkcon console see output
    } else {
	eval swkcon_tcl_puts $args
#        global errorCode errorInfo
#        if {[catch "swkcon $args" msg]} {
#            regsub swkcon $msg puts msg
#            regsub -all swkcon_tcl_puts $errorInfo puts errorInfo
#            return -code error $msg
#        }
#        return $msg
    }
    ## WARNING: This update should behave well because it uses idletasks,
    ## however, if there are weird looping problems with events, or
    ## hanging in waits, try commenting this out.
    if {$len} {update idletasks}
}

proc stringToSystemClipboard {string} {
	set toolkit [java::call java.awt.Toolkit getDefaultToolkit]
	set clipBoard [$toolkit getSystemClipboard]
	set stringTransfer [java::new java.awt.datatransfer.StringSelection $string]
	$clipBoard setContents $stringTransfer $stringTransfer
}
proc stringFromSystemClipboard {} {
	set toolkit [java::call java.awt.Toolkit getDefaultToolkit]
	set clipBoard [$toolkit getSystemClipboard]
	return [$clipBoard getContents java::null]
}

proc getClipboardFlavors {} {
	set toolkit [java::call java.awt.Toolkit getDefaultToolkit]
	set clipBoard [$toolkit getSystemClipboard]
	set transferable [$clipBoard getContents java::null]
	set dataFlavors [$transferable getTransferDataFlavors]
	if {$dataFlavors == [java::null]} {
		return ""
	}
	set nFlavors [$dataFlavors length]
	for {set i 0} {$i < $nFlavors} {incr i} {
		set flavor [$dataFlavors get $i]
		puts [$flavor toString]
	}
}
# tkTextSetCursor
# Move the insertion cursor to a given position in a text.  Also
# clears the selection, if there is one in the text, and makes sure
# that the insertion cursor is visible.  Also, don't let the insertion
# cursor appear on the dummy last line of the text.
#
# Arguments:
# w -           The text window.
# pos -         The desired new position for the cursor in the window.



