namespace eval ::swank::chart {
    variable currentCanvas 
    variable currentFigure 
    variable currentItem 
    variable dataID 
    variable dataTypes
    namespace export figure
    namespace export plot
    namespace export pdata
    namespace export barplot
    namespace export statplot
    namespace export exportFig
    namespace export plabel
    namespace export pconfig
    if {![info exists currentFigure]} {
        set currentFigure 1
        set dataID 1
    }
set dataTypes {
    XYArrayData 
    DefaultStatisticalCategoryData 
    DefaultCategoryData 
    XYTableStatsData 
    XYTableData 
    XYTableGroupData 
    BoxAndWhiskerTableData
}
}

proc ::swank::chart::exportFig {} {
    variable currentCanvas
   ::export $currentCanvas
}

proc ::swank::chart::figure {{figNum {}} {title {}}} {
   variable currentFigure
   variable currentCanvas
    variable currentItem 
   if {$figNum == {}} {
      set figNum 1
      while {[winfo exists .figure$figNum]} {
         incr figNum
      }
# fixme garbage collect figstuff
   }
   set currentFigure $figNum
   set figID Fig$currentFigure
   set figure figure$currentFigure
   set tl .$figure
   set canvas .$figure.c
   set currentCanvas $canvas
   if {[winfo exists $canvas]} {
        wm deiconify $tl
        raise $tl
   } else {
       if {[winfo exists $tl]} {
          set fWin [focus]
          wm deiconify  $tl
          raise $tl
          update
          focus $fWin
       } else {
          makeAll $tl $figID 
          wm geometry $tl 400x400
          if {$title eq {}} {
             set title "Figure $currentFigure"
          }
          wm title $tl $title
       }
   }
   return $canvas
}
proc ::swank::chart::processArgs {datasetID args} {
    variable currentFigure
    set nArgs [llength $args]
    set i 0
    set yArgs [list]
    set xArgs [list]
    set colors [list]
    set opts(-c) black
    set opts(-L) Y
    set opts(-l) 1
    set opts(-s) 1
    set opts(-S) ""
    set inOpt 0
    set xArg 1
    set nSeries 0
    set labels [list]
    if {[llength $args] == 0} {
        return
    }
    if {[llength $args] == 1} {
       set args [list "" [lindex $args 0]]
    }
    foreach arg $args {
        if {$inOpt} {
           set opts($optName) $arg 
           set inOpt 0
        } else {
            if {([llength $arg] == 1) && [string match -* $arg]} {
               set inOpt 1
               set optName $arg
            } else {
               if {$xArg} {
                   lappend xArgs $arg
                   set xArg 0
               } else {
                   lappend yArgs $arg
                   set xArg 1
                   lappend colors $opts(-c)
                   lappend lines $opts(-l)
                   lappend shapes $opts(-s)
                   lappend symbols $opts(-S)
                   incr nSeries
                   if {$opts(-L) eq "Y"} {
                       lappend labels Y$nSeries
                   } else {
                       lappend labels $opts(-L)
                   }
               }
            }
       }
    }
   foreach y $yArgs {
       if {([llength $y] > 1) || [string is double -strict $y]} {
           set mode listMode
           set size [llength $y]
       }  else {
           if {[catch {vecmat size $y} size]} {
               error "Vector $y doesn't exist"
           }
           set mode vecMode
       }
       lappend yvecs $y
   }
   foreach x $xArgs {
       if {$x == ""} {
           if {$mode eq "vecMode"} {
               set xvec chartx
               vecmat resize $xvec $size
               for {set i 0} {$i < $size} {incr i} {
                 vecmat set $xvec $i $i
               }
               set x $xvec
           } else {
               set x [list]
               for {set i 0} {$i < $size} {incr i} {
                   lappend x $i
               }
               set xArgs [list $x]
               break
           }
      }
     if {([llength $x] > 1) || [string is double -strict $x]} {
           set mode listMode
           set size [llength $x]
       }  else {
           if {[catch {vecmat size $x} size]} {
               error "Vector $x doesn't exist"
           }
           set xvec $x
       }
   }
   if {$mode eq "vecMode"} {
       createDatasetFromVectors $datasetID $xvec $yvecs Y
   } else {
      createDataset $datasetID $xArgs $yArgs $labels
   }
   return [list "$datasetID" $shapes $lines $colors $symbols]
}
proc ::swank::chart::getDataset {type} {
   variable currentCanvas
   variable currentItem
   set datasetID ""
   if {![catch {$currentCanvas itemcget $currentItem -dataset} cResult]} {
        set datasetID $cResult 
   }
   if {$datasetID eq ""} {
       set datasetID [getNextID $type]
   }
   return $datasetID
}
proc ::swank::chart::plot {args} {
   variable currentFigure
   variable currentCanvas
   variable currentItem
   figure $currentFigure
   set figure figure$currentFigure
   set datasetID [getDataset XYArrayData]
   lassign [eval processArgs $datasetID $args] datasetID shapes lines colors symbols
   plotXYChart "$datasetID" $shapes $lines $colors $symbols
}

