#include "the6.h"

// do not add extra libraries here
float get_sp1(int Node, std::vector<std::vector<float> >M)
{
    int size = M[Node].size();
    float min = INT_MAX;
    for(int i = 0; i < size; i++)
    {
        if(M[Node][i] < min)
        {
            min = M[Node][i];
        }
    }
    if(min == INT_MAX) return -1;
    return min;

}


std::vector< std::vector<float> > get_friendship_scores(const std::vector< std::vector< std::pair<int, int> > >& network){
    std::vector<std::vector<float> >M((int)network.size(), std::vector<float>((int)network.size(), INT_MAX));
    std::vector<float> sp1(network.size());



    int n = network.size();
    for(int i=0; i <n; i++) //initialize Matrix vector
    {
        int m = network[i].size();
        for(int j = 0; j < m; j++)
        {
            int vertex = network[i][j].first;
            int weight = network[i][j].second;
            M[i][vertex] = weight;
        }
    }

    for(int k = 0; k < n; k++)
    {
        for(int i = 0; i<n; i++)
        {
            if(M[i][k] == INT_MAX) continue;
            for(int j = 0; j < n; j++)
            {
                if(M[k][j] != INT_MAX &&M [i][j] > (M[i][k] + M[k][j]))
                {
                        M[i][j] =M[i][k] + M[k][j];
                }
            }
        }
    }
    for(int i = 0; i < n; i++)
    {
        sp1[i] = get_sp1(i,M);
    }
    std::vector<std::vector<float> >FS(network.size(), std::vector<float>(network.size()));
    for(int i = 0; i < n; i++)
    {
        float tempsp = sp1[i];
        for(int j = 0; j <= i; j++)
        {
            if(M[i][j] == INT_MAX && M[j][i] == INT_MAX)
            {
                FS[i][j] = -1;
            }
            else if(M[i][j] == INT_MAX || M[j][i] == INT_MAX)
            {
                FS[i][j] = 0;
            }
            else
            {
                FS[i][j] = (tempsp / M[i][j]) * (sp1[j] / M[j][i]);
            }

        }
    }



    
    return FS;
}
