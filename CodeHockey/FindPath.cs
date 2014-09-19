using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Xml.XPath;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        private Point _to;
        private double _need;
        private ArrayList _bestStack = new ArrayList();
        private int _bestTime;
        private ArrayList _stack = new ArrayList();
        private const int angles = 2;
        private const int spUps = 1;
        private const int ticksMove = 7;
        private const double okDist = 10.0;

        void rec(AHo state, int time, int deep)
        {
            if (deep > 5)
                return;
            if (time > _bestTime)
                return;

            if (state.GetDistanceTo2(_to) <= okDist*okDist)
            {
                _bestTime = time;
                return;
            }
            for (var dir = -1; dir <= 1; dir += 2)
            {
                for (var _angle = 0.0;
                    _angle <= game.HockeyistTurnAngleFactor;
                    _angle += game.HockeyistTurnAngleFactor/angles)
                {
                    if (Eq(0.0, _angle) && dir == -1)
                        continue;
                    var angle = dir*_angle;

                    for (var spUp = -1.0; spUp <= 1; spUp += 1.0/spUps)
                    {
                        var ho = state.Clone();
                        ho.Move(spUp, angle, ticksMove);
                        rec(ho, time + ticksMove, deep + 1);
                    }
                }
            }
        }

        public int FindPath(Hockeyist self, Point from, Point speed, double angle, double angularSpeed, Point to, double needAngle)
        {
            _bestTime = Inf;
            _bestStack.Clear();
            _stack.Clear();
            _to = to;
            _need = needAngle;
            var state = new AHo(from, speed, angle, angularSpeed, self);
            rec(state, 0, 0);
            return _bestTime;
        }
    }
}
