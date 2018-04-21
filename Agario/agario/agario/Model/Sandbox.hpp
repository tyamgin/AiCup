#pragma once

#include "World.hpp"

struct EatenFoodEvent
{
	int tick;
};

struct EatenEjectionEvent
{
	int tick;
	int ownerPlayerId;
};

struct EatenFragmentEvent
{
	int tick;
	double mass;
};

struct LostFragmentEvent
{
	int tick;
	double mass;
};

#define VISION_GRID_SIZE 48

struct Sandbox : public World 
{
	int lastSeen[VISION_GRID_SIZE + 1][VISION_GRID_SIZE + 1];
	vector<EatenFoodEvent> eatenFoodEvents;
	vector<EatenEjectionEvent> eatenEjectionEvents;
	vector<EatenFragmentEvent> eatenFragmentEvents;
	vector<LostFragmentEvent> lostFragmentEvents;
	bool useVisionMap = false;
	bool opponentDummyStrategy = false;
	bool opponentFuseStrategy = true;
	bool opponentForseFuseStrategy = false;
	bool opponentForseFuseStrategy2 = false;

	Sandbox(const World &world, int lastSeen[][VISION_GRID_SIZE + 1]) : World(world)
	{
		memcpy(this->lastSeen, lastSeen, sizeof(this->lastSeen));
	}

	void move(Move move)
	{
		OP_START(SANDBOX);

		_doApply(move);
		tick++;
		_doMove(move);
		_doEject(move);
		_doSplit(move);
		_doShrink();
		_doEat();

		_doFuse();
		_doBurst();

		_doFixes();
		//update_scores();
		//split_viruses();
		_updateVisionMap();

		OP_END(SANDBOX);
	}

	

private:
	void _doApply(const Move &move)
	{
		for (auto &frag : me.fragments)
			frag.applyDirect(move);
	}

	void _doMove(const Move &move)
	{
		for (auto &ej : ejections)
			ej.move();
		
		for (int i = 0, size = (int)me.fragments.size(); i < size; i++)
		{
			auto &frag1 = me.fragments[i];
			if (!frag1.isFast)
				for (int j = i + 1; j < size; j++)
					frag1.collisionCalc(me.fragments[j]);
		}
		for (int i = 0, size = (int)opponentFragments.size(); i < size; i++)
		{
			auto &frag1 = opponentFragments[i];
			if (!frag1.isFast)
			{
				for (int j = i + 1; j < size; j++)
				{
					auto &frag2 = opponentFragments[j];
					if (frag1.playerId == frag2.playerId)
						frag1.collisionCalc(frag2);
				}
			}
		}

		for (auto &frag : me.fragments)
			frag.move();

		
		_doOpponentDummyMove();
	}

	void _updateVisionMap()
	{
		if (!useVisionMap)
			return;

		for (auto &frag : me.fragments)
		{
			auto cen = frag.getVisionCenter();
			auto vis_rad = frag.radius * (me.fragments.size() <= 1 ? VIS_FACTOR : VIS_FACTOR_FR * sqrt(1.0 * me.fragments.size()));
			auto vis_rad2 = vis_rad*vis_rad;

			double d = 1.0 * Config::MAP_SIZE / VISION_GRID_SIZE;

			int startI = max(0, int((cen.x - vis_rad) / d - EPS) + 1);
			int startJ = max(0, int((cen.y - vis_rad) / d - EPS) + 1);

			bool anyI = false;
			for (int i = startI; i <= VISION_GRID_SIZE; i++)
			{
				double x = d * i;
				bool anyJ = false;
				for (int j = startJ; j <= VISION_GRID_SIZE; j++)
				{
					double y = d * j;
					if (cen.getDistanceTo2(x, y) <= vis_rad2)
					{
						lastSeen[i][j] = tick;
						anyJ = true;
					}
					else if (anyJ)
					{
						break;
					}
				}
				if (anyI && !anyJ)
					break;
				anyI = anyJ;
			}
		}
	}

	PlayerFragment *all_fragments_buf[128];

