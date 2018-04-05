#pragma once

#include "CircularUnit.hpp"
#include "../Config.hpp"

struct Virus : CircularUnit
{
	int id;

	Virus(const nlohmann::json &obj) : CircularUnit(obj)
	{
		radius = Config::VIRUS_RADIUS;
		id = stoi(obj["Id"].get<string>());
	}


	//double can_hurt(Circle *circle) const {
	//	if (circle->getR() < radius) {
	//		return INFINITY;
	//	}
	//	double qdist = circle->calc_qdist(x, y);
	//	double tR = radius * RAD_HURT_FACTOR + circle->getR();
	//	if (qdist < tR * tR) {
	//		return qdist;
	//	}
	//	return INFINITY;
	//}

	//double can_eat(Ejection *eject) {
	//	if (mass > eject->getM() * MASS_EAT_FACTOR) { // eat ejection
	//		double dist = eject->calc_dist(x, y);
	//		if (dist - eject->getR() + (eject->getR() * 2) * DIAM_EAT_FACTOR < radius) {
	//			return radius - dist;
	//		}
	//	}
	//	return -INFINITY;
	//}

	//void eat(Ejection *eject) {
	//	mass += eject->getM();
	//	split_angle = eject->getA();
	//}

	//bool can_split() {
	//	return mass > Constants::instance().VIRUS_SPLIT_MASS;
	//}

	//Virus *split_now(int new_id) {
	//	double new_speed = VIRUS_SPLIT_SPEED, new_angle = split_angle;

	//	Virus *new_virus = new Virus(new_id, x, y, Constants::instance().VIRUS_RADIUS, VIRUS_MASS);
	//	new_virus->set_impulse(new_speed, new_angle);

	//	mass = VIRUS_MASS;
	//	return new_virus;
	//}

	//bool move(int max_x, int max_y) {
	//	if (speed == 0.0) {
	//		return false;
	//	}
	//	double dx = speed * qCos(angle);
	//	double dy = speed * qSin(angle);

	//	double new_x = qMax(radius, qMin(max_x - radius, x + dx));
	//	bool changed = (x != new_x);
	//	x = new_x;

	//	double new_y = qMax(radius, qMin(max_y - radius, y + dy));
	//	changed |= (y != new_y);
	//	y = new_y;

	//	speed = qMax(0.0, speed - Constants::instance().VISCOSITY);
	//	return changed;
	//}
};