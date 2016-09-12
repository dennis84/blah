import {h} from 'virtual-dom'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './error'
import theme from './theme'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
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
