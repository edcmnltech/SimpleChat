## Updates
### 2020-04-22
- 'Channel' is embedded into an Actor's Props
- 'ChannelGroup' is embedded into an Actor's Props

## Notes
- Remember to wrap 'message' with TextWebFramewSocket before firing 'writeAndFlush' to a Channel or ChannelGroup
- To check if 'writeAndFlush' is successful, set the result of 'writeAndFlush' to a val and check if the Future is success
