namespace eval ::swank::chart {
    variable chartData
}
proc ::swank::chart::makeAll {tl chart xLabel yLabel {type xy}} {
    variable chartData
    destroy $tl
    toplevel $tl
    makeCanvas $chart $tl.c $xLabel $yLabel $type
}

proc ::swank::chart::calculatePlotArea {chart} {
    variable chartData
    set c $chartData($chart,canvas)
    set width [$c cget -width]
    set height [$c cget -height]
    set px1 10
    set py1 10
    set px2 [expr {$width-10}]
    set py2 [expr {$height-30}]
    set cy2 [expr {$height-10}]

    set dx1 [expr {$px1+100}]
    set dy1 [expr {$py1+100}]
    set dw [expr {$px2-100}]
    set dh [expr {$py2-100}]
    set chartData($chart,plotArea) [list $px1 $py1 $px2 $py2]
    set chartData($chart,chartArea) [list $px1 $py1 $px2 $cy2]
    set chartData($chart,dataArea) [list $dx1 $dy1 $dw $dh]
}

proc ::swank::chart::makeCanvas {chart c xLabel yLabel {type xy}} {
    variable chartData
    set chartData($chart,canvas) $c
    destroy $c
    pack [canvas $c] -fill both -expand y
    bind [winfo toplevel $c] <Configure> "::swank::chart::refreshChart $chart"
    calculatePlotArea $chart
    $c delete bottomAxis
    $c delete leftAxis
    $c newtype laxis com.onemoonscientific.swank.chart.LogarithmicAxisShape 8
    $c newtype naxis com.onemoonscientific.swank.chart.NumberAxisShape 8
    $c newtype caxis com.onemoonscientific.swank.chart.CategoryAxisShape 8
    $c newtype xyplot com.onemoonscientific.swank.chart.XYLineAndShape 4
    $c newtype xybar com.onemoonscientific.swank.chart.XYBar 4
    $c newtype xystat com.onemoonscientific.swank.chart.XYStatShape 4
    $c newtype barchart com.onemoonscientific.swank.chart.CategoryPlotShape 4
    $c newtype boxplot com.onemoonscientific.swank.chart.BoxPlotShape 4
    $c newtype legend com.onemoonscientific.swank.chart.LegendShape 4
    set chartData($chart,type) $type
    set chartData($chart,xLabel) $xLabel
    set chartData($chart,yLabel) $yLabel
}

proc ::swank::chart::addAxes {chart} {
    variable chartData
    set c $chartData($chart,canvas)
    $c delete bottomAxis
    $c delete leftAxis
    set type $chartData($chart,type)
    set xLabel $chartData($chart,xLabel)
    set yLabel $chartData($chart,yLabel)
    if {$type == "xy"} {
        eval $c create naxis $chartData($chart,plotArea)  $chartData($chart,dataArea) -edge bottom -cursor 150 -tag bottomAxis -label [list $xLabel]
    } elseif {$type == "xybar"} {
        eval $c create naxis $chartData($chart,plotArea)  $chartData($chart,dataArea) -edge bottom -cursor 150 -tag bottomAxis -label [list $xLabel]
    } else {
        eval $c create caxis $chartData($chart,plotArea)  $chartData($chart,dataArea) -edge bottom -cursor 150 -tag bottomAxis -label [list $xLabel]
    }
    eval $c create naxis $chartData($chart,plotArea)  $chartData($chart,dataArea) -edge left -cursor 150 -tag leftAxis -label [list $yLabel]
}

proc ::swank::chart::setBounds {chart x1 y1 x2 y2} {
    variable chartData
    set c $chartData($chart,canvas)
    $c itemconfigure bottomAxis -min $x1 -max $x2
    $c itemconfigure leftAxis -min $y1 -max $y2
}

