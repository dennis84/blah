import emitter from 'emitter-component'

function Connection(ws) {
  this.ws = ws
}

emitter(Connection.prototype)

function connect(url) {
  var ws = new WebSocket(url)
  var conn = new Connection(ws)

  ws.onopen = () => {
    conn.emit('opened', conn)
  }

  ws.onclose = () => {
    conn.emit('closed', conn)
  }

  ws.onmessage = (e) => {
    var res = parse(e.data)
    conn.emit(res.event, res.data)
  }

  return conn
}

function parse(text) {
  var res = text.match(/^([a-z-]+)@(.*)$/)
    , data = null

  try {
    var data = JSON.parse(res[2])
  } catch(e) {}

  return {event: res[1], data: data}
}

export default connect
