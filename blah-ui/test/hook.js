import test from 'tape'
import {h, diff, patch} from 'virtual-dom'
import createElement from 'virtual-dom/create-element'
import {hook, mount} from '../src/js/hook'

test('hook', (assert) => {
  assert.plan(2)

  function render() {
    return h('div', {
      hook: hook((node) => {
        assert.equal('DIV', node.tagName)
      })
    })
  }

  var tree = render()
  var node = createElement(tree)
  patch(node, diff(tree, render()))

  assert.end()
})

test('mount hook', (assert) => {
  assert.plan(1)

  function render() {
    return h('div', {
      hook: mount((node) => {
        assert.equal('DIV', node.tagName)
      })
    })
  }

  var tree = render()
  var node = createElement(tree)
  patch(node, diff(tree, render()))

  assert.end()
})
