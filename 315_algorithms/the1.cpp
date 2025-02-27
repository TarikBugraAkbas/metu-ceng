#include "the1.h"

// DO NOT CHANGE ABOVE THIS LINE!
// you may implement helper functions below
int count2 = 0;
void myswap(unsigned short *arr, int index1, int index2)
{
    int temp = arr[index2];
    arr[index2] = arr[index1];
    arr[index1] = temp;
    return;
}

std::pair<int, int> partition(unsigned short *arr, const unsigned int size,long &swap)
{
    int i = 1;
    int j = 1;
    int k = size - 1;
    int pivot = arr[0];
    while(j <= k)
    {
        if (arr[j] == pivot)
        {
            j++;
        }
        else if (arr[j] < pivot)
        {
            swap++;
            myswap(arr,i,j);
            i++;
            j++;
        }
        else if(arr[j] > pivot)
        {
            swap++;
            myswap(arr, j, k);
            k--;
        }
        
    }
    swap++;
    myswap(arr, i-1, 0);
    return std::make_pair(i-1,j);
}

void select_k_with_quick_sort3_helper(unsigned short *arr, const unsigned int size, const unsigned int index, long &swap1, int &count1)
{
    count1++;
    if(size <= 1)
    {
        return;
    }
    std::pair<int, int> part = partition(arr, size, swap1);
    int right_pivot = part.second;
    int left_pivot = part.first;

    select_k_with_quick_sort3_helper(arr, left_pivot, index, swap1, count1);
    select_k_with_quick_sort3_helper(arr + right_pivot, size - right_pivot, index - right_pivot, swap1, count1);
    return;
}


std::pair<unsigned short, unsigned int> select_k_with_quick_sort3(unsigned short *arr,
                                                                  const unsigned int size,
                                                                  const unsigned int index,
                                                                  long &swap){
    int count1 = 0;
    long swap1 = 0;
    select_k_with_quick_sort3_helper(arr,size,index,swap1, count1);
    swap = swap1;
    return std::make_pair(arr[index], count1);
}


std::pair<unsigned short, unsigned int> quick_select3(unsigned short *arr,
                                                      const unsigned int size,
                                                      const unsigned int index,
                                                      long &swap){
    count2++;
    if(size < 1)
    {
        return std::make_pair(arr[index], count2);
    }
    std::pair<int, int> part = partition(arr, size, swap);
    int right_pivot = part.second;
    int left_pivot = part.first;
    

    if(index < left_pivot)
    {
        //count++;
        quick_select3(arr, left_pivot, index, swap);
    }
    
    else if(index >= right_pivot)
    {
        //count++;
        quick_select3(arr + right_pivot, size - right_pivot, index - right_pivot, swap);
    }
    else
    {
        return std::make_pair(arr[index], count2);
    }
}
