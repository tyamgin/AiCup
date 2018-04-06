#pragma once

#include "CircularUnit.hpp"
#include "Move.hpp"
#include "Ejection.hpp"

#define VIS_FACTOR 4.0  // vision = radius * VF
#define VIS_FACTOR_FR 2.5 // vision = radius * VFF * qSqrt(fragments.count())
#define RADIUS_FACTOR 2.0
#define SPLIT_START_SPEED 9.0
#define COLLISION_POWER 20.0

#define MIN_EJECT_MASS 40.0
#define EJECT_START_SPEED 8.0
#define EJECT_RADIUS 4.0
#define EJECT_MASS 15.0
#define MIN_SHRINK_MASS 100
#define SHRINK_FACTOR 0.01

#define qSqrt sqrt
#define qAnd abs
#define qCos cos
#define qSin sin
#define QPair pair

struct PlayerFragment : CircularUnit
{
	int ttf = 0;
	int playerId = 0, fragmentId = 0;
	bool is_fast = false;

	PlayerFragment()
	{
	}

	PlayerFragment(const nlohmann::json &obj) : CircularUnit(obj)
	{
		if (obj.count("TTF"))
			ttf = obj["TTF"].get<int>();

		auto id_str = obj["Id"].get<string>();
		auto dot_pos = id_str.find('.');
		if (dot_pos == string::npos)
		{
			playerId = stoi(id_str);
		}
		else
		{
			playerId = stoi(id_str.substr(0, dot_pos));
			fragmentId = stoi(id_str.substr(dot_pos + 1));
		}
	}

	Point getVisionCenter() const
	{
		return *this + speed.take(10);
	}

	void addMass(double add)
	{
		mass += add;
		radius = RADIUS_FACTOR * sqrt(mass);
	}

	bool canBurst(int yet_cnt)  const
	{
		if (mass < Config::MIN_BURST_MASS * 2)
			return false;
		
		int frags_cnt = int(mass / Config::MIN_BURST_MASS);
		return frags_cnt > 1 && yet_cnt + 1 <= Config::MAX_FRAGS_CNT;
	}

	bool canSplit(int yet_cnt) const
	{
		if (yet_cnt + 1 <= Config::MAX_FRAGS_CNT)
			if (mass > Config::MIN_SPLIT_MASS)
				return true;
		
		return false;
	}

	//QPair<double, double> get_direct_norm() const {
	//	double dx = cmd_x - x, dy = cmd_y - y;
	//	double dist = qSqrt(dx * dx + dy * dy);
	//	if (dist > 0) {
	//		double factor = 50 / dist;
	//		return QPair<double, double>(x + dx * factor, y + dy * factor);
	//	}
	//	return QPair<double, double>(x, y);
	//}

	void applyViscosity(double usual_speed) 
	{
		auto spd = speed.length();
		// если на этом тике не снизим скорость достаточно - летим дальше
		if (spd - Config::VISCOSITY > usual_speed) 
		{
			speed = speed.take(spd - Config::VISCOSITY);
		}
		else 
		{
			// иначе выставляем максимальную скорость и выходим из режима полёта
			speed = speed.take(usual_speed);
			is_fast = false;
		}
	}

	/////////////////////////////// пока не нужно
	//void burst_on(Circle *virus) 
	//{
	//	double dist = calc_dist(virus->getX(), virus->getY());
	//	double dy = y - virus->getY(), dx = x - virus->getX();
	//	double new_angle = 0.0;

	//	if (dist > 0) {
	//		new_angle = qAsin(dy / dist);
	//		if (dx < 0) {
	//			new_angle = M_PI - new_angle;
	//		}
	//	}
	//	angle = new_angle;
	//	double max_speed = Config::SPEED_FACTOR / qSqrt(mass);
	//	if (speed < max_speed) {
	//		speed = max_speed;
	//	}
	//	mass += BURST_BONUS;
	//	score += SCORE_FOR_BURST;
	//}

	//QVector<Player*> burst_now(int max_fId, int yet_cnt) {
	//	QVector<Player*> fragments;
	//	int new_frags_cnt = int(mass / MIN_BURST_MASS) - 1;
	//	int max_cnt = Config::MAX_FRAGS_CNT - yet_cnt;
	//	if (new_frags_cnt > max_cnt) {
	//		new_frags_cnt = max_cnt;
	//	}

	//	double new_mass = mass / (new_frags_cnt + 1);
	//	double new_radius = Config::RADIUS_FACTOR * qSqrt(new_mass);

	//	for (int I = 0; I < new_frags_cnt; I++) {
	//		int new_fId = max_fId + I + 1;
	//		Player *new_fragment = new Player(id, x, y, new_radius, new_mass, new_fId);
	//		new_fragment->set_color(color);
	//		fragments.append(new_fragment);

	//		double burst_angle = angle - BURST_ANGLE_SPECTRUM / 2 + I * BURST_ANGLE_SPECTRUM / new_frags_cnt;
	//		new_fragment->set_impulse(BURST_START_SPEED, burst_angle);
	//	}
	//	set_impulse(BURST_START_SPEED, angle + BURST_ANGLE_SPECTRUM / 2);

	//	fragmentId = max_fId + new_frags_cnt + 1;
	//	mass = new_mass;
	//	radius = new_radius;
	//	fuse_timer = Config::TICKS_TIL_FUSION;
	//	return fragments;
	//}

	PlayerFragment split(int max_fragment_id) 
	{
		double new_mass = mass / 2;

		PlayerFragment new_player;
		new_player.x = x;
		new_player.y = y;
		new_player.addMass(new_mass);
		new_player.speed = speed.take(SPLIT_START_SPEED);
		new_player.is_fast = true;
		new_player.playerId = playerId;
		new_player.fragmentId = max_fragment_id + 1;
		new_player.ttf = Config::TICKS_TIL_FUSION;
		
		fragmentId = max_fragment_id + 2;
		ttf = Config::TICKS_TIL_FUSION;
		addMass(-new_mass);

		return new_player;
	}

