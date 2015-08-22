import {h} from 'virtual-dom'
import component from './component'
import pageviewWidget from './pageviews/widget'

function render(conn, model) {
  return h('div.dashboard', [
    h('h1', 'Dashboard ' + model.count),
    h('div.widgets', [
      component(pageviewWidget, conn)
    ])
  ])
}

export default render
