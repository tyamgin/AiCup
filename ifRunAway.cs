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
            int[,] d = new int[width, height];
            var p = new Pair<int, int>[width, height];
            for(int i = 0; i < width; i++)
                for(int j = 0; j < height; j++)
                    d[i, j] = Inf;
            d[startX, startY] = danger[startX, startY];
            var q = new PriorityQueue<Pair<int, Pair<int, int>>>();
            q.Push(new Pair<int, Pair<int, int>>(danger[startX, startY], new Pair<int, int>(startX, startY)));

	        while (!q.Empty()) 
            {
                var v = q.Top().second;
                var cur_d = -q.Top().first;
		        q.Pop();
		        if (cur_d > d[v.first, v.second])  
                    continue;

                foreach(Point n in Nearest(v.first, v.second, null))
                {
                    int dist = d[v.first, v.second] + danger[n.X, n.Y];
                    if (dist < d[n.X, n.Y])
                    {
                        d[n.X, n.Y] = dist;
                        p[n.X, n.Y] = v;
                        q.Push(new Pair<int, Pair<int, int>>(-dist, v));
                    }
		        }
	        }
        }
    }
}
