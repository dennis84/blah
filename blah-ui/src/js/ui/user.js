import h from 'snabbdom/h'
import debounce from 'debounce'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './error'
import container from './container'

function render(model, update, conn, storage) {
  return container(model, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'User Stats'),
    (model.error) ? error.unknown() : masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, [
      widgets.visitorsToday(conn),
      widgets.visitorsByCountry(conn),
      widgets.uniqueVisitors(conn)
    ])
  ])
}

export default render
