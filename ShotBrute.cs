//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
//using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;
//using System.Collections;
//using System.IO;

//namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
//{
//    public partial class MyStrategy : IStrategy
//    {
//        public static Trooper[] Troopers;

//        public static ArrayList actions = new ArrayList();

//        public class State
//        {
//            public TrooperStance[] Stance;
//            public Point[] Position;
//            public int id, actionPoints;

//            public int X
//            {
//                get
//                {
//                    return Position[id].X;
//                }
//            }

//            public int Y
//            {
//                get
//                {
//                    return Position[id].Y;
//                }
//            }

//            public override int GetHashCode()
//            {
//                int hash = 0;
//                unchecked
//                {
//                    for (int i = 0; i < Troopers.Count(); i++)
//                    {
//                        hash = hash * 31 + Position[i].X;
//                        hash = hash * 31 + Position[i].Y;
//                        hash = hash * 31 + Stance[i].GetHashCode();
//                    }
//                    hash = hash * 31 + id;
//                }
//                return hash;
//            }

//            public State Clone()
//            {
//                State clone = new State();
//                clone.id = id;
//                clone.actionPoints = actionPoints;
//                clone.Position = new Point[Position.Count()];
//                clone.Stance = new TrooperStance[Position.Count()];
//                for (int i = 0; i < Position.Count(); i++)
//                {
//                    clone.Position[i] = new Point(Position[i].X, Position[i].Y);
//                    clone.Stance[i] = Stance[i];
//                }
//                return clone;
//            }

//            public override string ToString()
//            {
//                return "" + id + " " + Position[id];
//            }
//        }

//        static Hashtable Result = new Hashtable();
//        static Hashtable CallbackK = new Hashtable();
//        static Hashtable CallbackT = new Hashtable();
//        static int cmd; // индекс Commandera, или -1 если его нет
//        static double TeamMaxBonusRadius = 3;
//        static Bonus[,] Bonuses;
//        static List<State> deq = new List<State>();
//        static Point probableGoal;

//        int getActionPoints(State state)
//        {
//            int x = state.X, y = state.Y;
//            int points = Troopers[state.id].InitialActionPoints;
//            if (cmd != -1 && state.Position[state.id].GetDistanceTo(state.Position[cmd].X, state.Position[cmd].Y) <= game.CommanderAuraRange)
//                points += game.CommanderAuraBonusActionPoints;
//            return points;
//        }

//        bool Validate(State state)
//        {
//            if (!(state.X >= 0 && state.Y >= 0 && state.X < width && state.Y < height && notFilledMap[state.X, state.Y] == 0))
//                return false;
//            double sumX = 0, sumY = 0;
//            foreach (Point p in state.Position)
//            {
//                sumX += p.X;
//                sumY += p.Y;
//            }
//            sumX /= state.Position.Count();
//            sumY /= state.Position.Count();
//            foreach (Point p in state.Position)
//                if (p.GetDistanceTo(sumX, sumY) > TeamMaxBonusRadius)
//                    return false;
//            int cnt = 0;
//            foreach (Point p in state.Position)
//                if (p.X == state.X && p.Y == state.Y)
//                    cnt++;
//            return cnt < 2;
//        }

//        void Swap(ref int a, ref int b)
//        {
//            int tmp = a;
//            a = b;
//            b = tmp;
//        }

//        State bfs(State currentState)
//        {
//            deq.Add(currentState);
//            Result[currentState] = 0;
//            while (deq.Count != 0)
//            {
//                currentState = deq.First();
//                deq.RemoveAt(0);
//                int id = currentState.id;
//                int x = currentState.Position[id].X;
//                int y = currentState.Position[id].Y;
//                int len = (int)Result[currentState];
//                TrooperStance stance = currentState.Stance[id];
//                Bonus bo = Bonuses[x, y];

//                if (bo != null && !haveSuchBonus(Troopers[id], bo))
//                {
//                    return currentState;
//                }

//                State to;

