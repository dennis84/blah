import {h} from 'virtual-dom'
import {mount} from '../../hook'
import {find} from './ctrl'

function mkItems(xs, update) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.list.most-viewed', xs.map(item => {
    return h('div.list-item.level', [
      h('div.level-left', [
        h('strong', `#${item.pos + 1} - ${item.item}`)
      ]),
      h('div.level-right', [
        h('span.tag.is-primary', String(item.count))
      ])
    ])
  }))
}

function render(model, update, conn, options = {}) {
  var items = model.items ? model.items.slice(0, options.limit) : []
  return h('div.widget.widget-most-viewed', {
    className: options.className,
    mount: mount(node => update(find, options))
  }, [
    h('h3', options.title),
    mkItems(items, update)
  ])
}

export default render
