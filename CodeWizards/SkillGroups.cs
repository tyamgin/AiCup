using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public enum SkillsGroup
    {
        Round2,
        Haster,
        Fireballer,
        Shielder,
        Froster,
    }

    public class SkillGroups
    {
        public static Dictionary<SkillsGroup, SkillType[]> Orders = new Dictionary<SkillsGroup, SkillType[]>
        {
            {
                SkillsGroup.Round2,
                new[]
                {
                    SkillType.Fireball,
                    SkillType.RangeBonusPassive1,
                    SkillType.Haste,
                    SkillType.AdvancedMagicMissile,

                    SkillType.FrostBolt,
                    SkillType.Shield
                }
            },
            {
                SkillsGroup.Haster,
                new[]
                {
                    SkillType.Haste,
                    SkillType.AdvancedMagicMissile,
                    SkillType.FrostBolt,

                    SkillType.Shield,
                    SkillType.Fireball,
                }
            },
            {
                SkillsGroup.Fireballer,
                new[]
                {
                    SkillType.Fireball,
                    SkillType.MagicalDamageBonusAura2,
                    SkillType.Shield,

                    SkillType.FrostBolt,
                    SkillType.Haste,
                    SkillType.AdvancedMagicMissile,
                }
            },
            {
                SkillsGroup.Froster,
                new[]
                {
                    SkillType.RangeBonusAura1,
                    SkillType.FrostBolt,
                    SkillType.Fireball,

                    SkillType.Shield,
                    SkillType.Haste,
                    SkillType.AdvancedMagicMissile,
                }
            },
            {
                SkillsGroup.Shielder,
                new[]
                {
                    SkillType.Shield,
                    SkillType.FrostBolt,

                    SkillType.Fireball,
                    SkillType.Haste,
                    SkillType.AdvancedMagicMissile,
                }
            },
        };

        public static SkillsGroup[] GetDistribution(int count)
        {
            switch (count)
            {
                case 0:
                    return new SkillsGroup[] { };
                case 1:
                    return new[] { SkillsGroup.Round2 };
                case 2:
                    return new[] { SkillsGroup.Fireballer, SkillsGroup.Haster };
                case 3:
                    return new[] { SkillsGroup.Fireballer, SkillsGroup.Haster, SkillsGroup.Froster };
                case 4:
                    return new[] { SkillsGroup.Fireballer, SkillsGroup.Haster, SkillsGroup.Froster, SkillsGroup.Shielder };
                default:
                    throw new Exception("?");
            }
        }
    }
}
