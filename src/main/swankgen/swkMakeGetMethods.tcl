#
# 
# Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, NJ, USA
#
# See the file \"LICENSE\" for information on usage and redistribution
# of this file.
# IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
# ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
# CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
# SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
# EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#
# THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
# PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
# IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
# DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
# SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
#
#

proc swkMakeGetMethods {methodGroups} {
global widgetType widgetVar  specialGets components tkcomps optionList
set result ""

set doneMethods ""
set getMethods ""
set deprecated {"setCursor int"}
foreach "methodClass methods" $methodGroups {

foreach method $methods  {
	if {[lsearch $deprecated $method] != -1} {
		continue
	}
   if {[regexp  {(set)(.*)( )(.*)} $method all a b c]} {
		if {[lsearch $methods get$b ] >= 0} {
			if {[lsearch $doneMethods get$b ] >= 0} {continue}
			if {[llength $method]==2} {
				set option [lindex $method 0]
				set argType [lindex $method 1]
				set rootPart $b
                                lappend getMethods "$option $argType $rootPart {} $methodClass"

			}
		}
   }
}
}

set getMethods [concat $specialGets $getMethods]
set excludes "-locale -page -styleddocument -color -actioncommand -armed -autocreatecolumnsfrommodel -borderpainted -borderpaintedflat
-columns -defaultcloseoperation -delay -displayedmnemonicindex -dragenabled -editingcolumn -editingrow -focuspainted
-lastdividerlocation -layer -popupmenuvisible -popupvisible -scrolloffset
-wantsinput -alignmentx -alignmenty -bounds -classname -debuggraphicsoptions -doublebuffered -horizontalalignment -ignorerepaint
-inheritspopupmenu -location -margin -maximumsize -minimumsize -name -preferredsize -size -verticalalignment
"
set excludeTypes "java.util.Locale java.lang.String {} int"

set dashOptions ""
foreach method $getMethods  {
     set methodClass [string tolower [lindex $method 4]]
     if {$methodClass != {}} {
                 set widgetVarLocal this.$methodClass
     } else {
                 set widgetVarLocal this
     }


				set option [lindex $method 0]
				set argType [lindex $method 1]
				set rootPart [lindex $method 2]
				set dashOption [lindex $method 3]
                                if {$dashOption == {}} {
                                        set dashOption -[string tolower  $rootPart]
                                }

				set optPos [lsearch $excludes $dashOption] 
				if {$optPos >=  0} {
				    set excludeType [lindex $excludeTypes $optPos]
				    if {($excludeType == {}) || ($excludeType == $argType)} {
#				    puts "skip $method"
				    continue
				}
				
				}
				
				if {[lsearch $dashOptions $dashOption] >=  0} {
					continue
				}
				lappend dashOptions $dashOption
				set getMethod get[string range $option 3 end]
				lappend doneMethods $getMethod
                                set gotMethod 1
				if {[lsearch $components $argType ] >= 0} {
   					if {![regexp  {(.*\.)(.*\.)(.*)} $argType all a b c]} {
						exit "bad $argType"
					}
						set varType $c
 
						set cmd "return(SwankUtil.parse${varType}($widgetVarLocal.${getMethod}()));"
					} elseif {[lsearch $tkcomps $argType ] >= 0} {
						set cmd "return(SwkTk.${getMethod}(interp,${widgetVarLocal}));"
					} elseif {$argType == "java.lang.String"} {
						set cmd "return($widgetVarLocal.${getMethod}());"
					} elseif {$argType == "tkSize"} {
						set cmd "return(SwankUtil.parseTkSize($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "tkSizeDI"} {
						set cmd "return(SwankUtil.parseTkSize($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "tkSizeD"} {
						set cmd "return(SwankUtil.parseTkSizeD($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "anchor"} {
						set cmd "return(SwankUtil.parseAnchor($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "anchor2"} {
						set cmd "return($widgetVarLocal.${getMethod}());"
					} elseif {$argType == "justify"} {
						set cmd "return($widgetVarLocal.${getMethod}());"
					} elseif {$argType == "textvariable"} {
						set cmd "return($widgetVarLocal.${getMethod}());"
					} elseif {$argType == "variable"} {
						set cmd "return($widgetVarLocal.${getMethod}());"
					} elseif {$argType == "state"} {
						set cmd "return($widgetVarLocal.${getMethod}());"
					} elseif {$argType == "tkRectangle"} {
						set cmd "return(SwankUtil.parseTkRectangle($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "tkRectangleCorners"} {
						set cmd "return(SwankUtil.parseTkRectangleCorners($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "tkRelief"} {
						set cmd "return(SwankUtil.parseTkRelief($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "tkSelectMode"} {
						set cmd "return(SwankUtil.parseTkSelectMode($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "int"} {
						set cmd "return(Integer.toString($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "boolean"} {
                                                set cmd "return(String.valueOf($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "float"} {
						set cmd "return(Float.toString($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "double"} {
						set cmd "return(Double.toString($widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "javax.swing.Icon"} {
						set cmd "return(SwankUtil.parseImageIcon( $widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "java.io.File"} {
						set cmd "return(SwankUtil.parseFile( $widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "spinlist"} {
						set cmd "return(SwankUtil.parseSpinlist( $widgetVarLocal.${getMethod}()));"
					} elseif {$argType == "java.net.URL"} {
						set cmd "return(SwankUtil.parseURL((URL) $widgetVarLocal.${getMethod}()));"
					} else {
#						puts "no action for $method"
                                                set gotMethod 0
					}
					if {$gotMethod} {
                                                set goptionList($dashOption) $cmd
					}


} 
set doneMethods ""
set isMethods ""
foreach method $methods  {
   if {[regexp  {(set)(.*)( )(.*)} $method all a b c]} {
		if {[lsearch $methods is$b ] >= 0} {
			if {[lsearch $doneMethods is$b ] >= 0} {continue}
			    set option [lindex $method 0]
			    set rootPart $b
			    if {[llength $rootPart] > 1} {continue}
			    lappend isMethods "$option $rootPart"
                }
   }
}
#set isMethods [concat $isMethods  $specialIss ]


foreach method $isMethods  {
				set option [lindex $method 0]
				set rootPart [lindex $method 1]
				if {[llength $method] == 3} {
					set dashOption [lindex $method 2]
				} else {
					set dashOption -[string tolower  $rootPart]
				}
				set isMethod is[string range $option 3 end]
				lappend doneMethods $isMethod
				if {[info exists optionList($dashOption)]} {
                                    set cmd "return(${isMethod}()?\"1\":\"0\");"
                                    set goptionList($dashOption) $cmd
				}

} 

set options [lsort -dictionary [array names goptionList]]
set i 0
global getOptions getOPTs getCASEs
set getOptions "static String validCmds\[\] = \{"
set getOPTs ""
set getCASEs  "
        switch (opt) \{
"

foreach option $options {
        set optionNoDash [string range $option 1 end]
        append getOptions "\"$option\",\n"
        append getOPTs "private static final int OPT_[string toupper $optionNoDash] = $i;\n"
        append getCASEs "case OPT_[string toupper $optionNoDash]:
                        $goptionList($option)
                       "
        incr i
}

append getOptions "\};"
append getCASEs "\}"










}
