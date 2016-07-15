import {h} from 'virtual-dom'
import {mount} from '../../hook'
import {find} from './ctrl'
import debounce from 'debounce'

function mkItems(xs) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.list', xs.map(item => {
    return h('div.list-item.level.is-bordered', [
      h('div.level-left', h('span', item.item)),
      h('div.level-right', h('span.is-pulled-right.tag.is-danger',
        String(parseFloat(item.score).toFixed(2))))
    ])
  }))
}

function render(model, update, options = {}) {
  return h('div.widget.is-borderless.widget-recommendation', {
    mount: mount(node => {
      if(options.user) update(find, options)
    })
  }, [
    h('div.is-bordered', [
      h('h3', 'Recommendations'),
      h('div.control', [
        h('input.input', {
          placeholder: 'Enter username',
          value: options.user,
          oninput: debounce(e => {
            if(!e.target.value) return
            update(find, {user: e.target.value})
          }, 500)
        })
      ])
    ]),
    mkItems(model.items)
  ])
}

export default render