proc ::swank::chart::getNextID {type} {
   variable dataID 
   set datasetID $type.$dataID
   incr dataID
   return $datasetID
}
proc ::swank::chart::getCurrentItem {{canvas {}}} {
   variable currentCanvas
   variable currentItem
   if {$canvas eq ""} {
      set canvas $currentCanvas
   }
   set selected [$canvas find withtag hselect]
   set plotItems [list]
   foreach item $selected {
       set tags [$canvas gettags $item]
       if {[lsearch $tags "plot"] != -1} {
            lappend plotItems $item
       }
   }
   if {[llength $plotItems] == 0} {
       set currentItem [$canvas find withtag plot]
   } else {
        set currentItem $plotItems
   }
   return $currentItem
}
proc ::swank::chart::pdata {args} {
   variable currentCanvas
   variable currentItem
   set currentItem [getCurrentItem]
   if {[llength $args] == 0} {
       return
   }
   if {[llength $currentItem] != 1} {
       warning "Can only add update data for canvas with one plot present (or selected)"
       return
   }
   set type [$currentCanvas type $currentItem]
   set shapes 1
   set lines 1
   set colors red
   set symbols square
   switch $type {
       xyplot {
           set datasetID [getDataset XYArrayData]
           lassign [eval processArgs $datasetID $args] datasetID shapes lines colors symbols
       }
       barplot {
           set datasetID [getDataset DefaultCategoryData]
           eval addBarData $datasetID $args
       }
       statplot {
           set datasetID [getDataset DefaultStatisticalCategoryData]
           eval addBarStatData $datasetID $args
       }
       default {
           warning "Can't update data for $type plot"
           return
       }
   }
   addDatasets "$datasetID" $shapes $lines $colors $symbols
}

proc ::swank::chart::axes {x1 y1 x2 y2} {
   setBounds  $x1 $y1 $x2 $y2
}

proc ::swank::chart::barplot {values} {
   variable currentFigure
    variable currentItem 
   figure $currentFigure
   set figure figure$currentFigure
   set tl .$figure
   update
   set datasetID [getDataset DefaultCategoryData]
   addBarData $datasetID $values
   plotBarChart $datasetID 
}
proc ::swank::chart::statplot {values} {
   variable currentFigure
   variable currentCanvas
   variable currentItem 
   figure $currentFigure
   set datasetID [getDataset DefaultStatisticalCategoryData]
   addBarStatData $datasetID $values
   plotBarStatChart $datasetID 
}

