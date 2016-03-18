import 'babel-polyfill'
import virtualDom from 'virtual-dom'
import mainLoop from 'main-loop'
import {SERVING_WS_URL} from './config'
import connect from './connection'
import * as ctrl from './ctrl'
import pageviews from './ui/pageviews'
import user from './ui/user'
import misc from './ui/misc'
import segmentation from './ui/segmentation'

var conn = connect(SERVING_WS_URL)
var model = {path: location.hash}

var loop = mainLoop(model, render.bind(null, update, conn), virtualDom)
document.body.appendChild(loop.target)

function update(action, ...args) {
  return loop.update(ctrl[action](model, ...args))
}

function render() {
  if('#/pageviews' === model.path) return pageviews(...arguments)
  else if('#/user' === model.path) return user(...arguments)
  else if('#/misc' === model.path) return misc(...arguments)
  else if('#/segmentation' === model.path) return segmentation(...arguments)
  else return pageviews(...arguments)
}

window.addEventListener('hashchange', () => update('path', location.hash))
