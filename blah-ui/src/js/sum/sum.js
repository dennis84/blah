import {h} from 'virtual-dom'
import {sum} from './ctrl'
import {mount} from '../hook'

function content(model, options) {
  if(undefined === model.sum) return
  var sum = String(model.sum.toFixed(2))
  var value = sum

  if(undefined !== options.template) {
    value = new Function('sum', 'return `'+options.template+'`;')(sum)
  }

  return h('div', [
    h('div.sum', value),
    h('div.title', options.title)
  ])
}

function render(model, update, conn, options) {
  return h('div.widget.widget-sum.center-hv', {
    className: options.className,
    mount: mount((node) => {
      conn.on('count', (data) => update(sum, options))
      update(sum, options)
    })
  }, content(model, options))
}

export default render
