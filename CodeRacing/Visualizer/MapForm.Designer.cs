namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk
{
    partial class MapForm
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
            this.buttonStop = new System.Windows.Forms.Button();
            this.buttonPause = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.panel)).BeginInit();
            this.SuspendLayout();
            // 
            // panel
            // 
            this.panel.Location = new System.Drawing.Point(56, 16);
            this.panel.Name = "panel";
            this.panel.Size = new System.Drawing.Size(1148, 595);
            this.panel.TabIndex = 0;
            this.panel.TabStop = false;
            // 
            // tickLabel
            // 
            this.tickLabel.AutoSize = true;
            this.tickLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.tickLabel.Location = new System.Drawing.Point(3, 18);
            this.tickLabel.Name = "tickLabel";
            this.tickLabel.Size = new System.Drawing.Size(18, 20);
            this.tickLabel.TabIndex = 1;
            this.tickLabel.Text = "0";
            // 
            // buttonZoom
            // 
            this.buttonZoom.Location = new System.Drawing.Point(7, 42);
            this.buttonZoom.Name = "buttonZoom";
            this.buttonZoom.Size = new System.Drawing.Size(21, 23);
            this.buttonZoom.TabIndex = 2;
            this.buttonZoom.Text = "+";
            this.buttonZoom.UseVisualStyleBackColor = true;
            this.buttonZoom.Click += new System.EventHandler(this.buttonZoom_Click);
            // 
            // buttonUnZoom
            // 
            this.buttonUnZoom.Location = new System.Drawing.Point(7, 71);
            this.buttonUnZoom.Name = "buttonUnZoom";
            this.buttonUnZoom.Size = new System.Drawing.Size(21, 23);
            this.buttonUnZoom.TabIndex = 3;
            this.buttonUnZoom.Text = "-";
            this.buttonUnZoom.UseVisualStyleBackColor = true;
            this.buttonUnZoom.Click += new System.EventHandler(this.buttonUnZoom_Click);
            // 
            // buttonStop
            // 
            this.buttonStop.Location = new System.Drawing.Point(7, 101);
            this.buttonStop.Name = "buttonStop";
            this.buttonStop.Size = new System.Drawing.Size(43, 48);
            this.buttonStop.TabIndex = 4;
            this.buttonStop.Text = "Stop";
            this.buttonStop.UseVisualStyleBackColor = true;
            this.buttonStop.Click += new System.EventHandler(this.buttonStop_Click);
            // 
            // buttonPause
            // 
            this.buttonPause.Location = new System.Drawing.Point(7, 155);
            this.buttonPause.Name = "buttonPause";
            this.buttonPause.Size = new System.Drawing.Size(43, 48);
            this.buttonPause.TabIndex = 5;
            this.buttonPause.Text = "| |";
            this.buttonPause.UseVisualStyleBackColor = true;
            this.buttonPause.Click += new System.EventHandler(this.buttonPause_Click);
            // 
            // MapForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1216, 623);
            this.Controls.Add(this.buttonPause);
            this.Controls.Add(this.buttonStop);
            this.Controls.Add(this.buttonUnZoom);
            this.Controls.Add(this.buttonZoom);
            this.Controls.Add(this.tickLabel);
            this.Controls.Add(this.panel);
            this.Name = "MapForm";
            this.Text = "MapForm";
            ((System.ComponentModel.ISupportInitialize)(this.panel)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.PictureBox panel;
        public System.Windows.Forms.Label tickLabel;
        private System.Windows.Forms.Button buttonZoom;
        private System.Windows.Forms.Button buttonUnZoom;
        private System.Windows.Forms.Button buttonStop;
        private System.Windows.Forms.Button buttonPause;
    }
}