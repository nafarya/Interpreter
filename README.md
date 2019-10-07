# Kefetator
Interpreter for kef language. 

# Documentation
All programs on Kef language start from function "main". 

## Variables declaration

Variable can be assigned with another variable or a number.

### Syntax

`[name] = [value | variable];`

Variable names must match `[a-zA-Z][a-zA-Z0-9]*` regular expression and values must match `[0-9]+`
    
### Example

```
x = 5;
b = x;
x123 = 15;
```

## Conditional expression

Kef supports conditional expressions. Second if-branch is optional and can be omitted.

### Syntax
    
```
if ([statement]) {
    ...
} else {
    ...
}
```

### Example

#### Basic usecase

```
if (a) {
    a = a + 1;
} else {
    a = a - 1;
}
```

#### Omitted second if-branch

```
if (1) {
    x = 5;
}
```
    
## Loop
    
Kef supports `for` loops with syntax cimilar to C. It has three optinal blocks:

  * **pre-action**, executed once before loop body
  * **predicate**, conditional expression to determine whether to execute current step of loop
  * **post-action**, executed every time after loop body is executed
  
### Syntax
    
```
for [preaction]; [forpredicate]; [forpostaction] {
    //for-body
}
```

### Example

Count sum of numbers from 1 to 5.

```
n = 5;
sum = 0;
for i = 1; (i - n); i = i + 1 {
    sum = sum + i;
}
print(sum);
```

## Functions

Functions declarations starts with `func` keyword, arguments are passed separated with spaces. All functions return number. 
    
### Syntax

1. name must start with [a-z], then can be [a-z] or [0-9]
2. args = Variable | Num | Function
3. returnValue = Variable | Num | Function

```
func name(args) {
    ...
    return returnValue;
}
```

### Example
    
```
func mul(a b) {
    return a * b;
}

func main() {
    a = 2;
    b = mul(a 10);
    return b;
}
```

## Print

**print** function outputs values to standard output

### Syntax

```
print([statement]);
```

### Example
    
```
a = 10;
b = 15;

func sum(a b) {
    return a + b;
}

func main() {
    print(5);
    print(a);
    print(sum(2 a));
}
```
      
# Program examples

1. Read **N** from console and calculate **factorial** of **N**

```
func f(n) {
    if (n) {
        return n * f( (n - 1) );
    } else {
        return 1;
    }
}

func main() {
    n = readInt();
    print(f(n));
    return 0;
}
```

2. Read **N** from console and calulate **N'th** Fibonacci number

```
func is1(n) {
    if ((n - 1)) {
        return 0;
    }
    return 1;
}

func is0(n) {
    if (n) {
        return 0;
    }
    return 1;
}    


func fib(n) {
    if (is0(n)) {
        return 1;
    }
    if (is1(n)) {
        return 1;
    }
    return fib((n-2)) + fib((n - 1));
}

func main() {
    n = readInt();
    m = fib(n);
    print(m);
    return 0;
}
```
        
3. Read **N** from console and print **squares** of all numbers from **1** to **N** (inclusive).

```
func sq(n) {
    return n * n;
}

func main() {
    n = readInt();
    for i = 1; (i - n - 1); i = i + 1 {
        print(sq(i));
    }
    return 0;
}
```