	void _doOpponentDummyMove()
	{
		if (!opponentDummyStrategy || opponentFragments.empty())
			return;

		OP_START(SANDBOX_OPPONENT_DUMMY_MOVE);

		int all_fragments_size = 0;
		for (auto &frag : me.fragments)
			all_fragments_buf[all_fragments_size++] = &frag;
		for (auto &frag : opponentFragments)
			all_fragments_buf[all_fragments_size++] = &frag;

		for (auto &opp : opponentFragments)
		{
			PlayerFragment *predator_pt = nullptr;
			PlayerFragment *target_pt = nullptr;
			double predator_dist2 = INFINITY;
			double target_dist2 = INFINITY;
			auto opp2 = opp;
			opp2.move();

			for (int i = 0; i < all_fragments_size; i++)
			{
				auto other = all_fragments_buf[i];
				if (opp.playerId == other->playerId)
					continue;

				if (opp.canEat(*other))
				{
					auto dist2 = other->getDistanceTo2(opp2);
					if (dist2 < target_dist2)
						target_dist2 = dist2, target_pt = other;
				}
				else if (other->canEat(opp))
				{
					auto dist2 = other->getDistanceTo2(opp2);
					if (dist2 < predator_dist2)
						predator_dist2 = dist2, predator_pt = other;
				}
			}

			if (target_pt && target_dist2 < predator_dist2 && !opponentForseFuseStrategy)
			{
				// если цель для него близко, то перебираем ходы точнее
				if (target_dist2 < sqr(20 + target_pt->radius + 2*opp.radius))
				{
					::Point sel_dir;
					double min_dist2 = INFINITY;
					double ang = atan2(target_pt->y - opp.y, target_pt->x - opp.x);
					double ang_from = ang - M_PI / 4;
					double ang_to = ang + M_PI / 4;
					const int grid = 16;
					double max_speed = opp.getMaxSpeed();
					for (int i = 0; i <= grid; i++)
					{
						auto clone = opp;
						auto n = ::Point::byAngle((ang_to - ang_from) / grid * i + ang_from);
						clone.applyDirect2(n, max_speed);
						clone.move();
						auto dist2 = target_pt->getDistanceTo2(clone);
						if (dist2 < min_dist2)
						{
							min_dist2 = dist2;
							sel_dir = opp + n;
						}
					}
					opp.applyDirect(sel_dir);
				}
				else
				{
					opp.applyDirect(*target_pt);
				}
			}
			else if (predator_pt || opponentForseFuseStrategy || opponentForseFuseStrategy2)
			{
				PlayerFragment *nearest_fuser = nullptr;
				if (opponentForseFuseStrategy || predator_pt == nullptr ||
					opponentFuseStrategy && opp.ttf == 0 && predator_dist2 < sqr(20 + 3*predator_pt->radius + opp.radius))
				{
					double nearest_fuser_dist2 = INFINITY;
					for (auto &fuser : opponentFragments)
					{
						if (fuser.playerId == opp.playerId && fuser.fragmentId != opp.fragmentId && (fuser.ttf == 0 || opponentForseFuseStrategy)
							//&& fuser.mass + opp.mass > MASS_EAT_FACTOR*predator_pt->mass
							)
						{
							auto dist2 = opp.getDistanceTo2(fuser);
							if (nearest_fuser == nullptr || 
								//dist - opp.radius - fuser.radius < nearest_fuser_dist - opp.radius - nearest_fuser->radius
								//dist - fuser.radius < nearest_fuser_dist - nearest_fuser->radius
								dist2 < sqr(sqrt(nearest_fuser_dist2) - nearest_fuser->radius + fuser.radius)
								)
							{
								nearest_fuser_dist2 = dist2;
								nearest_fuser = &fuser;
							}
						}
					}
				}
				if (nearest_fuser != nullptr)
					opp.applyDirect(*nearest_fuser);
				else if (predator_pt != nullptr)
					opp.applyDirect(opp + (opp - *predator_pt));
			}
			else
			{
				// к ближайшей еде
				opp2.move();
				opp2.move();
				Food *nearest_food = nullptr;
				double nearest_food_dist2 = INFINITY;
				for (auto &food : foods)
				{
					auto dist2 = opp2.getDistanceTo2(food);
					if (dist2 < nearest_food_dist2)
					{
						nearest_food_dist2 = dist2;
						nearest_food = &food;
					}
				}
				if (nearest_food_dist2 < sqr(opp2.radius + 70) && nearest_food != nullptr)
					opp.applyDirect(*nearest_food);
			}
			opp.move();
		}

		OP_END(SANDBOX_OPPONENT_DUMMY_MOVE);
	}

	pair<double, int> _getNearestPredator(const vector<PlayerFragment> &collection, const CircularUnit &unit)
	{
		int nearest_predator_idx = -1;
		double deeper_dist = -INFINITY;
		for (int i = 0; i < (int)collection.size(); i++)
		{
			auto &predator = collection[i];
			double qdist = predator.eatDepth(unit);
			if (qdist > deeper_dist)
			{
				deeper_dist = qdist;
				nearest_predator_idx = i;
			}
		}
		return{ deeper_dist, nearest_predator_idx };
	}

