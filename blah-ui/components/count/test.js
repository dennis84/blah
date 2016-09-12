var test = require('tape')
var diff = require('./diff')

var noop = function() {}

test('diff count', function(assert) {
  var vnode = diff({diff: 42}, noop, {})
  assert.equal('+42', vnode.children[0].children[0].text)

  vnode = diff({diff: -100}, noop, {})
  assert.equal('-100', vnode.children[0].children[0].text)

  vnode = diff({diff: 0}, noop, {})
  assert.equal('0', vnode.children[0].children[0].text)

  assert.end()
})

test('diff percentage', function(assert) {
  var vnode = diff({diff: 42}, noop, {percentage: true})
  assert.equal('42.00%', vnode.children[0].children[0].text)

  vnode = diff({diff: 1 / 0}, noop, {percentage: true})
  assert.equal('No data', vnode.children[0].children[0].children[1].text)

  assert.end()
})
