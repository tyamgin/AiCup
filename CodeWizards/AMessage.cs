using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;
using Message = Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model.Message;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public class AMessage
    {
        public ALaneType Lane;
        public SkillType? SkillToLearn;
        public byte[] RawMessage;

        public AMessage(Message msg)
        {
            Lane = (ALaneType) msg.Lane;
            SkillToLearn = msg.SkillToLearn;
            RawMessage = msg.RawMessage;
        }

        public AMessage(ALaneType lane, SkillType? skill, byte[] raw)
        {
            Lane = lane;
            SkillToLearn = skill;
            RawMessage = raw;
        }

        public AMessage(ALaneType lane, SkillsGroup skillGroup)
        {
            Lane = lane;
            RawMessage = new [] { (byte)skillGroup };
        }

        public Message ToMessage()
        {
            return new Message((LaneType) Lane, SkillToLearn, RawMessage);
        }

        public SkillsGroup SkillsGroup
        {
            get { return (SkillsGroup) RawMessage[0]; }
            set { RawMessage = new[] {(byte) value}; }
        }
    }
}