	pair<double, int> _getNearestPredator2(const vector<PlayerFragment> &collection1, const vector<PlayerFragment> &collection2, const PlayerFragment &unit)
	{
		int nearest_predator_idx = -1;
		double deeper_dist = -INFINITY;
		for (int i = 0; i < (int)collection1.size(); i++)
		{
			auto &predator = collection1[i];
			double qdist = predator.eatDepth(unit);
			if (qdist > deeper_dist && unit.playerId != predator.playerId)
			{
				deeper_dist = qdist;
				nearest_predator_idx = i;
			}
		}
		for (int i = 0; i < (int)collection2.size(); i++)
		{
			auto &predator = collection2[i];
			double qdist = predator.eatDepth(unit);
			if (qdist > deeper_dist && unit.playerId != predator.playerId)
			{
				deeper_dist = qdist;
				nearest_predator_idx = (int)collection1.size() + i;
			}
		}
		return{ deeper_dist, nearest_predator_idx };
	}

	void _doEat()
	{
		// поедают еду
		for (int i = 0; i < (int)foods.size(); i++)
		{
			auto &food = foods[i];
			auto nearest_my_predator_idx = _getNearestPredator(me.fragments, food);
			auto nearest_opp_predator_idx = _getNearestPredator(opponentFragments, food);
			if (nearest_my_predator_idx.second == -1 && nearest_opp_predator_idx.second == -1)
				continue;

			if (nearest_my_predator_idx.first > nearest_opp_predator_idx.first)
			{
				eatenFoodEvents.push_back({ tick });
				me.fragments[nearest_my_predator_idx.second].mass += Config::FOOD_MASS; // do not use addMass
			}
			else
			{
				opponentFragments[nearest_opp_predator_idx.second].mass += Config::FOOD_MASS; // do not use addMass
			}
			foods.erase(foods.begin() + i);
			i--;
		}

		// поедают выбросы
		for (int i = 0; i < (int)ejections.size(); i++)
		{
			auto &ej = ejections[i];
			auto nearest_my_predator_idx = _getNearestPredator(me.fragments, ej);
			auto nearest_opp_predator_idx = _getNearestPredator(opponentFragments, ej);
			if (nearest_my_predator_idx.second == -1 && nearest_opp_predator_idx.second == -1)
				continue;

			if (nearest_my_predator_idx.first > nearest_opp_predator_idx.first)
			{
				eatenEjectionEvents.push_back({ tick, ej.ownerPlayerId });
				me.fragments[nearest_my_predator_idx.second].mass += EJECT_MASS; // do not use addMass
			}
			else
			{
				opponentFragments[nearest_opp_predator_idx.second].mass += EJECT_MASS; // do not use addMass
			}
			ejections.erase(ejections.begin() + i);
			i--;
		}


		// противника поедают
		for (int i = 0; i < (int)opponentFragments.size(); i++)
		{
			auto &opp = opponentFragments[i];
			auto nearest_predator_idx = _getNearestPredator2(me.fragments, opponentFragments, opp).second;
			if (nearest_predator_idx != -1)
			{
				if (nearest_predator_idx < (int)me.fragments.size())
				{
					me.fragments[nearest_predator_idx].mass += opp.mass;  // do not use addMass
					eatenFragmentEvents.push_back({ tick, opp.mass });
				}
				else
				{
					opponentFragments[nearest_predator_idx - (int)me.fragments.size()].mass += opp.mass;  // do not use addMass
				}
				opponentFragments.erase(opponentFragments.begin() + i);
				i--;
			}
		}

		// меня поедают
		for (int i = 0; i < (int)me.fragments.size(); i++)
		{
			auto &my = me.fragments[i];
			auto nearest_predator_idx = _getNearestPredator(opponentFragments, my).second;
			if (nearest_predator_idx != -1)
			{
				opponentFragments[nearest_predator_idx].mass += my.mass; // do not use addMass
				lostFragmentEvents.push_back({ tick, my.mass });
				me.fragments.erase(me.fragments.begin() + i);
				i--;
			}
		}
	}

	void _doShrink() 
	{
		if (tick % SHRINK_EVERY_TICK)
			return;

		for (auto &frag : me.fragments)
			if (frag.canShrink())
				frag.shrink();
	}