proc ::swank::chart::destroyCanvas {tl c} {
   catch {
       set plots [$c find withtag plot]
       foreach plot $plots {
           set datasets [$c itemcget $plot -dataset]
           foreach dataset $datasets {
                if {[regexp {(.*)\.([0-9]+)} $dataset all className idNum]} {
                    puts "$className $idNum"
                    set xyData [java::call com.onemoonscientific.swank.chart.$className get $dataset]
                    puts $xyData
                    if {![java::isnull $xyData]} {
                        java::call com.onemoonscientific.swank.chart.XYArrayData remove $dataset
                    }
                }
           }
       }
   } result
   puts $result
   destroy $tl
}

proc ::swank::chart::makeAll {tl figID {type xy}} {
    variable currentCanvas
    destroy $tl
    toplevel $tl
    set mbar [jmenubar $tl.mbar]
    #$tl configure -menu $mbar
    set currentCanvas $tl.c
    wm protocol $tl WM_DELETE_WINDOW "::swank::chart::destroyCanvas $tl $tl.c"
    $mbar add cascade -label File -underline 0 -menu $mbar.file
    menu $mbar.file

    $mbar.file add command -text Print -command "print $currentCanvas"
    $mbar.file add command -text Copy -command "clipboard pastewidget $currentCanvas"
    $mbar.file add command -text Close -command "destroy $tl"
    pack $tl.mbar -side top -fill x
    makeCanvas 
}

proc ::swank::chart::calculatePlotArea {chartID} {
    variable chartData
    variable subPlots
    set chartData($chartID,chartArea) [list 0.02 0.02 0.98 0.98]
    return
    set figID [getFigFromChart $chartID] 
    set c $chartData($figID,canvas)
    set width [$c cget -width]
    set height [$c cget -height]
    if {![info exists subPlots($chartID,fraction)]} {
       set subPlots($chartID,fraction) "0 0 1 1"
    }
    lassign $subPlots($chartID,fraction) fx1 fy1 fx2 fy2
    set startX [expr {$fx1*$width}]
    set startY [expr {$fy1*$height}]
    set width [expr {($fx2-$fx1)*$width}]
    set height [expr {($fy2-$fy1)*$height}]
    set px1 [expr {10+$startX}]
    set py1 [expr {10+$startY}]
    set px2 [expr {$width-10+$startX}]
    set py2 [expr {$height-30+$startY}]
    set cy2 [expr {$height-10+$startY}]

    set dx1 [expr {$px1+100}]
    set dy1 [expr {$py1+100}]
    set dw [expr {$px2-100}]
    set dh [expr {$py2-100}]
    set chartData($chartID,chartArea) [list $px1 $py1 $px2 $cy2]
}

proc ::swank::chart::plabel {bottomLabel leftLabel} {
    variable currentCanvas 
    variable currentItem 
    set c $currentCanvas
    set currentItem [$currentCanvas find withtag plot]
    foreach item $currentItem {
        $c itemconfigure $item -dlabel $bottomLabel
        $c itemconfigure $item -rlabel $leftLabel
    }
}
proc ::swank::chart::figureCanvas {{c {}}} {
    variable currentCanvas 
    if {$c ne ""} {
        set currentCanvas $c
    }
    return $currentCanvas
}
proc ::swank::chart::makeCanvas {{c {}}} {
    variable currentCanvas 
    if {$c ne ""} {
        set currentCanvas $c
    } else {
        set c $currentCanvas
    }
    destroy $c
    pack [canvas $c] -fill both -expand y
    prepareCanvas $c
}

proc ::swank::chart::addToCanvas {figureNum c} {
    variable currentCanvas
    variable currentFigure
    set currentFigure $figureNum
    set figID Fig$currentFigure
    prepareCanvas $c
}

