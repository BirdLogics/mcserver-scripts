global_stat_grant = '';

init_stat(category, event) -> (
    logger('stats: run by ' + player()~'name');
    if(player()~'name' == global_stat_grant,
        init_stat2(category, event);
        print(player('all'), format(str('y %s set the mystery statistic!', player()~'name'))),
        print('Not allowed.')
    );
);

grant(target) -> (
    if(player()~'permission_level' > 2 || player()~'name' == global_stat_grant,
        global_stat_grant = target:0;
        print(player('all'), format(str('y %s can now set the mystery statistic.', target:0))),
        print('Not allowed.')
    );
);

init_stat2(category, event) -> (
    // Verify the statistic exists (for the calling player)
    if (statistic(player(), category, event) == null, print('Invalid statistic'); return());

    // Save the new statistic
    data = encode_nbt({'category' -> category, 'event'-> event});
    store_app_data(data);

    // Determine the criterion
    criterion = if (category == 'custom', event,
        'minecraft.' + category + ':minecraft.' + event);

    // Reset the leaderboard
    scoreboard_remove('scarpet_stat');
    scoreboard_add('scarpet_stat', criterion);
    scoreboard_property('scarpet_stat', 'display_name', 'Mystery Statistic');

    // Show the leaderboard
    scoreboard_display('list', 'scarpet_stat');

    // Set the leaderboard for all players
    for (player('all'),
        scoreboard('scarpet_stat', _,
            statistic(_, category, event)
    ));

    print(player(), 'Done.');
);

stat_blocks() -> (
    print(player(), block_list());
);

stat_entities() -> (
    print(player(), entity_types('*'));
);

stat_items() -> (
    print(player(), item_list());
);

__on_start(outer(config_category), outer(config_event)) -> (
    // Initialize the leaderboard
    scoreboard_add('scarpet_stat');
);

__on_player_connects(target) -> (
    // Update the leaderboard for the player
    data = parse_nbt(load_app_data());
    value = statistic(target, get(data, 'category'), get(data, 'event'));
    scoreboard('scarpet_stat', target, value);
);

__config() -> {
    'scope' -> 'global',
    'command_permission' -> 'all',
    'commands' -> {
        'mined <block>' -> _(block) -> init_stat('mined', block),
        'crafted <item>' -> _(item) -> init_stat('crafted', item),
        'used <item>' -> _(item) -> init_stat('used', item),
        'broken <item>' -> _(item) -> init_stat('broken', item),
        'picked_up <item>' -> _(item) -> init_stat('picked_up', item),
        'dropped <item>' -> _(item) -> init_stat('dropped', item),
        'killed <entity>' -> _(entity) -> init_stat('killed', entity),
        'killed_by <entity>' -> _(entity) -> init_stat('killed_by', entity),
        'custom <event>' -> _(event) -> init_stat('custom', event),
        'grant <player>' -> 'grant'
    },
    'arguments' -> {
        // 'category' -> {
        //         'type' -> 'term',
        //         'options' -> [
        //             'mined', 'crafted', 'used',
        //             'broken', 'picked_up', 'dropped',
        //             'killed', 'killed_by', 'custom'
        //         ]
        // },
        'event' -> {
            'type' -> 'term'
        },
        // 'criterion' -> {
        //     'type' -> 'criterion'
        // },
        'block' -> {
            'type' -> 'term',
            'options' -> block_list()
        },
        'item' -> {
            'type' -> 'term',
            'options' -> item_list()
        },
        'entity' -> {
            'type' -> 'term',
            'options' -> entity_types('*')
        },
        'player' -> {
            'type' -> 'players'
        }
        // 'block_test' -> {
        //     'type' -> 'block'
        // },
        // 'item_test' -> {
        //     'type' -> 'item'
        // },
        // 'entity_test' -> {
        //     'type' -> 'entity_type'
        // }
   }
};