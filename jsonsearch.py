import json
with open('./environmental_safety_inspection.json') as file:
    env = json.load(file)
with open('./program_running_normal.json') as file:
    prog = json.load(file)
env_hits=env.get("hits").get("hits")
prog_hits = prog.get("hits").get("hits")
for env_hit in env_hits:
    for prog_hit in prog_hits:
        if (env_hit.get("_source").get("deviceId") == prog_hit.get("_source").get("deviceId")) \
        and (env_hit.get("_source").get("appId") == prog_hit.get("_source").get("appId")) \
        and (env_hit.get("_source").get("userId") == prog_hit.get("_source").get("userId")):
            print("env_id: ",env_hit.get("_id"), "prog_id: ", prog_hit.get("_id"),env_hit.get("_source").get("deviceId"), env_hit.get("_source").get("appId"), env_hit.get("_source").get("userId"))