## Updates
##### 2020-04-22
- `Channel` is embedded into an Actor's Props
- `ChannelGroup` is embedded into an Actor's Props
##### 2020-04-29
- Unused `ChannelGroup` and opted to use simple `Map[ActorRef]` for tracking members of a `ChatRoom`
- `ConnectionActor` is used as output channel of the `UserActor` 
- Added `UserToConnector`, a function value parameter when creating a `ConnectionActor`

## Notes
- Remember to wrap 'message' with TextWebFramewSocket before firing 'writeAndFlush' to a Channel or ChannelGroup
- To check if 'writeAndFlush' is successful, set the result of 'writeAndFlush' to a val and check if the Future is success
- On using ActorSelection (Classic): https://doc.akka.io/docs/akka/current/actors.html#actorselection
```
It is always preferable to communicate with other Actors using their ActorRef instead of relying upon ActorSelection. 
Exceptions are:
  - sending messages using the At-Least-Once Delivery facility
  - initiating first contact with a remote system
In all other cases ActorRefs can be provided during Actor creation or initialization, passing them from parent to child or introducing Actors by sending their ActorRefs to other Actors within messages.
```

## References
##### 2020-04-29
_Good Actor Design:_ https://www.oreilly.com/library/view/applied-akka-patterns/9781491934876/ch04.html