proc ::swank::chart::addData {chart xy yNames {shapesVisible {}}  {linesVisible {}} {colors {}}} {
    variable chartData
    set c $chartData($chart,canvas)
    set xData [java::new java.util.ArrayList]
    set yData [java::new java.util.ArrayList]

    set i 0
    foreach "x y" $xy {
       $xData add [java::new java.lang.Double $x]
       $yData add [java::new java.lang.Double $y]
       incr i
    }
    lappend ySets $yData
    set chartData($chart,xData) $xData
    set chartData($chart,ySets) $ySets
    set chartData($chart,yNames) $yNames
    set chartData($chart,shapesVisible) $shapesVisible
    set chartData($chart,linesVisible) $linesVisible
    set chartData($chart,colors) $colors
    refreshChart $chart 
}


proc ::swank::chart::toolTip {c mode item x y} {
    if {$mode == "enter"} {
        set tip [$c hit $item $x $y]
    } else {
        set tip {}
    }
    $c configure -tooltiptext $tip
}

proc ::swank::chart::refreshChart {chart} {
    variable chartData
    if {$chartData($chart,type) == "xy"} {
        refreshXYChart $chart
    } else {
        refreshBarChart $chart
    }
}
proc ::swank::chart::refreshXYChart {chart} {
    variable chartData
    set c $chartData($chart,canvas)
    set xData $chartData($chart,xData) 
    set yDatasets $chartData($chart,ySets) 
    set yNames $chartData($chart,yNames) 
    set shapesVisible $chartData($chart,shapesVisible) 
    set linesVisible $chartData($chart,linesVisible) 
    set colors $chartData($chart,colors)
    set xyData [java::new com.onemoonscientific.swank.chart.XYArrayData test]
    $xyData setXValues $xData
    set i 0
    foreach yData $yDatasets yName $yNames {
        $xyData setYValues $yData $i $yName
        incr i
    }
    if {$shapesVisible == {}} {
        set shapesVisible 1
    }
    if {$linesVisible == {}} {
        set linesVisible 1
    }
    $c delete tag plot
    $c delete tag legend
    calculatePlotArea $chart
    addAxes $chart
    if {[llength $yNames] > 1} {
         set pa $chartData($chart,plotArea)
    } else {
         set pa $chartData($chart,chartArea)
    }
    set item [eval $c create xyplot $pa  -tag plot -domainaxis bottomAxis -rangeaxis leftAxis  -dataset test -shapesvisible [list $shapesVisible] -linesvisible [list $linesVisible] -paint [list $colors]]
    if {[llength $yNames] > 1} {
        eval $c create legend $chartData($chart,chartArea) -plot plot -tag legend
    }

}

proc ::swank::chart::refreshBarChart {chart} {
    variable chartData
    set c $chartData($chart,canvas)
    set rcv $chartData($chart,rcv)

    set catData [java::new com.onemoonscientific.swank.chart.DefaultCategoryData bardata]
    set i 0
    foreach "row column value" $rcv {
        $catData {addValue double java.lang.Comparable java.lang.Comparable}  $value [java::new java.lang.String $row] [java::new java.lang.String $column]
        incr i
    }
    $c delete all
    $c delete tag barchart
    $c delete tag legend
    calculatePlotArea $chart
    addAxes $chart

    set pa $chartData($chart,plotArea)
    eval $c create barchart $pa  -domainaxis bottomAxis -rangeaxis leftAxis  -dataset bardata -tag barchart
}

proc ::swank::chart::addBarChart {chart rcv} {
    variable chartData
    set chartData($chart,rcv) $rcv
    refreshChart $chart
}

proc ::swank::chart::demoXY {chart} {
   set n 200
   set values [list]
   for {set i 0} {$i < $n} {incr i} {
        lappend values $i
        lappend values [expr {sin($i/10.0)}]
   }
   ::swank::chart::addData $chart $values Test 1 1 blue
}

proc ::swank::chart::demoBar {chart} {
   set values "a 1 3.0 a 2 5.1 a 3 2.2 b 1 3.4 b 2 5.5 b 3 2.6" 
   ::swank::chart::addBarChart $chart $values
}

proc ::swank::chart::demo {} {
    ::swank::chart::makeAll .xychart xychart X Y xy
    wm geometry .xychart 500x300+50+50
    ::swank::chart::demoXY xychart

    ::swank::chart::makeAll .barchart barchart X Y bar
    wm geometry .barchart 500x300+50+400
    ::swank::chart::demoBar barchart
}
