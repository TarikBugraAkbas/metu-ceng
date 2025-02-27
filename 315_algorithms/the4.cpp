#include "the4.h"

// DO NOT CHANGE ABOVE THIS LINE!
// You may implement helper functions below

unsigned int alice_cutting_cake(const unsigned short cake_width,
                                const unsigned short cake_height,
                                bool **perfect_cuts){
    
     std::vector<std::vector<int>> dp(cake_width + 1, std::vector<int>(cake_height + 1));
    for(int i = 0; i <= cake_height; i++)
    {
        for(int j = 0; j <= cake_width; j++)
        {
            if(perfect_cuts[j][i])
            {
                dp[j][i] = 0;
            }
            else
            {
                dp[j][i] = i * j;
            }
            
            for(int h = 0; h < i; h++)
            {
                dp[j][i] = (dp[j][h] + dp[j][i-h]) < dp[j][i] ? (dp[j][h] + dp[j][i-h]) : dp[j][i];
            }
    
            for(int w = 0; w < j; w++)
            {
                dp[j][i] = (dp[w][i] + dp[j-w][i]) < dp[j][i] ? (dp[w][i] + dp[j-w][i]) : dp[j][i];
            }
        }
    }
    return dp[cake_width][cake_height];
}