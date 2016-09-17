import test from 'tape'
import listen from '../src/js/websocket'

function WebSocketMock() {}

test('connection API', (assert) => {
  assert.plan(3)

  var ws = new WebSocketMock
  var emitter = listen(ws)
  emitter.on('opened', (c) => assert.pass())
  ws.onopen()

  emitter.on('closed', (c) => assert.pass())
  ws.onclose()

  emitter.on('type', (data) => {
    assert.deepEqual({foo: 'bar'}, data)
  })

  ws.onmessage({data: 'type@{"foo":"bar"}'})

  assert.end()
})

test('invalid message format', (assert) => {
  assert.plan(0)

  var ws = new WebSocketMock
  var emitter = listen(ws)
  emitter.on('type', (data) => {
    assert.fail('This must not happen')
  })

  ws.onmessage({data: 'type#foo: bar'})
  assert.end()
})
