import test from 'tape'
import qs from '../src/js/querystring'

test("query string", (assert) => {
  assert.equal('', qs(''))
  assert.equal('', qs(true))
  assert.equal('', qs([]))
  assert.equal('', qs(1))
  assert.equal('foo=1&bar=2', qs({foo: 1, bar: 2}))
  assert.end()
})
