using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Threading;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static World world;
        public static Game game;
        public static Wizard self;
        public static Move move;

        public Wizard[] Opponents;
        public Minion[] OpponentMinions;
        public Building[] OpponentBuildings;

        public void Move(Wizard self, World world, Game game, Move move)
        {
            MyStrategy.world = world;
            MyStrategy.game = game;
            MyStrategy.self = self;
            MyStrategy.move = move;

            Const.Width = world.Width;
            Const.Height = world.Height;

            TreesObserver.Update(world);

            Opponents = world.Wizards
                .Where(w => w.Faction != self.Faction && !w.IsMe)
                .ToArray();
            OpponentMinions = world.Minions
                .Where(m => (m.Faction != self.Faction && (m.Faction == Faction.Academy || m.Faction == Faction.Renegades)))
                .ToArray();
            OpponentBuildings = world.Buildings
                .Where(b => b.Faction != self.Faction)
                .ToArray();

            InitializeDijkstra();

#if DEBUG
            while (Visualizer.Visualizer.Pause)
            {
                // pause here
            }
            Visualizer.Visualizer.CreateForm();
#endif
            var goTo = new Point(Const.Width - 120, 120);
            
            var target = FindTarget();
            if (target != null)
            {
                if (self.GetDistanceTo(target.X, target.Y) <= self.CastRange)
                {
                    var angle = self.GetAngleTo(target.X, target.Y);
                    if (Math.Abs(angle) <= game.StaffSector/2)
                    {
                        move.Action = ActionType.MagicMissile;
                        move.CastAngle = angle;
                    }
                    move.Turn = angle;
                }
                else
                {
                    GoAround(goTo);
                }
            }
            else
            {
                GoAround(goTo);
            }

#if DEBUG
            Visualizer.Visualizer.LookUp(new Point(self.X, self.Y));
            Visualizer.Visualizer.Draw();
            Thread.Sleep(10);
#endif
        }

        void GoAround(Point to)
        {
            var path = DijkstraFindPath(new Point(self), to);
            while (path.Count > 0 && path[0].GetDistanceTo(self) <= CellLength)
                path.RemoveAt(0);
            if (path.Count == 0)
                return;
            var pt = path[0];

            var angle = self.GetAngleTo(pt.X, pt.Y);
            var forwardSpeed = Math.Cos(angle)*game.WizardForwardSpeed;
            var strafeSpeed = Math.Sin(angle)*game.WizardStrafeSpeed;
            move.Speed = forwardSpeed;
            move.StrafeSpeed = strafeSpeed;
            move.Turn = angle;

#if DEBUG
            var tmp = Geom.Hypot(forwardSpeed / game.WizardForwardSpeed, strafeSpeed / game.WizardStrafeSpeed);
            Visualizer.Visualizer.SegmentsDrawQueue.Add(new object[] { path, Pens.Aqua });
#endif
        }

        Point FindTarget()
        {
            double minDist = int.MaxValue;
            var opponents = Opponents.Cast<Unit>().ToList();
            opponents.AddRange(OpponentBuildings.Cast<Unit>());
            opponents.AddRange(OpponentMinions.Cast<Unit>());
            Point sel = null;

            foreach (var x in opponents)
            {
                var dst = self.GetDistanceTo(x);
                if (dst < minDist)
                {
                    minDist = dst;
                    sel = new Point(x);
                }
            }
            return sel;
        }
    }
}