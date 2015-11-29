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
            this.buttonPause = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.jeepRadioButton = new System.Windows.Forms.RadioButton();
            this.buggyRadioButton = new System.Windows.Forms.RadioButton();
            ((System.ComponentModel.ISupportInitialize)(this.panel)).BeginInit();
            this.groupBox1.SuspendLayout();
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
            this.buttonZoom.Location = new System.Drawing.Point(2, 42);
            this.buttonZoom.Name = "buttonZoom";
            this.buttonZoom.Size = new System.Drawing.Size(23, 23);
            this.buttonZoom.TabIndex = 2;
            this.buttonZoom.Text = "+";
            this.buttonZoom.UseVisualStyleBackColor = true;
            this.buttonZoom.Click += new System.EventHandler(this.buttonZoom_Click);
            // 
            // buttonUnZoom
            // 
            this.buttonUnZoom.Location = new System.Drawing.Point(29, 42);
            this.buttonUnZoom.Name = "buttonUnZoom";
            this.buttonUnZoom.Size = new System.Drawing.Size(23, 23);
            this.buttonUnZoom.TabIndex = 3;
            this.buttonUnZoom.Text = "-";
            this.buttonUnZoom.UseVisualStyleBackColor = true;
            this.buttonUnZoom.Click += new System.EventHandler(this.buttonUnZoom_Click);
            // 
            // buttonPause
            // 
            this.buttonPause.Location = new System.Drawing.Point(2, 71);
            this.buttonPause.Name = "buttonPause";
            this.buttonPause.Size = new System.Drawing.Size(50, 48);
            this.buttonPause.TabIndex = 5;
            this.buttonPause.Text = "| |";
            this.buttonPause.UseVisualStyleBackColor = true;
            this.buttonPause.Click += new System.EventHandler(this.buttonPause_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.jeepRadioButton);
            this.groupBox1.Controls.Add(this.buggyRadioButton);
            this.groupBox1.Font = new System.Drawing.Font("Microsoft Sans Serif", 6.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.groupBox1.Location = new System.Drawing.Point(2, 125);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(50, 98);
            this.groupBox1.TabIndex = 7;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Look to";
            // 
            // jeepRadioButton
            // 
            this.jeepRadioButton.AutoSize = true;
            this.jeepRadioButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 6.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.jeepRadioButton.Location = new System.Drawing.Point(1, 41);
            this.jeepRadioButton.Name = "jeepRadioButton";
            this.jeepRadioButton.Size = new System.Drawing.Size(43, 16);
            this.jeepRadioButton.TabIndex = 1;
            this.jeepRadioButton.Text = "Jeep";
            this.jeepRadioButton.UseVisualStyleBackColor = true;
            // 
            // buggyRadioButton
            // 
            this.buggyRadioButton.AutoSize = true;
            this.buggyRadioButton.Checked = true;
            this.buggyRadioButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 6.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.buggyRadioButton.Location = new System.Drawing.Point(0, 17);
            this.buggyRadioButton.Name = "buggyRadioButton";
            this.buggyRadioButton.Size = new System.Drawing.Size(49, 16);
            this.buggyRadioButton.TabIndex = 0;
            this.buggyRadioButton.TabStop = true;
            this.buggyRadioButton.Text = "Buggy";
            this.buggyRadioButton.UseVisualStyleBackColor = true;
            // 
            // MapForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1216, 623);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.buttonPause);
            this.Controls.Add(this.buttonUnZoom);
            this.Controls.Add(this.buttonZoom);
            this.Controls.Add(this.tickLabel);
            this.Controls.Add(this.panel);
            this.Name = "MapForm";
            this.Text = "MapForm";
            ((System.ComponentModel.ISupportInitialize)(this.panel)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.PictureBox panel;
        public System.Windows.Forms.Label tickLabel;
        private System.Windows.Forms.Button buttonZoom;
        private System.Windows.Forms.Button buttonUnZoom;
        private System.Windows.Forms.Button buttonPause;
        private System.Windows.Forms.GroupBox groupBox1;
        public System.Windows.Forms.RadioButton jeepRadioButton;
        public System.Windows.Forms.RadioButton buggyRadioButton;
    }
}