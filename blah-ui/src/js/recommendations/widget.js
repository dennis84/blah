import {h} from 'virtual-dom'
import {mount} from '../hook'
import {recommendations} from './ctrl'
import debounce from 'debounce'

function views(xs) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.recommendations', xs.map((item) => {
    return h('div', [
      h('span', item.item),
      h('span.label.label-danger.pull-right', String(parseFloat(item.score).toFixed(2)))
    ])
  }))
}

function render(model, update, options = {}) {
  return h('div.widget.widget-recommendations', {
    mount: mount((node) => {
      if(options.user) update(recommendations, options)
    })
  }, [
    h('h3', 'Recommendations'),
    h('input.form-control.input-lg', {
      value: options.user,
      placeholder: 'Enter username',
      oninput: debounce((e) => {
        if(!e.target.value) return
        update(recommendations, {user: e.target.value})
      }, 500)
    }),
    views(model.views)
  ])
}

export default render
