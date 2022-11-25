init_stat(category, event) -> (
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

__on_player_disconnects(target, _) -> (
    // Clear the leaderboard for the player
    // Bedrock shows the full leaderboard before with the player list
    scoreboard('scarpet_stat', target, null);
);

__config() -> {
    'scope' -> 'global',
    'command_permission' -> 'ops',
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