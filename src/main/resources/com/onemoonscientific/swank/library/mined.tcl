package require Itcl
namespace import ::itcl::*
# contains highlighting and balancing code modified from ctext.tcl in  tklib
# reformatting code is derived from http://wiki.tcl.tk/15731

catch "::itcl::delete class minEditor"
# fixme make safe, allow source
if {![interp exists testScript]} {
    set checkScriptInterp [interp create testScript]
}

$checkScriptInterp eval {
    source -url resource:/tcl/pkg/tjc/library/parse.tcl
    source -url resource:/tcl/pkg/tjc/library/parseproc.tcl
    source -url resource:/tcl/pkg/tjc/library/descend.tcl
    source -url resource:/tcl/pkg/tjc/library/compileproc.tcl
    source -url resource:/tcl/pkg/tjc/library/module.tcl
    source -url resource:/tcl/pkg/tjc/library/nameproc.tcl
    source -url resource:/tcl/pkg/tjc/library/emitter.tcl
    package require parser
    
    proc checkScriptByTJC {script} {
        global _module
        set pkg nv
        nameproc_init $pkg
        
        set _module(package) ""
       set _module(options)  "inline-containers 1 \
                inline-controls 1 \
                cache-commands 1 \
                constant-increment 0 \
                cache-variables 1  \
                inline-commands 1 \
                omit-results 1 \
                inline-expr 1"
        
        set _module(proc_options) ""
        
        set debug 1
        set nocompile 1
        parseproc_init
        set file_and_procs [list]
        set source_file fake.tcl
        if {[catch {parseproc_start $script $source_file} results]} {
            puts stderr "[module_get_filename]: Internal error while parsing Tcl file $source_file:"
            puts stderr "$results"
            return -1
        }
        set proc_tuples [lindex $results 1]
        lappend file_and_procs [list $source_file $proc_tuples]
        unset script
        
        
        foreach pair $file_and_procs {
            set proc_filename [lindex $pair 0]
            set proc_tuples [lindex $pair 1]
            if {$debug} {
                puts "processing proc_tuples for file $proc_filename"
                puts "there are [llength $proc_tuples] proc_tuples"
                puts "proc_tuples is [string length $proc_tuples] bytes long"
            }
            foreach proc_tuple $proc_tuples {
                set procStuff [lindex $proc_tuple 2]
                set procName [lindex $procStuff 1]
                set procArgs [lindex $procStuff 2]
                set procBody [lindex $procStuff 3]
                puts "$proc_filename $procName"
                set tuple [compileproc_entry_point $proc_filename $proc_tuple]
                puts "tuple is $tuple"
                if {$tuple == "ERROR"} {
                    error "Error checking script"
                    # Error caught in compileproc_entry_point, diagnostic
                    # message already printed so stop compilation now.
                    return -1
                }
            }
        }
    }
}

class minEditor {
    variable tl
    variable textWin
    variable currentFile
    variable config
    
    variable highlight
    variable highlightSpecialChars
    variable highlightRegexp
    variable highlightCharStart
    variable classes
    variable updateLines
    
