#pragma once

namespace agario {

	using namespace System;
	using namespace System::ComponentModel;
	using namespace System::Collections;
	using namespace System::Windows::Forms;
	using namespace System::Data;
	using namespace System::Drawing;

	/// <summary>
	/// Summary for MainForm
	/// </summary>
	public ref class MainForm : public System::Windows::Forms::Form
	{
	public:
		MainForm(void)
		{
			InitializeComponent();
			//
			//TODO: Add the constructor code here
			//
		}

	protected:
		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		~MainForm()
		{
			if (components)
			{
				delete components;
			}
		}
	public: System::Windows::Forms::TextBox^  consoleTextBox;
	public: System::Windows::Forms::PictureBox^  panel;

	public:
	public: System::Windows::Forms::Label^  tickLabel;
	protected:

	private:
		/// <summary>
		/// Required designer variable.
		/// </summary>
		System::ComponentModel::Container ^components;

#pragma region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		void InitializeComponent(void)
		{
			this->consoleTextBox = (gcnew System::Windows::Forms::TextBox());
			this->panel = (gcnew System::Windows::Forms::PictureBox());
			this->tickLabel = (gcnew System::Windows::Forms::Label());
			(cli::safe_cast<System::ComponentModel::ISupportInitialize^>(this->panel))->BeginInit();
			this->SuspendLayout();
			// 
			// consoleTextBox
			// 
			this->consoleTextBox->Location = System::Drawing::Point(12, 622);
			this->consoleTextBox->Multiline = true;
			this->consoleTextBox->Name = L"consoleTextBox";
			this->consoleTextBox->Size = System::Drawing::Size(298, 229);
			this->consoleTextBox->TabIndex = 0;
			// 
			// panel
			// 
			this->panel->Anchor = static_cast<System::Windows::Forms::AnchorStyles>(((System::Windows::Forms::AnchorStyles::Top | System::Windows::Forms::AnchorStyles::Bottom)
				| System::Windows::Forms::AnchorStyles::Right));
			this->panel->BackColor = System::Drawing::Color::White;
			this->panel->Location = System::Drawing::Point(324, 12);
			this->panel->Name = L"panel";
			this->panel->Size = System::Drawing::Size(839, 839);
			this->panel->TabIndex = 1;
			this->panel->TabStop = false;
			// 
			// tickLabel
			// 
			this->tickLabel->AutoSize = true;
			this->tickLabel->Font = (gcnew System::Drawing::Font(L"Microsoft Sans Serif", 20.25F, System::Drawing::FontStyle::Regular, System::Drawing::GraphicsUnit::Point,
				static_cast<System::Byte>(204)));
			this->tickLabel->Location = System::Drawing::Point(13, 13);
			this->tickLabel->Name = L"tickLabel";
			this->tickLabel->Size = System::Drawing::Size(65, 31);
			this->tickLabel->TabIndex = 2;
			this->tickLabel->Text = L"Tick";
			// 
			// MainForm
			// 
			this->AutoScaleDimensions = System::Drawing::SizeF(6, 13);
			this->AutoScaleMode = System::Windows::Forms::AutoScaleMode::Font;
			this->ClientSize = System::Drawing::Size(1175, 863);
			this->Controls->Add(this->tickLabel);
			this->Controls->Add(this->panel);
			this->Controls->Add(this->consoleTextBox);
			this->Name = L"MainForm";
			this->Text = L"MainForm";
			(cli::safe_cast<System::ComponentModel::ISupportInitialize^>(this->panel))->EndInit();
			this->ResumeLayout(false);
			this->PerformLayout();

		}
#pragma endregion
	};
}
