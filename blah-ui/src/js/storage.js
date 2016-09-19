function Storage(storage, ttl) {
  this.storage = storage
  this.ttl = undefined === ttl ? 1209600000 : ttl
}

Storage.prototype.set = function(key, data) {
  data.timestamp = Date.now()
  this.storage.setItem(key, JSON.stringify(data))
}

Storage.prototype.get = function(key, def) {
  var data = this.storage.getItem(key)
  if(!data) return def
  data = JSON.parse(data)
  if(data.timestamp + this.ttl < Date.now()) {
    this.storage.removeItem(key)
    return def
  }
  delete data['timestamp']
  return data
}

module.exports = Storage
