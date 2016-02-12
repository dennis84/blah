import test from 'tape'
import diff from '../../../src/js/ui/common/diff'

test('diff count', assert => {
  var node = diff(42)
  assert.equal('+42', node.children[0].text)

  var node = diff(-100)
  assert.equal('-100', node.children[0].text)

  var node = diff(0)
  assert.equal('±0', node.children[0].text)

  assert.end()
})

test('diff percentage', assert => {
  var node = diff(42, true)
  assert.equal('42.00%', node.children[0].text)

  var node = diff(1 / 0, true)
  assert.equal('?', node.children[0].text)

  assert.end()
})
