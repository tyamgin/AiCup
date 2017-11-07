using System;
using System.Linq;
using System.Windows.Forms;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Visualizer
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
        }

        private double _step = 1.3;

        private void buttonZoom_Click(object sender, EventArgs e)
        {
            Visualizer.Zoom /= _step;
        }

        private void buttonUnZoom_Click(object sender, EventArgs e)
        {
            Visualizer.Zoom *= _step;
        }

        private void buttonPause_Click(object sender, EventArgs e)
        {
            Visualizer.Pause ^= true;
        }

        private void renderButton_Click(object sender, EventArgs e)
        {
            Visualizer.DrawSince = 0;
        }

        private void stopRenderButton_Click(object sender, EventArgs e)
        {
            Visualizer.DrawSince = 1000000;
        }

        private void lookAtTextBox_TextChanged(object sender, EventArgs e)
        {

        }
    }
}
