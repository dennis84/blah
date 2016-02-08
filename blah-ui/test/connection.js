import test from 'tape'
import connect from '../src/js/connection'

if(typeof global !== 'undefined') {
  global.WebSocket = WebSocket
  function WebSocket(url) {
    this.url = url
  }
}

test('connection API', (assert) => {
  assert.plan(3)

  var conn = connect('url')
  conn.on('opened', (c) => assert.pass())
  conn.ws.onopen()

  conn.on('closed', (c) => assert.pass())
  conn.ws.onclose()

  conn.on('type', (data) => {
    assert.deepEqual({foo: 'bar'}, data)
  })

  conn.ws.onmessage({data: 'type@{"foo":"bar"}'})

  assert.end()
})

test('invalid message format', (assert) => {
  assert.plan(0)

  var conn = connect('url')
  conn.on('type', (data) => {
    assert.fail('This must not happen')
  })

  conn.ws.onmessage({data: 'type#foo: bar'})
  assert.end()
})
