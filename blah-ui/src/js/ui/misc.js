import h from 'snabbdom/h'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './error'
import container from './container'

function render(model, update, conn, storage) {
  return container(model, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Misc'),
    (model.error) ? error.unknown() : masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, [
      widgets.totalRevenue(conn),
      widgets.recommendationWidget(update),
      widgets.similarityWidget(update),
      widgets.mostViewed(conn),
      widgets.referringSites(conn)
    ])
  ])
}

export default render