proc ::swank::chart::prepareCanvas {c} {
    variable currentCanvas
    set currentCanvas $c
    # catch error if these already exist
    catch {
    $c newtype xyplot   com.onemoonscientific.swank.chart.XYLineAndShapeComplete 4
    $c newtype xybarplot    com.onemoonscientific.swank.chart.XYBarShapeComplete 4
    $c newtype xystatplot   com.onemoonscientific.swank.chart.XYStatShapeComplete 4
    $c newtype xybarstatplot   com.onemoonscientific.swank.chart.XYBarStatShapeComplete 4
    $c newtype statplot   com.onemoonscientific.swank.chart.StatisticalCategoryPlotShapeComplete 4
    $c newtype barplot      com.onemoonscientific.swank.chart.CategoryPlotShapeComplete 4
    $c newtype boxplot  com.onemoonscientific.swank.chart.BoxPlotShapeComplete 4
    }
    catch {::nv::objEditor::setupBindings $c}

}
proc ::swank::chart::addAxes {} {
    variable currentCanvas
    variable currentItem
    set c $currentCanvas
    set type [$c type $currentItem]
#        $c itemconfigure $currentItem -dlabel $xLabel
#        $c itemconfigure $currentItem -rlabel $yLabel
}

proc ::swank::chart::setBounds {x1 y1 x2 y2} {
    variable currentCanvas
    variable currentItem
    $currentCanvas itemconfigure $currentItem -dmin $x1 -dmax $x2 -rmin $y1 -rmax $y2
}

proc ::swank::chart::addData {dataID xy yNames {shapesVisible {}}  {linesVisible {}} {colors {}} {symbols {}}} {
    set xData [java::new java.util.ArrayList]
    set yData [java::new java.util.ArrayList]

    set i 0
    foreach "x y" $xy {
       $xData add [java::new java.lang.Double $x]
       $yData add [java::new java.lang.Double $y]
       incr i
    }
    lappend ySets $yData
    plotXYChart $dataIDs $shapesVisible $linesVisible $colors $symbols
}

proc ::swank::chart::addDatasets {dataIDs {shapesVisible {}}  {linesVisible {}} {colors {}} {symbols {}}} {
    variable currentCanvas
    set currentItem [getCurrentItem]
    if {[llength $currentItem] == 1} {
         eval $currentCanvas itemconfigure $currentItem  -dataset [list $dataIDs] -shapesvisible [list $shapesVisible] -linesvisible [list $linesVisible] -paint [list $colors] -shape [list $symbols]
    } else {
        plotXYChart $dataIDs $shapesVisible $linesVisible $colors $symbols
    }
}

proc ::swank::chart::toolTip {c mode item x y} {
    if {$mode == "enter"} {
        set tip [$c hit $item $x $y]
    } else {
        set tip {}
    }
    $c configure -tooltiptext $tip
}

proc ::swank::chart::pconfig {args} {
    variable currentCanvas
    variable currentItem 
    set c $currentCanvas
    set currentItem [getCurrentItem]
    set result [list]
    foreach item $currentItem {
        if {[llength $args] == 1} {
            lappend result  [eval $c itemcget $item $args]
        } else {
            lappend result  [eval $c itemconfigure $item $args]
        }
    }
    return $result
}

proc ::swank::chart::createDatasetFromVectors {datasetID xvec yvecs yNames} {
    set xyData [java::new com.onemoonscientific.swank.chart.XYArrayData $datasetID]
    set xData [java::new java.util.ArrayList]
    set size [vecmat size $xvec]
    for {set i 0} {$i < $size} {incr i} {
         set x [vecmat get $xvec $i]
         $xData add [java::new java.lang.Double $x]
    }
    $xyData setXValues $xData
    set ySets [list]
    set iY 0
    foreach yvec $yvecs yName $yNames {
       set yData [java::new java.util.ArrayList]
       for {set i 0} {$i < $size} {incr i} {
         set y [vecmat get $yvec $i]
         $yData add [java::new java.lang.Double $y]
       }
       $xyData setYValues $yData $iY $yName
       incr iY
    }
    return $xyData
}

