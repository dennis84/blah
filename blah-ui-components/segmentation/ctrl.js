var clone = require('clone')
var xhr = require('xhr')
var xtend = require('xtend')

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
  var emptySegment = {filterBy: []}
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
    query.filterBy = segment.filterBy
  }

  if(model.groupBy && model.groupBy.length > 0) {
    query.groupBy = model.groupBy
  }

  if(model.collection) {
    query.collection = model.collection
  }

  return query
}

module.exports = {
  updateFrom: updateFrom,
  updateTo: updateTo,
  updateGroupBy: updateGroupBy,
  addSegment: addSegment,
  removeSegment: removeSegment,
  updateSegmentFilter: updateSegmentFilter,
  removeSegmentFilter: removeSegmentFilter,
  grouped: grouped
}
