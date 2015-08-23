import {h} from 'virtual-dom'
import component from './component'
import PageviewWidget from './pageviews/widget'

function render(conn, model) {
  return h('div.dashboard', [
    h('h1', 'Dashboard ' + model.count),
    h('div.widgets', [
      new PageviewWidget(conn)
    ])
  ])
}

export default render
