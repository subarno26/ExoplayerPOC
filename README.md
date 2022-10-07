# ExoplayerPOC
Objectives: 
Exo Player - 
1- landscape and portrait mode. 
2- playback should pause in the background and resume in the foreground. 
3 - HLS and DASH 
4- play/pause/forward/rewind

Methodology:
1. Implemented exoplayer with support for landscape as well as portrait mode. State is preserved when orientation is changed.
2. Implemented all lifecycle callbacks which preserve memory and release resources when not required.
3. Added support for playing videos with HLS and DASH.
4. The above functionality with play/pause/forward/rewind works as expected.

