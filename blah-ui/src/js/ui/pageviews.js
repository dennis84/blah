import {h} from 'virtual-dom'
import * as samples from './samples'
import masonry from './masonry'
import nav from './nav'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Pageviews'),
    masonry({className: 'widgets', itemSelector: '.widget'}, [
      samples.browserStats(model, update, conn),
      samples.pageviews(model, update, conn),
      samples.countAll(model, update, conn),
      samples.platformStats(model, update, conn),
      samples.pageviewDiff(model, update, conn),
      samples.mobileOsStats(model, update, conn)
    ])
  ])
}

export default render
