using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        void deikstra()
        {
            int startX = self.X;
            int startY = self.Y;
            int[,] d = new int[world.Width, world.Height];
            var p = new Pair<int, int>[world.Width, world.Height];
            for(int i = 0; i < world.Width; i++)
                for(int j = 0; j < world.Height; j++)
                    d[i, j] = Inf;
            d[startX, startY] = danger[startX, startY];
            var q = new PriorityQueue<Pair<int, Pair<int, int>>>();
            q.Push(new Pair<int, Pair<int, int>>(danger[startX, startY], new Pair<int, int>(startX, startY)));

            int[] _i = { 0, 0, 1, -1 };
            int[] _j = { 1, -1, 0, 0 };

	        while (!q.Empty()) 
            {
                var v = q.Top().second;
                var cur_d = -q.Top().first;
		        q.Pop();
		        if (cur_d > d[v.first, v.second])  
                    continue;

                for (int k = 0; k < 4; k++)
                {
                    int ni = _i[k] + v.first;
                    int nj = _j[k] + v.second;
                    int dist = d[v.first, v.second] + danger[ni, nj];
                    if (ni >= 0 && nj >= 0 && ni < world.Width && nj < world.Height && map[ni, nj] == 0 && dist < d[ni, nj])
                    {
                        d[ni, nj] = dist;
                        p[ni, nj] = v;
                        q.Push(new Pair<int, Pair<int, int>>(-dist, v));
                    }
		        }
	        }
        }
    }
}
