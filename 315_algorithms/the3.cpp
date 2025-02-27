#include "the3.h"

// DO NOT CHANGE ABOVE THIS LINE!
// you may implement helper functions below

int my_min(int a, int b, int c, int d, int e = -1, int f = -1) {
    int minimum = a;
    if (b < minimum) minimum = b;
    if (c < minimum) minimum = c;
    if (d < minimum) minimum = d;
    if (e > 0 && e < minimum) minimum = e;
    if (f > 0 && f < minimum) minimum = f;
    return minimum;
}

int find_min_cost(const std::vector<std::vector<int>>& costs, int N) {
    std::vector<int> flower1(N - 1);
    std::vector<int> flower2(N - 1);
    std::vector<int> bush1(N - 1);
    std::vector<int> bush2(N - 1);
    std::vector<int> tree1(N - 1);
    std::vector<int> tree2(N - 1);

    flower1[0] = costs[0][0];
    flower2[0] = costs[0][1];
    bush1[0] = costs[0][2];
    bush2[0] = costs[0][3];
    tree1[0] = costs[0][4];
    tree2[0] = costs[0][5];

    for (int i = 1; i < N - 1; i++) {
        flower1[i] = my_min(flower1[i - 1], bush1[i - 1], bush2[i - 1], tree1[i - 1], tree2[i - 1]) + costs[i][0];
        flower2[i] = my_min(flower2[i - 1], bush1[i - 1], bush2[i - 1], tree1[i - 1], tree2[i - 1]) + costs[i][1];
        bush1[i] = my_min(flower1[i - 1], flower2[i - 1], tree1[i - 1], tree2[i - 1]) + costs[i][2];
        bush2[i] = my_min(flower1[i - 1], flower2[i - 1], tree1[i - 1], tree2[i - 1]) + costs[i][3];
        tree1[i] = my_min(flower1[i - 1], flower2[i - 1], bush1[i - 1], bush2[i - 1]) + costs[i][4];
        tree2[i] = my_min(flower1[i - 1], flower2[i - 1], bush1[i - 1], bush2[i - 1]) + costs[i][5];
    }

    int min_cost = my_min(flower1[N - 2], flower2[N - 2], bush1[N - 2], bush2[N - 2], tree1[N - 2], tree2[N - 2]);

    return min_cost;
}