	bool canFuse(const PlayerFragment &frag) const 
	{
		if (ttf || frag.ttf)
			return false;

		// TODO: can optimize
		double dist = frag.getDistanceTo(x, y);
		double nR = radius + frag.radius;

		return dist <= nR;
	}

	void collisionCalc(PlayerFragment &other) 
	{
		if (is_fast || other.is_fast) // do not collide splits
			return;
		
		double dist2 = getDistanceTo2(other);
		if (dist2 >= sqr(radius + other.radius)) // do not intersects
			return;
		
		auto dist = sqrt(dist2);

		// vector from centers
		double collisionVectorX = this->x - other.x;
		double collisionVectorY = this->y - other.y;
		// normalize to 1
		double vectorLen = qSqrt(collisionVectorX * collisionVectorX + collisionVectorY * collisionVectorY);
		if (vectorLen < 1e-9) { // collision object in same point??
			return;
		}
		collisionVectorX /= vectorLen;
		collisionVectorY /= vectorLen;

		double collisionForce = 1. - dist / (radius + other.radius);
		collisionForce *= collisionForce;
		collisionForce *= COLLISION_POWER;

		double sumMass = mass + other.mass;
		// calc influence on us
		{
			double currPart = other.mass / sumMass; // more influence on us if other bigger and vice versa

			speed.x += collisionForce * currPart * collisionVectorX;
			speed.y += collisionForce * currPart * collisionVectorY;
		}

		// calc influence on other
		{
			double otherPart = mass / sumMass;
			
			other.speed.x -= collisionForce * otherPart * collisionVectorX;
			other.speed.y -= collisionForce * otherPart * collisionVectorY;
		}
	}

	void fusion(const PlayerFragment frag) 
	{
		double sumMass = mass + frag.mass;

		double fragInfluence = frag.mass / sumMass;
		double currInfluence = mass / sumMass;

		// center with both parts influence
		x = x * currInfluence + frag.x * fragInfluence;
		y = y * currInfluence + frag.y * fragInfluence;

		// new move vector with both parts influence
		speed.x = speed.x * currInfluence + frag.speed.x * fragInfluence;
		speed.y = speed.y * currInfluence + frag.speed.y * fragInfluence;

		addMass(frag.mass);
	}

	bool canEject() const
	{
		return mass > MIN_EJECT_MASS;
	}

	Ejection eject()
	{
		auto e = *this + speed.normalized() * (radius + 1);
		
		Ejection new_eject;
		new_eject.x = e.x;
		new_eject.y = e.y;
		new_eject.radius = EJECT_RADIUS;
		new_eject.mass = EJECT_MASS;
		new_eject.ownerPlayerId = playerId;
		
		new_eject.speed = speed.take(EJECT_START_SPEED);

		addMass(-EJECT_MASS);
		return new_eject;
	}

	//bool update_by_mass(int max_x, int max_y) {
	//	bool changed = false;
	//	double new_radius = Config::RADIUS_FACTOR * qSqrt(mass);
	//	if (radius != new_radius) {
	//		radius = new_radius;
	//		changed = true;
	//	}

	//	double new_speed = Config::SPEED_FACTOR / qSqrt(mass);
	//	if (speed > new_speed && !is_fast) {
	//		speed = new_speed;
	//	}

	//	if (x - radius < 0) {
	//		x += (radius - x);
	//		changed = true;
	//	}
	//	if (y - radius < 0) {
	//		y += (radius - y);
	//		changed = true;
	//	}
	//	if (x + radius > max_x) {
	//		x -= (radius + x - max_x);
	//		changed = true;
	//	}
	//	if (y + radius > max_y) {
	//		y -= (radius + y - max_y);
	//		changed = true;
	//	}

	//	return changed;
	//}

	void applyDirect(const Point &direct) {
		if (is_fast) return;


		double max_speed = getMaxSpeed();

		double dy = direct.y - y, dx = direct.x - x;
		double dist = qSqrt(dx * dx + dy * dy);
		double ny = (dist > 0) ? (dy / dist) : 0;
		double nx = (dist > 0) ? (dx / dist) : 0;
		double inertion = Config::INERTION_FACTOR;

		speed.x += (nx * max_speed - speed.x) * inertion / mass;
		speed.y += (ny * max_speed - speed.y) * inertion / mass;

		if (speed.length() > max_speed)
			speed = speed.take(max_speed);
	}

	void move() 
	{
		double rB = x + radius, lB = x - radius;
		double dB = y + radius, uB = y - radius;
		auto map_size = Config::MAP_SIZE;

		if (rB + speed.x < map_size && lB + speed.x > 0)
		{
			x += speed.x;
		}
		else 
		{
			// долетаем до стенки
			double new_x = max(radius, min(map_size - radius, x + speed.x));
			x = new_x;
			// зануляем проекцию скорости по dx
			speed.x = 0;
		}

		if (dB + speed.y < map_size && uB + speed.y > 0) 
		{
			y += speed.y;
		}
		else 
		{
			// долетаем до стенки
			double new_y = max(radius, min(map_size - radius, y + speed.y));
			y = new_y;
			// зануляем проекцию скорости по dy
			speed.y = 0;
		}

		if (is_fast)
			applyViscosity(getMaxSpeed());
		
		if (ttf > 0) 
			ttf--;
	}

	bool canShrink() 
	{
		return mass > MIN_SHRINK_MASS;
	}

	void shrink() 
	{
		addMass(-(mass - MIN_SHRINK_MASS) * SHRINK_FACTOR);
	}
};