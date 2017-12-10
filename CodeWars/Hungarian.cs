/*
 * http://e-maxx.ru/algo/assignment_hungary
 */
using System;
using System.Linq;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    class HungarianAssignment
    {
        public static int[] Minimize(double[][] a, double dummyInfinity)
        {
            int n = a.Length,
                m = a[0].Length;

            var arrN = n + 1;
            var arrM = m + 1;
            if (arrN > arrM)
            {
                arrM = arrN;
                if (dummyInfinity * n >= double.PositiveInfinity)
                    throw new Exception("Dummy infinity is too large");
            }

            a = new[] { Enumerable.Repeat(0.0, m).ToArray() }.Concat(a).ToArray();

            for (var i = 0; i < a.Length; i++)
                a[i] = new[] { 0.0 }.Concat(a[i]).Concat(Enumerable.Repeat(dummyInfinity, arrM - m - 1)).ToArray();

            var result = _solve(a);
            for (var i = 0; i < result.Length; i++)
                if (result[i] >= m)
                    result[i] = -1;
            return result;
        }

        private static int[] _solve(double[][] a)
        {
            var n = a.Length - 1;
            var m = a[0].Length - 1;

            var u = new double[n + 1];
            var v = new double[m + 1];

            var p = new int[m + 1];
            var way = new int[m + 1];

            for (var i = 1; i <= n; i++)
            {
                p[0] = i;
                var j0 = 0;
                var minv = new double[m + 1];
                for (var j = 0; j < minv.Length; j++)
                    minv[j] = double.PositiveInfinity;

                var used = new bool[m + 1];
                do
                {
                    used[j0] = true;
                    int i0 = p[j0], j1 = -1;
                    var delta = double.PositiveInfinity;
                    for (int j = 1; j <= m; j++)
                    {
                        if (!used[j])
                        {
                            var cur = a[i0][j] - u[i0] - v[j];
                            if (cur < minv[j])
                            {
                                minv[j] = cur;
                                way[j] = j0;
                            }
                            if (minv[j] < delta)
                            {
                                delta = minv[j];
                                j1 = j;
                            }
                        }
                    }
                    for (int j = 0; j <= m; j++)
                    {
                        if (used[j])
                        {
                            u[p[j]] += delta;
                            v[j] -= delta;
                        }
                        else
                        {
                            minv[j] -= delta;
                        }
                    }
                    j0 = j1;
                } while (p[j0] != 0);

                do
                {
                    var j1 = way[j0];
                    p[j0] = p[j1];
                    j0 = j1;
                } while (j0 != 0);
            }
            var ans = new int[n];
            for (var j = 0; j < ans.Length; j++)
                ans[j] = -1;
	        for (var j = 1; j <= m; ++j)
                if (p[j] > 0)
		            ans[p[j] - 1] = j - 1;
	        return ans;
        }
    }
}
