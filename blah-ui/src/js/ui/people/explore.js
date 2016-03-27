import {h} from 'virtual-dom'
import {mount} from '../../hook'
import {search} from './ctrl'
import debounce from 'debounce'

function views(xs) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.list', xs.map((user) => {
    return h('div.list-item.clearfix', [
      h('span', user.user),
    ])
  }))
}

function render(model, update, options = {}) {
  return h('div.explore', [
    h('div.control', h('input.input.is-large', {
      placeholder: 'Enter username',
      oninput: debounce((e) => {
        if(!e.target.value) return
        update(search, {filterBy: [
          {prop: 'user', operator: 'contains', value: e.target.value}
        ]})
      }, 500)
    })),
    views(model.users)
  ])
}

export default render
