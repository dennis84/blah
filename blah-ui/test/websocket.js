var test = require('tape')
var listen = require('../src/js/websocket')

function WebSocketMock() {}

test('connection API', function(assert) {
  assert.plan(3)

  var ws = new WebSocketMock
  var emitter = listen(ws)
  emitter.on('opened', function(c) {
    assert.pass()
  })
  ws.onopen()

  emitter.on('closed', function(c) {
    assert.pass()
  })
  ws.onclose()

  emitter.on('type', function(data) {
    assert.deepEqual({foo: 'bar'}, data)
  })

  ws.onmessage({data: 'type@{"foo":"bar"}'})

  assert.end()
})

test('invalid message format', function(assert) {
  assert.plan(0)

  var ws = new WebSocketMock
  var emitter = listen(ws)
  emitter.on('type', function(data) {
    assert.fail('This must not happen')
  })

  ws.onmessage({data: 'type#foo: bar'})
  assert.end()
})
