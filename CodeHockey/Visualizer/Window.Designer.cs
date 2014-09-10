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
            ((System.ComponentModel.ISupportInitialize)(this.panel)).BeginInit();
            this.SuspendLayout();
            // 
            // panel
            // 
            this.panel.Location = new System.Drawing.Point(13, -121);
            this.panel.Name = "panel";
            this.panel.Size = new System.Drawing.Size(1024, 768);
            this.panel.TabIndex = 0;
            this.panel.TabStop = false;
            // 
            // Window
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1040, 664);
            this.Controls.Add(this.panel);
            this.Location = new System.Drawing.Point(100, 0);
            this.Name = "Window";
            this.Text = "Window";
            ((System.ComponentModel.ISupportInitialize)(this.panel)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        public System.Windows.Forms.PictureBox panel;
    }
}