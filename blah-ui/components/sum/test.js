var test = require('tape')
var render = require('./render')

test('empty widget', function(assert) {
  var node = render({}, undefined, {
    className: 'test'
  })

  assert.ok(node.data.props.className === 'test')
  assert.equal(0, node.children.length)
  assert.end()
})

test('title and template options', function(assert) {
  var node = render({sum: 42}, undefined, {
    template: '$ {value}',
    title: 'Total'
  })

  var sumNode = node.children[0].children[0]
  assert.equal('$ 42.00', sumNode.text)
  
  var titleNode = node.children[0].children[1]
  assert.equal('Total', titleNode.text)

  assert.end()
})

test('no template', function(assert) {
  var node = render({sum: 42}, undefined, {
    title: 'Total'
  })

  var sumNode = node.children[0].children[0]
  assert.equal('42.00', sumNode.text)

  assert.end()
})
