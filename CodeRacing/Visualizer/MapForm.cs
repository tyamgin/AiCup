using System;
using System.Windows.Forms;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    public partial class MapForm : Form
    {
        public MapForm()
        {
            InitializeComponent();
        }

        private void buttonZoom_Click(object sender, EventArgs e)
        {
            MyStrategy.Zoom -= 1;
        }

        private void buttonUnZoom_Click(object sender, EventArgs e)
        {
            MyStrategy.Zoom += 1;
        }

        private void buttonStop_Click(object sender, EventArgs e)
        {
            MyStrategy.Debug = true;
        }

        private void buttonPause_Click(object sender, EventArgs e)
        {
            MyStrategy.Pause ^= true;
        }
    }
}
