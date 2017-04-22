var xtend = require('xtend')
var startOfHour = require('date-fns/start_of_hour')
var startOfDay = require('date-fns/start_of_day')
var startOfMonth = require('date-fns/start_of_month')
var startOfYear = require('date-fns/start_of_year')
var differenceInHours = require('date-fns/difference_in_hours')
var differenceInDays = require('date-fns/difference_in_days')
var differenceInMonths = require('date-fns/difference_in_months')
var differenceInYears = require('date-fns/difference_in_years')
var addHours = require('date-fns/add_hours')
var addDays = require('date-fns/add_days')
var addMonths = require('date-fns/add_months')
var addYears = require('date-fns/add_years')
var format = require('date-fns/format')
var isEqual = require('date-fns/is_equal')

function dateDiff(a, b, unit) {
  if('hour' === unit) return differenceInHours(a, b)
  else if('day' === unit) return differenceInDays(a, b)
  else if('month' === unit) return differenceInMonths(a, b)
  else if('years' === unit) return differenceInYears(a, b)
  else return 0
}

function startOf(date, unit) {
  if('hour' === unit) return startOfHour(date)
  else if('day' === unit) return startOfDay(date)
  else if('month' === unit) return startOfMonth(date)
  else if('year' === unit) return startOfYear(date)
  else return date
}

function add(date, amount, unit) {
  if('hour' === unit) return addHours(date, amount)
  else if('day' === unit) return addDays(date, amount)
  else if('month' === unit) return addMonths(date, amount)
  else if('years' === unit) return addYears(date, amount)
  else return date
}

function findCount(date, data, options) {
  if(!data) return 0
  for(var i in data) {
    var dataDate = startOf(data[i].date, options.step)
    if(isEqual(dataDate, date)) return data[i].count
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
 * @param {Date} Start date
 * @param {Date} End date
 * @param {Object} Options
 *
 * @return {Array}
 */
function timeframe(data, start, end, options) {
  options = xtend({step: 'hour', max: 24, format: 'ddd h:mm a'}, options)
  start = startOf(start, options.step)
  end = startOf(end, options.step)
  var out = []
  var count = dateDiff(end, start, options.step)

  for(var i = 1; i <= count; i ++) {
    var d = add(start, i, options.step)
    out.push({
      key: format(d, options.format),
      value: findCount(d, data, options) || 0
    })
  }

  return out
}

module.exports = {
  timeframe: timeframe,
  dateDiff: dateDiff
}
