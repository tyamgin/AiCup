namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Visualizer
{
    public partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.panel = new System.Windows.Forms.PictureBox();
            this.tickLabel = new System.Windows.Forms.Label();
            this.buttonZoom = new System.Windows.Forms.Button();
            this.buttonUnZoom = new System.Windows.Forms.Button();
            this.buttonPause = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.panel)).BeginInit();
            this.SuspendLayout();
            // 
            // panel
            // 
            this.panel.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.panel.BackColor = System.Drawing.Color.White;
            this.panel.Location = new System.Drawing.Point(61, 12);
            this.panel.Name = "panel";
            this.panel.Size = new System.Drawing.Size(1053, 855);
            this.panel.TabIndex = 0;
            this.panel.TabStop = false;
            // 
            // tickLabel
            // 
            this.tickLabel.AutoSize = true;
            this.tickLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 16F);
            this.tickLabel.Location = new System.Drawing.Point(743, 23);
            this.tickLabel.Name = "tickLabel";
            this.tickLabel.Size = new System.Drawing.Size(70, 26);
            this.tickLabel.TabIndex = 1;
            this.tickLabel.Text = "label1";
            // 
            // buttonZoom
            // 
            this.buttonZoom.Location = new System.Drawing.Point(3, 13);
            this.buttonZoom.Name = "buttonZoom";
            this.buttonZoom.Size = new System.Drawing.Size(25, 23);
            this.buttonZoom.TabIndex = 2;
            this.buttonZoom.Text = "+";
            this.buttonZoom.UseVisualStyleBackColor = true;
            this.buttonZoom.Click += new System.EventHandler(this.buttonZoom_Click);
            // 
            // buttonUnZoom
            // 
            this.buttonUnZoom.Location = new System.Drawing.Point(34, 13);
            this.buttonUnZoom.Name = "buttonUnZoom";
            this.buttonUnZoom.Size = new System.Drawing.Size(25, 23);
            this.buttonUnZoom.TabIndex = 3;
            this.buttonUnZoom.Text = "-";
            this.buttonUnZoom.UseVisualStyleBackColor = true;
            this.buttonUnZoom.Click += new System.EventHandler(this.buttonUnZoom_Click);
            // 
            // buttonPause
            // 
            this.buttonPause.Location = new System.Drawing.Point(3, 42);
            this.buttonPause.Name = "buttonPause";
            this.buttonPause.Size = new System.Drawing.Size(56, 48);
            this.buttonPause.TabIndex = 4;
            this.buttonPause.Text = "| |";
            this.buttonPause.UseVisualStyleBackColor = true;
            this.buttonPause.Click += new System.EventHandler(this.buttonPause_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1126, 879);
            this.Controls.Add(this.buttonPause);
            this.Controls.Add(this.buttonUnZoom);
            this.Controls.Add(this.buttonZoom);
            this.Controls.Add(this.tickLabel);
            this.Controls.Add(this.panel);
            this.Name = "MainForm";
            this.Text = "MainForm";
            ((System.ComponentModel.ISupportInitialize)(this.panel)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.PictureBox panel;
        public System.Windows.Forms.Label tickLabel;
        private System.Windows.Forms.Button buttonZoom;
        private System.Windows.Forms.Button buttonUnZoom;
        private System.Windows.Forms.Button buttonPause;
    }
}