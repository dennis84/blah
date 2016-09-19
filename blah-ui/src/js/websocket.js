var events = require('events')

function listen(ws) {
  if(typeof ws !== 'object') {
    ws = new WebSocket(ws)
  }

  var emitter = new events.EventEmitter
  emitter.setMaxListeners(0)

  ws.onopen = function() {
    emitter.emit('opened')
  }

  ws.onclose = function() {
    emitter.emit('closed')
  }

  ws.onmessage = function(e) {
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

module.exports = listen
