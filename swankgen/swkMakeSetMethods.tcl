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

proc swkMakeSetMethods {methodGroups} {
global specialGets widgetVar components tkcomps resourceNames specialConfig optionList widgetType
        set cmdElse1 "\} else if (argv\[i\].toString().equals(\""
        set cmdElse2 "\")) \{"
       array set complexTypes {anchor float[] anchor2 int[] tkRectangle Rectangle 
                                    tkRectangleCorners int[][] javax.swing.Icon javax.swing.Icon 
                                    java.io.File java.io.File java.net.URL java.net.URL menu Object options Object[] spinlist java.util.List}
       array set simpleTypes {java.lang.String s int i orient i textvariable s wrap s variable s justify s state s tkSize i tkSizeD d tkSizeDI d tkRelief s tkSelectMode i boolean b float f double d}

catch "unset optionList"
set setMethods ""
set deprecated {"setCursor int"}
foreach "methodClass methods" $methodGroups {
    foreach method $methods  {
        if {[lsearch $deprecated $method] != -1} {
                continue
        }
       if {[regexp  {(set)(.*)( )(.*)} $method all a b c]} {
		if {([lsearch $methods get$b ] >= 0) || ([lsearch $methods is$b ] >= 0)} {
			if {[llength $method]==2} {
				set option [lindex $method 0]
				set argType [lindex $method 1]
				set rootPart $b
				lappend setMethods "$option $argType $rootPart {} $methodClass"
			} else {
			}
		}
        }
    }
}

set resourceNames ""
set dashOptions ""
set excludes "-locale -page -styleddocument -actioncommand -armed -autocreatecolumnsfrommodel -borderpainted -borderpaintedflat -columns
-defaultcloseoperation -delay -displayedmnemonicindex -dragenabled -editingcolumn -editingrow -focuspainted -lastdividerlocation
-layer -popupmenuvisible -popupvisible -scrolloffset -wantsinput -alignmentx -alignmenty -bounds -classname -debuggraphicsoptions
-doublebuffered -horizontalalignment -ignorerepaint -inheritspopupmenu -location -margin -maximumsize -minimumsize -name -preferredsize
-size -verticalalignment
"
set excludeTypes "java.util.Locale java.lang.String {}"
#puts $specialGets
#puts ""
#puts $setMethods
set setMethods [concat $specialGets $setMethods ]
set result ""
foreach method $setMethods {
puts $method
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
				set getMethod get$rootPart
				set varName [string tolower $rootPart]
#				set rootPart [string tolower $rootPart]
				#set dashOption -[string tolower [string range $option 3 3]][string range $option 4 end]
				set resourceName [string tolower [string range $rootPart 0 0]][string range $rootPart 1 end]
				if {$dashOption == {}} {
					set dashOption -[string tolower  $rootPart]
				}
				set optPos [lsearch $excludes $dashOption] 
				if {$optPos >=  0} {
				    set excludeType [lindex $excludeTypes $optPos]
				    if {($excludeType == {}) || ($excludeType == $argType)} {
				    puts "skip $method"
				    continue
				}
				
				}
				
				if {[lsearch $dashOptions $dashOption] >=  0} {
				    puts "$dashOption in dashOptions"
					continue
				}
				set gotMethod 1
				lappend dashOptions $dashOption
puts startifs
				if {[lsearch $components $argType ] >= 0} {
   					if {![regexp  {(.*\.)(.*\.)(.*)} $argType all a b c]} {
						exit "bad $argType"
					this}
						set varType $c
						set cmd "$varType value = SwankUtil.get${varType}(interp,argv\[i+1\]);"
						append cmd "\n(new Setter((SwkWidget) this,opt)).exec(value);"
						set setter "${widgetVarLocal}.${option}(($varType) setter.oValue);"
				} elseif {$argType == "bitmap"} {
                                                set cmd [getSetter $widgetVar $argType]
						append cmd "\n(new Setter((SwkWidget) this,opt)).exec(value,sValue);"
						set setter "${widgetVarLocal}.${option}((ImageIcon) setter.oValue,(String) setter.sValue);"
				} elseif {$argType == "textvariable"} {
						set cmd "setTextVariable(interp,argv\[i+1\].toString());"
				} elseif {$argType == "variable"} {
						set cmd "setVarName(interp,argv\[i+1\].toString());"
				} elseif {[lsearch $tkcomps $argType ] >= 0} {
						set cmd "${widgetVarLocal}.${option}(SwkTk.${option}(interp,argv\[i+1\]));"
						set setter "${widgetVarLocal}.${option}(setter.iValue);"
				} elseif {[info exists simpleTypes($argType) ]} {
                                                set cmd [getSetter $widgetVar $argType]
                                                if {$rootPart == "Command"} {
						    append cmd "\n(new Setter((SwkWidget) this,opt,false)).exec(value);"
                                                } else {
						    append cmd "\n(new Setter((SwkWidget) this,opt)).exec(value);"
                                                }
						set setter "${widgetVarLocal}.${option}(setter.$simpleTypes($argType)Value);"
				} elseif {[info exists complexTypes($argType) ]} {
                                                set cmd [getSetter $widgetVar $argType]
						append cmd "\n(new Setter((SwkWidget) this,opt)).exec(value);"
						set setter "${widgetVarLocal}.${option}(($complexTypes($argType)) setter.oValue);"
                                } else {
						set gotMethod 0
						puts "no action for $method"
				}
puts endifs
				if {$gotMethod} {
					set optionList($dashOption) $cmd
					set setterList($dashOption) $setter
					set resourceList($dashOption) $resourceName
					set rootParts($dashOption) $rootPart
				}
} 

set options [lsort -dictionary [array names optionList]]
set result1 ""
set i 0
global configOptions configOPTs configCASEs setterCASEs
set configOptions "static String validCmds\[\] = \{"
set configOPTs ""
set configCASEs  "
        int opt = SwkIndex.get(interp, argv\[i\], validCmdsTM, \"option\", 0);
        switch (opt) \{
"
set setterCASEs  "
        switch (opt) \{
"

foreach option $options {
	set optionNoDash [string range $option 1 end]
	append configOptions "\"$option\",\n"
	append configOPTs "private static final int OPT_[string toupper $optionNoDash] = $i;\n"
	append configCASEs "case OPT_[string toupper $optionNoDash]: {
			$optionList($option)
                       break;
                       }
                       "
	append setterCASEs "case OPT_[string toupper $optionNoDash]: 
			$setterList($option)
                       break;
                       "
       	lappend resourceNames "$option $resourceList($option) $rootParts($option) OPT_[string toupper $optionNoDash]"
	incr i
}

append configOptions "\};"
append configCASEs "\}"
append setterCASEs "\}"
puts donewithsets
}
