import test from 'tape'
import createWidget from '../src/js/widget'
import {h, diff, patch} from 'virtual-dom'
import createElement from 'virtual-dom/create-element'
import virtualDom from 'main-loop'
import mainLoop from 'main-loop'

var noop = () => {}

test('render', (assert) => {
  assert.plan(4)

  function render(model, update) {
    assert.equal(typeof model, "object")
    assert.equal(typeof update, "function")
    return h('div')
  }

  var state = {}

  var widget = createWidget(render, state, noop)
  assert.equal(null, widget.id)

  createElement(widget)
  assert.equal(typeof widget.id, "string")

  assert.end()
})

test('rerender new state', (assert) => {
  assert.plan(2)

  var state = {}
  var widget = null
  var round = 0

  function render(s, c) {
    widget = createWidget((model, update) => {
      if(0 === round) {
        assert.comment('In the first round, the model is empty')
        assert.deepEqual({}, model)
      } else {
        assert.comment('Then it has a new state')
        assert.equal(42, model.count)
      }

      round ++
      return h('div')
    }, s, c)

    return h('div', widget)
  }

  var tree = render(state, noop)
  createElement(tree)
  state[widget.id] = {count: 42}
  diff(tree, render(state, noop))

  assert.end()
})

test('rerender same state', (assert) => {
  assert.plan(1)

  var widget = null
  var state = {}

  function renderWidget(model, update) {
    assert.pass('Widget was rendered')
    return h('div')
  }

  function render(s, c) {
    widget = createWidget(renderWidget, s, c)
    return h('div', widget)
  }

  var tree = render(state, noop)
  createElement(tree)
  diff(tree, render(state, noop))

  assert.end()
})

test('rerender other widget', (assert) => {
  var state = {}

  function renderWidget(model, update, title) {
    return h('div', title)
  }

  function renderA(s, c) {
    return createWidget(renderWidget, s, c, 'A')
  }

  function renderB(s, c) {
    return createWidget(renderWidget, s, c, 'B')
  }

  var tree = renderA(state, noop)
  var node = createElement(tree)
  assert.equal(node.childNodes[0].data, 'A')
  var patches = diff(tree, renderB(state, noop))
  node = patch(node, patches)
  assert.equal(node.childNodes[0].data, 'B')

  assert.end()
})

test('pass custom params', (assert) => {
  assert.plan(4)

  function render(model, update, ...args) {
    assert.equal(3, args.length)
    assert.equal(1, args[0])
    assert.equal('2', args[1])
    assert.deepEqual({foo: 'bar'}, args[2])
    return h('div')
  }

  var state = {}

  var widget = createWidget(render, state, noop, 1, '2', {foo: 'bar'})
  createElement(widget)

  assert.end()
})

test('update', (assert) => {
  assert.plan(5)

  function render(model, update, ...args) {
    update((state, arg1, arg2) => {
      assert.equal('foo', arg1)
      assert.equal('bar', arg2)
      return new Promise((resolve, reject) => resolve({count: 42}))
    }, 'foo', 'bar')
    return h('div')
  }

  var state = {}
  var updateFn = (action, ...args) => {
    assert.equal('widget', action)
    assert.equal(typeof args[0], 'string')
    assert.deepEqual({count: 42}, args[1])
    assert.end()
  }

  var widget = createWidget(render, state, updateFn)
  createElement(widget)
})
