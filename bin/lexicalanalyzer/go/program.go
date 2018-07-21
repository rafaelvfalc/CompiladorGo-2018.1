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
		x10 := 123
	}
	
	func if_statements() {
		if (!true) && !false {
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
		
		//if x := f(); x < y {
		//	return x
		//}
	}
	
	func yay() bool {
		for a < b {
			a *= 2
		}
		for x == b {
			a *= 2
		}
		
		for a > 50 {
			x = 13
		}
	}
	
	func yay(x) int {
		for ;  ; {
			x = 1
		}
		for x := 0; x > 2 ; x++ {
			x = 1
		}
		for ; x > 2 ; x++ {
			x = 1
		}
		for x := 0; ; x-- {
			x = 1
		}
		for x := 0; x > 2 ; {
			x = 1
		}
		for x := a[1]; x > 2 ; {
			x = 1
		}
		for i := 0; i < 10; i++ {
			x = 1
		}
	}	
	func yay(x string) int {
		for x, y := range some_map {
			x = 1
		}
		for x := range "some_string" {
			x = 1
		}
		for i := range "some_string" {
			x = 1
		}
		for i := range some_list {
			x = 1
		}
	}	
	
	func yay(x, y) int {
	
	switch {
		case:
		default:
	}
	switch _ {
		case x:
		case y:
		default:
	}
	switch x, y {
		case "x":
			x = 1
		case "y":
			y = 1
		default:
	}
	switch x {
		case x[1]:
			if x > max {
				x = max
			}
		case x[2]:
			if y > max {
				y = max
			}
		default:
	}
	switch x {
		case 1:
			x[1] += 123
		case 2:
			x[2] += 123
		default:
	}
	switch x[1], x[2] {
	}
	//switch "teste" {
	//	default:
	//}
	//switch 1 {
	//	case:
	//}
	
	}
	
	func yay(x int, y) int {
		type x int
		type y string
	}
	
	func yay(x int, y string) int {
		[1]int
		[1][2]int
		[2][2]float64
		[1]int{x}
		[2] struct { x, y }
		[1000]*float64
		//[2] struct { x, y int32 }
	}
	
	func yay(x, y string) int {
		struct {}
		struct {x, y}
		struct {u float32}
		struct {A *[]int}
		struct {a
				b}
		struct {x
				u}
		//struct {
		//		x, y int
		//		u float32
		//		_ float32  // padding
		//		A *[]int
		//		F func()
		//		}
	}
	
	func yay(x int, y string, z bool) {
		interface {}
		//type ReadWriter interface {
		//}
	}
	
	func yay(x, y string) (int) {
		map[int]
		map[string]
		map[float32]
		map[*T]struct{ x, y }
		map[string]interface{}
		//map[*T]struct{ x, y float64 }
	}
	
	func yay(x, y ...string) (int) {
		go 1
		go 2
		go 3
	}
	
	func yay(x ...int, y string) int {
		goto label1
		goto label2
		goto label3
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
