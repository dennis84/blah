var emitter = require('emitter-component')

function WebSocketListener(ws) {
  var that = this

  ws.onopen = function() {
    that.emit('opened', that)
  }

  ws.onclose = function() {
    that.emit('closed', that)
  }

  ws.onmessage = function(e) {
    try {
      var res = parse(e.data)
      that.emit(res.event, res.data)
    } catch(e) {}
  }
}

emitter(WebSocketListener.prototype)

function parse(text) {
  var res = text.match(/^([a-z-]+)@(.*)$/)
  var data = JSON.parse(res[2])
  return {event: res[1], data: data}
}

module.exports = WebSocketListener
