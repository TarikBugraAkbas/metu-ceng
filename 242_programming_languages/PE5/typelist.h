
#pragma once
#include "typelist.h"

template<typename ...Ts>
struct Size<List<Ts...>> {
    static constexpr auto value = sizeof...(Ts);
};

// @brief base case -- Get the 0th element of a list
template<typename T, typename... Ts>
struct At<0, List<T, Ts...>> {
    using type = T;
};

// @brief inductive case -- Get the Nth element of a list
template<int index, typename T, typename... Ts>
struct At<index, List<T, Ts...>> {
    static_assert(index > 0, "index cannot be negative");
    using type = typename At<index - 1, List<Ts...>>::type;
};

struct NotImpl;
#define NOT_IMPL NotImpl


template<typename Q, int index> //if the list is empty, then not found, return -1.
struct Find<Q, index, List<>> {
    static constexpr auto value = -1;
};

template<typename Q, int index, typename... Ts> //if same 
struct Find<Q, index, List<Q, Ts...>> {

    static constexpr auto value = index; //return the index
};

template<typename Q, int index, typename T, typename... Ts> //if not same
struct Find<Q, index, List<T, Ts...>> {

    static constexpr auto value = Find<Q,index+1,List<Ts...>>::value; //if not found then look for other part of the list.
};


template<typename Q, typename T, typename... Ts> //replace q 0 (x:xs) = q:xs
struct Replace<Q, 0, List<T, Ts...>> {
    using type = List<Q, Ts...>;
};

template<typename Q, int index, typename T, typename... Ts> //replace q i (x:xs) = x:find q (i-1) xs
struct Replace<Q, index, List<T, Ts...>> {
    using type = typename Prepend<T, typename Replace<Q, index-1, List<Ts...>>::type>::type;
};

template<typename NewItem, typename... Ts>
struct Append<NewItem, List<Ts...>> {
    using type = List <Ts..., NewItem>;
};

template<typename NewItem, typename... Ts>
struct Prepend<NewItem, List<Ts...>> {
    using type = List <NewItem, Ts...>;
};

template<template<typename> typename F, typename Ret> //map f ret [] = ret
struct Map<F, Ret, List<>> {
    using type = Ret;
};

template<template<typename> typename F, typename Ret, typename T, typename... Ts> //map f (f(x):ret) xs
struct Map<F, Ret, List<T, Ts...>> {
    using type = typename Map<F, typename Append<typename F<T>::type,Ret>::type, List<Ts...>>::type;
};


template <typename NewItem, typename T>
struct AppendList;

template<typename... Ts>
struct AppendList<List<>, List<Ts...>> {
    using type = List <Ts...>;
};

template<typename T, typename... Ts>
struct AppendList<List<T>, List<Ts...>> {
    using type = List <Ts..., T>;
};



template<bool F, typename T>
struct is_true{
    using type = List<>;
};



template<typename T>
struct is_true<true, T> {
    using type = List<T>;
};

template<template<typename> typename F, typename Ret>
struct Filter<F, Ret, List<>> {
    using type = Ret;
};


template<template<typename> typename F, typename Ret, typename T, typename... Ts>
struct Filter<F, Ret, List<T, Ts...>> {
    using type = typename Filter<F, typename AppendList<typename is_true<F<T>::value, T>::type, Ret>::type, List<Ts...>>::type;
};
