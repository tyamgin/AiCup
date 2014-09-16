namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Visualizer
{
    partial class Window
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
            this.TickLabel = new System.Windows.Forms.Label();
            this.ScoreLabel = new System.Windows.Forms.Label();
            this.infoLabel = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.panel)).BeginInit();
            this.SuspendLayout();
            // 
            // panel
            // 
            this.panel.Location = new System.Drawing.Point(13, -121);
            this.panel.Name = "panel";
            this.panel.Size = new System.Drawing.Size(1224, 796);
            this.panel.TabIndex = 0;
            this.panel.TabStop = false;
            // 
            // TickLabel
            // 
            this.TickLabel.AutoSize = true;
            this.TickLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 14.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.TickLabel.Location = new System.Drawing.Point(13, 13);
            this.TickLabel.Name = "TickLabel";
            this.TickLabel.Size = new System.Drawing.Size(45, 24);
            this.TickLabel.TabIndex = 1;
            this.TickLabel.Text = "Tick";
            // 
            // ScoreLabel
            // 
            this.ScoreLabel.AutoSize = true;
            this.ScoreLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 14.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.ScoreLabel.Location = new System.Drawing.Point(647, 190);
            this.ScoreLabel.Name = "ScoreLabel";
            this.ScoreLabel.Size = new System.Drawing.Size(45, 24);
            this.ScoreLabel.TabIndex = 2;
            this.ScoreLabel.Text = "0 : 0";
            // 
            // infoLabel
            // 
            this.infoLabel.AutoSize = true;
            this.infoLabel.Location = new System.Drawing.Point(648, 226);
            this.infoLabel.Name = "infoLabel";
            this.infoLabel.Size = new System.Drawing.Size(24, 13);
            this.infoLabel.TabIndex = 3;
            this.infoLabel.Text = "info";
            // 
            // Window
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1240, 687);
            this.Controls.Add(this.infoLabel);
            this.Controls.Add(this.ScoreLabel);
            this.Controls.Add(this.TickLabel);
            this.Controls.Add(this.panel);
            this.Location = new System.Drawing.Point(200, -50);
            this.Name = "Window";
            this.Text = "Window";
            ((System.ComponentModel.ISupportInitialize)(this.panel)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.PictureBox panel;
        public System.Windows.Forms.Label TickLabel;
        public System.Windows.Forms.Label ScoreLabel;
        public System.Windows.Forms.Label infoLabel;
    }
}