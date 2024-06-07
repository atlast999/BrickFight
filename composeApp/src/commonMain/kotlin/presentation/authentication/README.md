One-time event problem:

- ViewModel uses Channel to send a event and exposes a Flow of event
- View consumes the event by collecting the Flow and handle that event accordingly
- The view receives the event and schedules to handle it (may take some time)
- During that time, the view is destroyed, causing the event is received but is not handled
  -> The event is lost forever. [ref](https://github.com/Kotlin/kotlinx.coroutines/issues/2886)

*`Solution`: reduce event to state --> Eh