proc ::swank::chart::getData {{c {}} {item {}}} {
    variable currentCanvas
    variable currentItem 
    if {$c eq ""} {
        set c $currentCanvas
    }
    if {$item eq ""} {
        set currentItem [getCurrentItem $c]
        if {[llength $currentItem] != 1} {
           warning "Can only get data for canvas with one plot present (or selected)"
           return ""
        }
        set item [lindex $currentItem 0]
    }
    set dataIDs [$c itemcget $item -dataset]
    set dataID [lindex $dataIDs 0]
    return [getDataValues $dataID]
}
proc ::swank::chart::getDataValues {dataID} {
    if {[regexp {(.*)\.([0-9]+)} $dataID all className idNum]} {
    } else {
        set className XYArrayData
    }
    #set dataObject [java::call com.onemoonscientific.swank.chart.$className get $dataID]
    set dataObject [java::call com.onemoonscientific.swank.chart.XYData get $dataID]
    set result [list]
    if {[java::isnull $dataObject]} {
        warning "Can't find data $dataID"
        return
    }
    if {($className ne "XYArrayData") && ($className ne "XYTableData")} {
        set getStdDev 0
        if {[string match *Stat* $className]} {
            set getStdDev 1
        }
        set nRows [$dataObject getRowCount]
        set nColumns [$dataObject getColumnCount]
        puts "$nRows $nColumns"
        set rowKeys [list]
        set columnKeys [list]
        for {set iRow 0} {$iRow < $nRows} {incr iRow} {
            set rowKey [$dataObject getRowKey $iRow]
            lappend rowKeys [$rowKey toString]
        }
        for {set iCol 0} {$iCol < $nColumns} {incr iCol} {
            set columnKey [$dataObject getColumnKey $iCol]
            lappend columnKeys [$columnKey toString]
        }
        set varNames [list Row Column Value]
        if {$getStdDev} {
            set varNames [list Row Column Mean StdDev]
        }
        for {set iRow 0} {$iRow < $nRows} {incr iRow} {
            set rowKey [$dataObject getRowKey $iRow]
            for {set iCol 0} {$iCol < $nColumns} {incr iCol} {
                set columnKey [$dataObject getColumnKey $iCol]
                set value [$dataObject getValue $rowKey $columnKey]
                if {![java::isnull $value]} {
                    set nV [$value doubleValue]
                    lappend result $iRow
                    lappend result $iCol
                    lappend result $nV
                    if {$getStdDev} {
                        set stdValue [$dataObject getStdDevValue $rowKey $columnKey]
                        set sV [$stdValue doubleValue]
                        lappend result $sV
                    }
                }
            }
        }
        return [list $className [list $rowKeys $columnKeys] $varNames $result]
    } else {
        set nSeries [$dataObject getSeriesCount]
        set series 0
        set varNames [list Series X Y]
        set seriesNames [list]
        for {set series 0} {$series < $nSeries} {incr series} {
            lappend seriesNames [$dataObject getSeriesName $series]
        }
        for {set series 0} {$series < $nSeries} {incr series} {
            set nItems [$dataObject getItemCount $series]
            set xValues [list]
            set yValues [list]
            for {set i 0} {$i < $nItems} {incr i} {
                lappend result $series
                lappend result [$dataObject getXValue $series $i]
                lappend result [$dataObject getYValue $series $i]
            }
        }
        return [list $className [list $seriesNames] $varNames $result]
     }
}

proc ::swank::chart::createDataset {dataID xValues yValueLists yNames} {
    set xyData [java::new com.onemoonscientific.swank.chart.XYArrayData $dataID]
    set xData [java::new java.util.ArrayList]
    $xyData setXValues $xData
    foreach x $xValues {
       $xData add [java::new java.lang.Double $x]
    }
    set i 0
    foreach yValueList $yValueLists yName $yNames {
        set yData [java::new java.util.ArrayList]
        foreach y $yValueList {
           $yData add [java::new java.lang.Double $y]
        }
        $xyData setYValues $yData $i $yName
        incr i
    }
    return $xyData
}