//                // Подняться
//                if (canUpper(stance, currentState.actionPoints))
//                {
//                    to = currentState.Clone();
//                    to.Stance[id] = High(stance);
//                    to.actionPoints -= game.StanceChangeCost;
//                    if (!Result.Contains(to))
//                    {
//                        deq.Insert(0, to);
//                        Result[to] = len;
//                        CallbackK[to] = 5;
//                        CallbackT[to] = currentState;
//                    }
//                }

//                // move
//                if (getMoveCost(stance) <= currentState.actionPoints)
//                {
//                    for (int i = 0; i < 4; i++)
//                        for (int j = i + 1; j < 4; j++)
//                            if (probableGoal.GetDistanceTo(_i[i], _j[i]) < probableGoal.GetDistanceTo(_i[j], _j[j]))
//                            {
//                                Swap(ref _i[i], ref _i[j]);
//                                Swap(ref _j[i], ref _j[j]);
//                            }
//                    for (int k = 0; k < _i.Length; k++)
//                    {
//                        int ni = _i[k] + x,
//                            nj = _j[k] + y;
//                        to = currentState.Clone();
//                        to.Position[id].X = ni;
//                        to.Position[id].Y = nj;
//                        if (Validate(to))
//                        {
//                            to.actionPoints -= getMoveCost(stance);
//                            if (!Result.Contains(to))
//                            {
//                                deq.Insert(0, to);
//                                Result[to] = len;
//                                CallbackK[to] = k;
//                                CallbackT[to] = currentState;
//                            }
//                        }
//                    }
//                }

//                // EndTurn
//                to = currentState.Clone();
//                to.id = (id + 1) % to.Position.Count();
//                to.actionPoints = getActionPoints(to);
//                if (!Result.Contains(to))
//                {
//                    deq.Add(to);
//                    Result[to] = len + 1;
//                    CallbackK[to] = 6;
//                    CallbackT[to] = currentState;
//                }
//            }
//            return null;
//        }

//        void BruteForceMoveToBonus(Point probableGoal)
//        {
//            MyStrategy.probableGoal = probableGoal;
//            int fictive = 0;
//            if (queue.Count < team.Count)
//            {
//                foreach (Trooper tr in team)
//                {
//                    if (!queue.Contains(tr.Id))
//                    {
//                        queue.Add(tr.Id);
//                        fictive++;
//                    }
//                }
//            }

//            Bonuses = new Bonus[width, height];
//            foreach (Bonus bo in bonuses)
//                Bonuses[bo.X, bo.Y] = bo;
//            State startState = new State();
//            startState.Position = new Point[team.Count];
//            startState.Stance = new TrooperStance[team.Count];
//            Troopers = new Trooper[team.Count];
//            cmd = -1;
//            foreach (Trooper tr in troopers)
//            {
//                if (tr.IsTeammate)
//                {
//                    int pos = getQueuePlace2(tr, true) - 1;
//                    startState.Position[pos] = new Point(tr);
//                    startState.Stance[pos] = tr.Stance;
//                    Troopers[pos] = tr;
//                    if (tr.Type == TrooperType.Commander)
//                        cmd = pos;
//                }
//                // TODO: дорбавить врагов как препятствия?
//            }
//            startState.id = 0;
//            startState.actionPoints = Troopers[0].ActionPoints;
//            Result.Clear();
//            CallbackK.Clear();
//            CallbackT.Clear();
//            deq.Clear();
//            State end = bfs(startState);
//            actions.Clear();
//            while (CallbackK.Contains(end))
//            {
//                var move = new Move();
//                int action = (int)CallbackK[end];
//                if (action < 4)
//                {
//                    move.X = end.X;
//                    move.Y = end.Y;
//                    move.Action = ActionType.Move;
//                }
//                else if (action == 5)
//                {
//                    move.Action = ActionType.RaiseStance;
//                }
//                else if (action == 6)
//                {
//                    move.Action = ActionType.EndTurn;
//                }
//                else
//                {
//                    throw new Exception("");
//                }

//                actions.Add(move);
//                end = CallbackT[end] as State;
//            }
//        }
//    }
//}
