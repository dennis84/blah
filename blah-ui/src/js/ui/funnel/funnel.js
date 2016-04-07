import {h} from 'virtual-dom'
import {search} from './ctrl'
import {hook, mount} from '../../hook'
import bar from '../chart/bar'

function content(model, options) {
  if(undefined === model.steps || 0 === model.steps.length) return
  var data = model.steps
    .sort((a, b) => a.path.length > b.path.length)
    .map(x => {
      return {key: x.path.join(' > '), value: x.count}
    })

  return h('div.chart', {
    hook: hook((node) => bar(node, data))
  })
}

function render(model, update, conn, options) {
  return h('div.widget.widget-funnel', {
    className: options.className,
    mount: mount((node) => {
      conn.on('funnel', (data) => update(search, options))
      update(search, options)
    })
  }, [
    h('h3', options.title),
    content(model, options)
  ])
}

export default render
