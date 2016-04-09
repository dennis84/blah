import {h} from 'virtual-dom'
import {sum} from './ctrl'
import {mount} from '../../hook'

function content(model, options) {
  if(undefined === model.sum) return
  var value = options.template ?
    options.template.replace('{value}', model.sum.toFixed(2)) :
    String(model.sum.toFixed(2))

  return h('div', [
    h('div.widget-value', value),
    h('div.widget-title', options.title)
  ])
}

function render(model, update, conn, options) {
  return h('div.widget.widget-sum.is-centered-hv', {
    className: options.className,
    mount: mount(node => {
      conn.on('count', data => update(sum, options))
      update(sum, options)
    })
  }, content(model, options))
}

export default render
