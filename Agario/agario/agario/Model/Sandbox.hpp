#pragma once

#include "World.hpp"

struct EatenFoodEvent
{
	int tick;
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

struct Sandbox : public World 
{
	vector<EatenFoodEvent> eatenFoodEvents;
	vector<EatenFragmentEvent> eatenFragmentEvents;
	vector<LostFragmentEvent> lostFragmentEvents;
	bool opponentDummyStrategy = false;

	Sandbox(const World &world) : World(world)
	{		
	}

	void move(Move move)
	{
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
		
		for (int i = 0; i < (int) me.fragments.size(); i++)
			for (int j = i + 1; j < (int) me.fragments.size(); j++)
				me.fragments[i].collisionCalc(me.fragments[j]);

		for (auto &frag : me.fragments)
			frag.move();

		_doOpponentDummyMove();
	}

	void _doOpponentDummyMove()
	{
		if (!opponentDummyStrategy)
			return;

		for (auto &opp : opponentFragments)
		{
			::Point *predator_pt = nullptr;
			::Point *target_pt = nullptr;
			double predator_dist2 = INFINITY;
			double target_dist2 = INFINITY;

			for (auto &my : me.fragments)
			{
				if (opp.canEat(my))
				{
					auto dist2 = my.getDistanceTo2(opp);
					if (dist2 < target_dist2)
						target_dist2 = dist2, target_pt = &my;
				}
				else if (my.canEat(opp))
				{
					auto dist2 = my.getDistanceTo2(opp);
					if (dist2 < predator_dist2)
						predator_dist2 = dist2, predator_pt = &my;
				}
			}
			if (target_pt && target_dist2 < predator_dist2)
				opp.applyDirect(*target_pt);
			else if (predator_pt)
				opp.applyDirect(opp + (opp - *predator_pt).take(100));
			
			opp.move();
		}
	}

	int _getNearestPredator(const vector<PlayerFragment> &collection, const CircularUnit &unit)
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
		return nearest_predator_idx;
	}

	void _doEat()
	{



		//auto nearest_virus = [this](Ejection *eject) {
		//	Virus *nearest_predator = NULL;
		//	double deeper_dist = -INFINITY;
		//	for (Virus *predator : virus_array) {
		//		double qdist = predator->can_eat(eject);
		//		if (qdist > deeper_dist) {
		//			deeper_dist = qdist;
		//			nearest_predator = predator;
		//		}
		//	}
		//	return nearest_predator;
		//};

		// поедаю еду
		for (int i = 0; i < (int)foods.size(); i++)
		{
			auto &food = foods[i];
			auto nearest_predator_idx = _getNearestPredator(me.fragments, food);
			if (nearest_predator_idx != -1)
			{
				eatenFoodEvents.push_back({ tick });
				me.fragments[nearest_predator_idx].addMass(Config::FOOD_MASS);
				foods.erase(foods.begin() + i);
				i--;
			}
		}

		//for (auto eit = eject_array.begin(); eit != eject_array.end(); ) {
		//	auto eject = *eit;
		//	if (Virus *eater = nearest_virus(eject)) {
		//		eater->eat(eject);
		//	}
		//	else if (Player *eater = nearest_player(eject)) {
		//		eater->eat(eject);
		//	}
		//	else {
		//		eit++;
		//		continue;
		//	}
		//	logger->write_kill_cmd(tick, eject);
		//	delete eject;
		//	eit = eject_array.erase(eit);
		//}

		// я поедаю
		for (int i = 0; i < (int)opponentFragments.size(); i++)
		{
			auto &opp = opponentFragments[i];
			auto nearest_predator_idx = _getNearestPredator(me.fragments, opp);
			if (nearest_predator_idx != -1)
			{
				me.fragments[nearest_predator_idx].addMass(opp.mass);
				eatenFragmentEvents.push_back({ tick, opp.mass });
				opponentFragments.erase(opponentFragments.begin() + i);
				i--;
			}
		}

		// меня поедают
		for (int i = 0; i < (int)me.fragments.size(); i++)
		{
			auto &my = me.fragments[i];
			auto nearest_predator_idx = _getNearestPredator(opponentFragments, my);
			if (nearest_predator_idx != -1)
			{
				opponentFragments[nearest_predator_idx].addMass(my.mass);
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
		sort(me.fragments.begin(), me.fragments.end(), [](const PlayerFragment &a, const PlayerFragment &b)
		{
			if (a.mass == b.mass)
				return a.fragmentId < b.fragmentId;
			return a.mass > b.mass;
		});

		bool new_fusion_check = true;
		while (new_fusion_check) 
		{
			new_fusion_check = false;
			for (int i = 0; i < (int) me.fragments.size(); i++)
			{
				auto &frag1 = me.fragments[i];
				if (frag1.ttf)
					continue;

				for (int j = i + 1; j < (int)me.fragments.size(); j++)
				{
					auto &frag2 = me.fragments[j];
					if (frag1.canFuse(frag2))
					{
						frag1.fusion(frag2);
						new_fusion_check = true;
						me.fragments.erase(me.fragments.begin() + j);
						j--;
					}
				}
			}
			if (new_fusion_check) 
				for (auto &frag : me.fragments)
					_doFix(frag);
		}
		if (me.fragments.size() == 1)
			me.fragments[0].fragmentId = 0;
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
