import 'babel-polyfill'
import virtualDom from 'virtual-dom'
import main from './main'
import {SERVING_WS_URL} from './config'
import connect from './connection'
import * as ctrl from './ctrl'
import pageviews from './ui/pageviews'
import user from './ui/user'
import misc from './ui/misc'
import people from './ui/people'
import segmentation from './ui/segmentation'

var conn = connect(SERVING_WS_URL)
var model = {path: location.hash}
var renderFn = render.bind(null, update, conn)
var loop = main(model, renderFn, document.body)

function update(action, ...args) {
  return loop.update(ctrl[action](model, ...args))
}

function render() {
  if('#/pageviews' === model.path) return pageviews(...arguments)
  else if('#/user' === model.path) return user(...arguments)
  else if('#/misc' === model.path) return misc(...arguments)
  else if('#/people' === model.path) return people(...arguments)
  else if('#/segmentation' === model.path) return segmentation(...arguments)
  else return pageviews(...arguments)
}

window.addEventListener('hashchange', () => {
  return loop.update(ctrl.path(model, location.hash), true)
})
