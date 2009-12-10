proc bindObj {canvas item} {
    $canvas bind $item <1> "puts {# %# a %a b %b c %c d %d f %f h %h i %i k %k m %m o %o p %p s %s t %t w %w x %x y %y A %A B %B D %D E %E K %K N %N P %P R %R S %S T %T W %W X %X Y %Y}"
    $canvas bind $item <KeyPress-a> "puts {# %# a %a b %b c %c d %d f %f h %h i %i k %k m %m o %o p %p s %s t %t w %w x %x y %y A %A B %B D %D E %E K %K N %N P %P R %R S %S T %T W %W X %X Y %Y}"
    $canvas bind $item <1> "setFocus $canvas $item"
}
proc setFocus {canvas item} {
   $canvas focus $item
   focus $canvas
puts [focus]
}
pack [canvas .c -width 400 -height 400 -bg orange]
set item [.c create rectangle 50 50 150 150 -fill red]
bindObj .c $item
set item [.c create rectangle 100 100 200 200  -fill green]
bindObj .c $item