proc ::swank::chart::createDataset {dataID xValueLists yValueLists yNames} {
    if {[llength $xValueLists] != [llength $yValueLists]} {
        error "Number of x and y lists must be the same"
    }
    set xyData [java::new com.onemoonscientific.swank.chart.XYArrayData $dataID]
    set i 0
    foreach xValueList $xValueLists yValueList $yValueLists yName $yNames {
        if {[llength $xValueList] != [llength $yValueList]} {
            error "Number of items in x and y lists must be the same"
        }
        set xData [java::new java.util.ArrayList]
        set yData [java::new java.util.ArrayList]
        foreach x $xValueList y $yValueList {
           $xData add [java::new java.lang.Double $x]
           $yData add [java::new java.lang.Double $y]
        }
        $xyData setYValues $yData $i $yName
        $xyData setXValues $xData $i
        incr i
    }
    return $xyData
}

proc ::swank::chart::plotXYChart  {datasetIDs {shapesVisible {}}  {linesVisible {}} {colors {}} {symbols {}}} {
    variable currentCanvas
    variable currentItem
    set c $currentCanvas
    set nSeries 0
    foreach datasetID $datasetIDs {
        set xyDataset [java::call com.onemoonscientific.swank.chart.XYArrayData get $datasetID ]
        incr nSeries [$xyDataset getSeriesCount]
    }

    if {$shapesVisible == {}} {
        set shapesVisible 1
    }
    if {$linesVisible == {}} {
        set linesVisible 1
    }
    $c delete plot
    set pa "0.02 0.02 0.98 0.98"
    if {$nSeries > 1} {
       set legendState 1
    } else {
       set legendState 0
    }
    set currentItem [eval $c create xyplot $pa  -transform frac -tag [list "plot anno"] -dataset [list $datasetIDs] -legendstate $legendState -spline 10 -shapesvisible [list $shapesVisible] -linesvisible [list $linesVisible] -paint [list $colors] -shape [list $symbols]]
    addAxes
    $c lower $currentItem

}

proc ::swank::chart::addBarStatData {dataID rcv} {
    set catData [java::new com.onemoonscientific.swank.chart.DefaultStatisticalCategoryData $dataID]
    set i 0
    foreach "row column mean sdev" $rcv {
        $catData {add double double java.lang.Comparable java.lang.Comparable}  $mean $sdev [java::new java.lang.String $row] [java::new java.lang.String $column]
        incr i
    }
}
proc ::swank::chart::plotBarStatChart {dataID} {
    variable currentItem
    variable currentCanvas
    set c $currentCanvas
    $c delete plot
    set pa "0.02 0.02 0.98 0.98"
    set currentItem [eval $c create statplot $pa -transform frac  -dataset $dataID -tag [list "plot anno"]]
    addAxes 
}
proc ::swank::chart::addBarData {dataID rcv} {
    set catData [java::new com.onemoonscientific.swank.chart.DefaultCategoryData $dataID]
    set i 0
    foreach "row column value" $rcv {
        $catData {addValue double java.lang.Comparable java.lang.Comparable}  $value [java::new java.lang.String $row] [java::new java.lang.String $column]
        incr i
    }
}
proc ::swank::chart::plotBarChart {dataID} {
    variable currentItem
    variable currentCanvas
    set c $currentCanvas
    $c delete plot
    set pa "0.02 0.02 0.98 0.98"
    set currentItem [eval $c create barplot $pa  -transform frac -dataset $dataID -tag [list "plot anno"]]
    addAxes 
}

