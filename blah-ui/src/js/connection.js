var emitter = require('emitter-component')
  , parse = require('./parse')

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
    var res = parse(e.data)
    conn.emit(res.event, res.data)
  }

  return conn
}

module.exports = connect
