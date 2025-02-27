module PE3 where

data Expression = Variable String 
    | Constant Float
    | Sine Expression
    | Cosine Expression
    | Negation Expression
    | Addition Expression Expression
    | Multiplication Expression Expression

class Differential a where
    diff :: a -> a -> a

instance Show Expression where
    show (Variable expr) = "Variable " ++ "'" ++ expr ++ "'"
    show (Constant expr) = "Constant " ++ show expr
    show (Negation expr) = "-" ++ show expr
    show (Sine expr) = "sin " ++ show expr
    show (Cosine expr) = "cos " ++ show expr
    show (Addition expr1 expr2) = "(" ++ show expr1 ++ " + " ++ show expr2 ++ ")"
    show (Multiplication expr1 expr2) = "(" ++ show expr1 ++ " * " ++ show expr2 ++ ")"

instance Eq Expression where
    (==) (Variable var1) (Variable var2) = var1 == var2;
    (==) (Constant var1) (Constant var2) = var1 == var2;
    (==) (Sine var1) (Sine var2) = var1 == var2;
    (==) (Cosine var1) (Cosine var2) = var1 == var2;
    (==) (Negation var1) (Negation var2) = var1 == var2;
    (==) (Addition expr1 expr2) (Addition expr3 expr4) = (expr1 == expr3) && (expr2 == expr4);
    (==) (Multiplication expr1 expr2) (Multiplication expr3 expr4) = (expr1 == expr3) && (expr2 == expr4); 
    (==) _ _ = False
    {-
    (/=) (Variable var1) (Variable var2) = var1 /= var2
    (/=) (Constant var1) (Constant var2) = var1 /= var2
    (/=) (Sine var1) (Sine var2) = var1 /= var2
    (/=) (Cosine var1) (Cosine var2) = var1 /= var2
    (/=) (Negation var1) (Negation var2) = var1 /= var2
    (/=) (Addition expr1 expr2) (Addition expr3 expr4) = (expr1 /= expr3) || (expr2 /= expr4)
    (/=) (Multiplication expr1 expr2) (Multiplication expr3 expr4) = (expr1 /= expr3) || (expr2 /= expr4)
    (/=) _ _ = True-}
    

instance Num Expression where
    fromInteger flt = Constant(fromInteger flt);
    
    (*) _ (Constant 0) = Constant 0
    (*) (Constant 0) _ = Constant 0
    (*) expr (Constant 1) = expr
    (*) (Constant 1) expr = expr
    (*) expr1 expr2 = Multiplication expr1 expr2
    
    (+) expr (Constant 0) = expr
    (+) (Constant 0) expr = expr
    (+) expr1 expr2 = Addition expr1 expr2
    
    negate (Constant expr) = Constant(negate expr)
    negate expr = Negation expr

    abs _ = Constant 0
    signum _ = Constant 0

instance Differential Expression where
    diff (Constant _) _ = Constant 0
    diff (Variable expr) (Variable dexpr) = if dexpr == expr then Constant 1 else Constant 0
    diff(Sine expr) (Variable dexpr) = Multiplication (Cosine expr) (diff expr (Variable dexpr))
    diff(Cosine expr) (Variable dexpr) = Multiplication (Negation (Sine expr)) (diff expr (Variable dexpr))
    diff(Negation expr) (Variable dexpr) = Negation (diff expr (Variable dexpr))
    diff (Addition expr1 expr2) (Variable dexpr) = Addition (diff expr1 (Variable dexpr)) (diff expr2 (Variable dexpr))
    diff(Multiplication expr1 expr2) (Variable dexpr) = Addition (Multiplication (diff expr1 (Variable dexpr)) expr2) (Multiplication expr1 (diff expr2 (Variable dexpr)))


shuntingyard :: [String] -> [String] -> [String] -> [String]
shuntingyard [] [] queue = queue
shuntingyard [] stack queue = queue ++ stack
shuntingyard (head:tail) stack queue
    |isOperand head = shuntingyard tail stack (queue ++ [head]) --Constant or variable goes to output queue
    |head == "(" = shuntingyard tail (head:stack) queue --( goes to top of the stack
    |head == ")" = popUntilLeft tail stack queue --Stack is emptied until (, then ( popped
    |otherwise = popUntilPrecedence (head:tail) stack queue --If the operator has higher precedence, it goes top of te stack, else the stack is emptied condition satisfied

isOperator :: String -> Bool
isOperator opr
    |opr == "+" = True
    |opr == "-" = True
    |opr == "*" = True
    |opr == "sin" = True
    |opr == "cos" = True
    |otherwise = False

isOperand :: String -> Bool
isOperand opr = not (isOperator opr) && opr /= "(" && opr /= ")"

precedence opr -- -has the most precedence
    |opr == "-" = 4
    |opr == "sin" = 3
    |opr == "cos" = 3
    |opr == "*" = 2
    |opr == "+" = 1
    |opr == "(" = 0
    |otherwise = -1
--popUntilLeft [] stack queue = shuntingyard [] stack queue

popUntilLeft :: [String] -> [String] -> [String] -> [String] --stack is emptied until ( is found
popUntilLeft tail [] queue = shuntingyard tail [] queue
popUntilLeft tail (st:stack) queue
    | st == "(" = shuntingyard tail stack queue  -- pop )
    | otherwise = popUntilLeft tail stack (queue ++ [st]) 

popUntilPrecedence [] stack queue = shuntingyard [] stack queue
popUntilPrecedence (top:rest) [] queue = shuntingyard rest [top] queue
popUntilPrecedence (top:rest) stack queue
    |precedence top > precedence (head stack) = shuntingyard rest (top:stack) queue
    |otherwise = popUntilPrecedence (top:rest) (tail stack) (queue ++ [head stack]) 
