import {h} from 'virtual-dom'
import {count, incr} from './ctrl'
import hook from '../hook'

function content(model, options) {
  if(undefined === model.count) return
  return h('div', [
    h('div.count', String(model.count)),
    h('div.page', options.title)
  ])
}

function render(model, update, id, options) {
  return h('div.widget.widget-count.center-hv', {
    className: options.className,
    init: hook((node) => {
      if(null === id) update(count, options)
    }),
    onclick: (e) => update(incr)
  }, content(model, options))
}

export default render
