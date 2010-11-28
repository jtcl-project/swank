# bgerror.tcl --
#
# This file contains a default version of the bgerror procedure.  It
# posts a dialog box with the error message and gives the user a chance
# to see a more detailed stack trace.
#
# RCS: @(#) $Id: tkerror.tcl,v 1.2 2005/11/07 03:21:11 bruce_johnson Exp $
#
# Copyright (c) 1992-1994 The Regents of the University of California.
# Copyright (c) 1994-1996 Sun Microsystems, Inc.
#
# See the file "license.terms" for information on usage and redistribution
# of this file, and for a DISCLAIMER OF ALL WARRANTIES.


# bgerror --
# This is the default version of bgerror. 
# It tries to execute tkerror, if that fails it posts a dialog box containing
# the error message and gives the user a chance to ask to see a stack
# trace.
# Arguments:
# err -			The error message.

proc tkerror err {
    global errorInfo tcl_platform
    
    # save errorInfo which would be erased in the catch below otherwise.
    set info $errorInfo ;
 
    set button [tk_dialog .bgerrorDialog "Error in Tcl Script" "Error: $err" error 0 OK "Skip Messages" "Stack Trace"]
    if {$button == 0} {
	return
    } elseif {$button == 1} {
	return -code break
    }

    set w .bgerrorTrace
    catch {destroy $w}
    toplevel $w -class ErrorTrace
    wm minsize $w 1 1
    wm title $w "Stack Trace for Error"
    wm iconname $w "Stack Trace"
    wm alwaysontop $w 1
    button $w.ok -text OK -command "destroy $w" -default active
    text $w.text -relief flat -bd 2 -highlightthickness 0 -width 60 -height 20
    jscrollpane $w.spane
    $w.spane add $w.text
    pack $w.ok -side bottom -padx 3m -pady 2m
    pack $w.spane -side top -expand yes -fill both
    $w.text insert 0.0 $info
    $w.text mark set insert 0.0

    bind $w <Return> "destroy $w"
    bind $w.text <Return> "destroy $w; break"

    # Center the window on the screen.

    wm withdraw $w
    update idletasks
    set x [expr {[winfo screenwidth $w]/2 - [winfo reqwidth $w]/2 \
	    - [winfo vrootx [winfo parent $w]]}]
    set y [expr {[winfo screenheight $w]/2 - [winfo reqheight $w]/2 \
	    - [winfo vrooty [winfo parent $w]]}]
    if {$x < 0} {
	set x 0
    }
    if {$y < 0} {
	set y 0
    }
    wm geom $w +$x+$y
    wm deiconify $w

    # Be sure to release any grabs that might be present on the
    # screen, since they could make it impossible for the user
    # to interact with the stack trace.

    if {[string compare [grab current .] ""]} {
	grab release [grab current .]
    }
}