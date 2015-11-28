using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MyStrategy
    {
        public PathBruteForce[] Brutes, AdditionalBrutes, BackBrutes;

        public void InitBrutes()
        {
            if (Brutes != null)
                return;

            Brutes = new[]
            {
                /*
                     * - ехать в сторону поворота на полной можности
                     * - поворачивать в сторону цели на пол-мощности
                     * - тормозить
                     */
                new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 32,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 1,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToCenter},
                                IsBrake = false
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 16,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.5,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = false
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 34,
                        Step = 2,
                        Move =
                            new AMove
                            {
                                EnginePower = 0,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 0, 70),

                                /*
                     * - ехать в сторону поворота на полной можности
                     * - поворачивать в сторону цели на пол-мощности
                     * - тормозить
                     * - НИТРО!!!
                     */
                new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 20,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 1,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToCenter},
                                IsBrake = false
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 20,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.5,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = false
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 34,
                        Step = 3,
                        Move =
                            new AMove
                            {
                                EnginePower = 0,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove {IsUseNitro = true}, 0, 70),

                /*
                     * - снизить мощность
                     * - тормозить
                     */
                new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 25,
                        Step = 1,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.2,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = false
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 33,
                        Step = 3,
                        Move =
                            new AMove
                            {
                                EnginePower = 0,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 1, 70),

                /*
                     * - ехать от поворота на пол-мощности
                     * - поворачивать в сторону цели на полной мощности
                     * - тормозить
                     */
                new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 20,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.5,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.FromCenter},
                                IsBrake = false
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 24,
                        Step = 2,
                        Move =
                            new AMove
                            {
                                EnginePower = 1,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = false
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 32,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                EnginePower = 0,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 2, 70),
            };

            AdditionalBrutes = new[]
            {
               /*
                * - снизить мощность
                * - тормозить
                */
                new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 24,
                        Step = 2,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToCenter},
                                IsBrake = true
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 32,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 1, 40),

                                /*
                     * - снизить мощность
                     * - тормозить
                     */
                new PathBruteForce(new[]
                {
                    new PathPattern
                    {
                        From = 0,
                        To = 24,
                        Step = 2,
                        Move =
                            new AMove
                            {
                                EnginePower = 0.2,
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                            }
                    },
                    new PathPattern
                    {
                        From = 0,
                        To = 32,
                        Step = 4,
                        Move =
                            new AMove
                            {
                                WheelTurn = new TurnPattern {Pattern = TurnPatternType.ToNext},
                                IsBrake = true
                            }
                    }
                }, 8, new AMove(), 1, 40),
            };

            BackBrutes = new[] {-1, -0.5, 0, 0.5, 1}.Select(turn => new PathBruteForce(new[]
            {
                new PathPattern
                {
                    From = 0,
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
