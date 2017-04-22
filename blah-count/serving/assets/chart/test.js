var test = require('tape')
var util = require('./util')
var subHours = require('date-fns/sub_hours')
var subDays = require('date-fns/sub_days')
var startOfHour = require('date-fns/start_of_hour')

test('daily', function(assert) {
  assert.plan(2)
  var data = util.timeframe([], subHours(Date.now(), 24), Date.now())
  assert.equal(data.length, 24)
  assert.deepEqual(
    data.map(function(x) {
      return x.value
    }),
    Array.apply(null, new Array(24)).map(function() {
      return 0
    })
  )
  assert.end()
})

test('weekly', function(assert) {
  assert.plan(1)
  var data = util.timeframe([], subDays(Date.now(), 7), Date.now(), {
    step: 'day'
  })

  assert.equal(data.length, 7)
  assert.end()
})

test('monthly', function(assert) {
  assert.plan(1)
  var data = util.timeframe([],
    new Date('2016-03-01'),
    new Date('2016-04-01'),
    {step: 'day'}
  )

  assert.equal(data.length, 31)
  assert.end()
})

test('yearly', function(assert) {
  assert.plan(1)
  var data = util.timeframe([],
    new Date('2016-01-01'),
    new Date('2017-01-01'),
    {step: 'month'}
  )

  assert.equal(data.length, 12)
  assert.end()
})

test('set count', function(assert) {
  assert.plan(2)
  var data = util.timeframe([
    {date: startOfHour(subHours(Date.now(), 23)).toISOString(), count: 42},
    {date: startOfHour(subHours(Date.now(), 22)), count: 43}
  ], subHours(Date.now(), 24), Date.now())

  assert.equal(data[0].value, 42)
  assert.equal(data[1].value, 43)
  assert.end()
})
