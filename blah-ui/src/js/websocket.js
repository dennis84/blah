import events from 'events'

function listen(url) {
  var ws = new WebSocket(url)
  var emitter = new events.EventEmitter
  emitter.setMaxListeners(0)

  ws.onopen = () => {
    emitter.emit('opened')
  }

  ws.onclose = () => {
    emitter.emit('closed')
  }

  ws.onmessage = (e) => {
    try {
      var res = parse(e.data)
      emitter.emit(res.event, res.data)
    } catch(e) {}
  }

  return emitter
}

function parse(text) {
  var res = text.match(/^([a-z-]+)@(.*)$/)
  var data = JSON.parse(res[2])
  return {event: res[1], data: data}
}

export default listen
