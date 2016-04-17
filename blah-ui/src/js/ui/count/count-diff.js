import {h} from 'virtual-dom'
import {countDiff} from './ctrl'
import {mount} from '../../hook'
import progressBar from '../common/progress-bar'
import diff from '../common/diff'
import * as error from '../common/error'

function content(model, options) {
  return h('div', {
    className: options.progressBar ? 'has-progress-bar' : ''
  }, (!model.diff || !Number.isFinite(model.diff)) ? error.noData() : [
    diff(model.diff, options.percentage),
    h('div.widget-title', options.title),
    (options.percentage && options.progressBar) ? progressBar(model.diff) : null
  ])
}

function render(model, update, conn, options) {
  return h('div.widget.widget-diff.is-centered-hv', {
    className: options.className,
    mount: mount((node) => {
      conn.on('count', (data) => update(countDiff, options))
      update(countDiff, options)
    })
  }, content(model, options))
}

export default render
