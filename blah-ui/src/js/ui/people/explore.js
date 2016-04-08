import {h} from 'virtual-dom'
import moment from 'moment'
import {mount} from '../../hook'
import {search} from './ctrl'
import debounce from 'debounce'

function views(xs) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.list', xs.map((user) => {
    return h('div.list-item.clearfix', [
      h('div', user.user),
      h('h3', 'Activity'),
      h('div', user.events.map((event) => {
        return h('div', [
          h('b', moment(event.date).format('MMMM Do YYYY, hh:mm:ss a')),
          h('span', event.item),
          h('span', event.title)
        ])
      }))
    ])
  }))
}

function render(model, update, options = {}) {
  return h('div.explore', {
    mount: mount((node) => update(search))
  }, [
    h('div.control', [
      h('input.input.is-large', {
        oninput: debounce((e) => {
          if(!e.target.value) {
            e.target.classList.remove('dirty')
            return
          }

          e.target.classList.add('dirty')
          update(search, {filterBy: [
            {prop: 'user', operator: 'contains', value: e.target.value}
          ]})
        }, 500)
      }),
      h('label', 'Search for users')
    ]),
    views(model.users)
  ])
}

export default render
