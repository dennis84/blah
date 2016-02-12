import {h} from 'virtual-dom'
import {countDiff} from './ctrl'
import {mount} from '../../hook'
import progressBar from '../common/progress-bar'
import diff from '../common/diff'

function content(model, options) {
  if(undefined === model.diff) return

  var elems = [
    diff(model.diff, options.percentage),
    h('div.title', options.title)
  ]

  if(true === options.percentage && true === options.progressBar) {
    elems.push(progressBar(model.diff))
  }

  return h('div', {
    className: options.progressBar ? 'has-progress-bar' : ''
  }, elems)
}

function render(model, update, conn, options) {
  return h('div.widget.widget-diff.center-hv', {
    className: options.className,
    mount: mount((node) => {
      conn.on('count', (data) => update(countDiff, options))
      update(countDiff, options)
    })
  }, content(model, options))
}

export default render
