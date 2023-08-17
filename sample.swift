func fibonacciIterative(_ n: Int) -> Int {
    if n <= 0 {
        return 0
    } else if n == 1 {
        return 1
    } else {
        var fib1 = 0
        var fib2 = 1
        var result = 0
        for _ in 2...n {
            result = fib1 + fib2
            fib1 = fib2
            fib2 = result
        }
        return result
    }
}

func fibonacciRecursive(_ n: Int) -> Int {
    if n <= 0 {
        return 0
    } else if n == 1 {
        return 1
    } else {
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2)
    }
}

let n = 10 // Change this to the desired Fibonacci number you want to compute
let iterativeResult = fibonacciIterative(n)
let recursiveResult = fibonacciRecursive(n)

print("Iterative: Fibonacci number at index \(n) is \(iterativeResult)")
print("Recursive: Fibonacci number at index \(n) is \(recursiveResult)")
