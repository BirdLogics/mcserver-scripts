fkadv(target, name, msg) -> (
    logger(player()~'name' + ' gave [' + name + '] to ' + target + ': ' + msg);
    print(player('all'), format('w ' + target + ' has made the advancement ',
        'l [' + name + ']', '^w ' + msg, 'w *', '^wi Message by ' + player()~'name'))
);

__config() -> {
    'scope' -> 'global',
    'commands' -> {
        '<to> <name> <msg>' -> 'fkadv'
    },
    'arguments' -> {
        'to' -> {
            'type' -> 'term',
            'suggester' -> _(args) -> (
                nameset = {};
                for(player('all'), nameset += _);
                keys(nameset)
            ),
        },
        'name' -> {
            'type' -> 'string',
            'suggest' -> ['Toast', '"Aha!"', '"Bad Humor"']
            },
        'msg' -> {
            'type' -> 'string',
            'suggest' -> ['"Throw bread into lava."', '"Use a spyglass."', '"Make a joke that\'s so bad it\'s good"']
            }
   }
};
