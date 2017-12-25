using System;
using System.Collections.Generic;
using System.Linq;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public List<object[]> VisualSegments = new List<object[]>();

        Tuple<AMove[], MyGroup, DangerResult> MainLoopStrategy(bool opt)
        {
            VisualSegments.Clear();
            var visualVectorsStart = new List<Point>();
            var visualVectorsDir = new List<Point>();
            var visualVectorsScore = new List<double>();

            _isSlowMode = _isSlowMode && Environment.OppVehicles.Count == 0;
            var baseTicksCount = Const.ActionsBruteforceDepth;
            if (Environment.Nuclears.Length > 0)
                baseTicksCount *= 2;

            var selMoves = new[] { new AMove() };
            MyGroup selGroup = null;
            DangerResult selDanger = null;

            var targetFacilities = GetDanger(Environment, Environment, GroupsManager.MyGroups, MyUngroupedClusters).TargetFacility;

            for (var s = 0; s < GroupsManager.MyGroups.Count + MyUngroupedClusters.Count; s++)
            {
                var availableActions = MoveObserver.AvailableActions;
                if (Opp.RemainingNuclearStrikeCooldownTicks < 60 && Environment.Nuclears.Length == 0)
                    availableActions -= 2;

                MyGroup group;
                List<AVehicle> newGroupVehicles = null;
                if (s < GroupsManager.MyGroups.Count)
                    group = GroupsManager.MyGroups[s];
                else
                {
                    newGroupVehicles = MyUngroupedClusters[s - GroupsManager.MyGroups.Count];
                    if (newGroupVehicles.Count < NewGroupMinSize)
                        continue;
                    if (availableActions < 3) // required: clear, move, assign
                        continue;
                    group = new MyGroup(GroupsManager.NextGroupId, newGroupVehicles[0].Type);
                }

                if (opt && !(
                    Environment.Nuclears.Length > 0 ||
                    Environment.Facilities.Length <= 4 ||
                    World.TickIndex < 2500 ||
                    MoveObserver.MaxAvailableActions <= 12 ||
                    _doMainLastGroup != null && _doMainLastGroup.Group == group.Group || // ходил предыдущий раз
                    (newGroupVehicles == null && group.Group % 2 == _doMainsCount % 2 || newGroupVehicles != null && group.Group % 3 == _doMainsCount % 3) // через раз/два
                    ))
                {
                    continue;
                }

                if (_isSlowMode && World.TickIndex < 300 && group.VehicleType == VehicleType.Helicopter)
                    continue;

                Logger.CumulativeOperationStart("First env actions");

                var startEnv = Environment.Clone();
                var ticksCount = baseTicksCount;

                if (newGroupVehicles != null)
                {
                    foreach (var newVeh in newGroupVehicles)
                        startEnv.AddVehicleGroup(startEnv.VehicleById[newVeh.Id], group.Group);
                }

                var selectedIds = Utility.UnitsHash(startEnv.MyVehicles.Where(x => x.IsSelected));
                var needToSelectIds = Utility.UnitsHash(startEnv.GetVehicles(true, group));

                List<AMove> selectionMoves = new List<AMove>();

                if (selectedIds != needToSelectIds)
                {
                    if (availableActions < 2) // required: select, move
                    {
                        Logger.CumulativeOperationEnd("First env actions");
                        continue;
                    }

                    if (newGroupVehicles == null)
                        selectionMoves.Add(AMovePresets.ClearAndSelectGroup(group.Group));
                    else
                    {
                        var selectionMove = AMovePresets.ClearAndSelectType(group.VehicleType, Utility.BoundingRect(newGroupVehicles));
                        startEnv.ApplyMove(selectionMove); // ниже выполнится лишний раз, но не страшно
                        selectionMoves.Add(selectionMove);

                        foreach (var mg in GroupsManager.MyGroups)
                            if (mg.VehicleType == group.VehicleType && startEnv.MyVehicles.Where(x => x.IsSelected).Any(x => x.IsGroup(mg)))
                                selectionMoves.Add(new AMove { Action = ActionType.Deselect, Group = mg.Group });
                    }

                    if (availableActions < selectionMoves.Count + 1) // required: select, move, deselect all
                    {
                        Logger.CumulativeOperationEnd("First env actions");
                        continue;
                    }

                    foreach (var mv in selectionMoves)
                    {
                        startEnv.ApplyMove(mv);
                        availableActions--;
                    }
                }
                availableActions--;

                Logger.CumulativeOperationEnd("First env actions");

                // availableActions теперь - это сколько действий останется после выполнения текущего тика
                // должно остаться хотябы 2, или 0, если обладатель ядерки selected
                var myNuclear = startEnv.Nuclears.FirstOrDefault(n => n.IsMy);
                if (myNuclear != null && availableActions < 2 - (startEnv.VehicleById.ContainsKey(myNuclear.VehicleId) && startEnv.VehicleById[myNuclear.VehicleId].IsSelected ? 2 : 0))
                    continue;

                var typeRect = Utility.BoundingRect(startEnv.GetVehicles(true, group));

                Sandbox partialEnv = null;
                var sumMaxAlmostAttacksCache = -1.0;
                if (Environment.Nuclears.Length == 0)
                {
                    Logger.CumulativeOperationStart("Partial env actions");

                    partialEnv = new Sandbox(
                        startEnv.Vehicles.Where(x => !x.IsSelected),
                        new ANuclear[] { },
                        startEnv.Facilities,
                        clone: true
                        );
                    partialEnv.CheckCollisionsWithOpponent = false;

                    var grps = GroupsManager.MyGroups.Select(g => partialEnv.GetVehicles(true, g).ToArray()).ToArray();

                    try
                    {
                        for (var i = 0; i < ticksCount; i++)
                        {
                            for (var j = 0; j < grps.Length; j++)
                            {
                                var pgr = grps[j];
                                if (pgr.Length > 0)
                                    partialEnv.DoMoveApprox(pgr, pgr[0].Action != AVehicle.MoveType.Scale);
                            }
                            partialEnv.DoFacilities();
                            partialEnv.DoNuclears();
                        }
                        partialEnv.DoFight();

                        sumMaxAlmostAttacksCache = GetSumMaxAlmostAttacks(partialEnv, partialEnv.MyVehicles);
                    }
                    catch (QuadTree<AVehicle>.PointAlreadyExistsException e)
                    {
                        Logger.Log(e.Message);
                        continue;
                    }
                    finally
                    {
                        Logger.CumulativeOperationEnd("Partial env actions");
                    }
                }

                Logger.CumulativeOperationStart("Pre last env actions");

                Sandbox preLastEnv = null;
                AVehicle[] nearInteractors = null;
                List<AVehicle> currentVehicles = null;
                AVehicle[] preLastEnvVehiclesCopy = null;

                if (partialEnv != null)
                {
                    currentVehicles = startEnv.GetVehicles(true, group);
                    var currentVehiclesCenter = Utility.BoundingRect(currentVehicles).Center;
                    nearInteractors = partialEnv.Vehicles
                        .Where(x =>
                            !x.IsMy && x.GetDistanceTo2(currentVehiclesCenter) < Geom.Sqr(2*G.TacticalNuclearStrikeRadius)
                            || x.IsMy && x.IsAerial == Utility.IsAerial(group.VehicleType) && x.GetDistanceTo2(currentVehiclesCenter) < Geom.Sqr(G.TacticalNuclearStrikeRadius))
                        .ToArray();

                    preLastEnv = new Sandbox(
                        partialEnv.Vehicles.Concat(currentVehicles),
                        new ANuclear[] { },
                        new AFacility[] { },
                        clone: true
                    );
                    preLastEnvVehiclesCopy = preLastEnv.Vehicles.Select(x => new AVehicle(x)).ToArray();
                }

                Logger.CumulativeOperationEnd("Pre last env actions");

                Func<AMove, double> checkAction = move =>
                {
                    Logger.CumulativeOperationStart("Building nearby environment");
                    Sandbox env;

                    if (partialEnv == null)
                        env = startEnv.Clone();
                    else
                        env = new Sandbox(nearInteractors.Concat(currentVehicles),
                            startEnv.Nuclears, partialEnv.Facilities, clone: true);

                    env.CheckCollisionsWithOpponent = false;
                    env.UseFightOptimization = false;

                    if (partialEnv != null)
                    {
                        foreach (var veh in env.Vehicles)
                        {
                            if (veh.IsGroup(group))
                                continue;

                            veh.ForgotTarget(); // чтобы не шли повторно
                            veh.CanChargeFacility = false; // чтобы не захватывали повторно
                            // TODO: лечение

                            if (veh.RemainingAttackCooldownTicks > 0) // у тех, кто стрелял давно, откатываем кд
                                veh.RemainingAttackCooldownTicks += ticksCount;
                            // TODO: если кд только восстановилось
                        }
                    }
                    Logger.CumulativeOperationEnd("Building nearby environment");

                    Logger.CumulativeOperationStart("End of simulation");
                    env.ApplyMove(move);
                    try
                    {
                        env.DoTicksApprox(ticksCount,
                        moveApprox: move.Action == ActionType.Move || move.Action == ActionType.Rotate,
                        fightApprox: true);
                    }
                    catch (QuadTree<AVehicle>.PointAlreadyExistsException e)
                    {
                        Logger.Log(e.Message);
                        return double.PositiveInfinity;
                    }
                    finally
                    {
                        Logger.CumulativeOperationEnd("End of simulation");
                    }

                    Logger.CumulativeOperationStart("Building last environment");
                    var cache = sumMaxAlmostAttacksCache;
                    if (partialEnv != null)
                    {
                        var tmp = env;
                        env = preLastEnv;

                        for (var i = 0; i < env.Vehicles.Length; i++)
                        {
                            var veh = env.Vehicles[i];
                            if (tmp.VehicleById.ContainsKey(veh.Id))
                                env.UpdateVehicle(veh, tmp.VehicleById[veh.Id]);
                            else
                                env.UpdateVehicle(veh, preLastEnvVehiclesCopy[i]);
                        }

                        env.Nuclears = tmp.Nuclears;
                        env.Facilities = tmp.Facilities;
                        cache += GetSumMaxAlmostAttacks(env, env.GetVehicles(true, group));
                    }

                    var myGroups = GroupsManager.MyGroups.AsEnumerable();
                    var myUngroups = MyUngroupedClusters.AsEnumerable();
                    if (newGroupVehicles != null)
                    {
                        myGroups = myGroups.ConcatSingle(group);
                        myUngroups = myUngroups.Where(cl => !cl.Equals(newGroupVehicles));
                    }

                    if (env.Vehicles.Length != Environment.Vehicles.Length)
                        throw new Exception("Final sandbox size mismatch");
                    Logger.CumulativeOperationEnd("Building last environment");


                    var danger = GetDanger(Environment, env, myGroups.ToList(), myUngroups.ToList(), cache);

                    if (move.Action == ActionType.Move)
                    {
                        visualVectorsStart.Add(typeRect.Center);
                        visualVectorsDir.Add(new Point(move.X, move.Y).Normalized());
                        visualVectorsScore.Add(danger.Score);
                    }

                    if (selDanger == null || danger.Score < selDanger.Score)
                    {
                        selDanger = danger;
                        selMoves = selectionMoves.Concat(new[]
                        {
                            move,
                            newGroupVehicles == null
                                ? null
                                : AMovePresets.AssignGroup(group.Group)
                        })
                            .Where(x => x != null)
                            .ToArray();
                        selGroup = group;
                    }
                    return danger.Score;
                };

                var typeRectCenter = typeRect.Center;
                var simpleMode = Environment.Nuclears.Length == 0 && (
                    Environment.OppVehicles.Count == 0 ||
                    Environment.OppVehicles.Min(x => x.GetDistanceTo2(typeRectCenter)) > Geom.Sqr(120));

                var moveMaxSpeed = _isSlowMode && Utility.IsAerial(group.VehicleType) ? G.MaxSpeed[(int) group.VehicleType] * 0.6 : 0; 

                Func<int, AMove> idxToMove = idx => AMovePresets.Move(
                    Point.ByAngle(2*Math.PI/12*idx)
                    *startEnv.MyVehicles.Where(x => x.IsSelected).Max(x => x.ActualSpeed)
                    *ticksCount*40,
                    group.Group == GroupsManager.StartingTanksGroupId && Const.MixArrvsWithGrounds
                        ? Math.Min(G.MaxSpeed[(int) VehicleType.Tank], G.MaxSpeed[(int) VehicleType.Arrv])
                        : group.Group == GroupsManager.StartingIfvsGroupId && Const.MixArrvsWithGrounds
                            ? Math.Min(G.MaxSpeed[(int) VehicleType.Ifv], G.MaxSpeed[(int) VehicleType.Arrv])
                            : moveMaxSpeed
                    );

                
                double[] dangers;
                if (simpleMode)
                {
                   // проверка на каждые 3 часа
                   dangers = Enumerable.Range(0, 4).Select(i => checkAction(idxToMove(i * 3))).ToArray();
                }
                else
                {
                    // проверка на каждые 2 часа
                    dangers = Enumerable.Range(0, 6).Select(i => checkAction(idxToMove(i * 2))).ToArray();

                    // проверка на середины лучших промежутков
                    var dangers2 = dangers.Select((x, i) => new Tuple<double, int>(x, i)).OrderBy(x => x.Item1).Select(x => x.Item2).Take(2).ToArray();
                    foreach (var i in dangers2.Select(i => i * 2 + 1).Concat(dangers2.Select(i => i * 2 - 1)).Distinct())
                        checkAction(idxToMove(i));
                }

                foreach (var move in
                    Environment.Nuclears
                        .Where(n =>
                            n.GetDistanceTo(Utility.Average(startEnv.MyVehicles.Where(x => x.IsSelected))) <
                            G.TacticalNuclearStrikeRadius * 2)
                        .SelectMany(nuclear => new[]
                        {
                            AMovePresets.Scale(nuclear, 1.5),
                            AMovePresets.Scale(nuclear, 3.0),
                            Environment.VehicleById.ContainsKey(nuclear.VehicleId) ? AMovePresets.Scale((Environment.VehicleById[nuclear.VehicleId] + nuclear) / 2, 2.0) : null,
                        }))
                {
					if (move != null)
						checkAction(move);
                }

                checkAction(AMovePresets.Scale(typeRect.Center, 0.1));
                if (_doMainLastUnscale.ContainsKey(group.Group))
                {
                    var prevScaleMove = _doMainLastUnscale[group.Group].Item2;
                    checkAction(AMovePresets.Scale(prevScaleMove.Point, 1 / prevScaleMove.Factor));
                }
                checkAction(AMovePresets.Rotate(typeRect.Center, Math.PI / 4));
                checkAction(AMovePresets.Rotate(typeRect.Center, -Math.PI / 4));

                List<Point> positiveInteractors = new List<Point>(), negativeInteractors = new List<Point>();
                foreach (var cluster in OppClusters)
                {
                    foreach (var oppType in Const.AllTypes)
                    {
                        if (cluster.CountByType[(int)oppType] > 0)
                        {
                            var avg = Utility.Average(cluster.VehicleType(oppType));
                            if (G.AttackDamage[(int)group.VehicleType, (int)oppType] > 0)
                                positiveInteractors.Add(avg);
                            else if (G.AttackDamage[(int)oppType, (int)group.VehicleType] > 0)
                                negativeInteractors.Add(avg);
                        }
                    }
                }

                foreach (var move in
                    positiveInteractors.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                        .Take(2)
                        .Select(cen => AMovePresets.MoveTo(typeRect.Center, cen, moveMaxSpeed)))
                {
                    checkAction(move);
                }

                foreach (var move in
                    negativeInteractors.OrderBy(cen => cen.GetDistanceTo2(typeRect.Center))
                        .Take(1)
                        .Select(cen => AMovePresets.Move((typeRect.Center - cen).Take(150), moveMaxSpeed)))
                {
                    checkAction(move);
                }

                if (targetFacilities.ContainsKey(group.Group))
                {
                    checkAction(AMovePresets.MoveTo(typeRect.Center, targetFacilities[group.Group], moveMaxSpeed));
                }

                // проверка действия "ничего не делать"
                if (selectionMoves.Count == 0)
                {
                    checkAction(new AMove());
                }

                // полет к ближайшему Arrv
                if (group.VehicleType == VehicleType.Helicopter || group.VehicleType == VehicleType.Fighter)
                {
                    var arrvGroupsCenters = GroupsManager.MyGroups
                        .Where(g => g.VehicleType == VehicleType.Arrv)
                        .Select(g => Utility.BoundingRect(Environment.GetVehicles(true, g)).Center)
                        .ToArray();
                    if (arrvGroupsCenters.Length > 0)
                    {
                        checkAction(AMovePresets.MoveTo(typeRect.Center,
                            arrvGroupsCenters.ArgMin(p => p.GetDistanceTo2(typeRect.Center)), moveMaxSpeed));
                    }
                }
            }

#if DEBUG
            //if (selDanger != null)
            //{
            //    var maxScore = visualVectorsScore.Max();
            //    var minScore = visualVectorsScore.Min();
            //    if (maxScore > minScore)
            //    {
            //        for (var i = 0; i < visualVectorsStart.Count; i++)
            //        {
            //            VisualSegments.Add(new object[]
            //            {
            //                new List<Point>
            //                {
            //                    visualVectorsStart[i],
            //                    visualVectorsStart[i] + visualVectorsDir[i]*((visualVectorsScore[i]-minScore)/(minScore-maxScore)*40)
            //                },
            //                new System.Drawing.Pen(System.Drawing.Color.Green),
            //                5
            //            });
            //        }
            //    }
            //}
#endif

            return new Tuple<AMove[], MyGroup, DangerResult>(selMoves, selGroup, selDanger);
        }


        private bool _isSlowMode = false;

        public AMove[] UnstuckStrategy()
        {
            var availableActions = MoveObserver.AvailableActions;
            if (Opp.RemainingNuclearStrikeCooldownTicks < 60 && Environment.Nuclears.Length == 0)
                availableActions -= 2;

            if (availableActions < 2)
                return null;

            foreach (var cl in MyUngroupedClusters)
            {
                foreach (var type in Const.AllTypes)
                {
                    if (cl.CountByType[(int) type] < 5)
                        continue;

                    foreach (var group in GroupsManager.MyGroups)
                    {
                        if (group.VehicleType == type)
                        {
                            var br = Utility.BoundingRect(Environment.GetVehicles(true, group));
                            br.ExtendRadius(G.VehicleRadius);
                            var bu = cl.BoundingRect.Clone();
                            bu.ExtendRadius(G.VehicleRadius);
                            if (br.IntersectsWith(bu))
                            {
                                if (cl.Count == Environment.MyVehicles.Count(
                                    x => x.Type == type && !x.IsGroup(group) && cl.BoundingRect.ContainsPoint(x)))
                                {
                                    return new[]
                                    {
                                        AMovePresets.ClearAndSelectType(type, cl.BoundingRect),
                                        AMovePresets.AssignGroup(group.Group),
                                    };
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}
