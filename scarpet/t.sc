title_chat(msg) -> (
    display_title(player('all'), 'actionbar', format(['y <' + player()~'name' + '> ', 'w ' + msg]), 0, 20*5, 20);

);

// __on_player_connects(p) -> (
//     display_title(player('all'), 'title', format('y ' + p~'name' + ' joined the game'), 20, 20, 20);
// );

// __on_player_disconnects(p, r) -> (
//     display_title(player('all'), 'title', format('y ' + p~'name' + ' left the game'), 20, 20, 20);
// );

__config() -> {
    'commands' -> {
        '<msg>' -> 'title_chat'
    },
    'arguments' -> {
      'msg' -> { 'type' -> 'text'}
   }
};
