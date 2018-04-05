#pragma once

#include "MainForm.h"

using namespace System;
using namespace System::Drawing;

ref struct Visualizer
{
	static agario::MainForm ^form;

	static void update(const World &world)
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

		for (auto &food : world.foods)
		{
			fillCircle(Color::Chocolate, food.x, food.y, food.radius);
		}

		for (auto &virus : world.viruses)
		{
			fillCircle(Color::Gray, virus.x, virus.y, virus.radius);
		}

		for (auto &frag : world.me.fragments)
		{
			fillCircle(Color::Violet, frag.x, frag.y, frag.radius);
			if (frag.canSplit(world.me.fragments.size()))
				drawCircle(Color::White, frag.x, frag.y, frag.radius * 0.8, 2);
			drawLine(Color::Green, frag.x, frag.y, frag.x + frag.speed.x, frag.y + frag.speed.y, 2);
		}

		for (auto &frag : world.opponentFragments)
		{
			fillCircle(Color::Orange, frag.x, frag.y, frag.radius);
			int frags_count = 0;
			for (auto &f : world.opponentFragments)
				frags_count += f.playerId == frag.playerId;
			if (frag.canSplit(frags_count))
				drawCircle(Color::White, frag.x, frag.y, frag.radius * 0.8, 2);
			drawLine(Color::Green, frag.x, frag.x, frag.y + frag.speed.x, frag.y + frag.speed.y, 2);
		}

		for (auto &ej : world.ejections)
		{
			fillCircle(Color::DarkViolet, ej.x, ej.y, ej.radius);
		}
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