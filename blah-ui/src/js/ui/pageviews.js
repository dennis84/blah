import h from 'snabbdom/h'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './error'
import container from './container'

function render(model, update, conn, storage) {
  return container(model, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Pageviews'),
    (model.error) ? error.unknown() : masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, [
      widgets.browserStats(update, conn),
      widgets.pageviews(conn),
      widgets.countAll(conn),
      widgets.countOne(conn, 'item-1'),
      widgets.platformStats(conn),
      widgets.pageviewDiff(conn),
      widgets.mobileOsStats(conn)
    ])
  ])
}

export default render