    constructor {tl1} {
        set tl $tl1
        destroy $tl
        toplevel $tl
        set jscroll [jscrollpane $tl.scroll]
        pack $jscroll -side top -fill both -expand y
        set textWin [text $tl.text -wrap word -undo 1]
        bind $textWin <Command-B> "$this matchP;break"
        bind $textWin <KeyPress> "$this keyPress"

        $jscroll add $textWin
        
        set menu [jmenubar $tl.mbar]
        
        ## File Menu
        ##
        set m [menu $menu.file -label File]
        $menu add cascade -label File -menu $m
        $m add command -label "Open"  -underline 0  -command "$this openFile"
        $m add command -label "Save"  -underline 0  -command "$this saveFile"
        $m add command -label "Save As..."  -underline 0  -command "$this saveFileAs"
        set m [menu $menu.edit -label Edit]
        $menu add cascade -label Edit -menu $m
        $m add command -label "Cut"  -underline 0  -command "$this cut"
        $m add command -label "Copy"  -underline 0  -command "$this copy"
        $m add command -label "Paste"  -underline 0  -command "$this paste"
        $m add command -label "Undo"  -underline 0  -command "$textWin edit undo"
        $m add command -label "Redo"  -underline 0  -command "$textWin edit redo"
        set m [menu $menu.text -label Text]
        $menu add cascade -label Text -menu $m
        $m add command -label "Reformat"  -underline 0  -command "$this reformatWin"
        $m add command -label "Balance"  -underline 0  -command "$this matchP"
        set m [menu $menu.eval -label Eval]
        $menu add cascade -label Eval -menu $m
        $m add command -label "Evaluate"  -underline 0  -command "$this evalScript"
        $m add command -label "Check"  -underline 0  -command "$this checkScript"
        $tl configure -menu $menu
        wm protocol $tl WM_DELETE_WINDOW "$this closeEditor"
        setHighlightTcl
    }
    
    method closeEditor {} {
        destroy $tl
        ::itcl::delete object $this
    }
    method saveFile {} {
        set f2 [open $currentFile.tmp w]
        set data [$textWin get 1.0 end]
        puts $f2 $data
        close $f2
        file rename $currentFile.tmp $currentFile
    }
    method saveFileAs {} {
        set fileName [tk_getSaveFile -initialdir [pwd]]
        if {$fileName eq ""} {
            return
        }
        set f2 [open $fileName w]
        set data [$textWin get 1.0 end]
        puts $f2 $data
        close $f2
    }
    method openFile {{fileName {}}} {
        if {$fileName eq ""} {
            set fileName [tk_getOpenFile -initialdir [pwd]]
        }
        if {$fileName eq ""} {
            return
        }
        set f1 [open $fileName r]
        set data [read $f1]
        close $f1
        $textWin delete 1.0 end
        $textWin insert end $data
        $textWin see 1.0
        set currentFile $fileName
        highlight 1.0 end
    }
    method loadProc {args}  {
        set data [::swank::getProc $args]
        $textWin delete 1.0 end
        $textWin insert end $data
        $textWin see 1.0
        highlight 1.0 end
    }
    method checkScript {}  {
        set data [$textWin get 1.0 end]
        set ok 1
        if {[catch {reformat $data 4 1} error]} {
            tk_messageBox -type ok -icon warning -message $error
            set ok 0
        }
        return $ok
    }
    method evalScript {}  {
        if {![checkScript]} {
            return
        }
        set data [$textWin get 1.0 end]
        set iExec [string first "exxec " $data]
        if {$iExec != -1} {
            error "Has exec"
        } else {
            namespace eval :: $data
        }
    }
    method keyPress {} {
        set index [$textWin index insert]
        set line [lindex [split $index "."] 0]
        if {![info exists updateLines(first)]} {
            set updateLines(first) $line
            set updateLines(last) $line
        } else {
            if {$line < $updateLines(first)} {
                set updateLines(first) $line
            }
            if {$line > $updateLines(last)} {
                set updateLines(last) $line
            }
        }
        after cancel "$this updated"
        after 500 "$this updated"
    }
    method updated {} {
        highlight $updateLines(first).0 $updateLines(last).end
    }
    method updateEditor {} {
    }
    method cut {}  {
        clipboard clear
        catch {
            set txt [selection get]
            clipboard append $txt
            if {[$textWin compare sel.first >= limit]} {
                $textWin delete sel.first sel.last
            }
        }
    }
    method copy {} {
        clipboard clear
        catch {
            set txt [$textWin get sel.first sel.last]
            clipboard append $txt
        }
    }
    method paste {}  {
        if {![catch {getSelection $textWin} txt]} {
            if {[$textWin compare insert < limit]} {
                $textWin mark set insert end
            }
            $textWin insert insert $txt
            $textWin see insert
        }
    }
    method getSelection {textWin} {
        if {
            ![catch {clipboard get} txt]
        } {
            return $txt
        }
        return -code error "could not find default selection"
    }
    
    
    
