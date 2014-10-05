using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;
using Point = Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Point;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        Point GetDefendPos2()
        {
            var y = RinkCenter.Y;
            const double offset = 0.09;
            return new Point(MyLeft() ? Game.RinkLeft + RinkWidth * offset : Game.RinkRight - RinkWidth * offset, y);
        }

        public void StayOn(Hockeyist self, Point to, Point lookAt)
        {
            if (FindPath(self, to, lookAt, Get(OppGoalie)))
                return;
            DoMove(self, to, GetTicksToUp(new AHock(self), to) < GetTicksToDown(new AHock(self), to) ? 1 : -1);
        }
    }
}