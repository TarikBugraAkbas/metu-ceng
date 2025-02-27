module PE2 where

data Tree k v = EmptyTree | Node k v [Tree k v] deriving (Show, Eq)

-- Question 1
selectiveMap :: (a -> Bool) -> (a -> a) -> [a] -> [a]
selectiveMap _ _ [] = []
selectiveMap select func (first:rest) -- = [func frst |frst <- lst,select frst]
    |select first = func first: selectiveMap select func rest
    |otherwise = first: selectiveMap select func rest
-- Question 2
tSelectiveMap :: (k -> Bool) -> (v -> v) -> Tree k v -> Tree k v
tSelectiveMap _ _ EmptyTree = EmptyTree
tSelectiveMap select func (Node k v subtree) = Node k newVal newSubtree where
    newVal = if select k then func v else v
    newSubtree = map ( tSelectiveMap select func ) subtree
-- Question 3

comblist acc comb [] = comb acc acc
comblist acc comb (l:ls) =
    if null ls then comb acc l else comb l (comblist acc comb ls)

tSelectiveMappingFold :: (k -> Bool) -> (k -> v -> r) -> (r -> r -> r) -> r -> Tree k v -> r
tSelectiveMappingFold _ _ _ acc EmptyTree = acc
tSelectiveMappingFold select func comb acc (Node k v subtree) = 
    if select k then comb (func k v) retval else retval
    where retval = comblist acc comb (map (tSelectiveMappingFold select func comb acc) subtree)
-- Question 4
-- This question commented out so that your file compiles even before you complete it
-- It shouldn't effect the grades of other questions when commented out
-- When ready, just remove the lines with {- and -} to uncomment the function


searchTree :: (Eq k, Eq v) => v -> Tree k v -> (k -> v)
searchTree def = tSelectiveMappingFold a b c d
  where a = const True -- predicate return true always)
        b = \k v -> \key -> if key == k then v else def
        c = \f val -> (\key -> if (f key) /= def then (f key) else val key)
        d = const def


