import {h} from 'virtual-dom'
import moment from 'moment'
import {mount} from '../../hook'
import {list, run, stop} from './ctrl'
import debounce from 'debounce'

function jobs(xs, update) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.people-list', xs.map(job => {
    return h('div.card.is-fullwidth', [
      h('header.card-header', [
        h('p.card-header-title', job.name),
        h('i', `Last success: ${job.lastSuccess || '-'}`),
        job.running ? h('a.button.is-danger', {
          onclick: e => update(stop, job.name)
        }, 'Stop') : h('a.button.is-primary', {
          onclick: e => update(run, job.name)
        }, 'Run')
      ])
    ])
  }))
}

function render(model, update, options = {}) {
  return h('div.jobs', {
    mount: mount((node) => update(list))
  }, jobs(model.jobs, update))
}

export default render
