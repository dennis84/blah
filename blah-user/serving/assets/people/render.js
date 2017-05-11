var h = require('snabbdom/h').default
var debounce = require('debounce')
var distanceInWordsToNow = require('date-fns/distance_in_words_to_now')
var ctrl = require('./ctrl')

function views(xs, update) {
  var collections = []
  return h('div.list', xs.map(function(user) {
    return h('div.card.is-fullwidth', [
      h('header.card-header', [
        h('p.card-header-title', user.user),
        h('a.card-header-icon', {
          on: {click: function() {
            update(ctrl.open, user)
          }}
        }, [h('i.material-icons', 'expand_more')])
      ]),
      user.opened ? h('div.card-content', [
        h('div.media', [
          h('div.media-left', [h('i.material-icons', 'face')]),
          h('div.media-content', [
            h('p.title.is-5', user.user),
            h('p.subtitle.is-6', [
              h('a', {href: 'mailto:' + user.email}, user.email)
            ]),
            h('p.subtitle.is-6', 'Country: ' + user.country),
            h('p.subtitle.is-6', 'City: ' + user.city),
            h('p.subtitle.is-6', 'Number of events: ' + user.nbEvents)
          ])
        ]),
        h('div.list', user.events.map(function(evt) {
          var color = collections.indexOf(evt.collection) + 1
          if(color === 0) {
            color = collections.push(evt.collection)
          }

          var classAttrs = {}
          classAttrs['is-color-' + color] = true
          return h('div.level.list-item', [
            h('div.level-left', [
              h('div.level-item', [h('span.tag', {
                class: classAttrs
              }, distanceInWordsToNow(evt.date))]),
              evt.item ? h('div.level-item', [h('span.tag', evt.item)]) : '',
              h('div.level-item', evt.title)
            ])
          ])
        }))
      ]) : ''
    ])
  }))
}

function render(model, update, options) {
  return h('div.people', [
    h('h1.title.is-1.has-text-centered', 'Explore People'),
    h('div.field', [
      h('p.control', [
        h('input.input.is-large', {
          props: {
            placeholder: 'Search users ...',
          },
          on: {
            input: debounce(function(e) {
              if(!e.target.value) {
                e.target.classList.remove('dirty')
                return
              }

              e.target.classList.add('dirty')
              update(ctrl.search, {
                baseUrl: options.baseUrl,
                filterBy: [
                  {prop: 'user', operator: 'contains', value: e.target.value}
                ]
              })
            }, 500)
          }
        })
      ])
    ]),
    views(model.users || [], update)
  ])
}

module.exports = render
