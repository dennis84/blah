var gulp = require('gulp')
  , gutil = require('gulp-util')
  , browserify = require('browserify')
  , watchify = require('watchify')
  , source = require('vinyl-source-stream')
  , streamify = require('gulp-streamify')
  , stylus = require('gulp-stylus')
  , uglify = require('gulp-uglify')

var onError = function(error) {
  gutil.log(gutil.colors.red(error.message))
}

gulp.task('js', function() {
  var bundleStream = watchify(browserify('./src/js/index.js'))
    .on('update', rebundle)
    .on('log', gutil.log)

  function rebundle() {
    return bundleStream.bundle()
      .on('error', onError)
      .pipe(source('index.js'))
      .pipe(streamify(uglify()))
      .pipe(gulp.dest('./assets/js'))
  }

  return rebundle()
})

gulp.task('css', function() {
  gulp.src('src/css/index.styl')
    .pipe(stylus({ compress: true }))
    .pipe(gulp.dest('./assets/css'))
})

gulp.task('watch', function() {
  gulp.watch('src/css/**', ['css'])
})

gulp.task('default', ['watch', 'js', 'css'])
