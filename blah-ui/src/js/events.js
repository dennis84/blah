var events = require('events')

function listen(url) {
  var source = new EventSource(url + "/events")
  var emitter = new events.EventEmitter
  emitter.setMaxListeners(0)

  source.onmessage = function(e) {
    try {
      var res = parse(e.data)
      emitter.emit(res.event, res.data)
    } catch(e) {}
  }

  return emitter
}

function parse(text) {
  var res = text.match(/^([a-z-_]+)@(.*)$/)
  var data = JSON.parse(res[2])
  return {event: res[1], data: data}
}

module.exports = listen
