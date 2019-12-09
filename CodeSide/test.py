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
    idx, p1, p2, lr_bin, start_seed, level, nthreads, count = args
    port1 = 32003 + idx * 2
    port2 = port1 + 1
    seed = start_seed + idx
    # print([port1, port2])
    # return

    if level in ["Simple"]:
        level_config = level
    else:
        level_config = {"LoadFrom": {"path": level}}

    config = {
        "options_preset": {
            "Custom": {
                "level": level_config,
                "properties": None,
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
            subprocess.Popen([p1, "127.0.0.1", str(port1), "0000000000000000"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            subprocess.Popen([p2, "127.0.0.1", str(port2), "0000000000000000"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            process.wait()
            with open(result_path) as result_inp:
                result = json.load(result_inp)
                log.info("game with seed=%d results: %d - %d", seed, result["results"][0], result["results"][1])
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
def run(p1, p2, lr_bin, start_seed, level, nthreads, count):
    if not os.path.exists("tmp"):
        os.makedirs("tmp")

    pool = multiprocessing.Pool(processes=nthreads, initializer=start_process)
    pool.map(worker, [(
            i,
            p1, p2, lr_bin, start_seed, level, nthreads, count
    ) for i in range(count)])


if __name__ == "__main__":
    main()
