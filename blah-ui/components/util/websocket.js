var emitter = require('emitter-component')

function Connection(ws) {
  this.ws = ws
}

emitter(Connection.prototype)

function connect(url) {
  var ws = new WebSocket(url)
  var conn = new Connection(ws)

  ws.onopen = function() {
    conn.emit('opened', conn)
  }

  ws.onclose = function() {
    conn.emit('closed', conn)
  }

  ws.onmessage = function(e) {
    try {
      var res = parse(e.data)
      conn.emit(res.event, res.data)
    } catch(e) {}
  }

  return conn
}

function parse(text) {
  var res = text.match(/^([a-z-]+)@(.*)$/)
  var data = JSON.parse(res[2])
  return {event: res[1], data: data}
}

module.exports = connect
