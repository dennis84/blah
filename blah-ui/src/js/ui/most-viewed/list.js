import {h} from 'virtual-dom'
import {mount} from '../../hook'
import {find} from './ctrl'

function mkItems(xs, update) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.list.most-viewed', xs.map((item, index) => {
    return h('div.list-item.level.is-bordered', [
      h('div.level-left', [
        h('span', `#${index + 1} - ${item.item}`)
      ]),
      h('div.level-right', [
        h('span.tag.is-primary', String(item.count))
      ])
    ])
  }))
}

function render(model, update, conn, options = {}) {
  return h('div.widget.is-borderless.widget-most-viewed', {
    className: options.className,
    mount: mount(node => {
      conn.on('most-viewed', data => update(find, options))
      update(find, options)
    })
  }, [
    h('div.is-bordered', h('h3', options.title)),
    mkItems(model.items, update)
  ])
}

export default render
