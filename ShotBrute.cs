using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting.Messaging;
using System.Text;
using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
using System.Collections;
using System.IO;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public partial class MyStrategy : IStrategy
    {
        public static Trooper[] Troopers;

        public class State
        {
            public int[] Stance;
            public int[] hit;
            public int[] act;
            public Point[] Position;
            public int id;
            public bool[] heal;
            public bool[] grenade;

            public int X
            {
                get
                {
                    return Position[id].X;
                }
            }

            public int Y
            {
                get
                {
                    return Position[id].Y;
                }
            }

            public override int GetHashCode()
            {
                int hash = 0;
                unchecked
                {
                    for (int i = 0; i < Troopers.Count(); i++)
                    {
                        hash = hash * 31 + Position[i].X;
                        hash = hash * 31 + Position[i].Y;
                        hash = hash * 31 + Stance[i];
                        hash = hash * 31 + hit[i];
                        hash = hash * 31 + (heal[i] ? 1 : 0);
                        hash = hash * 31 + (grenade[i] ? 1 : 0);
                    }
                    hash = hash * 31 + id;
                }
                return hash;
            }

            public override string ToString()
            {
                return "" + id + " " + Position[id];
            }
        }

        static int CommanderId; // индекс Commandera, или -1 если его нет

        private static State state;
        private static Point[] moveToVariants;
        private static ArrayList[] stack;
        private static ArrayList[] bestStack;
        private static double bestProfit;

        void dfs_changeStance1()
        {
            var id = state.id;

            for (int ds = -2; ds <= 2; ds++)
            {
                state.Stance[id] += ds;
                if (state.Stance[id] >= 0 && state.Stance[id] < 3)
                {
                    int cost = game.StanceChangeCost * Math.Abs(ds);
                    if (cost <= state.act[id])
                    {
                        state.act[id] -= cost;
                        if (ds != 0)
                            stack[id].Add("st " + ds);
                        dfs_move2();
                        if (ds != 0)
                            stack[id].RemoveAt(stack[id].Count - 1);
                        state.act[id] += cost;
                    }
                }
                state.Stance[id] -= ds;
            }
        }

        private void dfs_move2()
        {
            var id = state.id;
            var stance = state.Stance[id];
            var moveCost = getMoveCost(stance);
            
            foreach (Point p in moveToVariants)
            {
                state.Position[id].X += p.X;
                state.Position[id].Y += p.Y;
                // TODO: найти кратчайший путь с учетом state
                var cost = (Math.Abs(p.X) + Math.Abs(p.Y))*moveCost;
                if (cost <= state.act[id] && Validate(state) || p.X == 0 && p.Y == 0)
                {
                    state.act[id] -= cost;
                    if (p.X != 0 || p.Y != 0)
                        stack[id].Add("at " + state.X + " " + state.Y);
                    dfs_changeStance5(); //dfs_heal_grenade3();
                    if (p.X != 0 || p.Y != 0)
                        stack[id].RemoveAt(stack[id].Count - 1);
                    state.act[id] += cost;
                }
                state.Position[id].X -= p.X;
                state.Position[id].Y -= p.Y;
            }
        }

        private void dfs_heal_grenade3()
        {
            var id = state.id;
            var stance = state.Stance[id];
            var moveCost = getMoveCost(stance);

            /*if (ss.heal && ss.actionPoints >= game.MedikitUseCost)
            {
                foreach (Point p in ss.Position)
                {
                    if (p.Nearest(ss.Position[id]))
                    {
                        ss.heal = false;
                        ss.actionPoints -= game.MedikitUseCost;
                        dfs_move4();
                        ss.heal = true;
                        ss.actionPoints += game.MedikitUseCost;
                    }
                }
            }

            if (ss.grenade && ss.actionPoints >= game.GrenadeThrowCost)
            {
                Point to;/// TODO
                ss.grenade = false;
                ss.actionPoints -= game.GrenadeThrowCost;
                dfs_move4();
                ss.actionPoints += game.GrenadeThrowCost;
                ss.grenade = true;
            }

            dfs_move4();*/
        }

        //private void dfs_move4()
        //{
        //    var hash = ss.GetHashCode();
        //    if (used.Contains(hash))
        //        return;
        //    used.Add(hash);

        //    var id = ss.id;
        //    var stance = ss.Stance[id];
        //    var moveCost = getMoveCost(stance);

        //    foreach (Point p in moveToVariants)
        //    {
        //        ss.Position[id].X += p.X;
        //        ss.Position[id].Y += p.Y;
        //        var cost = (Math.Abs(p.X) + Math.Abs(p.Y)) * moveCost;
        //        if (cost <= ss.act[id] && Validate(ss) || p.X == 0 && p.Y == 0)
        //        {
        //            ss.act[id] -= cost;
        //            dfs_changeStance5();
        //            ss.act[id] += cost;
        //        }
        //        ss.Position[id].X -= p.X;
        //        ss.Position[id].Y -= p.Y;
        //    }
        //}

        void dfs_changeStance5()
        {
            var id = state.id;

            for (int ds = -2; ds <= 2; ds++)
            {
                state.Stance[id] += ds;
                if (state.Stance[id] >= 0 && state.Stance[id] < 3)
                {
                    int cost = game.StanceChangeCost * Math.Abs(ds);
                    if (cost <= state.act[id])
                    {
                        state.act[id] -= cost;
                        if (ds != 0)
                            stack[id].Add("st " + ds);
                        dfs_end(); //dfs_move2();
                        if (ds != 0)
                            stack[id].RemoveAt(stack[id].Count - 1);
                        state.act[id] += cost;
                    }
                }
                state.Stance[id] -= ds;
            }
        }

        private int cnt = 0;
        private int[] opphit = new int[55];
        private int oppSize;
        private int mySize;

        void dfs_end()
        {
            var id = state.id;
            var opphitOld = new int[oppSize];
            var actOld = new int[mySize];
            Array.Copy(opphit, opphitOld, oppSize);
            Array.Copy(state.act, actOld, mySize);

            if (id == mySize - 1)
            {
                cnt++;
                
                // stask sizes backup
                var oldStackSize = new int[mySize];
                for (int i = 0; i < mySize; i++)
                    oldStackSize[i] = stack[i].Count;

                double profit = 0;
                for (int i = 0; i < mySize; i++)
                {
                    for (int idx = 0; idx < oppSize; idx++)
                    {
                        var opp = opponents[idx] as Trooper;
                        if (opphit[idx] > 0 && world.IsVisible(Troopers[i].ShootingRange, state.Position[i].X, state.Position[i].Y, getStance(state.Stance[i]),
                                                                                          opp.X, opp.Y, opp.Stance))
                        {
                            int can = state.act[i]/Troopers[i].ShootCost;
                            int damage = Troopers[i].GetDamage(getStance(state.Stance[i]));
                            int need = (opphit[idx] + damage - 1)/damage;
                            if (need <= can)
                            {
                                //profit += opphit[idx]; 
                                profit += opphit[idx] * 1.2;
                                opphit[idx] = 0;
                                state.act[i] -= Troopers[i].ShootCost*need;
                                for (int k = 0; k < need; k++)
                                {
                                    stack[i].Add("sh " + opp.X + " " + opp.Y);
                                }
                            }
                            else
                            {
                                state.act[i] = 0;
                                opphit[idx] -= damage*can;
                                profit += damage*can;
                                for (int k = 0; k < can; k++)
                                {
                                    stack[i].Add("sh " + opp.X + " " + opp.Y);
                                }
                            }
                        }
                    }
                }

                // Штраф за большой радиус
                double sumX = 0, sumY = 0;
                foreach (Point p in state.Position)
                {
                    sumX += p.X;
                    sumY += p.Y;
                }
                sumX /= mySize;
                sumY /= mySize;
                foreach (Point p in state.Position)
                    profit -= p.GetDistanceTo(sumX, sumY);
                
                for (int i = 0; i < oppSize; i++)
                {
                    if (opphit[i] > 0)
                    {
                        var opp = opponents[i] as Trooper;
                        var act = opp.InitialActionPoints;
                        for (int idx = 0; idx < mySize; idx++)
                        {
                            if (state.hit[idx] > 0 && world.IsVisible(opp.VisionRange, opp.X, opp.Y, opp.Stance,
                                                                      state.Position[idx].X, state.Position[idx].Y, getStance(state.Stance[idx]))
                                )
                            {
                                int can = act / opp.ShootCost;
                                int damage = opp.GetDamage(opp.Stance);
                                int need = (state.hit[idx] + damage - 1) / damage;
                                if (need <= can)
                                {
                                    //profit += opphit[idx]; 
                                    profit -= state.hit[idx] * 1.4;
                                    state.hit[idx] = 0;
                                    act -= opp.ShootCost * need;
                                }
                                else
                                {
                                    act = 0;
                                    state.hit[idx] -= damage * can;
                                    profit -= damage * can;
                                }
                            }
                        }
                    }
                }

                if (profit > bestProfit)
                {
                    bestProfit = profit;
                    bestStack = new ArrayList[mySize];
                    for (int i = 0; i < mySize; i++)
                        bestStack[i] = stack[i].Clone() as ArrayList;
                }
                Array.Copy(actOld, state.act, mySize);
                Array.Copy(opphitOld, opphit, oppSize);
                for (int i = 0; i < mySize; i++)
                    stack[i].RemoveRange(oldStackSize[i], stack[i].Count - oldStackSize[i]);
            }
            else
            {
                // EndTurn
                int prevId = state.id;
                state.id = (state.id + 1) % mySize;
                dfs_changeStance1();
                state.id = prevId;
            }
        }

        //int getActionPoints(State state)
        //{
        //    int x = state.X, y = state.Y;
        //    int points = Troopers[state.id].InitialActionPoints;
        //    if (CommanderId != -1 && state.Position[state.id].GetDistanceTo(state.Position[CommanderId].X, state.Position[CommanderId].Y) <= game.CommanderAuraRange)
        //        points += game.CommanderAuraBonusActionPoints;
        //    return points;
        //}

        int getInitialActionPoints(Trooper tr)
        {
            int x = tr.X, y = tr.Y;
            int points = tr.InitialActionPoints;
            if (CommanderId != -1 && tr.GetDistanceTo(state.Position[CommanderId].X, state.Position[CommanderId].Y) <= game.CommanderAuraRange)
                points += game.CommanderAuraBonusActionPoints;
            return points;
        }

        private bool Validate(State state)
        {
            if (
                !(state.X >= 0 && state.Y >= 0 && state.X < width && state.Y < height &&
                  notFilledMap[state.X, state.Y] == 0))
                return false;
            
            // Проверяю чтобы не стать в занятую клетку
            return state.Position.Count(p => p.X == state.X && p.Y == state.Y) < 2;
        }

        Move BruteForceDo()
        {
            int fictive = 0;
            if (queue.Count < team.Count)
            {
                foreach (Trooper tr in team)
                {
                    if (!queue.Contains(tr.Id))
                    {
                        queue.Add(tr.Id);
                        fictive++;
                    }
                }
            }

            state = new State();
            state.Position = new Point[team.Count];
            state.Stance = new int[team.Count];
            state.act = new int[team.Count];
            state.hit = new int[team.Count];
            state.heal = new bool[team.Count];
            state.grenade = new bool[team.Count];
            Troopers = new Trooper[team.Count];
            CommanderId = -1;
            foreach (Trooper tr in troopers)
            {
                if (tr.IsTeammate)
                {
                    int pos = getQueuePlace2(tr, true) - 1;
                    state.Position[pos] = new Point(tr);
                    state.Stance[pos] = getStanceId(tr.Stance);
                    state.heal[pos] = tr.IsHoldingMedikit;
                    state.grenade[pos] = tr.IsHoldingGrenade;
                    Troopers[pos] = tr;
                    if (tr.Type == TrooperType.Commander)
                        CommanderId = pos;
                }
            }
            state.id = 0;
            state.act[0] = Troopers[0].ActionPoints;
            state.hit[0] = Troopers[0].Hitpoints;
            for (int i = 1; i < Troopers.Count(); i++)
            {
                state.act[i] = getInitialActionPoints(Troopers[i]);
                state.hit[i] = Troopers[i].MaximalHitpoints;
            }
            mySize = state.Position.Count();
            stack = new ArrayList[mySize];
            bestStack = new ArrayList[mySize];
            for (int i = 0; i < mySize; i++)
            {
                stack[i] = new ArrayList();
                bestStack[i] = null;
            }
            bestProfit = -Inf;
            cnt = 0;
            oppSize = opponents.Count;
            mySize = state.Position.Count();
            for (int i = 0; i < oppSize; i++)
                opphit[i] = (opponents[i] as Trooper).Hitpoints;

            ArrayList to = new ArrayList();
            for(int dx = -3; dx <= 3; dx++)
                for (int dy = -3; dy <= 3; dy++)
                    if (Math.Abs(dx) + Math.Abs(dy) <= 3)
                        to.Add(new Point(dx, dy));

            moveToVariants = new Point[to.Count];
            for (int i = 0; i < to.Count; i++)
                moveToVariants[i] = to[i] as Point;
            dfs_changeStance1();

            if (bestStack[0] == null)
                return null;
            var move = new Move();
            if (bestStack[0].Count == 0)
                return move; // EndTurn
            var cmd = ((string)bestStack[0][0]).Split(' ');
            if (cmd[0] == "st")
            {
                // change stance
                int ds = int.Parse(cmd[1]);
                if (ds < 0)
                    move.Action = ActionType.LowerStance;
                else if (ds > 0)
                    move.Action = ActionType.RaiseStance;
                else
                    throw new Exception("");
            }
            else if (cmd[0] == "at")
            {
                int x = int.Parse(cmd[1]);
                int y = int.Parse(cmd[2]);
                Point To = goToUnit(self, new Point(x, y), map, beginFree: true, endFree: false);
                move.Action = ActionType.Move;
                move.X = To.X;
                move.Y = To.Y;
            }
            else if (cmd[0] == "sh")
            {
                int x = int.Parse(cmd[1]);
                int y = int.Parse(cmd[2]);
                move.Action = ActionType.Shoot;
                move.X = x;
                move.Y = y;
            }

            return move;
        }
    }
}
