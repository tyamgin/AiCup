using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Forms.VisualStyles;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        private bool half1Moved = false;
        private bool half2Moved = false;
        private bool bottom1Moved = false;
        private bool bottom2Moved = false;
        private bool right1Moved = false;
        private bool right2moved = false;
        private bool scaled = false;
        private int skipTicks = 0;
        private bool shifted = false;

        private long[] tankArrvs, ifvArrvs;

        public bool FirstMovesComplete = false;

        void MoveFirstTicks()
        {
            if (skipTicks > 0)
            {
                skipTicks--;
                return;
            }

            var arrvs = Environment.GetVehicles(true, VehicleType.Arrv);
            var tanks = Environment.GetVehicles(true, VehicleType.Tank);
            var ifvs = Environment.GetVehicles(true, VehicleType.Ifv);

            var arrvsRect = GetUnitsBoundingRect(arrvs);
            var tanksRect = GetUnitsBoundingRect(tanks);
            var ifvsRect = GetUnitsBoundingRect(ifvs);

            var aVert = arrvsRect.SplitVertically();

            Action<double, double, double, double> add = (x, y, dx, dy) =>
            {
                var rect = new Rect { X = x, Y = y, X2 = G.MapSize, Y2 = G.MapSize };

                if (arrvs.Concat(tanks).Concat(ifvs).All(veh => !rect.ContainsPoint(veh)))
                    return;

                MoveQueue.Add(new AMove
                {
                    Action = ActionType.ClearAndSelect,
                    Rect = rect
                });

                skipTicks = 1;

                MoveQueue.Add(new AMove
                {
                    Action = ActionType.Move,
                    X = dx,
                    Y = dy
                });
            };

            var sh = 10;
            var cs = G.CellSize * 2.5;

            if (!bottom1Moved)
            {
                bottom1Moved = true;
                add(0, 2 * cs, 0, sh);
                return;
            }

            var allStopped = Environment.MyVehicles.All(x => Geom.PointsEquals(x, MoveObserver.BeforeMoveUnits[x.Id]));

            if (!bottom2Moved)
            {
                if (allStopped)
                {
                    bottom2Moved = true;
                    add(0, cs, 0, sh);
                }
                return;
            }

            if (!right1Moved)
            {
                if (allStopped)
                {
                    right1Moved = true;
                    add(2 * cs, 0, sh, 0);
                }
                return;
            }

            if (!right2moved)
            {
                if (allStopped)
                {
                    right2moved = true;
                    add(cs, 0, sh, 0);
                }
                return;
            }

            if (!scaled)
            {
                if (allStopped)
                {
                    scaled = true;
                    skipTicks = 10;

                    var d = 1.38;
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        VehicleType = VehicleType.Arrv,
                        Rect = G.MapRect,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Scale,
                        Point = arrvsRect.Center,
                        Factor = d,
                    });

                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        VehicleType = VehicleType.Tank,
                        Rect = G.MapRect,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Scale,
                        Point = tanksRect.Center,
                        Factor = d,
                    });

                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        VehicleType = VehicleType.Ifv,
                        Rect = G.MapRect,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Scale,
                        Point = ifvsRect.Center,
                        Factor = d,
                    });
                }
                return;
            }

            var shift = 4.02;

            if (!shifted)
            {
                if (allStopped)
                {
                    shifted = true;
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.ClearAndSelect,
                        VehicleType = VehicleType.Arrv,
                        Rect = G.MapRect,
                    });
                    MoveQueue.Add(new AMove
                    {
                        Action = ActionType.Move,
                        X = shift,
                        Y = shift,
                    });
                    skipTicks = 1;
                }
                return;
            }

            if (!half1Moved)
            {
                if (allStopped)
                {
                    half1Moved = true;
                    var topArrvs = arrvs.Where(x => x.Y <= arrvsRect.Center.Y).Select(x => x.Id).ToArray();
                    var bottomArrvs = arrvs.Where(x => x.Y > arrvsRect.Center.Y).Select(x => x.Id).ToArray();

                    if (Math.Max(aVert[0].Center.GetDistanceTo2(tanksRect.Center),
                        aVert[1].Center.GetDistanceTo2(ifvsRect.Center))
                        <
                        Math.Max(aVert[1].Center.GetDistanceTo2(tanksRect.Center),
                            aVert[0].Center.GetDistanceTo2(ifvsRect.Center)))
                    {
                        tankArrvs = topArrvs;
                        ifvArrvs = bottomArrvs;
                    }
                    else
                    {
                        tankArrvs = bottomArrvs;
                        ifvArrvs = topArrvs;
                    }


                    var tankArrvsRect = GetUnitsBoundingRect(tankArrvs.Select(id => Environment.VehicleById[id]));
                    var ifvArrvsRect = GetUnitsBoundingRect(ifvArrvs.Select(id => Environment.VehicleById[id]));

                    //if (tankArrvsRect.Center.GetDistanceTo2(tanksRect.Center)
                    //    > ifvArrvsRect.Center.GetDistanceTo2(ifvsRect.Center))
                    {
                        MoveQueue.Add(new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            Rect = tankArrvsRect,
                            VehicleType = VehicleType.Arrv,
                        });
                        MoveQueue.Add(new AMove
                        {
                            Action = ActionType.Move,
                            X = tanksRect.Center.X - (tankArrvsRect.Center.X - shift),
                        });
                    }
                    {
                        MoveQueue.Add(new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            Rect = ifvArrvsRect,
                            VehicleType = VehicleType.Arrv,
                        });
                        MoveQueue.Add(new AMove
                        {
                            Action = ActionType.Move,
                            X = ifvsRect.Center.X - (ifvArrvsRect.Center.X - shift),
                        });
                    }
                    skipTicks = 4;
                }
                return;
            }

            if (!half2Moved)
            {
                if (allStopped)
                {
                    half2Moved = true;

                    var tankArrvsRect = GetUnitsBoundingRect(tankArrvs.Select(id => Environment.VehicleById[id]));
                    var ifvArrvsRect = GetUnitsBoundingRect(ifvArrvs.Select(id => Environment.VehicleById[id]));

                    {
                        MoveQueue.Add(new AMove
                        {
                            Action = ActionType.Move,
                            Y = ifvsRect.Center.Y - (ifvArrvsRect.Center.Y - shift),
                        });
                    }

                    {
                        MoveQueue.Add(new AMove
                        {
                            Action = ActionType.ClearAndSelect,
                            Rect = tankArrvsRect,
                            VehicleType = VehicleType.Arrv,
                        });
                        MoveQueue.Add(new AMove
                        {
                            Action = ActionType.Move,
                            Y = tanksRect.Center.Y - (tankArrvsRect.Center.Y - shift),
                        });
                    }

                    skipTicks = 4;
                }
                return;
            }

            if (half2Moved)
            {
                if (allStopped)
                {
                    FirstMovesComplete = true;
                }
                return;
            }

            return; 
        }
    }

    public class MyGroup
    {
        public readonly VehicleType? Type;
        public readonly int? Group;

        public MyGroup(int group)
        {
            Group = group;
        }

        public MyGroup(VehicleType type)
        {
            Type = type;
        }

        public override string ToString()
        {
            if (Type != null)
                return Type.ToString();
            if (Group != null)
                return Group.ToString();
            return "";
        }
    }
}
