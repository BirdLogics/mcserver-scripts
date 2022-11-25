global_plag_started = false;

run_command() -> (
    logger(str('plag: Used by %s.', player()~'name'));
    print_count();
);

reset_leaderboard() -> (
    scoreboard_remove('scarpet_entities');
    scoreboard_add('scarpet_entities');
    scoreboard_property('scarpet_entities', 'display_name', 'Mystery Statistic');

    scoreboard_display('list', 'scarpet_entities');

    update_count();
);

update_count() -> (
    for(player('all'),
        set_stat(_, length(entity_area('*', _~'pos', 128, 512, 128)))
    );
);

print_count() -> (
    for(player('all'),
        print(str('%s: %s entities',
            _~'name',
            length(entity_area('*', _~'pos', 128, 512, 128))
        ))
    );
);

set_stat(player_, value) -> (
    scoreboard('scarpet_entities', player_, value)
);

schedule_repeat() -> (
    update_count();
    schedule(200, 'schedule_repeat');
);

init() -> (
    print('disabled');
    // if(!global_plag_started, start(), print('Already running.'));
);

start() -> (
    global_plag_started = true;
    reset_leaderboard();
    schedule_repeat();
    print('Started plag.');
);

__config() -> {
    'scope' -> 'global',
    'command_permission' -> 'players',
    'commands' -> {'' -> 'run_command', 'start' -> 'init'}
};
