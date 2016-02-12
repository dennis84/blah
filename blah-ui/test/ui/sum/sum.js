import test from 'tape'
import sum from '../../../src/js/ui/sum/sum'

test('empty widget', (assert) => {
  var node = sum({}, undefined, undefined, {
    className: 'test'
  })

  assert.equal('DIV', node.tagName)
  assert.ok(node.properties.className.indexOf('test'))
  assert.equal(0, node.children.length)
  assert.end()
})

test('title and template options', (assert) => {
  var node = sum({sum: 42}, undefined, undefined, {
    template: '$ {value}',
    title: 'Total'
  })

  var sumNode = node.children[0].children[0]
  assert.equal('value', sumNode.properties.className)
  assert.equal('$ 42.00', sumNode.children[0].text)
  
  var titleNode = node.children[0].children[1]
  assert.equal('title', titleNode.properties.className)
  assert.equal('Total', titleNode.children[0].text)

  assert.end()
})

test('no template', (assert) => {
  var node = sum({sum: 42}, undefined, undefined, {
    title: 'Total'
  })

  var sumNode = node.children[0].children[0]
  assert.equal('value', sumNode.properties.className)
  assert.equal('42.00', sumNode.children[0].text)

  assert.end()
})
