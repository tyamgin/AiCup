using System.Linq;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public PathBruteForcer[] Brutes, AdditionalBrutes, BackBrutes;

        public void InitBrutes()
        {
            if (Brutes != null)
                return;

            const int subWayPointsCount = 60;

            Brutes = new[]
            {
                /*
                     * - ехать в сторону поворота на полной можности
                     * - поворачивать в сторону цели на пол-мощности
                     * - тормозить
                     */
                new PathBruteForcer(new[]
                {
                    new PathPattern
                    {
                        To = 32,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 1,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToCenter},
                            }
                    },
                    new PathPattern
                    {
                        To = 16,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.5,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                            }
                    },
                    new PathPattern
                    {
                        To = 34,
                        Step = 2,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 0, subWayPointsCount),

                                /*
                     * - ехать в сторону поворота на полной можности
                     * - поворачивать в сторону цели на пол-мощности
                     * - тормозить
                     * - НИТРО!!!
                     */
                new PathBruteForcer(new[]
                {
                    new PathPattern
                    {
                        To = 20,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 1,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToCenter},
                            }
                    },
                    new PathPattern
                    {
                        To = 20,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.5,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                            }
                    },
                    new PathPattern
                    {
                        To = 34,
                        Step = 3,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove {IsUseNitro = true}, 0, subWayPointsCount),

                /*
                     * - снизить мощность
                     * - тормозить
                     */
                new PathBruteForcer(new[]
                {
                    new PathPattern
                    {
                        To = 25,
                        Step = 1,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.2,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                            }
                    },
                    new PathPattern
                    {
                        To = 33,
                        Step = 3,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 1, subWayPointsCount),

                               /*
                * - снизить мощность
                * - тормозить
                */
                new PathBruteForcer(new[]
                {
                    new PathPattern
                    {
                        To = 30,
                        Step = 5,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.FromNext},
                                IsBrake = true
                            }
                    },
                    new PathPattern
                    {
                        To = 40,
                        Step = 5,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 121, subWayPointsCount),

                /*
                     * - ехать от поворота на пол-мощности
                     * - поворачивать в сторону цели на полной мощности
                     * - тормозить
                     */
                new PathBruteForcer(new[]
                {
                    new PathPattern
                    {
                        To = 20,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.5,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.FromCenter},
                            }
                    },
                    new PathPattern
                    {
                        To = 24,
                        Step = 2,
                        Move =
                            new AMove
                            {
                                EnginePower = 1,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                            }
                    },
                    new PathPattern
                    {
                        To = 32,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 2, subWayPointsCount),
            };

            BackBrutes = new[] {-1, -0.5, 0, 0.5, 1}.Select(turn => new PathBruteForcer(new[]
            {
                new PathPattern
                {
                    To = 200,
                    Step = 10,
                    Move =
                        new AMove
                        {
                            EnginePower = -1,
                            WheelTurn = turn,
                            RangesMode = true,
                            SafeMargin = -1,
                        }
                }
            }, 8, new AMove(), 66, 30))
                .ToArray();
        }
    }
}
