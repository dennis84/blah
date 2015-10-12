import {h} from 'virtual-dom'
import {count} from './ctrl'
import {mount} from '../hook'

function content(model, options) {
  if(undefined === model.users) return
  return h('div', [
    h('div.count', String(model.users.length)),
    h('div.title', options.title)
  ])
}

function render(model, update, conn, options) {
  return h('div.widget.widget-users.center-hv', {
    className: options.className,
    mount: mount((node) => update(count))
  }, content(model, options))
}

export default render
