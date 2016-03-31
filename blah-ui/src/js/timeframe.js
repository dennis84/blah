import xtend from 'xtend'
import moment from 'moment'

function findCount(date, data) {
  if(!data) return 0
  for(var i in data) {
    if(moment(data[i].date).isSame(date)) return data[i].count
  }
}

/**
 * Generates Chartist labels and series by timeframe.
 *
 * ```
 * timeframe(data, fromDate, toDate, {step: 'hour', max: 2, format: 'h:mm a'})
 * ==> {labels: ['9:00 am', '10:00'], series: [[1, 2, 3]]}
 * ```
 *
 * @param {Array}  Grouped data from API layer e.g. [{date: 'ISO8601', count: Number}]
 * @param {Moment} From date
 * @param {Moment} To date
 * @param {Object} Options
 *
 * @return {Object} An object with: {labels: [], series: []}
 */
function timeframe(data, from, to, options = {}) {
  options = xtend({step: 'hour', max: 24, format: 'h:mm a'}, options)
  from = from.startOf(options.step)
  to = to.startOf(options.step)

  var labels = []
  var series = []

  for(var d = moment(from); d.diff(to) < 0; d.add(1, options.step)) {
    labels.push(d.format(options.format))
    series.push(findCount(d, data) || 0)
  }

  return {labels: labels, series: [series]}
}

/**
 * Generates D3 data
 */
function timeframeD3(data, from, to, options = {}) {
  options = xtend({step: 'hour', max: 24}, options)
  from = from.startOf(options.step)
  to = to.startOf(options.step)
  var out = []

  for(var d = moment(from); d.diff(to) < 0; d.add(1, options.step)) {
    out.push({
      date: moment(d).toDate(),
      count: findCount(d, data) || 0
    })
  }

  return out
}

export {
  timeframe,
  timeframeD3
}
