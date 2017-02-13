var xtend = require('xtend')
var moment = require('moment')

function findCount(date, data, options) {
  if(!data) return 0
  for(var i in data) {
    var dataDate = moment(data[i].date).startOf(options.step)
    if(dataDate.isSame(date)) return data[i].count
  }
}

/**
 * Generates D3 data.
 *
 * ```
 * timeframe(data, fromDate, toDate, {step: 'hour', max: 2, format: 'ddd h:mm a'})
 * ==> [{key: '9:00 am', value: 1}, {key: '10:00', value: 2}]
 * ```
 *
 * @param {Array}  Grouped data from API layer e.g. [{date: 'ISO8601', count: Number}]
 * @param {Moment} From date
 * @param {Moment} To date
 * @param {Object} Options
 *
 * @return {Array}
 */
function timeframe(data, from, to, options) {
  options = xtend({step: 'hour', max: 24, format: 'ddd h:mm a'}, options)
  from = from.startOf(options.step)
  to = to.startOf(options.step)
  var out = []

  for(var d = moment(from); d.diff(to) <= 0; d.add(1, options.step)) {
    out.push({
      key: d.format(options.format),
      value: findCount(d, data, options) || 0
    })
  }

  return out
}

module.exports = timeframe