proc ::swank::chart::addXYStatChart {table xColName meanColName sDevColName} {
    plotXYStatChart $table $xColName $meanColName $sDevColName
}
proc ::swank::chart::plotXYStatChart {table xColName meanColName sDevColName} {
    variable currentCanvas
    variable currentItem
    set c $currentCanvas
    if {[catch "$table getModel" model]} {
        puts $model
        return
    }
    set modelObject [$model object]
    set xyDataTable [java::new com.onemoonscientific.swank.chart.XYTableStatsData statdata]
    if {[catch "$xyDataTable setTableModel $modelObject"]} {
        puts $model
        return
    }
    if {[catch {$table columnindex $xColName} xCol]} {
        puts $xCol
          error "No column named $xColName"
    }
    if {[catch {$table columnindex $meanColName} meanCol]} {
        puts $meanCol
          error "No column named $meanColName"
    }
    if {[catch {$table columnindex $sDevColName} sDevCol]} {
        puts $sDevCol
          error "No column named $sDevColName"
    }
    $xyDataTable setXColumn  $xCol
    $xyDataTable setMeanColumn $meanCol
    $xyDataTable setSDevColumn $sDevCol

    $c delete plot
    set pa "0.02 0.02 0.98 0.98"
    set currentItem [eval $c create xystatplot $pa -transformer frac -tag [list "plot anno"] -dataset statdata] 
    addAxes 
}

proc ::swank::chart::addBoxAndWhiskerTablePlot {table columns colors} {
    addTablePlot boxplot $table $columns $colors
}
proc ::swank::chart::addGroupedTablePlot {table columns colors} {
    addTablePlot xyplot $table $columns $colors
}
proc ::swank::chart::addTablePlot {mode table columns colors symbols} {
    variable currentCanvas
    variable currentItem
    set c $currentCanvas
    $c delete all
    if {[catch "$table getModel" model]} {
        return
    }
    array set colData $columns
    if {![info exists colData(x)]} {
        return
    }
    set modelObject [$model object]
        set gCol -1
        if {[info exists colData(x)]} {
            if {[catch {$table columnindex $colData(g)} gCol]} {
                set gCol -1
            }
        }
        if {$gCol == -1} {
            set datasetID [getNextID XYTableData]
            set xyDataTable [java::new com.onemoonscientific.swank.chart.XYTableData $datasetID]
            if {[catch "$xyDataTable setTableModel $modelObject"]} {
                return
            }
        } else {
            if {$mode eq "xyplot"} {
                set datasetID [getNextID XYTableGroupData]
                set xyDataTable [java::new com.onemoonscientific.swank.chart.XYTableGroupData $datasetID]
            } else {
                set datasetID [getNextID BoxAndWhiskerTableData]
                set xyDataTable [java::new com.onemoonscientific.swank.chart.BoxAndWhiskerTableData $datasetID]
            }
            if {$xyDataTable == [java::null]} {
                puts "null object"
                return
            }
            if {[catch "$xyDataTable setTableModel $modelObject"]} {
                return
            }
        }
        set xCol -1
        if {$colData(x) != {_Index_}} {
            catch {$table columnindex $colData(x)} xCol
        }
        $xyDataTable setXColumn $xCol
        set yCols {}
        foreach yColName $colData(y) {
                if {[catch {$table columnindex $yColName} yCol]} {
                    puts "column $yColName doesn't exist"
                    return
                }
                lappend yCols $yCol
        }
        set yCols [lsort -increasing -integer $yCols]
        if {[llength $yCols] == 0} {
            return
        }
        if {$gCol == -1} {
            $xyDataTable setYColumns [java::getinterp] $yCols
            set linesVisible [list 0]
        } else {
            if {[llength $yCols] == 0} {
                warning "Must select at least one yColumn"
                return
            }
            if {$mode eq "xyplot"} {
                if {[llength $yCols] != 1} {
                    warning "Must have exactly one yColumn selected"
                    return
                }
                $xyDataTable setYColumn $yCols
            } else {
                $xyDataTable setYColumns [java::getinterp] $yCols
            }
            $xyDataTable setGColumn $gCol
            $xyDataTable getSeries
            set nGroups [llength [$table values $gCol]]
            set linesVisible [list]
            for {set i 0} {$i < $nGroups} {incr i} {
                 lappend linesVisible 0
            }
        }
        set pa "0.02 0.02 0.98 0.98"
        
        set xLabel [string trim $colData(x) _]
        set yLabel [string trim $colData(y) _]
        if {$gCol >= 0} {
            set legendState 1
        } else {
            set legendState 0
        }
        if {$mode eq "xyplot"} {
            set item [eval $c create $mode $pa -transform frac  -tag [list "plot anno"] -dataset $datasetID -spline 10 -legendstate $legendState -shapesvisible 1 -linesvisible [list $linesVisible] -paint [list $colors] -shape [list $symbols] -dlabel $xLabel -rlabel $yLabel] 
            $c itemconfigure $item -dlabel $xLabel -rlabel $yLabel -dauto 1 -rauto 1
        } else {
            set item [eval $c create boxplot $pa -transform frac -dataset $datasetID -tag [list "plot anno"] -paint [list $colors]]
        }
        
        $c bind $item  <Enter> "::swank::chart::toolTip $c enter $item %x %y"
        $c bind $item  <Leave> "::swank::chart::toolTip $c leave $item %x %y"
        set currentItem $item
        return $item
}

