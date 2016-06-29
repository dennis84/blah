import {h} from 'virtual-dom'
import moment from 'moment'
import {mount} from '../../hook'
import {search, open} from './ctrl'
import debounce from 'debounce'

function views(xs, update) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.people-list', xs.map(user => {
    return h('div.card.is-fullwidth', [
      h('header.card-header', [
        h('p.card-header-title', user.user),
        h('a.card-header-icon', {
          onclick: node => update(open, user)
        }, h('i.material-icons', 'expand_more'))
      ]),
      user.opened ? h('div.card-content', [
        h('div.media', [
          h('div.media-left', h('i.material-icons', 'face')),
          h('div.media-content', [
            h('p.title.is-5', user.user),
            h('p.subtitle.is-6', [
              h('a', {href: `mailto:${user.email}` }, user.email)
            ]),
            h('p.subtitle.is-6', `Country: ${user.country}`),
            h('p.subtitle.is-6', `City: ${user.city}`),
            h('p.subtitle.is-6', `Number of events: ${user.nbEvents}`)
          ])
        ]),
        h('div.list', user.events.map(evt => h('div.level.list-item', [
          h('div.level-left', [
            h('div.level-item', h('span.tag', {
              className: `is-collection-${evt.collection}`
            }, moment(evt.date).calendar())),
            evt.item ? h('div.level-item', h('span.tag', evt.item)) : null,
            h('div.level-item', evt.title)
          ])
        ])))
      ]) : null
    ])
  }))
}

function render(model, update, options = {}) {
  return h('div.explore', {
    mount: mount((node) => update(search))
  }, [
    h('div.control', [
      h('input.input.is-large', {
        placeholder: 'Search users ...',
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
      })
    ]),
    views(model.users, update)
  ])
}

export default render
