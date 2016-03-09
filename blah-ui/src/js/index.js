import 'babel-polyfill'
import {diff, patch} from 'virtual-dom'
import createElement from 'virtual-dom/create-element'
import csp from 'js-csp'
import {SERVING_WS_URL} from './config'
import connect from './connection'
import * as ctrl from './ctrl'
import pageviews from './ui/pageviews'
import user from './ui/user'
import misc from './ui/misc'

var conn = connect(SERVING_WS_URL)
var chan = csp.chan()
var model = {path: location.hash}

function update(model, action) {
  return ctrl[action.type].apply(null, [model].concat(action.args))
}

function renderLoop(render) {
  var tree = render(model, chan, conn)
  var node = createElement(tree)
  document.body.appendChild(node)

  csp.go(function*() {
    while(true) {
      var action = yield csp.take(chan)
      model = update(model, action)
      var updated = render(model, chan, conn)
      var patches = diff(tree, updated)
      node = patch(node, patches)
      tree = updated
    }
  })
}

function render() {
  if('#/pageviews' === model.path) return pageviews(...arguments)
  else if('#/user' === model.path) return user(...arguments)
  else if('#/misc' === model.path) return misc(...arguments)
  else return pageviews(...arguments)
}

window.addEventListener('hashchange', () => {
  csp.putAsync(chan, {type: 'path', args: [location.hash]})
})

renderLoop(render)
