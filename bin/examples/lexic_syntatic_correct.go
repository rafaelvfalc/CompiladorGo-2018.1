package math;

import "fmt";
import "time";

import   "lib/math";
import m "lib/math";
import . "lib/math";

//teste comentario

/*teste 
bloco 
de 
comentario
*/

//-----------------test variavel----------------------
func main(){

	var a = "initial";
	var b, c int = 1, 2;
	var d = true;
	var e int;
	f = "short";

	fmt.Println(A + _x);
};

//-----------------------teste constant------------------------

const s string = "constant";

func teste_constante() {
    fmt.Println(s);
    const n = 500000000;
    const d = 3e20 / n;
}

//----------------teste for-------------------

func teste_for() {

	// simples
	i := 1
	for i <= 3 {
	    fmt.Println(i)
	    i = i + 1
	}

	// for classico
	for j := 7; j <= 9; j++ {
	    fmt.Println(j)
	}

	// for sem condicao (while)
	for {
	    fmt.Println("loop")
	    break
	}

	// for com continue
	for n := 0; n <= 5; n++ {
	    if n%2 == 0 {
	        continue
	    }
	    fmt.Println(n)
	}
}

//-----------------teste for com range---------------------

func teste_for_com_range() {


    nums := []int{2, 3, 4}
    sum := 0

    //range em arrays trabalham com o index e o valor. Usamos o _ quando nao precisamos do index
    for _, num := range nums {
        sum += num
    }
    fmt.Println("sum:", sum)

    //usando o index
    for i, num := range nums {
        if num == 3 {
            fmt.Println("index:", i)
        }
    }

    // no mapa usa chave/valor
    kvs := map[string]string{"a": "apple", "b": "banana"}
    for k, v := range kvs {
        fmt.Printf("%s -> %s\n", k, v)
    }

    // ou so a chave
    for k := range kvs {
        fmt.Println("key:", k)
    }

    // strings utiliza o index e o codigo unicode
    for i, c := range "go" {
        fmt.Println(i, c)
    }
}


// ------------------teste if/else-------------------
func teste_if_else() {

    // exemplo basico
    if 7%2 == 0 {
        fmt.Println("7 eh par")
    } else {
        fmt.Println("7  eh impar")
    }

    // if sem else
    if 8%4 == 0 {
        fmt.Println("8 eh divisivel por 4")
    }

    // else if
    if num := 9; num < 0 {
        fmt.Println(num, "eh negativo")
    } else if num < 10 {
        fmt.Println(num, "tem so 1 digito")
    } else {
        fmt.Println(num, "tem multiplos digitos")
    }
}

//-------------------teste switch-------------------------
func teste_switch() {

    i := 2

    switch i {
    case 1:
        fmt.Println("um")
    case 2:
        fmt.Println("dois")
    case 3:
        fmt.Println("tres")
    }

    //switch com varias expressoes
    switch time.Now().Weekday() {
    case time.Saturday, time.Sunday:
        fmt.Println("descanso")
    default:
        fmt.Println("Trabalho")
    }

    //switch sem expressao
    t := time.Now()
    switch {
    case t.Hour() < 12:
        fmt.Println("Manha")
    default:
        fmt.Println("Tarde")
    }

    // comparar tipo em vez de valor
    whatAmI := func(i interface{}) {
        switch t := i.(type) {
        case bool:
            fmt.Println("Bool")
        case int:
            fmt.Println("sou int")
        default:
            fmt.Printf("Do tipo %T\n", t)
        }
    }
    whatAmI(true)
    whatAmI(1)
    whatAmI("opa")
}

//------------------teste array--------------------------

func teste_array() {

	//criar array de tamnho fixo
    var a [5]int
    fmt.Println("emp:", a)

    // valor do index
    a[4] = 100
    fmt.Println("set:", a)
    fmt.Println("get:", a[4])

    // uso do len (tamanho do array).
    fmt.Println("len:", len(a))

    // declarar e inicializar um array
    b := [5]int{1, 2, 3, 4, 5}
    fmt.Println("dcl:", b)

    // Array multidimensional.
    var twoD [2][3]int
    for i := 0; i < 2; i++ {
        for j := 0; j < 3; j++ {
            twoD[i][j] = i + j
        }
    }
    fmt.Println("2d: ", twoD)
}