    method reformatWin {} {
        set data [$textWin get sel.first sel.last]
        set start 1.0
        set end end
        if {$data ne ""} {
            set start [$textWin index "sel.first linestart"]
            set end [$textWin index "sel.last lineend"]
        }
        set data [$textWin get $start $end]
        set data [reformat $data]
        $textWin delete $start $end
        $textWin insert $start $data
        $textWin see $start
        highlight $start $end
    }
    method reformat {tclcode {pad 4} {checkFinalIndent 0}} {
        set lines [split $tclcode \n]
        set out ""
        set continued no
        set oddquotes 0
        set line [lindex $lines 0]
        set indent [expr {([string length $line]-[string length [string trimleft $line \ \t]])/$pad}]
        set origIndent $indent
        set pad [string repeat " " $pad]
        
        foreach orig $lines {
            set newline [string trim $orig \ \t]
            set line [string repeat $pad $indent]$newline
            if {[string index $line end] eq "\\"} {
                if {!$continued} {
                    incr indent 2
                    set continued yes
                }
            } elseif {$continued} {
                incr indent -2
                set continued no
            }
            
            if { ! [regexp {^[ \t]*\#} $line] } {
                
                # oddquotes contains : 0 when quotes are balanced
                # and 1 when they are not
                set oddquotes [expr {([count $line \"] + $oddquotes) % 2}]
                if {! $oddquotes} {
                    set  nbbraces  [count $line \{]
                    incr nbbraces -[count $line \}]
                    set brace   [string equal [string index $newline end] \{]
                    set unbrace [string equal [string index $newline 0] \}]
                    if {$nbbraces>0 || $brace} {
                        incr indent $nbbraces ;# [GWM] 010409 multiple open braces
                    }
                    if {$nbbraces<0 || $unbrace} {
                        incr indent $nbbraces ;# [GWM] 010409 multiple close braces
                        if {$indent<0} {
                            error "unbalanced braces"
                        }
                        set np [expr {$unbrace? [string length $pad]:-$nbbraces*[string length $pad]}]
                        set line [string range $line $np end]
                    }
                } else {
                    # unbalanced quotes, preserve original indentation
                    set line $orig
                }
            }
            append out $line\n
        }
        if {$checkFinalIndent && ($indent != $origIndent)} {
            error "Unbalanced brace"
        }
        return [string trimright $out]
    }
    method eol {} {
        switch -- $::tcl_platform(platform) {
            windows {return \r\n}
            unix {return \n}
            macintosh {return \r}
            default {error "no such platform: $::tc_platform(platform)"}
        }
    }
    
    method count {string char} {
        set count 0
        while {[set idx [string first $char $string]]>=0} {
            set backslashes 0
            set nidx $idx
            while {[string equal [string index $string [incr nidx -1]] \\]} {
                incr backslashes
            }
            if {$backslashes % 2 == 0} {
                incr count
            }
            set string [string range $string [incr idx] end]
        }
        return $count
    }
    
    method addHighlightClass {class color keywords} {
        foreach word $keywords {
            set highlight($word) [list $class $color]
        }
        #$textWin tag configure $class
        
        set classes($class) [list $keywords]
    }
    
    #For [ ] { } # etc.
    method addHighlightClassForSpecialChars {class color chars} {
        set charList [split $chars ""]
        
        foreach char $charList {
            set highlightSpecialChars($char) [list $class $color]
        }
        #$textWin tag configure $class
        
        set classes($class) [list $charList]
    }
    
    method addHighlightClassForRegexp {class color re} {
        set highlightRegexp($class) [list $re $color]
        #$textWin tag configure $class
        
        set classes($class) [list $class]
    }
    
    #For things like $blah
    method addHighlightClassWithOnlyCharStart {class color char} {
        set highlightCharStart($char) [list $class $color]
        #$textWin tag configure $class
        
        set classes($class) [list $char]
    }
    
    
    method highlight {start end {afterTriggered 0}} {
        if {$afterTriggered} {
            set config(highlightAfterId) ""
        }
        
        #  if {!$config(-highlight)} {
        #   return
        #}
        catch {array unset updateLines}
        
        set twin $textWin
        $twin tag remove balance 1.0 end
        ::minEditor::applyHighlights $twin $start $end [array get highlight]
        ::minEditor::applyHighlightSpecialChars $twin $start $end [array get highlightSpecialChars]
        ::minEditor::applyHighlightRegexp $twin $start $end [array get highlightRegexp]
    }
    method setHighlightTcl {} {
        set color(widgets) red3
        set color(flags) orange3
        set color(stackControl) red
        set color(vars) magenta4
        set color(variable_funcs) red4
        set color(brackets) DeepPink
        set color(comments) blue4
        set color(strings) green4
        
        addHighlightClass widgets $color(widgets) \
                [list obutton button label text frame toplevel cscrollbar \
                scrollbar checkbutton canvas listbox menu menubar menubutton \
                radiobutton scale entry message tk_chooseDir tk_getSaveFile \
                tk_getOpenFile tk_chooseColor tk_optionMenu]
        
        
        addHighlightClass stackControl $color(stackControl) \
                [list proc uplevel namespace while for foreach if else]
        addHighlightClassWithOnlyCharStart  vars $color(vars) "\$"
        addHighlightClass  variable_funcs $color(variable_funcs) \
                [list set global variable unset]
        addHighlightClassForSpecialChars  brackets $color(brackets) {[]{}}
        addHighlightClassForRegexp  comments $color(comments) {\#[^\n\r]*}
        addHighlightClassForRegexp strings $color(strings) {"(\\"|[^"])*"}
    }
    
    method textWin {} {
        return $textWin
    }
    method matchP {} {
        matchPair  "\\\{" "\\\}" "\\"
    }
    
    method matchPair {str1 str2 escape} {
        set win $textWin
        $win tag remove balance 1.0 end
        
        set prevChar [$win get "insert - 2 chars"]
        if {[string equal $prevChar $escape]} {
            #The char that we thought might be the end is actually escaped.
            return
        }
        
        set searchRE "[set str1]|[set str2]"
        foreach mode "start end" {
            if {$mode eq "start"} {
                set pos [$win index "insert - 1 chars"]
                set strAct1 $str1
                set strAct2 $str2
            } else {
                set pos [$win index "insert + 1 chars"]
                set strAct2 $str1
                set strAct1 $str2
            }
            set endPair $pos
            set count 1
            set lastFound ""
            while 1 {
                if {$mode eq "start"} {
                    set found [$win search -backwards -regexp $searchRE $pos 1.0]
                    if {$found == "" || [$win compare $found > $pos]} {
                        return
                    }
                } else {
                    set found [$win search -regexp $searchRE $pos end]
                    if {$found == "" || [$win compare $found < $pos]} {
                        return
                    }
                }
                if {$lastFound != "" && [$win compare $found == $lastFound]} {
                    #The search wrapped and found the previous search
                    return
                }
                
                set lastFound $found
                set char [$win get $found]
                set prevChar [$win get "$found - 1 chars"]
                set pos $found
                if {$mode eq "end"} {
                    set pos [$win index "$found +1 char"]
                }
                
                if {[string equal $prevChar $escape]} {
                    continue
                } elseif {[string equal $char [subst $strAct2]]} {
                    incr count
                } elseif {[string equal $char [subst $strAct1]]} {
                    incr count -1
                    if {$count == 0} {
                        if {$mode eq "start"} {
                            set startPair $found
                        } else {
                            set endPair $found
                        }
                        break
                    }
                } else {
                    #This shouldn't happen.  I may in the future make it return -code error
                    puts stderr "ctext seems to have encountered a bug in ctext::matchPair"
                    return
                }
            }
        }
        $win tag add balance $startPair $endPair
        $win tag configure balance -background green
    }
    
    
    
}

proc ::minEditor::applyHighlights {twin start end values}  {
    array set highlight $values
    set si $start
    while 1 {
        set res [$twin search -count length -regexp -- {([^\s\(\{\[\}\]\)\.\t\n\r;\"'\|,]+)} $si $end]
        if {$res == ""} {
            break
        }
        
        set wordEnd [$twin index "$res + $length chars"]
        set word [$twin get $res $wordEnd]
        set firstOfWord [string index $word 0]
        
        if {[info exists highlight($word)] == 1} {
            set wordAttributes [set highlight($word)]
            foreach {tagClass color} $wordAttributes break
            
            lappend ranges($tagClass) $res $wordEnd
            set colors($tagClass) $color
            
        } elseif {[info exists highlightCharStart($firstOfWord)] == 1} {
            set wordAttributes [set highlightCharStart($firstOfWord)]
            foreach {tagClass color} $wordAttributes break
            
            lappend ranges($tagClass) $res $wordEnd
            set colors($tagClass) $color
        }
        set si $wordEnd
    }
    set tagClasses [array names colors]
    foreach tagClass $tagClasses {
        eval $twin tag add $tagClass $ranges($tagClass)
        $twin tag configure $tagClass -foreground $colors($tagClass)
    }
}
proc ::minEditor::applyHighlightSpecialChars {twin start end values}  {
    array set highlightSpecialChars $values
    foreach {ichar tagInfo} [array get highlightSpecialChars] {
        set ranges [list]
        set si $start
        foreach {tagClass color} $tagInfo break
        
        while 1 {
            set res [$twin search -- $ichar $si $end]
            if {"" == $res} {
                break
            }
            set wordEnd [$twin index "$res + 1 chars"]
            
            lappend ranges $res $wordEnd
            set colors($tagClass) $color
            set si $wordEnd
            
        }
        eval $twin tag add $tagClass $ranges
    }
    set tagClasses [array names colors]
    foreach tagClass $tagClasses {
        $twin tag configure $tagClass -foreground $colors($tagClass)
    }
}
proc ::minEditor::applyHighlightRegexp {twin start end values}  {
    array set highlightRegexp $values
    foreach {tagClass tagInfo} [array get highlightRegexp] {
        set ranges [list]
        set si $start
        foreach {re color} $tagInfo break
        while 1 {
            set res [$twin search -count length -regexp -- $re $si $end]
            if {"" == $res} {
                break
            }
            
            set wordEnd [$twin index "$res + $length chars"]
            set colors($tagClass) $color
            set si $wordEnd
            lappend ranges $res $wordEnd
        }
        eval $twin tag add $tagClass $ranges
    }
    set tagClasses [array names colors]
    foreach tagClass $tagClasses {
        $twin tag configure $tagClass -foreground $colors($tagClass)
    }
}
proc ::swank::getProc {args} {
    set whine 0
    set res ""
    foreach arg $args {
        set tProc $arg
        if {![string match ::* $arg]} {
            set tProc ::$arg
        }
        if {
            ![llength [set procs [info proc $arg]]] && ([string match *::* $arg] &&
            [llength [set ps [namespace eval \
                    [namespace qualifier $arg] \
                    info procs [namespace tail $arg]]]])
        } {
            set procs {}
            set namesp [namespace qualifier $arg]
            foreach p $ps {
                if {![string match ::* $p} {
                    lappend procs ${namesp}::$p
                } else {
                    lappend procs $p
                }
            }
        }
        if {[llength $procs]} {
            foreach p [lsort $procs] {
                set as {}
                set pArgs [info args $p]
                foreach a $pArgs {
                    if {[info default $p $a tmp]} {
                        lappend as [list $a $tmp]
                    } else {
                        lappend as $a
                    }
                }
                set pBody [info body $p]
                append res [list proc $p $as $pBody]\n
            }
        } elseif {$whine} {
            append res "\#\# No known proc $arg\n"
        set code error                }
    }
    return $res
}


