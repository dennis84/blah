var h = require('snabbdom/h').default
var distanceInWordsToNow = require('date-fns/distance_in_words_to_now')
var ctrl = require('./ctrl')

function button(job, update, options) {
  if('queued' === job.status || job.clicked) {
    return h('a.button.is-loading.is-medium', 'Loa')
  }

  if(-1 !== job.status.indexOf('running')) {
    return h('a.button.is-medium', {
      on: {
        click: function() {
          update(ctrl.stop, job.name, options)
        }
      }
    }, 'Stop')
  }

  return h('a.button.is-danger.is-medium', {
    on: {
      click: function() {
        update(ctrl.run, job.name, options)
      }
    }
  }, 'Run')
}

function render(model, update, options) {
  var jobs = model.jobs || []
  return h('div.jobs', [
    h('h1.title.is-1.has-text-centered', 'Run your Chronos Jobs'),
    h('div', jobs.map(function(job) {
      var lastSuccess = job.lastSuccess
      if(lastSuccess) lastSuccess = distanceInWordsToNow(lastSuccess)
      return h('div.card.is-fullwidth', [
        h('header.card-header', [
          h('p.card-header-title', job.name),
          h('p.last-success', h('i', 'Last success: ' + (lastSuccess || '-'))),
          button(job, update, options)
        ])
      ])
    }))
  ])
}

module.exports = render