	int _maxMeId()
	{
		int max_id = 0;
		for (auto &frag : me.fragments)
			max_id = max(max_id, frag.fragmentId);
		return max_id;
	}

	void _doSplit(const Move &move)
	{
		if (!move.split)
			return;

		int yet_cnt = me.fragments.size();
		int size = yet_cnt;

		int max_id = _maxMeId();

		sort(me.fragments.begin(), me.fragments.end(), [](const PlayerFragment &a, const PlayerFragment &b)
		{
			if (a.mass == b.mass)
				return a.fragmentId > b.fragmentId;
			return a.mass > b.mass;
		});

		for (int i = 0; i < size; i++)
		{
			auto &frag = me.fragments[i];
			if (frag.canSplit(yet_cnt)) 
			{
				auto new_frag = frag.split(max_id);
				me.fragments.push_back(new_frag);
				yet_cnt++;
			}
		}
	}

	void _doEject(const Move &move)
	{
		if (!move.eject)
			return;

		for (auto &frag : me.fragments)
		{
			if (frag.canEject())
			{
				auto new_ej = frag.eject();
				ejections.push_back(new_ej);
			}
		}
	}

	void _doFix(PlayerFragment &frag)
	{
		frag.addMass(0); // fix radius

		if (!frag.isFast)
			frag.dropSpeed();
		
		auto mx = Config::MAP_SIZE;

		if (frag.x - frag.radius < 0)
			frag.x += (frag.radius - frag.x);
		
		if (frag.y - frag.radius < 0)
			frag.y += (frag.radius - frag.y);
		
		if (frag.x + frag.radius > mx)
			frag.x -= (frag.radius + frag.x - mx);
		
		if (frag.y + frag.radius > mx)
			frag.y -= (frag.radius + frag.y - mx);
	}

	void _doFixes() 
	{
		for (auto &frag : me.fragments)
			_doFix(frag);
		for (auto &frag : opponentFragments)
			_doFix(frag);
	}

	void _doFuse()
	{
		_doFuse(me.fragments);
		if (opponentDummyStrategy && opponentFuseStrategy)
			_doFuse(opponentFragments);
	}


	void _doFuse(vector<PlayerFragment> &fragments) 
	{
		sort(fragments.begin(), fragments.end(), [](const PlayerFragment &a, const PlayerFragment &b)
		{
			if (a.mass == b.mass)
				return a.fragmentId < b.fragmentId;
			return a.mass > b.mass;
		});

		bool new_fusion_check = true;
		while (new_fusion_check) 
		{
			new_fusion_check = false;
			for (int i = 0; i < (int)fragments.size(); i++)
			{
				if (fragments[i].ttf)
					continue;

				for (int j = i + 1; j < (int)fragments.size(); j++)
				{
					auto &frag1 = fragments[i];
					auto &frag2 = fragments[j];
					if (frag1.playerId == frag2.playerId && frag1.canFuse(frag2))
					{
						frag1.fusion(frag2);
						new_fusion_check = true;
						fragments.erase(fragments.begin() + j);
						j--;
					}
				}
			}
			if (new_fusion_check) 
				for (auto &frag : fragments)
					_doFix(frag);
		}
		if (fragments.size() == 1)
			fragments[0].fragmentId = 0;
	}


	int _nearestVirusTarget(const Virus &virus)
	{
		double nearest_dist = INFINITY;
		int nearest_fragment_idx = -1;

		int yet_cnt = me.fragments.size();
		for (int i = 0; i < yet_cnt; i++)
		{
			auto &frag = me.fragments[i];
			double qdist = virus.hurtDepth(frag);
			if (qdist < nearest_dist)
			{
				if (frag.canBurst(yet_cnt))
				{
					nearest_dist = qdist;
					nearest_fragment_idx = i;
				}
			}
		}
		return nearest_fragment_idx;
	}

	void _doBurst() 
	{
		for (int i = 0; i < (int) viruses.size(); i++)
		{
			auto &virus = viruses[i];
			auto frag_idx = _nearestVirusTarget(virus);
			if (frag_idx == -1)
				continue;

			auto &target = me.fragments[frag_idx];
			int yet_cnt = me.fragments.size();
			int max_fragment_id = _maxMeId();

			auto new_fragments = target.burst(virus, max_fragment_id, yet_cnt);
			me.fragments.insert(me.fragments.end(), new_fragments.begin(), new_fragments.end());

			viruses.erase(viruses.begin() + i);
			i--;
		}
	}

};