//-----------------teste funcao ------------------------

func soma(a int, b int) int {
    return a + b
}

// parametros de mesmo tipo
func soma_soma(a, b, c int) int {
    return a + b + c
}

func teste_func() {

    // chamda de funcao
    res := plus(1, 2)
    fmt.Println("1+2 =", res)

    res = plusPlus(1, 2, 3)
    fmt.Println("1+2+3 =", res)
}

//funcao com multiplos retornos
func vals() (int, int) {
    return 3, 7
}

func teste_func_mult_retornos() {

    a, b := vals()
    fmt.Println(a)
    fmt.Println(b)

    // If you only want a subset of the returned values,
    // use the blank identifier `_`.
    _, c := vals()
    fmt.Println(c)
}

//teste recursao
func fact(n int) int {
    if n == 0 {
        return 1
    }
    return n * fact(n-1)
}

func teste_recursao() {
    fmt.Println(fact(7))
}


//------------------test ponteiros----------------
func zeroval(ival int) {
    ival = 0
}

func zeroptr(iptr *int) {
    *iptr = 0
}

func teste_ponteiros() {
    i := 1
    fmt.Println("initial:", i)

    zeroval(i)
    fmt.Println("zeroval:", i)

    zeroptr(&i)
    fmt.Println("zeroptr:", i)

    fmt.Println("pointer:", &i)
}

//--------------test struct------------------
type person struct {
    name string
    age  int
}

func teste_struct() {

    // This syntax creates a new struct.
    fmt.Println(person{"Bob", 20})

    // You can name the fields when initializing a struct.
    fmt.Println(person{name: "Alice", age: 30})

    // Omitted fields will be zero-valued.
    fmt.Println(person{name: "Fred"})

    // An `&` prefix yields a pointer to the struct.
    fmt.Println(&person{name: "Ann", age: 40})

    // Access struct fields with a dot.
    s := person{name: "Sean", age: 50}
    fmt.Println(s.name)

    // You can also use dots with struct pointers - the
    // pointers are automatically dereferenced.
    sp := &s
    fmt.Println(sp.age)

    // Structs are mutable.
    sp.age = 51
    fmt.Println(sp.age)
}

//-----------------------metodos---------------

type retangulo struct {
    altura, largura int
}

func (r *retangulo) area() int {
    return r.altura * r.largura
}


func (r retangulo) perim() int {
    return 2*r.altura + 2*r.largura
}

func teste_method() {
    r := retangulo{altura: 10, largura: 5}

    fmt.Println("area: ", r.area())
    fmt.Println("perim:", r.perim())
}

// --------------operacao aritmetica------------

func teste_aritmetico() {
	var a,b,c,d,e int

	a = 1+1
	b = 1-1
	c = 2*2
	d = 2/2
	e = 8%4

	fmt.Println(a,b,c,d,e)
}

//--------------operacao booleana----------------------

//and
func teste_and() {
	var a,b,c,d bool

	a = true && true
	b = true && false
	c = false && false
	d = true && false && true && false

	fmt.Println(a,b,c,d)
}

//  or
func terste_or() {
	var a,b,c,d bool

	a = true || true
	b = true || false
	c = false || false
	d = true || false || true || false

	fmt.Println(a,b,c,d)
}

//  not
func teste_not() {
	var a,b,c,d bool

	a = !true
	b = !false
	a = !(true || true)
	b = !(true || false)

	fmt.Println(a,b,c,d)
}


//---------------operacao relacional--------------------

func teste_relacional() {

	var a int = 2

	if a == 0{
		fmt.Println(num, "eh zero")
	}
	else if a < 0 {
        fmt.Println(num, "eh negativo")
    } else if a > 0 {
        fmt.Println(num, "eh positivo")
    } else if {
        fmt.Println(num, "tem multiplos digitos")
    }


    if a <= 0 || a >= 0{
    	fmt.Println(num, "eh zero")
    }
    if a != 0 {
    	fmt.Println(num, " nao eh zero")
    }

}

//erros lexicos