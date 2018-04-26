#pragma once

#include "MainForm.h"
#include <cliext/vector>

using namespace System::Drawing;
using namespace System::Collections::Generic;

ref struct VCircle
{
	double x, y, r;
};

ref struct Visualizer
{
	static agario::MainForm ^form;

	static void update(const World &world, const vector<FoodInfo> &foods, const vector<EjectionInfo> ejections)
	{
		form->tickLabel->Text = world.tick.ToString();

		auto panel = form->panel;

		auto draw_area = gcnew Bitmap(panel->Size.Width, panel->Size.Height);
		panel->Image = draw_area;

		_graphics = Graphics::FromImage(draw_area);

		if (_scale < 0)
		{
			_scale = 1.0 * Config::MAP_SIZE / panel->Size.Height;
		}

		drawCircle(Color::Black, Config::MAP_CENTER.x, Config::MAP_CENTER.y, Config::MAP_SIZE*M_SAFE_RAD_FACTOR, 1);
		
		for (auto &food_info : foods)
		{
			auto &food = food_info.food;

			if (food_info.lastSeenTick == world.tick)
				fillCircle(Color::Chocolate, food.x, food.y, food.radius);
			else if (food_info.isMirror)
				fillCircle(Color::DarkBlue, food.x, food.y, food.radius);
			else
				fillCircle(Color::Red, food.x, food.y, food.radius);
		}

		for (auto &virus : world.viruses)
		{
			fillCircle(Color::Gray, virus.x, virus.y, virus.radius);
		}
		const int speed_line_factor = 5;
		for (auto &frag : world.me.fragments)
		{
			fillCircle(Color::Violet, frag.x, frag.y, frag.radius);
			if (frag.canSplit((int)world.me.fragments.size()))
				drawCircle(Color::White, frag.x, frag.y, frag.radius * 0.8, 2);
			if (frag.isFast)
				drawCircle(Color::Yellow, frag.x, frag.y, frag.radius * 0.5, 2);
			drawLine(Color::Green, frag.x, frag.y, frag.x + frag.speed.x*speed_line_factor, frag.y + frag.speed.y*speed_line_factor, 2);
			if (frag.ttf)
				drawText(frag.ttf.ToString(), 8, gcnew SolidBrush(Color::Green), frag.x + 5, frag.y - 16);
		}

		for (auto &frag : world.opponentFragments)
		{
			fillCircle(Color::Orange, frag.x, frag.y, frag.radius);
			int frags_count = 0;
			for (auto &f : world.opponentFragments)
				frags_count += f.playerId == frag.playerId;
			if (frag.canSplit(frags_count))
				drawCircle(Color::White, frag.x, frag.y, frag.radius * 0.8, 2);
			if (frag.isFast2())
				drawCircle(Color::Yellow, frag.x, frag.y, frag.radius * 0.5, 2);
			drawLine(Color::Green, frag.x, frag.y, frag.x + frag.speed.x*speed_line_factor, frag.y + frag.speed.y*speed_line_factor, 2);

			if (frag.ttf)
				drawText(frag.ttf.ToString(), 8, gcnew SolidBrush(Color::Green), frag.x + 5, frag.y - 16);
		}

		for (auto &ej_info : ejections)
		{
			auto &ej = ej_info.ejection;
			if (ej_info.lastSeenTick != world.tick)
				fillCircle(Color::Violet, ej.x, ej.y, ej.radius);
			else
				fillCircle(Color::DarkViolet, ej.x, ej.y, ej.radius);
		}
	}

	static List<VCircle^> _moves;

	static void updateMove(const Move &move)
	{
		VCircle^ circle = gcnew VCircle();
		circle->x = move.x;
		circle->y = move.y;
		circle->r = 6;
		_moves.Add(circle);

		for (int i = (int) _moves.Count - 1; i >= 0; i--)
		{
			_moves[i]->r -= 0.33;
			if (_moves[i]->r <= 0)
			{
				_moves.RemoveAt(i);
			}
		}

		for each (VCircle ^circle in _moves)
			fillCircle(Color::Black, circle->x, circle->y, circle->r);
	}

	static void fillCircle(Color color, double x, double y, double radius)
	{
		_graphics->FillEllipse(gcnew SolidBrush(color), _x(x - radius), _y(y - radius), _s(radius * 2), _s(radius * 2));
	}

	static void drawCircle(Color color, double x, double y, double radius, float width)
	{
		auto pen = width > 0 ? gcnew Pen(color, width) : gcnew Pen(color);
		_graphics->DrawEllipse(pen, _x(x - radius), _y(y - radius), _s(radius * 2), _s(radius * 2));
	}

	static void drawLine(Color color, double x, double y, double X, double Y, float width)
	{
		_graphics->DrawLine(gcnew Pen(color, width), _x(x), _y(y), _x(X), _y(Y));
	}

	static void drawText(System::String ^text, int size, Brush ^brush, double x, double y)
	{
		auto font = gcnew Font("Comic Sans MS", (float) _s(size));
		_graphics->DrawString(text, font, brush, (float) _x(x), (float) _y(y));
	}

private:
	static Graphics ^_graphics;

	static double _lookX = 0, _lookY = 0, _scale = -1;

	static int _x(double x)
	{
		if (_lookX > 0)
			x -= _lookX;

		return (int)(x / _scale);
	}

	static int _y(double y)
	{
		if (_lookY > 0)
			y -= _lookY;
		return (int)(y / _scale);
	}

	static int _s(double x)
	{
		return (int)ceil(x / _scale);
	}
};