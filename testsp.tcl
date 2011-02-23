proc fitDataPane {panel} {
    jscrollpane $panel.spane
    pack $panel.spane -fill both -expand y
    set tbl [jtable $panel.tbl]
    $tbl configure -rows 1 -cols 4 
    $panel.spane add $tbl
    update
    $tbl configure -autoresizemode 0
    set frame $panel.rmsd
    frame $frame
    pack $frame -side top -fill x
    label $frame.label -text "RMSD  "
    pack $frame.label
    update
}
proc makeTab {nm} {
    jtabbedpane $nm.tab
    set tb $nm.tab
    pack $tb -fill both  -side top
    set tabs "fitData"
    foreach tab $tabs {
         set frame [string tolower $tab]
         frame $tb.$frame
         $tb add $tb.$frame $frame
    }
    foreach tab $tabs {
        set frame [string tolower $tab]
        ${tab}Pane $tb.$frame
    }

}
toplevel .t
makeTab .t  

