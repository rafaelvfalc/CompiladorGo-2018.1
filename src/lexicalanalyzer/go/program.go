package main

import ("fmt" 
		"fmt")
import m "lib/math"
import "fmt"

// Assignment Statements
func assignments() int {
	const x += 123
	*x += 123
	x[1] += 123
	x[i] += 123
	x[teste] += 123
	(x) += 123
	var i &^= 1
	x,y,z = 1
	x[1], x[2] = 123
	var x = 1
	*x int = 123
	x int = 1
	x[1] int = 1
	x int, y = 1
	//x int, y string = 2 NOT WORKING
	//x = funcao() NOT WORKING
	//var x
	var x = 1
	x = y
	x = "abc"
	x, y = y, x
	x = x[1]
	x, y = x[i], x[2]
	*p = 1
	a[i] = 23
	a[i] <<= 2
	one, two, three = "一", "二", "三"
	_ = x
	//x := []int{1, 2, 3} NOT WORKING
	//i, x[i] = 1, 2 NOT WORKING
	x[0], x[0] = 1, 2
	a, b = "x", "y"
	x1 *= 123
	x2 <<= 123
	x3 >>= 123
	x4 /= 123
	x5 -= 123
	x6 &= 123
	x7 ^= 123
	x8 |= 123
	x9 %= 123
}

func if_statements() {
	if x > max {
		x = max
	}
	
	if x > max {
		x = max
	} else if x < max {
		x = min
	}
	
	if x > max {
		x = max
	} else {
		x = median
	}
	
	if x > max {
		x = max
	} else if x < max {
		x = min
	} else if x == max {
		x = min
	} 
	
	if x > max {
		x = max
	} else if x == max {
		x = min
	} else {
		x = median
	}
	
	if x > max {
		x = max
	} else if x < max {
		x = min
	} else if x == max {
		x = min
	} else if x == max {
		x = min
	} else if x == max {
		x = min
	} else {
		x = median
	}
}

func yay() bool {
}

func yay(x) int {
}

func yay(x string) int {
}

func yay(x, y) int {
}

func yay(x int, y) int {
}

func yay(x int, y string) int {
}

func yay(x, y string) int {
}

func yay(x int, y string, z bool) {
}

func yay(x, y string) (int) {
}

func yay(x, y ...string) (int) {
}

func yay(x ...int, y string) int {
}

func yay(x, y ...string) (string, int) {
}

func yay(x, y ...string) (string, int, bool) {
}

func yay(a, b int, z float64, opt ...interface{}) {
}

func yay(b specificType) {
}

func yay(x, y string) (success bool) {
}

func yay(x, y string) (success bool, int) {
}

func yay(x, y []string) (int) {
}

func yay(x, y string) ([]int) {
}

func yay(x, y, z) (float64, *[]int) {
}

