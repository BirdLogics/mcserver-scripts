global_afk_start = {};
global_afk_end = {};
global_afk_timers = {};
global_afk_players = [];
global_afk_lag_counter = 0;

// Commands
go_afk() -> (
    player_name = player()~'name';

    logger(str('Player %s is going afk.', player_name));

    start_afk(player_name);
);

go_afk_for(minutes) -> (
    player_name =  player()~'name';
    end_time = tick_time() + minutes * 20;

    logger(str('Player %s is going afk for %d minutes.', player_name, minutes));

    start_afk(player_name);
    put(global_afk_timers, player_name, end_time);
);

print_help() -> (
    print('Replaces you with a robot player using carpet mod.');
    print('Your actual player is the robot, so you can still loose items.');
    print('If the server lags, you may be kicked to improve performance.');
    print('You can use "/afk for" to afk for a certain number of minutes.');
    print('You won\'t be kicked for lag if you afk for < 20 minutes.');
);

kick_all() -> (
    if(player()~'permission_level' > 3,
        kick_afk_players(0),
        print('Not allowed.')
    );
);

// Runs once per minute

check_lag() -> (
    if(average_mspt() > 50 && global_afk_lag_counter < 3,
        global_afk_lag_counter += 1,
        global_afk_lag_counter = 0
    );

    if(global_afk_lag_counter >= 3,
        kick_afk_players(tick_time() + 400;);
    );
);

check_timers() -> (
    current_time = tick_time();

    for(global_afk_playes,
        if(has(global_afk_timers, _) && current_time > get(global_afk_timers, _),
            end_afk(_);
        );
    );
);

// Helpers

kick_afk_players(max_timer) -> (
    for(global_afk_players,
        if(!has(global_afk_timers, _) || get(global_afk_timers, _) > max_timer,
            end_afk(_)
        );
    );
);

start_afk(player_name) -> (
    logger('afk: start afk for ' + player_name);
    run(str('player %s shadow', player_name));

    put(global_afk_start, player_name, tick_time());
    schedule(20, 'mark_afk', player_name);
);

end_afk(player_name) -> (
    logger('afk: end afk for ' + player_name);
    run(str('player %s kill', player_name)); // kick bots only

    delete(global_afk_timer, player_name);
);

mark_afk(player_name) -> (
    put(global_afk_players, null, player_name); // append
    team_add('afkDis.afk', player_name);
);

average_mspt() -> (
    mspt = system_info('server_last_tick_times');
    (mspt:0 + mspt:20 + mspt:40 + mspt:60 + mspt:80) / 5
);

// Events

// On afk start, connect then disconnect
// On afk end, disconnect then connect

__on_player_connects(target) -> (
    player_name = target~'name';

    if(has(global_afk_end, player_name),
        afk_time = get(global_afk_end, player_name) - get(global_afk_start, player_name);
        print(str('You were afk for %d minutes.', round(afk_time / 1200)));

        delete(global_afk_start, player_name);
        delete(global_afk_end, player_name);
    );
);

__on_player_disconnects(target, reason) -> (
    player_name = target~'name';

    if(global_afk_players~player_name != null,
        delete(global_afk_players, global_afk_players~player_name);
        put(global_afk_end, player_name, tick_time());
    );
);

// Config

schedule_check() -> (
    check_lag();
    check_timers();

    schedule(1200, 'schedule_check');
);

__config() -> {
    'scope' -> 'global',
    'command_permission' -> 'players',
    'commands' -> {
        '' -> 'go_afk',
        'for <minutes>' -> 'go_afk_for',
        'help' -> 'print_help',
    },
    'arguments' -> {
        'minutes' -> {'type' -> 'int', 'min' -> 1, 'max' -> 12000, 'suggest' -> [60]}
    }
};

schedule_check();
