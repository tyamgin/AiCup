#pragma once

#include "CircularUnit.hpp"
#include "Move.hpp"
#include "Ejection.hpp"
#include "Virus.hpp"

struct PlayerFragment : CircularUnit
{
	int ttf = 0;
	int playerId = 0, fragmentId = 0;
	bool isFast = false;

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

	static int _restFragmentsCount(int existing_fragments_count) 
	{
		return Config::MAX_FRAGS_CNT - existing_fragments_count;
	}
		
	bool canBurst(int yet_cnt)  const
	{
		if (mass < MIN_BURST_MASS * 2)
			return false;
		
		int frags_cnt = int(mass / MIN_BURST_MASS);
		return frags_cnt > 1 && _restFragmentsCount(yet_cnt) > 0;
	}

	bool canSplit(int yet_cnt) const
	{
		return mass > MIN_SPLIT_MASS && _restFragmentsCount(yet_cnt) > 0;
	}

	// вызывается для isFast'ов
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
			isFast = false;
		}
	}

	vector<PlayerFragment> burst(const Virus &virus, int max_fragment_id, int yet_cnt)
	{
		vector<PlayerFragment> fragments;
		double dist = getDistanceTo(virus);
		double dy = y - virus.y, dx = x - virus.x;
		double angle = atan2(y, x);

		if (dist > 0) 
		{
			angle = asin(dy / dist);
			if (dx < 0)
				angle = M_PI - angle;
		}

		mass += BURST_BONUS;

		int new_frags_cnt = int(mass / MIN_BURST_MASS) - 1;

		new_frags_cnt = min(new_frags_cnt, _restFragmentsCount(yet_cnt));

		double new_mass = mass / (new_frags_cnt + 1);

		for (int I = 0; I < new_frags_cnt; I++) 
		{
			int new_fId = max_fragment_id + I + 1;

			PlayerFragment new_fragment;
			new_fragment.x = x;
			new_fragment.y = y;
			new_fragment.addMass(new_mass);
			new_fragment.playerId = playerId;
			new_fragment.fragmentId = new_fId;

			double burst_angle = angle - BURST_ANGLE_SPECTRUM / 2 + I * BURST_ANGLE_SPECTRUM / new_frags_cnt;
			new_fragment.speed = ::Point::byAngle(burst_angle) * BURST_START_SPEED;
			new_fragment.isFast = true;
			new_fragment.ttf = Config::TICKS_TIL_FUSION;

			fragments.push_back(new_fragment);
		}
		speed = ::Point::byAngle(angle + BURST_ANGLE_SPECTRUM / 2) * BURST_START_SPEED;
		isFast = true;

		fragmentId = max_fragment_id + new_frags_cnt + 1;
		addMass(new_mass - mass);
		ttf = Config::TICKS_TIL_FUSION;
		return fragments;
	}

	PlayerFragment split(int max_fragment_id) 
	{
		double new_mass = mass / 2;

		PlayerFragment new_player;
		new_player.x = x;
		new_player.y = y;
		new_player.addMass(new_mass);
		new_player.speed = speed.take(SPLIT_START_SPEED);
		new_player.isFast = true;
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

		return getDistanceTo2(frag) <= sqr(radius + frag.radius);
	}

	void collisionCalc(PlayerFragment &other) 
	{
		if (isFast || other.isFast) // do not collide splits
			return;
		
		double dist2 = getDistanceTo2(other);
		if (dist2 >= sqr(radius + other.radius)) // do not intersects
			return;
		
		auto dist = sqrt(dist2);

		// vector from centers
		double collisionVectorX = x - other.x;
		double collisionVectorY = y - other.y;
		// normalize to 1
		double vectorLen = sqrt(collisionVectorX * collisionVectorX + collisionVectorY * collisionVectorY);
		if (vectorLen < 1e-9) // collision object in same point??
			return;
		
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

	void fusion(const PlayerFragment &frag) 
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

	void applyDirect(const Point &direct) 
	{
		if (isFast)
			return;

		double max_speed = getMaxSpeed();

		double dy = direct.y - y, dx = direct.x - x;
		double dist = sqrt(dx * dx + dy * dy);
		double ny = (dist > 0) ? (dy / dist) : 0;
		double nx = (dist > 0) ? (dx / dist) : 0;
		double inertion = Config::INERTION_FACTOR;

		speed.x += (nx * max_speed - speed.x) * inertion / mass;
		speed.y += (ny * max_speed - speed.y) * inertion / mass;

		dropSpeed();
	}

	void move() 
	{
		auto map_size = Config::MAP_SIZE;

		if (x + radius + speed.x < map_size && x - radius + speed.x > 0)
			x += speed.x;
		else 
		{
			// долетаем до стенки
			x = max(radius, min(map_size - radius, x + speed.x));
			// зануляем проекцию скорости по dx
			speed.x = 0;
		}

		if (y + radius + speed.y < map_size && y - radius + speed.y > 0)
			y += speed.y;
		else 
		{
			// долетаем до стенки
			y = max(radius, min(map_size - radius, y + speed.y));
			// зануляем проекцию скорости по dy
			speed.y = 0;
		}

		if (isFast)
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