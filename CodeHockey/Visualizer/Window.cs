using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Visualizer
{
    public partial class Window : Form
    {
        public Window()
        {
            InitializeComponent();
        }

        private void panel_Click(object sender, EventArgs ea)
        {
            var e = ea as MouseEventArgs;
            label1.Text = e.X + " " + e.Y;
        }
    }
}
