import h from 'snabbdom/h'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './error'
import container from './container'

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Misc'),
    (model.error) ? error.unknown() : masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, [
      widgets.totalRevenue(ws),
      widgets.recommendationWidget(update),
      widgets.similarityWidget(update),
      widgets.mostViewed(ws),
      widgets.referringSites(ws)
    ])
  ])
}

export default render
