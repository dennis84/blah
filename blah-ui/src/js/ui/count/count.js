import {h} from 'virtual-dom'
import {count} from './ctrl'
import {mount} from '../../hook'

function content(model, options) {
  if(undefined === model.count) return
  return h('div', [
    h('div.widget-value', String(model.count)),
    h('div.widget-title', options.title)
  ])
}

function render(model, update, conn, options) {
  return h('div.widget.widget-count.has-centered-hv', {
    className: options.className,
    mount: mount(node => {
      conn.on('count', data => update(count, options))
      update(count, options)
    })
  }, content(model, options))
}

export default render
