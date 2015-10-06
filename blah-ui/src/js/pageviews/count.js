import {h} from 'virtual-dom'
import {count, incr} from './ctrl'
import {mount} from '../hook'

function content(model, options) {
  if(undefined === model.count) return
  return h('div', [
    h('div.count', String(model.count)),
    h('div.page', options.title)
  ])
}

function render(model, update, conn, options) {
  return h('div.widget.widget-count.center-hv', {
    className: options.className,
    mount: mount((node) => {
      conn.on('count', (data) => update(count, options))
      update(count, options)
    }),
    onclick: (e) => update(incr)
  }, content(model, options))
}

export default render
