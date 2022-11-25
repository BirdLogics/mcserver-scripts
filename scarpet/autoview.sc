__on_player_connects(_) -> (
    update_view_distance();
);

__on_player_disconnects(_, _) -> (
    update_view_distance();
);

update_view_distance() -> (
    player_count = length(player('all'));
    current_dist = system_info('game_view_distance');

    new_dist = if(
        player_count <= 1, 32,
        player_count <= 2, 16,
        player_count <= 3, 12,
        player_count <= 4, 10,
        player_count >= 5, 8,
        8
    );

    if(current_dist != new_dist,
        run(str('carpet viewDistance %d', new_dist));
        logger(str('autoview: viewDistance is now %d.', new_dist));
    );
);

update_view_distance();
