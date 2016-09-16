import h from 'snabbdom/h'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './error'
import container from './container'

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Pageviews'),
    (model.error) ? error.unknown() : masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, [
      widgets.browserStats(update, ws),
      widgets.pageviews(ws),
      widgets.countAll(ws),
      widgets.countOne(ws, 'item-1'),
      widgets.platformStats(ws),
      widgets.pageviewDiff(ws),
      widgets.mobileOsStats(ws)
    ])
  ])
}

export default render
