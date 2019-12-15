#!/usr/bin/env python3

import click
import subprocess
import json
import os
import time
import logging
import traceback
import multiprocessing


logging.basicConfig(
    format='%(asctime)s %(levelname)-8s %(message)s',
    level=logging.INFO,
    datefmt='%Y-%m-%d %H:%M:%S')
log = logging.getLogger()


@click.group()
def main():
    pass


def get_player_config(port):
    return {
       "Tcp": {
           "host": None,
           "port": port,
           "accept_timeout": None,
           "timeout": None,
           "token": None
       }
    }


def worker(args):
    idx, p1, p2, lr_bin, start_seed, level, nthreads, count, team_size, profile_mode = args
    port1 = 32003 + idx * 2
    port2 = port1 + 1
    seed = start_seed + idx
    # print([port1, port2])
    # return

    if level in ["Simple"]:
        level_config = level
    else:
        level_config = {"LoadFrom": {"path": level}}

    swap = idx % 2

    config = {
        "options_preset": {
            "Custom": {
                "level": level_config,
                "properties": {
                    "max_tick_count": 3600,
                    "team_size": team_size,
                    "ticks_per_second": 60.0,
                    "updates_per_tick": 100,
                    "loot_box_size": {
                        "x": 0.5,
                        "y": 0.5
                    },
                    "unit_size": {
                        "x": 0.9,
                        "y": 1.8
                    },
                    "unit_max_horizontal_speed": 10.0,
                    "unit_fall_speed": 10.0,
                    "unit_jump_time": 0.55,
                    "unit_jump_speed": 10.0,
                    "jump_pad_jump_time": 0.525,
                    "jump_pad_jump_speed": 20.0,
                    "unit_max_health": 100,
                    "health_pack_health": 50,
                    "weapon_params": {
                        "RocketLauncher": {
                            "magazine_size": 1,
                            "fire_rate": 1.0,
                            "reload_time": 1.0,
                            "min_spread": 0.1,
                            "max_spread": 0.5,
                            "recoil": 1.0,
                            "aim_speed": 1.0,
                            "bullet": {
                                "speed": 20.0,
                                "size": 0.4,
                                "damage": 30
                            },
                            "explosion": {
                                "radius": 3.0,
                                "damage": 50
                            }
                        },
                        "Pistol": {
                            "magazine_size": 8,
                            "fire_rate": 0.4,
                            "reload_time": 1.0,
                            "min_spread": 0.05,
                            "max_spread": 0.5,
                            "recoil": 0.5,
                            "aim_speed": 1.0,
                            "bullet": {
                                "speed": 50.0,
                                "size": 0.2,
                                "damage": 20
                            },
                            "explosion": None
                        },
                        "AssaultRifle": {
                            "magazine_size": 20,
                            "fire_rate": 0.1,
                            "reload_time": 1.0,
                            "min_spread": 0.1,
                            "max_spread": 0.5,
                            "recoil": 0.2,
                            "aim_speed": 1.9,
                            "bullet": {
                                "speed": 50.0,
                                "size": 0.2,
                                "damage": 5
                            },
                            "explosion": None
                        }
                    },
                    "mine_size": {
                        "x": 0.5,
                        "y": 0.5
                    },
                    "mine_explosion_params": {
                        "radius": 3.0,
                        "damage": 50
                    },
                    "mine_prepare_time": 1.0,
                    "mine_trigger_time": 0.5,
                    "mine_trigger_radius": 1.0,
                    "kill_score": 1000
                }
            }
        },
        "players": [
            get_player_config(port1),
            get_player_config(port2),
        ],
        "seed": seed,
    }
    cwd = os.getcwd()

    config_path = os.path.join(cwd, f"tmp/_config{idx}.json")
    result_path = os.path.join(cwd, f"tmp/_result{idx}.txt")
    with open(config_path, "w") as out:
        json.dump(config, out, indent=4)
    if os.path.exists(result_path):
        os.remove(result_path)

    try:
        with subprocess.Popen(f"{lr_bin} --config {config_path} --save-results {result_path} --batch-mode --log-level warn".split(" ")) as process:
            time.sleep(0.5)
            stream = subprocess.DEVNULL
            if profile_mode:
                stream = None
            subprocess.Popen([p2 if swap else p1, "127.0.0.1", str(port1), "0000000000000000"], stdout=stream, stderr=stream)
            subprocess.Popen([p1 if swap else p2, "127.0.0.1", str(port2), "0000000000000000"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            process.wait()
            with open(result_path) as result_inp:
                result = json.load(result_inp)
                log.info("game with seed=%d results: %d - %d", seed, result["results"][swap], result["results"][1 - swap])
                #print(" - ".join([("CRASHERD " if result["players"][i]["crashed"] else "") + str(result["results"][i]) for i in range(2)]))
    except Exception:
        log.error(traceback.format_exc())
    finally:
        pass


def start_process():
    pass


@main.command()
@click.option('--p1', type=str, required=True)
@click.option('--p2', type=str, required=True)
@click.option('--lr-bin', type=str, default="./local-runner/aicup2019")
@click.option('--start-seed', type=int, default=1)
@click.option('--level', type=str, default="Simple")
@click.option('--nthreads', type=int, default=4)
@click.option('--count', type=int, default=12)
@click.option('--team-size', type=int, default=1)
@click.option('--profile-mode', is_flag=True)
def run(p1, p2, lr_bin, start_seed, level, nthreads, count, team_size, profile_mode):
    if not os.path.exists("tmp"):
        os.makedirs("tmp")

    pool = multiprocessing.Pool(processes=nthreads, initializer=start_process)
    pool.map(worker, [(
            i,
            p1, p2, lr_bin, start_seed, level, nthreads, count, team_size, profile_mode
    ) for i in range(count)])


if __name__ == "__main__":
    main()
