import test from 'tape'
import bindAppend from '../src/js/bind-append'

test('bind append', (assert) => {
  assert.plan(2)
  var fn = (...args) => args

  var append = bindAppend(fn, null, 1, 2)
  assert.deepEqual(append(3), [3, 1, 2])

  var prepend = fn.bind(null, 1, 2)
  assert.deepEqual(prepend(3), [1, 2, 3])

  assert.end()
})
