import {h} from 'virtual-dom'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import error from './common/error'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Pageviews'),
    error(model) || masonry({className: 'widgets', itemSelector: '.widget'}, [
      widgets.browserStats(model, update, conn),
      widgets.pageviews(model, update, conn),
      widgets.countAll(model, update, conn),
      widgets.platformStats(model, update, conn),
      widgets.pageviewDiff(model, update, conn),
      widgets.mobileOsStats(model, update, conn)
    ])
  ])
}

export default render
