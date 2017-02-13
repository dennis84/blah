var clone = require('clone')
var xhr = require('xhr')
var xtend = require('xtend')

/**
 * Sets the date filter mode.
 */
function setDateFilterMode(model, mode) {
  var m = clone(model)
  m.dateFilterMode = mode
  return m
}

/**
 * Updates the `from` property.
 */
function updateFrom(model, value) {
  var m = clone(model)
  m.from = value
  return m
}

/**
 * Updates the `to` property.
 */
function updateTo(model, value) {
  var m = clone(model)
  m.to = value
  return m
}

/**
 * Updates the `groupBy` property.
 */
function updateGroupBy(model, groups) {
  var m = clone(model)
  m.groupBy = groups
  return m
}

/**
 * Adds a new segment to the state.
 */
function addSegment(model) {
  var m = clone(model)
  var emptySegment = {filterBy: [{prop: '', operator: '', value: ''}]}
  m.segments.push(emptySegment)
  return m
}

/**
 * Removes a segment.
 */
function removeSegment(model, segment) {
  var m = clone(model)
  var index = model.segments.indexOf(segment)
  m.segments.splice(index, 1)
  return m
}

/**
 * Add a new segment filter.
 */
function addSegmentFilter(model, segment) {
  var m = clone(model)
  var index = model.segments.indexOf(segment)

  segment.filterBy.push({
    prop: '',
    operator: '',
    value: ''
  })

  m.segments[index] = segment
  return m
}

/**
 * Updates a segment filter.
 */
function updateSegmentFilter(model, segment, filterIndex, prop) {
  var m = clone(model)
  var index = model.segments.indexOf(segment)

  segment.filterBy[filterIndex] = xtend({
    prop: '',
    operator: '',
    value: ''
  }, segment.filterBy[filterIndex], prop)

  m.segments[index] = segment
  return m
}

/**
 * Removes a segment filter.
 */
function removeSegmentFilter(model, segment, filterIndex) {
  var m = clone(model)
  var index = model.segments.indexOf(segment)
  m.segments[index].filterBy.splice(filterIndex, 1)
  return m
}

/**
 * Fetch data from serving API. Makes a HTTP call for every segment.
 * TODO Implement multiple results serverside.
 */
function grouped(model) {
  if(0 === model.segments.length) return Promise.resolve(model)

  var promises = model.segments.map(function(segment) {
    return new Promise(function(resolve) {
      xhr.post(model.baseUrl + '/count', {
        json: mkQuery(model, segment)
      }, function(err, resp, body) {
        var s = clone(segment)
        s.data = body
        resolve(s)
      })
    })
  })

  return Promise.all(promises)
    .then(function(segments) {
      var m = clone(model)
      m.segments = segments
      return m
    })
}

function mkQuery(model, segment) {
  var query = {}
  if(segment.filterBy) {
    query.filterBy = clone(segment.filterBy)
  }

  if(model.groupBy && model.groupBy.length > 0) {
    query.groupBy = clone(model.groupBy)
  }

  if(model.collection) {
    query.collection = model.collection
  }

  if(model.from) {
    query.filterBy.push({
      prop: 'date.from',
      operator: 'gte',
      value: model.from
    })
  }

  if(model.to) {
    query.filterBy.push({
      prop: 'date.to',
      operator: 'lte',
      value: model.to
    })
  }

  return query
}

module.exports = {
  setDateFilterMode: setDateFilterMode,
  updateFrom: updateFrom,
  updateTo: updateTo,
  updateGroupBy: updateGroupBy,
  addSegment: addSegment,
  removeSegment: removeSegment,
  addSegmentFilter: addSegmentFilter,
  updateSegmentFilter: updateSegmentFilter,
  removeSegmentFilter: removeSegmentFilter,
  grouped: grouped
}
