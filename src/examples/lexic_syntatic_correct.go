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
};

//-----------------------teste constant------------------------

const s string = "constant";

func teste_constante() {
    fmt.Println(s);
    const n = 500000000;
    const d = 3e20 / n;
};

//----------------teste for-------------------

func teste_for() {

 // simples
 var i = 1;
 for i <= 3 {
     fmt.Println(i);
     i = i + 1;
 };

 // for classico
 var j int;
 for j =  7; j <= 9; j++ {
     fmt.Println(j);
 };

 // for sem condicao (while)
 for {
     fmt.Println("loop");
     break;
 };

 // for com continue
 var n int;
 for n = 0; n <= 5; n++ {
     if n%2 == 0 {
         continue;
     };
     fmt.Println(n);
 };
};

//-----------------teste for com range---------------------

func teste_for_com_range() {

    //usando o index
    for i, num = range nums {
        if num == 3 {
            fmt.Println( i);
        };
    };

    // ou so a chave
    
    for k = range kvs {
        fmt.Println(k);
    };

    // strings utiliza o index e o codigo unicode
    for i, c = range "go" {
        fmt.Println(i);
    };
};



// ------------------teste if/else-------------------
func teste_if_else() {

    // exemplo basico
    if 7%2 == 0 {
        fmt.Println("7 eh par");
    } else {
        fmt.Println("7  eh impar");
    };

    // if sem else
    if 8%4 == 0 {
        fmt.Println("8 eh divisivel por 4");
    };

    // else if
    var num int = 9;
    if num < 0 {
        fmt.Println( "eh negativo");
    } else if num < 10 {
        fmt.Println("tem so 1 digito");
    } else {
        fmt.Println( "tem multiplos digitos");
    };
};

//------------------teste array--------------------------

func teste_array() {

 //criar array de tamnho fixo
    var a [5]int;
    fmt.Println( a);

    // valor do index
    a[4] = 100;
    fmt.Println( a);
    fmt.Println( a[4]);

    // uso do len (tamanho do array)
    fmt.Println(len(a));
};


//-----------------teste funcao ------------------------

func soma(a int, b int) int {
    return a + b;
};

// parametros de mesmo tipo
func soma_soma(a, b, c int) int {
    return a + b + c;
};

func teste_func() {

    // chamda de funcao
    var res int; 
    
    res = soma(1);
    fmt.Println( res);
    res = soma_soma(1);
    fmt.Println( res);
};

//teste recursao
func fact(n int) int {
    if n == 0 {
        return 1;
    };
    return n * fact(n-1);
};

func teste_recursao() {
    fmt.Println(fact(7));
};


//------------------test ponteiros----------------
func zeroval(ival int) {
    ival = 0;
};
func zeroptr(iptr *int) {
    *iptr = 0;
};

func teste_ponteiros() {
    var i = 1;
    fmt.Println( i);

    zeroval(i);
    fmt.Println(i);

    zeroptr(&i);
    fmt.Println( i);

    fmt.Println( &i);
};

// --------------operacao aritmetica------------

func teste_aritmetico() {
 var a,b,c,d,e int;

 a = 1+1;
 b = 1-1;
 c = 2*2;
 d = 2/2;
 e = 8%4;

 fmt.Println(a +b +c +d +e);
};

//--------------operacao booleana----------------------

//and
func teste_and() {
 var a,b,c,d bool;

 a = true && true;
 b = true && false;
 c = false && false;
 d = true && false && true && false;

 fmt.Println(a);
};

//  or
func terste_or() {
 var a,b,c,d bool;

 a = true || true;
 b = true || false;
 c = false || false;
 d = true || false || true || false;
};

//  not
func teste_not() {
 var a,b,c,d bool;

 a = !true;
 b = !false;
 a = !(true || true);
 b = !(true || false);
};


//---------------operacao relacional--------------------

func teste_relacional() {

 var a int = 2;

 if a == 0{
   fmt.Println( "eh zero");
 } else if a < 0 {
        fmt.Println("eh negativo");
    } else if a > 0 {
        fmt.Println("eh positivo");
    } else {
        fmt.Println("tem multiplos digitos");
    };


    if a <= 0 || a >= 0{
     fmt.Println( "eh zero");
    };
    if a != 0 {
     fmt.Println(" nao eh zero");
    };

};