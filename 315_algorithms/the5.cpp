#include "the5.h"

void selection_sort(std::vector<int>& arr) {
    int size = arr.size();
    int min_idx;

    for (int i = 0; i < size; i++) {
        min_idx = i;
        for (int j = i + 1; j < size; j++) {
            if (arr[j] < arr[min_idx]) {
                min_idx = j;
            }
        }
        if (i != min_idx) {
            int temp = arr[min_idx];
            arr[min_idx] = arr[i];
            arr[i] = temp;
        }
    }
}

void dfs_mark(int begin, const std::vector< std::vector<bool> > &dependencies, std::vector<bool>& visited, std::vector<int> &finishorder)
{
    visited[begin] = true;
    for(int i = 0; i < dependencies[begin].size(); i++)
    {
        if(dependencies[begin][i] == 1 && !visited[i])
        {
            dfs_mark(i,dependencies,visited, finishorder);
        }
    }
    finishorder.push_back(begin);
    //store finish time order in a vector


}

void dfs_collect(int begin, std::vector<std::vector<bool> > &transposed, std::vector<bool>& visited, std::vector<int> &component)
{
    visited[begin] = true;
    component.push_back(begin);
    for(int i = 0; i < transposed[begin].size(); i++)
    {
        if(transposed[begin][i] == 1 && !visited[i])
        {
            dfs_collect(i, transposed, visited, component);
        }
    }
}

void transpose(const std::vector< std::vector<bool> > &dependencies, std::vector<std::vector<bool> > &transposed, int r, int c)
{
    for(int i = 0; i < r; i++)
    {
        for(int j = 0; j < c; j++)
        {
            transposed[j][i] = dependencies[i][j];
        }
    }

}
std::vector<std::vector<int>> kosaraju(const std::vector< std::vector<bool> > &dependencies)
{
    int row_size = dependencies.size();
    int col_size = dependencies[0].size();
    std::vector<bool>visited(row_size,false);
    std::vector<std::vector<bool> > transposed(col_size, std::vector<bool>(row_size, false)); 
    std::vector<int> finishorder;
    std::vector< std::vector<int> > scclist;



    for (int i = row_size-1; i >= 0; i--) {
        if (!visited[i]) {
            dfs_mark(i, dependencies, visited, finishorder);
        }
    }

    transpose(dependencies, transposed, row_size, col_size);

    for(int i = 0; i < row_size; i++)
    {
        visited[i] = false;
    }

    for(int i = finishorder.size()-1; i>= 0; i--)
    {
        if(!visited[finishorder[i]])
        {
            std::vector<int> component;
            dfs_collect(finishorder[i], transposed, visited, component);
            selection_sort(component);
            scclist.push_back(component);
        }

    }
    return scclist;

}
std::vector<std::vector<int>> find_work_order(const std::vector<std::vector<bool>>& dependencies) {
    std::vector<std::vector<int>> work_order = kosaraju(dependencies);

    return work_order;
}