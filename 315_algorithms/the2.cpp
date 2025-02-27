#include "the2.h"
int my_hash (std::string &str, int group_size, int position)
{
    int res = 0;
    int ascii, index;

    for(int i = 0; i < group_size; i++)
    {
        index = position + i;
        if(str.size() <= index)
        {
            ascii = 0;
        }
        else
        {
            ascii = str[index] - 'A' +  1;
        }
        res = res * 26 + ascii;
    }
    return res;
}


void count_sort(std::string* arr, std::string* output, const int size, const bool ascending, int group_size, int position, long &iterations)
{
    int k = pow(27,group_size);
    int* C = new int[k];
    int hashed;

    for(int i = 0; i < k; i++) //Pass 1: Initialize Count Array
    {
        C[i] = 0;
    }

    for(int j = 0; j < size; j++) //Pass 2: count the hashed counts (now contains equal to i)
    {
        iterations++;
        hashed = my_hash(arr[j], group_size, position);
        C[hashed]++;
    }

    //Pass 3: count array now contains numbers less than or equal to i.
    if(ascending)
    {
        for(int i = 1; i < k; i++) 
        {
            iterations++;
            C[i] +=C[i-1];
        }
    }
    else
    {
        for(int i = k - 2; i >= 0; i--)
        {
            iterations++;
            C[i] += C[i+1];
        }
    }
    
    for(int j = size - 1; j >= 0; j--) //Pass 4: copy the array back.
    {
        iterations++;
        hashed = my_hash(arr[j], group_size, position);
        output[C[hashed] - 1] = arr[j];
        C[hashed]--;
    }
    delete[] C;
    return;
}


long multi_digit_string_radix_sort(std::string *arr,
                                   const int size,
                                   const bool ascending,
                                   const int group_size){
    // your code here
    std::string* output = new std::string[size];
    int max_len, curr_group_size = group_size;
    long iterations = 0;
    max_len = arr[0].size();

    for(int i = 1; i < size; i++) //find the max len string
    {   
        if(arr[i].size() > max_len)
        {
            max_len = arr[i].size();
        }
    }
    int num_groups = (max_len+ group_size - 1) / group_size;

    for (int j = 0; j < num_groups; j++) 
    {
        int position = max_len - (j + 1) * group_size;
        if (position < 0) 
        {
            position = 0;
            curr_group_size = max_len % group_size;
        }
        count_sort(arr, output, size, ascending, curr_group_size, position, iterations);

        //copy back to original array
        for (int i = 0; i < size; ++i) {
            iterations++;
            arr[i] = output[i];
        }
    }
    // do not forget to update the return value!
    delete[] output;
    return iterations;
}