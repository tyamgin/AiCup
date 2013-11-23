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
        private int counter = 0;
        private int oppSize;
        private int mySize;
        int[] opphitOld;
        int[] actOld;
        int[] hitOld;
        int[] oldStackSize;

        public class State
        {
            public int[] Stance;
            public int[] hit;
            public int[] act;
            public Point[] Position;
            public int id = 0;
            public double profit = 0;
            public bool[] heal;
            public bool[] grenade;
            public int[] opphit = new int[55];

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

            public override string ToString()
            {
                return "" + id + " " + Position[id];
            }
        }

        static int CommanderId; // индекс Commandera, или -1 если его нет
        private static State state;
        private static ArrayList[] stack;
        private static ArrayList[] bestStack;
        private static double bestProfit;

        void dfs_changeStance1()
        {
            var id = state.id;

            for (int deltaStance = -2; deltaStance <= 2; deltaStance++)
            {
                // Изменяю stance на deltaStance
                state.Stance[id] += deltaStance;
                if (state.Stance[id] >= 0 && state.Stance[id] < 3)
                {
                    int cost = game.StanceChangeCost * Math.Abs(deltaStance);
                    if (cost <= state.act[id])
                    {
                        state.act[id] -= cost;
                        if (deltaStance != 0)
                            stack[id].Add("st " + deltaStance);
                        dfs_move();
                        if (deltaStance != 0)
                            stack[id].RemoveAt(stack[id].Count - 1);
                        state.act[id] += cost;
                    }
                }
                state.Stance[id] -= deltaStance;
            }
        }

        private void dfs_move()
        {
            var id = state.id;
            var stance = state.Stance[id];
            var moveCost = getMoveCost(stance);

            var To = FastBfs(state.X, state.Y, 4, notFilledMap, state.Position);
            foreach (Point to in To)
            {
                int byX = to.X - state.X;
                int byY = to.Y - state.Y;
                state.Position[id].X += byX;
                state.Position[id].Y += byY;
                var cost = (int)(to.profit + Eps)*moveCost;
                if (cost <= state.act[id] && Validate(state))
                {
                    state.act[id] -= cost;
                    if (byX != 0 || byY != 0)
                        stack[id].Add("at " + state.X + " " + state.Y);
                    if (state.grenade[id])
                        dfs_grenade();
                    if (state.heal[id])
                        dfs_heal();
                    dfs_changeStance5();
                    if (byX != 0 || byY != 0)
                        stack[id].RemoveAt(stack[id].Count - 1);
                    state.act[id] += cost;
                }
                state.Position[id].X -= byX;
                state.Position[id].Y -= byY;
            }
        }

        private void dfs_heal()
        {
            int id = state.id;
        }

        private void dfs_grenade()
        {
            int id = state.id;

            //if (state.heal[id])
            //{
            //    state.heal[id] = false;
            //    var holdingGrenade = state.grenade[id];
            //    state.grenade[id] = false;
            //    for (int i = 0; i < mySize; i++)
            //    {
            //        Trooper to = Troopers[i];
            //        // если имеет смысл юзать
            //        if (state.hit[i] < 0.8 * to.MaximalHitpoints)
            //        {
            //            var dist = Math.Abs(state.X - state.Position[i].X) + Math.Abs(state.Y - state.Position[i].Y); // TODO:
            //            var cost = game.MedikitUseCost + (dist - 1) * getMoveCost(state.Stance[id]);
            //            if (cost < state.act[id])
            //            {
            //                var oldhit = state.hit[i];
            //                state.act[id] -= cost;
            //                state.hit[i] = Math.Min(to.MaximalHitpoints, state.hit[i] + (i == id ? game.MedikitHealSelfBonusHitpoints : game.MedikitBonusHitpoints));
            //                stack[id].Add("med");
            //                dfs_move2();
            //                stack[id].RemoveAt(stack[id].Count - 1);
            //                state.hit[i] = oldhit;
            //                state.act[id] += cost;
            //            }
            //        }
            //    }
            //    state.heal[id] = true;
            //    state.grenade[id] = holdingGrenade;
            //}


            //if (state.grenade[id] && state.hit[id] >= game.GrenadeThrowCost)
            //{
            //    state.grenade[id] = false;

            //    Point bestPoint = Point.Inf;
            //    foreach (Trooper trooper in opponents)
            //    {
            //        for (int k = 0; k < 4; k++)
            //        {
            //            int ni = trooper.X + _i[k];
            //            int nj = trooper.Y + _j[k];
            //            if (ni >= 0 && nj >= 0 && ni < width && nj < height && notFilledMap[ni, nj] == 0 
            //                && state.Position[id].GetDistanceTo(ni, nj) <= game.GrenadeThrowRange)
            //            {
            //                int profit = 0;
            //                int loop = 0;
            //                foreach (Trooper tr in opponents)
            //                {
            //                    int dist = Math.Abs(ni - tr.X) + Math.Abs(nj - tr.Y);
            //                    if (dist == 0)
            //                        profit += Math.Min(opphit[loop], game.GrenadeDirectDamage);
            //                    else if (dist == 1)
            //                        profit += Math.Min(opphit[loop], game.GrenadeCollateralDamage);
            //                    loop++;
            //                }
            //                if (bestPoint.profit < profit)
            //                    bestPoint.Set(ni, nj, profit);
            //            }
            //        }
            //    }
            //    if (bestPoint.profit > 0)
            //    {
            //        stack[id].Add("gr " + bestPoint.X + " " + bestPoint.Y);
            //        state.hit[id] -= game.GrenadeThrowCost;
            //        for(int i = 0; i < oppSize; i++)
            //        {
            //            Trooper opp = opponents[i] as Trooper;
            //            int dist = Math.Abs(bestProfit.X - opp.X) + Math.Abs(bestPoint.Y - opp.Y);
            //            if (dist == 0)
            //                profit += Math.Min(opphit[loop], game.GrenadeDirectDamage);
            //            else if (dist == 1)
            //                profit += Math.Min(opphit[loop], game.GrenadeCollateralDamage);
            //            loop++;
            //        }
            //        dfs_move2();
            //        state.hit[id] += game.GrenadeThrowCost;
            //        stack[id].RemoveAt(stack[id].Count - 1);
            //    }
            //    else
            //    {
            //        // TODO: ?????
            //    }
            //    state.grenade[id] = true;
            //}
            //dfs_changeStance5();
        }

        void dfs_changeStance5()
        {
            var id = state.id;

            for (int deltaStance = -2; deltaStance <= 2; deltaStance++)
            {
                state.Stance[id] += deltaStance;
                if (state.Stance[id] >= 0 && state.Stance[id] < 3)
                {
                    var cost = game.StanceChangeCost * Math.Abs(deltaStance);
                    if (cost <= state.act[id])
                    {
                        state.act[id] -= cost;
                        if (deltaStance != 0)
                            stack[id].Add("st " + deltaStance);
                        dfs_end();
                        if (deltaStance != 0)
                            stack[id].RemoveAt(stack[id].Count - 1);
                        state.act[id] += cost;
                    }
                }
                state.Stance[id] -= deltaStance;
            }
        }

        private void dfs_end()
        {
            var id = state.id;

            // stask size backup
            var oldStackSize = stack[id].Count;

            double profit = state.profit;
            // делаю как будто каждый будет убивать у кого меньше жизней которого видит (и у них не теряются жизни)
            int bestIdx = -1;
            int minHit = Inf;
            for (int idx = 0; idx < oppSize; idx++)
            {
                var opp = opponents[idx] as Trooper;
                if (state.opphit[idx] > 0 &&
                    world.IsVisible(Troopers[id].ShootingRange, state.X, state.Y, getStance(state.Stance[id]), opp.X,
                        opp.Y, opp.Stance))
                {
                    if (state.opphit[idx] < minHit)
                    {
                        minHit = state.opphit[idx];
                        bestIdx = idx;
                    }
                }
            }
            if (bestIdx != -1)
            {
                var opp = opponents[bestIdx] as Trooper;
                var can = state.act[id]/Troopers[id].ShootCost;
                var damage = Troopers[id].GetDamage(getStance(state.Stance[id]));
                var need = (minHit + damage - 1)/damage;

                if (need <= can)
                {
                    profit += minHit*1.2;
                    for (int k = 0; k < need; k++)
                        stack[id].Add("sh " + opp.X + " " + opp.Y);
                }
                else
                {
                    profit += damage*can;
                    for (int k = 0; k < can; k++)
                        stack[id].Add("sh " + opp.X + " " + opp.Y);
                }
            }

            // Штраф за большой радиус
            double centerX = 0, centerY = 0;
            foreach (Point position in state.Position)
            {
                centerX += position.X;
                centerY += position.Y;
            }
            centerX /= mySize;
            centerY /= mySize;
            foreach (Point position in state.Position)
                profit -= position.GetDistanceTo(centerX, centerY);


            if (id == mySize - 1)
            {
                counter++;
                if (profit > bestProfit)
                {
                    bestProfit = profit;
                    bestStack = new ArrayList[mySize];
                    for (int i = 0; i < mySize; i++)
                        bestStack[i] = stack[i].Clone() as ArrayList;
                }
            }
            else
            {
                // Считаем потенциал
                double potential = Inf; // TODO:
                if (potential >= bestProfit)
                {
                    // EndTurn
                    var oldProfit = state.profit;
                    state.profit = profit;
                    int prevId = state.id;
                    state.id = (state.id + 1)%mySize;
                    dfs_changeStance1();
                    state.profit = oldProfit;
                    state.id = prevId;
                }
            }
            // откатывает стек
            stack[id].RemoveRange(oldStackSize, stack[id].Count - oldStackSize);
        }

                        
                //// К профиту прибавляю итоговое количество жизней
                //for (int i = 0; i < mySize; i++)
                //    profit += state.hit[i] * 1.5;

                //for (int i = 0; i < oppSize; i++)
                //{
                //    if (opphit[i] > 0)
                //    {
                //        var opp = opponents[i] as Trooper;
                //        var act = opp.InitialActionPoints;
                //        for (int idx = 0; idx < mySize; idx++)
                //        {
                //            if (state.hit[idx] > 0 && world.IsVisible(opp.VisionRange, opp.X, opp.Y, opp.Stance,
                //                                                      state.Position[idx].X, state.Position[idx].Y, getStance(state.Stance[idx]))
                //                )
                //            {
                //                var can = act / opp.ShootCost;
                //                var damage = opp.GetDamage(opp.Stance);
                //                var need = (state.hit[idx] + damage - 1) / damage;
                //                if (need <= can)
                //                {
                //                    profit -= state.hit[idx] * 0.8;
                //                    state.hit[idx] = 0;
                //                    act -= opp.ShootCost * need;
                //                }
                //                else
                //                {
                //                    act = 0;
                //                    state.hit[idx] -= damage * can;
                //                    profit -= damage * can * 0.5;
                //                }
                //            }
                //        }
                //    }
                //}

            
                //double high = state.profit; // Верхняя оценка ветки            
        

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
            // TODO: можно ускорить если заполнять карту
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
            counter = 0;
            oppSize = opponents.Count;
            mySize = state.Position.Count();
            opphitOld = new int[oppSize];
            actOld = new int[mySize];
            hitOld = new int[mySize];
            oldStackSize = new int[mySize];
            for (int i = 0; i < oppSize; i++)
                state.opphit[i] = (opponents[i] as Trooper).Hitpoints;

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
