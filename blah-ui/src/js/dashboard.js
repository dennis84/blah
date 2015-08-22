import {h} from 'virtual-dom'
import component from './component'
import pageviewWidget from './pageviews/widget'

function render(conn, text) {
  return h('div.dashboard', [
    h('h1', 'Dashboard ' + text),
    h('div.widgets', [
      component(pageviewWidget, conn)
    ])
  ])
}

export default render
