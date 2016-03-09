import {h} from 'virtual-dom'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'

function render(model, chan, conn) {
  return h('div.container', [
    nav(model, chan, conn),
    h('h1.center-hv', 'Pageviews'),
    masonry({className: 'widgets', itemSelector: '.widget'}, [
      widgets.browserStats(model, chan, conn),
      widgets.pageviews(model, chan, conn),
      widgets.countAll(model, chan, conn),
      widgets.platformStats(model, chan, conn),
      widgets.pageviewDiff(model, chan, conn),
      widgets.mobileOsStats(model, chan, conn)
    ])
  ])
}

export default render
