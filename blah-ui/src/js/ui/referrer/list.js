import {h} from 'virtual-dom'
import {mount} from '../../hook'
import {search} from './ctrl'

function mkReferrers(xs, update) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.list.referrers', xs.map(referrer => {
    return h('div.list-item.level.is-bordered', [
      h('div.level-left', [
        h('span', referrer.url)
      ]),
      h('div.level-right', [
        h('span.tag.is-primary', String(referrer.count))
      ])
    ])
  }))
}

function render(model, update, conn, options = {}) {
  return h('div.widget.is-borderless.widget-referrer-list', {
    className: options.className,
    mount: mount(node => {
      conn.on('referrer', data => update(search, options))
      update(search, options)
    })
  }, [
    h('div.is-bordered', h('h3', options.title)),
    mkReferrers(model.referrers, update)
  ])
}

export default render
