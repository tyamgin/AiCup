using System;
using System.Windows.Forms;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Visualizer
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
        }

        private void buttonZoom_Click(object sender, EventArgs e)
        {
            Visualizer.Zoom -= 1.0/3;
        }

        private void buttonUnZoom_Click(object sender, EventArgs e)
        {
            Visualizer.Zoom += 1.0/3;
        }

        private void buttonPause_Click(object sender, EventArgs e)
        {
            Visualizer.Pause ^= true;
        }
    }
}
