var h = require('snabbdom/h')
var progressBar = require('./progress-bar')

function noData() {
  return h('div.has-text-centered', [
    h('i.material-icons.is-large', 'error_outline'),
    h('p', 'No data')
  ])
}

function diff(value, percentage) {
  if(true === percentage) {
    value = value.toFixed(2) + '%'
  } else {
    var plusMinus = ''
    if(value < 0) plusMinus = '-'
    else if(value > 0) plusMinus = '+'
    value = plusMinus + Math.abs(value)
  }

  return h('div.widget-value', value)
}

function content(model, options) {
  return h('div', {
    class: {'has-progress-bar': options.progressBar}
  }, (undefined === model.diff || !Number.isFinite(model.diff)) ? [noData()] : [
    diff(model.diff, options.percentage),
    h('div.widget-title', options.title),
    (options.percentage && options.progressBar) ? progressBar(model.diff) : ''
  ])
}

function render(model, update, options) {
  return h('div.widget.widget-diff.is-centered-hv', {
    class: options.class
  }, [content(model, options)])
}

module.exports = render
