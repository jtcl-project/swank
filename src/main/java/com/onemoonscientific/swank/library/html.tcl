package require java

namespace eval swank::htmlViewer {
variable urlPath
variable urlIndex
variable URL
variable status

proc viewer {{name viewer}} {
    set tl .$name
    if {[winfo exists $tl]} {
        error "window $tl already exists"
    }
    if {($name != "") && ([info command ::${name}] == "::${name}")} {
            error "Proc $name for htmlViewer already exists"
    }
    show $name
    wm geometry $tl 600x600
    return $name
}

proc show {name} {
    set tl .$name
    if {![winfo exists $tl]} {
         toplevel $tl
         wm geometry $tl 600x600
    }
    set win $tl.f
    makeWin $win
    makeProc $win $name
    wm deiconify $tl
    raise $tl
}

proc makeProc {win htmlProc} {
        set html $win.html
        if {($htmlProc != "") && ([info command ::${htmlProc}] != "::${htmlProc}")} {
        set procCode "[namespace current]::loadPage $html \$url"
        proc ::$htmlProc {url} "
            $procCode
        "
        }
}

proc makeWin {win} {
        if {[winfo exists $win]} {
             return
        }
        variable urlPath
        variable urlIndex
        variable status
        frame $win
        pack $win -fill both -expand y
	set fr1 $win.fr1
	set fr2 $win.fr2
        set html $win.html

	frame $fr1 -bd 5
	frame $fr2 -bd 5
	
        button $fr1.back -text Back -command [namespace code "pageBack $html"]
	button $fr1.forward -text Forward -command [namespace code "pageForward $html"]
	menu $fr1.list -postcommand [namespace code "postMenu $html $fr1.list"]
	menubutton $fr1.menu -menu $fr1.list -text Visits
        label $fr1.status -textvariable [namespace current]::status($html) -bd 3 -relief sunken
	pack $fr1.back $fr1.forward $fr1.menu -side left
        pack $fr1.status -padx 10 -fill x
	button $fr2.load -text "Load: " -command [namespace code "loadPage $html"]
	entry $fr2.url -textvariable [namespace current]::URL($html) -width 40
	bind $fr2.url <Return> [namespace code "loadPage $html"]
	pack $fr2.load -side left
	pack $fr2.url -side left  -fill both -expand 1
	pack $fr1 $fr2 -side top -fill x
        jscrollpane $win.spane
	pack $win.spane -fill both -expand 1 -side top
	html $html
        $win.spane add $html
	$html config -editable false 
	$html config -contenttype text/html 
        set urlPath($html) ""
        set urlIndex($html) -1
        set status($html) ""
}

proc postMenu {htmlWin menu} {
    variable urlPath
    variable urlIndex
    $menu delete 0 end
    
    set n [expr {[llength $urlPath($htmlWin)]-1}]
    for {set i $n} {$i >= 0} {incr i -1} {
	set url [lindex $urlPath($htmlWin) $i]
	if {$i == $urlIndex($htmlWin)} {
	    $menu add radiobutton -variable [namespace current]::urlIndex($htmlWin) -value $i -text $url -selected 1 -command [namespace code [list loadPage $htmlWin $url]]
	} else {
	    $menu add radiobutton -variable [namespace current]::urlIndex($htmlWin) -value $i -text $url -command [namespace code [list loadPage $htmlWin $url]]
	}	
    }
}

	
proc setPage {htmlWin {url ""}} {
    variable URL
    if {$url == ""} {
        set url $URL($htmlWin)
    }
    if {$url == ""} {
        return
    }
    $htmlWin config -page [list $url]
}
proc loadPage {htmlWin {url ""}} {
    variable URL
    if {$url == ""} {
        set url $URL($htmlWin)
    }
    if {(![string match http://* $url]) && (![string match file:/* $url])} {
        set url http://$url
    }
    set jurl [java::new java.net.URL $url]
    
    if {$jurl == [java::null]} {
        error "jurl null"
    }
    set ref [$jurl getRef]
    if {($ref == [java::null]) || ($ref == "")} {
        setPage $htmlWin $url
        return
    }
    set fileName [$jurl getFile]
    if {([string length $fileName] == 0) || [string match */ $fileName] || [[$winObj getPage] sameFile $jurl]} {
        $htmlWin scrolltoreference $ref
    } else {
        setPage $htmlWin $url
    }
}

proc hyperlink {mode htmlWin url} {
    variable status
    if {$mode == "entered"} {
        set status($htmlWin) $url
    } elseif {$mode == "exited"} {
        set status($htmlWin) ""
    } else {
        loadPage $htmlWin $url
    }
}

proc load {mode htmlWin url} {
    variable status
    variable urlPath
    variable urlIndex
    variable status
    variable URL
    if {[string equal $mode "loading"]} {
        set status($htmlWin) "loading $url"
    } elseif {[string equal $mode "loaded"]} {
        if {![info exists urlPath($htmlWin)]} {
            set urlPath($htmlWin) ""
        }
        set pathLength [llength $urlPath($htmlWin)]
        if {[info exists urlIndex($htmlWin)]} {
	    set index [lsearch $urlPath($htmlWin) $url]
	    if {$index == -1} {
	        incr urlIndex($htmlWin)
	        lappend urlPath($htmlWin) $url
	    } else {
	        set urlIndex($htmlWin) $index
	    }
       } else {
	    lappend urlPath($htmlWin) $url
	    set urlIndex($htmlWin) 0
       }
       set URL($htmlWin) $url
       set status($htmlWin) "loaded $url"
     }
}

proc pageGoto {htmlWin index} {
    variable urlPath
    variable urlIndex
    set url [lindex $urlPath($htmlWin) $index]
    set urlIndex($htmlWin) $index
    $htmlWin config -page $url
}

proc pageBack {htmlWin} {
    variable urlIndex
    if {[info exists urlIndex($htmlWin)]} {
    if {$urlIndex($htmlWin) > 0} {
	pageGoto $htmlWin [expr {$urlIndex($htmlWin)-1}]
    }
    }
}

proc pageForward {htmlWin} {
    variable urlPath
    variable urlIndex
    set pathLength [llength $urlPath($htmlWin)]
    if {[info exists urlIndex($htmlWin)]} {
    if {$urlIndex($htmlWin) < ($pathLength-1) } {
	pageGoto $htmlWin [expr {$urlIndex($htmlWin)+1}]
    }
    }
}
}
