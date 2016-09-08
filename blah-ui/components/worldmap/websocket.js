var EventEmitter = require('events').EventEmitter

function parse(text) {
  var res = text.match(/^([a-z-]+)@(.*)$/)
  var data = JSON.parse(res[2])
  return {event: res[1], data: data}
}

module.exports = function(ws) {
  var emitter = new EventEmitter
  ws.onopen = function() {
    emitter.emit('opened', emitter)
  }

  ws.onclose = function() {
    emitter.emit('closed', emitter)
  }

  ws.onmessage = function(e) {
    try {
      var res = parse(e.data)
      emitter.emit(res.event, res.data)
    } catch(ex) {}
  }

  return emitter
}
