import test from 'tape'
import moment from 'moment'
import timeframe from '../src/js/timeframe'

test('daily', (assert) => {
  assert.plan(2)
  var data = timeframe([], moment().subtract(24, 'hour'), moment())
  assert.equal(data.length, 24)
  assert.deepEqual(
    data.map(x => x.value),
    Array.from(new Array(24), (x, i) => 0)
  )
  assert.end()
})

test('weekly', (assert) => {
  assert.plan(1)
  var data = timeframe([], moment().subtract(7, 'day'), moment(), {
    step: 'day'
  })

  assert.equal(data.length, 7)
  assert.end()
})

test('monthly', (assert) => {
  assert.plan(1)
  var data = timeframe([],
    moment('2016-03-01'),
    moment('2016-04-01'),
    {step: 'day'}
  )

  assert.equal(data.length, 31)
  assert.end()
})

test('yearly', (assert) => {
  assert.plan(1)
  var data = timeframe([],
    moment('2016-01-01'),
    moment('2017-01-01'),
    {step: 'month'}
  )

  assert.equal(data.length, 12)
  assert.end()
})

test('set count', (assert) => {
  assert.plan(2)
  var data = timeframe([
    {date: moment().subtract(24, 'hour').startOf('hour').format(), count: 42},
    {date: moment().subtract(23, 'hour').startOf('hour').format(), count: 43}
  ], moment().subtract(24, 'hour'), moment())
  assert.equal(data[0].value, 42)
  assert.equal(data[1].value, 43)
  assert.end()
})
