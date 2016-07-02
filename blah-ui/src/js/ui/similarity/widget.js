import {h} from 'virtual-dom'
import {mount} from '../../hook'
import {find} from './ctrl'
import debounce from 'debounce'

function mkSimilarities(xs) {
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
  return h('div.widget.is-borderless.widget-similarity', {
    mount: mount(node => {
      if(options.item) update(find, options)
    })
  }, [
    h('div.is-bordered', [
      h('h3', 'Similarity'),
      h('div.control', [
        h('input.input', {
          placeholder: 'Enter item',
          value: options.item,
          oninput: debounce(e => {
            if(!e.target.value) return
            var items = e.target.value.split(",")
            update(find, {items: items})
          }, 500)
        })
      ])
    ]),
    mkSimilarities(model.similarities)
  ])
}

export default render
