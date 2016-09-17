import h from 'snabbdom/h'
import debounce from 'debounce'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './error'
import container from './container'

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'User Stats'),
    (model.error) ? error.unknown() : masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.visitorsToday(ws),
      widgets.visitorsByCountry(ws),
      widgets.uniqueVisitors(ws)
    ])
  ])
}

export default render