proc ::swank::chart::demoXYData {{dataID demoXY}} {
    set xValues "1 2 3 4 5"
    set yValues "1 3 2 4 4.5"
    lappend xValueLists $xValues
    lappend yValueLists $yValues
   createDataset $dataID $xValueLists $yValueLists Y
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

proc ::swank::chart::demoXYY {fig} {
   set n 200
   set xValues [list]
   set yValues0 [list]
   set yValues1 [list]
   set yValueLists [list]
   for {set i 0} {$i < $n} {incr i} {
        lappend xValues $i
        lappend yValues0 [expr {sin($i/10.0)}]
        lappend yValues1 [expr {cos($i/10.0)}]
   }
   lappend xValueLists $xValues
   lappend xValueLists $xValues
   lappend yValueLists $yValues0
   lappend yValueLists $yValues1
   set datasetID [getDataset XYArrayData]
   createDataset $datasetID $xValueLists $yValueLists "Sin Cos"
   plotXYChart "$datasetID" "1 1" "1 1" "blue red"
}

proc ::swank::chart::demoBarData {datasetID} {
   set values "a 1 3.0 a 2 5.1 a 3 2.2 b 1 3.4 b 2 5.5 b 3 2.6" 
   addBarData $datasetID $values
}
proc ::swank::chart::demoStatData {datasetID} {
   set values "a 1 3.0 0.2 a 2 5.1 0.3 a 3 2.2 0.4 b 1 3.4 0.1 b 2 5.5 1.0 b 3 2.6 0.5" 
   addBarStatData $datasetID $values
}
proc ::swank::chart::demoBar {fig} {
   set values "a 1 3.0 a 2 5.1 a 3 2.2 b 1 3.4 b 2 5.5 b 3 2.6" 
   set datasetID [getDataset DefaultCategoryData]
   addBarData $datasetID $values
   plotBarChart $datasetID 
    pconfig -legendstate 1
}

proc ::swank::chart::demoStatBar {fig} {
   set values "a 1 3.0 0.2 a 2 5.1 0.3 a 3 2.2 0.4 b 1 3.4 0.1 b 2 5.5 1.0 b 3 2.6 0.5" 
   set datasetID [getDataset DefaultStatisticalCategoryData]
   addBarStatData $datasetID $values
   plotBarStatChart $datasetID 
    pconfig -legendstate 1
}

proc ::swank::chart::demo {} {
     figure XYDemo
     demoXYY XYDemo

     figure BarDemo
     demoBar BarDemo

     figure StatBarDemo
     demoStatBar StatBarDemo
}
