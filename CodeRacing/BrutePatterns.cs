using System.IO;
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

            const int subWayPointsCount = 67;

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
                }, 8, new AMove{EnginePower = 1}, 0, subWayPointsCount, useDist2:true),

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
                }, 8, new AMove {IsUseNitro = true, EnginePower = 1}, 0, subWayPointsCount, useDist2:true),

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
                }, 8, new AMove{EnginePower = 1}, 1, subWayPointsCount, useDist2:true),

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
                }, 8, new AMove{EnginePower = 1}, 121, subWayPointsCount, useDist2:true),

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
                }, 8, new AMove{EnginePower = 1}, 2, subWayPointsCount, useDist2:true),

                new PathBruteForcer(new[]
                {
                    new PathPattern
                    {
                        To = 30,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 1.0,
                                WheelTurn = 1.0,
                            }
                    },
                    new PathPattern
                    {
                        To = 30,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                WheelTurn = -1.0,
                                IsBrake = true
                            }
                    }
                }, 8, new AMove{EnginePower = 1}, 2, subWayPointsCount, useDist2:true) {Special = true},

                new PathBruteForcer(new[]
                {
                    new PathPattern
                    {
                        To = 30,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 1.0,
                                WheelTurn = -1.0,
                            }
                    },
                    new PathPattern
                    {
                        To = 30,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                WheelTurn = 1.0,
                                IsBrake = true
                            }
                    }
                }, 8, new AMove{EnginePower = 1}, 2, subWayPointsCount, useDist2:true) {Special = true},
            };

            BackBrutes =
                new[] {-1, -0.5, 0, 0.5, 1}.Select(turn => new PathBruteForcer(new[]
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
                }, 8, new AMove {EnginePower = 1}, 66, 40, useDist2: true))
                    .Concat(
                        new[] {-1.0, 1.0}.Select(sign => new PathBruteForcer(new[]
                        {
                            new PathPattern
                            {
                                To = 40,
                                Step = 5,
                                Move = new AMove
                                {
                                    EnginePower = 1.0,
                                    WheelTurn = sign
                                }
                            }
                        }, 8, new AMove {EnginePower = 1}, 111, 40, useDist2: true)
                            ))
                    .Concat(new[]
                    {
                        new PathBruteForcer(
                            new[]
                            {
                                new PathPattern
                                {
                                    To = 48,
                                    Step = 8,
                                    Move = new AMove
                                    {
                                        EnginePower = -1.0,
                                        WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToCenter}
                                    }
                                }
                            }, 8, new AMove {EnginePower = -1.0}, 1111, 40, useDist2: true
                            )
                    })
                    .ToArray();
        }
    }
}
