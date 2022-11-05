__on_player_changes_dimension(player, from_pos, from_dimension, to_pos, to_dimension) -> (
    if(to_dimension == 'the_end',
        schedule(20, 'kill_player', player~'name');
    );
);

kill_player(name) -> (
    print('Sorry, the end is disabled.');
    run('/kill ' + name);
);
