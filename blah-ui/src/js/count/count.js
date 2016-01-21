import {h} from 'virtual-dom'
import {count, incr} from './ctrl'
import {mount} from '../hook'

function content(model, options) {
  if(undefined === model.count) return
  return h('div', [
    h('div.count', String(model.count)),
    h('div.item', options.title)
  ])
}

function render(model, update, conn, options) {
  return h('div.widget.widget-count.center-hv', {
    className: options.className,
    mount: mount((node) => {
      conn.on('count', (data) => update(count, options))
      update(count, options)
    })
  }, content(model, options))
}

export default render
