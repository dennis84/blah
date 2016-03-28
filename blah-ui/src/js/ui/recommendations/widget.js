import {h} from 'virtual-dom'
import {mount} from '../../hook'
import {recommendations} from './ctrl'
import debounce from 'debounce'

function views(xs) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.recommendations', xs.map((item) => {
    return h('div.recommendation.clearfix', [
      h('span', item.item),
      h('span.label.label-red.pull-right', String(parseFloat(item.score).toFixed(2)))
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
    h('div.control', [
      h('input.input', {
        value: options.user,
        oninput: debounce((e) => {
          if(!e.target.value) {
            e.target.classList.remove('dirty')
            return
          }

          update(recommendations, {user: e.target.value})
          e.target.classList.add('dirty')
        }, 500)
      }),
      h('label', 'Enter username')
    ]),
    views(model.views)
  ])
}

export default render
